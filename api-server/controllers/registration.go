package controllers

// File: controllers/authentication.go
// Date: 2017-02-08
// Desc: This file includes all user-related controllers.

import "net/http"

// Register [POST] /register: controls registration of new users
func Register(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	writeError(w, http.StatusNotImplemented, "Work in progress")
}

// Login [POST] /login: controls user login - it will return a JWT token upon successful login
func Login(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	writeError(w, http.StatusNotImplemented, "Work in progress")
}
