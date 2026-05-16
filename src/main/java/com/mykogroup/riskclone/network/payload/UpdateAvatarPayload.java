package com.mykogroup.riskclone.network.payload;

public class UpdateAvatarPayload {
    public String avatarPath;

    public UpdateAvatarPayload() {}
    public UpdateAvatarPayload(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
