package controllers

// File: controllers/index.go
// Date: 2017-02-10
// Desc: The index route - just for testing whether we're online

import "net/http"

// Index [GET] /: The route at the server's root
func Index(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	writeSuccess(w) // JSON way of saying "Hello World", in a sense.
}
