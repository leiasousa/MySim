package ons;

import ons.util.WeightedGraph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.w3c.dom.*;

/**
 * The virtual topology is created based on a given Physical Topology and on the
 * lightpaths specified on the XML file.
 *
 * @author andred
 */
public class VirtualTopology {

    protected long nextLightpathID = 0;
    protected TreeSet<LightPath>[][] adjMatrix;
    protected int adjMatrixSize;
    protected Map<Long, LightPath> lightPaths;
    protected PhysicalTopology pt;
    protected Tracer tr = Tracer.getTracerObject();
    protected MyStatistics st = MyStatistics.getMyStatisticsObject();

    private static class LightPathSort implements Comparator<LightPath> {

        @Override
        public int compare(LightPath lp1, LightPath lp2) {
            if (lp1.getID() < lp2.getID()) {
                return -1;
            }
            if (lp1.getID() > lp2.getID()) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * Creates a new VirtualTopology object.
     *
     * @param xml file that contains all simulation information
     * @param pt Physical Topology of the network
     */
    @SuppressWarnings("unchecked")
    public VirtualTopology(Element xml, PhysicalTopology pt) {
        int nodes, lightpaths;

        lightPaths = new HashMap<Long, LightPath>();

        try {
            this.pt = pt;
            if (Simulator.verbose) {
                System.out.println(xml.getAttribute("name"));
            }

            adjMatrixSize = nodes = pt.getNumNodes();

            // Process lightpaths
            adjMatrix = new TreeSet[nodes][nodes];
            for (int i = 0; i < nodes; i++) {
                for (int j = 0; j < nodes; j++) {
                    if (i != j) {
                        adjMatrix[i][j] = new TreeSet<LightPath>(new LightPathSort());
                    }
                }
            }
            NodeList lightpathlist = xml.getElementsByTagName("lightpath");
            lightpaths = lightpathlist.getLength();
            if (Simulator.verbose) {
                System.out.println(Integer.toString(lightpaths) + " lightpath(s)");
            }
            if (lightpaths > 0) {
                //TODO
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * First, creates a lightpath in the Physical Topology through the
     * createLightpathInPT function. Then, gets the lightpath's source and
     * destination nodes, so a new LightPath object can finally be created and
     * added to the lightPaths HashMap and to the adjMatrix TreeSet.
     *
     * @param lp the lightpath created by the User to test
     * @return -1 if LightPath object cannot be created, or its unique
     * identifier otherwise
     */
    public long createLightpath(LightPath lp) {
        long id;

        if (lp.getLinks().length < 1) {
            throw (new IllegalArgumentException());
        } else {
            if (!pt.canCreatePhysicalLightpath(lp)) {
                return -1;
            }
            id = this.nextLightpathID;
            lp.setId(id);
            pt.createPhysicalLightpath(lp);
            adjMatrix[lp.getSource()][lp.getDestination()].add(lp);
            lightPaths.put(nextLightpathID, lp);
            tr.createLightpath(lp);
            this.nextLightpathID++;
            return id;
        }
    }

    /**
     * First, removes a given lightpath in the Physical Topology through the
     * removeLightpathInPT function. Then, gets the lightpath's source and
     * destination nodes, to remove it from the lightPaths HashMap and the
     * adjMatrix TreeSet.
     *
     * @param id the unique identifier of the lightpath to be removed
     * @return true if operation was successful, or false otherwise
     */
    public boolean removeLightPath(long id) {
        LightPath lp;

        if (id < 0) {
            throw (new IllegalArgumentException());
        } else {
            if (!lightPaths.containsKey(id)) {
                return false;
            }
            lp = lightPaths.get(id);
            pt.removePhysicalLightpath(lp);

            lightPaths.remove(id);
            adjMatrix[lp.getSource()][lp.getDestination()].remove(lp);
            tr.removeLightpath(lp);

            return true;
        }
    }

    /**
     * Removes a given lightpath from the Physical Topology and then puts it
     * back, but with a new route (set of links).
     *
     * @param id the id of old lightpath
     * @param lp the new lightpath
     * @return true if operation was successful, or false otherwise
     */
    public boolean rerouteLightPath(long id, LightPath lp) {
        LightPath old;
        if (!lightPaths.containsKey(id)) {
            return false;
        } else {
            old = lightPaths.get(id);
            PhysicalTopology aux = pt;
            aux.removePhysicalLightpath(old);
            if (!aux.canCreatePhysicalLightpath(lp)) {
                return false;
            }
            pt.removePhysicalLightpath(old);
            lp.setId(id);
            pt.createPhysicalLightpath(lp);
            //talvez isso nao seja necessario
            lightPaths.remove(id);
            adjMatrix[old.getSource()][old.getDestination()].remove(old);
            tr.removeLightpath(old);
            //////////////////////////////////////
            adjMatrix[lp.getSource()][lp.getDestination()].add(lp);
            lightPaths.put(id, lp);
            tr.createLightpath(lp);
        }
        return true;
    }

    /**
     * Says whether or not a given LightPath object has a determined amount of
     * available bandwidth.
     *
     * @param src the lightpath's source node
     * @param dst the lightpath's destination node
     * @param bw required amount of bandwidth
     * @return true if lightpath is available
     */
    public boolean isLightpathAvailable(int src, int dst, int bw) {
        TreeSet<LightPath> lps = getLightpaths(src, dst);

        for (LightPath lp : lps) {
            if (getLightpathBWAvailable(lp.getID()) >= bw) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a TreeSet with the Virtual Topology's available lightpaths.
     *
     * @param src the lightpath's source node
     * @param dst the lightpath's destination node
     * @param bw required amount of available bandwidth the lightpath must have
     * @return a TreeSet with the available lightpaths
     */
    public TreeSet<LightPath> getAvailableLightpaths(int src, int dst, int bw) {
        TreeSet<LightPath> lps = getLightpaths(src, dst);

        if (lps != null && !lps.isEmpty()) {
            Iterator<LightPath> it = lps.iterator();

            while (it.hasNext()) {
                if (getLightpathBWAvailable(it.next().getID()) < bw) {
                    it.remove();
                }
            }
            return lps;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the available bandwidth of a given lightpath.
     *
     * @param id the lightpath's unique identifier
     * @return amount of available bandwidth
     */
    public double getLightpathBWAvailable(long id) {
        LightPath lp = lightPaths.get(id);
        return pt.getBWAvailable(lp);
    }

    /**
     * Says whether or not a given lightpath is idle, i.e., all its bandwidth is
     * available.
     *
     * @param id the lightpath's unique identifier
     * @return true if lightpath is idle, or false otherwise
     */
    public boolean isLightpathIdle(long id) {
        LightPath lp = lightPaths.get(id);
        double bwTotal = pt.getBW(lp);
        double bwAvailable = pt.getBWAvailable(lp);
        return bwTotal == bwAvailable;
    }

    /**
     * Says whether or not a given lightpath is full, i.e., all its bandwidth is
     * allocated.
     *
     * @param id the lightpath's unique identifier
     * @return true if lightpath is full, or false otherwise
     */
    public boolean isLightpathFull(long id) {
        return getLightpathBWAvailable(id) == 0;
    }

    /**
     * Retrieves a determined LightPath object from the Virtual Topology.
     *
     * @param id the lightpath's unique identifier
     * @return the required lightpath
     */
    public LightPath getLightpath(long id) {
        if (id < 0) {
            throw (new IllegalArgumentException());
        } else {
            if (lightPaths.containsKey(id)) {
                return lightPaths.get(id);
            } else {
                return null;
            }
        }
    }

    /**
     * Retrieves the TreeSet with all LightPath objects that belong to the
     * Virtual Topology.
     *
     * @param src the lightpath's source node
     * @param dst the lightpath's destination node
     * @return the TreeSet with all of the lightpaths
     */
    public TreeSet<LightPath> getLightpaths(int src, int dst) {
        return new TreeSet<LightPath>(adjMatrix[src][dst]);
    }

    /**
     * Retrieves the adjacency matrix of the Virtual Topology.
     *
     * @return the VirtualTopology object's adjMatrix
     */
    public TreeSet<LightPath>[][] getAdjMatrix() {
        return adjMatrix;
    }

    /**
     * Says whether or not a lightpath exists, based only on its source and
     * destination nodes.
     *
     * @param src the lightpath's source node
     * @param dst the lightpath's destination node
     * @return true if the lightpath exists, or false otherwise
     */
    public boolean hasLightpath(int src, int dst) {
        if (adjMatrix[src][dst] != null) {
            if (!adjMatrix[src][dst].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Says whether or not a lightpath can be created, based only on its links
     * and wavelengths.
     *
     * @param links list of integers that represent the links that form the
     * lightpath
     * @param wavelengths list of wavelength values used in the lightpath links
     * @return true if the lightpath can be created, or false otherwise
     */
    //public abstract boolean canCreateLightpath(LightpathArguments args);
    /**
     * Retrieves the number of links (or hops) a given LightPath object has.
     *
     * @param lp the LightPath object
     * @return the number of hops the lightpath has
     */
    public int hopCount(LightPath lp) {
        return lp.getLinks().length;
    }

    /**
     * Retrieves the lightpaths of a weighted graph.
     *
     * @param bw required amount of bandwidth
     * @return a weighted graph formed only by the lightpaths
     */
    public WeightedGraph getLightpathsGraph(int bw) {
        int nodes = pt.getNumNodes();
        WeightedGraph g = new WeightedGraph(nodes);
        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                if (i != j) {
                    if (getAvailableLightpaths(i, j, bw) != null) {
                        g.addEdge(i, j, 1);
                    }
                }
            }
        }
        return g;
    }

    /**
     * Prints all lightpaths belonging to the Virtual Topology.
     *
     * @return string containing all the elements of the adjMatrix TreeSet
     */
    @Override
    public String toString() {
        String vtopo = "";
        for (int i = 0; i < adjMatrixSize; i++) {
            for (int j = 0; j < adjMatrixSize; j++) {
                if (adjMatrix[i][j] != null) {
                    if (!adjMatrix[i][j].isEmpty()) {
                        vtopo += adjMatrix[i][j].toString() + "\n\n";
                    }
                }
            }
        }
        return vtopo;
    }
    public long getNextLightpathID() {
        return nextLightpathID;
    }
    
}
