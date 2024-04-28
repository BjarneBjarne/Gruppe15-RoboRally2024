package gruppe15.roborally.model.upgrades;

import gruppe15.roborally.model.Player;

import java.util.EventListener;
import java.util.List;

public interface EffectTrigger extends EventListener {
    /**
     * Defines the behavior that should happen automatically when the card's effect is triggered.
     * <br>
     * For permanent cards, this typically includes most of their behavior.
     * <br>
     * For temporary cards, this may not include any behavior.
     * @param owner The player who owns the card.
     * @param targets The list of players targeted by the card's effect.
     */
}
