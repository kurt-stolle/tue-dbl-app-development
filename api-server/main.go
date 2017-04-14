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
	"io"
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

	// Add the URI to the routes slice so that we can display it on the index page
	routes = append(routes, uri)

	return r.Handle(uri, negroni.New(handlers...))
}

// Quick utility for registering DBMDLs and catching the possible errors
func registerStruct(t string, s interface{}) {
	if err := dbmdl.RegisterStruct(postgres.Connect(), "postgres", t, s); err != nil {
		log.Panic(err)
	}

	if err := dbmdl.GenerateTable(postgres.Connect(), s); err != nil {
		log.Panic(err)
	}
}

// Main function
func main() {

	// Setting this manually often helps speed up the application
	runtime.GOMAXPROCS(runtime.NumCPU())

	// Open postgres, just to see if it works
	conn := postgres.Connect()
	defer conn.Close() // Close connection should the program end

	// Define data models to be added to the database
	// If you're not familiar with the language: (*x)(nil) allows us to pass type x without actually having to waste memory on an empty type (the function only needs to know the type - not utilise and sort of values)
	registerStruct("tuego_users", (*models.User)(nil))
	registerStruct("tuego_images", (*models.Image)(nil))

	// Initialize the router, we use the Gorilla Mux library for this as it makes header and method matching easier
	r := mux.NewRouter()

	// Setup routes using the helper function setupRoute(...) function we created at the top of this file
	setupRoute(r, "/", indexRouteController).Methods(http.MethodGet)
	setupRoute(r, "/register", controllers.Register).Methods(http.MethodPost) // application/json is the MIME type for JSON, encoding is always UTF-8
	setupRoute(r, "/login", controllers.Login).Methods(http.MethodPost)
	setupRoute(r, "/whoami", controllers.WhoAmI).Methods(http.MethodGet)

	setupRoute(r, "/images", authentication.Verify, controllers.Images).Methods(http.MethodPost, http.MethodGet)
	setupRoute(r, "/images/{uuid}", authentication.Verify, controllers.Image).Methods(http.MethodPost)
	setupRoute(r, "/images/{uuid}/image.jpg", controllers.ImageFile).Methods(http.MethodGet)

	setupRoute(r, "/leaderboard", controllers.Leaderboard).Methods(http.MethodGet)

	setupRoute(r, "/users/{uuid}", authentication.Verify, controllers.User).Methods(http.MethodGet, http.MethodPost)

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

	log.Println("Web server listening on :80")
	log.Fatal(http.ListenAndServe(":80", n))
}

// The index should list the routes that we have available
func indexRouteController(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	io.WriteString(w, "Welcome to the TU/e GO web API\nCreated for bachelor course DBL App Development\n\nAvailable routes:\n")
	for _, rt := range routes {
		io.WriteString(w, " "+rt+"\n")
	}

	io.WriteString(w, "\nUse REST-compliant methods for manipulating datasets\nAll routes other than the index and image upload accept \na JSON object and will always write a JSON object")
	io.WriteString(w, "\n\nCreated by K.H.W. Stolle (k.h.w.stolle@student.tue.nl)\n\nCopyright 2017 Eindhoven University of Technology")
}
