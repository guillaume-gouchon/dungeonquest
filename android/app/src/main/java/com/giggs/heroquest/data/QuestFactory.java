package com.giggs.heroquest.data;

import com.giggs.heroquest.models.Quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume ON 10/6/14.
 */
public class QuestFactory {

    public static List<Quest> getAll() {
        List<Quest> lst = new ArrayList<>();
        lst.add(buildTutorial());
        lst.add(buildTutorial());
        lst.add(buildTutorial());
        lst.add(buildTutorial());
        lst.add(buildTutorial());
        return lst;
    }

    public static Quest buildTutorial() {
        Quest book = new Quest.Builder(0, "tutorial").setAvailable(true).build();


        return book;
    }

}
