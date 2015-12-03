package server;


import java.io.*;
import java.net.*;

public class GCMThread extends Thread {

    Socket socket = null;
    int serverPort;
    String toNumber;
    String messageID;

    public GCMThread(int port, String toNumber, String messageID) {
        super("GCMThread");
        serverPort = port;
        this.toNumber = toNumber;
        this.messageID = messageID;

    }

    public void run() {


        try {
            socket = new Socket(InetAddress.getByName("gcm-http.googleapis.com"),serverPort);
            System.out.println("Started GCM communication on port: " + serverPort);

            //write json string
            String jsonString = "{\"to\":" + "\"" + toNumber + "\", \"data\":\"[\"messageID\":\"" + messageID + "\"]\"}";

            //send post request
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            wr.write("POST " + "https://gcm-http.googleapis.com/gcm/send" + " HTTP/1.0\r\n");
            wr.write("Content-Length: " + jsonString.length() + "\r\n");
            wr.write("Content-Type: application/x-www-form-urlencoded;charset=UTF-8\r\n");
            wr.write("Authorization: key=AIzaSyAO39IewFjVE2Mdc4xq3et6j2w0lynoKM4\r\n");
            wr.write("\r\n");

            wr.write(jsonString);
            wr.flush();

            //read response
            BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
            wr.close();
            rd.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
