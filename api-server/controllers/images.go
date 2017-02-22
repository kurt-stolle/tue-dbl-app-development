package controllers

import (
	"encoding/json"
	"net/http"
	"os"
	"path"

	"time"

	jwt "github.com/dgrijalva/jwt-go"
	"github.com/gorilla/context"
	"github.com/gorilla/mux"
	dbmdl "github.com/kurt-stolle/go-dbmdl"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/postgres"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/models"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/services"
	"github.com/pborman/uuid"
)

// Images is a controller for receiving an image sent by a user and storing it in the filesystem
func Images(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	switch r.Method {
	case http.MethodPost: // Upload an image (JPEG)
		// Find the user
		var uuidUser string
		if token, ok := context.Get(r, "token").(*jwt.Token); !ok {
			writeError(w, http.StatusUnauthorized)
		} else {
			uuidUser = token.Claims["sub"].(string)

			if uuidUser == "" {
				writeError(w, http.StatusForbidden)
			}
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

		p := path.Join(pwd, "uploads", img.UUID+".jpg")

		parseImageUpload(w, r, p, 250, 3000, 2000)

		// Save the image struct
		if err := dbmdl.Save(postgres.Connect(), img, nil); err != nil {
			writeError(w, http.StatusInternalServerError, "Failed to save image data")
			return
		}

	case http.MethodGet: // Fetch an manifest of images
		status, images, pag := services.GetActiveImages(1, 250)
		if status != http.StatusOK {
			writeError(w, status)
		}
		writeJSONPaginated(w, images, pag)
	default: // Return a friendly error
		writeError(w, http.StatusNotImplemented, "Method not implemented: "+r.Method)
	}
}

// Image is a controller for guessing the location of an image
func Image(w http.ResponseWriter, r *http.Request, next http.HandlerFunc) {
	uuid := mux.Vars(r)["uuid"]
	if uuid == "" {
		writeError(w, http.StatusBadRequest)
	}

	switch r.Method {
	case http.MethodPatch: // Guess the location of an image
		coords := new(models.Coordinates)

		dec := json.NewDecoder(r.Body)
		if err := dec.Decode(&coords); err != nil {
			writeError(w, http.StatusBadRequest)
		}
	case http.MethodGet: // Get a picture (JPEG)
		serveFile(w, r, "/uploads/"+uuid+".jpg")
	default: // The method isn't implemented
		writeError(w, http.StatusNotImplemented, "Method not implemented: "+r.Method)
	}
}
