package authentication

import (
	"bufio"
	"crypto/rsa"
	"crypto/x509"
	"database/sql"
	"encoding/pem"
	"log"
	"os"
	"time"

	jwt "github.com/dgrijalva/jwt-go"
	"github.com/kurt-stolle/tue-dbl-app-development/api-server/core/postgres"

	"golang.org/x/crypto/bcrypt"
)

// JWTData is a structure containing keys for authentication
type JWTData struct {
	privateKey *rsa.PrivateKey
	PublicKey  *rsa.PublicKey
}

const (
	tokenDuration = 72
	expireOffset  = 3600
)

var authBackendInstance *JWTData

// GetJWTInstance will initialize a new instance of authentication data
func GetJWTInstance() *JWTData {
	if authBackendInstance == nil {
		authBackendInstance = &JWTData{
			privateKey: getPrivateKey(),
			PublicKey:  getPublicKey(),
		}
	}

	return authBackendInstance
}

// GenerateToken generates a user token
func (backend *JWTData) GenerateToken(userUUID string) (string, error) {
	token := jwt.New(jwt.SigningMethodRS512)
	token.Claims["iat"] = time.Now().Unix()
	token.Claims["sub"] = userUUID

	tokenString, err := token.SignedString(backend.privateKey)

	if err != nil {
		log.Panic(err)
	}

	return tokenString, nil
}

// Authenticate attempts to sign an authentication request
func (backend *JWTData) Authenticate(email, password string) (bool, string) {
	// Open database connection
	conn := postgres.Connect()

	// Select salt and password from database
	var uuid, salt, passwordHash string
	if err := conn.QueryRow("SELECT uuid, salt,password FROM tuego_users WHERE lower(email)=lower($1) LIMIT 1", email).Scan(&uuid, &salt, &passwordHash); err != nil {
		if err != sql.ErrNoRows {
			log.Panic(err)
		}
		return false, ""
	}

	// Compare the password from the database to the hash of the plain text password plus the salt
	return bcrypt.CompareHashAndPassword([]byte(passwordHash), []byte(password+salt)) == nil, uuid
}

func getPrivateKey() *rsa.PrivateKey {
	pwd, err := os.Getwd()
	if err != nil {
		log.Panic(err)
	}
	privateKeyFile, err := os.Open(pwd + "/keys/private.key")
	if err != nil {
		log.Panic(err)
	}

	pemfileinfo, _ := privateKeyFile.Stat()
	var size = pemfileinfo.Size()
	pembytes := make([]byte, size)

	buffer := bufio.NewReader(privateKeyFile)
	_, err = buffer.Read(pembytes)
	if err != nil {
		log.Panic(err)
	}

	data, _ := pem.Decode([]byte(pembytes))

	privateKeyFile.Close()

	privateKeyImported, err := x509.ParsePKCS1PrivateKey(data.Bytes)

	if err != nil {
		log.Panic(err)
	}

	return privateKeyImported
}

func getPublicKey() *rsa.PublicKey {
	pwd, err := os.Getwd()
	if err != nil {
		log.Panic(err)
	}
	publicKeyFile, err := os.Open(pwd + "/keys/public.key")
	if err != nil {
		log.Panic(err)
	}

	pemfileinfo, _ := publicKeyFile.Stat()
	var size = pemfileinfo.Size()
	pembytes := make([]byte, size)

	buffer := bufio.NewReader(publicKeyFile)
	_, err = buffer.Read(pembytes)
	if err != nil {
		log.Panic(err)
	}

	data, _ := pem.Decode([]byte(pembytes))

	publicKeyFile.Close()

	publicKeyImported, err := x509.ParsePKIXPublicKey(data.Bytes)

	if err != nil {
		log.Panic(err)
	}

	rsaPub, ok := publicKeyImported.(*rsa.PublicKey)

	if !ok {
		log.Panic(err)
	}

	return rsaPub
}
