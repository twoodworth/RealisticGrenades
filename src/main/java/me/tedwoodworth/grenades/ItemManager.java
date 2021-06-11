package me.tedwoodworth.grenades;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ItemManager {
    public static ItemManager instance = null;
    private final ItemStack fragGrenade;

    public static ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }

    private ItemManager() {
        // frag grenade
        fragGrenade = getCustomSkull(Constants.FRAG_TEXTURE, Grenade.FRAG, "Frag Grenade");
    }

    public ItemStack getCustomSkull(String texture, Grenade type, String... text) {
        var head = new ItemStack(Material.PLAYER_HEAD); // create item

        if (!texture.isEmpty()) { // set texture
            UUID hashAsId = new UUID(texture.hashCode(), texture.hashCode());
            Bukkit.getUnsafe().modifyItemStack(head, "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\""
                    + texture + "\"}]}}}");
        }

        var meta = head.getItemMeta();
        if (text.length > 0 && meta != null) { // add name/lore
            for (int i = 0; i < text.length; i++) {
                text[i] = "" + ChatColor.RESET + text[i];
            }
            meta.setDisplayName("" + ChatColor.RESET + text[0]);
            meta.setLore(new ArrayList<>(Arrays.asList(text).subList(1, text.length)));
            head.setItemMeta(meta);

            var container = meta.getPersistentDataContainer();
            container.set(Constants.TYPE_KEY, PersistentDataType.SHORT, (short) type.ordinal());
            head.setItemMeta(meta);
        }


        return head;
    }

    public ItemStack getGrenade(Grenade type) {
        return switch (type) {
            case FRAG -> fragGrenade.clone();
            default -> fragGrenade.clone();
        };
    }

    public ItemStack getGrenade(Grenade type, int amount) {
        var grenade = switch (type) {
            case FRAG -> fragGrenade.clone();
            default -> fragGrenade.clone();
        };
        grenade.setAmount(amount);
        return grenade;
    }

    public boolean isGrenade(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        return container.has(Constants.TYPE_KEY, PersistentDataType.SHORT);
    }

    public boolean isPrimed(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        return container.has(Constants.PRIMED_KEY, BooleanPersistentDataType.instance) && container.get(Constants.PRIMED_KEY, BooleanPersistentDataType.instance);
    }

    public void primeGrenade(ItemStack grenade) {
        if (!isGrenade(grenade)) {
            throw new IllegalArgumentException("Error: argument is not a grenade.");
        }

        var meta = grenade.getItemMeta();
        var container = meta.getPersistentDataContainer();
        container.set(Constants.PRIMED_KEY, BooleanPersistentDataType.instance, true);
        grenade.setItemMeta(meta);
    }

    public Grenade getType(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return null;
        var container = meta.getPersistentDataContainer();
        var ordinal = container.get(Constants.TYPE_KEY, PersistentDataType.SHORT);
        return Grenade.values()[ordinal];
    }
}
