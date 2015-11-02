package olympus.json.message.payload;

import olympus.message.types.Message;

import java.util.HashMap;

public class GenericPayload extends HashMap<String, Object> implements Message.MessagePayload{
    @Override
    public String toXml() {
        return null;
    }

    @Override
    public String getType() {
        return "onAnyMessage";
    }
}
