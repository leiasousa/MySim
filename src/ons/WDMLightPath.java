/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ons;

/**
 *
 * @author lucasrc
 */
public class WDMLightPath extends LightPath{
    
    private int[] wavelengths;
    
    public WDMLightPath(long id, int src, int dst, int[] links, int[] wavelengths){
        super(id, src, dst, links);
        if (wavelengths.length != links.length) {
            throw (new IllegalArgumentException());
        } else {
            this.wavelengths = wavelengths;
        }
    }

    /**
     * Retrieves the LightPath's vector containing the wavelengths that the
     * fiber links in the path use.
     *
     * @return a vector of integer that represent wavelengths
     */
    public int[] getWavelengths() {
        return wavelengths;
    }
    
    /**
     * Prints all information related to a given LightPath, starting with
     * its ID, to make it easier to identify.
     * 
     * @return string containing all the values of the LightPath's parameters
     */
    @Override
    public String toString() {
        String lightpath = Long.toString(id) + " " + Integer.toString(src) + " " + Integer.toString(dst) + " ";
        for (int i = 0; i < links.length; i++) {
            lightpath += Integer.toString(links[i]) + " (" + Integer.toString(wavelengths[i]) + ") ";
        }
        return lightpath;
    }
    
    @Override
    public String toTrace() {
        String lightpath = Long.toString(id) + " " + Integer.toString(src) + " " + Integer.toString(dst) + " ";
        for (int i = 0; i < links.length; i++) {
            lightpath += Integer.toString(links[i]) + "_" + Integer.toString(wavelengths[i]) + " ";
        }
        return lightpath;
    }
}
