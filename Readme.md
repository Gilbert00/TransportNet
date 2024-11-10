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
