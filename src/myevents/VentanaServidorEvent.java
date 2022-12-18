package myevents;

import java.util.EventObject;
import servidor.ClientSocket;

/**
 *
 * @author Sergio_W
 */
public class VentanaServidorEvent extends EventObject{
    private String name;
    private String msg;

    public VentanaServidorEvent(Object source, String _name) {
        super(source);
        this.name = _name;
    }
    
    public void setMsg(String _msg){
        this.msg = _msg;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getMsg(){
        return this.msg;
    }
}
