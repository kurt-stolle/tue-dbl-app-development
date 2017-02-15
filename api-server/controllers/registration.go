package controllers

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
		return
	}

	// Register the user
	if status, err := services.CreateUser(user, user.Password); err != nil {
		writeError(w, status, err.Error())
		return
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
		return
	}

	// Attempt to login
	jwt := authentication.GetJWTInstance()
	if canLogin, uuid := jwt.Authenticate(login.Email, login.Password); canLogin {
		token, err := jwt.GenerateToken(uuid)

		if err != nil {
			writeError(w, http.StatusInternalServerError, "Internal server error")
			return
		}

		writeJSON(w, &struct {
			Token string
		}{token})
		return
	}

	// No login. Send an error
	writeError(w, http.StatusUnauthorized, "Invalid email address or password")
}
