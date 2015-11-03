package olympus.json.message.payload;

import olympus.message.types.Message;

import java.util.List;

public abstract class AbstractMessagePayload implements Message.MessagePayload{

    public AbstractMessagePayload(List<Action> actions) {
        this.actions = actions;
    }

    private List<Action> actions;



}
