package networking.requests;

import com.closestudios.bro.networking.BroLocation;
import com.closestudios.bro.networking.BroMessage;
import com.closestudios.bro.networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 12/1/15.
 */
public class SendBroMessageRequest {

    ServerRequest serverRequest;
    String token;
    String broName;
    BroMessage broMessage;

    public SendBroMessageRequest(ServerRequest request) {
        serverRequest = request;

        ArrayList<byte[]> blocks = DataMessage.getBlocks(serverRequest.getRequestBytes());

        token = new String(blocks.get(0));
        broName = new String(blocks.get(1));
        broMessage = new BroMessage(blocks.get(2));

    }

    public String getToken() {
        return token;
    }
    public String getBroName() {
        return broName;
    }
    public BroMessage getBroMessage() {
        return broMessage;
    }

    public static byte[] createMessage(String token, String broName, BroMessage message) throws IOException {

        ArrayList<byte[]> data = new ArrayList<>();
        data.add(token.getBytes());
        data.add(broName.getBytes());
        data.add(message.getBytes());

        return ServerRequest.createMessage(DataMessage.createBlocks(data), ServerRequest.ServerRequestType.SendBroMessage);
    }


}
