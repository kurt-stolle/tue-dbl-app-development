package main

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/kurt-stolle/tue-dbl-app-development/api-server/controllers"
)

const (
	testEmail    = "user@example.com"                     // @example.com may be used for these purposes
	testPassword = "TU/e-GOis9000timesbetterthanpokemon!" // For the test, we need a password that meets our requirements
)

var notFound = http.NotFoundHandler().ServeHTTP

// TestRegistration tests whether an account can be created
func TestRegistration(t *testing.T) {
	// Set some data
	user := new(struct {
		Password string
		Email    string
		Name     string
	})

	user.Password = testPassword
	user.Email = testEmail
	user.Name = "Pikachu"

	// Encode as JSON
	userJSON, err := json.Marshal(user)
	if err != nil {
		t.Error(err)
		t.Fail()
		return
	}

	// Execute a fake request
	r := httptest.NewRequest(http.MethodPost, "http://localhost:8058/register", bytes.NewReader(userJSON))
	w := httptest.NewRecorder()

	controllers.Register(w, r, notFound)

	// Handle our findings
	if w.Result().StatusCode != http.StatusOK {
		t.Fail()
		return
	}
}

// TestLogin tests whether the account created above can login
func TestLogin(t *testing.T) {
	// Set some data
	user := new(struct {
		Password string
		Email    string
	})

	user.Password = testPassword
	user.Email = testEmail

	// Encode as JSON
	userJSON, err := json.Marshal(user)
	if err != nil {
		t.Error(err)
		t.Fail()
		return
	}

	// Execute a fake request
	r := httptest.NewRequest(http.MethodPost, "http://localhost:8058/login", bytes.NewReader(userJSON))
	w := httptest.NewRecorder()

	controllers.Register(w, r, notFound)

	// Handle our findings
	if w.Result().StatusCode != http.StatusOK {
		t.Fail()
		return
	}
}
