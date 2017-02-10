package controllers

import (
	"bytes"
	"encoding/json"
	"log"
	"net/http"
	"strconv"
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
func writeError(w http.ResponseWriter, code int, msg string) {
	// Setup headers and status code
	w.WriteHeader(code)
	w.Header().Set("Content-Type", "application/json")

	// HTTP errors are logged for debugging purposes
	log.Println("HTTP error written: [", code, "]", msg)

	// Concatenate
	var buffer bytes.Buffer

	buffer.WriteString("{\"code\":")
	buffer.WriteString(strconv.Itoa(code))
	buffer.WriteString(",\"error\":\"")
	buffer.WriteString(msg)
	buffer.WriteString("\"}")

	// Send reply
	w.Write(buffer.Bytes())
}
