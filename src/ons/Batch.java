package ons;

/**
 * The Batch class defines an object that has a group flows
 *
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Batch {

    //public class Batch implements comparable<BulkData>{

    private long[] Ids;
    static long currentId = 0;
    private long id;
    private int[] sources;
    private int dst;
    private int[] dataAmounts;
    private double deadline;
    private int[] cost;
    private BulkData bulkOfBatch;
    ArrayList<BulkData> bulks = new ArrayList<>();
    final int type = 2;

    /**
     * Create a new Batch object.
     *
     * @param arrayIds uniques identifiers of Batch and its BulkDatas
     * @param grupoSrc sources nodes of BulkData calls
     * @param dstFinal destination node
     * @param arrayVariations data amout plus variations for each BulkData call
     * @param deadline maximum tolerable transfer time (seconds)
     *
     */
    
    Batch(long[] arrayIds, int[] grupoSrc, int dstFinal, int[] arrayVariations, double deadline, int[] arrayCos) {
        this.Ids = arrayIds; //array de tamanho batchSize+1 porque o primeiro dos Ids será do Batch
        this.sources = grupoSrc;
        this.dst = dstFinal;
        this.dataAmounts = arrayVariations;
        this.deadline = deadline;
        this.cost  = arrayCos;
        
        
        
        for(int i = 1;i < this.Ids.length; i++){
            bulks.add(new BulkData(this.Ids[i],this.sources[i],this.dst,this.dataAmounts[i],this.deadline, this.cost[i] ));
        }
    }

    /**
     * Retrieves the unique identifier for a given Batch.
     *
     * @return the value of the Batch's id attribute
     */
    public long getIDbatch() {
        return this.Ids[0];
    }

    /**
     * Retrieves the array of the unique identifiers of BulkDatas into Batch.
     *
     *
     * @return the value's array of the BulkData's id attribute
     */
    public long[] getIds() {
        return this.Ids;
    }
    
    public int[] getAllDataAmounts(){
        return this.dataAmounts;
    }

    /**
     * Retrieves the all sorces.
     *
     * @return the sorces's array of BulkData's calls
     */
    public int[] getBatchSources() {
        return this.sources;
    }

    /**
     * Retrieves the destination Batch.
     *
     * @return the destination of BulkData's calls
     */
    public int getBatchDst() {
        return this.dst;
    }
    
    public double getDeadline(){
        return this.deadline;
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this
     * queue is empty.
     *
     * @return
     */
    public BulkData popBulkData() {
        if(!bulks.isEmpty()){
            return bulks.remove(bulks.size() - 1);
        }else throw new IndexOutOfBoundsException("Cannot pop an empty List");
        
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null
     * if this queue is empty.
     *
     * @return
     */
    public BulkData getBulkData() {
        return bulks.get(bulks.size() - 1);
    }
    
    public int[] getCos(){
        return this.cost;
    }

    /**
     * Inform if the batch does not contain flows
     *
     * @return
     */
    public boolean isEmpty() {
        return bulks.size() == 0;
    }

    /**
     * Retrives the size of Batch or amount of flow inside one.
     *
     * @return the size of BatchArrivalEvent's Batch attribute
     */
    public int getSize() {
        return bulks.size();
       // System.out.println("Size of ArrayList is : " + bulks.size());//test
    }

    /**
     * Prints all information related to the arriving Batch.
     *
     * @return string containing all the values of the batch's parameters
     */
    public String toTrace() {
        String trace = Long.toString(id) + " bulk data:\n";

       // ArrayLis<BulkData> bulkQueue = this.bulkQueue;
        int size = this.bulks.size();
        Iterator<BulkData> it = bulks.iterator( );
        for (int i = 0; i < size; i++) {
            while ( it.hasNext( ) ) {
            trace += bulks.get(i).toTrace();
            }
        }

        return trace;
    }

}
