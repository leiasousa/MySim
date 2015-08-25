package ons;

public class MyStatistics {

    private static MyStatistics singletonObject;

    private int minNumberArrivals;
    private int numberArrivals;
    private int numberBulkArrivals;
    private int numberBatchArrivals;
    private double meanArrivalRate;
    private int arrivalRate;
    private int arrivals;
    private int departures;
    private int accepted;
    private int batchAccepted;
    private int bulkAccepted;
    private int blocked;
    private int requiredBandwidth;
    private int blockedBandwidth;
    private double acumulatedCost;
    private int numNodes;
    private int[][] arrivalsPairs;
    private int[][] blockedPairs;
    private int[][] requiredBandwidthPairs;
    private int[][] blockedBandwidthPairs;
    private int acumulatedK;
    private int acumulatedHops;
    private int acumulatedLPs;
    private int numfails;
    private int flowfails;
    private int lpsfails;
    private float trafficfails;
    private long execTime;

    // Diff
    private int numClasses;
    private int[] arrivalsDiff;
    private int[] blockedDiff;
    private int[] requiredBandwidthDiff;
    private int[] blockedBandwidthDiff;
    private int[][][] arrivalsPairsDiff;
    private int[][][] blockedPairsDiff;
    private int[][][] requiredBandwidthPairsDiff;
    private int[][][] blockedBandwidthPairsDiff;

    /**
     * A private constructor that prevents any other class from instantiating.
     */
    private MyStatistics() {

        numberArrivals = 0;
        numberBatchArrivals = 0;
        numberBulkArrivals = 0;
        arrivalRate=0;
//        meanArrivalRate = 1 / arrivalRate;

        arrivals = 0;
        departures = 0;
        accepted = 0;
        batchAccepted = 0;
        bulkAccepted = 0;
        blocked = 0;

        requiredBandwidth = 0;
        blockedBandwidth = 0;

        numfails = 0;
        flowfails = 0;
        lpsfails = 0;
        trafficfails = 0;

        execTime = 0;
    }

    /**
     * Creates a new MyStatistics object, in case it does'n exist yet.
     *
     * @return the MyStatistics singletonObject
     */
    public static synchronized MyStatistics getMyStatisticsObject() {
        if (singletonObject == null) {
            singletonObject = new MyStatistics();
        }
        return singletonObject;
    }

    /**
     * Throws an exception to stop a cloned MyStatistics object from being
     * created.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Attributes initializer.
     *
     * @param numNodes number of nodes in the network
     * @param numClasses number of classes of service
     * @param minNumberArrivals minimum number of arriving events
     */
    public void statisticsSetup(int numNodes, int numClasses, int minNumberArrivals) {
        this.numNodes = numNodes;
        this.arrivalsPairs = new int[numNodes][numNodes];
        this.blockedPairs = new int[numNodes][numNodes];
        this.requiredBandwidthPairs = new int[numNodes][numNodes];
        this.blockedBandwidthPairs = new int[numNodes][numNodes];

        this.minNumberArrivals = minNumberArrivals;

        //Diff
        this.numClasses = numClasses;
        this.arrivalsDiff = new int[numClasses];
        this.blockedDiff = new int[numClasses];
        this.requiredBandwidthDiff = new int[numClasses];
        this.blockedBandwidthDiff = new int[numClasses];
        for (int i = 0; i < numClasses; i++) {
            this.arrivalsDiff[i] = 0;
            this.blockedDiff[i] = 0;
            this.requiredBandwidthDiff[i] = 0;
            this.blockedBandwidthDiff[i] = 0;
        }
        this.arrivalsPairsDiff = new int[numNodes][numNodes][numClasses];
        this.blockedPairsDiff = new int[numNodes][numNodes][numClasses];
        this.requiredBandwidthPairsDiff = new int[numNodes][numNodes][numClasses];
        this.blockedBandwidthPairsDiff = new int[numNodes][numNodes][numClasses];
    }

    /**
     * Adds an accepted flow to the statistics.
     *
     * @param flow the accepted Flow object
     * @param lightpaths list of lightpaths in the flow
     */
    public void acceptFlow(Flow flow, LightPath[] lightpaths) {
        if (this.numberArrivals > this.minNumberArrivals) {
            this.accepted++;
            //this.acumulatedCost += rwa.getPathCost(lightpaths);
        }
    }
    
    public void acceptBulkData(BulkData bulk, LightPath[] lightpaths) {
        if (this.numberBulkArrivals > this.minNumberArrivals) {
        this.bulkAccepted++;
        }
    }
    
    /**
     * Adds an accepted Batch to the statistics.
     *
     * @param batch the accepted Batch object
     * @param Blightpaths list of lightpaths in each object of Batch
     */
    
    public void acceptBatch(Batch batch){
        int size = batch.getSize();
        for(int i = 0; i < size; i++){
            if (this.numberBulkArrivals > this.minNumberArrivals) {
                this.bulkAccepted++;
             }
        }
        if (this.bulkAccepted == 3 || this.bulkAccepted > 3){
            this.batchAccepted++;
        }        
        }
    
        /**
     * Adds a blocked flow to the statistics.
     *
     * @param flow the blocked Flow object
     */
    public void blockFlow(Flow flow){
        if (this.numberArrivals > this.minNumberArrivals) {
            int cos = flow.getCOS();
            this.blocked++;
            this.blockedDiff[cos]++;
            this.blockedBandwidth += flow.getRate();
            this.blockedBandwidthDiff[cos] += flow.getRate();
            this.blockedPairs[flow.getSource()][flow.getDestination()]++;
            System.out.println("Pares bloqueados: " +this.blockedPairs);// test
            this.blockedPairsDiff[flow.getSource()][flow.getDestination()][cos]++;
            this.blockedBandwidthPairs[flow.getSource()][flow.getDestination()] += flow.getRate();
            this.blockedBandwidthPairsDiff[flow.getSource()][flow.getDestination()][cos] += flow.getRate();
        }
    }
    
    public void blockBulkData(BulkData bulkData){
        int rate = (int) (bulkData.getDataAmount()/bulkData.getDeadline());
        if (this.numberBulkArrivals > this.minNumberArrivals) {
            int cos = bulkData.getCos();
            this.blocked++;
            this.blocked++;
            this.blockedDiff[cos]++;
            this.blockedBandwidth += rate;
            this.blockedBandwidthDiff[cos] += rate;
            this.blockedPairs[bulkData.getSource()][bulkData.getDestination()]++;
            System.out.println("Pares bloqueados: " +this.blockedPairs);// test
            this.blockedPairsDiff[bulkData.getSource()][bulkData.getDestination()][cos]++;
            this.blockedBandwidthPairs[bulkData.getSource()][bulkData.getDestination()] += rate;
            this.blockedBandwidthPairsDiff[bulkData.getSource()][bulkData.getDestination()][cos] += rate;
       }
    }
 
    public void blockBatch(Batch batch){
        if (this.bulkAccepted < 3){
            this.blocked++;
        }
    }

    /**
     * Adds an event to the statistics.
     *
     * @param event the Event object to be added
     */
    public void addEvent(Event event) {
        try {
            if (event instanceof FlowArrivalEvent) {
                this.numberArrivals++;
                if (this.numberArrivals > this.minNumberArrivals) {
                    int cos = ((FlowArrivalEvent) event).getFlow().getCOS();
                    this.arrivals++;
                    this.arrivalsDiff[cos]++;
                    this.requiredBandwidth += ((FlowArrivalEvent) event).getFlow().getRate();
                    this.requiredBandwidthDiff[cos] += ((FlowArrivalEvent) event).getFlow().getRate();
                    this.arrivalsPairs[((FlowArrivalEvent) event).getFlow().getSource()][((FlowArrivalEvent) event).getFlow().getDestination()]++;
                    this.arrivalsPairsDiff[((FlowArrivalEvent) event).getFlow().getSource()][((FlowArrivalEvent) event).getFlow().getDestination()][cos]++;
                    this.requiredBandwidthPairs[((FlowArrivalEvent) event).getFlow().getSource()][((FlowArrivalEvent) event).getFlow().getDestination()] += ((FlowArrivalEvent) event).getFlow().getRate();
                    this.requiredBandwidthPairsDiff[((FlowArrivalEvent) event).getFlow().getSource()][((FlowArrivalEvent) event).getFlow().getDestination()][cos] += ((FlowArrivalEvent) event).getFlow().getRate();
                }
                //
                if (Simulator.verbose && Math.IEEEremainder((double) arrivals, (double) 10000) == 0) {
                    System.out.println(Integer.toString(arrivals));
                }
                //
            } else if (event instanceof FlowDepartureEvent) {
                if (this.numberArrivals > this.minNumberArrivals) {
                    this.departures++;
                }
            } else if (event instanceof BulkDataArrivalEvent){
                this.numberArrivals++;
                if (this.numberArrivals > this.minNumberArrivals) {
                    int cos = ((BulkDataArrivalEvent) event).getBulkData().getCos();
                    int rate = (int) (((BulkDataArrivalEvent) event).getBulkData().getDataAmount()/((BulkDataArrivalEvent) event).getBulkData().getDeadline());
                    this.arrivals++;
                    this.arrivalsDiff[cos]++;
                    this.requiredBandwidth += rate;
                    this.requiredBandwidthDiff[cos] += rate;
                    this.arrivalsPairs[((BulkDataArrivalEvent) event).getBulkData().getSource()][((BulkDataArrivalEvent) event).getBulkData().getDestination()]++;
                    this.arrivalsPairsDiff[((BulkDataArrivalEvent) event).getBulkData().getSource()][((BulkDataArrivalEvent) event).getBulkData().getDestination()][cos]++;
                    this.requiredBandwidthPairs[((BulkDataArrivalEvent) event).getBulkData().getSource()][((BulkDataArrivalEvent) event).getBulkData().getDestination()] += rate;
                    this.requiredBandwidthPairsDiff[((BulkDataArrivalEvent) event).getBulkData().getSource()][((BulkDataArrivalEvent) event).getBulkData().getDestination()][cos] += rate;
                }
            } else if (event instanceof BatchArrivalEvent){
                 this.numberBatchArrivals++;
                int size = ((BatchArrivalEvent) event).getBatchSize();
                for (int i = 0; i < size; i++){
                    this.numberArrivals++;
                    this.numberArrivals++;
                if (this.numberArrivals > this.minNumberArrivals) {
                    int cos = ((BulkDataArrivalEvent) event).getBulkData().getCos();
                    int rate = (int) (((BulkDataArrivalEvent) event).getBulkData().getDataAmount()/((BulkDataArrivalEvent) event).getBulkData().getDeadline());
                    this.arrivals++;
                    this.arrivalsDiff[cos]++;
                    this.requiredBandwidth += rate;
                    this.requiredBandwidthDiff[cos] += rate;
                    this.arrivalsPairs[((BulkDataArrivalEvent) event).getBulkData().getSource()][((BulkDataArrivalEvent) event).getBulkData().getDestination()]++;
                    this.arrivalsPairsDiff[((BulkDataArrivalEvent) event).getBulkData().getSource()][((BulkDataArrivalEvent) event).getBulkData().getDestination()][cos]++;
                    this.requiredBandwidthPairs[((BulkDataArrivalEvent) event).getBulkData().getSource()][((BulkDataArrivalEvent) event).getBulkData().getDestination()] += rate;
                    this.requiredBandwidthPairsDiff[((BulkDataArrivalEvent) event).getBulkData().getSource()][((BulkDataArrivalEvent) event).getBulkData().getDestination()][cos] += rate;
                }
                }
                
            
        }
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This function is called during the simulation execution, but only if
     * verbose was activated.
     *
     * @return string with the obtained statistics
     */
    public String fancyStatistics() {
        // TODO Identifica quando for EONSim
        float acceptProb, blockProb, bbr, meanK;
        if (accepted == 0) {
            acceptProb = 0;
            meanK = 0;
        } else {
            acceptProb = ((float) accepted) / ((float) arrivals) * 100;
        }
        if (blocked == 0) {
            blockProb = 0;
            bbr = 0;
        } else {
            blockProb = ((float) blocked) / ((float) arrivals) * 100;
            bbr = ((float) blockedBandwidth) / ((float) requiredBandwidth) * 100;
        }

        String stats = "Arrivals \t: " + Integer.toString(arrivals) + "\n";
        stats += "Required BW \t: " + Integer.toString(requiredBandwidth) + "\n";
        stats += "Departures \t: " + Integer.toString(departures) + "\n";
        stats += "Accepted \t: " + Integer.toString(accepted) + "\t(" + Float.toString(acceptProb) + "%)\n";
        stats += "Blocked \t: " + Integer.toString(blocked) + "\t(" + Float.toString(blockProb) + "%)\n";
        stats += "BBR     \t: " + Float.toString(bbr) + "%\n";
        stats += "\n";
        stats += "Blocking probability per s-d pair:\n";
        for (int i = 0; i < numNodes; i++) {
            for (int j = i + 1; j < numNodes; j++) {
                stats += "Pair (" + Integer.toString(i) + "->" + Integer.toString(j) + ") ";
                stats += "Calls (" + Integer.toString(arrivalsPairs[i][j]) + ")";
                if (blockedPairs[i][j] == 0) {
                    blockProb = 0;
                    bbr = 0;
                } else {
                    blockProb = ((float) blockedPairs[i][j]) / ((float) arrivalsPairs[i][j]) * 100;
                    bbr = ((float) blockedBandwidthPairs[i][j]) / ((float) requiredBandwidthPairs[i][j]) * 100;
                }
                stats += "\tBP (" + Float.toString(blockProb) + "%)";
                stats += "\tBBR (" + Float.toString(bbr) + "%)\n";
            }
        }

        return stats;
    }

    /**
     * Prints all the obtained statistics, but only if verbose was not
     * activated.
     */
    public void printStatistics() {
        int count = 0;
        float bp, bbr, jfi, sum1 = 0, sum2 = 0;
        float bpDiff[], bbrDiff[];

        if (blocked == 0) {
            bp = 0;//blocking probability
            bbr = 0;// bandwidth-blocking ratio
        } else {
            bp = ((float) blocked) / ((float) arrivals) * 100;//blocking probability
            bbr = ((float) blockedBandwidth) / ((float) requiredBandwidth) * 100;// bandwidth-blocking ratio
        }
        bpDiff = new float[numClasses];
        bbrDiff = new float[numClasses];
        for (int i = 0; i < numClasses; i++) {
            if (blockedDiff[i] == 0) {
                bpDiff[i] = 0;
                bbrDiff[i] = 0;
            } else {
                bpDiff[i] = ((float) blockedDiff[i]) / ((float) arrivalsDiff[i]) * 100;
                bbrDiff[i] = ((float) blockedBandwidthDiff[i]) / ((float) requiredBandwidthDiff[i]) * 100;
            }
        }

        System.out.println("MBP " + Float.toString(bp));
        for (int i = 0; i < numClasses; i++) {
            System.out.println("MBP-" + Integer.toString(i) + " " + Float.toString(bpDiff[i]));
        }
        System.out.println("MBBR " + Float.toString(bbr));
        for (int i = 0; i < numClasses; i++) {
            System.out.println("MBBR-" + Integer.toString(i) + " " + Float.toString(bbrDiff[i]));
        }

        for (int i = 0; i < numNodes; i++) {
            for (int j = i + 1; j < numNodes; j++) {
                if (i != j) {
                    System.out.print(Integer.toString(i) + "-" + Integer.toString(j) + " ");
                    System.out.print("A " + Integer.toString(arrivalsPairs[i][j]) + " ");
                    if (blockedPairs[i][j] == 0) {
                        bp = 0;//blocking probability
                        bbr = 0;//bandwidth-blocking ratio
                    } else {
                        bp = ((float) blockedPairs[i][j]) / ((float) arrivalsPairs[i][j]) * 100;//blocking probability
                        bbr = ((float) blockedBandwidthPairs[i][j]) / ((float) requiredBandwidthPairs[i][j]) * 100;//bandwidth-blocking ratio
                    }
                    count++;
                    sum1 += bbr;
                    sum2 += bbr * bbr;
                    System.out.print("BP " + Float.toString(bp) + " ");
                    System.out.println("BBR " + Float.toString(bbr));
                }
            }
        }
        jfi = (sum1 * sum1) / ((float) count * sum2);
        System.out.println("JFI " + Float.toString(jfi));
        //Diff
        for (int c = 0; c < numClasses; c++) {
            count = 0;
            sum1 = 0;
            sum2 = 0;
            for (int i = 0; i < numNodes; i++) {
                for (int j = i + 1; j < numNodes; j++) {
                    if (i != j) {
                        if (blockedPairsDiff[i][j][c] == 0) {
                            bp = 0;//blocking probability
                            bbr = 0;//bandwidth-blocking ratio
                        } else {
                            bp = ((float) blockedPairsDiff[i][j][c]) / ((float) arrivalsPairsDiff[i][j][c]) * 100;//blocking probability
                            bbr = ((float) blockedBandwidthPairsDiff[i][j][c]) / ((float) requiredBandwidthPairsDiff[i][j][c]) * 100;//bandwidth-blocking ratio
                        }
                        count++;
                        sum1 += bbr;
                        sum2 += bbr * bbr;
                    }
                }
            }
            jfi = (sum1 * sum1) / ((float) count * sum2);
            System.out.println("JFI-" + Integer.toString(c) + " " + Float.toString(jfi));
        }
    }

    /**
     * Terminates the singleton object.
     */
    public void finish() {
        singletonObject = null;
    }
}
