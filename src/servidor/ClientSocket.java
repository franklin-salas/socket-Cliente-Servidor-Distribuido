package servidor;

import myevents.ClientEventListener;
import myevents.ClientEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author Sergio_W
 */
public class ClientSocket implements Runnable{
    private static ArrayList listeners;
    private Socket client;
    private String name;
    private boolean state;
    private Thread hilo;

    public ClientSocket(Socket client, boolean state, String _name) {
        this.client = client;
        this.state = state;
        this.name = _name;
        this.listeners = new ArrayList();
    }
    
    public Socket getClient(){
        return this.client;
    }
    
    public String getClientName(){
        return this.name;
    }
    
    public void start(){
        this.hilo = new Thread(this);
        this.hilo.start();
    }

    public void stop(){
        try {
            this.state = false;
            this.client.close();
            this.hilo.stop();
        } catch (IOException ex) {
            System.err.println("ClientSocket Stop Error: " + ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            while (this.state) {
                DataInputStream flujoEntrada = new DataInputStream(client.getInputStream());
                String msg = flujoEntrada.readUTF();
                this.triggerReadMsg(msg);
                System.out.println(msg);
                //DataOutputStream flujoSalida = new DataOutputStream(client.getOutputStream());
                //flujoSalida.writeUTF("Hola " + msg);
            }
        } catch (IOException ex) {
            this.triggerDesconnected(this);
        }finally{
            //Linea que se debe ejecutar siempre...
        }
    }
    
    public void addEventListener(ClientEventListener listener){
        this.listeners.add(listener);
    }
    
    public void triggerReadMsg(String msg){
        ListIterator li = this.listeners.listIterator();
        while (li.hasNext()) {
            ClientEventListener listener = (ClientEventListener) li.next();
            ClientEvent clientEvent = new ClientEvent(this,this);
            clientEvent.setText(msg);
            (listener).onRead(clientEvent);
        }
    }
    
    public void triggerDesconnected(ClientSocket client){
        ListIterator li = this.listeners.listIterator();
        while(li.hasNext()){
            ClientEventListener listener = (ClientEventListener) li.next();
            ClientEvent clientEvent = new ClientEvent(this, client);
            (listener).onDesconnected(clientEvent);
        }
    }
}
