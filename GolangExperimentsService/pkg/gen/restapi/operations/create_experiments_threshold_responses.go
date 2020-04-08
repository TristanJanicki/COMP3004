// Code generated by go-swagger; DO NOT EDIT.

package operations

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the swagger generate command

import (
	"net/http"

	"github.com/go-openapi/runtime"

	models "github.com/COMP3004/GolangExperimentsService/pkg/gen/models"
)

// CreateExperimentsThresholdOKCode is the HTTP code returned for type CreateExperimentsThresholdOK
const CreateExperimentsThresholdOKCode int = 200

/*CreateExperimentsThresholdOK OK

swagger:response createExperimentsThresholdOK
*/
type CreateExperimentsThresholdOK struct {

	/*
	  In: Body
	*/
	Payload *models.OkResponse `json:"body,omitempty"`
}

// NewCreateExperimentsThresholdOK creates CreateExperimentsThresholdOK with default headers values
func NewCreateExperimentsThresholdOK() *CreateExperimentsThresholdOK {

	return &CreateExperimentsThresholdOK{}
}

// WithPayload adds the payload to the create experiments threshold o k response
func (o *CreateExperimentsThresholdOK) WithPayload(payload *models.OkResponse) *CreateExperimentsThresholdOK {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the create experiments threshold o k response
func (o *CreateExperimentsThresholdOK) SetPayload(payload *models.OkResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *CreateExperimentsThresholdOK) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(200)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

// CreateExperimentsThresholdUnauthorizedCode is the HTTP code returned for type CreateExperimentsThresholdUnauthorized
const CreateExperimentsThresholdUnauthorizedCode int = 401

/*CreateExperimentsThresholdUnauthorized insufficient permissions/not allowed

swagger:response createExperimentsThresholdUnauthorized
*/
type CreateExperimentsThresholdUnauthorized struct {

	/*
	  In: Body
	*/
	Payload *models.NotAllowedResponse `json:"body,omitempty"`
}

// NewCreateExperimentsThresholdUnauthorized creates CreateExperimentsThresholdUnauthorized with default headers values
func NewCreateExperimentsThresholdUnauthorized() *CreateExperimentsThresholdUnauthorized {

	return &CreateExperimentsThresholdUnauthorized{}
}

// WithPayload adds the payload to the create experiments threshold unauthorized response
func (o *CreateExperimentsThresholdUnauthorized) WithPayload(payload *models.NotAllowedResponse) *CreateExperimentsThresholdUnauthorized {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the create experiments threshold unauthorized response
func (o *CreateExperimentsThresholdUnauthorized) SetPayload(payload *models.NotAllowedResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *CreateExperimentsThresholdUnauthorized) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(401)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

// CreateExperimentsThresholdConflictCode is the HTTP code returned for type CreateExperimentsThresholdConflict
const CreateExperimentsThresholdConflictCode int = 409

/*CreateExperimentsThresholdConflict Already exists

swagger:response createExperimentsThresholdConflict
*/
type CreateExperimentsThresholdConflict struct {

	/*
	  In: Body
	*/
	Payload *models.AlreadyExistsResponse `json:"body,omitempty"`
}

// NewCreateExperimentsThresholdConflict creates CreateExperimentsThresholdConflict with default headers values
func NewCreateExperimentsThresholdConflict() *CreateExperimentsThresholdConflict {

	return &CreateExperimentsThresholdConflict{}
}

// WithPayload adds the payload to the create experiments threshold conflict response
func (o *CreateExperimentsThresholdConflict) WithPayload(payload *models.AlreadyExistsResponse) *CreateExperimentsThresholdConflict {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the create experiments threshold conflict response
func (o *CreateExperimentsThresholdConflict) SetPayload(payload *models.AlreadyExistsResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *CreateExperimentsThresholdConflict) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(409)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}

// CreateExperimentsThresholdInternalServerErrorCode is the HTTP code returned for type CreateExperimentsThresholdInternalServerError
const CreateExperimentsThresholdInternalServerErrorCode int = 500

/*CreateExperimentsThresholdInternalServerError Internal error

swagger:response createExperimentsThresholdInternalServerError
*/
type CreateExperimentsThresholdInternalServerError struct {

	/*
	  In: Body
	*/
	Payload *models.ErrorResponse `json:"body,omitempty"`
}

// NewCreateExperimentsThresholdInternalServerError creates CreateExperimentsThresholdInternalServerError with default headers values
func NewCreateExperimentsThresholdInternalServerError() *CreateExperimentsThresholdInternalServerError {

	return &CreateExperimentsThresholdInternalServerError{}
}

// WithPayload adds the payload to the create experiments threshold internal server error response
func (o *CreateExperimentsThresholdInternalServerError) WithPayload(payload *models.ErrorResponse) *CreateExperimentsThresholdInternalServerError {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the create experiments threshold internal server error response
func (o *CreateExperimentsThresholdInternalServerError) SetPayload(payload *models.ErrorResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *CreateExperimentsThresholdInternalServerError) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(500)
	if o.Payload != nil {
		payload := o.Payload
		if err := producer.Produce(rw, payload); err != nil {
			panic(err) // let the recovery middleware deal with this
		}
	}
}
