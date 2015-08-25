package ons;

/**
 * 
 * The BulkData class defines an object that can be thought of as a data amount transfer request
 * ,going from a source node to a destination node within a specific deadline. 
 * @author Leia Sousa 
 */

public class BulkData{
    private long id;
    private int src;
    private int dst;
    private int dataAmount;
    private double deadline;
    private int cos;
    final int type = 1;
     

    /**
     * Creates a new request.
     * 
     * @param id            unique identifier
     * @param src           source node
     * @param dst           destination node
     * @param dataAmount     quantity of bulk data
     * @param deadline       maximum tolerable transfer time (seconds)
     * 
     */
    
        
       
    public BulkData(long id, int src, int dst, int dataAmount, double deadline, int cos) {
        if (id < 0 || src < 0 || dst < 0 || dataAmount < 1 || deadline < 0 ) {
            throw (new IllegalArgumentException());
        } else {
            this.id = id;
            this.src = src;
            this.dst = dst;
            this.dataAmount = dataAmount;
            this.deadline = deadline;
            this.cos = cos;
            //this.type = type;
        }
    }

            
    /**
     * Retrieves the unique identifier for a given BulkDataUnicast request.
     * 
     * @return the value of the request's id attribute
     */
    public long getID() {
        return id;
    }
    
    public void setID(long id) {
        this.id = id;
    }
    
    /**
     * Retrieves the source node for a given BulkDataUnicast request.
     * 
     * @return the value of the request's src attribute
     */
    public int getSource() {
        return src;
    }
    
    /**
     * Retrieves the destination node for a given BulkDataUnicast request.
     * 
     * @return the value of the Flow's dst attribute
     */
    public int getDestination() {
        return dst;
    }
    /**
     * Retrieves the data amount to be transferred for a given BulkDataUnicast request.
     * 
     * @return the quantity of bulk data
     */
    public int getDataAmount() {
        return dataAmount;
    }
    
    /**
     * Assigns a new value to the required quantity of bulk data of a given BulkDataUnicast request.
     * 
     * @param setDataAmount new required quantity of bulk data 
     */
    
    public void setDataAmount(int dataAmount){
        this.dataAmount = dataAmount; 
    }
    
    /**
     * Retrieves the duration time to make the transfer, in seconds, of a given BulkDataUnicast request.
     *
     * @return the value of the BulkDataUnicast's duration attribute
     */
    public double getDeadline() {
        return deadline;
    }
    
    public int getCos(){
        return cos;
    }
    
    
    /**
     * Prints all information related to the arriving BulkDataUnicast request.
     * 
     * @return string containing all the values of the parameters of all requests
     */
    
    public String toString(){
        String bulkDataUnicast = Long.toString(id) + ": " + Integer.toString(src) + "->" + Integer.toString(dst) + " Bulk Data Amount: " + Integer.toString(dataAmount) + " Deadline: " + Double.toString(deadline);
        return bulkDataUnicast;
    }
    
    /**
     * Creates a string with relevant information about the BulkDataUnicast request, to be
     * printed on the Trace file.
     * 
     * @return string with values of the parameters of BulkDataUnicast request
     */
    
    public String toTrace(){
    	String trace = Long.toString(id) + " " + Integer.toString(src) + " " + Integer.toString(dst) + " " + Integer.toString(dataAmount) + " " + Double.toString(deadline);
    	return trace;
    }

     
     
}