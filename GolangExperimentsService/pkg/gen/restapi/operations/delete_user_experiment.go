// Code generated by go-swagger; DO NOT EDIT.

package operations

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the generate command

import (
	"net/http"

	middleware "github.com/go-openapi/runtime/middleware"
)

// DeleteUserExperimentHandlerFunc turns a function with the right signature into a delete user experiment handler
type DeleteUserExperimentHandlerFunc func(DeleteUserExperimentParams) middleware.Responder

// Handle executing the request and returning a response
func (fn DeleteUserExperimentHandlerFunc) Handle(params DeleteUserExperimentParams) middleware.Responder {
	return fn(params)
}

// DeleteUserExperimentHandler interface for that can handle valid delete user experiment params
type DeleteUserExperimentHandler interface {
	Handle(DeleteUserExperimentParams) middleware.Responder
}

// NewDeleteUserExperiment creates a new http.Handler for the delete user experiment operation
func NewDeleteUserExperiment(ctx *middleware.Context, handler DeleteUserExperimentHandler) *DeleteUserExperiment {
	return &DeleteUserExperiment{Context: ctx, Handler: handler}
}

/*DeleteUserExperiment swagger:route DELETE /v1/users/experiments deleteUserExperiment

Delete a experiment from a users experiments list

*/
type DeleteUserExperiment struct {
	Context *middleware.Context
	Handler DeleteUserExperimentHandler
}

func (o *DeleteUserExperiment) ServeHTTP(rw http.ResponseWriter, r *http.Request) {
	route, rCtx, _ := o.Context.RouteInfo(r)
	if rCtx != nil {
		r = rCtx
	}
	var Params = NewDeleteUserExperimentParams()

	if err := o.Context.BindValidRequest(r, route, &Params); err != nil { // bind params
		o.Context.Respond(rw, r, route.Produces, route, err)
		return
	}

	res := o.Handler.Handle(Params) // actually handle the request

	o.Context.Respond(rw, r, route.Produces, route, res)

}
