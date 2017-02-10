package services

// File: services/user.go
// Date: 2017-02-10
// Desc: This file includes all user-related services.

import (
	"errors"
	"net/http"
	"net/mail"

	"golang.org/x/crypto/bcrypt"

	"github.com/kurt-stolle/esc/api/models"
	dbmdl "github.com/kurt-stolle/go-dbmdl"
	"github.com/pborman/uuid" // For UUID generation in the registration process
)

// CreateUser creates a new user in the database
// Returns a http status code and possibly an error
func CreateUser(u *models.User, password string) (int, error) {
	// First, we do some verification on the data provided
	if len(password) < 4 || len(password) > 64 {
		return http.StatusBadRequest, errors.New("Password must have between 6 and 64 characters")
	}

	if len(u.Email) < 5 || len(u.Email) > 100 {
		return http.StatusBadRequest, errors.New("Email must have between 5 and 100 characters")
	}

	if _, err := mail.ParseAddress(u.Email); err != nil {
		return http.StatusBadRequest, errors.New("The provided e-mail adress is not an e-mail address")
	}

	// Secondly, we generate a UUID and Salt
	u.UUID = uuid.New()
	u.Salt = randomString(32)

	// Then the password is set
	passwd, _ = bcrypt.GenerateFromPassword([]byte(password+u.Salt), 10)
	u.Password = string(passwd) // Cast array of bytes to string, no need to check for errors, as there aren't any available

	// Now use dbmdl to save this user to the database
	if err := dbmdl.Save("tuego_users", u, dbmdl.NewWhereClause("postgres")); err != nil {
		return http.StatusInternalServerError, err
	}

	// TODO: Possibly send an e-mail to the address

	return http.StatusOK, nil
}
