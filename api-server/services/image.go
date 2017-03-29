package services

import (
	"bytes"
	"image"
	"image/jpeg"
	"image/png"
	"io"
	"log"
	"net/http"
	"reflect"
	"strconv"

	dbmdl "github.com/kurt-stolle/go-dbmdl"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/postgres"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/models"
)

// GetActiveImages returns a manifest of images
func GetActiveImages(page, amount int) (int, []*models.Image, *dbmdl.Pagination) {
	// Fetch the type
	imageType := reflect.TypeOf((*models.Image)(nil)).Elem()

	// Where clause for making sure we select only active images
	where := dbmdl.NewWhereClause(imageType)
	where.AddClause("Finder=''")

	// Pagination
	dbp := dbmdl.NewPagination(page, amount)

	// DBMDL fetch
	data, pag, err := dbmdl.Fetch(postgres.Connect(), imageType, where, dbp)
	if err != nil {
		return http.StatusNotFound, nil, nil
	}

	// Cast the result set
	images := make([]*models.Image, len(data))
	for i, img := range data {
		images[i] = img.(*models.Image) // We needn't error check because the cast type is guaranteed by dbmdl
	}

	// Return our findings
	return http.StatusOK, images, pag
}

// GetUserImages returns a manifest of images uploaded by a single user, active or not
func GetUserImages(uuid string, page, amount int) (int, []*models.Image, *dbmdl.Pagination) {
	// Store the type
	imageType := reflect.TypeOf((*models.Image)(nil)).Elem()

	// Where clause for selecting the proper uploader
	where := dbmdl.NewWhereClause(imageType)
	where.AddValuedClause("Uploader="+where.GetPlaceholder(0), uuid)

	// Pagination
	dpb := dbmdl.NewPagination(page, amount)

	// DBMDL fetch
	data, pag, err := dbmdl.Fetch(postgres.Connect(), imageType, where, dpb)
	if err != nil {
		return http.StatusNotFound, nil, nil
	}

	// Cast the result set
	images := make([]*models.Image, len(data))
	for i, img := range data {
		images[i] = img.(*models.Image)
	}

	// Return our findings
	return http.StatusOK, images, pag
}

// Define some macro constants
const (
	JPEG = "image/jpeg"
	PNG  = "image/png"
)

// WriteImage will create a buffer that can be written as a reply
func WriteImage(w io.Writer, img *image.Image, encoding string) (string, error) {
	var buffer = new(bytes.Buffer)
	var err error

	// Parse depending on encoding
	switch encoding {
	case JPEG:
		err = jpeg.Encode(buffer, *img, nil)
	case PNG:
		err = png.Encode(buffer, *img)
	}

	if err != nil {
		return "", err
	}

	// Get the length
	l := strconv.Itoa(len(buffer.Bytes()))

	// Check if is HTTP
	if wHTTP, ok := w.(http.ResponseWriter); ok {
		wHTTP.Header().Set("Content-Type", encoding)
		wHTTP.Header().Set("Content-Length", l)
	}

	// Write
	w.Write(buffer.Bytes())

	// Return bytes, length and no error
	return l, nil
}

// ParseImage treats a file as an image
func ParseImage(file []byte) (*image.Image, error) {
	// Decode image
	img, _, err := image.Decode(bytes.NewReader(file))
	if err != nil {
		return nil, err
	}

	return &img, nil
}

// VerifyImageSize verified whether an image is of proper size (px)
func VerifyImageSize(img *image.Image, width int, height int) bool {

	b := (*img).Bounds()

	log.Println("Image dim: ", b)
	return (b.Max.X == width && b.Max.Y == height)
}

// VerifyFileType checks whether a file type is what we expect
func VerifyFileType(f []byte, types ...string) bool {
	// Check if the detected type equals one of the provided types
	t := http.DetectContentType(f)
	for _, x := range types {
		if t == x {
			return true
		}
	}

	return false
}

// How much may the guess location differ from the actual location?
const maxRangeDiffernece float64 = 1.0

// GuessImage checks whether the image was guessed correctrly
func GuessImage(uuidUser, uuidImage string, coords *models.Coordinates) bool {
	// First, load the image Coordinates
	img := new(models.Image)
	where := dbmdl.NewWhereClause("postgres")
	where.AddValuedClause("UUID="+where.GetPlaceholder(0), uuidImage) // UUID of img model
	where.AddClause("Finder=''")                                      // Not yet found

	if err := dbmdl.Load(postgres.Connect(), img, where); err != nil {
		if err == dbmdl.ErrNotFound {
			return false
		}

		log.Panic("Failed to fetch image from database: ", err)
	}

	// Check Coordinates
	if !((img.Latitude+maxRangeDiffernece) > coords.Latitude && (img.Latitude-maxRangeDiffernece) < coords.Latitude &&
		(img.Longitude+maxRangeDiffernece) > coords.Longitude && (img.Longitude-maxRangeDiffernece) < coords.Longitude) {
		return false
	}

	// Award points
	if err := AwardPoints(uuidUser, 15); err != nil {
		log.Panic("Could not award points to user: ", err)
	}

	// Delete file
	// TODO

	// Write back to database
	where = dbmdl.NewWhereClause("postgres")
	where.AddValuedClause("UUID="+where.GetPlaceholder(0), uuidImage)

	img.Finder = uuidUser
	if err := dbmdl.Save(postgres.Connect(), img, where); err != nil {
		log.Panic(err)
	}

	return true
}
