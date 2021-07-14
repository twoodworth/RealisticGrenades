package me.tedwoodworth.grenades;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("grenades")) {
            grenades(sender);
        } else if (command.getName().equalsIgnoreCase("rg")) {
            if (args.length == 0) {
                rg(sender);
            } else if (args[0].equalsIgnoreCase("give")) {
                if (args.length == 3) {
                    give(sender, args[1], args[2], "64");
                } else if (args.length == 4) {
                    give(sender, args[1], args[2], args[3]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Incorrect usage, try: /rg give <player> <grenade id> <amount>");
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("rg")) {
            if (args.length == 0) {
                return null;
            } else if (args.length == 1) {
                return Collections.singletonList(
                        "give"
                );
            } else if (args[0].equalsIgnoreCase("give")) {
                if (args.length == 2) {
                    return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).filter(p -> p.toLowerCase().contains(args[1].toLowerCase())).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
                } else if (args.length == 3) {
                    return ItemManager.getInstance().getGrenadeIDs().stream().filter(g -> g.toLowerCase().contains(args[2].toLowerCase())).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
                } else {
                    return Collections.emptyList();
                }
            }
        }
        return null;
    }

    public static void register(PluginCommand pluginCommand) {
        var commands = new Commands();

        pluginCommand.setExecutor(commands);
        pluginCommand.setTabCompleter(commands);
    }

    private void grenades(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Must be a player to use this command.");
            return;
        }
        if (sender.hasPermission("realisticgrenades.grenades")) {
            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.4f, 2);
            GrenadeGui.getGrenadeGui().show((Player) sender);
        }
    }

    private void rg(CommandSender sender) {
        var lines = new ArrayList<String>();
        if (sender.hasPermission("realisticgrenades.admin.give")) {
            lines.add(ChatColor.YELLOW + "/rg give <player> <grenade id> <amount>");
        }
        if (lines.size() == 0) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        } else {
            sender.sendMessage("" + ChatColor.YELLOW + ChatColor.BOLD + "RealisticGrenades Admin Commands");
            for (var line : lines) {
                sender.sendMessage(line);
            }
        }
    }

    private void give(CommandSender sender, String strPlayer, String id, String strAmt) {
        if (!sender.hasPermission("realisticgrenades.admin.give")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }

        var player = Bukkit.getPlayer(strPlayer);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }

        var ids = ItemManager.getInstance().getGrenadeIDs();
        for (var tempId : ids) {
            if (tempId.equalsIgnoreCase(id)) {
                id = tempId;
                break;
            }
        }
        var grenade = ItemManager.getInstance().getGrenade(id);
        if (grenade == null) {
            sender.sendMessage(ChatColor.RED + "Unknown grenade ID");
            return;
        }

        int amt;
        try {
            amt = Integer.parseInt(strAmt);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number");
            return;
        }

        if (amt < 1) {
            sender.sendMessage("" + ChatColor.RED + amt + " is too few. Must be at least 1.");
            return;
        }
        if (amt > 64) {
            sender.sendMessage("" + ChatColor.RED + amt + " is too many. Must be at most 64.");
            return;
        }

        grenade.setAmount(amt);
        var leftovers = player.getInventory().addItem(grenade);
        for (var i : leftovers.keySet()) {
            player.getWorld().dropItem(player.getLocation(), leftovers.get(i));
        }

        if (sender.equals(player)) {
            player.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.RED + amt + ChatColor.GREEN + " of " + ChatColor.RED + id + ChatColor.GREEN + ".");
        } else {
            player.sendMessage(ChatColor.RED + sender.getName() + ChatColor.GREEN + " has given you " + ChatColor.RED + amt + ChatColor.GREEN + " of " + ChatColor.RED + id + ChatColor.GREEN + ".");
            sender.sendMessage(ChatColor.RED + player.getName() + ChatColor.GREEN + " has been given " + ChatColor.RED + amt + ChatColor.GREEN + " of " + ChatColor.RED + id + ChatColor.GREEN + ".");
        }
    }

}
