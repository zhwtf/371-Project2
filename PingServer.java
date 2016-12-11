/**
 * PingServer.java
 *
 * Login name: yilunq
 *
 * @Yilun Qian
 *
 */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

/**
 * Server to process ping requests over UDP
 */
public class PingServer {

    private static final int AVERAGE_DELAY = 100; // milliseconds

    public static void main(String[] args) throws Exception {
        // Get command line argument
        if (args.length != 2) {
            System.out.println("Required arguments: port loss_rate");
            return;
        }

        int port = Integer.parseInt(args[0]);
        double loss_rate = Double.parseDouble(args[1]);

        System.out.println("\n *** Ping Server is working on Port = "
            + port + " with loss rate = " + loss_rate);

        // Create random number generator for use in simulating
        // packet loss and network delay
        Random random = new Random();

        // Create a datagram socket for receiving and sending UDP packets
        // though the port specified on the command line
        DatagramSocket socket = new DatagramSocket(port);

        // Processing loop
        while (true) {
            // Create a data gram packet to hold incoming UDP packet
            DatagramPacket request = new DatagramPacket(new byte[1024], 1024);

            // Block until the host receives a UDP packet
            socket.receive(request);


            // Print the received data
            printData(request);

            // Decide whether to reply, or simulate packet loss
            double d = random.nextDouble();
            if (d < loss_rate) {
                System.out.println("    Reply not sent. " + d);
                continue;
            }


            // Simulate network delay
            Thread.sleep((int)(random.nextDouble() * 2 * AVERAGE_DELAY));

            // Send reply
            InetAddress clientHost = request.getAddress();

            int clientPort = request.getPort();
            byte[] buf = request.getData();
            DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
            socket.send(reply);

            System.out.println("    Reply sent.");
        }
    }

    /*
    * Print ping data to the standard output stream
     */
    private static void printData(DatagramPacket request) throws Exception{
        // Obtain references to the packet's array of bytes
        byte[] buf = request.getData();
        // Wrap the bytes in a byte array input stream,
        // so you can read the data as a stream of bytes
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        // Wrap the byte array output stream in an input stream reader,
        // so you can read the data as a stream of characters
        InputStreamReader isr = new InputStreamReader(bais);
        // Wrap the input stream reader in a buffered reader
        // so you can read the character data a line at a time
        // (A line is a sequence of chars terminated by any combination of \r and \n)
        BufferedReader br = new BufferedReader(isr);
        // The message data is contained in a single line, so read this line
        String line = br.readLine();
        // Print host address and data received from it
        System.out.println(
                "Received from "
                        + request.getAddress().getHostAddress() + ": "
                        + new String(line)
        );
    }
}
