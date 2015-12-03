package networking.requests;

import networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class SignUpRequest {

    ServerRequest serverRequest;
    String broName;
    String password;
    String gcmId;

    public SignUpRequest(ServerRequest request) {
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

    public static byte[] createMessage(String broName, String password, String gcmID) throws IOException {

        ArrayList<byte[]> signUpBlocks = new ArrayList<>();

        signUpBlocks.add(broName.getBytes());
        signUpBlocks.add(password.getBytes());
        signUpBlocks.add(gcmID.getBytes());

        return ServerRequest.createMessage(DataMessage.createBlocks(signUpBlocks), ServerRequest.ServerRequestType.SignUp);
    }


}
