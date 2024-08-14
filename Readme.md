# BuildConstrains.py #
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

### There are two program versions.

* First version by procedures is in branch **main**.
* Second version by classes is in branch **oop**.
