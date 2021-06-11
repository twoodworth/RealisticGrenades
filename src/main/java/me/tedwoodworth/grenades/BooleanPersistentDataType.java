package me.tedwoodworth.grenades;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class BooleanPersistentDataType implements PersistentDataType<Byte, Boolean> {
    public static BooleanPersistentDataType instance = new BooleanPersistentDataType();

    private BooleanPersistentDataType() {
    }

    @Override
    public @NotNull Class<Byte> getPrimitiveType() {
        return Byte.class;
    }

    @Override
    public @NotNull Class<Boolean> getComplexType() {
        return Boolean.class;
    }

    @NotNull
    @Override
    public Byte toPrimitive(@NotNull Boolean complex, @NotNull PersistentDataAdapterContext context) {
        return complex ? (byte) 1 : 0;
    }

    @NotNull
    @Override
    public Boolean fromPrimitive(@NotNull Byte primitive, @NotNull PersistentDataAdapterContext context) {
        return switch (primitive) {
            case 1 -> true;
            case 0 -> false;
            default -> throw new IllegalArgumentException("Error converting persistent types, byte must be 0 or 1, not" + primitive);
        };
    }
}
