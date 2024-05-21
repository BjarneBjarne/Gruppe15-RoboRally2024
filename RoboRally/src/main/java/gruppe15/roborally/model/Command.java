/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package gruppe15.roborally.model;

import gruppe15.roborally.model.damage.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public enum Command {
    // This is a very simplistic way of realizing different commands.
    MOVE_0("Move 0"), // Do nothing
    MOVE_1("Move 1"),
    MOVE_2("Move 2"),
    MOVE_3("Move 3"),
    RIGHT_TURN("Right Turn"),
    LEFT_TURN("Left Turn"),
    U_TURN("U-Turn"),
    MOVE_BACK("Move Back"),
    AGAIN("Again"),
    POWER_UP("Power Up"),

    // Special commands
    OPTION_LEFT_RIGHT("Left OR Right", LEFT_TURN, RIGHT_TURN),
    //UPGRADE("Upgrade"),

    // UpgradeCards commands
    BRAKES("Brakes", MOVE_0, MOVE_1),

    // Damage commands
    SPAM(DamageTypes.SPAM.displayName),
    TROJAN_HORSE(DamageTypes.TROJAN_HORSE.displayName),
    WORM(DamageTypes.WORM.displayName),
    VIRUS(DamageTypes.VIRUS.displayName);

    public final String displayName;
    private final List<Command> options;

    static {
        DamageTypes.SPAM.setCommandCardType(SPAM);
        DamageTypes.TROJAN_HORSE.setCommandCardType(TROJAN_HORSE);
        DamageTypes.WORM.setCommandCardType(WORM);
        DamageTypes.VIRUS.setCommandCardType(VIRUS);
    }
    Command(String displayName, Command... options) {
        this.displayName = displayName;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }

    public boolean isInteractive() {
        return !options.isEmpty();
    }

    public List<Command> getOptions() {
        return options;
    }
}
