package me.tedwoodworth.grenades;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Constructs and manages grenades and the values of their attributes.
 */
public class ItemManager {
    /**
     * Singleton instance of ItemManager
     */
    public static ItemManager instance = null;

    /**
     * Map of grenade ID to grenade ItemStack
     */
    private final Map<String, ItemStack> grenades = new HashMap<>();

    /**
     * Returns the singleton instance of ItemManager.
     *
     * @return ItemManager instance
     */
    public static ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }

    /**
     * Constructs ItemManager
     */
    private ItemManager() {
    }

    /**
     * Translates string color codes into their corresponding {@link ChatColor} values
     *
     * @param text: Text to colorize
     * @return Colorized text
     */
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

    /**
     * Creates a grenade with the given attributes
     *
     * @param texture:           The texture code of the grenade
     * @param grenadeID:         The ID of the grenade
     * @param name:              The name of the grenade
     * @param bounciness:        How bouncy the grenade is
     * @param airResistance:     How much air resistance impacts the grenade
     * @param waterResistance:   How much water resistance impacts the grenade
     * @param fuseTime:          How long it takes the grenade to explode
     * @param despawnTime:       How long it takes the grenade to despawn
     * @param directHitDamage:   How much damage a direct hit from the grenade deals
     * @param blastRadius:       The size of the grenade's blast explosion
     * @param smokeRadius:       The size of the grenade's smoke explosion
     * @param fireRadius:        The size of the grenade's fire explosion
     * @param destructionRadius: The size of the grenade's destruction explosion
     * @param flashRadius:       The size of the grenade's flash explosion
     * @param weight:            The grenade's weight
     * @param hasGravity:        Whether or not the grenade has gravity
     * @param hasSmokeTrail:     Whether or not the grenade has a smoke trail
     * @param explodeOnContact:  Whether or not the grenade explodes on contact
     * @param beeps:             Whether or not the grenade beeps
     * @param lore:              The grenade's lore
     * @return A grenade ItemStack
     */
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

    /**
     * Returns a grenade ItemStack corresponding to the given ID
     *
     * @param id: ID of grenade
     * @return Grenade
     */
    public ItemStack getGrenade(String id) {
        var grenade = grenades.get(id);
        if (grenade == null) return null;
        return grenade.clone();
    }

    /**
     * Returns a list of all grenade IDs
     *
     * @return List of grenade IDs
     */
    public List<String> getGrenadeIDs() {
        return new ArrayList<>(grenades.keySet());
    }

    /**
     * Returns the fuse time of a given grenade
     *
     * @param item: The ItemStack of the given Grenade
     * @return The grenade's fuse time
     */
    public int getFuseTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return -1;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.FUSE_TIME_KEY, PersistentDataType.INTEGER)) return -1;
        return container.get(Constants.FUSE_TIME_KEY, PersistentDataType.INTEGER);
    }

    /**
     * Determines whether the given grenade beeps
     *
     * @param item: The ItemStack of the given Grenade
     * @return whether the given grenade beeps
     */
    public boolean beeps(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.BEEPS_KEY, BooleanPersistentDataType.instance)) return false;
        return container.get(Constants.BEEPS_KEY, BooleanPersistentDataType.instance);
    }

    /**
     * Determines the remaining time until a given grenade explodes
     *
     * @param item: The ItemStack of the given Grenade
     * @return remaining time until explosion
     */
    public long getRemainingTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return -1;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.REMAINING_KEY, PersistentDataType.LONG)) return -1;
        return container.get(Constants.REMAINING_KEY, PersistentDataType.LONG);
    }


    /**
     * Determines the explosion time at the moment a grenade was thrown
     *
     * @param item: The ItemStack of the given Grenade
     * @return The initial explosion time
     */
    public long getInitialTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return -1;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.INITIAL_KEY, PersistentDataType.LONG)) return -1;
        return container.get(Constants.INITIAL_KEY, PersistentDataType.LONG);
    }


    /**
     * Determines the weight of a grenade
     *
     * @param item: The ItemStack of the given Grenade
     * @return The weight of the given grenade
     */
    public double getWeight(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 10.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.WEIGHT_KEY, PersistentDataType.DOUBLE)) return 10.0;
        return container.get(Constants.WEIGHT_KEY, PersistentDataType.DOUBLE);
    }


    /**
     * Determines the air resistance of a given grenade
     *
     * @param item: The ItemStack of the given Grenade
     * @return Grenade air resistance
     */
    public double getAirResistance(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.AIR_RESISTANCE_KEY, PersistentDataType.DOUBLE)) return 0.0;
        return 1 - Math.pow(1 - container.get(Constants.AIR_RESISTANCE_KEY, PersistentDataType.DOUBLE), 1 / (20.0 * ConfigManager.CALCULATIONS_PER_TICK));
    }


    /**
     * Determines the given grenade's water resistance
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade water resistance
     */
    public double getWaterResistance(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.WATER_RESISTANCE_KEY, PersistentDataType.DOUBLE)) return 0.0;
        return 1 - Math.pow(1 - container.get(Constants.WATER_RESISTANCE_KEY, PersistentDataType.DOUBLE), 1 / (20.0 * ConfigManager.CALCULATIONS_PER_TICK));
    }


    /**
     * Determines whether the given grenade has gravity
     *
     * @param item: The ItemStack of the given Grenade
     * @return whether the grenade has gravity
     */
    public boolean getHasGravity(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return true;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.GRAVITY_KEY, BooleanPersistentDataType.instance)) return true;
        return container.get(Constants.GRAVITY_KEY, BooleanPersistentDataType.instance);
    }

    /**
     * Determines the given grenade's blast radius
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade blast radius
     */
    public float getBlastRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.BLAST_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.BLAST_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    /**
     * Determines the given grenade's fire radius
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade fire radius
     */
    public float getFireRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.FIRE_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.FIRE_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    /**
     * Determines the given grenade's destruction radius
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade destruction radius
     */
    public float getDestructionRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.DESTRUCTION_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.DESTRUCTION_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    /**
     * Determines the given grenade's bounciness
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade bounciness
     */
    public double getBounciness(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.BOUNCINESS_KEY, PersistentDataType.DOUBLE)) return 0.0;
        return container.get(Constants.BOUNCINESS_KEY, PersistentDataType.DOUBLE);
    }

    /**
     * Determines the given grenade's direct hit damage
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade direct hit damage
     */
    public double getDirectHitDamage(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.DIRECT_HIT_DAMAGE_KEY, PersistentDataType.DOUBLE)) return 0.0;
        return container.get(Constants.DIRECT_HIT_DAMAGE_KEY, PersistentDataType.DOUBLE);
    }

    /**
     * Determines the given grenade's smoke explosion radius
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade smoke explosion radius
     */
    public float getSmokeRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.SMOKE_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.SMOKE_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    /**
     * Determines the given grenade's flash explosion radius
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade flash explosion radius
     */
    public float getFlashRadius(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0.0F;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.FLASH_RADIUS_KEY, PersistentDataType.FLOAT)) return 0.0F;
        return container.get(Constants.FLASH_RADIUS_KEY, PersistentDataType.FLOAT);
    }

    /**
     * Determines if the given grenade has a smoke trail
     *
     * @param item: The ItemStack of the given Grenade
     * @return whether the grenade has a smoke trail or not
     */
    public boolean hasSmokeTrail(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.SMOKE_TRAIL_KEY, BooleanPersistentDataType.instance)) return false;
        return container.get(Constants.SMOKE_TRAIL_KEY, BooleanPersistentDataType.instance);
    }

    /**
     * Sets the given grenade's remaining time
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade remaining time
     */
    public void setRemainingTime(ItemStack item, long time) {
        var meta = item.getItemMeta();
        if (meta == null) return;
        var container = meta.getPersistentDataContainer();
        container.set(Constants.REMAINING_KEY, PersistentDataType.LONG, time);
        item.setItemMeta(meta);
    }

    /**
     * Sets the given grenade's remaining despawn time
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade remaining despawn time
     */
    public void setRemainingDespawnTime(ItemStack item, long time) {
        var meta = item.getItemMeta();
        if (meta == null) return;
        var container = meta.getPersistentDataContainer();
        container.set(Constants.DESPAWN_REMAINING_KEY, PersistentDataType.LONG, time);
        item.setItemMeta(meta);
    }

    /**
     * Determines the given grenade's remaining despawn time
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade remaining despawn time
     */
    public long getRemainingDespawnTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.DESPAWN_REMAINING_KEY, PersistentDataType.LONG)) return 0;
        return container.get(Constants.DESPAWN_REMAINING_KEY, PersistentDataType.LONG);
    }

    /**
     * Gets the given grenade's initial despawn time
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade despawn time
     */
    public int getDespawnTime(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.DESPAWN_TIME_KEY, PersistentDataType.INTEGER)) return 0;
        return container.get(Constants.DESPAWN_TIME_KEY, PersistentDataType.INTEGER);
    }

    /**
     * Determines if the given grenade explodes on collision
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade explodes on collision
     */
    public boolean getExplodeOnCollision(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        if (!container.has(Constants.EXPLODE_ON_CONTACT_KEY, BooleanPersistentDataType.instance)) return false;
        return container.get(Constants.EXPLODE_ON_CONTACT_KEY, BooleanPersistentDataType.instance);
    }

    /**
     * Determines if the given item is a grenade
     *
     * @param item: An ItemStack
     * @return item is a grenade
     */
    public boolean isGrenade(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        return container.has(Constants.GRENADE_ID_KEY, PersistentDataType.STRING);
    }

    /**
     * Gets the given grenade's ID
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade ID
     */
    public String getID(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return null;
        var container = meta.getPersistentDataContainer();
        return container.get(Constants.GRENADE_ID_KEY, PersistentDataType.STRING);
    }

    /**
     * Determines if the given grenade is primed
     *
     * @param item: The ItemStack of the given Grenade
     * @return grenade is primed
     */
    public boolean isPrimed(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return false;
        var container = meta.getPersistentDataContainer();
        return container.has(Constants.PRIMED_KEY, BooleanPersistentDataType.instance) && container.get(Constants.PRIMED_KEY, BooleanPersistentDataType.instance);
    }

    /**
     * Primes the given grenade.
     *
     * @param grenade: The grenade to prime
     * @throws IllegalArgumentException if the ItemStack is not a grenade ItemStack.
     */
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
