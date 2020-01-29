// run with ./awslogin -p mgmt

package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"os"
	"os/user"
	"path"
	"strconv"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/aws/external"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/sts"
	"github.com/go-ini/ini"
	"github.com/nightlyone/lockfile"
)

const (
	// This is the max allowed by Amazon
	expiration = int64(36 * time.Hour / time.Second)

	mfaAccessProfileSuffix = "-mfa-access"

	accessKeyIDIniKey     = "aws_access_key_id"
	secretAccessKetIniKey = "aws_secret_access_key"
	sessionTokenIniKey    = "aws_session_token"
)

var (
	profile         = flag.String("p", "", "The AWS profile to login to (e.g. dev|mgmt|prod|production)")
	tokenCode       = flag.String("c", "", "The 6-digit MFA token code")
	credentialsFile = flag.String("f", "~/.aws/credentials", "The path to the AWS credentials file")
)

func check(err error) {
	if err != nil {
		log.Fatal(err)
	}
}

func main() {
	flag.Parse()

	// Argument validation
	if *profile == "" {
		fmt.Println("AWS account must be set")
		os.Exit(1)
	}
	if *credentialsFile == "" {
		fmt.Println("Credentials file must be specified")
		os.Exit(1)
	}
	if strings.HasPrefix(*credentialsFile, "~") {
		usr, err := user.Current()
		check(err)
		*credentialsFile = path.Join(usr.HomeDir, (*credentialsFile)[2:])
	}
	if _, err := os.Stat(*credentialsFile); os.IsNotExist(err) {
		fmt.Println(fmt.Sprintf("Credentials file \"%s\" doesn't exist", *credentialsFile))
		os.Exit(1)
	}

	// Use a lock file out of paranoia
	lock, err := lockfile.New(*credentialsFile + ".lock")
	check(err)
	check(lock.TryLock())
	defer lock.Unlock()

	profiles, err := ini.Load(*credentialsFile)
	check(err)

	// Verify a MFA access profile has been set, which allows us to query a user's set of MFA devices for convenience
	mfaAccessProfile := *profile + mfaAccessProfileSuffix
	if _, err = profiles.GetSection(mfaAccessProfile); err != nil {
		fmt.Println(fmt.Sprintf("AWS profile \"%s\" does not exist to access MFA devices", mfaAccessProfile))
		os.Exit(1)
	}

	if *tokenCode == "" {
		fmt.Print("Enter MFA Code: ")
		_, err := fmt.Scanln(tokenCode)
		if err != nil {
			fmt.Println("Failed to read code:", err)
			os.Exit(1)
		}
	}

	if _, err := strconv.Atoi(*tokenCode); err != nil || len(*tokenCode) != 6 {
		fmt.Println("Invalid MFA code (must be a 6-digit number)")
		os.Exit(1)
	}

	cfg, err := external.LoadDefaultAWSConfig(external.WithSharedConfigProfile(mfaAccessProfile))
	check(err)

	// Get the MFA device. Note we assume there's always one per account
	iamClient := iam.New(cfg)
	mfaDevices, err := iamClient.ListMFADevicesRequest(&iam.ListMFADevicesInput{}).Send(context.Background())
	check(err)
	if len(mfaDevices.MFADevices) == 0 {
		fmt.Println("MFA device has not been setup")
		os.Exit(1)
	}

	// Authenticate the MFA code with AWS
	stsClient := sts.New(cfg)
	mfaSession, err := stsClient.GetSessionTokenRequest(&sts.GetSessionTokenInput{
		SerialNumber:    mfaDevices.MFADevices[0].SerialNumber,
		TokenCode:       tokenCode,
		DurationSeconds: aws.Int64(expiration),
	}).Send(context.Background())
	check(err)

	// Copy the current credentials file out of paranoia
	bkp, err := os.OpenFile(*credentialsFile+".bkp", os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0644)
	check(err)
	defer bkp.Close()
	_, err = profiles.WriteTo(bkp)
	check(err)

	// Update the credentials file
	section, err := profiles.GetSection(*profile)
	if err != nil {
		section, err = profiles.NewSection(*profile)
		check(err)
	}
	section.NewKey(accessKeyIDIniKey, *mfaSession.Credentials.AccessKeyId)
	section.NewKey(secretAccessKetIniKey, *mfaSession.Credentials.SecretAccessKey)
	section.NewKey(sessionTokenIniKey, *mfaSession.Credentials.SessionToken)
	creds, err := os.OpenFile(*credentialsFile, os.O_WRONLY|os.O_TRUNC, 0644)
	check(err)
	defer creds.Close()
	_, err = profiles.WriteTo(creds)
	check(err)
}
