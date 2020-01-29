// This file is safe to edit. Once it exists it will not be overwritten

package restapi

import (
	"crypto/tls"
	"net/http"

	errors "github.com/go-openapi/errors"
	runtime "github.com/go-openapi/runtime"
	middleware "github.com/go-openapi/runtime/middleware"
	"github.com/rs/cors"

	"github.com/COMP3004/UserAccounts/pkg/gen/restapi/operations"
)

//go:generate swagger generate server --target ..\..\gen --name SwaggerRest --spec ..\..\..\swagger.yaml

func configureFlags(api *operations.SwaggerRestAPI) {
	// api.CommandLineOptionsGroups = []swag.CommandLineOptionsGroup{ ... }
}

func configureAPI(api *operations.SwaggerRestAPI) http.Handler {
	// configure the api here
	api.ServeError = errors.ServeError

	// Set your custom logger if needed. Default one is log.Printf
	// Expected interface func(string, ...interface{})
	//
	// Example:
	// api.Logger = log.Printf

	api.JSONConsumer = runtime.JSONConsumer()

	api.JSONProducer = runtime.JSONProducer()

	if api.CompleteAuthChallengeHandler == nil {
		api.CompleteAuthChallengeHandler = operations.CompleteAuthChallengeHandlerFunc(func(params operations.CompleteAuthChallengeParams) middleware.Responder {
			return middleware.NotImplemented("operation .CompleteAuthChallenge has not yet been implemented")
		})
	}
	if api.CompletePasswordRecoveryHandler == nil {
		api.CompletePasswordRecoveryHandler = operations.CompletePasswordRecoveryHandlerFunc(func(params operations.CompletePasswordRecoveryParams) middleware.Responder {
			return middleware.NotImplemented("operation .CompletePasswordRecovery has not yet been implemented")
		})
	}
	if api.CreateUserAccountHandler == nil {
		api.CreateUserAccountHandler = operations.CreateUserAccountHandlerFunc(func(params operations.CreateUserAccountParams) middleware.Responder {
			return middleware.NotImplemented("operation .CreateUserAccount has not yet been implemented")
		})
	}
	if api.DeleteUserAccountHandler == nil {
		api.DeleteUserAccountHandler = operations.DeleteUserAccountHandlerFunc(func(params operations.DeleteUserAccountParams) middleware.Responder {
			return middleware.NotImplemented("operation .DeleteUserAccount has not yet been implemented")
		})
	}
	if api.GetAllUserAccountsHandler == nil {
		api.GetAllUserAccountsHandler = operations.GetAllUserAccountsHandlerFunc(func(params operations.GetAllUserAccountsParams) middleware.Responder {
			return middleware.NotImplemented("operation .GetAllUserAccounts has not yet been implemented")
		})
	}
	if api.GetCustomerAccountHandler == nil {
		api.GetCustomerAccountHandler = operations.GetCustomerAccountHandlerFunc(func(params operations.GetCustomerAccountParams) middleware.Responder {
			return middleware.NotImplemented("operation .GetCustomerAccount has not yet been implemented")
		})
	}
	if api.GetUserAccountHandler == nil {
		api.GetUserAccountHandler = operations.GetUserAccountHandlerFunc(func(params operations.GetUserAccountParams) middleware.Responder {
			return middleware.NotImplemented("operation .GetUserAccount has not yet been implemented")
		})
	}
	if api.RequestPasswordRecoveryHandler == nil {
		api.RequestPasswordRecoveryHandler = operations.RequestPasswordRecoveryHandlerFunc(func(params operations.RequestPasswordRecoveryParams) middleware.Responder {
			return middleware.NotImplemented("operation .RequestPasswordRecovery has not yet been implemented")
		})
	}
	if api.SignInHandler == nil {
		api.SignInHandler = operations.SignInHandlerFunc(func(params operations.SignInParams) middleware.Responder {
			return middleware.NotImplemented("operation .SignIn has not yet been implemented")
		})
	}
	if api.SignOutHandler == nil {
		api.SignOutHandler = operations.SignOutHandlerFunc(func(params operations.SignOutParams) middleware.Responder {
			return middleware.NotImplemented("operation .SignOut has not yet been implemented")
		})
	}
	if api.SignUpHandler == nil {
		api.SignUpHandler = operations.SignUpHandlerFunc(func(params operations.SignUpParams) middleware.Responder {
			return middleware.NotImplemented("operation .SignUp has not yet been implemented")
		})
	}
	if api.UpdateCustomerAccountHandler == nil {
		api.UpdateCustomerAccountHandler = operations.UpdateCustomerAccountHandlerFunc(func(params operations.UpdateCustomerAccountParams) middleware.Responder {
			return middleware.NotImplemented("operation .UpdateCustomerAccount has not yet been implemented")
		})
	}
	if api.UpdateUserAccountHandler == nil {
		api.UpdateUserAccountHandler = operations.UpdateUserAccountHandlerFunc(func(params operations.UpdateUserAccountParams) middleware.Responder {
			return middleware.NotImplemented("operation .UpdateUserAccount has not yet been implemented")
		})
	}
	if api.VerifyJwtHandler == nil {
		api.VerifyJwtHandler = operations.VerifyJwtHandlerFunc(func(params operations.VerifyJwtParams) middleware.Responder {
			return middleware.NotImplemented("operation .VerifyJwt has not yet been implemented")
		})
	}
	if api.ChangePasswordHandler == nil {
		api.ChangePasswordHandler = operations.ChangePasswordHandlerFunc(func(params operations.ChangePasswordParams) middleware.Responder {
			return middleware.NotImplemented("operation .ChangePassword has not yet been implemented")
		})
	}
	if api.RefreshTokensHandler == nil {
		api.RefreshTokensHandler = operations.RefreshTokensHandlerFunc(func(params operations.RefreshTokensParams) middleware.Responder {
			return middleware.NotImplemented("operation .RefreshTokens has not yet been implemented")
		})
	}

	api.ServerShutdown = func() {}

	return setupGlobalMiddleware(api.Serve(setupMiddlewares))
}

// The TLS configuration before HTTPS server starts.
func configureTLS(tlsConfig *tls.Config) {
	// Make all necessary changes to the TLS configuration here.
}

// As soon as server is initialized but not run yet, this function will be called.
// If you need to modify a config, store server instance to stop it individually later, this is the place.
// This function can be called multiple times, depending on the number of serving schemes.
// scheme value will be set accordingly: "http", "https" or "unix"
func configureServer(s *http.Server, scheme, addr string) {
}

// The middleware configuration is for the handler executors. These do not apply to the swagger.json document.
// The middleware executes after routing but before authentication, binding and validation
func setupMiddlewares(handler http.Handler) http.Handler {
	return handler
}

// The middleware configuration happens before anything, this middleware also applies to serving the swagger.json document.
// So this is a good place to plug in a panic handling middleware, logging and metrics
func setupGlobalMiddleware(handler http.Handler) http.Handler {
	corsHandler := cors.New(cors.Options{
		Debug:          false,
		AllowedHeaders: []string{"*"},
		AllowedOrigins: []string{"*"},
		AllowedMethods: []string{"*"},
		MaxAge:         1000,
	})
	return corsHandler.Handler(handler)
}
