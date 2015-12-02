package networking.requests;

import java.io.IOException;

/**
 * Created by closestudios on 11/23/15.
 */
public class GetBrosRequest {

    ServerRequest serverRequest;
    String token;

    public GetBrosRequest(ServerRequest request) {
        serverRequest = request;

        token = new String(serverRequest.getRequestBytes());

    }

    public String getToken() {
        return token;
    }

    public static byte[] createMessage(String token) throws IOException {
        return ServerRequest.createMessage(token.getBytes(), ServerRequest.ServerRequestType.GetBros);
    }
}
