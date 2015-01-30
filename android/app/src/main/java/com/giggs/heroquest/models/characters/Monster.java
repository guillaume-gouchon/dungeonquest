package com.giggs.heroquest.models.characters;

/**
 * Created by guillaume ON 10/8/14.
 */
public class Monster extends Unit {

    private static final long serialVersionUID = 3641820414751057694L;

    public Monster(String identifier, int hp, int currentHP, int strength, int dexterity, int spirit, int movement) {
        super(identifier, Ranks.ENEMY, hp, currentHP, strength, dexterity, spirit, movement);
    }

}
