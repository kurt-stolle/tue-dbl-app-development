@echo off

echo This deployment script requires Heroku CLI and Git to be installed
echo Pushing to Heroku master branch...
git subtree push --prefix api-server heroku master
