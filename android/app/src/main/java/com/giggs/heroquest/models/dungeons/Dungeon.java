//package com.giggs.heroquest.models.dungeons;
//
//import android.os.Bundle;
//import android.util.Log;
//
//import com.giggs.heroquest.models.characters.Hero;
//import com.giggs.heroquest.models.characters.Unit;
//import com.giggs.heroquest.utils.MazeAlgorithm;
//
//import org.andengine.extension.tmx.TMXTiledMap;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * Created by guillaume ON 10/2/14.
// */
//public class Dungeon implements Serializable {
//
//    private static final String TAG = "Dungeon";
//    private static final long serialVersionUID = -5750050429857666567L;
//
//    private final int width, height;
//    private Room[][] rooms;
//    private int start;
//    private int nbRoomVisited;
//    private int currentPosition;
//    private Directions currentDirection;
//
//    public Dungeon(int width, int height) {
//        this.width = width;
//        this.height = height;
//        nbRoomVisited = 0;
//        this.events = events;
//
//        createRandomDungeon();
//    }
//
//    public void createRandomDungeon() {
//        rooms = new Room[height][width];
//        boolean[][] doors = MazeAlgorithm.createMaze(width, height);
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                rooms[i][j] = new Room(doors, i, j);
//            }
//
//        start = (int) (width * Math.random()) + 10 * (int) (height * Math.random());
//        currentPosition = start;
//    }
//
//
//}
