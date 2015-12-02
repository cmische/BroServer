package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by closestudios on 10/10/15.
 */
public class ClientPingThread extends Thread {

    DatagramSocket socket;
    String host;
    int port;
    String uuid;

    public ClientPingThread(String host, int port, String uuid) {
        super("ClientPingThread");
        this.host = host;
        this.port = port;
        this.uuid = uuid;
    }

    public void run() {

        // Print out what you are doing
        System.out.println("Ping Server");

        try {

            socket = new DatagramSocket();

            while(true) {
                byte[] uuidBytes = uuid.getBytes();
                InetAddress address = InetAddress.getByName(host);
                DatagramPacket packet = new DatagramPacket(uuidBytes, uuidBytes.length,
                        address, port);
                socket.send(packet);

                sleep(60*1000); // Ping every 60 seconds
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(socket != null) {
                socket.close();
            }
        }
    }

}
