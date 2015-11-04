package io.drakon.flightpath.example.simple;

import io.drakon.flightpath.Airdrop;
import io.drakon.flightpath.Flightpath;

/**
 * A dumb-and-simple demo of Flightpath.
 */
public class SimpleExample {

    public static void main(String[] args) {
        // Create test objects
        Responder r1 = new Responder("r1");
        Responder r2 = new Responder("r2");
        Responder r3 = new Responder("r3");

        // Make flightpath and send example events.
        Flightpath fp = new Flightpath();
        fp.register(r1);
        fp.register(r2);
        fp.register(r3);

        fp.post(new Event("Message 1"));
        fp.post(new Event("Message 2"));
    }

    private static class Responder {
        private final String name;

        public Responder(String name) { this.name = name; }

        @Airdrop
        public void boop(Event evt) {
            System.out.println(name + " >> " + evt.getMessage());
        }
    }

    private static class Event {
        private final String msg;

        public Event(String message) { this.msg = message; }

        public String getMessage() { return msg; }
    }

}
