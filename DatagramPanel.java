
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

import java.net.*;
import java.io.*;

/* Place holder for ip-address and port number for internet addressing */
public class DatagramPanel extends JPanel {
    public static final long serialVersionUID = 2L;

    JTextField address;
    JTextField port;

    public DatagramPanel() {
        /* default dummy values */
        this("192.168.255.254", 65535);
    }

    public DatagramPanel(String address, int port) {
        /* create a JPanel populated with border and text fields */
        super( new FlowLayout( FlowLayout.LEFT, 5, 0));
        this.setBorder( BorderFactory.createTitledBorder("Socket Address"));
        this.add(new JLabel("IP:"));
        this.address = new JTextField(address);
        this.add(this.address);
        this.add(new JLabel("port:"));
        this.port = new JTextField(Integer.toString(port));
        this.add(this.port);
    }

    public void setAddress(String address){
        this.address.setText(address);
    }

    public void setAddress(String address, boolean edit){
        this.address.setText(address);
        this.address.setEditable(edit);
    }

    public void setPort(int port){
        this.port.setText(Integer.toString(port));
    }

    public void setPort(int port, boolean edit){
        this.port.setText(Integer.toString(port));
        this.port.setEditable(edit);
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(address.getText(),
                        Integer.parseInt(port.getText()));
    }

}
