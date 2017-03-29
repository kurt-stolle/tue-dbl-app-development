package controllers

import (
	"bytes"
	"fmt"
	"image"
	"io"
	"log"
	"net/http"
	"os"
	"strconv"

	"github.com/kurt-stolle/tue-dbl-app-development/api-server/services"
)

// parse upload handles a file upload
func parseImageUpload(w http.ResponseWriter, r *http.Request, path string, maxSizeKB int64, maxWidth int, maxHeight int, filetype ...string) bool {
	if len(filetype) == 0 || filetype[0] == "" {
		log.Panic("No filetype provided!")
	}

	r.Body = http.MaxBytesReader(w, r.Body, maxSizeKB*1024) // max 400 kB
	r.ParseMultipartForm(32 << 20)

	// Parse and open file
	fTemp, _, err := r.FormFile("file")
	if err != nil {
		log.Println("Upload failled: ", err)
		w.WriteHeader(http.StatusBadRequest)
		w.Write([]byte("Failed to form file from form data encoding"))
		return false
	}
	defer fTemp.Close()

	// Write out into memory
	cur, err := fTemp.Seek(0, 1)
	if err != nil {
		writeError(w, http.StatusBadRequest, "File seek failed (1)")
		return false
	}
	size, err := fTemp.Seek(0, 2)
	if err != nil {
		writeError(w, http.StatusBadRequest, "File seek failed (2)")
		return false
	}
	_, err = fTemp.Seek(cur, 0)
	if err != nil {
		writeError(w, http.StatusBadRequest, "File seek failed (3)")
		return false
	}

	file := make([]byte, size)
	if _, err = fTemp.Read(file); err != nil {
		writeError(w, http.StatusBadRequest, "Could not write file into memory")
		return false
	}

	// Check filetype
	if !services.VerifyFileType(file, filetype...) {
		writeError(w, http.StatusBadRequest, "File must be "+filetype[0]+" format")
		return false
	}

	// Parse as image
	img, err := services.ParseImage(file)
	if err != nil {
		fmt.Println(err)
		writeError(w, http.StatusForbidden, "Could not parse as image")
		return false
	}

	// Check size
	if !services.VerifyImageSize(img, maxWidth, maxHeight) {
		writeError(w, http.StatusBadRequest, "File must be "+strconv.Itoa(maxWidth)+"x"+strconv.Itoa(maxHeight)+" pixels")
		return false
	}

	// Open filesystem
	fSys, err := os.OpenFile(path, os.O_WRONLY|os.O_CREATE, 0666)
	if err != nil {
		log.Println("Filesystem open failled: ", err)
		writeError(w, http.StatusInternalServerError, "Failed to allocate image on filesystem")
		return false
	}
	defer fSys.Close()

	// Copy temp file to filesystem
	if _, err = io.Copy(fSys, bytes.NewReader(file)); err != nil {
		log.Println("Filesystem copy failed: ", err)
		writeError(w, http.StatusInternalServerError, "Failed to copy image to filesystem")
		return false
	}

	return true
}

// Writes an image to the response writer
func writeImage(w http.ResponseWriter, img *image.Image, encoding string) {
	_, err := services.WriteImage(w, img, encoding)
	if err != nil {
		log.Println("Image write failed: ", err)
		writeError(w, http.StatusInternalServerError, "Failed to write image")
	}
}

// Serves a file from the fiilesystem
func serveFile(w http.ResponseWriter, r *http.Request, path string) {
	if _, err := os.Stat(path); os.IsNotExist(err) {
		writeError(w, http.StatusNotFound)
		return
	}

	http.ServeFile(w, r, path)
}
