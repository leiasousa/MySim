/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ons;

import ons.ra.RA;
import ons.ra.ControlPlaneForRA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The Control Plane is responsible for managing resources and connection within
 * the network.
 */
public class ControlPlane implements ControlPlaneForRA { // RA is Routing Assignment Problem

    private RA ra;
    private PhysicalTopology pt;
    private VirtualTopology vt;
    private Map<Flow, Path> mappedFlows; // Flows that have been accepted into the network
    private Map<Long, Flow> activeFlows; // Flows that have been accepted or that are waiting for a decision 
    private Map<BulkData, Path> mappedBulks;
    private Map<Long, BulkData> activeBulks;
    private Tracer tr = Tracer.getTracerObject();
    private MyStatistics st = MyStatistics.getMyStatisticsObject();

    /**
     * Creates a new ControlPlane object.
     *
     * @param raModule the name of the RA class
     * @param pt the network's physical topology
     * @param vt the network's virtual topology
     */
    public ControlPlane(String raModule, PhysicalTopology pt, VirtualTopology vt) {
        Class RAClass;
        //String RAClassToString = RAClass.toString();
        

        mappedFlows = new HashMap<Flow, Path>();
        activeFlows = new HashMap<Long, Flow>();
        //mappedBulks = new HashMap<BulkData, Path>();
        activeBulks = new HashMap<Long, BulkData>();

        this.pt = pt;
        this.vt = vt;

        try {
            RAClass = Class.forName(raModule);
            ra = (RA) RAClass.newInstance();
            ra.simulationInterface(this);
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Erro cp: Algum problema com instancias da interface RA");//Test
        }

    }

    /**
     * Deals with an Event from the event queue. If it is of the
     * FlowArrivalEvent kind, adds it to the list of active flows. If it is from
     * the FlowDepartureEvent, removes it from the list.
     *
     * @param event the Event object taken from the queue
     */
    public void newEvent(Event event) {

        if (event instanceof FlowArrivalEvent) {
            newFlow(((FlowArrivalEvent) event).getFlow());
            ra.flowArrival(((FlowArrivalEvent) event).getFlow());
            System.out.println("RA recebe novo fluxo: evento de chegada");//test
        } else if (event instanceof FlowDepartureEvent) {
            ra.flowDeparture(((FlowDepartureEvent) event).getID());

        } else if (event instanceof BulkDataArrivalEvent) {
            newBulkData(((BulkDataArrivalEvent) event).getBulkData());
            ra.bulkDataArrival(((BulkDataArrivalEvent) event).getBulkData());
            System.out.println("RA recebe novo bulk: evento de chegada");//test
        } else if (event instanceof BatchArrivalEvent) {
            newBatch(((BatchArrivalEvent) event).getBatch());
            ra.batchArrival(((BatchArrivalEvent) event).getBatch());
//            int size = ((BatchArrivalEvent) event).getBatchSize();
//            for (int i = 0; i < size; i++) {
//                ra.bulkDataArrival(((BulkDataArrivalEvent) event).getBulkData());
//                if (event instanceof FlowDepartureEvent) {
//                    ra.flowDeparture(((FlowDepartureEvent) event).getID());
//                    System.out.println("instancia de FlowDeparture");
//
//                }
//            }
            //boolean VerifyQoSBatch = VerifyQoSBatch(((BatchArrivalEvent) event).getBatchArrivalEvent());
        }

    }

    /**
     * Adds a given active Flow object to a determined Physical Topology.
     *
     * @param id unique identifier of the Flow object
     * @param lightpaths the Path, or list of LighPath objects
     * @return true if operation was successful, or false if a problem occurred
     */
    public boolean acceptFlow(long id, LightPath[] lightpaths) {
        Flow flow;

        if (id < 0 || lightpaths.length < 1) {
            throw (new IllegalArgumentException());
        } else {
            if (!activeFlows.containsKey(id)) {
                return false;
            }
            flow = activeFlows.get(id);
            if (!canAddFlowToPT(flow, lightpaths)) {
                return false;
            }
            addFlowToPT(flow, lightpaths);
            mappedFlows.put(flow, new Path(lightpaths));
            tr.acceptFlow(flow, lightpaths);
            st.acceptFlow(flow, lightpaths);

            return true;
        }
    }

    public boolean acceptBulkData(long id, LightPath[] lightpaths) {
        BulkData bulkData;

        if (id < 0 || lightpaths.length < 1) {
            throw (new IllegalArgumentException());
        } else {
            if (!activeBulks.containsKey(id)) {
                return false;
            }
            bulkData = activeBulks.get(id);
            if (!canAddBulkDataToPT(bulkData, lightpaths)) {
                return false;
            }
            addBulkDataToPT(bulkData, lightpaths);
            mappedBulks.put(bulkData, new Path(lightpaths));
            tr.acceptBulkData(bulkData, lightpaths);
            st.acceptBulkData(bulkData, lightpaths);

            return true;
        }
    }

    /**
     * Removes a given Flow object from the list of active flows.
     *
     * @param id unique identifier of the Flow object
     * @return true if operation was successful, or false if a problem occurred
     */
    public boolean blockFlow(long id) {
        Flow flow;

        if (id < 0) {
            throw (new IllegalArgumentException());
        } else {
            if (!activeFlows.containsKey(id)) {
                return false;
            }
            flow = activeFlows.get(id);
            if (mappedFlows.containsKey(flow)) {
                return false;
            }
            activeFlows.remove(id);
            tr.blockFlow(flow);
            st.blockFlow(flow);
            return true;
        }
    }

    public boolean blockBulkData(long id) {
        BulkData bulkData;
        if (id < 0) {
            throw (new IllegalArgumentException());
        } else {
            if (!activeBulks.containsKey(id)) {
                return false;
            }
            bulkData = activeBulks.get(id);
            if (mappedBulks.containsKey(bulkData)) {
                return false;
            }
            activeBulks.remove(id);
            tr.blockBulkData(bulkData);
            st.blockBulkData(bulkData);
            return true;
        }
    }

    /**
     * Removes a given Flow object from the Physical Topology and then puts it
     * back, but with a new route (set of LightPath objects).
     *
     * @param id unique identifier of the Flow object
     * @param lightpaths list of LightPath objects, which form a Path
     * @return true if operation was successful, or false if a problem occurred
     */
    // @Override
    public boolean rerouteFlow(long id, LightPath[] lightpaths) {
        Flow flow;
        Path oldPath;

        if (id < 0 || lightpaths.length < 1) {
            throw (new IllegalArgumentException());
        } else {
            if (!activeFlows.containsKey(id)) {
                return false;
            }
            flow = activeFlows.get(id);
            if (!mappedFlows.containsKey(flow)) {
                return false;
            }
            oldPath = mappedFlows.get(flow);
            removeFlowFromPT(flow, lightpaths);
            if (!canAddFlowToPT(flow, lightpaths)) {
                addFlowToPT(flow, oldPath.getLightpaths());
                return false;
            }
            addFlowToPT(flow, lightpaths);
            mappedFlows.put(flow, new Path(lightpaths));
            //tr.flowRequest(id, true);
            return true;
        }
    }

    /**
     * Adds a given Flow object to the HashMap of active flows. The HashMap also
     * stores the object's unique identifier (ID).
     *
     * @param flow Flow object to be added
     */
    private void newFlow(Flow flow) {
        activeFlows.put(flow.getID(), flow);
    }

    private void newBulkData(BulkData bulkData) {
        activeBulks.put(bulkData.getID(), bulkData);
    }

    /**
     * Adds a given Batch object to the HashMap of active Batches. The HashMap
     * stores the Batch object's unique identifier (ID) and the respective set
     * of Flows.
     *
     * @param batch
     */
    private void newBatch(Batch batch) {
        //PriorityQueue<BulkData> bulkData = batch.getBulkData();
        int size = batch.getSize();
        for (int i = 0; i < size; i++) {
            activeBulks.put(batch.getBulkData().getID(), batch.getBulkData());
        }
    }

    /**
     * Removes a given Flow object from the list of active flows.
     *
     * @param id the unique identifier of the Flow to be removed
     */
    private void removeFlow(long id) {
        Flow flow;
        LightPath[] lightpaths;

        if (activeFlows.containsKey(id)) {
            flow = activeFlows.get(id);
            if (mappedFlows.containsKey(flow)) {
                lightpaths = mappedFlows.get(flow).getLightpaths();
                removeFlowFromPT(flow, lightpaths);
                mappedFlows.remove(flow);
            }
            activeFlows.remove(id);
        }
    }

    /**
     * Removes a given Flow object from a Physical Topology.
     *
     * @param flow the Flow object that will be removed from the PT
     * @param lightpaths a list of LighPath objects
     */
    private void removeFlowFromPT(Flow flow, LightPath[] lightpaths) {
        for (LightPath lightpath : lightpaths) {
            pt.removeFlow(flow, lightpath);
            // Lightpath pode ser removido?
            if (vt.isLightpathIdle(lightpath.getID())) {
                vt.removeLightPath(lightpath.getID());
            }
        }
    }

    /**
     * Says whether or not a given Flow object can be added to a determined
     * Physical Topology, based on the amount of bandwidth the flow requires
     * opposed to the available bandwidth.
     *
     * @param flow the Flow object to be added
     * @param lightpaths list of LightPath objects the flow uses
     * @return true if Flow object can be added to the PT, or false if it can't
     */
    private boolean canAddFlowToPT(Flow flow, LightPath[] lightpaths) {
        for (LightPath lightpath : lightpaths) {
            if (!pt.canAddFlow(flow, lightpath)) {
                return false;
            }
        }
        return true;
    }

    private boolean canAddBulkDataToPT(BulkData bulkData, LightPath[] lightpaths) {
        for (LightPath lightpath : lightpaths) {
            if (!pt.canAddBulkData(bulkData, lightpath)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a Flow object to a Physical Topology. This means adding the flow to
     * the network's traffic, which simply decreases the available bandwidth.
     *
     * @param flow the Flow object to be added
     * @param lightpaths list of LightPath objects the flow uses
     */
    private void addFlowToPT(Flow flow, LightPath[] lightpaths) {
        for (LightPath lightpath : lightpaths) {
            pt.addFlow(flow, lightpath);
        }
    }

    private void addBulkDataToPT(BulkData bulkData, LightPath[] lightpaths) {
        for (LightPath lightpath : lightpaths) {
            pt.addBulkData(bulkData, lightpath);
        }
    }

    /**
     * Retrieves a Path object, based on a given Flow object. That's possible
     * thanks to the HashMap mappedFlows, which maps a Flow to a Path.
     *
     * @param flow Flow object that will be used to find the Path object
     * @return Path object mapped to the given flow
     */
    public Path getPath(Flow flow) {
        return mappedFlows.get(flow);
    }
    
    public Path getPath(BulkData bulkdata){
        return mappedBulks.get(bulkdata);
    }

    /**
     * Retrieves the complete set of Flow/Path pairs listed on the mappedFlows
     * HashMap.
     *
     * @return the mappedFlows HashMap
     */
    public Map<Flow, Path> getMappedFlows() {
        return mappedFlows;
    }
    
    public Map<BulkData, Path> getMappedBulks(){
        return mappedBulks;
    }

    /**
     * Retrieves a Flow object from the list of active flows.
     *
     * @param id the unique identifier of the Flow object
     * @return the required Flow object
     */
    @Override
    public Flow getFlow(long id) {
        return activeFlows.get(id);
    }
    
    public BulkData getBulkData(long id){
        return activeBulks.get(id);
    }

    /**
     * Counts number of times a given LightPath object is used within the Flow
     * objects of the network.
     *
     * @param id unique identifier of the LightPath object
     * @return integer with the number of times the given LightPath object is
     * used
     */
    @Override
    public int getLightpathFlowCount(long id) {
        int num = 0;
        Path p;
        LightPath[] lps;
        ArrayList<Path> ps = new ArrayList<>(mappedFlows.values());
        for (Path p1 : ps) {
            p = p1;
            lps = p.getLightpaths();
            for (LightPath lp : lps) {
                if (lp.getID() == id) {
                    num++;
                    break;
                }
            }
        }
        return num;
    }
    //////////////////////////////////////////////////////////

    @Override
    public PhysicalTopology getPT() {
        return pt;
    }

    @Override
    public VirtualTopology getVT() {
        return vt;
    }

    public EONLightPath createCandidateEONLightPath(int src, int dst, int[] links, int firstSlot, int lastSlot, int modulation) {
        return new EONLightPath(1, src, dst, links, firstSlot, lastSlot, modulation, ((EONPhysicalTopology) pt).getSlotSize());
    }

//    public boolean VerifyQoSBatch(Batch batch) {
//        long[] arrayIds = batch.getArrayIds();
//        int cont = 0;
//        for (int i = 0; i < arrayIds.length; i++) {
//            if (mappedFlows.containsKey(i)) {
//                cont++;
//            }
//        }
//        if (cont == 3 || cont > 3) {
//            st.acceptBatch(batch);
//            return true;
//        }
//
//        return false;
//    }

}
