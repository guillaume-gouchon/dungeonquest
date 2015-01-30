package com.giggs.heroquest.utils.pathfinding;

public interface MovingElement<N extends Node> {

    public abstract boolean canMoveIn(N node);

}
