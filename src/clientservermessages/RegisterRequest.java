package clientservermessages;

/**
 * Created by closestudios on 10/10/15.
 */
public class RegisterRequest {

    ServerRequest serverRequest;

    public RegisterRequest(ServerRequest request) {
        serverRequest = request;
    }

    public static byte[] createMessage() {
        return ServerRequest.createMessage("Register".getBytes(), ServerRequest.ServerRequestType.Register);
    }

}
