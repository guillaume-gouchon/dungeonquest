package com.giggs.heroquest.models;

import android.content.res.Resources;

import com.giggs.heroquest.activities.games.GameActivity;

/**
 * Created by guillaume ON 10/3/14.
 */
public class Quest extends StorableResource {

    private static final long serialVersionUID = -4662020495531736056L;

    private static final String TAG = "Quest";

    private final int id;
    private final String outroText;
    private final Class activityClass;
    private String introText;
    private boolean isDone;
    private boolean isAvailable;

    private Quest(Builder builder) {
        super(builder.identifier);
        this.id = builder.id;
        this.introText = builder.intro;
        this.outroText = builder.outro;
        this.activityClass = builder.activityClass;
        this.isDone = false;
        this.isAvailable = builder.isAvailable;
    }

    @Override
    public int getImage(Resources resources) {
        return StorableResource.getResource(resources, "bg_book", true);
    }

    public int getIntroText(Resources resources) {
        return StorableResource.getResource(resources, introText, false);
    }


    public int getId() {
        return id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone() {
        isDone = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public static class Builder {

        private final int id;
        private final String identifier;
        private Class activityClass;
        private String intro;
        private String outro;
        private boolean isAvailable;

        public Builder(int id, String identifier) {
            this.id = id;
            this.identifier = identifier;
            this.intro = "";
            this.outro = "";
            this.activityClass = GameActivity.class;
            this.isAvailable = false;
        }

        public Builder setIntro(String intro) {
            this.intro = intro;
            return this;
        }

        public Builder setOutro(String outro) {
            this.outro = outro;
            return this;
        }

        public Builder setActivityClass(Class activityClass) {
            this.activityClass = activityClass;
            return this;
        }

        public Builder setAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public Quest build() {
            return new Quest(this);
        }

    }

}
