package I4722.Final;

import java.util.*;

/**
 * Created by codyboppert on 5/26/14.
 */
public class ProbeSim {

    public static void main(String[] args) {
        NetworkElement sender = new NetworkElement("S", "Sender");
        NetworkElement routerA = new NetworkElement("a", "Router A");
        NetworkElement routerB = new NetworkElement("b", "Router B");
        NetworkElement receiver = new NetworkElement("R", "Receiver");
        NetworkElement ctrA = new NetworkElement("ca", "CTRA");
        NetworkElement ctrB = new NetworkElement("cb", "CTRB");

        TrafficSim trafficSim = new TrafficSim(ctrA, ctrB, 800, 2, "crosstraffic");

        sender.setupLine(10, routerA);
        routerA.setupLine(5, ctrA);
        routerB.setupLine(3, routerA);
        routerB.setupLine(5, ctrB);
        receiver.setupLine(25, routerB);

        sender.receivePacket(new Packet(50, "p1", routerB));
        sender.receivePacket(new Packet(50, "p2", routerB));

        TimeKeeper keeper = new TimeKeeper();
        keeper.start();
        trafficSim.start();
        receiver.start();
        ctrB.start();
        routerB.start();
        routerA.start();
        ctrA.start();
        sender.start();
        while (TimeKeeper.t < 15000) {
        }


    }

    private static class TimeKeeper extends Thread {
        static int t = -1; // Time (microseconds)

        public void run() {
            while(true) {
                t++;
                try {
                    TimeKeeper.sleep(1);
                } catch(InterruptedException e) {
                    System.out.println("TK Error: " + e);
                }
            }
        }
    }

    private static class Packet {
        int size;
        String id;
        String message;
        NetworkElement destination;

        public Packet(int size, String message, NetworkElement destination) {
            this.size = size;
            this.id = TimeKeeper.t + destination.id;
            this.message = message;
            this.destination = destination;
        }
    }

    private static class NetworkLine extends Thread {
        int bpus; // bits per microsecond
        Packet packet;
        NetworkElement origin;
        NetworkElement destination;

        public NetworkLine(int bpus, NetworkElement origin, NetworkElement destination) {
            this.bpus = bpus;
            this.origin = origin;
            this.destination = destination;
        }

        public void sendPacket() {
            destination.queue.add(packet);
            packet = null;
        }

        public void receivePacket(Packet p) {
            packet = p;
        }

        public void run() {
            while(true) {
                if (packet != null) {
                    int s = packet.size;
                    int t = TimeKeeper.t;
                    while (s > 0) {
                        s -= t * bpus;
                    }
                    sendPacket();
                    System.out.println(origin.id + TimeKeeper.t + ": Packet sent from " + origin.name + " to " + destination.name);
                }
            }
        }
    }

    private static class NetworkElement extends Thread {
        String id;
        String name;

        private Map<String, NetworkLine> network = new HashMap<>();
        List<NetworkLine> connections = new ArrayList<>();
        List<Packet> queue = Collections.synchronizedList(new ArrayList<>());
        List<Packet> backupQueue = Collections.synchronizedList(new ArrayList<Packet>());

        public NetworkElement(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public void receivePacket(Packet p) {
            System.out.println(this.id + TimeKeeper.t + ": Packet received at " + this.name + " with destination " + p.destination.name);
            if (p.destination.id.equals(this.id)) {
                System.out.println(this.id + TimeKeeper.t + ": Packet received at " + this.name);
                System.out.println(p.id + "Message: " + p.message);
            } else {
                synchronized (backupQueue) {
                    backupQueue.add(p);
                }
            }
        }

        public void setupLine(int bpus, NetworkElement d) {
            NetworkLine line = new NetworkLine(bpus, this, d);
            line.start();
            connections.add(line);
            d.connections.add(line);
            System.out.println(this.id + TimeKeeper.t + ": Line setup between " + this.name + " and " + d.name);
        }

        public void discoverNetwork() {
            List<NetworkLine> checked = new ArrayList<>();
            for (NetworkLine line : connections) {
                checked.add(line);
                networkMapper(line, line, checked);
            }
        }

        public void networkMapper(NetworkLine lineForMap, NetworkLine lineToFollow, List<NetworkLine> checked) {
            NetworkElement destination = lineToFollow.destination;
            if (!network.containsKey(destination)) network.put(destination.id, lineForMap);
            for (NetworkLine l : destination.connections) {
                if (!checked.contains(l)) {
                    checked.add(l);
                    networkMapper(lineForMap, l, checked);
                }
            }
        }

        public Map<String, NetworkLine> getNetwork() {
            return network;
        }

        public void setNetwork(Map<String, NetworkLine> network) {
            this.network = network;
        }

        public void run() {
            System.out.println(this.id + TimeKeeper.t + ": Starting " + this.name);
            discoverNetwork();
            while(true) {
                if (TimeKeeper.t % 10000 == 0) {
                    discoverNetwork();
                }
                if (queue.size() > 0) {
                    for (Packet p : queue) {
                        String d = p.destination.id;
                        if (network.containsKey(d)) {
                            NetworkLine route = network.get(d);
                            if (route != null) {
                                if (route.packet == null) {
                                    route.packet = p;
                                    queue.remove(p);
                                    System.out.println(this.id + TimeKeeper.t + ": Packet at " + this.name + " on its way to "
                                            + p.destination.name + " currently en route to " + route.destination.name);
                                }
                            }
                        }
                    }
                }
                synchronized (backupQueue) {
                    for (Packet packet : backupQueue) {
                        synchronized (queue) {
                            queue.add(packet);
                        }
                        backupQueue.remove(packet);
                    }
                }
            }
        }
    }

//    private static class NetworkMapper extends Thread {
//        NetworkElement requester;
//        List<NetworkLine> connections;
//        Map<String, List<NetworkLine>> network;
//
//        public NetworkMapper(NetworkElement requester, List<NetworkLine> connections, Map<String, List<NetworkLine>> network) {
//            this.requester = requester;
//            this.connections = connections;
//            this.network = network;
//        }
//
//        public void run() {
//            for (NetworkLine l : connections) {
//                NetworkElement destination = l.destination;
//                if (!network.containsKey(destination.id)) {
//                    addConnection(new ArrayList<NetworkLine>(), l, destination.id);
//                }
//                Map<String, List<NetworkLine>> nw = l.destination.getNetwork();
//                for (String ne : nw.keySet()) {
//                    List<NetworkLine> route = nw.get(ne);
//                    if (network.containsKey(ne)) {
//                        if (route.size() + 1 < network.get(ne).size()) {
//                            addConnection(route, l, ne);
//                        }
//                    } else {
//                        addConnection(route, l, ne);
//                    }
//                }
//            }
//            requester.setNetwork(network);
//            System.out.println(requester.id + TimeKeeper.t + ": " + requester.name + " network update complete.");
//        }
//
//        public void addConnection(List<NetworkLine> route,  NetworkLine l, String ne) {
//            route.add(l);
//            network.put(ne, route);
//            System.out.print(requester.id + TimeKeeper.t+ ": Path between " + requester.name + " and " + ne + " to: " + requester.name);
//            for (NetworkLine line : route) {
//                System.out.print(" -> " + line.destination.name);
//            }
//            System.out.println();
//        }
//    }

    private static class TrafficSim extends Thread {
        NetworkElement origin;
        NetworkElement destination;
        int s;
        int rate;
        int last = TimeKeeper.t;
        String message;
        public TrafficSim(NetworkElement origin, NetworkElement destination, int s, int rate, String message) {
            this.origin = origin;
            this.destination = destination;
            this.s = s;
            this.message =  message;
            this.rate = rate;
        }

        public void run() {
            while(true) {
                int leftToSend = s;
                while (leftToSend > 0) {
                    leftToSend -= (TimeKeeper.t - last) * rate;
                }
                last = TimeKeeper.t;
                origin.receivePacket(new Packet(s, message, destination));
            }
        }
    }
}
