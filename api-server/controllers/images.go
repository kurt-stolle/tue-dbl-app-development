package controllers

import "net/http"

// Images is a controller for receiving an image sent by a user and storing it in the filesystem
func Images(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	switch r.Method {
	case http.MethodPost: // Upload an image (JPEG)
		// TODO
	case http.MethodGet: // Fetch an manifest of images
		// TODO
	default: // Return a friendly error
		writeError(w, http.StatusNotImplemented, "Method not implemented: "+r.Method)
	}
}

// Image is a controller for guessing the location of an image
func Image(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	switch r.Method {
	case http.MethodPatch: // Guess the location of an image
		// TODO
	case http.MethodGet: // Get a picture (JPEG)
		// TODO
	default: // The method isn't implemented, error!
		writeError(w, http.StatusNotImplemented, "Method not implemented: "+r.Method)
	}
}
