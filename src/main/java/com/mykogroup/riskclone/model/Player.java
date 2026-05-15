package com.mykogroup.riskclone.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private String id;
    private String displayName;
    private boolean isAi;

    private String avatarPath;

    // Jackson REQUIRES a default, no-argument constructor to deserialize JSON into objects.
    public Player() {}

    public Player(String id, String displayName, boolean isAi) {
        this(id, displayName, isAi, "/com/mykogroup/riskclone/assets/Avatar1.png");
    }

    public Player(String id, String displayName, boolean isAi, String avatarPath) {
        this.id = id;
        this.displayName = displayName;
        this.isAi = isAi;
        this.avatarPath = avatarPath;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public boolean isAi() { return isAi; }
    public void setAi(boolean ai) { isAi = ai; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
}