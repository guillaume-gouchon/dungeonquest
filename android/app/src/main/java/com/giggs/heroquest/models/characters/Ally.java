package com.giggs.heroquest.models.characters;


/**
 * Created by guillaume ON 10/2/14.
 */
public class Ally extends Unit {

    private static final long serialVersionUID = -5970005172767341685L;

    public Ally(String identifier, int hp, int attack, int defense, int spirit, int movement) {
        super(identifier, Ranks.ALLY, hp, hp, attack, defense, spirit, movement);
    }

}
