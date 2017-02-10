package models

// File: services/user.go
// Date: 2017-02-08
// Desc: This file includes all user-related services.

// User is a model for users, including dbmdl tags (see: ..\main.go)
type User struct {
	UUID     string `dbmdl:"uuid, primary key"`                     // UUID, generated on user creation and never changed afterwards
	Password string `dbmdl:"varchar(255), not null, omit" json:"-"` // Password, json:"-" means that this can never (accidentally) be sent with a JSON response
	Salt     string `dbmdl:"varchar(32), not null, omit" json:"-"`  // Salt, the stuff added to the password to make rainbowing exponentially more difficult. Also not sent to requesters
	Email    string `dbmdl:"varchar(100), not null"`                // E-mail address
	UserName string `dbmdl:"varchar(20), not null, default ''"`     // Username as picked by the user
}
