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
package com.gruppe15.model;

import com.gruppe15.model.damage.*;

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
    // Normal programming cards
    MOVE_1("Move 1", true),
    MOVE_2("Move 2", true),
    MOVE_3("Move 3", true),
    RIGHT_TURN("Right Turn", true),
    LEFT_TURN("Left Turn", true),
    U_TURN("U-Turn", true),
    MOVE_BACK("Move Back", true),
    AGAIN("Again"),
    POWER_UP("Power Up", true),

    // Special programming cards
    ENERGY_ROUTINE("Energy Routine", true),
    SANDBOX_ROUTINE("Sandbox Routine", true, MOVE_1, MOVE_2, MOVE_3, MOVE_BACK, LEFT_TURN, RIGHT_TURN, U_TURN),
    WEASEL_ROUTINE("Weasel Routine", true, LEFT_TURN, RIGHT_TURN, U_TURN),
    SPEED_ROUTINE("Speed Routine", true),
    SPAM_FOLDER("Spam Folder", true),
    REPEAT_ROUTINE("Repeat Routine"),

    // Utility commands for UpgradeCards
    MOVE_0("Move 0"), // Do nothing
    MOVE_RIGHT("Move Right"),
    MOVE_LEFT("Move Left"),

    // UpgradeCards commands
    BRAKES("Brakes", MOVE_0, MOVE_1),
    CRAB_LEGS("Brakes", MOVE_LEFT, MOVE_RIGHT),

    // Damage commands
    SPAM(DamageType.SPAM.displayName),
    TROJAN_HORSE(DamageType.TROJAN_HORSE.displayName),
    WORM(DamageType.WORM.displayName),
    VIRUS(DamageType.VIRUS.displayName);

    public final String displayName;
    public final boolean repeatable;
    private final List<Command> options;

    static {
        DamageType.SPAM.setCommandCardType(SPAM);
        DamageType.TROJAN_HORSE.setCommandCardType(TROJAN_HORSE);
        DamageType.WORM.setCommandCardType(WORM);
        DamageType.VIRUS.setCommandCardType(VIRUS);
    }
    Command(String displayName, boolean repeatable, Command... options) {
        this.displayName = displayName;
        this.repeatable = repeatable;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }
    Command(String displayName, Command... options) {
        this.displayName = displayName;
        this.repeatable = false;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }

    public boolean isInteractive() {
        return !options.isEmpty();
    }

    public List<Command> getOptions() {
        return options;
    }

    public boolean isDamage() {
        return this == SPAM || this == TROJAN_HORSE || this == WORM || this == VIRUS;
    }

    public boolean isNormalProgramCommand() {
        return (this == MOVE_1 || this == MOVE_2 || this == MOVE_3 || this == MOVE_BACK ||
                this == RIGHT_TURN || this == LEFT_TURN || this == U_TURN ||
                this == AGAIN || this == POWER_UP);
    }
}
