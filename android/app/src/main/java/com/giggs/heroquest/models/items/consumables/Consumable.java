package com.giggs.heroquest.models.items.consumables;

import com.giggs.heroquest.models.items.Item;

/**
 * Created by guillaume ON 10/6/14.
 */
public abstract class Consumable extends Item {

    public Consumable(String identifier, int price) {
        super(identifier, true, price);
    }

}
