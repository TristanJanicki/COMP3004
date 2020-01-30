// Code generated by go-swagger; DO NOT EDIT.

package operations

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the swagger generate command

import (
	"net/http"

	"github.com/go-openapi/runtime"

	models "github.com/COMP3004/UserAccounts/pkg/gen/models"
)

// GetAllUserAccountsOKCode is the HTTP code returned for type GetAllUserAccountsOK
const GetAllUserAccountsOKCode int = 200

/*GetAllUserAccountsOK OK

swagger:response getAllUserAccountsOK
*/
type GetAllUserAccountsOK struct {

	/*
	  In: Body
	*/
	Payload *models.UserAccountResults `json:"body,omitempty"`
}

// NewGetAllUserAccountsOK creates GetAllUserAccountsOK with default headers values
func NewGetAllUserAccountsOK() *GetAllUserAccountsOK {

	return &GetAllUserAccountsOK{}
}

// WithPayload adds the payload to the get all user accounts o k response
func (o *GetAllUserAccountsOK) WithPayload(payload *models.UserAccountResults) *GetAllUserAccountsOK {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the get all user accounts o k response
func (o *GetAllUserAccountsOK) SetPayload(payload *models.UserAccountResults) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *GetAllUserAccountsOK) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(200)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

// GetAllUserAccountsUnauthorizedCode is the HTTP code returned for type GetAllUserAccountsUnauthorized
const GetAllUserAccountsUnauthorizedCode int = 401

/*GetAllUserAccountsUnauthorized Not allowed

swagger:response getAllUserAccountsUnauthorized
*/
type GetAllUserAccountsUnauthorized struct {

	/*
	  In: Body
	*/
	Payload *models.NotAllowedResponse `json:"body,omitempty"`
}

// NewGetAllUserAccountsUnauthorized creates GetAllUserAccountsUnauthorized with default headers values
func NewGetAllUserAccountsUnauthorized() *GetAllUserAccountsUnauthorized {

	return &GetAllUserAccountsUnauthorized{}
}

// WithPayload adds the payload to the get all user accounts unauthorized response
func (o *GetAllUserAccountsUnauthorized) WithPayload(payload *models.NotAllowedResponse) *GetAllUserAccountsUnauthorized {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the get all user accounts unauthorized response
func (o *GetAllUserAccountsUnauthorized) SetPayload(payload *models.NotAllowedResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *GetAllUserAccountsUnauthorized) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(401)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

// GetAllUserAccountsNotFoundCode is the HTTP code returned for type GetAllUserAccountsNotFound
const GetAllUserAccountsNotFoundCode int = 404

/*GetAllUserAccountsNotFound Not found

swagger:response getAllUserAccountsNotFound
*/
type GetAllUserAccountsNotFound struct {

	/*
	  In: Body
	*/
	Payload *models.NotFoundResponse `json:"body,omitempty"`
}

// NewGetAllUserAccountsNotFound creates GetAllUserAccountsNotFound with default headers values
func NewGetAllUserAccountsNotFound() *GetAllUserAccountsNotFound {

	return &GetAllUserAccountsNotFound{}
}

// WithPayload adds the payload to the get all user accounts not found response
func (o *GetAllUserAccountsNotFound) WithPayload(payload *models.NotFoundResponse) *GetAllUserAccountsNotFound {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the get all user accounts not found response
func (o *GetAllUserAccountsNotFound) SetPayload(payload *models.NotFoundResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *GetAllUserAccountsNotFound) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(404)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

// GetAllUserAccountsInternalServerErrorCode is the HTTP code returned for type GetAllUserAccountsInternalServerError
const GetAllUserAccountsInternalServerErrorCode int = 500

/*GetAllUserAccountsInternalServerError Internal error

swagger:response getAllUserAccountsInternalServerError
*/
type GetAllUserAccountsInternalServerError struct {

	/*
	  In: Body
	*/
	Payload *models.ErrorResponse `json:"body,omitempty"`
}

// NewGetAllUserAccountsInternalServerError creates GetAllUserAccountsInternalServerError with default headers values
func NewGetAllUserAccountsInternalServerError() *GetAllUserAccountsInternalServerError {

	return &GetAllUserAccountsInternalServerError{}
}

// WithPayload adds the payload to the get all user accounts internal server error response
func (o *GetAllUserAccountsInternalServerError) WithPayload(payload *models.ErrorResponse) *GetAllUserAccountsInternalServerError {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the get all user accounts internal server error response
func (o *GetAllUserAccountsInternalServerError) SetPayload(payload *models.ErrorResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *GetAllUserAccountsInternalServerError) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(500)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}
