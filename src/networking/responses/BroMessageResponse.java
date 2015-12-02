package networking.responses;

import networking.Bro;
import networking.BroMessage;
import networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;


public class BroMessageResponse extends ErrorResponse {

    public BroMessageResponse() {
        super();
    }

    public BroMessage getBroMessage() {
        return new BroMessage(getDataBytes());
    }

    public static byte[] createSuccessMessage(BroMessage broMessage) throws IOException {
        return createMessage(true, broMessage.getBytes());
    }



}
