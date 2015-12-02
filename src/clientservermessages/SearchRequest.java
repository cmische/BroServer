package clientservermessages;

/**
 * Created by closestudios on 10/10/15.
 */
public class SearchRequest {

    ServerRequest serverRequest;

    public SearchRequest(ServerRequest request) {
        serverRequest = request;
    }

    public String getQuery() {
        return new String(serverRequest.getDataBytes());
    }

    public static byte[] createMessage(String query) {
        return ServerRequest.createMessage(query.getBytes(), ServerRequest.ServerRequestType.Search);
    }

}