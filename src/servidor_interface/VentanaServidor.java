package servidor_interface;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import myevents.VentanaServidorEvent;
import myevents.VentanaServidorEventListener;
import servidor.ServidorSocket;

public class VentanaServidor extends JFrame implements ActionListener{
    JButton btnIniciar;
    JButton btnFinalizar;
    JButton btnSendAll;
    JTextField nroPuerto;
    JTextArea textBox;
    JLabel lbEstado;
    JLabel lbMsg;
    JLabel lbConnected;
    JList list;
    DefaultListModel client; //Para el JList
    ServidorSocket server;
    VentanaServidorEventListener event;
    int nroConnected;

    public VentanaServidor() {
        super("Servidor Socket");
        this.nroConnected = 0;
        Init();
        this.server = new ServidorSocket(Integer.parseInt(this.nroPuerto.getText()));
        this.server.addEventListener(event);
    }
    
    private void Init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.lbEstado = new JLabel("ESTADO");
        this.lbEstado.setBounds(100, 10, 200, 30);
        this.lbEstado.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(this.lbEstado);
        
        this.btnIniciar = new JButton("Iniciar");
        this.btnIniciar.setBounds(10, 40, 150, 30);
        this.btnIniciar.addActionListener(this);
        add(this.btnIniciar);
        
        this.nroPuerto = new JTextField("9090");
        this.nroPuerto.setBounds(170, 40, 150, 30);
        add(this.nroPuerto);
        
        this.btnFinalizar = new JButton("Finalizar");
        this.btnFinalizar.setBounds(10, 80, 310, 30);
        this.btnFinalizar.addActionListener(this);
        this.btnFinalizar.setEnabled(false);
        add(this.btnFinalizar);
        
        this.lbConnected = new JLabel("Conectados: 0");
        this.lbConnected.setBounds(30, 120, 100, 30);
        this.lbConnected.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(this.lbConnected);
        
        this.list = new JList();
        this.client = new DefaultListModel();
        this.list.setModel(this.client);
        this.list.setBounds(10, 160, 310, 200);
        add(this.list);
        
        this.lbMsg = new JLabel("Cuadro de Mensajes:");
        this.lbMsg.setBounds(30, 360, 300, 30);
        this.lbMsg.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(this.lbMsg);
        
        this.textBox = new JTextArea();
        this.textBox.setBounds(10, 400, 310, 100);
        add(this.textBox);
        
        this.btnSendAll = new JButton("Enviar Mensaje a Todos");
        this.btnSendAll.setBounds(10, 510, 310, 30);
        this.btnSendAll.addActionListener(this);
        this.btnSendAll.setEnabled(false);
        add(this.btnSendAll);
        
        setLayout(null);
        setSize(350,590);
        setVisible(true);
               
        this.event = new VentanaServidorEventListener() {
            @Override
            public void onConnected(VentanaServidorEvent ev) {
                client.addElement(ev.getName());
                nroConnected += 1;
                lbConnected.setText("Conectados: " + nroConnected);
            }
            
            @Override
            public void onDesconnected(VentanaServidorEvent ev){
                nroConnected -= 1;
                lbConnected.setText("Conectados: " + nroConnected);
                for (int i = 0; i < client.size(); i++) {
                    String name = client.get(i).toString();
                    if (name.equalsIgnoreCase(ev.getName())) {
                        client.remove(i);
                        break;
                    }
                }
            }
                    
            @Override
            public void onRead(VentanaServidorEvent ev) {
                textBox.setText("");
                textBox.append(ev.getName() + ": " + ev.getMsg());
            }
        };
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.btnIniciar) {
            this.server.startServer();
            lbEstado.setText("SERVIDOR INICIADO...");
            lbEstado.setForeground(Color.GREEN);
            btnIniciar.setEnabled(false);
            btnFinalizar.setEnabled(true);
            btnSendAll.setEnabled(true);
        }else if (e.getSource() == this.btnFinalizar) {
            this.server.stopServer();
            lbEstado.setText("SERVIDOR DETENIDO!!!");
            lbEstado.setForeground(Color.RED);
            btnIniciar.setEnabled(true);
            btnFinalizar.setEnabled(false);
            btnSendAll.setEnabled(false);
        }else if (e.getSource() == this.btnSendAll) {
            this.server.sedMessageAll(this.textBox.getText());
        }
        
    }
}
