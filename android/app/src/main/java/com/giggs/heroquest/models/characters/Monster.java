package com.giggs.heroquest.models.characters;

import com.giggs.heroquest.models.dungeons.Tile;
import com.giggs.heroquest.models.dungeons.traps.Trap;

/**
 * Created by guillaume ON 10/8/14.
 */
public class Monster extends Unit {

    private static final long serialVersionUID = 3641820414751057694L;

    public Monster(String identifier, int hp, int attack, int defense, int spirit, int movement) {
        super(identifier, Ranks.ENEMY, hp, hp, attack, defense, spirit, movement);
    }

    @Override
    public boolean canMoveIn(Tile tile) {
        return tile.getGround() != null && tile.getContent() == null
                && (tile.getSubContent().size() == 0 || !(tile.getSubContent().get(0) instanceof Trap)
                || !((Trap) tile.getSubContent().get(0)).isRevealed());
    }

}
