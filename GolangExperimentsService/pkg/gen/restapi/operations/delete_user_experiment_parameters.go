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

// NewDeleteUserExperimentParams creates a new DeleteUserExperimentParams object
// no default values defined in spec.
func NewDeleteUserExperimentParams() DeleteUserExperimentParams {

	return DeleteUserExperimentParams{}
}

// DeleteUserExperimentParams contains all the bound params for the delete user experiment operation
// typically these are obtained from a http.Request
//
// swagger:parameters DeleteUserExperiment
type DeleteUserExperimentParams struct {

	// HTTP Request Object
	HTTPRequest *http.Request `json:"-"`

	/*The database ID of the experiment to unsubscribe the user from
	  Required: true
	  In: header
	*/
	ExperimentID string
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
// To ensure default values, the struct must have been initialized with NewDeleteUserExperimentParams() beforehand.
func (o *DeleteUserExperimentParams) BindRequest(r *http.Request, route *middleware.MatchedRoute) error {
	var res []error

	o.HTTPRequest = r

	if err := o.bindExperimentID(r.Header[http.CanonicalHeaderKey("experiment-id")], true, route.Formats); err != nil {
		res = append(res, err)
	}

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

// bindExperimentID binds and validates parameter ExperimentID from header.
func (o *DeleteUserExperimentParams) bindExperimentID(rawData []string, hasKey bool, formats strfmt.Registry) error {
	if !hasKey {
		return errors.Required("experiment-id", "header")
	}
	var raw string
	if len(rawData) > 0 {
		raw = rawData[len(rawData)-1]
	}

	// Required: true

	if err := validate.RequiredString("experiment-id", "header", raw); err != nil {
		return err
	}

	o.ExperimentID = raw

	return nil
}

// bindIDToken binds and validates parameter IDToken from header.
func (o *DeleteUserExperimentParams) bindIDToken(rawData []string, hasKey bool, formats strfmt.Registry) error {
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
func (o *DeleteUserExperimentParams) bindUserID(rawData []string, hasKey bool, formats strfmt.Registry) error {
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
