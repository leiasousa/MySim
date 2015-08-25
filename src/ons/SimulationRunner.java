/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ons;

/**
 * Simply runs the simulation, as long as there are events
 * scheduled to happen.
 * 
 * @author andred
 */
public class SimulationRunner {

    /**
     * Creates a new SimulationRunner object.
     * 
     * @param cp the the simulation's control plane
     * @param events the simulation's event scheduler
     */
	public SimulationRunner(ControlPlane cp, EventScheduler events) {
        Event event;
        Tracer tr = Tracer.getTracerObject();
        MyStatistics st = MyStatistics.getMyStatisticsObject();
        
//        events = new EventScheduler();
//
//        event = new FlowArrivalEvent(new Flow(0, 6, 11, 192, 0, 1));
//        event.setTime(0.1);
//        events.addEvent(event);
//        event = new FlowArrivalEvent(new Flow(1, 1, 2, 192, 0, 1));
//        event.setTime(0.1);
//        events.addEvent(event);
//        event = new FlowArrivalEvent(new Flow(2, 1, 2, 192, 0, 1));
//        event.setTime(0.1);
//        events.addEvent(event);
//        event = new FlowArrivalEvent(new Flow(3, 2, 3, 192, 0, 1));
//        event.setTime(0.1);
//        events.addEvent(event);
//        event = new FlowArrivalEvent(new Flow(4, 3, 4, 192, 0, 1));
//        event.setTime(0.1);
//        events.addEvent(event);
//        event = new FlowArrivalEvent(new Flow(5, 3, 4, 192, 0, 1));
//        event.setTime(0.1);
//        events.addEvent(event);
//        event = new FlowDepartureEvent(1);
//        event.setTime(0.2);
//        events.addEvent(event);
//        event = new FlowDepartureEvent(4);
//        event.setTime(0.2);
//        events.addEvent(event);
//        event = new FlowArrivalEvent(new Flow(6, 0, 4, 192, 0, 1));
//        event.setTime(0.3);
//        events.addEvent(event);
        
        while ((event = events.popEvent()) != null) {
            tr.add(event);
            st.addEvent(event);
            cp.newEvent(event);
        }
    }
}
