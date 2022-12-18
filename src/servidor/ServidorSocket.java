package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import myevents.ClientEvent;
import myevents.ClientEventListener;
import myevents.VentanaServidorEvent;
import myevents.VentanaServidorEventListener;

/**
 *
 * @author Programacion
 */
public class ServidorSocket implements ClientEventListener{
    protected ServerSocket server;
    protected int puerto;
    protected ClientListener clientListener;
    protected LinkedList<ClientSocket> clients;
    protected ClientPing stillConnected;
    private static ArrayList listeners; //PARA LA INTERFACE DEL SERVIDOR
    
    public ServidorSocket(int puerto){
        this.puerto = puerto;
        this.clients = new LinkedList<>();
        this.listeners = new ArrayList();
        this.stillConnected = new ClientPing(clients);
    }
    
    public void startServer(){
        try {
            this.server = new ServerSocket(this.puerto);
            this.clientListener = new ClientListener(server);
            this.clientListener.addEventListener(this);
            this.clientListener.start();
            this.stillConnected.start();
            System.out.println("Se inicio el Servidor...");
        } catch (Exception ex) {
            System.err.println("ServidorSocket Constructor Error: " + ex.getMessage());
        }
    }
    
    public void stopServer(){
        try {
            stopClients();
            this.clientListener.stopClientListener();
            this.server.close();
            System.out.println("El Servidor se detuvo...");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void stopClients() {
        for (ClientSocket client : this.clients) {
            client.stop();
        }
        this.clients.clear();
    }
    
    @Override
    public void onConnected(ClientEvent ev) {
        this.triggerOnConnected(ev.getClientSocket().getClientName());
        ev.getClientSocket().addEventListener(this);
        ev.getClientSocket().start();
        this.clients.add(ev.getClientSocket());
    }

    @Override
    public void onDesconnected(ClientEvent ev) {
        this.triggerOnDesconnected(ev.getClientSocket().getClientName());
        for (int i = 0; i < this.clients.size(); i++) {
            String name = this.clients.get(i).getClientName();
            if (ev.getClientSocket().getClientName().equalsIgnoreCase(name)) {
                this.clients.get(i).stop();
                this.clients.remove(i);
            }
        }
    }

    @Override
    public void onRead(ClientEvent ev) {
        this.triggerOnRead(ev.getClientSocket().getClientName(), ev.getText());
    }
    
    public void sedMessage(ClientSocket client, String msg){
        try {
            SendMessage send = new SendMessage();
            send.sendMessage(client.getClient().getOutputStream(), msg);
        } catch (IOException ex) {
            System.err.println("ServidorSocket sedMessage Method Error: " + ex.getMessage());
        }
    }
    
    public void sedMessageAll(String msg){
        SendMessage send = new SendMessage();
        send.setClients(clients, msg);
        send.start();
    }
    
    //DISPARADORES PARA LA INTERFACE--------------------------------------------
    
    public void addEventListener(VentanaServidorEventListener listener){
        this.listeners.add(listener);
    }
    
    public void triggerOnConnected(String clientName){
        ListIterator li = this.listeners.listIterator();
        while (li.hasNext()) {
            VentanaServidorEventListener listener = (VentanaServidorEventListener) li.next();
            VentanaServidorEvent clientEvent = new VentanaServidorEvent(this, clientName);
            (listener).onConnected(clientEvent);
        }
    }
    
    public void triggerOnDesconnected(String clientName){
        ListIterator li = this.listeners.listIterator();
        while (li.hasNext()) {
            VentanaServidorEventListener listener = (VentanaServidorEventListener) li.next();
            VentanaServidorEvent clientEvent = new VentanaServidorEvent(this, clientName);
            (listener).onDesconnected(clientEvent);
        }
    }
    
    public void triggerOnRead(String clientName, String msg){
        ListIterator li = this.listeners.listIterator();
        while (li.hasNext()) {
            VentanaServidorEventListener listener = (VentanaServidorEventListener) li.next();
            VentanaServidorEvent clientEvent = new VentanaServidorEvent(this, clientName);
            clientEvent.setMsg(msg);
            (listener).onRead(clientEvent);
        }
    }
    

}
