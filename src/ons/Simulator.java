/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ons;

import java.io.File;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Centralizes the simulation execution. Defines what the command line
 * arguments do, and extracts the simulation information from the XML file.
 * 
 * @author andred
 */
public class Simulator {

    private static String simName;
    private static final Float simVersion = new Float(0.3);
    public static boolean verbose = false;
    public static boolean trace = false;
    
    /**
     * Executes simulation based on the given XML file and the used command line arguments.
     * 
     * @param simConfigFile name of the XML file that contains all information about the simulation
     * @param trace activates the Tracer class functionalities
     * @param verbose activates the printing of information about the simulation, on runtime, for debugging purposes
     * @param load range of loads for which several simulations are automated; if not specified, load is taken from the XML file
     * @param seed a number in the interval [1,25] that defines up to 25 different random simulations
     */
    public void Execute(String simConfigFile, boolean trace, boolean verbose, double load, int seed) {

        Simulator.verbose = verbose;
        Simulator.trace = trace;

        if (Simulator.verbose) {
            System.out.println("########################################################");
            System.out.println("# Optical Networks Simulator - version " + simVersion.toString() + "  #");
            System.out.println("#######################################################\n");
        }

        try {

            long begin = System.currentTimeMillis();

            if (Simulator.verbose) {
                System.out.println("(0) Accessing simulation file " + simConfigFile + "...");
            }
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(simConfigFile));

            // normalize text representation
            doc.getDocumentElement().normalize();

            // check the root TAG name and version
            int simType = -1;
            simName = doc.getDocumentElement().getTagName();
            System.out.println("Valor do simName antes do switch: "+simName);//TEST
            switch (simName) {
                case "wdmsim":
                    if(Simulator.verbose)
                        System.out.println("Simulation type: " + simName + " (Fixed)");
                    simType = 0;
                    System.out.println("**** simType = 0 ------> wdmsim");//TEST
                    break;
                case "eonsim":
                    System.out.println("Valor do simulador: "+Simulator.verbose);
                    if(Simulator.verbose)
                        System.out.println("Simulation type: " + simName + " (Elastic)");
                    simType = 1;
                    System.out.println("**** simType = 1 ------> eonsim");//TEST
                    break;
                default:
                    System.out.println("Root element of the simulation file is " + doc.getDocumentElement().getNodeName() + ", eonsim or wdmsim is expected!");
                    System.exit(0);
            }
            
            if (!doc.getDocumentElement().hasAttribute("version")) {
                System.out.println("Cannot find version attribute!");
                System.exit(0);
            }
            if (Float.compare(new Float(doc.getDocumentElement().getAttribute("version")), simVersion) > 0) {
                System.out.println("Simulation config file requires a newer version of the simulator!");
                System.exit(0);
            }
            if (Simulator.verbose) {
                System.out.println("(0) Done. (" + Float.toString((float) ((float) (System.currentTimeMillis() - begin) / (float) 1000)) + " sec)\n");
            }

            /*
             * Extract physical topology part
             */
            begin = System.currentTimeMillis();
            if (Simulator.verbose) {
                System.out.println("(1) Loading physical topology information...");
            }
            
            PhysicalTopology pt;
            if (simType == 0){
            	pt = new WDMPhysicalTopology((Element) doc.getElementsByTagName("physical-topology").item(0));
                System.out.println("pt simType=0 para wdm");
            } else{
            	pt = new EONPhysicalTopology((Element) doc.getElementsByTagName("physical-topology").item(0));
                System.out.println("***++++ pt simType=1 para eon ..nova pt instanciada para eon");
            }
            
            if (Simulator.verbose) {
                System.out.println(pt);
            }

            if (Simulator.verbose) {
                System.out.println("(1) Done. (" + Float.toString((float) ((float) (System.currentTimeMillis() - begin) / (float) 1000)) + " sec)\n");
            }

            /*
             * Extract virtual topology part
             */
            begin = System.currentTimeMillis();
            if (Simulator.verbose) {
                System.out.println("(2) Loading virtual topology information...");
            }

            VirtualTopology vt = new VirtualTopology((Element) doc.getElementsByTagName("virtual-topology").item(0), pt);
            		
            if (Simulator.verbose) {
                System.out.println(vt);
            }

            if (Simulator.verbose) {
                System.out.println("(2) Done. (" + Float.toString((float) ((float) (System.currentTimeMillis() - begin) / (float) 1000)) + " sec)\n");
            }

            /*
             * Extract simulation traffic part
             */
            begin = System.currentTimeMillis();
            if (Simulator.verbose) {
                System.out.println("(3) Loading traffic information...");
            }

            EventScheduler events = new EventScheduler();
            TrafficGenerator traffic = new TrafficGenerator((Element) doc.getElementsByTagName("traffic").item(0), load);
            traffic.generateTraffic(pt, events, seed);

            if (Simulator.verbose) {
                System.out.println("(3) Done. (" + Float.toString((float) ((float) (System.currentTimeMillis() - begin) / (float) 1000)) + " sec)\n");
            }

            /*
             * Extract simulation setup part
             */
            begin = System.currentTimeMillis();
            if (Simulator.verbose) {
                System.out.println("(4) Loading simulation setup information...");
            }

            MyStatistics st = MyStatistics.getMyStatisticsObject();
            st.statisticsSetup(pt.getNumNodes(), 3, 0);
            
            Tracer tr = Tracer.getTracerObject();
            if (Simulator.trace == true)
            {
            	if (load == 0) {
                	tr.setTraceFile(simConfigFile.substring(0, simConfigFile.length() - 4) + ".trace");
            	} else {
                	tr.setTraceFile(simConfigFile.substring(0, simConfigFile.length() - 4) + "_Load_" + Double.toString(load) + ".trace");
            	}
            }
            tr.toogleTraceWriting(Simulator.trace);
            
            String raModule;
            raModule = "ons.ra." + ((Element) doc.getElementsByTagName("ra").item(0)).getAttribute("module");
            //raModule =(Element) doc.getElementsByTagName("ra").item(0).getAttributes(");
            System.out.println("Impressão do raModule: "+raModule);
            if (Simulator.verbose) {
                System.out.println("RA module: " + raModule);
            }
            ControlPlane cp = new ControlPlane(raModule, pt, vt);

            if (Simulator.verbose) {
                System.out.println("(4) Done. (" + Float.toString((float) ((System.currentTimeMillis() - begin) / (float) 1000)) + " sec)\n");
            }

            /*
             * Run the simulation
             */
            begin = System.currentTimeMillis();
            if (Simulator.verbose) {
                System.out.println("(5) Running the simulation...");
            }

            SimulationRunner sim = new SimulationRunner(cp, events);

            if (Simulator.verbose) {
                System.out.println("(5) Done. (" + Float.toString((float) ((float) (System.currentTimeMillis() - begin) / (float) 1000)) + " sec)\n");
            }

            if (Simulator.verbose) {
                if (load == 0) {
                    System.out.println("Statistics (" + simConfigFile + "):\n");
                } else {
                    System.out.println("Statistics for " + Double.toString(load) + " erlangs (" + simConfigFile + "):\n");
                }
                System.out.println(st.fancyStatistics());
            } else {
                System.out.println("*****");
                if (load != 0) {
                    System.out.println("Load:" + Double.toString(load));
                }
                st.printStatistics();
            }
            
            // Terminate MyStatistics singleton
            st.finish();

            // Flush and close the trace file and terminate the singleton
            if (Simulator.trace == true){
                tr.finish();
            }
            
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
  
