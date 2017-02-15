package controllers

import "net/http"

// Index [GET] /: The route at the server's root
func Index(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	writeSuccess(w) // JSON way of saying "Hello World", in a sense.
}
