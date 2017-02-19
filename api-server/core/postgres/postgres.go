package postgres

import (
	"database/sql"
	"log"
	"os"

	_ "github.com/lib/pq" // pq extends the database/sql package, will allow us to use postgres
)

// For storing an open database connection
var db *sql.DB

// Open will open a connection to the database
func Connect() (conn *sql.DB) {
	if db == nil {
		if connection, err := sql.Open("postgres", os.Getenv("DATABASE_URL")); err == nil {
			db = connection
		} else {
			log.Fatal("Database connection failed!\n", err)
		}
	}

	return db
}
