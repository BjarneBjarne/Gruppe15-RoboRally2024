package gruppe15.roborally.model.upgrades.events;

import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.upgrades.EventListener;

public interface RegisterActivateListener extends EventListener {
    /**
     * Called when any register is activated for any player.
     * <br>
     * @param player The player whose register is activated.
     * @param command The command for that players register.
     */
    void onRegisterActivate(Player player, Command command);
}
