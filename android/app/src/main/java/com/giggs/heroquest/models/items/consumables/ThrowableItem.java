package com.giggs.heroquest.models.items.consumables;

import com.giggs.heroquest.models.effects.Effect;

/**
 * Created by guillaume ON 10/6/14.
 */
public class ThrowableItem extends Consumable {

    private final Effect effect;

    public ThrowableItem(String identifier, Effect effect, int price) {
        super(identifier, price);
        this.effect = effect;
    }

    public Effect getEffect() {
        return effect;
    }

}
