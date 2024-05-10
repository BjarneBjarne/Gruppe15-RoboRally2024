package gruppe15.roborally.model.events;

import gruppe15.roborally.model.Command;

@FunctionalInterface
public interface PlayerCommandListener extends EventListener {
    /**
     * Called when any register is activated for any player.
     *
     * @return
     */
    Command onPlayerCommand(Command command);
}
