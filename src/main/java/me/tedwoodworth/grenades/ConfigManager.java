package me.tedwoodworth.grenades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
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
        CALCULATIONS_PER_TICK = config.getInt("calculations-per-tick");
        SMOKE_THICKNESS = config.getDouble("smoke-thickness");
        var keys = config.getConfigurationSection("grenades").getKeys(false);
        for (var grenadeID : keys) {
            var section = config.getConfigurationSection("grenades." + grenadeID);
            if (section == null) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " section in the config is null.");
                continue;
            }

            var name = section.getString("name");
            if (name == null) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'name' setting in the config.");
                continue;
            }

            var lore = section.getStringList("lore");

            var texture = section.getString("texture");
            if (texture == null) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'texture' setting in the config.");
                continue;
            }

            if (!section.contains("bounciness")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'bounciness' setting in the config.");
                continue;
            }
            var bounciness = section.getDouble("bounciness");


            if (!section.contains("air-resistance")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'air-resistance' setting in the config.");
                continue;
            }
            var airResistance = section.getDouble("air-resistance");

            if (!section.contains("water-resistance")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'water-resistance' setting in the config.");
                continue;
            }
            var waterResistance = section.getDouble("water-resistance");

            if (!section.contains("fuse-time")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'fuse-time' setting in the config.");
                continue;
            }
            var fuseTime = section.getInt("fuse-time");

            if (!section.contains("despawn-time")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'despawn-time' setting in the config.");
                continue;
            }
            var despawnTime = section.getInt("despawn-time");

            if (!section.contains("direct-hit-damage")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'direct-hit-damage' setting in the config.");
                continue;
            }
            var directHitDamage = section.getDouble("direct-hit-damage");

            if (!section.contains("blast-radius")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'blast-radius' setting in the config.");
                continue;
            }
            var blastRadius = (float) section.getDouble("blast-radius");

            if (!section.contains("smoke-radius")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'smoke-radius' setting in the config.");
                continue;
            }
            var smokeRadius = (float) section.getDouble("smoke-radius");

            if (!section.contains("fire-radius")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'fire-radius' setting in the config.");
                continue;
            }
            var fireRadius = (float) section.getDouble("fire-radius");

            if (!section.contains("destruction-radius")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'destruction-radius' setting in the config.");
                continue;
            }
            var destructionRadius = (float) section.getDouble("destruction-radius");

            if (!section.contains("weight")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'weight' setting in the config.");
                continue;
            }
            var weight = section.getDouble("weight");

            if (!section.contains("gravity")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'gravity' setting in the config.");
                continue;
            }
            var hasGravity = section.getBoolean("gravity");

            if (!section.contains("smoke-trail")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'smoke-trail' setting in the config.");
                continue;
            }
            var hasSmokeTrail = section.getBoolean("smoke-trail");

            if (!section.contains("explode-on-contact")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'explode-on-contact' setting in the config.");
                continue;
            }
            var explodeOnContact = section.getBoolean("explode-on-contact");

            if (!section.contains("beeps")) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'beeps' setting in the config.");
                continue;
            }
            var beeps = section.getBoolean("beeps");
            var recipeSection = section.getConfigurationSection("recipe");
            if (recipeSection == null) {
                System.out.println("[RealisticGrenades] Error: " + grenadeID + " is missing the 'recipe' setting in the config.");
                continue;
            }

            if (!recipeSection.contains("is-shaped")) {
                System.out.println("[RealisticGrenades] Error: The " + grenadeID + " recipe configuration is missing the 'is-shaped' setting.");
                continue;
            }
            var isShaped = recipeSection.getBoolean("is-shaped");

            if (!recipeSection.contains("amount")) {
                System.out.println("[RealisticGrenades] Error: The " + grenadeID + " recipe configuration is missing the 'amount' setting.");
                continue;
            }
            var amount = recipeSection.getInt("amount");

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
            }
        }
    }
}
