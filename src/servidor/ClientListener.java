/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servidor;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
import myevents.ClientEvent;
import myevents.ClientEventListener;

/**
 *
 * @author Programacion
 */
public class ClientListener extends Thread{
    private static ArrayList listeners;
    private ServerSocket server;
    private boolean state;
    private Thread hilo;
    
    public ClientListener(ServerSocket server) {
        this.server = server;
        this.state = true;
        this.listeners = new ArrayList();
    }
    
    public void start(){
        this.hilo = new Thread(this);
        this.hilo.start();
    }
    
    public void stopClientListener(){
        this.hilo.interrupt();
        this.state = false;
    }
    
    @Override
    public void run() {
        while (this.state) {                
            try {
                Socket client;
                client = this.server.accept();
                DataInputStream flujoEntrada = new DataInputStream(client.getInputStream());
                String name = flujoEntrada.readUTF();
                this.triggerOnConnected(new ClientSocket(client, true, name));
            } catch (IOException ex) {
                break;
            }
        }
    }
    
    public void addEventListener(ClientEventListener listener){
        this.listeners.add(listener);
    }
    
    public void triggerOnConnected(ClientSocket client){
        ListIterator li = this.listeners.listIterator();
        while (li.hasNext()) {
            ClientEventListener listener = (ClientEventListener) li.next();
            ClientEvent clientEvent;
            clientEvent = new ClientEvent(this, client);
            (listener).onConnected(clientEvent);
        }
    }
}
