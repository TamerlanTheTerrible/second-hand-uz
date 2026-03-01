#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven Start Up Batch script
#
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars
# -----------------
#   M2_HOME - location of maven2's installed home dir
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
# ----------------------------------------------------------------------------

set -e

# OS specific support. $var _must_ be set to either true or false.
cygwin=false
msys=false
darwin=false
nonstop=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  MINGW*) msys=true ;;
  MSYS*) msys=true ;;
  Darwin*) darwin=true ;;
  NONSTOP*) nonstop=true ;;
esac

MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-"$(pwd)"}
wrapperJarPath="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
wrapperPropertiesPath="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

if [ -r "$wrapperPropertiesPath" ]; then
  distributionUrl=$(grep "^distributionUrl" "$wrapperPropertiesPath" | cut -d'=' -f2-)
fi

MAVEN_HOME_PARENT="$MAVEN_PROJECTBASEDIR/.mvn/wrapper"
MAVEN_HOME="$MAVEN_HOME_PARENT/$(basename "$distributionUrl" .zip)"

if [ -d "$MAVEN_HOME" ]; then
  MAVEN_CMD="$MAVEN_HOME/bin/mvn"
else
  # Download wrapper jar if not present
  if [ ! -e "$wrapperJarPath" ]; then
    wrapperUrl=$(grep "^wrapperUrl" "$wrapperPropertiesPath" | cut -d'=' -f2-)
    echo "Downloading Maven Wrapper..."
    if command -v curl > /dev/null 2>&1; then
      curl -fsSL -o "$wrapperJarPath" "$wrapperUrl"
    elif command -v wget > /dev/null 2>&1; then
      wget -q -O "$wrapperJarPath" "$wrapperUrl"
    else
      echo "curl or wget required to download Maven Wrapper" >&2
      exit 1
    fi
  fi

  # Extract Maven
  echo "Extracting Maven..."
  mkdir -p "$MAVEN_HOME_PARENT"
  if command -v unzip > /dev/null 2>&1; then
    tmpFile="$MAVEN_HOME_PARENT/maven-dist.zip"
    if command -v curl > /dev/null 2>&1; then
      curl -fsSL -o "$tmpFile" "$distributionUrl"
    else
      wget -q -O "$tmpFile" "$distributionUrl"
    fi
    unzip -q "$tmpFile" -d "$MAVEN_HOME_PARENT"
    rm "$tmpFile"
  else
    echo "unzip required" >&2
    exit 1
  fi
  MAVEN_CMD="$MAVEN_HOME/bin/mvn"
fi

exec "$MAVEN_CMD" "$@"
