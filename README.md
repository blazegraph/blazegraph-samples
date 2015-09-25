#Welcome to the Blazegraph Samples Project#
Blazegraph™ is our ultra high-performance graph database supporting Blueprints and RDF/SPARQL APIs. It supports up to 50 Billion edges on a single machine and has a High Availability and Scale-out architecture. It is in production use for Fortune 500 customers such as EMC, Autodesk, and many others.  It powers the Wikimedia Foundation's Wiki Data Query Service.  See the latest [Feature Matrix](http://www.blazegraph.com/product/).

[Sign up](http://eepurl.com/VLpUj) to get the latest news on Blazegraph.  

Please also visit us at our: [website](http://www.blazegraph.com), [wiki](https://wiki.blazegraph.com), and [blog](https://wiki.blazegraph.com/).

Find an issue?   Need help?  See [JIRA](https://jira.blazegraph.com) or purchase [Support](https://www.blazegraph.com/buy).

![image](http://blog.blazegraph.com/wp-content/uploads/2015/07/blazegraph_by_systap_favicon.png)

# blazegraph-samples
Samples for using Blazegraph™

To build everything run:

```
mvn package
```

Simple applications demonstrating using Blazegraph for loading/querying data in different modes:

To build a sample, cd in the directory and run:

```
cd sample-sesame-first
mvn package
```

1. sample-sesame-first - Sesame API in emmbedded mode
  https://wiki.blazegraph.com/wiki/index.php/First_Application_Tutorial

2. 'sample-sesame-embedded' - Sesame API in emmbedded mode
  https://wiki.blazegraph.com/wiki/index.php/Sesame_API_embedded_mode

3. 'sample-sesame-remote' - Sesame API in remote mode
  https://wiki.blazegraph.com/wiki/index.php/Sesame_API_remote_mode

4. 'sample-blueprints-embedded' - Blueprints API in embedded mode
  https://wiki.blazegraph.com/wiki/index.php/Blueprints_API_embedded_mode

5. 'sample-blueprints-remote' - Blueprints API in remote mode
  https://wiki.blazegraph.com/wiki/index.php/Blueprints_API_remote_mode

6. 'sample-rdr' - using RDF* and SPARQL* with Blazegraph™
  https://wiki.blazegraph.com/wiki/index.php/RDR

7.  'sample-customFunction-embedded'- Custom Embedded Function

8.  'sample-test' - Sample Unit Tests







