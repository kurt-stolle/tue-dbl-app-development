package models

// Image represents an image as it is kept in the database
type Image struct {
	UUID       string  `dbmdl:"uuid, primary key"`
	UploadTime string  `dbmdl:"char(16), not null, default '1958-01-01 00:00'"` // Format: 2017-02-19 12:47
	Uploader   string  `dbmdl:"uuid, not null"`
	Finder     string  `dbmdl:"varchar(255) not null, default ''"`
	Latitude   float64 `dbmdl:"double precision not null, default 0.0"`
	Longitude  float64 `dbmdl:"double precision not null, default 0.0"`
}

// ManifestEntry is one entry of the image manifest. Links a User and Image model
type ManifestEntry struct {
	Image    Image
	Uploader User
}
