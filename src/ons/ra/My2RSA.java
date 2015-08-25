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
 * This is a sample algorithm for the Routing and Wavelength Assignment problem.
 *
 * Fixed path routing is the simplest approach to finding a lightpath. The same
 * fixed route for a given source and destination pair is always used. This path
 * is computed using Dijkstra's Algorithm.
 *
 * First-Fit wavelength assignment tries to establish the lightpath using the
 * first wavelength available sought in the increasing order.
 */
public class My2RSA implements RA {
    
    private ControlPlaneForRA cp;
    private WeightedGraph graph;

    @Override
    public void simulationInterface(ControlPlaneForRA cp) {
        this.cp = cp;
        this.graph = cp.getPT().getWeightedGraph();
    }

    @Override
    public void flowArrival(Flow flow) {
        //((EONLink) cp.getPT().getLink(6)).printLink();
        int[] nodes;
        int[] links;
        long id;
        LightPath[] lps = new LightPath[1];

        // Shortest-Path routing
        nodes = Dijkstra.getShortestPath(graph, flow.getSource(), flow.getDestination());

        // If no possible path found, block the call
        if (nodes.length == 0) {
            cp.blockFlow(flow.getID()); 
            return;
        }

        // Create the links vector
        links = new int[nodes.length - 1];
        for (int j = 0; j < nodes.length - 1; j++) {
            links[j] = cp.getPT().getLink(nodes[j], nodes[j + 1]).getID();
        }

        // First-Fit spectrum assignment in BPSK Modulation
        int requiredSlots = convertRateToSlotBPSK(flow.getRate(), ((EONLink) cp.getPT().getLink(links[0])).getSlotSize());
        for (int i = 0; i < links.length; i++){
            if (!((EONLink) cp.getPT().getLink(links[i])).hasSlotsAvaiable(requiredSlots)){
                cp.blockFlow(flow.getID()); 
                return;
            }
        }
        int[] firstSlot;
        for (int i = 0; i < links.length; i++) {
            // Try the slots available in each link
            firstSlot = ((EONLink) cp.getPT().getLink(links[i])).getSlotsAvailable(requiredSlots);
            for (int j = 0; j < firstSlot.length; j++) {
                // Now you create the lightpath to use the createLightpath VT
                //Relative index modulation: BPSK = 0; QPSK = 1; 8QAM = 2; 16QAM = 3;
                EONLightPath lp = new EONLightPath(1, flow.getSource(), flow.getDestination(), links,
                        firstSlot[j], (firstSlot[j] + requiredSlots - 1), 0, ((EONLink) cp.getPT().getLink(links[i])).getSlotSize());
                // Now you try to establish the new lightpath, accept the call
                if ((id = cp.getVT().createLightpath(lp)) >= 0) {
                    // Single-hop routing (end-to-end lightpath)
                    lps[0] = cp.getVT().getLightpath(id);
                    cp.acceptFlow(flow.getID(), lps);
                    return;
                }
            }
        }
        // Block the call
        cp.blockFlow(flow.getID());
    }

    @Override
    public void flowDeparture(long id) {
    }

    private int convertRateToSlotBPSK(int rate, int slotSize) {
        int slots;
        slots = (int) Math.ceil(((double) rate)/((double)slotSize));
        return slots;
    }
    
    private int convertRateToSlotQPSK(int rate, int slotSize) {
        int slots;
        slots = (int) Math.ceil(((double) rate)/(((double)slotSize) * 2.0));
        return slots;
    }
    private int convertRateToSlot8QAM(int rate, int slotSize) {
        int slots;
        slots = (int) Math.ceil(((double) rate)/(((double)slotSize) * 3.0));
        return slots;
    }
    private int convertRateToSlot16QAM(int rate, int slotSize) {
        int slots;
        slots = (int) Math.ceil(((double) rate)/(((double)slotSize) * 4.0));
        return slots;
    }

    @Override
    public void bulkDataArrival(BulkData bulkData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void batchArrival(Batch batch) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
