package I4722.project.NetworkElements.IO;

import I4722.project.NetworkElements.Packet;

/**
 * Created by codyboppert on 6/29/14.
 */
public class Converters {

    //Converters
    public static interface PacketConverter<T> {
        Packet convert(T data);
    }

    public static class StringConverter implements PacketConverter<String> {
        public Packet convert(String string) {
            return new Packet(new byte[4]);
        }
    }

    //Deconverters
    public static interface PacketDeconverter<T> {
        T convert(Packet packet);
    }

    public class StringDeconverter implements PacketDeconverter<String> {
        public String convert(Packet packet) {

            return "";
        }
    }

}
