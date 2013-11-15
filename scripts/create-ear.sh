#!/bin/bash

if [ -z "$1" ]; then
  echo "Invalid usage! You need to add one argument specifying the number for EAR with sample-portal to create. For example 'create-ear.sh 2' will create sample-portal2.ear"
fi

# Create directory "test" and unzip sample-container EAR file into it
mkdir test
cp -r sample-portal1/ear/target/sample-portal1.ear test/
cd test
unzip -q sample-portal1.ear
rm sample-portal1.ear

# Unzip config-jar and replace all "sample-portal1" patterns in all files in it. Create JAR again with new name
cd lib
unzip -q sample-portal1-config-0.1-SNAPSHOT.jar
rm sample-portal1-config-0.1-SNAPSHOT.jar
rm -rf META-INF/maven

replaceSamplePortalPattern $1;

jar cvf sample-portal$1-config.jar *
rm -rf conf
rm -rf META-INF


# Replace all "sample-portal1" patterns in META-INF of ear file
cd ../META-INF
replaceSamplePortalPattern $1;
cd ..


# Unzip rest-sample-portal1.war and replace all "sample-portal1" patterns in all files in it. Create WAR again with new name
mkdir test2
cd test2
unzip -q ../rest-sample-portal1.war
rm ../rest-sample-portal1.war
rm -rf ./META-INF/maven

replaceSamplePortalPattern $1;

jar cvf rest-sample-portal$1.war *
cp rest-sample-portal$1.war ../
cd ..
rm -rf test2

# Unzip sample-portal1.war and replace all "sample-portal1" patterns in all files in it. Create WAR again with new name
mkdir test2
cd test2
unzip -q ../sample-portal1.war
rm ../sample-portal1.war
rm -rf ./META-INF/maven
mv WEB-INF/conf/sample-portal1 WEB-INF/conf/sample-portal$1

replaceSamplePortalPattern $1;

jar cvf sample-portal$1.war *
cp sample-portal$1.war ../
cd ..
rm -rf test2

# BUNDLE WHOLE EAR
jar cvf sample-portal$1.ear *
cp sample-portal$1.ear ../
cd ..
rm -rf test


# Find all files under current directory and subdirectories and replace all "sample-portal1" with "sample-portalX" in these files
function replaceSamplePortalPattern {
  for I in $(find .); do
    echo "replacing sample-portal1 with sample-portal$1 in file: $I";
    if [ -f $I ]; then
      cat  $I | sed s/sample-portal1/sample-portal$1/g > $I.bak
      mv $I.bak $I
    fi;
  done;
}