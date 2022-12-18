
package myevents;

import java.util.EventObject;
import servidor.ClientSocket;

/**
 *
 * @author Programacion
 */
public class ClientEvent extends EventObject{
    private ClientSocket client;
    private String text;
    public ClientEvent(Object o, ClientSocket _cliente) {
        super(o);
        this.client = _cliente;
    }
    
    public void setText(String _text){
        this.text = _text;
    }
    
    public ClientSocket getClientSocket(){
        return this.client;
    }
    
    public String getText(){
        return this.text;
    }
}
