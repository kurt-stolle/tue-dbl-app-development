package services

import (
	"errors"
	"net/http"

	jwt "github.com/dgrijalva/jwt-go"
	"github.com/gorilla/context"
)

// GetUUID can be used to find a UUID from a request
func GetUUID(r *http.Request) (string, error) {
	if token, ok := context.Get(r, "token").(jwt.Token); ok {
		return token.Claims["sub"].(string), nil
	}

	return "", errors.New("Token not found")
}
