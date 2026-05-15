package com.mykogroup.riskclone.model;

import java.io.Serializable;

public class LobbyPlayer implements Serializable {
    private String name;
    private String avatarPath;
    private String colorHex;
    private boolean isAi;

    public LobbyPlayer(String name, String avatarPath, String colorHex, boolean isAi) {
        this.name = name;
        this.avatarPath = avatarPath;
        this.colorHex = colorHex;
        this.isAi = isAi;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public boolean isAi() { return isAi; }
    public void setAi(boolean ai) { isAi = ai; }
}
