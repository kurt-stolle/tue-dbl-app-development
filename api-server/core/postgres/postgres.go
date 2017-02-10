package postgres

// File: core/postgres/postgres.go
// Date: 2017-02-08
// Desc: This is a wrapper for pq, my preferred postgres driver for the database/sql standard package

import (
	"database/sql"

	_ "github.com/lib/pq" // pq extends the database/sql package, will allow us to use postgres
)

const postgresURL = "" // Harcode this as we're not going to use this app in production; we don't need a settings manager. TODO: Set me to some testing environment

// For storing an open database connection
var db *sql.DB

// Open will open a connection to the database
func Open() (conn *sql.DB, err error) {
	if db == nil {
		if connection, err := sql.Open("postgres", postgresURL); err == nil {
			db = connection
		} else {
			db = nil
			return nil, err
		}
	}

	return db, nil
}
