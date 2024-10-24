package pixlze.guildapi.handlers.discord.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.json.JSONObject;

public class S2CDiscordEvents {
    public static Event<Message> MESSAGE = EventFactory.createArrayBacked(Message.class, (listeners) -> (message) -> {
        for (Message listener : listeners) {
            listener.interact(message);
        }
    });

    public interface Message {
        void interact(JSONObject message);
    }
}
