package olympus.builder;

import olympus.message.types.Message;

public class ChatState implements Message.MessagePayload {

    public ChatState(STATE state) {
        this.state = state;
    }

    enum STATE {
        typing, reading;
    }

    private STATE state;

    @Override
    public String toXml() {
        return "xml";
    }

    @Override
    public String getType() {
        return "type";
    }

    @Override
    public String toString() {
        return "{state:\"" + state + "\"}";
    }

    public static class Builder extends MessageBuilder<ChatState, Builder> {


        private STATE state;

        public Builder state(String state) {
            this.state = STATE.valueOf(state);
            return this;
        }


        @Override
        public Message build() {
            payload(new ChatState(state));
            return super.build();
        }

    }

}
