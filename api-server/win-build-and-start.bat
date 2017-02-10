@echo off

echo "Fetching dependencies" &&
go get ./... &&
echo "Building DBL App Development webapi application" &&
go build &&
echo "Starting application" &&
api-server.exe &&