package controllers

import (
	"encoding/json"
	"log"
	"net/http"
)

// writeJSON is a helper function for writing a struct as a JSON response
func writeJSON(w http.ResponseWriter, d interface{}) {
	// Marshal JSON
	j, err := json.Marshal(d)
	if err != nil {
		log.Panic(err)
		return
	}

	// Setup header
	w.Header().Set("Content-Type", "application/json")

	// Send reply
	w.Write(j)
}

// writeSuccess is a helper function for letting the user know their request was handled successfully
func writeSuccess(w http.ResponseWriter) {
	// Setup header
	w.Header().Set("Content-Type", "application/json")

	// Send reply - empty array
	w.Write([]byte("[]"))
}

// writeError is a helper function that assures all errors are delivered in the same way
// pretty error handling makes development easier on the long-run
type jsonError struct {
	Code  int
	Error string
}

func writeError(w http.ResponseWriter, code int, msgSlice ...string) {
	var msg string
	if len(msgSlice) >= 1 { // a message was passed
		msg = msgSlice[0]
	} else { // No message was passed, so let's generate one
		msg = http.StatusText(code)
	}

	// Setup headers and status code
	w.WriteHeader(code)
	w.Header().Set("Content-Type", "application/json")

	// HTTP errors are logged for debugging purposes
	log.Println("HTTP error written: [", code, "]", msg)

	res := new(jsonError)
	res.Code = code
	res.Error = msg

	resJSON, err := json.Marshal(res)
	if err != nil {
		log.Panic(err)
	}

	// Send reply
	w.Write(resJSON)
}
