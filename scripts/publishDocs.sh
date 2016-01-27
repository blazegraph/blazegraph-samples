#!/bin/bash
#Script to publish javadoc to https://blazegraph.github.com/
BASE_DIR=`dirname $0`
PARENT_POM="${BASE_DIR}/../pom.xml"
DEST_DIR=blazegraph-samples/apidocs/
#You must have cloned https://github.com/blazegraph/blazegraph.github.io into a directory at the same level as where tinkerpop3 is checked out
GITHUB_PAGES="${BASE_DIR}/../../blazegraph.github.io"

if [ ! -d "${GITHUB_PAGES}" ] ; then

   echo "${GITHUB_PAGES} does not exist."
   echo "You must have cloned https://github.com/blazegraph/blazegraph.github.io into a directory at the same level as where bigdata is checked out."
   exit 1

fi

mvn -f "${PARENT_POM}" javadoc:aggregate

echo "Javadoc is located in ${BASE_DIR}/../target/site/apidocs/"

pushd `pwd`
mkdir -p "${GITHUB_PAGES}/${DEST_DIR}"
echo cp -rf "${BASE_DIR}"/../target/site/apidocs/* "${GITHUB_PAGES}/${DEST_DIR}"
cp -rf "${BASE_DIR}"/../target/site/apidocs/* "${GITHUB_PAGES}/${DEST_DIR}"
cd $"${GITHUB_PAGES}"
git pull
git add --all
git commit -m "Update for Blazegraph $DEST_DIR Javadocs"
git push origin master

popd 



