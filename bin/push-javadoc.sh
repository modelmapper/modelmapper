#!/bin/sh
# Pushes javadocs for current build
# Run from top level dir

mvn javadoc:javadoc
rm -rf target/docs
git clone git@github.com:modelmapper/modelmapper.github.io.git target/docs > /dev/null
cd target/docs
git rm -rf javadoc
mkdir -p javadoc
mv -v ../../core/target/site/apidocs/* javadoc
git add -A -f javadoc
git commit -m "Updated JavaDocs"
git push -fq origin master > /dev/null

echo "Published Javadoc to modelmapper.github.io.\n"