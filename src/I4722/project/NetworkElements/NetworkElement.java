package I4722.project.NetworkElements;

/**
 * Created by codyboppert on 6/20/14.
 */
public abstract class NetworkElement {

    public void accept(Packet packet) { emit(packetOperations(packet)); }

    protected abstract Packet packetOperations(Packet packet);

    protected abstract void emit(Packet packet);

}
