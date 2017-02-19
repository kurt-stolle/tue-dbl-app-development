package services

import (
	"database/sql"
	"log"
	"strconv"

	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/postgres"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/models"
)

// GetLeaderboard returns a leaderboard ordered by points desc
func GetLeaderboard(a int) []*models.LeaderboardEntry {
	// Bounds for the amount of rows fetched to prevent DOS attack
	if a < 10 {
		a = 10
	} else if a > 100 {
		a = 100
	}

	// Slice for storing results
	var res []*models.LeaderboardEntry

	// Perform a query
	rows, err := postgres.Connect().Query("SELECT Name,Points FROM tuego_users ORDER BY Points DESC LIMIT " + strconv.Itoa(a))
	if err != nil {
		if err == sql.ErrNoRows {
			return res
		}
		log.Panic(err)
	}

	// Loop through the rows, adding entries with each pass
	for rows.Next() {
		// Initialize a new entry
		lbe := new(models.LeaderboardEntry)

		// Scan into the Name and Points address
		rows.Scan(&lbe.Name, &lbe.Points)

		// Append the row to the slice
		res = append(res, lbe)
	}

	// Return the result
	return res
}
