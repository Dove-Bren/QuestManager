#!/usr/bin/env bash

# Delete old release

# Create new release
GROUP="com/skyisland/questmanager"
PROJECT_NAME="QuestManager"
VERSION=(ls ${HOME}/.m2/repository/$GROUP/$PROJECT_NAME)
if [ "$(echo $VERSION | grep -o SNAPHOT)" == "SNAPSHOT" ]; then
    IS_PRE_RELEASE='true'
else
    IS_PRE_RELEASE='false'
fi
TAG_NAME="v$VERSION"
NAME="$PROJECT_NAME v$VERSION"
API_JSON="{\"tag_name\": \"$TAG_NAME\",\"target_commitish\": \"master\",\"name\": \"$NAME\",\"body\": \"Plugin release of version $VERSION from Travis build ${TRAVIS_BUILD_NUMBER}\",\"draft\": false,\"prerelease\": $IS_PRE_RELEASE}"
curl --data "$API_JSON" https://api.github.com/repos/Dove-Bren/QuestManager/releases?access_token=${GH_TOKEN}

# Upload assets to release
