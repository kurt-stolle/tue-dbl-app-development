package controllers

import (
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/services"
)

// User can be used to fetch information about a user
// Returns JSON-encoded models.User
func User(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	u := services.GetUser(mux.Vars(r)["uuid"])

	if u == nil {
		writeError(w, http.StatusNotFound)
		return
	}

	switch r.Method {
	case http.MethodPost: // Update user's information
		// TODO
		writeError(w, http.StatusNotImplemented, "Method not implemented: "+r.Method)
	case http.MethodGet: // Get user's information
		// Parse the user and JSON and write out
		writeJSON(w, u)
	default: // The method isn't implemented, error!
		writeError(w, http.StatusNotImplemented, "Method not implemented: "+r.Method)
	}
}

// Leaderboard returns usernames ordered by points (desc)
func Leaderboard(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	var a = 10 // Amount of usernames we want to fetch

	// There may be an amount specified in the URL using a urlencoded query
	if qStr := r.URL.Query().Get("amount"); qStr != "" {
		if qInt, err := strconv.Atoi(qStr); err == nil {
			a = qInt
		}
	}

	// Write the leaderboards
	writeJSON(w, services.GetLeaderboard(a))
}
