package api.senior.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseMessages {
    SUCCESS_TRANSFER("Transfer successful"),
    SUCCESS_PROFILE_UPDATE("Profile updated successfully");

    private final String msg;

}
