# PraxiconVideoAnnotation
Annotation GUI for actions in videos based on the [PRAXICON knowledge base](https://github.com/CSRI/PraxiconDB).

## Installation

### PRAXICON
This tool is based on PRAXICON, so it requires [PraxiconDB](https://github.com/CSRI/PraxiconDB) to be set up in your system.
**praxicondb** package must be imported into the maven repository.

### WORDNET
[WordNet-3.0](https://wordnet.princeton.edu/wordnet/download/) needs to be installed in your system to run PraxiconVideoAnnotation

### Neo4J
[Neo4J Graph DB](https://neo4j.com/) is optional. If you want to enable the migration of PRAXICON to Neo4J functionality, a fresh installation of Neo4J is required.

### Config file
PraxiconVideoAnnotation requires a config.properties. Create this file according to the template (config.properties.template) in src/main/resources folder.

### Video list
A video list need to be loaded in order to start the annotation. Before the first usage prepare a CSV file containing 2 columns (comma separated), an unique ID of the video resource and its URI.

ID,URI

Import it from the interface through the load button.


