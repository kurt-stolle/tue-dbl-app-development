package controllers

import (
	"encoding/json"
	"log"
	"net/http"
	"os"
	"path"
	"strconv"

	"time"

	jwt "github.com/dgrijalva/jwt-go"
	"github.com/gorilla/context"
	"github.com/gorilla/mux"
	"github.com/kurt-stolle/go-dbmdl"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/postgres"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/models"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/services"
	"github.com/pborman/uuid"
)

// Images /images is a controller for receiving an image sent by a user and storing it in the filesystem
func Images(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	switch r.Method {
	case http.MethodPost: // Upload an image (JPEG)
		// Find the user
		token, ok := context.Get(r, "token").(*jwt.Token)
		if !ok {
			writeError(w, http.StatusUnauthorized)
			return
		}

		var uuidUser = (token.Claims.(*jwt.StandardClaims)).Subject

		if uuidUser == "" {
			writeError(w, http.StatusForbidden)
			return
		}

		// Scan the maount
		var amount int
		if err := postgres.Connect().QueryRow("SELECT COUNT(*) FROM tuego_images WHERE Uploader=$1 AND Finder=''", uuidUser).Scan(&amount); amount >= 5 {
			writeError(w, http.StatusConflict, "You already have 5 active pictures")
			return
		} else if err != nil {
			log.Fatal("Could not check max image count:", err)
		}

		// Create a new image struct
		img := new(models.Image)
		img.UploadTime = time.Now().Format("2006-01-02 15:04")
		img.Uploader = uuidUser
		img.UUID = uuid.New()

		// Accept the upload
		pwd, err := os.Getwd()
		if err != nil {
			writeError(w, http.StatusInternalServerError)
			return
		}

		p := path.Join(pwd, img.UUID+".jpg")

		ok, coords := parseImageUpload(w, r, p, 6000, 3000, 2000, services.JPEG)
		if !ok {
			return
		}

		img.Latitude = coords.Latitude
		img.Longitude = coords.Longitude

		// Save the image struct
		if err := dbmdl.Save(postgres.Connect(), img, nil); err != nil {
			writeError(w, http.StatusInternalServerError, "Failed to save image data")
			return
		}

		writeJSON(w, img)
	case http.MethodGet: // Fetch an manifest of images
		page, err := strconv.Atoi(r.URL.Query().Get("page"))
		if err != nil {
			page = 1
		}

		status, images, pag := services.GetActiveImagesWithAssociatedUsers(page, 25)
		if status != http.StatusOK {
			writeError(w, status)
			return
		}

		writeJSONPaginated(w, images, pag)
	default: // Return a friendly error
		writeError(w, http.StatusNotImplemented, "Method not implemented: "+r.Method)
	}
}

// Image /images/{uuid} is a controller for guessing the location of an image
func Image(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	uuid := mux.Vars(r)["uuid"]
	if uuid == "" {
		writeError(w, http.StatusBadRequest)
		return
	}

	switch r.Method {
	case http.MethodPost: // Guess the location of an image
		coords := new(models.Coordinates)

		dec := json.NewDecoder(r.Body)
		if err := dec.Decode(&coords); err != nil {
			writeError(w, http.StatusBadRequest)
			return
		}

		// Find the uploading user
		token, ok := context.Get(r, "token").(*jwt.Token)
		if !ok {
			writeError(w, http.StatusUnauthorized)
			return
		}

		// claims.Subject is the uuid of the issued user
		claims, ok := token.Claims.(*jwt.StandardClaims)
		if !ok || claims.Subject == "" {
			writeError(w, http.StatusForbidden)
			return
		}

		// Determine whether it is found or not
		if services.GuessImage(claims.Subject, uuid, coords) {
			writeSuccess(w)
			return
		}

		writeError(w, http.StatusNotFound, "Incorrect guess")
	case http.MethodGet: // Get a picture (JPEG)

		// TODO return image model with UUID
	default: // The method isn't implemented
		writeError(w, http.StatusNotImplemented, "Method not implemented: "+r.Method)
	}
}

// ImageFile returns the actual file as jpeg
func ImageFile(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	uuid := mux.Vars(r)["uuid"]
	if uuid == "" {
		writeError(w, http.StatusBadRequest)
		return
	}

	serveFile(w, r, "./"+uuid+".jpg")
}
