package networking.requests;

import com.closestudios.bro.networking.BroLocation;
import com.closestudios.bro.networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class UpdateLocationRequest {

    ServerRequest serverRequest;
    String token;
    BroLocation broLocation;

    public UpdateLocationRequest(ServerRequest request) {
        serverRequest = request;

        ArrayList<byte[]> blocks = DataMessage.getBlocks(serverRequest.getRequestBytes());

        token = new String(blocks.get(0));
        broLocation = new BroLocation(blocks.get(1));

    }

    public String getToken() {
        return token;
    }
    public BroLocation getBroLocation() {
        return broLocation;
    }

    public static byte[] createMessage(String token, BroLocation location) throws IOException {

        ArrayList<byte[]> data = new ArrayList<>();
        data.add(token.getBytes());
        data.add(location.getBytes());

        return ServerRequest.createMessage(DataMessage.createBlocks(data), ServerRequest.ServerRequestType.UpdateLocation);
    }

}
