package com.giggs.heroquest.models.dungeons.decorations;

import com.giggs.heroquest.game.graphics.DoorSprite;
import com.giggs.heroquest.game.graphics.HorizontalDoorSprite;
import com.giggs.heroquest.models.dungeons.Tile;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by guillaume on 10/16/14.
 */
public class Door extends Decoration {

    private final boolean isHorizontal;
    private boolean isOpen;

    public Door(boolean isHorizontal) {
        super(null, 87, 48, 2, 1);
        this.isHorizontal = isHorizontal;
        this.isOpen = false;
    }

    @Override
    public String getSpriteName() {
        return isHorizontal ? "door_horizontal.png" : "door.png";
    }

    @Override
    public void setTilePosition(Tile tilePosition) {
        if (this.tilePosition != null) {
            this.tilePosition.getSubContent().remove(this);
        }
        this.tilePosition = tilePosition;
        if (tilePosition != null) {
            tilePosition.getSubContent().add(this);
        }
    }

    @Override
    public void createSprite(VertexBufferObjectManager vertexBufferObjectManager) {
        if (isHorizontal) {
            sprite = new HorizontalDoorSprite(this, vertexBufferObjectManager);
        } else {
            sprite = new DoorSprite(this, vertexBufferObjectManager);
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void open() {
        isOpen = true;
        ((DoorSprite) sprite).open();
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

}
