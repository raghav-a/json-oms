package olympus.builder;

import olympus.common.JID;
import olympus.message.types.Message;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class MessageBuilder<T extends Message.MessagePayload, D extends MessageBuilder> {

    private JID to;
    private JID from;
    private Message.MessagePayload payload;
    private String id;
    private String type;

    public D to(JID jid) {
        this.to = jid;
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
        this.payload = payload;
        return (D)this;
    }

    public Message build() {
        checkNotNull(to);
        checkNotNull(payload);
        Message message= new Message();

        message.from(from);
        message.to(to);
        message.attr("type", type);
        message.attr("id", id);
        message.payload(payload);

        return message;
    }
}
