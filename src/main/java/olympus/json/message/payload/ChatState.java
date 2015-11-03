package olympus.json.message.payload;

import olympus.json.message.builder.MessageBuilder;
import olympus.message.types.Message;

import java.util.List;

public class ChatState extends AbstractMessagePayload{
    public static enum Type {
        ACTIVE,
        INACTIVE,
        COMPOSING,
        PAUSED,
        GONE
    }

    private final Type t;

    public ChatState(Type t, List<Action> actions, List<IgnoreAction> ignoreActions) {
        super(actions, ignoreActions);
        this.t = t;
    }

    @Override
    public String toXml() {
        return String.format("<%s xmlns='http://jabber.org/protocol/chatstates'/>",
                t.name().toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatState)) return false;
        ChatState chatState = (ChatState) o;
        if (t != chatState.t) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return t != null ? t.hashCode() : 0;
    }

    @Override
    public String getType() {
        return "chatState";
    }


    public static class Builder extends MessageBuilder<olympus.message.types.messagePayload.ChatState, Builder> {

        private Type type;

        public ChatState.Builder type(Type type){
            this.type = type;
            return this;
        }

        @Override
        public Message build() {
            payload(new ChatState(type, actions, ignoreActions));
            return super.build();
        }
    }
}
