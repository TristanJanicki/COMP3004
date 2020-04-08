// Code generated by go-swagger; DO NOT EDIT.

package operations

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the swagger generate command

import (
	"net/http"

	"github.com/go-openapi/runtime"

	models "github.com/COMP3004/UserAccounts/pkg/gen/models"
)

// UpdateUserAccountOKCode is the HTTP code returned for type UpdateUserAccountOK
const UpdateUserAccountOKCode int = 200

/*UpdateUserAccountOK OK

swagger:response updateUserAccountOK
*/
type UpdateUserAccountOK struct {

	/*
	  In: Body
	*/
	Payload *models.OkResponse `json:"body,omitempty"`
}

// NewUpdateUserAccountOK creates UpdateUserAccountOK with default headers values
func NewUpdateUserAccountOK() *UpdateUserAccountOK {

	return &UpdateUserAccountOK{}
}

// WithPayload adds the payload to the update user account o k response
func (o *UpdateUserAccountOK) WithPayload(payload *models.OkResponse) *UpdateUserAccountOK {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the update user account o k response
func (o *UpdateUserAccountOK) SetPayload(payload *models.OkResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *UpdateUserAccountOK) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(200)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

// UpdateUserAccountUnauthorizedCode is the HTTP code returned for type UpdateUserAccountUnauthorized
const UpdateUserAccountUnauthorizedCode int = 401

/*UpdateUserAccountUnauthorized Update not permitted

swagger:response updateUserAccountUnauthorized
*/
type UpdateUserAccountUnauthorized struct {

	/*
	  In: Body
	*/
	Payload *models.NotAllowedResponse `json:"body,omitempty"`
}

// NewUpdateUserAccountUnauthorized creates UpdateUserAccountUnauthorized with default headers values
func NewUpdateUserAccountUnauthorized() *UpdateUserAccountUnauthorized {

	return &UpdateUserAccountUnauthorized{}
}

// WithPayload adds the payload to the update user account unauthorized response
func (o *UpdateUserAccountUnauthorized) WithPayload(payload *models.NotAllowedResponse) *UpdateUserAccountUnauthorized {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the update user account unauthorized response
func (o *UpdateUserAccountUnauthorized) SetPayload(payload *models.NotAllowedResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *UpdateUserAccountUnauthorized) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(401)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

// UpdateUserAccountNotFoundCode is the HTTP code returned for type UpdateUserAccountNotFound
const UpdateUserAccountNotFoundCode int = 404

/*UpdateUserAccountNotFound Not found

swagger:response updateUserAccountNotFound
*/
type UpdateUserAccountNotFound struct {

	/*
	  In: Body
	*/
	Payload *models.NotFoundResponse `json:"body,omitempty"`
}

// NewUpdateUserAccountNotFound creates UpdateUserAccountNotFound with default headers values
func NewUpdateUserAccountNotFound() *UpdateUserAccountNotFound {

	return &UpdateUserAccountNotFound{}
}

// WithPayload adds the payload to the update user account not found response
func (o *UpdateUserAccountNotFound) WithPayload(payload *models.NotFoundResponse) *UpdateUserAccountNotFound {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the update user account not found response
func (o *UpdateUserAccountNotFound) SetPayload(payload *models.NotFoundResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *UpdateUserAccountNotFound) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(404)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

// UpdateUserAccountInternalServerErrorCode is the HTTP code returned for type UpdateUserAccountInternalServerError
const UpdateUserAccountInternalServerErrorCode int = 500

/*UpdateUserAccountInternalServerError Internal error

swagger:response updateUserAccountInternalServerError
*/
type UpdateUserAccountInternalServerError struct {

	/*
	  In: Body
	*/
	Payload *models.ErrorResponse `json:"body,omitempty"`
}

// NewUpdateUserAccountInternalServerError creates UpdateUserAccountInternalServerError with default headers values
func NewUpdateUserAccountInternalServerError() *UpdateUserAccountInternalServerError {

	return &UpdateUserAccountInternalServerError{}
}

// WithPayload adds the payload to the update user account internal server error response
func (o *UpdateUserAccountInternalServerError) WithPayload(payload *models.ErrorResponse) *UpdateUserAccountInternalServerError {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the update user account internal server error response
func (o *UpdateUserAccountInternalServerError) SetPayload(payload *models.ErrorResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *UpdateUserAccountInternalServerError) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(500)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}
