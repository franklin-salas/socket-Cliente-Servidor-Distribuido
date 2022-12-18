package clientInterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import myevents.clientListennerEvent;
import myevents.clientMsgListenner;

/**
 *
 * @author Sergio_W
 */
public class cliente extends JFrame implements ActionListener, clientMsgListenner{
    JButton btnConnect;
    JButton btnDesconnect;
    JButton btnenviar;
    JLabel lbEntrada;
    JLabel lbSalida;
    JLabel lbEstado;
    JTextField txtName;
    JTextField txtSend;
    JTextArea textBox;
    ServidorListenner listenner;
    Socket cli;

    public cliente(){
        super("Cliente");
        Init();
    }
    
    public void Init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.lbEstado = new JLabel("ESTADO");
        this.lbEstado.setBounds(100, 10, 200, 30);
        this.lbEstado.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(this.lbEstado);
        
        btnConnect = new JButton("Conectar");
        btnConnect.setBounds(10, 50, 150, 30);
        btnConnect.addActionListener(this);
        add(this.btnConnect);
        
        txtName = new JTextField();
        txtName.setBounds(170, 50, 150, 30);
        add(txtName);
        
        this.btnDesconnect = new JButton("Desconectarse...");
        this.btnDesconnect.setBounds(10, 90, 310, 30);
        this.btnDesconnect.addActionListener(this);
        this.btnDesconnect.setEnabled(false);
        add(this.btnDesconnect);
        
        this.lbEntrada = new JLabel("Recepcion de Mensaje:");
        this.lbEntrada.setBounds(10, 120, 200, 30);
        this.lbEntrada.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(this.lbEntrada);
        
        textBox = new JTextArea();
        textBox.setBounds(10, 160, 310, 100);
        add(this.textBox);
        
        this.lbSalida = new JLabel("Enviar Mesaje a Servidor: ");
        this.lbSalida.setBounds(10, 260, 200, 30);
        this.lbSalida.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(this.lbSalida);
        
        this.txtSend = new JTextField();
        this.txtSend.setBounds(10, 300, 310, 30);
        add(this.txtSend);
        
        btnenviar = new JButton();
        btnenviar.setText("Enviar");
        btnenviar.setBounds(10, 340, 310, 30);
        btnenviar.addActionListener(this);
        btnenviar.setEnabled(false);
        add(btnenviar);
        
        setLayout(null);
        setSize(350, 430);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new cliente();
    }
    
    public void conectar(){
        if (txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe Ingresar un Nombre de Cliente!!");
        }else{
            try {
                cli = new Socket("127.0.0.1", 9090);
                DataOutputStream flujo = new DataOutputStream(cli.getOutputStream());
                flujo.writeUTF(txtName.getText());
                super.setTitle(txtName.getText());
                this.lbEstado.setText("CONECTADO...");
                this.lbEstado.setForeground(Color.GREEN);
                this.listenner = new ServidorListenner(cli);
                this.listenner.addEventListener(this);
                this.listenner.start();
            } catch (IOException ex) {
                Logger.getLogger(cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.btnDesconnect.setEnabled(true);
            this.btnenviar.setEnabled(true);
            this.btnConnect.setEnabled(false);
        }
    }
    
    public void desconectar(){
        try {
            this.listenner.stop();
            this.cli.close();
            super.setTitle("Cliente");
            this.lbEstado.setText("DESCONECTADO!!!");
            this.lbEstado.setForeground(Color.RED);
        } catch (IOException ex) {
            System.err.println("Error al desconectar: " + ex.getMessage());
        }
        this.btnDesconnect.setEnabled(false);
        this.btnenviar.setEnabled(false);
        this.btnConnect.setEnabled(true);
    }

    public void sendMessage(){
        if (txtSend.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe Ingresar algun mensaje!!");
        }else{
            try {
                DataOutputStream flujo = new DataOutputStream(cli.getOutputStream());
                flujo.writeUTF(txtSend.getText());
            } catch (IOException ex) {
                Logger.getLogger(cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnenviar) {
            sendMessage();
        }else if (e.getSource() == btnConnect) {
            conectar();
        }else if (e.getSource() == btnDesconnect) {
            desconectar();
        }
    }

    @Override
    public void onRead(clientListennerEvent ev) {
        this.textBox.append("Servidor: " + ev.getText() + "\n");
    }

    @Override
    public void onDesconnected(clientListennerEvent ev) {
        super.setTitle("Cliente");
        this.lbEstado.setText("DESCONECTADO!!!");
        this.lbEstado.setForeground(Color.RED);
        this.btnConnect.setEnabled(true);
        this.btnDesconnect.setEnabled(false);
        this.btnenviar.setEnabled(false);
    }
}
