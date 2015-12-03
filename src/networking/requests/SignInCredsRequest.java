package networking.requests;

import networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

public class SignInCredsRequest {


    ServerRequest serverRequest;
    String broName;
    String password;
    String gcmId;

    public SignInCredsRequest(ServerRequest request) {
        serverRequest = request;

        ArrayList<byte[]> blocks = DataMessage.getBlocks(serverRequest.getRequestBytes());
        broName = new String (blocks.get(0));
        password = new String (blocks.get(1));
        gcmId = new String (blocks.get(2));

    }

    public String getBroName() {
        return broName;
    }

    public String getPassword() {
        return password;
    }
    public String getGcmId() {
        return gcmId;
    }

    public static byte[] createMessage(String broName, String password, String gcmId) throws IOException {

        ArrayList<byte[]> signInBlocks = new ArrayList<>();

        signInBlocks.add(broName.getBytes());
        signInBlocks.add(password.getBytes());
        signInBlocks.add(gcmId.getBytes());

        return ServerRequest.createMessage(DataMessage.createBlocks(signInBlocks), ServerRequest.ServerRequestType.SignInCreds);
    }


}

