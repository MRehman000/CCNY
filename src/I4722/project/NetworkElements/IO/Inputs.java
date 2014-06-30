package I4722.project.NetworkElements.IO;

import I4722.project.NetworkElements.NetworkElement;
import I4722.project.NetworkElements.Packet;

/**
 * Created by codyboppert on 6/20/14.
 */
public class Inputs {

    public static abstract class Input<T> {
        public void accept(T data) { emit(convert(data)); }
        protected abstract Packet convert(T data);
        protected abstract void emit(Packet packet);
    }

    public static class StringInput extends Input<String> {

        protected Converters.StringConverter stringConverter;
        protected NetworkElement destination;

        public StringInput(NetworkElement destination, Converters.StringConverter stringConverter) {
            this.stringConverter = stringConverter;
            this.destination = destination;
        }

        protected Packet convert(String string) {
            return stringConverter.convert(string);
        }

        protected void emit(Packet packet) {
            destination.accept(packet);
        }
    }

}
