package me.tedwoodworth.grenades;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * A custom {@link PersistentDataType} which supports the complex data type Boolean by mapping
 * a value of the primitive data type Byte onto each Boolean value.
 */
public class BooleanPersistentDataType implements PersistentDataType<Byte, Boolean> {
    public static BooleanPersistentDataType instance = new BooleanPersistentDataType();

    /**
     * Constructs a BooleanPersistentDataType
     */
    private BooleanPersistentDataType() {
    }

    /**
     * Provides the class of Byte, the primitive data type being utilized
     *
     * @return Byte.class
     */
    @Override
    public @NotNull Class<Byte> getPrimitiveType() {
        return Byte.class;
    }

    /**
     * Provides the class of Boolean, the complex data type being utilized
     *
     * @return Boolean.class
     */
    @Override
    public @NotNull Class<Boolean> getComplexType() {
        return Boolean.class;
    }

    /**
     * Maps a Boolean value into its corresponding Byte value.
     *
     * @param complex: The Boolean value to map
     * @param context: The context in which the value is being mapped
     * @return The corresponding byte value
     */
    @NotNull
    @Override
    public Byte toPrimitive(@NotNull Boolean complex, @NotNull PersistentDataAdapterContext context) {
        return complex ? (byte) 1 : 0;
    }

    /**
     * Maps a Byte value into its corresponding Boolean value.
     *
     * @param primitive: The Byte value to map
     * @param context:   The context in which the value is being mapped
     * @return The corresponding Boolean value.
     */
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
