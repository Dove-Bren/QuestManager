#!/usr/bin/env bash
# Get to the Travis build directory, configure git and clone the repo
cd $HOME
git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/Dove-Bren/QuestManager gh-pages > /dev/null

# Commit and Push the Changes
cd gh-pages
git rm -rf ./docs
cp -Rf $HOME/build/* ./docs
git add -f .
git commit -m "Lastest javadoc on successful travis build ${TRAVIS_BUILD_NUMBER} auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null