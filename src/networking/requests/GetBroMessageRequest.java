package networking.requests;

import networking.BroMessage;
import networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;


public class GetBroMessageRequest {

    ServerRequest serverRequest;
    String token;
    String messageId;

    public GetBroMessageRequest(ServerRequest request) {
        serverRequest = request;

        ArrayList<byte[]> blocks = DataMessage.getBlocks(serverRequest.getRequestBytes());

        token = new String(blocks.get(0));
        messageId = new String(blocks.get(1));

    }

    public String getToken() {
        return token;
    }
    public String getMessageId() {
        return messageId;
    }

    public static byte[] createMessage(String token, String messageId) throws IOException {

        ArrayList<byte[]> data = new ArrayList<>();
        data.add(token.getBytes());
        data.add(messageId.getBytes());

        return ServerRequest.createMessage(DataMessage.createBlocks(data), ServerRequest.ServerRequestType.SendBroMessage);
    }


}
