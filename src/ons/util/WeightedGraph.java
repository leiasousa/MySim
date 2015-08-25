/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ons.util;

/**
 * A weighted graph associates a label (weight) with every edge in the graph.
 * If a pair of nodes has weight equal to zero, it means the edge between them
 * doesn't exist.
 * 
 * @author andred
 */
public class WeightedGraph {

    private int numNodes;
    private double[][] edges;  // adjacency matrix
    
    /**
     * Creates a new WeightedGraph object with no edges,
     * 
     * @param n number of nodes the new graph will have
     */
    public WeightedGraph(int n) {
        edges = new double[n][n];
        numNodes = n;
    }
    
    /**
     * Creates a new WeightedGraph object, based on an already existing
     * weighted graph.
     * 
     * @param g the graph that will be copied into the new one
     */
    public WeightedGraph(WeightedGraph g) {
        numNodes = g.numNodes;
        edges = new double[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                edges[i][j] = g.getWeight(i, j);
            }
        }
    }
    
    /**
     * Retrieves the size of the graph, i.e., the amount of vertexes it has.
     * 
     * @return integer with the quantity of nodes in the graph
     */
    public int size() {
        return numNodes;
    }
    
    /**
     * Creates a new edge within the graph, which requires its two vertexes
     * and its weight.
     * 
     * @param source the edge's source node 
     * @param target the edge's destination node
     * @param w the value of the edge's weight
     */
    public void addEdge(int source, int target, double w) {
        edges[source][target] = w;
    }
    
    /**
     * Says whether or not a given pair of nodes has an edge between them.
     * 
     * @param source the source node
     * @param target the destination node
     * @return true if the edge exists, or false otherwise
     */
    public boolean isEdge(int source, int target) {
        return edges[source][target] > 0;
    }
    
    /**
     * Removes a given edge from the graph by simply attributing
     * zero to its source and target coordinates within the matrix of edges.
     * 
     * @param source the edge's source node
     * @param target the edge's destination node
     */
    public void removeEdge(int source, int target) {
        edges[source][target] = 0;
    }
    
    /**
     * Retrieves the weight of a given edge on the graph.
     * 
     * @param source the edge's source node
     * @param target the edge's destination node
     * @return the value of the edge's weight
     */
    public double getWeight(int source, int target) {
        return edges[source][target];
    }
    
    /**
     * Sets a determined weight to a given edge on the graph.
     * 
     * @param source the edge's source node
     * @param target the edge's destination node
     * @param w the value of the weight
     */
    public void setWeight(int source, int target, double w) {
        edges[source][target] = w;
    }
    
    /**
     * Retrieves the neighbors of a given vertex. 
     * 
     * @param vertex index of the vertex within the matrix of edges
     * @return list with indexes of the vertex's neighbors
     */
    public int[] neighbors(int vertex) {
        int count = 0;
        for (int i = 0; i < edges[vertex].length; i++) {
            if (edges[vertex][i] > 0) {
                count++;
            }
        }
        final int[] answer = new int[count];
        count = 0;
        for (int i = 0; i < edges[vertex].length; i++) {
            if (edges[vertex][i] > 0) {
                answer[count++] = i;
            }
        }
        return answer;
    }
    
    /**
     * Prints all information related to the weighted graph.
     * For each vertex, shows the vertexes is is adjacent to and the
     * weight of each edge.
     * 
     * @return string containing the edges of each vertex
     */
    @Override
    public String toString() {
        String s = "";
        for (int j = 0; j < edges.length; j++) {
            s += Integer.toString(j) + ": ";
            for (int i = 0; i < edges[j].length; i++) {
                if (edges[j][i] > 0) {
                    s += Integer.toString(i) + ":" + Double.toString(edges[j][i]) + " ";
                }
            }
            s += "\n";
        }
        return s;
    }
    
    /**
     * Remode all edges node
     * 
     * @param node the node
     */
    public void removeNodeEdge(int node) {
        //removing edges from this node
        for(int i = 0; i < numNodes; i++){
            if(isEdge(node, i)){
                removeEdge(node, i);
            }
            if(isEdge(i, node)){
                removeEdge(i, node);
            }
        }
    }
    
    /**
     * Remove node in graph
     * 
     * @param node the node 
     */
    public void removeNode(int node) {
        //removing edges from this node
        removeNodeEdge(node);
        //remove node from the graph
        double[][] newedges = new double[numNodes-1][numNodes-1];
        int k = 0, l = 0;
        for(int i = 0; i< numNodes; i++){
            if(i != node){
                l = 0;
                for(int j = 0; j< numNodes; j++){
                    if(j != node){
                        newedges[k][l] = edges[i][j];
                        l++;
                    }
                }
                k++;   
            }
        }
        numNodes--;
        edges = newedges;
    }
    
    /**
     * Creates a new node in graph.
     */
    public void addNode(){
        double[][] newedges = new double[numNodes+1][numNodes+1];
        for(int i = 0; i < numNodes; i++){
            System.arraycopy(edges[i], 0, newedges[i], 0, numNodes);
        }
        numNodes++;
        edges = newedges;
    }
}
