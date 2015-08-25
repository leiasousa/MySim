package ons;

import ons.util.WeightedGraph;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EONPhysicalTopology extends PhysicalTopology {

    private static final int N_MOD = 4;
    private static final int MOD_BPSK = 0;
    private static final int MOD_QPSK = 1;
    private static final int MOD_8QAM = 2;
    private static final int MOD_16QAM = 3;
    private static int slotSize;
    
    public EONPhysicalTopology(Element xml) {
        super(xml);

        int id;
        int groomingInPorts, groomingOutPorts, capacity = 0;
        int[] modulations = new int[N_MOD];
        boolean generalCapacity = false, generalModulation = false;
        double delay, weight;
        String[] parts;

        try {
            // Checking the atributtes of <nodes> tag for general values
            NodeList nodesEntities = xml.getElementsByTagName("nodes");
            if (((Element) nodesEntities.item(0)).hasAttribute("modulations")) {
                generalModulation = true;
                for (int i = 0; i < modulations.length; i++) {
                    modulations[i] = 0;
                }
                parts = (((Element) nodesEntities.item(0)).getAttribute("modulations").split(",[ ]*"));
                for (String part : parts) {
                    modulations[convertModulationTypeToInteger(part)] = 1;
                }
            }
            if (((Element) nodesEntities.item(0)).hasAttribute("capacity")) {
                generalCapacity = true;
                capacity = Integer.parseInt(((Element) nodesEntities.item(0)).getAttribute("capacity"));
            }
            // Process nodes
            NodeList nodelist = xml.getElementsByTagName("node");
            nodes = nodelist.getLength();
            if (Simulator.verbose) {
                System.out.println(Integer.toString(nodes) + " nodes");
            }
            nodeVector = new EONOXC[nodes];
            for (int i = 0; i < nodes; i++) {
                id = Integer.parseInt(((Element) nodelist.item(i)).getAttribute("id"));
                groomingInPorts = Integer.parseInt(((Element) nodelist.item(i)).getAttribute("grooming-in-ports"));
                groomingOutPorts = Integer.parseInt(((Element) nodelist.item(i)).getAttribute("grooming-out-ports"));
                if (!generalModulation) {
                    for (int j = 0; j < modulations.length; j++) {
                        modulations[j] = 0;
                    }
                    parts = (((Element) nodelist.item(i)).getAttribute("modulations").split(",[ ]*"));
                    // FIXME: Fazer o tratamento de execao caso o usuario nao coloque a 'modulations' em todos os nos
                    for (String part : parts) {
                        modulations[convertModulationTypeToInteger(part)] = 1;
                    }
                }
                if (!generalCapacity) {
                    // FIXME: Fazer o tratamento de execao caso o usuario nao coloque a 'capacity' em todos os nos
                    capacity = Integer.parseInt(((Element) nodelist.item(i)).getAttribute("capacity"));
                }
                nodeVector[id] = new EONOXC(id, groomingInPorts, groomingOutPorts, capacity, modulations);
            }

            int src, dst, slots = 0, guardband = 0, slotSize = 0;
            // FIXME: 'slotSize' pode ser float para economizar espaco
            boolean generalSlots = false, generalGuardband = false, generalSlotSize = false;
            
            // Checking the atributtes of <links> tag for general values
            NodeList linksEntities = xml.getElementsByTagName("links");
            if (((Element) linksEntities.item(0)).hasAttribute("slots")) {
                generalSlots = true;
                slots = Integer.parseInt(((Element) linksEntities.item(0)).getAttribute("slots"));
            }
            if (((Element) linksEntities.item(0)).hasAttribute("guardband")) {
                generalGuardband = true;
                guardband = Integer.parseInt(((Element) linksEntities.item(0)).getAttribute("guardband"));
            }
            if (((Element) linksEntities.item(0)).hasAttribute("slot-size")) {
                generalSlotSize = true;
                slotSize = Integer.parseInt(((Element) linksEntities.item(0)).getAttribute("slot-size"));
            }

            // Process links
            NodeList linklist = xml.getElementsByTagName("link");
            links = linklist.getLength();
            if (Simulator.verbose) {
                System.out.println(Integer.toString(links) + " links");
            }
            linkVector = new EONLink[links];
            adjMatrix = new EONLink[nodes][nodes];
            // <link id="0" source="0" destination="1" delay="3.75" slots="200" guardband="2" weight="750"/>
            for (int i = 0; i < links; i++) {
                id = Integer.parseInt(((Element) linklist.item(i)).getAttribute("id"));
                src = Integer.parseInt(((Element) linklist.item(i)).getAttribute("source"));
                dst = Integer.parseInt(((Element) linklist.item(i)).getAttribute("destination"));
                delay = Double.parseDouble(((Element) linklist.item(i)).getAttribute("delay"));
                if (!generalSlots) {
                    // FIXME: Fazer o tratamento de execao caso o usuario nao coloque o 'slots' em todos os links
                    slots = Integer.parseInt(((Element) linklist.item(i)).getAttribute("slots"));
                }
                if (!generalGuardband) {
                    // FIXME: Fazer o tratamento de execao caso o usuario nao coloque a 'guardband' em todos os links
                    guardband = Integer.parseInt(((Element) linklist.item(i)).getAttribute("guardband"));
                }
                if (!generalSlotSize) {
                    // FIXME: Fazer o tratamento de execao caso o usuario nao coloque o 'slot-size' em todos os links
                    slotSize = Integer.parseInt(((Element) linksEntities.item(0)).getAttribute("slot-size"));
                }
                weight = Double.parseDouble(((Element) linklist.item(i)).getAttribute("weight"));
                linkVector[id] = adjMatrix[src][dst] = new EONLink(id, src, dst, delay, weight, slots, guardband, slotSize);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     * Convert the string value of modulation type to the equivalent integer
     * value, definied on this class.
     *
     * @param name the name of modulation format
     * @return integer correponding to the modulation format
     */
    private int convertModulationTypeToInteger(String name) {
        String toLowerCase = name.toLowerCase();
        switch (toLowerCase) {
            case "16qam":
                return MOD_16QAM;
            case "8qam":
                return MOD_8QAM;
            case "qpsk":
                return MOD_QPSK;
            case "bpsk":
                return MOD_BPSK;
        }
        return -1;
    }
    
    /**
     * Retrieves the slot size in MHz.
     * @return slot size in MHz
     */
    public static int getSlotSize() {
        return slotSize;
    }

    @Override
    public void createPhysicalLightpath(LightPath lightpath) {
        //testa se em cada link tem esses slots requeridos
        for (int i = 0; i < lightpath.links.length; i++) {
            ((EONLink) linkVector[lightpath.links[i]]).reserveSlots(lightpath.id, ((EONLightPath) lightpath).getFirstSlot(), ((EONLightPath) lightpath).getLastSlot());
        }
        // Reserve ports
        this.getNode(this.getLink(lightpath.links[0]).getSource()).reserveGroomingInputPort();
        this.getNode(this.getLink(lightpath.links[lightpath.links.length - 1]).getDestination()).reserveGroomingOutputPort();
    }

    @Override
    public boolean canCreatePhysicalLightpath(LightPath lightpath) {
        //teste de continuidade
        if (!checkLinkPath(lightpath.links)) {
            return false; 
        }
        //testa de modulacao
        if (!((((EONOXC) nodeVector[lightpath.getSource()]).hasModulation(((EONLightPath) lightpath).getModulation()))
                && (((EONOXC) nodeVector[lightpath.getDestination()]).hasModulation(((EONLightPath) lightpath).getModulation())))) {
            return false;
        }
        // Available transceivers
        if (!getNode(getLink(lightpath.links[0]).getSource()).hasFreeGroomingInputPort()) {
            return false;
        }
        if (!getNode(getLink(lightpath.links[lightpath.links.length - 1]).getDestination()).hasFreeGroomingOutputPort()) {
            return false;
        }
        //teste de capacidade do transceiver de origem
        if (((EONOXC) nodeVector[lightpath.getSource()]).getCapacity() < (((EONLightPath) lightpath).getLastSlot() - ((EONLightPath) lightpath).getFirstSlot() + 1)) {
            return false;
        }
        //teste de slot
        //testa se em cada link tem esses slots requeridos
        for (int i = 0; i < lightpath.links.length; i++) {
            if (!(((EONLink) linkVector[lightpath.links[i]]).areSlotsAvaiable(((EONLightPath) lightpath).getFirstSlot(), ((EONLightPath) lightpath).getLastSlot()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void removePhysicalLightpath(LightPath lightpath) {
        //faz o cast
        //testa se em cada link tem esses slots requeridos
        for (int i = 0; i < lightpath.links.length; i++) {
            ((EONLink) linkVector[lightpath.links[i]]).releaseSlots(((EONLightPath) lightpath).getFirstSlot(), ((EONLightPath) lightpath).getLastSlot());
        }
        // Release ports
        this.getNode(this.getLink(lightpath.links[0]).getSource()).releaseGroomingInputPort();
        this.getNode(this.getLink(lightpath.links[lightpath.links.length - 1]).getDestination()).releaseGroomingOutputPort();
    }

    @Override
    public double getBWAvailable(LightPath lightpath) {
        return ((EONLightPath) lightpath).getBwAvailable();
    }

    @Override
    public void addFlow(Flow flow, LightPath lightpath) {
        ((EONLightPath) lightpath).addFlowOnLightPath(flow.getRate());
    }
    
    public void addBulkData(BulkData bulkData, LightPath lightpath){
        double rate = bulkData.getDataAmount()/bulkData.getDeadline();
        ((EONLightPath) lightpath).addBulkDataonLightPath(rate);
    }

    @Override
    public double getBW(LightPath lightpath) {
        return ((EONLightPath) lightpath).getBw();               
    }
    
    /**
     * Returns a weighted graph with vertices representing the physical network
     * nodes, and the edges representing the physical links.
     *
     * The weight of each edge receives the same value of the original link
     * weight if the wavelength wvl in that link has at least bw Mbps of
     * bandwidth available. Otherwise it has no edges.
     *
     * @param firstSlot the firstSlot index
     * @param lastSlot thes lastSlot index
     * @return an WeightedGraph class object
     */
    public WeightedGraph getWeightedGraph(int firstSlot, int lastSlot) {
        EONLink link;
        WeightedGraph g = new WeightedGraph(nodes);
        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                if (hasLink(i, j)) {
                    link = (EONLink) getLink(i, j);
                    if (link.areSlotsAvaiable(firstSlot,lastSlot)) {
                        g.addEdge(i, j, link.getWeight());
                    }
                }
            }
        }
        return g;
    }

    @Override
    public boolean canAddFlow(Flow flow, LightPath lightpath) {
        return ((EONLightPath) lightpath).getBwAvailable() >= flow.getRate();
    }
    
    public boolean canAddBulkData(BulkData bulkData,LightPath lightpath ){
        return ((EONLightPath) lightpath).getBwAvailable() >= (bulkData.getDataAmount()/bulkData.getDeadline());
    }

    @Override
    public void removeFlow(Flow flow, LightPath lightpath) {
        ((EONLightPath) lightpath).removeFlowOnLightPath(flow.getRate());
    }
    
    public void removeBulkData(BulkData bulkData, LightPath lightpath) {
        double rate = bulkData.getDataAmount()/bulkData.getDeadline();
        ((EONLightPath) lightpath).removeFlowOnLightPath(rate);
    }
    
    /**
     * Retrieves the slots available in all physical topology links.
     * @return the number of slots available
     */
    public int getAvailableSlots(){
        int slots = 0;
        for(int i = 0; i < links; i++){
            slots = slots + ((EONLink) this.getLink(i)).getAvaiableSlots();
        }
        return slots;
    }
}
