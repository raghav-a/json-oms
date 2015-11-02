package olympus.json.message.builder;

import olympus.common.JID;
import olympus.message.types.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageBuilder<T extends Message.MessagePayload, D extends MessageBuilder> {

    private JID to;
    private JID from;
    private List<Message.MessagePayload> payloads = new ArrayList<>();
    private String id;
    private String type;
    private Map<String, String> attributes;
    private String socketID;

    public D to(JID jid) {
        this.to = jid;
        return (D)this;
    }

    public D socketId(String socketID) {
        this.socketID = socketID;
        return (D)this;
    }


    public D from(JID jid) {
        this.from = jid;
        return (D)this;
    }

    public D id(String id) {
        this.id = id;
        return (D) this;
    }

    public D type(String type) {
        this.type = type;
        return (D) this;
    }


    public D payload(Message.MessagePayload payload) {
        this.payloads.add(payload);
        return (D)this;
    }

    public Message build() {
        checkNotNull(to);
        checkNotNull(payloads);
        Message message= new Message();
        message.from(from);
        message.to(to);
        message.attr("type", type);
        message.attr("id", id);
        message.attr("socketId", socketID);
        for (Message.MessagePayload payload : payloads) {
            message.payload(payload);
        }

        return message;
    }
}
