package myevents;

import clientInterface.ServidorListenner;
import java.util.EventObject;

/**
 *
 * @author Sergio_W
 */
public class clientListennerEvent extends EventObject{
    private ServidorListenner servidorListenner;
    private String text;

    public clientListennerEvent(Object source, ServidorListenner _servidorListenner) {
        super(source);
        this.servidorListenner = _servidorListenner;
    }

    public ServidorListenner getServidorListenner() {
        return servidorListenner;
    }

    public void setServidorListenner(ServidorListenner servidorListenner) {
        this.servidorListenner = servidorListenner;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}
