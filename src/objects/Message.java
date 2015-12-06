package objects;

import networking.BroMessage;

import java.io.Serializable;


public class Message extends BroMessage implements Serializable {
    public String messageID;

    public Message () {
    }

    public Message (BroMessage message, String messageID) {
        super(message.messageTitle, message.messageDetails, message.audioBytes, message.extension);
        this.messageID = messageID;
    }
}
