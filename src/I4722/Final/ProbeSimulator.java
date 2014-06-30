package I4722.Final;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codyboppert on 5/26/14.
 */
public class ProbeSimulator {

    public static void main(String[] args) {
        int t = -1; // time (microseconds)
        int rf = 800; // size of cross-traffic packet
        int qf = 2; // constant q
        int size = 50; // size of probe packet
        boolean fin = false;
        int gi = 1000;
        int cprimes = 10; // bits ps
        int cprimer = 25;
        int cprimeprime = 5;
        int c = 3;
        int lambdaf = 2;

        Router b = new Router(cprimeprime, cprimer, c);
        Receiver r = new Receiver();
        Router a = new Router(cprimeprime, cprimes, c);
        Sender s = new Sender(gi, cprimes, size, a, r);
        Crosstraffic ct = new Crosstraffic(lambdaf, cprimeprime,new Packet(rf, cprimeprime, null), a);

        r.start();
        b.start();
        a.start();
        s.start();
        while (r.active) {
            r.tplusplus();
            b.tplusplus();
            a.tplusplus();
            s.tplusplus();
        }
    }

    private static class Packet {
        private int psize;
        private int rateofline; // bits per microsecond
        private Receiver r;
        public Packet(int size, int rateofline, Receiver r) {
            this.psize = size;
            this.rateofline = rateofline;
            this.r = r;
        }
        public int getSize() {
            return psize;
        }
        public int getRateofline() {
            return rateofline;
        }
        public Receiver getR()  {
            return  r;
        }
    }

    private static class Sender extends Thread {
        int gi;
        int cprime;
        Router a;
        int size;
        Packet p;
        Receiver r;
        int t = 0;

        public void tplusplus() {
            t++;
        }

        public Sender(int gi, int cprime, int size, Router a, Receiver r) {
            this.gi = gi;
            this.cprime = cprime;
            this.a = a;
            this.size = size;
            this.p = new Packet(size, cprime, r);
            this.r = r;
        }

        private void sendpacket(Router a, Packet p) {
            a.queuepacket(p);
        }

        public void run() {
            System.out.println("Sender Started");
            boolean sent = false;
            while (!sent) {
                if (t == 0) {
                    sendpacket(a, p);
                    System.out.println(t + ": Sent p1.");
                } else if (t > gi && !a.blocked && !sent) {
                    sendpacket(a, p);
                    sent = true;
                    System.out.println(t + ": Sent p2.");
                }
            }
        }
    }

    private static class Receiver extends Thread {
        int t1 = -1;
        int t2;
        boolean active = false;
        int t = 0;

        public void tplusplus() {
            t++;
        }

        public Receiver() {
        }

        public void receive(Packet p, String id) {
            if (t1 == -1) {
                t1 = t;
            } else {
                t2 = t;
                System.out.println(t + ": " + id + " arrived");
                System.out.println("g(O) = " + (t2 - t1));
                active = false;
            }
        }

        public void run() {
            System.out.println("Receiver started");
            active = true;
        }
    }

    private static class Crosstraffic extends Thread {
        int rate; // bytes per microsecond
        int line;
        Packet ctp; // size of each packet
        float delay;
        int timelastsent = 0;
        Router a;
        boolean active = false;
        int t = 0;

        public void tplusplus() {
            t++;
        }

        public Crosstraffic(int rate, int line, Packet p, Router a) {
            this.rate = rate;
            this.ctp = p;
            delay = (p.getSize()/(float) rate) - (p.getSize()/(float) line);
            this.a = a;
            this.line = line;
        }

        public void run() {
            System.out.println("Crosstraffic started");
            active = true;
            while(active) {
                if((t > timelastsent + delay) && !a.blocked) {
                    a.queuepacket(ctp);
                    timelastsent = t;
                    System.out.println(t + ": CT Packet Sent");
                }
            }
        }
    }

    private static class Router extends Thread {
        boolean blocked = false; //accepting another packet
        List<Packet> queue;
        boolean active = false;
        int lastsent = 0;
        Router r;
        int t = 0;

        public void tplusplus() {
            t++;
        }

        private Router(int ctline, int ioline, int rline) {
            queue = new ArrayList<>();
        }

        private void addrouter(Router r) {
            this.r = r;
        }

        private void sendpacket(Router b, Packet p) {
            b.queuepacket(p);
        }

        private void queuepacket(Packet p) {
            blocked = true;
            float tf = t + p.rateofline/p.psize;
            while (t < tf) {
            }
            queue.add(p);
            System.out.println(t + ": Packet queued");
        }

        public void run() {
            while (active) {
                if (queue.size() > 0 && lastsent + queue.get(0).getSize()/queue.get(0).rateofline > t) {
                    sendpacket(r, queue.get(0));
                    queue.remove(0);
                    lastsent = t;
                    System.out.println(t + ": Packet sent to b");
                }
            }
            this.stop();
        }
    }


}
