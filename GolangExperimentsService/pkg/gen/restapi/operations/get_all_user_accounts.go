// Code generated by go-swagger; DO NOT EDIT.

package operations

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the generate command

import (
	"net/http"

	middleware "github.com/go-openapi/runtime/middleware"
)

// GetAllUserAccountsHandlerFunc turns a function with the right signature into a get all user accounts handler
type GetAllUserAccountsHandlerFunc func(GetAllUserAccountsParams) middleware.Responder

// Handle executing the request and returning a response
func (fn GetAllUserAccountsHandlerFunc) Handle(params GetAllUserAccountsParams) middleware.Responder {
	return fn(params)
}

// GetAllUserAccountsHandler interface for that can handle valid get all user accounts params
type GetAllUserAccountsHandler interface {
	Handle(GetAllUserAccountsParams) middleware.Responder
}

// NewGetAllUserAccounts creates a new http.Handler for the get all user accounts operation
func NewGetAllUserAccounts(ctx *middleware.Context, handler GetAllUserAccountsHandler) *GetAllUserAccounts {
	return &GetAllUserAccounts{Context: ctx, Handler: handler}
}

/*GetAllUserAccounts swagger:route GET /v1/users/accounts getAllUserAccounts

Returns all the users. This function is only allowed for users who have the permission to view other users profiles.

*/
type GetAllUserAccounts struct {
	Context *middleware.Context
	Handler GetAllUserAccountsHandler
}

func (o *GetAllUserAccounts) ServeHTTP(rw http.ResponseWriter, r *http.Request) {
	route, rCtx, _ := o.Context.RouteInfo(r)
	if rCtx != nil {
		r = rCtx
	}
	var Params = NewGetAllUserAccountsParams()

	if err := o.Context.BindValidRequest(r, route, &Params); err != nil { // bind params
		o.Context.Respond(rw, r, route.Produces, route, err)
		return
	}

	res := o.Handler.Handle(Params) // actually handle the request

	o.Context.Respond(rw, r, route.Produces, route, res)

}
