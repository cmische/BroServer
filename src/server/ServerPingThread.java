package server;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class ServerPingThread extends Thread {

    DatagramSocket pingSocket = null;
    int serverPort;

    public ServerPingThread(int port) {
        super("ServerPingThread");
        serverPort = port;

    }

    public void run() {


        try {
            pingSocket = new DatagramSocket(serverPort);
            System.out.println("Started UDP Socket Listening to Port: " + serverPort);

            while(true) {

                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                pingSocket.receive(packet);

                byte[] pingMessage = new byte[packet.getLength()];
                for(int i=0;i<pingMessage.length;i++) {
                    pingMessage[i] = packet.getData()[i];
                }

                String userUUID = new String(pingMessage);
                System.out.println("Received Ping From: " + userUUID);

//                try {
//                    BroServer.pingUser(userUUID, packet.getAddress().getHostAddress());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(pingSocket != null) {
                pingSocket.close();
            }
        }


    }

}
