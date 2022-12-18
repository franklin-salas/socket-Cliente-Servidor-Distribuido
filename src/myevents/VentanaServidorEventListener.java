package myevents;

/**
 *
 * @author Sergio_W
 */
public interface VentanaServidorEventListener {
    public abstract void onConnected(VentanaServidorEvent ev);
    public abstract void onDesconnected(VentanaServidorEvent ev);
    public abstract void onRead(VentanaServidorEvent ev);
}
