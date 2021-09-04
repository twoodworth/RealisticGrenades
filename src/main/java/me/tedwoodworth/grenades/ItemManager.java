package me.tedwoodworth.grenades;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemManager {
    public static ItemManager instance = null;
    private final Map<String, ItemStack> grenades = new HashMap<>();

    public static ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }

    private ItemManager() {
    }

    public String colorizeText(String text) {
        text = text.replaceAll("&0", ChatColor.BLACK.toString());
        text = text.replaceAll("&1", ChatColor.DARK_BLUE.toString());
        text = text.replaceAll("&2", ChatColor.DARK_GREEN.toString());
        text = text.replaceAll("&3", ChatColor.DARK_AQUA.toString());
        text = text.replaceAll("&4", ChatColor.DARK_RED.toString());
        text = text.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
        text = text.replaceAll("&6", ChatColor.GOLD.toString());
        text = text.replaceAll("&7", ChatColor.GRAY.toString());
        text = text.replaceAll("&8", ChatColor.DARK_GRAY.toString());
        text = text.replaceAll("&9", ChatColor.BLUE.toString());
        text = text.replaceAll("&a", ChatColor.GREEN.toString());
        text = text.replaceAll("&b", ChatColor.AQUA.toString());
        text = text.replaceAll("&c", ChatColor.RED.toString());
        text = text.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
        text = text.replaceAll("&e", ChatColor.YELLOW.toString());
        text = text.replaceAll("&f", ChatColor.WHITE.toString());
        text = text.replaceAll("&k", ChatColor.MAGIC.toString());
        text = text.replaceAll("&l", ChatColor.BOLD.toString());
        text = text.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
        text = text.replaceAll("&n", ChatColor.UNDERLINE.toString());
        text = text.replaceAll("&o", ChatColor.ITALIC.toString());
        text = text.replaceAll("&r", ChatColor.RESET.toString());
        return text;
    }

    public ItemStack createGrenade(String texture, String grenadeID, String name, double bounciness, double airResistance, double waterResistance, int fuseTime, int despawnTime, double directHitDamage, float blastRadius, float smokeRadius, float fireRadius, float destructionRadius, float flashRadius, double weight, boolean hasGravity, boolean hasSmokeTrail, boolean explodeOnContact, boolean beeps, List<String> lore) {
        var grenade = new ItemStack(Material.PLAYER_HEAD); // create item

        if (!texture.isEmpty()) { // set texture
            UUID hashAsId = new UUID(texture.hashCode(), texture.hashCode());
            Bukkit.getUnsafe().modifyItemStack(grenade, "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\""
                    + texture + "\"}]}}}");
        }

        var meta = grenade.getItemMeta();
        if (meta == null) {
            System.out.println("[RealisticGrenades] Error: Meta of " + grenadeID + " item is null.");
            return grenade;
        }

        meta.setDisplayName("" + ChatColor.RESET + ChatColor.WHITE + colorizeText(name));

        if (lore.size() > 0) { // add name/lore
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, "" + ChatColor.RESET + ChatColor.WHITE + colorizeText(lore.get(i)));
            }
            meta.setLore(lore);
            grenade.setItemMeta(meta);
        }

        var container = meta.getPersistentDataContainer();
        container.set(Constants.GRENADE_ID_KEY, PersistentDataType.STRING, grenadeID);
        container.set(Constants.BOUNCINESS_KEY, PersistentDataType.DOUBLE, bounciness);
        container.set(Constants.AIR_RESISTANCE_KEY, PersistentDataType.DOUBLE, airResistance);
        container.set(Constants.WATER_RESISTANCE_KEY, PersistentDataType.DOUBLE, waterResistance);
        container.set(Constants.FUSE_TIME_KEY, PersistentDataType.INTEGER, fuseTime);
        container.set(Constants.DESPAWN_TIME_KEY, PersistentDataType.INTEGER, despawnTime);
        container.set(Constants.DIRECT_HIT_DAMAGE_KEY, PersistentDataType.DOUBLE, directHitDamage);
        container.set(Constants.BLAST_RADIUS_KEY, PersistentDataType.FLOAT, blastRadius);
        container.set(Constants.SMOKE_RADIUS_KEY, PersistentDataType.FLOAT, smokeRadius);
        container.set(Constants.FIRE_RADIUS_KEY, PersistentDataType.FLOAT, fireRadius);
        container.set(Constants.DESTRUCTION_RADIUS_KEY, PersistentDataType.FLOAT, destructionRadius);
        container.set(Constants.FLASH_RADIUS_KEY, PersistentDataType.FLOAT, flashRadius);
        container.set(Constants.WEIGHT_KEY, PersistentDataType.DOUBLE, weight);
        container.set(Constants.GRAVITY_KEY, BooleanPersistentDataType.instance, hasGravity);
        container.set(Constants.SMOKE_TRAIL_KEY, BooleanPersistentDataType.instance, hasSmokeTrail);
        container.set(Constants.EXPLODE_ON_CONTACT_KEY, BooleanPersistentDataType.instance, explodeOnContact);
        container.set(Constants.BEEPS_KEY, BooleanPersistentDataType.instance, beeps);
        grenade.setItemMeta(meta);

        grenades.put(grenadeID, grenade);
        return grenade.clone();
    }

    public ItemStack getGrenade(String id) {
        var grenade = grenades.get(id);
        if (grenade == null) return null;
        return grenade.clone();
    }

    public List<String> getGrenadeIDs() {
        return new ArrayList<>(grenades.keySet());
    }

    public int getFuseTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return -1;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.FUSE_TIME_KEY, PersistentDataType.INTEGER)) return -1;
        return container.get(Constants.FUSE_TIME_KEY, PersistentDataType.INTEGER);
    }

    public boolean beeps(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.BEEPS_KEY, BooleanPersistentDataType.instance)) return false;
        return container.get(Constants.BEEPS_KEY, BooleanPersistentDataType.instance);
    }

    public long getRemainingTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return -1;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.REMAINING_KEY, PersistentDataType.LONG)) return -1;
        return container.get(Constants.REMAINING_KEY, PersistentDataType.LONG);
    }

    public long getInitialTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return -1;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.INITIAL_KEY, PersistentDataType.LONG)) return -1;
        return container.get(Constants.INITIAL_KEY, PersistentDataType.LONG);
    }

    public double getWeight(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 10.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.WEIGHT_KEY, PersistentDataType.DOUBLE)) return 10.0;
        return container.get(Constants.WEIGHT_KEY, PersistentDataType.DOUBLE);
    }

    public double getAirResistance(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.AIR_RESISTANCE_KEY, PersistentDataType.DOUBLE)) return 0.0;
        return 1 - Math.pow(1 - container.get(Constants.AIR_RESISTANCE_KEY, PersistentDataType.DOUBLE), 1 / (20.0 * ConfigManager.CALCULATIONS_PER_TICK));
    }

    public double getWaterResistance(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.WATER_RESISTANCE_KEY, PersistentDataType.DOUBLE)) return 0.0;
        return 1 - Math.pow(1 - container.get(Constants.WATER_RESISTANCE_KEY, PersistentDataType.DOUBLE), 1 / (20.0 * ConfigManager.CALCULATIONS_PER_TICK));
    }

    public boolean getHasGravity(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return true;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.GRAVITY_KEY, BooleanPersistentDataType.instance)) return true;
        return container.get(Constants.GRAVITY_KEY, BooleanPersistentDataType.instance);
    }

    public float getBlastRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.BLAST_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.BLAST_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    public float getFireRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.FIRE_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.FIRE_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    public float getDestructionRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.DESTRUCTION_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.DESTRUCTION_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    public double getBounciness(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.BOUNCINESS_KEY, PersistentDataType.DOUBLE)) return 0.0;
        return container.get(Constants.BOUNCINESS_KEY, PersistentDataType.DOUBLE);
    }

    public double getDirectHitDamage(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.DIRECT_HIT_DAMAGE_KEY, PersistentDataType.DOUBLE)) return 0.0;
        return container.get(Constants.DIRECT_HIT_DAMAGE_KEY, PersistentDataType.DOUBLE);
    }

    public float getSmokeRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.SMOKE_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.SMOKE_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    public float getFlashRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.FLASH_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.FLASH_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    public boolean hasSmokeTrail(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.SMOKE_TRAIL_KEY, BooleanPersistentDataType.instance)) return false;
        return container.get(Constants.SMOKE_TRAIL_KEY, BooleanPersistentDataType.instance);
    }

    public void setRemainingTime(ItemStack item, long time) {
        var meta = item.getItemMeta();
        if (meta == null) return;
        var container = meta.getPersistentDataContainer();
        container.set(Constants.REMAINING_KEY, PersistentDataType.LONG, time);
        item.setItemMeta(meta);
    }

    public void setRemainingDespawnTime(ItemStack item, long time) {
        var meta = item.getItemMeta();
        if (meta == null) return;
        var container = meta.getPersistentDataContainer();
        container.set(Constants.DESPAWN_REMAINING_KEY, PersistentDataType.LONG, time);
        item.setItemMeta(meta);
    }

    public long getRemainingDespawnTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.DESPAWN_REMAINING_KEY, PersistentDataType.LONG)) return 0;
        return container.get(Constants.DESPAWN_REMAINING_KEY, PersistentDataType.LONG);
    }

    public int getDespawnTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.DESPAWN_TIME_KEY, PersistentDataType.INTEGER)) return 0;
        return container.get(Constants.DESPAWN_TIME_KEY, PersistentDataType.INTEGER);
    }

    public boolean getExplodeOnCollision(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.EXPLODE_ON_CONTACT_KEY, BooleanPersistentDataType.instance)) return false;
        return container.get(Constants.EXPLODE_ON_CONTACT_KEY, BooleanPersistentDataType.instance);
    }

    public boolean isGrenade(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        return container.has(Constants.GRENADE_ID_KEY, PersistentDataType.STRING);
    }

    public String getID(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return null;
        var container = meta.getPersistentDataContainer();
        return container.get(Constants.GRENADE_ID_KEY, PersistentDataType.STRING);
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
}
