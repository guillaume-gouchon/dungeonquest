package com.giggs.heroquest.models.items;

import com.giggs.heroquest.game.GameConstants;
import com.giggs.heroquest.models.StorableResource;
import com.giggs.heroquest.models.items.equipments.Equipment;

/**
 * Created by guillaume ON 10/6/14.
 */
public class Item extends StorableResource {

    private static final long serialVersionUID = -7794186148059317585L;

    private final boolean droppable;
    private final int price;

    public Item(String identifier, boolean droppable, int price) {
        super(identifier);
        this.droppable = droppable;
        this.price = price;
    }

    public int getColor() {
        if (this instanceof Equipment) {
            return GameConstants.getLevelColor(((Equipment) this).getLevel());
        } else {
            return GameConstants.getLevelColor(0);
        }
    }

    public boolean isDroppable() {
        return droppable;
    }

    public int getPrice() {
        return price;
    }

    public int getSellPrice() {
        return price / 2;
    }

}
