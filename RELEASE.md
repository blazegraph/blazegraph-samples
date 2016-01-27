##Javadocs
[Javadocs](https://blazegraph.github.io/blazegraph-samples/apidocs/)

##Building a release

 * Make sure the pom is at the latest version-SNAPSHOT, i.e. 1.0.0-SNAPSHOT
 * Setup your environment for Sonatype [See here](http://central.sonatype.org/pages/apache-maven.html#other-prerequisites).
 * Make sure to set the blazegraph version to the latest release in Maven Central.
 * Add and commit all changes.   
 * `mvn -Pmaven-central release:clean release:prepare -Darguments="-DskipTests"` [See here](http://central.sonatype.org/pages/apache-maven.html#performing-a-release-deployment-with-the-maven-release-plugin).  You will be prompted to enter the next version number, which should be in the form X.Y.X, i.e. 0.1.1.  It's OK to accept the defaults.
 * `mvn -Pmaven-central release:perform -Darguments="-DskipTests"` [See here](http://central.sonatype.org/pages/apache-maven.html#performing-a-release-deployment-with-the-maven-release-plugin).
 * Checkout the release tag, `git checkout blazegraph-samples-1.0.0`, and publish the javadocs:  `./scripts/publishDocs.sh`.
 * Reverse merge into master and commit the changes:  `git checkout master`, `git merge blazegraph-samples-1.0.0`; `git push origin master`
 * Got to [Github](https://github.com/blazegraph/blazegraph-samples/releases) and update the release tag.

