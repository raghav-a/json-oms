package olympus.json.message.payload;

import com.google.common.base.Preconditions;
import olympus.json.message.builder.MessageBuilder;
import olympus.message.types.Message;

public class Receipt implements Message.MessagePayload {
    public static enum Type {
        REQUEST,
        RECEIVED,
        READ,
        FAILED
    }

    private final Type t;
    private final String id;
    private final String sid;

    public Receipt(Type t) {
        this(t, null, null);
    }

    public Receipt(Type t, String id, String sid) {
        this.t = t;
        switch (t) {
            case REQUEST:
                this.id = null;
                this.sid = null;
                break;
            default:
                Preconditions.checkArgument(id != null || sid != null, "both id and sid cannot be null");
                this.id = id;
                this.sid = sid;
        }
    }

    @Override
    public String toXml() {
        switch (t) {
            case REQUEST:
                return String.format("<request xmlns='urn:xmpp:receipts'/>");
            default:
                return String.format("<%s xmlns='urn:xmpp:receipts' id='%s' sid='%s'/>",
                        t.name().toLowerCase(), id, sid);
        }
    }

    @Override
    public String getType() {
        return "receipt";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Receipt)) return false;

        Receipt receipt = (Receipt) o;

        if (id != null ? !id.equals(receipt.id) : receipt.id != null) return false;
        if (sid != null ? !sid.equals(receipt.sid) : receipt.sid != null) return false;
        if (t != receipt.t) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = t != null ? t.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (sid != null ? sid.hashCode() : 0);
        return result;
    }

    public class Builder extends MessageBuilder<olympus.message.types.messagePayload.Receipt, Builder> {

        private olympus.message.types.messagePayload.Receipt.Type type;
        private String id;
        private String sid;

        public Builder type(olympus.message.types.messagePayload.Receipt.Type type){
            this.type = type;
            return this;
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder sid(String sid){
            this.sid = sid;
            return this;
        }

        @Override
        public Message build() {
            payload(new olympus.message.types.messagePayload.Receipt(type, id, sid));
            return super.build();
        }
    }
}

