package I4722.project.NetworkElements;

import I4722.project.NetworkElements.IO.Inputs;

import java.io.BufferedReader;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by codyboppert on 6/20/14.
 */
public class Server<T> implements Runnable {

    Inputs.Input<T> input;
    List<NetworkElement> routeFromInput = new ArrayList<>();

    protected DatagramSocket socket;
    protected BufferedReader in;
    protected boolean moreDataToSend = true;
    //Arbitrarily choses port number
    protected final int PORT = 7878;


    // To build a server pass in an input with destination routeFromInput[0]
    // Each element in routeFromInput has destination n + 1 where n is index and n + 1 is the next element
    // The final elements destination is the client
    public Server(String name, Inputs.Input<T> input, List<NetworkElement> routeFromInput) {
        this.input = input;
        this.routeFromInput = routeFromInput;
    }

    public void startServer() {

    }

    public void send(T t) {
        input.accept(t);
    }

    @Override
    public void run() {
        new Thread(this).start();
        startServer();
    }
}
