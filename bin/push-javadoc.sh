#!/bin/sh
# run from bin dir
cd ../
rm -rf docs
git clone git@github.com:jhalterman/modelmapper.git docs -b gh-pages
cd core
mvn -Pjavadoc javadoc:javadoc
cd ../docs
git add -A
git commit -m "updated javadocs"
git push origin gh-pages
