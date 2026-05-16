package com.mykogroup.riskclone.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class LobbyPlayer implements Serializable {
    public String playerId;
    public String displayName;
    public String color;
    @JsonProperty("isAi")
    public boolean isAi;
    public String avatarPath;

    public LobbyPlayer() {}

    public LobbyPlayer(String playerId, String displayName, String color, boolean isAi, String avatarPath) {
        this.playerId = playerId;
        this.displayName = displayName;
        this.color = color;
        this.isAi = isAi;
        this.avatarPath = avatarPath;
    }
}
