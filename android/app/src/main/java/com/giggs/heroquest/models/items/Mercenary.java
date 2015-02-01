package com.giggs.heroquest.models.items;

import com.giggs.heroquest.models.characters.Unit;

/**
 * Created by guillaume ON 10/6/14.
 */
public class Mercenary extends Item {

    private final Unit unit;

    public Mercenary(String identifier, int price, Unit unit) {
        super(identifier, false, price);
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

}
