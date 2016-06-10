#!/usr/bin/env bash

git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git clone --branch=gh-pages https://${GH_TOKEN}@github.com/Dove-Bren/QuestManager repository

# Commit and Push the Changes
cd repository

DIR='com/skyisland/questmanager/QuestManager/'
mkdir $DIR
cp -rf ${HOME}/.m2/repository/${DIR}* $DIR
git add -f .
git commit -m "Lastest maven dependency on successful travis build ${TRAVIS_BUILD_NUMBER} auto-pushed to repository"
git push -f origin repository

