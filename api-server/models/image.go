package models

// Image represents an image as it is kept in the database
type Image struct {
	UUID       string `dbmdl:"uuid, primary key"`
	UploadTime string `dbmdl:"char(16), not null, default '1958-01-01'"` // Format: 2017-02-19 12:47
	Uploader   string `dbmdl:"uuid, not null"`
	Finder     string `dbmdl:"uuid"`
}
