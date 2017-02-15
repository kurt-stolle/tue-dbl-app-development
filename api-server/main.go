package main

// File: main.go
// Date: 2017-02-08
//
// TUE GO API SERVER PROJECT
//
// Written by K.H.W. Stolle for the DBL App Development at Eindhoven University of Technology
//
// This project is written using the Go programming language.
// See: https://golang.org/

import (
	"log"
	"net/http"
	"os"
	"runtime"

	"github.com/codegangsta/negroni"
	"github.com/gorilla/mux"
	"github.com/kurt-stolle/go-dbmdl"
	_ "github.com/kurt-stolle/go-dbmdl/postgres"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/controllers"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/authentication"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/postgres"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/models"
	"github.com/rs/cors"
)

// Init - this is a function that runs before main, and every file may have its own init() function
func init() {
	log.SetOutput(os.Stdout) // Redirect log output to Stdout so that debugging is easier
}

// Quick utility for setting up a route
var routes []string

func setupRoute(r *mux.Router, uri string, functions ...negroni.HandlerFunc) *mux.Route {
	var handlers []negroni.Handler

	for _, f := range functions {
		handlers = append(handlers, negroni.HandlerFunc(f))
	}

	return r.Handle(uri, negroni.New(handlers...))
}

// Quick utility for registering DBMDLs and catching the possible errors
func registerStruct(t string, s interface{}) {
	if err := dbmdl.RegisterStruct("postgres", t, s); err != nil {
		log.Panic("Failed to register struct: ", err)
	}
}

// Main function
func main() {
	var err error
	var port = os.Getenv("PORT")
	if port == "" {
		port = "9058"
	}

	// Setting this manually often helps speed up the application
	runtime.GOMAXPROCS(runtime.NumCPU())

	// Open postgres, just to see if it works
	conn, err := postgres.Open()
	if err != nil {
		log.Panic(err)
	}
	defer conn.Close()

	// This starts a goroutine that listens for queries from the dbmdl package
	// go-dbmdl is a package written by me (kurt) that makes it very easy to store datastructures (aka models) into a database of choice by generating queries for various actions
	go func() {
		ch := dbmdl.QueryChannel()

		for { // Eternal loop
			qry := <-ch // Listen for queries on the QueryChannel

			go func(q *dbmdl.Query) {
				rows, erra := conn.Query(q.String, q.Arguments...)

				if erra != nil { // Error handling - in case an invalid query is generated
					log.Panic("Failed to execute DBMDL query! \nQuery: ", q.String, "\nPQ Error: ", err, "\nArguments: ", q.Arguments)
				}

				if q.Result != nil { // Does the query request a results return? Then use the result channel to pass rows
					q.Result <- rows
				} else {
					rows.Close()
				}
			}(qry)
		}
	}()

	// Define data models to be added to the database
	// If you're not familiar with the language: (*x)(nil) allows us to pass type x without actually having to waste memory on an empty type (the function only needs to know the type - not utilise and sort of values)
	registerStruct("tuego_users", (*models.User)(nil))

	// Initialize the router, we use the Gorilla Mux library for this as it makes header and method matching easier
	r := mux.NewRouter()

	// Setup routes using the helper function setupRoute(...) function we created at the top of this file
	setupRoute(r, "/", controllers.Index).Methods(http.MethodGet)
	setupRoute(r, "/register", controllers.Register).Methods(http.MethodPost).Headers("Content-Type", "application/json") // application/json is the MIME type for JSON, encoding is always UTF-8
	setupRoute(r, "/login", controllers.Login).Methods(http.MethodPost).Headers("Content-Type", "application/json")

	setupRoute(r, "/images", authentication.Verify, controllers.Images).Methods(http.MethodPost, http.MethodGet)
	setupRoute(r, "/images/{image}", authentication.Verify, controllers.Image).Methods(http.MethodPatch, http.MethodGet)

	setupRoute(r, "/leaderboard", authentication.Verify, controllers.Leaderboard).Methods(http.MethodGet)

	setupRoute(r, "/users/{uuid}", authentication.Verify, controllers.User).Methods(http.MethodGet, http.MethodPatch)

	// Server setup - using the negroni library
	n := negroni.New()
	n.Use(negroni.NewLogger())   // This is for debugging
	n.Use(negroni.NewRecovery()) // This is for making sure we stay up after a non-fatal error
	n.Use(cors.New(cors.Options{ // Use CORS support module
		AllowCredentials: true,
		AllowedOrigins:   []string{"*"}, // We don't care about this being less-secure, as we're not running in production currently.
		AllowedHeaders:   []string{"Authorization", "Accept", "Content-Type"},
		AllowedMethods:   []string{http.MethodGet, http.MethodPost, http.MethodPatch, http.MethodDelete},
	}))
	n.UseHandler(r) // Implement routes

	log.Println("Web server listening on :" + port)
	log.Fatal(http.ListenAndServe(":"+port, n))
}
