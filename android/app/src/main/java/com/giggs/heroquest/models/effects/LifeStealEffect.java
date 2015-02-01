package com.giggs.heroquest.models.effects;

/**
 * Created by guillaume on 19/10/14.
 */
public class LifeStealEffect extends Effect {

    private static final long serialVersionUID = 5321695335925128052L;

    public LifeStealEffect(String spriteName, int value, int duration, int level) {
        super(spriteName, null, value, duration, null, level);
    }

}
