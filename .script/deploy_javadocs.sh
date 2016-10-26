#!/bin/bash

set -ex

REPO="git@github.com:6thsolution/EasyMVP.git"

DIR=temp-clone

# Delete any existing temporary website clone
rm -rf $DIR

# Clone the current repo into temp folder
git clone $REPO $DIR

# Move working directory into temp folder
cd $DIR
# Checkout and track the gh-pages branch
git checkout -t origin/gh-pages

# Artifactory location
server=https://oss.jfrog.org
repo=oss-snapshot-local

# jfrog artifacts location
for name in easymvp-api easymvp-rx-api
	do
	artifact=com/sixthsolution/easymvp/$name
	path=$server/$repo/$artifact
	version=`curl -s $path/maven-metadata.xml | grep latest | sed "s/.*<latest>\([^<]*\)<\/latest>.*/\1/"`
	build=`curl -s $path/$version/maven-metadata.xml | grep '<value>' | head -1 | sed "s/.*<value>\([^<]*\)<\/value>.*/\1/"`
	jar=$name-$build-javadoc.jar
	url=$path/$version/$jar

	# Download
	echo $url
	curl -L $url > "$name".zip
	javadoc="${name:8}-javadoc"
	mkdir -p "$javadoc"
	unzip "$name".zip -d "$javadoc"
	rm "$name".zip
done

# Stage all files in git and create a commit
git add .
git add -u
git commit -m "java docs updated at $(date)"

# Push the new files up to GitHub
git push origin gh-pages

cd ..
rm -rf $DIR