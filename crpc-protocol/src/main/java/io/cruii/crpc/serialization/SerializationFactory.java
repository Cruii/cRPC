package io.cruii.crpc.serialization;

public class SerializationFactory {

    private SerializationFactory() {
    }

    public static RpcSerialization getRpcSerialization(byte serializationType) {
        SerializationTypeEnum typeEnum = SerializationTypeEnum.ofType(serializationType);

        switch (typeEnum) {
            case HESSIAN:
                return new HessianSerialization();
            case JSON:
                return new JsonSerialization();
            default:
                throw new IllegalArgumentException("Illegal serialization type , " + serializationType);
        }
    }
}
