# BuildConstrains.py #
# BuildConstrains.java #

## The program for constructing all independent constraints for the values of flows at incoming and outgoing vertices, at which there is a solution to the distributive problem on a bipartite graph. ##

The source graph data are entered by a csv text file without headers.
The results are output as a set of inequalities between the values at the vertices for the main or dual graph.

### Parameters
1. The name of the input file with the graph
2. Operating mode (optional)

### Parameters description
1. The input file with the graph has a csv format without headers with a field separator character ',' and describes a bipartite graph (X,Y).
   In each line 
	1st field: vertex name from X, 
	others fields: a set of vertex names from Y connected by an edge to the vertex of field 1. 
2. =0: constraints are built for the main and dual graphs - applied by default;
   =1: constraints are built only for the main graph;
   =2: constraints are built only for the dual graph;
   
   
### There are three program versions.
- First version by Python3 procedures is in branch **main**.
- Second version by Python3 classes is in branch **oop**.
- Third version by Java-17 is in branch **java-full**.  

The current Python 3 version does not list constraints in the form of inequalities.

# FullNet.java #

## The program for statistical analysis of the number of all independent constraints for flows in a network under which there is a solution to a distributive problem on a bipartite graph. All fixed-size networks are being investigated ##

### Parameters ###
1. X Network components size
2. Y Network components size

### Output ###
Mathematical expectation and average deviation of the network size.
   
The SQLite database TransportNet.s3db is used.


# RandomNets.java #

## The program for statistical analysis of the number of all independent constraints for flows in a network under which there is a solution to a distributive problem on a bipartite graph. Random fixed-size networks are being investigated ##

### Parameters ###
1. X Network components size
2. Y Network components size
3. К-во генерируемых сетей
4. Operating mode (optional) =12: the calculation is added to the previous one

### Output ###
Mathematical expectation and average deviation of the network size.
   
The SQLite database TransportNet.s3db is used.


# CheckResource.java #

## The procedure for calculating all constraints for a specific set of resources ##

### Parameters ###
1. The name of the input file with the graph 
2. The name of the input file with the set of resources
3. Operating mode (optional)

### Parameters description ###
1. The structure is the same as in BuildConstrains
2.
    * The input file with a set of resources has a csv format without headers with a field separator character ',' and consists of 2 lines. 1 line is the value of the incoming resource for each source, 2 line is the value of the outgoing resource for each consumer. The order of the values in each row corresponds to the order of the vertices in the graph. In each resource, the symbol '.' is used to separate the integer and fractional parts. 
    * The input file with a set of resources is in json format. The key "X" describes the sources, the key "Y" describes the consumers in the original graph. These objects have the same structure of the list of "node" and "value" objects. The "node" object is an array of vertex names, the "value" object is the corresponding numeric array of resource values.
    * Example:
{"X":{"node":["1", "2", "3"],"value":[1.0, 2.0, 3.0]},"Y":{"node":["0", "1", "2", "3"],"value":[1.0, 2.0, 3.0, 4.0]}} 
3. The values are the same as in BuildConstrains

### Output ###
The set of results for calculating each constraint after substituting resource values into it + the symbolic view of each constraint.
   
The SQLite database is used, the name of which is formed from the input graph file name with the addition suffix '.s3db'.

# Programs launching #

## Python program ##
Python program was run in the PyCharm 2024.1.4 (Community Edition) environment using python 3.12.  
Required modules:
* numpy
* pip-23.2.1.virtualenv

## Java programs ##
Java programs were run in the Apache NetBeans 22 environment using Java 17.  
Required modules:
* gson-2.10.1.jar
* gson-parent-2.10.1.pom
* slf4j-api-1.7.36.jar
* slf4j-nop-1.7.36.jar
* sqlite.jdbc-3.46.0.1.jar

### Launches examples ###
* Main class:	buildconstrains.BuildConstrains
* Arguments:	graph304.csv 1 0
* Working Directory:	D:\Develop0\TestTeX\TransportNet  
or
* bc.cmd

* Main class:	buildconstrains.FullNet
* Arguments:	3 4
* Working Directory:	D:\Develop0\TestTeX\TransportNet

* Main class:	buildconstrains.RandomNets
* Arguments:	4 10
* Working Directory:	D:\Develop0\TestTeX\TransportNet

* Main class:	buildconstrains.CheckResource
* Arguments:	graph300.csv  rsrc00.json 0
* Working Directory:	D:\Develop0\TestTeX\TransportNet
