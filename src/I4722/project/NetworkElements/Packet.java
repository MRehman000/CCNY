package I4722.project.NetworkElements;

/**
 * Created by codyboppert on 6/19/14.
 */
public class Packet {

    private byte[] packet;

    public Packet(byte[] header, byte[] body) {
        packet = new byte[header.length + body.length];
        setPacket(header, body);
    }

    public Packet(byte[] packet) {
        this.packet = packet;
    }

    public byte[] getPacket() { return packet; }

    public void setPacket(byte[] packet) {
        int index = 0;
        while (index < this.packet.length && index < packet.length) {
            this.packet[index] = packet[index];
            index++;
        }
    }

    public void setPacket(byte[] header, byte[] body) {
        int index = 0;
        while(index < header.length && index < packet.length) {
            packet[index] = header[index];
            index++;
        }
        int indexTwo = 0;
        while (indexTwo < body.length && index < packet.length) {
            packet[index] = body[indexTwo];
            index++;
            indexTwo++;
        }
    }

}
