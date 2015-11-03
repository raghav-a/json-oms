package olympus.json.message.payload;

import olympus.message.types.Message;

import java.util.List;

public abstract class AbstractMessagePayload implements Message.MessagePayload{

    public AbstractMessagePayload(List<Action> actions, List<IgnoreAction> ignoreActions) {
        this.actions = actions;
        this.ignoreActions = ignoreActions;
    }

    private List<Action> actions;


    public List<Action> getActions() {
        return actions;
    }

    private List<IgnoreAction> ignoreActions;


    public List<IgnoreAction> getIgnoreActions() {
        return ignoreActions;
    }
}
