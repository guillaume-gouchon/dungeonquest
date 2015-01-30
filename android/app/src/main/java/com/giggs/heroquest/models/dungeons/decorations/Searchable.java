package com.giggs.heroquest.models.dungeons.decorations;

import com.giggs.heroquest.game.graphics.GameElementSprite;
import com.giggs.heroquest.models.Reward;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by guillaume on 10/10/14.
 */
public class Searchable extends Decoration {

    private static final long serialVersionUID = 3803164461586732585L;

    private boolean isEmpty = false;
    private Reward reward;

    public Searchable(String identifier, Reward reward, int spriteWidth, int spriteHeight, int nbSpritesX, int nbSpritesY) {
        super(identifier, spriteWidth, spriteHeight, nbSpritesX, nbSpritesY);
        this.reward = reward;
    }

    public Reward search() {
        if (isEmpty) return null;

        isEmpty = true;
        return reward;
    }

    @Override
    public void createSprite(VertexBufferObjectManager vertexBufferObjectManager) {
        sprite = new GameElementSprite(this, vertexBufferObjectManager);
        sprite.setScale(0.6f);
    }

}
