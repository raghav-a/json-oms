package olympus.json.message.payload;

import olympus.json.message.builder.MessageBuilder;
import olympus.message.types.Message;

import java.util.HashMap;
import java.util.Map;

public class GenericMessagePayload extends HashMap<String, Object> implements Message.MessagePayload {
    @Override
    public String toXml() {
        return null;
    }

    @Override
    public String getType() {
        return "genericMessage";
    }

    public static class Builder extends MessageBuilder<GenericMessagePayload, Builder> {

        private Map<String, Object> map = new HashMap<>();

        public void put(String key, Object value) {
            map.put(key, value);
        }


        @Override
        public Message build() {
            GenericMessagePayload payload = new GenericMessagePayload();
            payload.putAll(map);
            payload(payload);
            return super.build();
        }
    }
}
