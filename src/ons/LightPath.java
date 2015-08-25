/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ons;

/**
 * In an optical network, a lightpath is a clear optical path which may traverse
 * several links in the network.
 * It is also good to know that information transmitted through lightpaths does not
 * undergo any conversion to or from electrical form.
 * 
 * @author andred
 */
public abstract class LightPath {

    protected long id;
    protected int src;
    protected int dst;
    protected int[] links;

    /**
     * Creates a new LightPath object.
     * 
     * @param id            unique identifier
     * @param src           source node
     * @param dst           destination node
     * @param links         fyberlinks list composing the path
     * @param wavelengths    wavelengths list used in fiberlinks
     */
    public LightPath(long id, int src, int dst, int[] links) {
        if (id < 0 || src < 0 || dst < 0 || links.length < 1) {
            throw (new IllegalArgumentException());
        } else {
            this.id = id;
            this.src = src;
            this.dst = dst;
            this.links = links;
        }
    }
    
    /**
     * Retrieves the unique identifier of a given LightPath.
     * 
     * @return the LightPath's id attribute
     */
    public long getID() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Retrieves the source node of a given LightPath.
     * 
     * @return the LightPath's src attribute
     */
    public int getSource() {
        return src;
    }
    
    /**
     * Retrieves the destination node of a given LightPath.
     * 
     * @return the LightPath's dst attribute.
     */
    public int getDestination() {
        return dst;
    }
    
    /**
     * Retrieves the LightPath's vector containing the identifier numbers
     * of the links that compose the path.
     * 
     * @return a vector of integers that represent fiberlinks identifiers
     */
    public int[] getLinks() {
        return links;
    }
    
    /**
     * The fiber links are physical hops. Therefore, by retrieving the number
     * of elements in a LightPath's list of fiber links, we get the number of
     * hops the LightPath has.
     * 
     * @return the number of hops in a given LightPath
     */
    public int getHops() {
        return links.length;
    }
    
    /**
     * Prints all information related to a given LightPath, starting with
     * its ID, to make it easier to identify.
     * 
     * @return string containing all the values of the LightPath's parameters
     */
    @Override
    public String toString() {
        String lightpath = Long.toString(id) + " " + Integer.toString(src) + " " + Integer.toString(dst);
        return lightpath;
    }
    
    public String toTrace() {
        String lightpath = Long.toString(id) + " " + Integer.toString(src) + " " + Integer.toString(dst);
        return lightpath;
    }
}
