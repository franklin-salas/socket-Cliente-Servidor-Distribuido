package clientInterface;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
import myevents.ClientEvent;
import myevents.ClientEventListener;
import myevents.clientListennerEvent;
import myevents.clientMsgListenner;
import servidor.ClientSocket;

/**
 *
 * @author Sergio_W
 */
public class ServidorListenner implements Runnable{
    private boolean state;
    private Socket client;
    private static ArrayList listeners;
    private Thread hilo;

    public ServidorListenner(Socket _client) {
        this.client = _client;
        this.state = true;
        this.listeners = new ArrayList();
    }
    
    public void start(){
        this.hilo = new Thread(this);
        this.hilo.start();
    }
    
    public void stop(){
        this.state = false;
        this.hilo.stop();
    }

    @Override
    public void run() {
        try {
            while (this.state) {
                DataInputStream flujoEntrada = new DataInputStream(client.getInputStream());
                String msg = flujoEntrada.readUTF();
                this.triggerReadMsg(msg);
                System.out.println(msg);
            }
        } catch (IOException ex) {
            this.triggerDesconnected(this);
        }finally{
            //Linea que se debe ejecutar siempre...
        }
    }
    
     public void addEventListener(clientMsgListenner listener){
        this.listeners.add(listener);
    }
    
    public void triggerReadMsg(String msg){
        ListIterator li = this.listeners.listIterator();
        while (li.hasNext()) {
            clientMsgListenner listener = (clientMsgListenner) li.next();
            clientListennerEvent clientEvent = new clientListennerEvent(this,this);
            clientEvent.setText(msg);
            (listener).onRead(clientEvent);
        }
    }
    
    public void triggerDesconnected(ServidorListenner client){
        ListIterator li = this.listeners.listIterator();
        while(li.hasNext()){
            clientMsgListenner listener = (clientMsgListenner) li.next();
            clientListennerEvent clientEvent = new clientListennerEvent(this, client);
            (listener).onDesconnected(clientEvent);
        }
    }
}
