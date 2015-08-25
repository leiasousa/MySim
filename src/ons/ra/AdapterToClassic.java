/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ons.ra;

import java.util.ArrayList;
import ons.Batch;
import ons.BulkData;
import ons.EONLightPath;
import ons.EONLink;
import ons.Flow;
import ons.LightPath;
import ons.util.Dijkstra;
import ons.util.WeightedGraph;

/**
 * This class adjusts requests Batch to be interpreted by Classics Algorithms of
 * literature.
 *
 * The requests Batch are received and discretized in requests BulkData. Then,
 * the Classic Algorithm for Bulk Data Transfer is invoked and it gets these
 * requests BulkData.
 *
 */
public class AdapterToClassic{

    private MyClassicBD classic;
    //private Batch batch;
    private BulkData bulk;
    private ArrayList<BulkData> bd = new ArrayList();
    private ControlPlaneForRA cp;
    private WeightedGraph graph;

    
    public void bulkDataArrival(BulkData bulk) {
        this.bulk = bulk;
        int[] nodes;
        int[] links;
        long id;
        LightPath[] lps = new LightPath[1];

        nodes = Dijkstra.getShortestPath(graph, this.bulk.getSource(), this.bulk.getDestination());
        System.out.println(nodes);//test
        if (nodes.length == 0) {
            cp.blockBulkData(this.bulk.getID());
            return;
        }

        // Create the links vector
        links = new int[nodes.length - 1];
        for (int j = 0; j < nodes.length - 1; j++) {
            links[j] = cp.getPT().getLink(nodes[j], nodes[j + 1]).getID();
        }

        // First-Fit spectrum assignment in BPSK Modulation
        double rate = this.bulk.getDataAmount()/this.bulk.getDeadline();
        double requiredSlots = convertRateToSlotBPSK((int) rate, ((EONLink) cp.getPT().getLink(links[0])).getSlotSize());
        for (int i = 0; i < links.length; i++) {
            if (!((EONLink) cp.getPT().getLink(links[i])).hasSlotsAvaiable(requiredSlots)) {
                cp.blockBulkData(this.bulk.getID());
                //classic.blocked(true);
                return;
            }
            System.out.println("Required Slots " + requiredSlots);
        }
        int[] firstSlot;
        for (int i = 0; i < links.length; i++) {
            // Try the slots available in each link
            firstSlot = ((EONLink) cp.getPT().getLink(links[i])).getSlotsAvailable((int) requiredSlots);
            for (int j = 0; j < firstSlot.length; j++) {
                // Now you create the lightpath to use the createLightpath VT
                //Relative index modulation: BPSK = 0; QPSK = 1; 8QAM = 2; 16QAM = 3;
                EONLightPath lp = new EONLightPath(1, this.bulk.getSource(), this.bulk.getDestination(), links,
                        firstSlot[j], (int) (firstSlot[j] + requiredSlots - 1), 0, ((EONLink) cp.getPT().getLink(links[i])).getSlotSize());
                // Now you try to establish the new lightpath, accept the call
                if ((id = cp.getVT().createLightpath(lp)) >= 0) {
                    // Single-hop routing (end-to-end lightpath)
                    lps[0] = cp.getVT().getLightpath(id);
                    cp.acceptBulkData(this.bulk.getID(), lps);
                    //classic.accept(true);
                    return;
                }
            }
            //classic.bulkDataArrival(bdArray[size]);

        }
    }

//    public BulkData getBulk() {
//        return this.batch.getBulkData();
//    }

    /**
     *
     * @param rate
     * @param slotSize
     * @return
     */
    private int convertRateToSlotBPSK(int rate, int slotSize) {
        int slots;
        slots = (int) Math.ceil(((double) rate) / ((double) slotSize));
        return slots;
    }

    public void simulationInterface(ControlPlaneForRA cp) {
        this.cp = cp;
        this.graph = cp.getPT().getWeightedGraph();
    }

    //@Override
    public void flowArrival(Flow flow) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //@Override
    public void batchArrival(Batch batch) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //@Override
    public void flowDeparture(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
