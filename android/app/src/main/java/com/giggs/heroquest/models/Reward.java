package com.giggs.heroquest.models;

import android.content.res.Resources;

import com.giggs.heroquest.models.items.Item;

import java.io.Serializable;

/**
 * Created by guillaume on 10/13/14.
 */
public class Reward extends StorableResource implements Serializable {

    private static final long serialVersionUID = 5814508698731975285L;

    private final String description;
    private final Item item;
    private final int gold;

    public Reward(String identifier, String description, Item item, int gold) {
        super(identifier);
        this.description = description;
        this.item = item;
        this.gold = gold;
    }

    public Item getItem() {
        return item;
    }

    public int getGold() {
        return gold;
    }

    @Override
    public int getDescription(Resources resources) {
        if (description != null) {
            return getResource(resources, description + "_description", false);
        }
        return super.getDescription(resources);
    }

}
