// Code generated by go-swagger; DO NOT EDIT.

package operations

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the generate command

import (
	"net/http"

	middleware "github.com/go-openapi/runtime/middleware"
)

// ExperimentsCorrelationCreateHandlerFunc turns a function with the right signature into a experiments correlation create handler
type ExperimentsCorrelationCreateHandlerFunc func(ExperimentsCorrelationCreateParams) middleware.Responder

// Handle executing the request and returning a response
func (fn ExperimentsCorrelationCreateHandlerFunc) Handle(params ExperimentsCorrelationCreateParams) middleware.Responder {
	return fn(params)
}

// ExperimentsCorrelationCreateHandler interface for that can handle valid experiments correlation create params
type ExperimentsCorrelationCreateHandler interface {
	Handle(ExperimentsCorrelationCreateParams) middleware.Responder
}

// NewExperimentsCorrelationCreate creates a new http.Handler for the experiments correlation create operation
func NewExperimentsCorrelationCreate(ctx *middleware.Context, handler ExperimentsCorrelationCreateHandler) *ExperimentsCorrelationCreate {
	return &ExperimentsCorrelationCreate{Context: ctx, Handler: handler}
}

/*ExperimentsCorrelationCreate swagger:route POST /v1/experiments/correlation experimentsCorrelationCreate

Add a new experiment to a users account

*/
type ExperimentsCorrelationCreate struct {
	Context *middleware.Context
	Handler ExperimentsCorrelationCreateHandler
}

func (o *ExperimentsCorrelationCreate) ServeHTTP(rw http.ResponseWriter, r *http.Request) {
	route, rCtx, _ := o.Context.RouteInfo(r)
	if rCtx != nil {
		r = rCtx
	}
	var Params = NewExperimentsCorrelationCreateParams()

	if err := o.Context.BindValidRequest(r, route, &Params); err != nil { // bind params
		o.Context.Respond(rw, r, route.Produces, route, err)
		return
	}

	res := o.Handler.Handle(Params) // actually handle the request

	o.Context.Respond(rw, r, route.Produces, route, res)

}
