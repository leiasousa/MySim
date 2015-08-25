/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ons.ra;

import java.util.ArrayList;
import ons.EONLightPath;
import ons.LightPath;
import ons.Flow;
import ons.Path;
import ons.PhysicalTopology;
import ons.VirtualTopology;
import java.util.Map;
import ons.Batch;
import ons.BulkData;

/**
 * This is the interface that provides several methods for the
 * RWA Class within the Control Plane.
 * 
 * @author andred
 */
public interface ControlPlaneForRA {

    public boolean acceptFlow(long id, LightPath[] lightpaths);
    
    public boolean acceptBulkData(long id, LightPath[] lightpaths);

    public boolean blockFlow(long id);
    
    public boolean blockBulkData(long id);

    public boolean rerouteFlow(long id, LightPath[] lightpaths);
    
    public Flow getFlow(long id);
    
    public Path getPath(Flow flow);
    
    public int getLightpathFlowCount(long id);

    public Map<Flow, Path> getMappedFlows();
    
    public Map<BulkData, Path> getMappedBulks();

    public PhysicalTopology getPT();
    
    public VirtualTopology getVT();
    
    public EONLightPath createCandidateEONLightPath(int src, int dst, int[] links, int firstSlot, int lastSlot, int modulation);
}
