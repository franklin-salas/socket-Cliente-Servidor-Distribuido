package servidor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 *
 * @author Sergio_W
 */
public class SendMessage implements Runnable{
    private DataOutputStream msg;
    private LinkedList<ClientSocket> clients;
    private Thread hilo;
    private String msgAll;
    
    public void setClients(LinkedList<ClientSocket> _clients, String _msg){
        this.clients = _clients;
        this.msgAll = _msg;
    }
    
    public void start(){
        this.hilo = new Thread(this);
        this.hilo.start();
    }

    public void sendMessage(OutputStream client, String _msg){
        try {
            this.msg = new DataOutputStream(client);
            this.msg.writeUTF(_msg);
        } catch (IOException ex) {
            System.err.println("SendMessage Error: " + ex.getMessage());
        }
    }
    
    @Override
    public void run() {
        for (ClientSocket client : clients) {
            try {
                sendMessage(client.getClient().getOutputStream(), msgAll);
            } catch (IOException ex) {
                System.err.println("SendMessage to All Error: " + ex.getMessage());
            }
        }
        this.hilo.stop();
    }

}
