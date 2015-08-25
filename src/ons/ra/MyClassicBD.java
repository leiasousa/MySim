/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ons.ra;

import ons.Batch;
import ons.BulkData;
import ons.EONLightPath;
import ons.EONLink;
import ons.Flow;
import ons.LightPath;
import ons.util.Dijkstra;
import ons.util.WeightedGraph;

/**
 * This is a sample algorithm for the Routing and Spectrum Assignment problem to Bulk Data Transfer.
 *
 * Fixed path routing is the simplest approach to finding a lightpath. The same
 * fixed route for a given source and destination pair is always used. This path
 * is computed using Dijkstra's Algorithm.
 *
 * First-Fit wavelength assignment tries to establish the lightpath using the
 * first spectrum available sought in the increasing order.
 */
public class MyClassicBD implements RA {
    
    private ControlPlaneForRA cp;
    private WeightedGraph graph;
    private BulkData bulk;
    private Batch batch;
    private AdapterToClassic adapter = new AdapterToClassic();
    private boolean blocked, accept;

   
    
    
    public void simulationInterface(ControlPlaneForRA cp) {
        this.cp = cp;
        this.graph = cp.getPT().getWeightedGraph();
    }
    
    

    //@Override
    public void batchArrival(Batch batch) {
        int size = batch.getSize();
        this.batch = batch;
        for(int indice = 0; indice < size; indice++){
            adapter.bulkDataArrival(this.batch.getBulkData());
        }
    }
    
    public BulkData getBulkData(){
        return batch.getBulkData();
    }
    

    
    public void bulkDataArrival(BulkData bulkData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flowArrival(Flow flow) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flowDeparture(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

         
}
