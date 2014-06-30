package I4722.Final;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codyboppert on 5/26/14.
 */
public class gapCalc {
    int rf = 800; // bytes for cross-traffic packet
    int s = 75; // bytes for probe packet
    int t = 0; // time ms
    int c = 3; // bytes per us - c
    int cct = 5; // bytes per us - c'
    double lambda = 1.6; // bytes per us - lambda
    int gap = 1000; // g(I) us

    public static void main(String[] args) {
        new gapCalc();
    }

    public gapCalc() {
        List<Integer> packets = new ArrayList<>();
        packets.add(rf);
        packets.add(rf);
        packets.add(s);
        int arrivedOne = 0;
        int arrivedTwo = 0;
        int minDelta = 9999999;
        for (gap = 1000; gap <= 8000; gap += 500) {
            while (t < gap * 2) {

                if (t % Math.ceil(rf / lambda + rf / cct) == 0) {
                    packets.add(rf);
//                    System.out.println(t + " : " + rf);
                }

                if (t == gap) {
                    packets.add(s);
                    System.out.println(t + " : " + s + " added " + packets.size());
                }

                if (t != 0 && packets.size() > 0 && t % Math.ceil(packets.get(0) / c) == 0) {
                    int popped = packets.remove(0);
                    if (popped == s) {
                        if (arrivedOne == 0) {
                            arrivedOne = t;
                            System.out.println(t + " : " + popped + " " + packets.size());
                        } else {
                            arrivedTwo = t;
                            System.out.println(t + " : " + popped + " " + packets.size());
                        }
                    }
                }
                t++;
            }
            System.out.println("g(O) : " + (arrivedTwo - arrivedOne));
            System.out.println("deltag : " + (gap - (arrivedTwo - arrivedOne)));
            if (Math.abs(gap - (arrivedTwo - arrivedOne)) < minDelta) {
                minDelta = Math.abs(gap - (arrivedTwo - arrivedOne));
            }
            System.out.println("Bav " + (s * 2 * 1000.0/1000000 * Math.abs(gap - arrivedTwo + arrivedOne)));
            packets = new ArrayList<>();
            packets.add(rf);
            packets.add(rf);
            packets.add(s);
            arrivedOne = 0;
            arrivedTwo = 0;
            t = 0;
            System.out.println();
            System.out.println();

        }
    }

}
