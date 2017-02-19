@echo off

cd api-server
echo Configuring environment
set PORT=8058
set DATABASE_URL=postgres://filflszcxcjdmv:bb55cfa184bbe134baaf48f6604eda37a85e3249b925f8a60ebe05cf78646a73@ec2-176-34-111-152.eu-west-1.compute.amazonaws.com:5432/de6pae7l3fmb0i
echo Fetching dependencies
go get ./...
echo Building GO application
go build
echo Starting application
api-server.exe
cd ..