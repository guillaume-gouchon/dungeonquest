package com.giggs.heroquest.game.graphics;

import com.giggs.heroquest.models.dungeons.decorations.Door;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by guillaume on 12/5/14.
 */
public class DoorSprite extends GameElementSprite {

    private final Door door;

    public DoorSprite(Door door, VertexBufferObjectManager vertexBufferObjectManager) {
        super(door, vertexBufferObjectManager);
        this.door = door;
        setCurrentTileIndex(door.isHorizontal() ? 1 : 0);
    }

    public void setPosition(float pX, float pY) {
        super.setPosition(pX, pY + GameElementSprite.Y_OFFSET);
    }

    public void open() {
        setCurrentTileIndex(door.isHorizontal() ? 0 : 1);
    }

}
