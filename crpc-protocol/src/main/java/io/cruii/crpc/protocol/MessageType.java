package io.cruii.crpc.protocol;

import lombok.Getter;

public enum MessageType {
    REQUEST(1),
    RESPONSE(2),
    HEARTBEAT(3);

    @Getter
    private final int type;

    MessageType(int type) {
        this.type = type;
    }

    public static MessageType of(int type) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getType() == type) {
                return messageType;
            }
        }
        return null;
    }
}
