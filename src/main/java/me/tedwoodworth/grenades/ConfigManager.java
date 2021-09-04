package me.tedwoodworth.grenades;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigManager {
    private static ConfigManager instance = null;
    private final RealisticGrenades plugin;
    private final FileConfiguration config;
    public static int CALCULATIONS_PER_TICK;
    public static double SMOKE_THICKNESS;
    public static String GUI_TITLE;

    public static final HashMap<String, Recipe> grenadeRecipes = new HashMap<>();

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private ConfigManager() {
        plugin = RealisticGrenades.getInstance();
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void loadConfig() {
        config.options().copyDefaults(true).header(
                "\u0023###########################################################\n" +
                        "\u0023 +------------------------------------------------------+ #\n" +
                        "\u0023 |                  RealisticGrenades                   | #\n" +
                        "\u0023 +------------------------------------------------------+ #\n" +
                        "\u0023###########################################################\n" +
                        "\u0020\n" +
                        "This is the config file for RealisticGrenades Version " + plugin.getDescription().getVersion() + ".\n" +
                        "For explanations or help with configuration, visit https://github.com/twoodworth/RealisticGrenades/wiki/Configuration.\n" +
                        "If you need additional help, you can join the #realistic-grenades-help channel in my discord: https://discord.gg/MzJhecjNQU\n" +
                        "\u0020\n" +
                        "\u0023###########################################################\n" +
                        "\u0023 +------------------------------------------------------+ #\n" +
                        "\u0023 |                    Configuration                     | #\n" +
                        "\u0023 +------------------------------------------------------+ #\n" +
                        "\u0023###########################################################\n"
        );
        plugin.saveConfig();
        readConfig();
    }

    private void readConfig() {
        if (!config.contains("calculations-per-tick")) {
            CALCULATIONS_PER_TICK = 20;
            config.set("calculations-per-tick", 20);
        } else {
            CALCULATIONS_PER_TICK = config.getInt("calculations-per-tick");
        }

        if (!config.contains("smoke-thickness")) {
            SMOKE_THICKNESS = 1.0;
            config.set("smoke-thickness", 1.0);
        } else {
            SMOKE_THICKNESS = config.getDouble("smoke-thickness");
        }

        if (!config.contains("grenade-menu-title")) {
            GUI_TITLE = "&8&lGrenades";
            config.set("grenade-menu-title", GUI_TITLE);
        } else {
            GUI_TITLE = config.getString("grenade-menu-title");
        }


        var keys = config.getConfigurationSection("grenades").getKeys(false);
        for (var grenadeID : keys) {
            var section = config.getConfigurationSection("grenades." + grenadeID);
            if (section == null) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " section in the config is null.");
                continue;
            }

            var name = section.getString("name");
            if (name == null) {
                name = "Unnamed Grenade";
                section.set("name", name);
            }

            var lore = section.getStringList("lore");

            var texture = section.getString("texture");
            if (texture == null) {
                texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M2MDNjNzk1NjAzMTk5OTZkNjM5NDEyOGI0OWZlYzc2NTBjZjg2N2ExZTQ4ZmI4MGM2MDQzZTc3MGRkNzFiZCJ9fX0";
                section.set("texture", texture);
            }

            double bounciness;
            if (!section.contains("bounciness")) {
                bounciness = 0.0;
                section.set("bounciness", bounciness);
            } else {
                bounciness = section.getDouble("bounciness");
            }

            double airResistance;
            if (!section.contains("air-resistance")) {
                airResistance = 0.0;
                section.set("air-resistance", airResistance);
            } else {
                airResistance = section.getDouble("air-resistance");
            }

            double waterResistance;
            if (!section.contains("water-resistance")) {
                waterResistance = 0.0;
                section.set("water-resistance", waterResistance);
            } else {
                waterResistance = section.getDouble("water-resistance");
            }

            int fuseTime;
            if (!section.contains("fuse-time")) {
                fuseTime = 10;
                section.set("fuse-time", fuseTime);
            } else {
                fuseTime = section.getInt("fuse-time");
            }

            int despawnTime;
            if (!section.contains("despawn-time")) {
                despawnTime = 15;
                section.set("despawn-time", despawnTime);
            } else {
                despawnTime = section.getInt("despawn-time");
            }

            double directHitDamage;
            if (!section.contains("direct-hit-damage")) {
                directHitDamage = 0.0;
                section.set("direct-hit-damage", directHitDamage);
            } else {
                directHitDamage = section.getDouble("direct-hit-damage");
            }


            float blastRadius;
            if (!section.contains("blast-radius")) {
                blastRadius = 0.0F;
                section.set("blast-radius", blastRadius);
            } else {
                blastRadius = (float) section.getDouble("blast-radius");
            }

            float smokeRadius;
            if (!section.contains("smoke-radius")) {
                smokeRadius = 0.0F;
                section.set("smoke-radius", smokeRadius);
            } else {
                smokeRadius = (float) section.getDouble("smoke-radius");
            }

            float fireRadius;
            if (!section.contains("fire-radius")) {
                fireRadius = 0.0F;
                section.set("fire-radius", fireRadius);
            } else {
                fireRadius = (float) section.getDouble("fire-radius");
            }

            float destructionRadius;
            if (!section.contains("destruction-radius")) {
                destructionRadius = 0.0F;
                section.set("destruction-radius", destructionRadius);
            } else {
                destructionRadius = (float) section.getDouble("destruction-radius");
            }

            float flashRadius;
            if (!section.contains("flash-radius")) {
                flashRadius = 0.0F;
                section.set("flash-radius", flashRadius);
            } else {
                flashRadius = (float) section.getDouble("flash-radius");
            }

            double weight;
            if (!section.contains("weight")) {
                weight = 0.0F;
                section.set("weight", weight);
            } else {
                weight = section.getDouble("weight");
            }

            boolean hasGravity;
            if (!section.contains("gravity")) {
                hasGravity = true;
                section.set("gravity", true);
            } else {
                hasGravity = section.getBoolean("gravity");
            }

            boolean hasSmokeTrail;
            if (!section.contains("smoke-trail")) {
                hasSmokeTrail = true;
                section.set("smoke-trail", true);
            } else {
                hasSmokeTrail = section.getBoolean("smoke-trail");
            }


            boolean explodeOnContact;
            if (!section.contains("explode-on-contact")) {
                explodeOnContact = false;
                section.set("explode-on-contact", false);
            } else {
                explodeOnContact = section.getBoolean("explode-on-contact");
            }

            boolean beeps;
            if (!section.contains("beeps")) {
                beeps = false;
                section.set("beeps", false);
            } else {
                beeps = section.getBoolean("beeps");
            }


            var recipeSection = section.getConfigurationSection("recipe");
            if (recipeSection == null) {
                recipeSection = section.createSection("recipe",
                        ImmutableMap.of(
                                "is-shaped", true,
                                "amount", 1,
                                "1A", "BEDROCK",
                                "1B", "BEDROCK",
                                "1C", "BEDROCK"
                        ));
            }

            boolean isShaped;
            if (!recipeSection.contains("is-shaped")) {
                isShaped = false;
                recipeSection.set("is-shaped", false);
            } else {
                isShaped = recipeSection.getBoolean("is-shaped");
            }

            int amount;
            if (!recipeSection.contains("amount")) {
                amount = 1;
                recipeSection.set("amount", amount);
            } else {
                amount = recipeSection.getInt("amount");
            }

            var strIngredients = new String[9];
            strIngredients[0] = recipeSection.getString("1A");
            strIngredients[1] = recipeSection.getString("1B");
            strIngredients[2] = recipeSection.getString("1C");
            strIngredients[3] = recipeSection.getString("2A");
            strIngredients[4] = recipeSection.getString("2B");
            strIngredients[5] = recipeSection.getString("2C");
            strIngredients[6] = recipeSection.getString("3A");
            strIngredients[7] = recipeSection.getString("3B");
            strIngredients[8] = recipeSection.getString("3C");

            var allNull = true;
            for (var item : strIngredients) {
                if (item != null) {
                    allNull = false;
                    break;
                }
            }

            if (allNull) {
                System.out.println("[RealisticGrenades] Error: The " + grenadeID + " recipe configuration requires at least one ingredient.");
                continue;
            }

            var ingredients = new Material[9];

            var unknown = false;
            for (int i = 0; i < 9; i++) {
                var str = strIngredients[i];
                if (str == null) {
                    ingredients[i] = null;
                } else {
                    var ingredient = Material.getMaterial(str);
                    if (ingredient == null) {
                        unknown = true;
                        System.out.println("[RealisticGrenades] Error: The " + grenadeID + " recipe configuration contains an unknown ingredient: " + str + ".");
                        break;
                    } else {
                        ingredients[i] = ingredient;
                    }
                }
            }
            if (unknown) continue;

            var grenade = ItemManager.getInstance().createGrenade(
                    texture,
                    grenadeID,
                    name,
                    bounciness,
                    airResistance,
                    waterResistance,
                    fuseTime,
                    despawnTime,
                    directHitDamage,
                    blastRadius,
                    smokeRadius,
                    fireRadius,
                    destructionRadius,
                    flashRadius,
                    weight,
                    hasGravity,
                    hasSmokeTrail,
                    explodeOnContact,
                    beeps,
                    lore
            );
            grenade.setAmount(amount);


            if (isShaped) {
                char c = 0;
                var ingredientMap = new HashMap<Material, Character>();
                var recipe = new ShapedRecipe(new NamespacedKey(plugin, grenadeID + "_recipe"), grenade);
                var chars = new ArrayList<Character>();
                for (var ingredient : ingredients) {
                    if (ingredient == null) {
                        chars.add(' ');
                    } else {
                        if (ingredientMap.containsKey(ingredient)) {
                            chars.add(ingredientMap.get(ingredient));
                        } else {
                            ingredientMap.put(ingredient, c);
                            chars.add(c);
                            c++;
                        }
                    }
                }
                StringBuilder string1 = new StringBuilder();
                StringBuilder string2 = new StringBuilder();
                StringBuilder string3 = new StringBuilder();

                var aEmpty = chars.get(0) == ' ' && chars.get(3) == ' ' && chars.get(6) == ' ';
                var bEmpty = chars.get(1) == ' ' && chars.get(4) == ' ' && chars.get(7) == ' ';
                var cEmpty = chars.get(2) == ' ' && chars.get(5) == ' ' && chars.get(8) == ' ';
                var oneEmpty = chars.get(0) == ' ' && chars.get(1) == ' ' && chars.get(2) == ' ';
                var twoEmpty = chars.get(3) == ' ' && chars.get(4) == ' ' && chars.get(5) == ' ';
                var threeEmpty = chars.get(6) == ' ' && chars.get(7) == ' ' && chars.get(8) == ' ';
                if (!aEmpty) {
                    string1.append(chars.get(0));
                    string2.append(chars.get(3));
                    string3.append(chars.get(6));
                }
                if (!(aEmpty && bEmpty) && !(bEmpty && cEmpty)) {
                    string1.append(chars.get(1));
                    string2.append(chars.get(4));
                    string3.append(chars.get(7));
                }
                if (!cEmpty) {
                    string1.append(chars.get(2));
                    string2.append(chars.get(5));
                    string3.append(chars.get(8));
                }
                var sequenceList = new ArrayList<String>();
                if (!oneEmpty) {
                    sequenceList.add(string1.toString());
                }
                if (!(oneEmpty && twoEmpty) && !(twoEmpty && threeEmpty)) {
                    sequenceList.add(string2.toString());
                }
                if (!threeEmpty) {
                    sequenceList.add(string3.toString());
                }
                var sequenceArray = new String[sequenceList.size()];
                sequenceList.toArray(sequenceArray);
                recipe.shape(sequenceArray);
                for (var ingredient : ingredientMap.keySet()) {
                    recipe.setIngredient(ingredientMap.get(ingredient), ingredient);
                }
                Bukkit.getServer().addRecipe(recipe);
                grenadeRecipes.put(grenadeID, recipe);
            } else {
                var recipe = new ShapelessRecipe(new NamespacedKey(plugin, grenadeID + "_recipe"), grenade);
                var ingredientMap = new HashMap<Material, Integer>();

                for (var ingredient : ingredients) {
                    if (ingredient != null) {
                        if (ingredientMap.containsKey(ingredient)) {
                            var count = ingredientMap.get(ingredient);
                            count++;
                            ingredientMap.replace(ingredient, count);
                        } else {
                            ingredientMap.put(ingredient, 1);
                        }
                    }
                }
                for (var ingredient : ingredientMap.keySet()) {
                    recipe.addIngredient(ingredientMap.get(ingredient), ingredient);
                }
                Bukkit.getServer().addRecipe(recipe);
                grenadeRecipes.put(grenadeID, recipe);
            }
        }
        plugin.saveConfig();
    }
}
