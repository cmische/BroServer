package networking.responses;

import networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class SignInResponse extends ErrorResponse {

    public SignInResponse() {
        super();
    }

    public String getToken() {
        ArrayList<byte[]> blocks = DataMessage.getBlocks(getDataBytes());
        return new String(blocks.get(0));
    }

    public String getBroName() {
        ArrayList<byte[]> blocks = DataMessage.getBlocks(getDataBytes());
        return new String(blocks.get(1));
    }

    public static byte[] createSuccessMessage(String token, String broName) throws IOException {
        ArrayList<byte[]> blocks = new ArrayList<>();

        blocks.add(token.getBytes());
        blocks.add(broName.getBytes());

        return createMessage(true, DataMessage.createBlocks(blocks));
    }



}
