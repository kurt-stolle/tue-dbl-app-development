package services

import (
	"errors"
	"log"
	"math/rand"
	"net/http"
	"net/mail"
	"time"

	"golang.org/x/crypto/bcrypt"

	"github.com/kurt-stolle/go-dbmdl"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/postgres"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/models"
	"github.com/pborman/uuid" // For UUID generation in the registration process
)

// randomString is a helper function that generates a random string of specified length
func randomString(strlen int) string {
	seed := time.Now().UTC().UnixNano()
	rand.Seed(seed + rand.Int63n(seed))

	const chars = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789@#$%&"

	result := make([]byte, strlen)
	for i := 0; i < strlen; i++ {
		result[i] = chars[rand.Intn(len(chars))]
	}
	return string(result)
}

// CreateUser creates a new user in the database
// Returns a http status code and possibly an error
func CreateUser(u *models.User, password string) (int, error) {
	// First, we do some verification on the data provided
	if len(password) < 4 || len(password) > 64 {
		return http.StatusBadRequest, errors.New("Password must have between 6 and 64 characters")
	}

	if len(u.Email) < 5 || len(u.Email) > 100 {
		return http.StatusBadRequest, errors.New("Email must have between 5 and 100 characters")
	}

	if _, err := mail.ParseAddress(u.Email); err != nil {
		return http.StatusBadRequest, errors.New("The provided e-mail adress is not an e-mail address")
	}

	var alreadyExists bool
	if postgres.Connect().QueryRow("SELECT TRUE FROM tuego_users WHERE Email=$1", u.Email).Scan(&alreadyExists); alreadyExists {
		return http.StatusBadRequest, errors.New("The provided e-mail address already exists")
	}

	// Secondly, we generate a UUID and Salt
	u.UUID = uuid.New()
	u.Salt = randomString(32)
	u.Points = 0

	// Then the password is set
	passwd, _ := bcrypt.GenerateFromPassword([]byte(password+u.Salt), 10)
	u.Password = string(passwd) // Cast array of bytes to string, no need to check for errors, as there aren't any available

	// Now use dbmdl to save this user to the database
	if err := dbmdl.Save(postgres.Connect(), u, nil); err != nil {
		return http.StatusInternalServerError, err
	}

	// TODO: Possibly send an e-mail to the address

	return http.StatusOK, nil
}

// GetUser returns a user form the database
func GetUser(uuid string) *models.User {
	u := new(models.User) // Setup a new user struct

	where := dbmdl.NewWhereClause("postgres") // Initialize a where clause
	where.AddValuedClause("UUID", uuid)       // Add the UUID to said where clause

	if err := dbmdl.Load(postgres.Connect(), u, where); err != nil {
		if err == dbmdl.ErrNotFound {
			return nil
		}
		log.Panic(err) // If the error is not a NotFound error, then something went terribly wrong!
	}

	return u
}

// AwardPoints gives points to a user
func AwardPoints(uuid string, points int) error {
	if _, err := postgres.Connect().Exec("UPDATE tuego_users SET Points=Points+$1 WHERE UUID=$2", points, uuid); err != nil {
		return err
	}
	return nil
}
