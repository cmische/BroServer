package objects;

import networking.BroMessage;

import java.io.Serializable;

/**
 * Created by Trevor on 12/3/15.
 */
public class Message extends BroMessage implements Serializable {
    public String messageID;

    public Message () {
    }

    public Message (BroMessage message, String messageID) {
        super(message.messageTitle, message.messageDetails, message.audioBytes);
        this.messageID = messageID;
    }
}
