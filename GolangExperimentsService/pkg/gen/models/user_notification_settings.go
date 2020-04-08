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

// UserNotificationSettings user notification settings
// swagger:model userNotificationSettings
type UserNotificationSettings struct {

	// notify emails bounce rate warning
	// Required: true
	NotifyEmailsBounceRateWarning *bool `json:"notifyEmailsBounceRateWarning"`

	// notify emails send limit warning
	// Required: true
	NotifyEmailsSendLimitWarning *bool `json:"notifyEmailsSendLimitWarning"`

	// send daily digest
	// Required: true
	SendDailyDigest *bool `json:"sendDailyDigest"`
}

// Validate validates this user notification settings
func (m *UserNotificationSettings) Validate(formats strfmt.Registry) error {
	var res []error

	if err := m.validateNotifyEmailsBounceRateWarning(formats); err != nil {
		res = append(res, err)
	}

	if err := m.validateNotifyEmailsSendLimitWarning(formats); err != nil {
		res = append(res, err)
	}

	if err := m.validateSendDailyDigest(formats); err != nil {
		res = append(res, err)
	}

	if len(res) > 0 {
		return errors.CompositeValidationError(res...)
	}
	return nil
}

func (m *UserNotificationSettings) validateNotifyEmailsBounceRateWarning(formats strfmt.Registry) error {

	if err := validate.Required("notifyEmailsBounceRateWarning", "body", m.NotifyEmailsBounceRateWarning); err != nil {
		return err
	}

	return nil
}

func (m *UserNotificationSettings) validateNotifyEmailsSendLimitWarning(formats strfmt.Registry) error {

	if err := validate.Required("notifyEmailsSendLimitWarning", "body", m.NotifyEmailsSendLimitWarning); err != nil {
		return err
	}

	return nil
}

func (m *UserNotificationSettings) validateSendDailyDigest(formats strfmt.Registry) error {

	if err := validate.Required("sendDailyDigest", "body", m.SendDailyDigest); err != nil {
		return err
	}

	return nil
}

// MarshalBinary interface implementation
func (m *UserNotificationSettings) MarshalBinary() ([]byte, error) {
	if m == nil {
		return nil, nil
	}
	return swag.WriteJSON(m)
}

// UnmarshalBinary interface implementation
func (m *UserNotificationSettings) UnmarshalBinary(b []byte) error {
	var res UserNotificationSettings
	if err := swag.ReadJSON(b, &res); err != nil {
		return err
	}
	*m = res
	return nil
}
