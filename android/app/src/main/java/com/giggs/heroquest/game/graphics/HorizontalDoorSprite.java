package com.giggs.heroquest.game.graphics;

import com.giggs.heroquest.models.dungeons.decorations.Door;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by guillaume on 12/5/14.
 */
public class HorizontalDoorSprite extends DoorSprite {

    public HorizontalDoorSprite(Door door, VertexBufferObjectManager vertexBufferObjectManager) {
        super(door, vertexBufferObjectManager);
        setZIndex(11);
    }

    @Override
    public void setPosition(float pX, float pY) {
        super.setPosition(pX, pY + GameElementSprite.Y_OFFSET - 43);
    }

}
