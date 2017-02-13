package authentication

import (
	"bytes"
	"fmt"
	"net/http"
	"strconv"

	jwt "github.com/dgrijalva/jwt-go"
	"github.com/gorilla/context"
)

// Check checks whether a request is authenticated
func Check(r *http.Request) bool {
	authBackend := GetJWTInstance()

	// Important security measure: if ALG is not valid or set to 'none', then authentication must fail!
	token, err := jwt.ParseFromRequest(r, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {
			return nil, fmt.Errorf("Unexpected signing method: %v", token.Header["alg"])
		}

		return authBackend.PublicKey, nil
	})

	if err == nil && token.Valid {
		context.Set(r, "token", token) // Store the token in the session so that we can fetch the UUID or other information from it later
		return true
	}

	return false

}

// Verify is a middleware to force people to be authenticated before viewing a page
func Verify(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	if Check(r) {
		next(w, r)
	} else {
		w.WriteHeader(http.StatusUnauthorized)
		w.Header().Set("Content-Type", "application/json")

		// Concatenate
		var buffer bytes.Buffer

		buffer.WriteString("{\"code\":")
		buffer.WriteString(strconv.Itoa(http.StatusUnauthorized))
		buffer.WriteString(",\"error\":\"Authentication failed\"}")

		// Send reply
		w.Write(buffer.Bytes())
	}
}
