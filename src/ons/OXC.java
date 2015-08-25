/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ons;

/**
 * The Optical Cross-Connects (OXCs) are present in nodes, for route data
 * traffic and has grooming input and output ports. Traffic grooming 
 * is the process of grouping many small data flows into larger units, 
 * so they can be processed as single units. Grooming in OXCs has the 
 * objective of minimizing the cost of the network.
 * 
 * @author andred
 */
public abstract class OXC {

    protected int id;
    protected int groomingInputPorts;
    protected int groomingOutputPorts;
    protected int freeGroomingInputPorts;
    protected int freeGroomingOutputPorts;
    
    /**
     * Creates a new OXC object. All its attributes must be given
     * given by parameter, except for the free grooming input and output
     * ports, that, at the beginning of the simulation, are the same as 
     * the total number of grooming input and output ports.
     * 
     * @param id the OXC's unique identifier
     * @param groomingInputPorts total number of grooming input ports
     * @param groomingOutputPorts total number of grooming output ports
     */
    public OXC(int id, int groomingInputPorts, int groomingOutputPorts) {
        this.id = id;
        this.groomingInputPorts = this.freeGroomingInputPorts = groomingInputPorts;
        this.groomingOutputPorts = this.freeGroomingOutputPorts = groomingOutputPorts;
    }
    
    /**
     * Retrieves the OXC's unique identifier.
     * 
     * @return the OXC's id attribute
     */
    public int getID() {
        return id;
    }
    
    /**
     * Says whether or not a given OXC has free
     * grooming input port(s).
     * 
     * @return true if the OXC has free grooming input port(s)
     */
    public boolean hasFreeGroomingInputPort() {
        return freeGroomingInputPorts > 0;
    }
    
    /**
     * By decreasing the number of free grooming input ports,
     * this function "reserves" a grooming input port.
     * 
     * @return false if there are no free grooming input ports
     */
    public boolean reserveGroomingInputPort() {
        if (freeGroomingInputPorts > 0) {
            freeGroomingInputPorts--;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * By increasing the number of free grooming input ports,
     * this function "releases" a grooming input port.
     * 
     * @return false if there are no grooming input ports to be freed
     */
    public boolean releaseGroomingInputPort() {
        if (freeGroomingInputPorts < groomingInputPorts) {
            freeGroomingInputPorts++;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * This function says whether or not a given OXC has free
     * grooming output port(s).
     * 
     * @return true if the OXC has free grooming output port(s)
     */
    public boolean hasFreeGroomingOutputPort() {
        return freeGroomingOutputPorts > 0;
    }
    
    /**
     * By decreasing the number of free grooming output ports,
     * this function "reserves" a grooming output port.
     * 
     * @return false if there are no free grooming output ports
     */
    public boolean reserveGroomingOutputPort() {
        if (freeGroomingOutputPorts > 0) {
            freeGroomingOutputPorts--;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * By increasing the number of free grooming output ports,
     * this function "releases" a grooming output port.
     * 
     * @return false if there are no grooming output ports to be freed
     */
    public boolean releaseGroomingOutputPort() {
        if (freeGroomingOutputPorts < groomingOutputPorts) {
            freeGroomingOutputPorts++;
            return true;
        } else {
            return false;
        }
    }
    
}
