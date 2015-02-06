package com.giggs.heroquest.models.dungeons.traps;

import com.giggs.heroquest.game.graphics.TrapSprite;
import com.giggs.heroquest.models.dungeons.decorations.ItemOnGround;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by guillaume on 2/3/15.
 */
public class Trap extends ItemOnGround {

    private final int trapIndex;
    private final int attackDice;
    private boolean isRevealed = false;

    public Trap(int trapIndex, int attackDice) {
        super("traps", null);
        this.trapIndex = trapIndex;
        this.attackDice = attackDice;
    }

    @Override
    public void createSprite(VertexBufferObjectManager vertexBufferObjectManager) {
        sprite = new TrapSprite(this, vertexBufferObjectManager);
    }

    public void reveal() {
        if (!isRevealed) {
            isRevealed = true;
            ((TrapSprite) sprite).reveal();
        }
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public int getAttackDice() {
        return attackDice;
    }

    public int getTrapIndex() {
        return trapIndex;
    }

    @Override
    public String getSpriteName() {
        return "traps.png";
    }

}
