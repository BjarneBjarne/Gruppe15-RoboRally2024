package com.group15.roborally.client.model;

import com.group15.roborally.common.observer.Subject;

public abstract class Card extends Subject {
    public abstract String getDisplayName();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card otherCard) {
            return this.getDisplayName().equals(otherCard.getDisplayName());
        }
        return false;
    }
}
