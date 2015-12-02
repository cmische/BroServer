package client;

import com.ndsucsci.clientservermessages.SearchRequest;
import com.ndsucsci.clientservermessages.SearchResponse;
import com.ndsucsci.objects.SearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/10/15.
 */
public class ClientSearchThread extends Thread {

    Socket socket = null;
    String host;
    int port;
    String query;
    SearchCallback callback;

    public ClientSearchThread(String host, int port, String query, SearchCallback callback) {
        super("ClientSearchThread");
        this.host = host;
        this.port = port;
        this.query = query;
        this.callback = callback;
    }

    public interface SearchCallback {
        void searchResults(ArrayList<SearchResult> searchResults);
    }

    public void run() {

        // Print out what you are doing
        System.out.println("Search File");

        try {

            socket = new Socket(host, port);

            OutputStream outToServer = socket.getOutputStream();
            InputStream inFromServer = socket.getInputStream();

            // Send Register Message
            outToServer.write(SearchRequest.createMessage(query));
            outToServer.flush();

            // Get response
            SearchResponse searchResponse = new SearchResponse();
            searchResponse.getBytesFromInput(inFromServer);

            callback.searchResults(searchResponse.getSearchResults());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
