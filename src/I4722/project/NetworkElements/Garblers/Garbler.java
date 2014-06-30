package I4722.project.NetworkElements.Garblers;

import I4722.project.NetworkElements.NetworkElement;
import I4722.project.NetworkElements.Packet;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by codyboppert on 6/20/14.
 */
public class Garbler extends NetworkElement {

    private NetworkElement destination;
    private ArrayList<Integer> dropNumbers = new ArrayList<>();
    private ArrayList<Integer> extraDelayNumbers = new ArrayList<>();
    private DelayQueue<DelayedPacket> packetDelayQueue = new DelayQueue<>();
    private int delayTime = 0;
    private boolean drops = true;
    private boolean extraDelay = true;
    private int extraDelayTime = 0;
    private Random random = new Random(System.currentTimeMillis());
    private Random extraDelayRandom = new Random(System.currentTimeMillis());

    private int RANGE_FOR_RANDOM = 10000;

    // The garbler takes in a percentage of packets (out of RANGE_FOR_RANDOM) to drop and delay in milliseconds
    // As well as an additional extraDelay and percentage
    public Garbler(NetworkElement destination, Integer percentageToDrop, Integer delayTime,
                   Integer extraDelayTime, Integer extraDelayPercentage) {
        this.destination = destination;

        if (percentageToDrop == null) { percentageToDrop = 0; }
        if (extraDelayPercentage == null) { extraDelayPercentage = 0; }

        if (percentageToDrop + extraDelayPercentage > RANGE_FOR_RANDOM) {
            while (percentageToDrop + extraDelayPercentage > RANGE_FOR_RANDOM) {
                percentageToDrop = (int) Math.floor(percentageToDrop / 2);
                extraDelayPercentage = (int) Math.floor(extraDelayPercentage / 2);
            }
        }

        if (percentageToDrop > 0) {
            for (int i = 0; i < percentageToDrop; i++) {
                int number = random.nextInt(RANGE_FOR_RANDOM);
                while (dropNumbers.contains(number)) {
                    number = random.nextInt(RANGE_FOR_RANDOM);
                }
                dropNumbers.add(number);
            }
        } else {
            drops = false;
        }

        if (delayTime != null && delayTime > 0) {
            this.delayTime = delayTime;
        }

        if (extraDelayTime != null && extraDelayTime > 0 && extraDelayPercentage > 0) {
            this.extraDelayTime = extraDelayTime;
            for (int i = 0; i < extraDelayPercentage; i++) {
                int number = random.nextInt(RANGE_FOR_RANDOM);
                while(dropNumbers.contains(number) || extraDelayNumbers.contains(number)) {
                    number = random.nextInt(RANGE_FOR_RANDOM);
                }
                extraDelayNumbers.add(number);
            }
        } else {
            extraDelay = false;
        }
    }

    protected Packet packetOperations(Packet packet) {
        int packetDelayTime = 0;
        packetDelayTime += delayTime;

        int lottery = random.nextInt(RANGE_FOR_RANDOM);
        if (drops) {
            if (dropNumbers.contains(lottery)) {
                return packet;
            }
        }
        if (extraDelay) {
            if (extraDelayNumbers.contains(lottery)) {
                packetDelayTime += extraDelayRandom.nextInt(extraDelayTime);
            }
        }
        packetDelayQueue.add(new DelayedPacket(packet, packetDelayTime));
        return packet;
    }

    //Empty method in this case
    protected void emit(Packet packet) {}

    private class PacketEmitter implements Runnable {
        public void run() {
            while (true) {
                try {
                    destination.accept(packetDelayQueue.take().getPacket());
                } catch(InterruptedException e) {
                    System.out.println("Garbler Packet Emitter Thread Interruption: " + e);
                }
            }
        }
    }

    private class DelayedPacket implements Delayed {

        private long delay;
        private Packet packet;
        private long timeInitiated;

        public DelayedPacket(Packet packet, long delay) {
            timeInitiated = System.currentTimeMillis();
            this.packet = packet;
            //Take in milliseconds
            this.delay = delay;
        }

        public Packet getPacket() { return packet; }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(timeInitiated + delay - System.currentTimeMillis(), unit);
        }

        @Override
        public int compareTo(Delayed delayedPacket) {
            if (delay < delayedPacket.getDelay(TimeUnit.NANOSECONDS)) {
                return -1;
            } else if (delay > delayedPacket.getDelay(TimeUnit.NANOSECONDS)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
