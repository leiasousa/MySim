/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ons;

/**
 * Returns details about the network traffic: holding time, rate,
 * class of service and weight.
 * 
 * @author andred
 */
public class TrafficInfo {
    
    private int sources;
    private int dataAmount;
    private int delta;
    private int deadline;
    private int weight;
    private int type;
    
    
    
    /**
     * Creates a new TrafficInfo object of Type=1: BulkData.
     * 
     * @param sources size of array of sources's nodes
     * @param dataAmount data transfer amount, measured in GB
     * @param deadline time limit for attending the request to transfer data, in seconds
     * @param weight cost of the network link
     *  @param type type of traffic (BulkData or Batch)
     */
    
    public TrafficInfo(int sources, int dataAmount, int delta, int deadline, int weight, int type){
        this.sources = sources;
        this.dataAmount = dataAmount;
        this.delta = delta;
        this.deadline = deadline;
        this.weight = weight;
        if(type == 1){
            this.type = 1;
        }else if(type == 2){
            this.type = 2;
        }
    }
    /**
     * Creates a new TrafficInfo object of Type=1: BulkData.
     * 
     * 
     * @param dataAmount data transfer amount, measured in GB
     * @param deadline time limit for attending the request to transfer data, in seconds
     * @param weight cost of the network link
     * @param type type of traffic (BulkData or Batch)
     */
    
    public TrafficInfo(int dataAmount, int deadline, int weight, int type) {
        this.dataAmount = dataAmount;
        this.deadline = deadline;
        this.weight = weight;
        //type = 1;
        this.type = type;
    }
    
//    /**
//     * Creates a new TrafficInfo object of Type=2: Batch (constructor overloading).
//     * 
//     * @param sources size of array of sources's nodes
//     * @param dataAmount data transfer amount, measured in GB
//     * @param delta variation in the volume of data transferred
//     * @param deadline time limit for attending the request to transfer data, in seconds
//     * @param weight cost of the network link
//     *  @param type type of traffic (BulkData or Batch)
//     */
//    public TrafficInfo(int sources, int dataAmount,int delta, int deadline, int weight, int type) {
//        this.sources = sources;
//        this.dataAmount = dataAmount;
//        this.delta = delta;
//        this.deadline = deadline;
//        this.weight = weight;
//        type = 2;
//    }
    
    /**
     * Retrieves the sources's nodes.
     * 
     * @return the sources attribute of the TrafficInfo object
     */
    
    int getSources(){
        return sources;
    }
    /**
     * Retrieves the transfer amount of data, in GB, of a call.
     * 
     * @return the data amount attribute of the TrafficInfo object
     */
    int getDataAmount() {
        return dataAmount;
    }
    
    /**
     * Retrieves variation in the volume of data transferred, in GB, of a call.
     * 
     * @return the delta attribute of the TrafficInfo object
     */
    int getDelta() {
        return delta;
    }
    
    /**
     * Retrieves the deadline of the TrafficInfo object.
     * 
     * 
     * @return the deadline attribute of the TrafficInfo object
     */
    double getDeadline() {
        return deadline;
    }
    /**
     * Retrieves the cost of a link on the network.
     * 
     * @return the weight attribute of the TrafficInfo object
     */
    int getWeight() {
        return weight;
    }
    
    /**
     * Retrieves the type of a call.
     * 
     * @return the type attribute of the TrafficInfo object: 1 for BulData or 2 for Batch
     */
    
    int getType(){
        return type;
    }

}
