package me.apeiros.villagerutil.commands;

import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;

import me.apeiros.villagerutil.VillagerUtil;
import me.apeiros.villagerutil.items.wands.TradeWand;

public class ResetVillagerCommand implements CommandExecutor {

    // Number pattern
    Pattern numPattern = Pattern.compile("^[0-9]$");

    // Constructor which initializes the command
    public ResetVillagerCommand(VillagerUtil p) {
        p.getCommand("resetvillager").setExecutor(this);
    }

    // Triggers when the command is run
    // Uses command number to check if command was run
    // using a Villager Trade Wand
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (sender instanceof Player) {
            Player p = (Player) sender;
            int commandNumber;

            // Try to parse command number, if it's not a number set it to -1
            if (numPattern.matcher(args[0]).matches()) {
                commandNumber = Integer.parseInt(args[0]);
            } else {
                commandNumber = -1;
            }
            
            // Check if the command number if correct
            if (commandNumber == VillagerUtil.getCommandNumber()) {
                // Reset villager's trades
                TradeWand.resetLockedTrades(p);
            } else {
                p.sendMessage(ChatColors.color("&cInvalid command!"));
            }
        } else {
            sender.sendMessage(ChatColors.color("&cOnly players are able to execute this command!"));
        }

        return true;
    }

}