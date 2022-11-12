package io.cruii.crpc.protocol;

import lombok.Getter;

public enum MessageStatus {
    SUCCESS(0),
    FAIL(1);

    @Getter
    private final int code;

    MessageStatus(int code) {
        this.code = code;
    }

}
