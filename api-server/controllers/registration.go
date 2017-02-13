package controllers

// File: controllers/authentication.go
// Date: 2017-02-08
// Desc: This file includes all user-related controllers.

import (
	"encoding/json"
	"net/http"

	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/authentication"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/models"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/services"
)

// Register [POST] /register: controls registration of new users
func Register(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	// This creates a new instance of user, later to be filled out by parsing the request's JSON
	var user = new(models.User)

	// Decode the JSON that was sent to us
	if err := json.NewDecoder(r.Body).Decode(&user); err != nil {
		writeError(w, http.StatusBadRequest, "Not a JSON request")
	}

	// Fetch the wanted password from the request, and set the Password and Salt fields to the null value (because this stuff is supposed to be a hash in the database, the plaintext password shouldn't be a property of user)
	var passwd = user.Password
	user.Password = ""
	user.Salt = ""

	// Register the user
	if status, err := services.CreateUser(user, passwd); err != nil {
		writeError(w, status, err.Error())
	}

	writeSuccess(w)
}

// Login [POST] /login: controls user login - it will return a JWT token upon successful login
func Login(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	// Struct for login request
	var login struct {
		Email    string
		Password string
	}

	// Decode the JSON that was sent to us
	if err := json.NewDecoder(r.Body).Decode(&login); err != nil {
		writeError(w, http.StatusBadRequest, "Not a JSON request")
	}

	// Attempt to login
	if canLogin, uuid := authentication.Authenticate(login.Email, login.Password); canLogin {
		token := authentication.GenerateToken(uuid)

		return writeJSON(w, struct {
			Token string
		}{token})
	}

	// No login. Send an error
	writeError(w, http.StatusUnauthorized, "Invalid email address or password")
}
