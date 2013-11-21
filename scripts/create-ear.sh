#!/bin/bash

# Function to find all files under current directory and subdirectories and replace all "sampleportal1" with "sampleportalX" in these files
function replaceSamplePortalPattern() {
  for I in $(find .); do        
    if [ -f $I ]; then
      echo "replacing sampleportal1 with sampleportal$1 in file: $I";
      cat  $I | sed s/sampleportal1/sampleportal$1/g > $I.bak
      mv $I.bak $I
    else 
      echo "Ignoring directory $I";
    fi;
  done;
}

if [ -z "$1" ]; then
  echo "Invalid usage!";
  echo "You need to add one argument specifying the number for EAR with sampleportal to create. For example 'scripts/create-ear.sh 2' will create sampleportal2.ear";
  exit
fi

# Delete old EAR if exists
rm sampleportal$1.ear
rm -rf test

# Create directory "test" and unzip sample container EAR file into it
mkdir test
cp -r sampleportal1/ear/target/sampleportal1.ear test/
cd test
unzip -q sampleportal1.ear
rm sampleportal1.ear

# Replace all "sampleportal1" patterns in META-INF of ear file
cd META-INF
replaceSamplePortalPattern $1;
cd ..

# Unzip config-jar and replace all "sampleportal1" patterns in all files in it. Create JAR again with new name
cd lib
unzip -q sampleportal1-config-0.1-SNAPSHOT.jar
rm sampleportal1-config-0.1-SNAPSHOT.jar
rm -rf META-INF/maven

replaceSamplePortalPattern $1;

echo "Creating JAR: sampleportal$1-config.jar"
jar cvf sampleportal$1-config.jar * > /dev/null
rm -rf conf
rm -rf META-INF
cd ..


# Unzip rest-sampleportal1.war and replace all "sampleportal1" patterns in all files in it. Create WAR again with new name
mkdir test2
cd test2
unzip -q ../rest-sampleportal1.war
rm ../rest-sampleportal1.war
rm -rf ./META-INF/maven

replaceSamplePortalPattern $1;

echo "Creating WAR: rest-sampleportal$1.war"
jar cvf rest-sampleportal$1.war * > /dev/null
cp rest-sampleportal$1.war ../
cd ..
rm -rf test2

# Unzip sampleportal1.war and replace all "sampleportal1" patterns in all files in it. Create WAR again with new name
mkdir test2
cd test2
unzip -q ../sampleportal1.war
rm ../sampleportal1.war
rm -rf ./META-INF/maven
mv WEB-INF/conf/sampleportal1 WEB-INF/conf/sampleportal$1

replaceSamplePortalPattern $1;

echo "Creating WAR: sampleportal$1.war"
jar cvf sampleportal$1.war * > /dev/null
cp sampleportal$1.war ../
cd ..
rm -rf test2

# BUNDLE WHOLE EAR
echo "Creating EAR: sampleportal$1.ear"
jar cvf sampleportal$1.ear * > /dev/null
cp sampleportal$1.ear ../
cd ..
rm -rf test
