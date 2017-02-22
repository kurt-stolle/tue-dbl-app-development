package services

import (
	"net/http"
	"reflect"

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
	where.AddClause("Finder=NULL")

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
