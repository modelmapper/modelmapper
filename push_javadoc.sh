#!/bin/sh
rm -rf docs
git clone git@github.com:jhalterman/modelmapper.git docs -b gh-pages
cd core
mvn javadoc:javadoc
cd ../docs
git add .
git commit -m "updated javadocs"
git push origin gh-pages