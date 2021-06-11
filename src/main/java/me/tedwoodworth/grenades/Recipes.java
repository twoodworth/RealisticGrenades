package me.tedwoodworth.grenades;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class Recipes {
    private static Recipes instance = null;

    public static Recipes getInstance() {
        if (instance == null) {
            instance = new Recipes();
        }
        return instance;
    }

    public void loadRecipes() {
        // Frag grenade
        var fragGrenade = ItemManager.getInstance().getGrenade(Grenade.FRAG, 2);
        var fragRecipe = new ShapedRecipe(new NamespacedKey(RealisticGrenades.getInstance(), "frag_recipe"), fragGrenade);
        fragRecipe.shape("RNN", "NGN", "NNN");
        fragRecipe.setIngredient('N', Material.IRON_NUGGET);
        fragRecipe.setIngredient('R', Material.REDSTONE);
        fragRecipe.setIngredient('G', Material.GUNPOWDER);
        RealisticGrenades.getInstance().getServer().addRecipe(fragRecipe);
    }
}