package services

import (
	"net/http"

	dbmdl "github.com/kurt-stolle/go-dbmdl"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/postgres"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/models"
)

// GetActiveImages returns a manifest of images
func GetActiveImages(page, amount int) (int, []*models.Image, *dbmdl.Pagination) {
	// Where clause for making sure we select only active images
	where := dbmdl.NewWhereClause("postgres")
	where.AddClause("Finder=NULL")

	// Pagination
	pag := dbmdl.NewPagination(uint(page), uint(amount))

	// DBMDL fetch
	res, err := dbmdl.Fetch(postgres.Connect(), "tuego_images", (*models.Image)(nil), where, pag)
	if err != nil {
		return http.StatusNotFound, nil, nil
	}

	// Cast the result set
	var images []*models.Image
	for _, img := range res.Data {
		images = append(images, img.(*models.Image)) // We needn't error check because the cast type is guaranteed by dbmdl
	}

	// Return our findings
	return http.StatusOK, images, pag
}

// GetUserImages returns a manifest of images uploaded by a single user, active or not
func GetUserImages(uuid string, page, amount int) (int, []*models.Image, *dbmdl.Pagination) {
	// Where clause for selecting the proper uploader
	where := dbmdl.NewWhereClause("postgres")
	where.AddValuedClause("Uploader="+where.GetPlaceholder(0), uuid)

	// Pagination
	pag := dbmdl.NewPagination(uint(page), uint(amount))

	// DBMDL fetch
	res, err := dbmdl.Fetch(postgres.Connect(), "tuego_images", (*models.Image)(nil), where, pag)
	if err != nil {
		return http.StatusNotFound, nil, nil
	}

	// Cast the result set
	var images []*models.Image
	for _, img := range res.Data {
		images = append(images, img.(*models.Image))
	}

	// Return our findings
	return http.StatusOK, images, pag
}
