package com.giggs.heroquest.models.effects;

import com.giggs.heroquest.models.characters.Unit;

/**
 * Created by guillaume on 19/10/14.
 */
public class InvocationEffect extends Effect {

    private static final long serialVersionUID = 2109747444465353571L;

    private final Unit unit;

    public InvocationEffect(String spriteName, Unit unit) {
        super(spriteName, null, 0, INSTANT_EFFECT, null, 0);
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

}
