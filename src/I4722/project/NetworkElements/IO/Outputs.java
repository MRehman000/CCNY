package I4722.project.NetworkElements.IO;

import I4722.project.NetworkElements.Packet;

/**
 * Created by codyboppert on 6/20/14.
 */
public class Outputs {

    public static abstract class Output<T> {
        public void accept(Packet packet) {
            output(convert(packet));
        }
        protected abstract T convert(Packet packet);
        protected abstract void output(T data);
    }

    public static class StringOutput extends Output<String> {

        protected Converters.StringDeconverter stringDeconverter;

        public StringOutput(Converters.StringDeconverter stringConverter) {
            this.stringDeconverter = stringDeconverter;
        }

        protected String convert(Packet packet) {
            return stringDeconverter.convert(packet);
        }

        protected void output(String string) {
            System.out.println(string);
        }

    }
}
