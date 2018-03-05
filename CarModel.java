
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.net.*;

public class CarModel extends JFrame implements Runnable {
    public static final long serialVersionUID = 2L;
    public static void main ( String[] args ) throws SocketException {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() { new CarModel(); }
        } );
    }

    DatagramPanel receive = new DatagramPanel();
    public CarModel() {
        super("Car Model");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel( );
        content.setLayout( new BoxLayout( content, BoxLayout.Y_AXIS) );

        try {
            receive.setAddress(InetAddress.getLocalHost().getHostAddress(), false);
           receive.setPort(65200, false);
        }catch(UnknownHostException e){
            System.err.println(e.getMessage());
        }
        content.add(receive);

        JPanel debug = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        debug.add(new JLabel("throttle"));
        throttle = new JTextField("###.##%");
        throttle.setEditable(false);
        debug.add(throttle);
        debug.add(new JLabel("velocity"));
        velocity = new JTextField("100.00m/s");
        velocity.setEditable(false);
        debug.add(velocity);
        debug.add(new JLabel("Speed"));
        speed = new JTextField("99900.##mph");
        speed.setEditable(false);
        debug.add(speed);
        content.add(debug);

        this.setContentPane(content);
        this.pack();
        this.setVisible(true);

        (new javax.swing.Timer(dt,new CarDynamics())).start();
        /* start thread that handles comminications */
        (new Thread(this)).start();
    }


    public void run() {
        try{
        /* set up socket for reception */
            SocketAddress address = receive.getSocketAddress();
            DatagramSocket socket = new DatagramSocket(address);

            while(true) {
                try{
                    /* start with fresh datagram packet */
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive( packet );
                    /* extract message and pick appart into
                       lines and key:value pairs
                    */
                    String message = new String(packet.getData());
                    String[] lines = message.trim().split("\n");
                    String[] pair  = lines[0].split(":");

                    switch( pair[0] ) {/*<-- Java now lets you do switches on strings :-) */
                        case "speed":
                            if( pair[1].equals("?")){
                                String reply = String.format("speed:%5.2f\n",mph);
                                packet.setData(reply.getBytes());
                                socket.send(packet);
                            }
                        break;
                        case "throttle":
                            double setting = Double.parseDouble(pair[1]);
                            th = setting;
                            throttle.setText(String.format("%5.2f%%",th*100));
                        break;
                    }
                }catch(IOException e){
                    System.err.println(e.getMessage());
                }
            }
        }catch(SocketException e){
            System.err.println(e.getMessage());
        }
    }

    JTextField velocity;
    JTextField speed;
    JTextField throttle;
    final int dt = 100; /*ms*/
    double v = 0.0 ;  /* velocity ms-1 */
    double u = 0.05;  /* drag constant */
    double Tq= 0.0;   /* Engine output */
    double th=0.0;    /* Throttle setting [0..1] */
    double mph;
    class CarDynamics implements ActionListener {
        public void actionPerformed(ActionEvent t) {
            double Tq = 100*(th);
            double drag = u*v*v;
            double dv = (Tq-drag)*(float)dt/1000.0;
            v += dv;
            velocity.setText(String.format("%5.2fm/s",v));
            mph = v*2.23694;
            speed.setText(String.format("%5.2fmph",mph));
            throttle.setText(String.format("%5.2f%%",th*100));
        }
    }

}
