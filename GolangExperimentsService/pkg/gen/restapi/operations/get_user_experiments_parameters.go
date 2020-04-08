// Code generated by go-swagger; DO NOT EDIT.

package operations

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the swagger generate command

import (
	"net/http"

	"github.com/go-openapi/errors"
	"github.com/go-openapi/runtime/middleware"
	"github.com/go-openapi/validate"

	strfmt "github.com/go-openapi/strfmt"
)

// NewGetUserExperimentsParams creates a new GetUserExperimentsParams object
// no default values defined in spec.
func NewGetUserExperimentsParams() GetUserExperimentsParams {

	return GetUserExperimentsParams{}
}

// GetUserExperimentsParams contains all the bound params for the get user experiments operation
// typically these are obtained from a http.Request
//
// swagger:parameters GetUserExperiments
type GetUserExperimentsParams struct {

	// HTTP Request Object
	HTTPRequest *http.Request `json:"-"`

	/*access token obtained from AWS Cognito
	  Required: true
	  In: header
	*/
	IDToken string
	/*the users ID to associate the experiment with
	  Required: true
	  In: header
	*/
	UserID string
}

// BindRequest both binds and validates a request, it assumes that complex things implement a Validatable(strfmt.Registry) error interface
// for simple values it will use straight method calls.
//
// To ensure default values, the struct must have been initialized with NewGetUserExperimentsParams() beforehand.
func (o *GetUserExperimentsParams) BindRequest(r *http.Request, route *middleware.MatchedRoute) error {
	var res []error

	o.HTTPRequest = r

	if err := o.bindIDToken(r.Header[http.CanonicalHeaderKey("idToken")], true, route.Formats); err != nil {
		res = append(res, err)
	}

	if err := o.bindUserID(r.Header[http.CanonicalHeaderKey("user-id")], true, route.Formats); err != nil {
		res = append(res, err)
	}

	if len(res) > 0 {
		return errors.CompositeValidationError(res...)
	}
	return nil
}

// bindIDToken binds and validates parameter IDToken from header.
func (o *GetUserExperimentsParams) bindIDToken(rawData []string, hasKey bool, formats strfmt.Registry) error {
	if !hasKey {
		return errors.Required("idToken", "header")
	}
	var raw string
	if len(rawData) > 0 {
		raw = rawData[len(rawData)-1]
	}

	// Required: true

	if err := validate.RequiredString("idToken", "header", raw); err != nil {
		return err
	}

	o.IDToken = raw

	return nil
}

// bindUserID binds and validates parameter UserID from header.
func (o *GetUserExperimentsParams) bindUserID(rawData []string, hasKey bool, formats strfmt.Registry) error {
	if !hasKey {
		return errors.Required("user-id", "header")
	}
	var raw string
	if len(rawData) > 0 {
		raw = rawData[len(rawData)-1]
	}

	// Required: true

	if err := validate.RequiredString("user-id", "header", raw); err != nil {
		return err
	}

	o.UserID = raw

	return nil
}
