#!/usr/bin/env bash

# Delete old release

# Create new release
TAG_NAME="maven"
GROUP="com/skyisland/questmanager"
PROJECT_NAME="QuestManager"
VERSION="0.0.1-SNAPSHOT"
if [ "$(echo $VERSION | grep -o SNAPHOT)" == "SNAPSHOT" ]; then
    IS_PRE_RELEASE='true'
else
    IS_PRE_RELEASE='false'
fi
NAME="$GROUP/$PROJECT_NAME/$VERSION"
API_JSON="{\"tag_name\": \"$TAG_NAME\",\"target_commitish\": \"master\",\"name\": \"$NAME\",\"body\": \"Maven Release of version $VERSION from Travis build ${TRAVIS_BUILD_NUMBER}\",\"draft\": false,\"prerelease\": $IS_PRE_RELEASE}"
curl --data "$API_JSON" https://api.github.com/repos/Dove-Bren/QuestManager/releases?access_token=${GH_TOKEN}

# Upload assets to release