/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ons;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import ons.util.Distribution;
import org.w3c.dom.*;

/**
 * Generates the network's traffic based on the information passed through the
 * command line arguments and the XML simulation file.
 *
 * @author andred
 */
public class TrafficGenerator {

    private Batch lote;
    private int calls;
    private double arrivalRate;
    private double load;
    private int maxRate;
    private TrafficInfo[] callsTypesInfo;
    private double meanRate;
    private double meanHoldingTime;
    private Integer transmitersNodes = 3;
    private int TotalWeight;
    private int numberCallsTypes;
    private int trafficType;
    private int delta;
    private int contBulkCalls = 0;
    private int contBatchCalls = 0;
    private long[] arrayIds;
    private int[] arrayVariations;

    /**
     * Creates a new TrafficGenerator object. Extracts the traffic information
     * from the XML file and takes the chosen load and seed from the command
     * line arguments.
     *
     * @param xml file that contains all information about the simulation
     * @param forcedLoad range of offered loads for several simulations
     */
    public TrafficGenerator(Element xml, double forcedLoad) {
        int numberCallsTypes, dataAmount, deadline, weight;

        calls = Integer.parseInt(xml.getAttribute("calls"));
        System.out.println("iniciando geração de tráfego");//test
        System.out.println("O arquivo xml possui " + calls + " chamadas");
        arrivalRate = forcedLoad;

        if (arrivalRate == 0) {
            arrivalRate = Double.parseDouble(xml.getAttribute("arrivalRate"));
        }

        if (Simulator.verbose) {
            System.out.println(xml.getAttribute("calls") + " calls, " + xml.getAttribute("arrivalRate") + " arrival rate.");
            System.out.println("pegando os dados das chamadas no xml");
        }

        // Process calls
        NodeList callslist = xml.getElementsByTagName("calls");
        numberCallsTypes = callslist.getLength();
        System.out.println("Existe uma lista com " + numberCallsTypes + " tipos de chamadas");

        if (Simulator.verbose) {
            System.out.println(Integer.toString(numberCallsTypes) + " type(s) of calls:");
        }

        callsTypesInfo = new TrafficInfo[numberCallsTypes];

        TotalWeight = 0;

        for (int i = 0; i < numberCallsTypes; i++) {
            TotalWeight += Integer.parseInt(((Element) callslist.item(i)).getAttribute("weight"));
            trafficType = Integer.parseInt(((Element) callslist.item(i)).getAttribute("type"));

            if (trafficType == 1) {
                contBulkCalls++;
                System.out.println("tipo de tráfego: 1 - bulk ");//test
                dataAmount = Integer.parseInt(((Element) callslist.item(i)).getAttribute("dataAmount"));
                deadline = Integer.parseInt(((Element) callslist.item(i)).getAttribute("deadline"));
                weight = Integer.parseInt(((Element) callslist.item(i)).getAttribute("weight"));

                callsTypesInfo[i] = new TrafficInfo(dataAmount, deadline, weight, trafficType);
                if (Simulator.verbose) {
                    System.out.println("#################################");
                    System.out.println("Data_Amount: " + Integer.toString(dataAmount) + "GB. ");
                    System.out.println("Deadline: " + Integer.toString(deadline) + "seconds. ");
                    System.out.println("Weight: " + Integer.toString(weight) + ".");
                }
            } else if (trafficType == 2) {
                contBatchCalls++;
                System.out.println("tipo de tráfego: 2 - batch");
                transmitersNodes = Integer.parseInt(((Element) callslist.item(i)).getAttribute("sources"));
                dataAmount = Integer.parseInt(((Element) callslist.item(i)).getAttribute("dataAmount"));
                delta = Integer.parseInt(((Element) callslist.item(i)).getAttribute("delta"));
                deadline = Integer.parseInt(((Element) callslist.item(i)).getAttribute("deadline"));
                double q = dataAmount / deadline;//test
                weight = Integer.parseInt(((Element) callslist.item(i)).getAttribute("weight"));

                System.out.println("Transferindo " + Integer.toString(dataAmount) + "GB em " + Integer.toString(deadline));//test
                System.out.println("com taxa de " + q + " gigabits por segundo. ");//test

                //callsTypesInfo[i] = new TrafficInfo(transmitersNodes, dataAmount, delta, deadline, weight, trafficType);
                callsTypesInfo[i] = new TrafficInfo(transmitersNodes, dataAmount, delta, deadline, weight, trafficType);
                if (Simulator.verbose) {
                    System.out.println("#################################");
                    System.out.println("Number_Source_Nodes: " + Integer.toString(transmitersNodes) + ". ");
                    System.out.println("Data_Amount: " + Integer.toString(dataAmount) + "GB. ");
                    //System.out.println("Data_Amount: " + Integer.toString(variation) + "GB. ");
                    System.out.println("Variation_Size_Data: " + Integer.toString(delta) + "GB. ");
                    System.out.println("Deadline: " + Integer.toString(deadline) + "seconds. ");
                    System.out.println("Weight: " + Integer.toString(weight) + ".");
                }
            }

        }
    }

    /**
     * Generates the network's traffic.
     *
     * @param events EventScheduler object that will contain the simulation
     * events
     * @param pt the network's Physical Topology
     * @param seed a number in the interval [1,25] that defines up to 25
     * different random simulations
     */
    public void generateTraffic(PhysicalTopology pt, EventScheduler events, int seed) {

        // Compute the weight vector
        int[] weightVector = new int[TotalWeight];
        int aux = 0;
        for (int i = 0; i < numberCallsTypes; i++) {
            System.out.println("PARA  A numberCallsTypes " + i);//test
            for (int j = 0; j < callsTypesInfo[i].getWeight(); j++) {
                weightVector[aux] = i;
                aux++;
                System.out.println("PRIORIDADE:callsTypesInfo " + j);//test
            }
        }

        /* Compute the Arrival Rate
         *
         */
        double meanArrivalRate = 1 / arrivalRate;

        // Generate events
        int type, dst;
        double time = 0.0;
        int id = 0;
        int numNodes = pt.getNumNodes();
        

        Distribution dist1, dist2, dist3, dist4;
        Event event;

        dist1 = new Distribution(1, seed);
        dist2 = new Distribution(2, seed);
        dist3 = new Distribution(3, seed);
        dist4 = new Distribution(4, seed);
        for (int j = 0; j < calls; j++) {
            for (int i = 0; i < numberCallsTypes; i++) {
                if (callsTypesInfo[i].getType() == 1) {
                    System.out.println("Chamadas do tipo BulkData: src ---> dst");
                    type = weightVector[dist1.nextInt(TotalWeight)];
                    System.out.println(j);
                    int src;
                    src = dst = dist2.nextInt(numNodes);
                    while (src == dst) {
                        dst = dist2.nextInt(numNodes);
                    }
                    System.out.println("BulkTraffic: ---sortenado os nós src,dst");//test
                    System.out.println("trafficGenerator - destino: " + dst);
                    System.out.println("trafficGenerator - Origens: " + src);

                    event = new BulkDataArrivalEvent(new BulkData(((long) id), src, dst, callsTypesInfo[type].getDataAmount(), callsTypesInfo[type].getDeadline(), callsTypesInfo[type].getWeight()));
                    time += dist3.nextExponential(meanArrivalRate);
                    event.setTime(time);
                    events.addEvent(event);
                    event = new FlowDepartureEvent(id);
                    event.setTime(time + dist4.nextExponential(callsTypesInfo[type].getDeadline()));
                    events.addEvent(event);
                    id++;
                } else if (callsTypesInfo[i].getType() == 2) {
                    System.out.println("Chamadas para Batch: Src[i,...,[3-5]]---> dst");
                    type = weightVector[dist1.nextInt(TotalWeight)];
                    System.out.println(j);
                    int[] arrayCos = new int[this.transmitersNodes];
                    for(int t = 0;t < this.transmitersNodes;t++){
                        arrayCos[t] = callsTypesInfo[i].getWeight();
                    }
                    int dstFinal;
                    dstFinal = dist2.nextInt(numNodes);

                    Set<Integer> setSrcs = new HashSet<Integer>();
                    while (setSrcs.size() < this.transmitersNodes) {
                        int source = dist2.nextInt(numNodes);
                        setSrcs.add(new Integer(source));
                    }
                    boolean distintc = setSrcs.contains(dstFinal);
                    if (distintc == true) {
                        dstFinal = dist2.nextInt(numNodes);
                    }
                    Object[] arraySrcs = setSrcs.toArray();
                    for (int ind = 0; ind < arraySrcs.length; ind++) {
                        System.out.println("Source : Src[ " + ind + " ] = " + arraySrcs[ind]);//TEST
                    }
                    int[] grupoSrc = new int[this.transmitersNodes];
                    for (int g = 0; g < grupoSrc.length; g++) {
                        grupoSrc[g] = (int) arraySrcs[g];
                    }
                    System.out.println("Destino da ressincronização : ---> " + dstFinal);//TEST

                    int variation = 0;
                    Random gerador = new Random();
                    arrayIds = new long[this.transmitersNodes + 1];
                    arrayVariations = new int[this.transmitersNodes];
                    for (int ids = 0; ids < this.transmitersNodes; ids++) {
                        arrayIds[ids] = id++;
                        int number = gerador.nextInt(2 * delta);
                        variation = callsTypesInfo[type].getDataAmount() + (number - delta);
                        arrayVariations[i] = variation;
                    }
                    event = new BatchArrivalEvent(new Batch(arrayIds, grupoSrc, dstFinal, arrayVariations, callsTypesInfo[type].getDeadline(), arrayCos));
                    System.out.println("@@@@  Evento BatchArrival: " +event);
                    time += dist3.nextExponential(meanArrivalRate);
                    event.setTime(time);
                    events.addEvent(event);
                    for (int g = 0; g < this.transmitersNodes; g++) {
                        event = new FlowDepartureEvent(id);
                        event.setTime(time + dist4.nextExponential(callsTypesInfo[type].getDeadline()));
                        events.addEvent(event);
                    }
                }

            }

        }
    }

}
