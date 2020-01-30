// Code generated by go-swagger; DO NOT EDIT.

package models

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the swagger generate command

import (
	strfmt "github.com/go-openapi/strfmt"

	"github.com/go-openapi/errors"
	"github.com/go-openapi/swag"
	"github.com/go-openapi/validate"
)

// ConfirmRecoverPasswordInput confirm recover password input
// swagger:model confirmRecoverPasswordInput
type ConfirmRecoverPasswordInput struct {

	// confirmation code
	// Required: true
	ConfirmationCode *string `json:"confirmationCode"`

	// email
	// Required: true
	// Format: email
	Email *strfmt.Email `json:"email"`

	// new password
	// Required: true
	NewPassword *string `json:"newPassword"`
}

// Validate validates this confirm recover password input
func (m *ConfirmRecoverPasswordInput) Validate(formats strfmt.Registry) error {
	var res []error

	if err := m.validateConfirmationCode(formats); err != nil {
		res = append(res, err)
	}

	if err := m.validateEmail(formats); err != nil {
		res = append(res, err)
	}

	if err := m.validateNewPassword(formats); err != nil {
		res = append(res, err)
	}

	if len(res) > 0 {
		return errors.CompositeValidationError(res...)
	}
	return nil
}

func (m *ConfirmRecoverPasswordInput) validateConfirmationCode(formats strfmt.Registry) error {

	if err := validate.Required("confirmationCode", "body", m.ConfirmationCode); err != nil {
		return err
	}

	return nil
}

func (m *ConfirmRecoverPasswordInput) validateEmail(formats strfmt.Registry) error {

	if err := validate.Required("email", "body", m.Email); err != nil {
		return err
	}

	if err := validate.FormatOf("email", "body", "email", m.Email.String(), formats); err != nil {
		return err
	}

	return nil
}

func (m *ConfirmRecoverPasswordInput) validateNewPassword(formats strfmt.Registry) error {

	if err := validate.Required("newPassword", "body", m.NewPassword); err != nil {
		return err
	}

	return nil
}

// MarshalBinary interface implementation
func (m *ConfirmRecoverPasswordInput) MarshalBinary() ([]byte, error) {
	if m == nil {
		return nil, nil
	}
	return swag.WriteJSON(m)
}

// UnmarshalBinary interface implementation
func (m *ConfirmRecoverPasswordInput) UnmarshalBinary(b []byte) error {
	var res ConfirmRecoverPasswordInput
	if err := swag.ReadJSON(b, &res); err != nil {
		return err
	}
	*m = res
	return nil
}
