package me.tedwoodworth.grenades;

import de.themoep.inventorygui.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

public class GrenadeGui {
    private static InventoryGui gui = null;
    private static final HashMap<String, InventoryGui> recipes = new HashMap<>();

    public static InventoryGui getGrenadeGui() {
        if (gui == null) {
            createGui();
        }
        return gui;
    }

    private static void createGui() {
        var title = "" + ChatColor.DARK_GREEN + ChatColor.BOLD + "Grenades";

        // Create setup
        var guiSetup = new String[]{
                "         ",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                " P     N ",
        };

        // create item gui
        var itemGui = new InventoryGui(RealisticGrenades.getInstance(), title, guiSetup);
        itemGui.setCloseAction(close -> false);
        itemGui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        itemGui.setSilent(false);

        // create next page
        var pageElement = new GuiPageElement('N',
                new ItemStack(Material.LIME_STAINED_GLASS_PANE),
                GuiPageElement.PageAction.NEXT,
                ChatColor.GREEN + "Next Page");
        itemGui.addElement(pageElement);

        // create previous page
        pageElement = new GuiPageElement('P',
                new ItemStack(Material.RED_STAINED_GLASS_PANE),
                GuiPageElement.PageAction.PREVIOUS,
                ChatColor.RED + "Previous Page");
        itemGui.addElement(pageElement);


        // create item group
        var itemGroup = new GuiElementGroup('i');

        var ids = ItemManager.getInstance().getGrenadeIDs();
        ids.sort(String::compareToIgnoreCase);

        for (var id : ids) {
            var grenade = ItemManager.getInstance().getGrenade(id);

            var element = new StaticGuiElement('i', grenade, 1,
                    click -> {
                        var clicker = click.getEvent().getWhoClicked();
                        ((Player) clicker).playSound(clicker.getLocation(), Sound.UI_BUTTON_CLICK, 0.2f, 1);
                        recipes.get(id).show(clicker);
                        return true;
                    },
                    grenade.getItemMeta().getDisplayName(),
                    ChatColor.BLUE + "Click: " + ChatColor.GRAY + "View recipe"
            );

            itemGroup.addElement(element);
            var display = grenade.getItemMeta().getDisplayName();
            title = "" + ChatColor.DARK_GRAY + ChatColor.BOLD + display;
            guiSetup = new String[]{
                    " B  T    ",
                    "         ",
                    " abc AAA ",
                    " def AjA ",
                    " ghi AAA ",
                    "         ",
            };

            var recipeGui = new InventoryGui(RealisticGrenades.getInstance(), title, guiSetup);
            recipeGui.setCloseAction(close -> false);
            recipeGui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            itemGui.setSilent(false);
            recipeGui.addElement(new StaticGuiElement('A', new ItemStack(Material.AIR)));
            recipeGui.addElement(new GuiBackElement('B',
                    new ItemStack(Material.ARROW),
                    "" + ChatColor.YELLOW + ChatColor.BOLD + "Go Back",
                    ChatColor.BLUE + "Click: " + ChatColor.GRAY + "Return to previous page"
            ));
            recipeGui.addElement(new StaticGuiElement('T',
                    new ItemStack(Material.CRAFTING_TABLE),
                    "" + ChatColor.YELLOW + ChatColor.BOLD + "Crafting Table",
                    ChatColor.GRAY + "Item created in a crafting table"
            ));

            var recipe = ConfigManager.grenadeRecipes.get(id);
            if (recipe instanceof ShapedRecipe) {
                var r = (ShapedRecipe) recipe;
                var shape = r.getShape();
                var choices = r.getIngredientMap();
                var c = 'a';

                for (var string : shape) {
                    for (var chr : string.toCharArray()) {
                        if (chr != ' ') {
                            var choice = choices.get(chr);
                            var e = new StaticGuiElement(c, choice, click -> true, display);
                            recipeGui.addElement(e);
                        }
                        c++;

                    }
                }
            } else if (recipe instanceof ShapelessRecipe) {
                var r = (ShapelessRecipe) recipe;
                var choices = r.getIngredientList();
                var c = 'a';
                var i = 0;
                for (var choice : choices) {
                    var e = new StaticGuiElement(c, choice, Objects.requireNonNull(choice.getItemMeta()).getDisplayName());
                    recipeGui.addElement(e);
                    c++;
                    i++;
                }
                while (i < 9) {
                    var e = new StaticGuiElement(c, new ItemStack(Material.AIR));
                    recipeGui.addElement(e);
                    c++;
                    i++;
                }

            } else {
                System.out.println("[RealisticGrenades] Error loading recipe for grenade with ID " + id + ": Unknown class.");
                continue;
            }

            recipeGui.addElement(new StaticGuiElement('A', new ItemStack(Material.AIR)));
            var lore = Objects.requireNonNull(grenade.getItemMeta()).getLore();
            StaticGuiElement g;
            if (lore == null) {
                g = new StaticGuiElement('j', grenade, recipe.getResult().getAmount(), click -> true, display);
            } else {
                lore.add(0, display);
                g = new StaticGuiElement('j', grenade, recipe.getResult().getAmount(), click -> true, lore.toArray(new String[0]));
            }
            recipeGui.addElement(g);

            recipes.put(id, recipeGui);
        }

        itemGui.addElement(itemGroup);
        gui = itemGui;
    }
}
