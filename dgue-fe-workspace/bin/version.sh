#!/bin/bash

PROJECT_NAME=app-ainop
PROJECT_PATH=`pwd`/projects/${PROJECT_NAME}

VERSION_FILE=$PROJECT_PATH/src/app/app.version.ts
> $VERSION_FILE
echo "// This file was generated on $(date)
export const appVersion: string = '$1';" >> $VERSION_FILE
git add $VERSION_FILE
