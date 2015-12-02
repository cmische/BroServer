package networking;

import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class ServerApi implements ServerApiCalls.NetworkCall{

    private static ServerApi instance;
    ArrayList<ServerApiCalls> apiCalls = new ArrayList<>();
    static String host;
    static int port;

    public ServerApi() {

    }

    public static ServerApi getApi() {
        if(instance == null) {
            instance = new ServerApi();
        }
        return instance;
    }

    public ServerApiCalls createNewRequest() {
        ServerApiCalls newApiCall = new NetworkServerApiCalls(this, host, port);
        apiCalls.add(newApiCall);
        return newApiCall;
    }


    @Override
    public void onComplete(ServerApiCalls serverApiCalls) {
        apiCalls.remove(serverApiCalls);
    }

}
