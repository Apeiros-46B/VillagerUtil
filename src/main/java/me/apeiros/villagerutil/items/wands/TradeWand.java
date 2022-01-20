package me.apeiros.villagerutil.items.wands;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;

import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.VillagerUtil;
import me.apeiros.villagerutil.util.Utils;

public class TradeWand extends SlimefunItem implements Listener {

    // Weak Reference to a villager
    private static WeakReference<Villager> ref;

    // Creates Villager Trade Wand
    public TradeWand(ItemGroup ig) {
        super(ig, Setup.TRADE_WAND, "VU_TRADE_WAND", RecipeType.ANCIENT_ALTAR, new ItemStack[] {
            SlimefunItems.VILLAGER_RUNE, SlimefunItems.STRANGE_NETHER_GOO, Setup.TOKEN,
            SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, new ItemStack(Material.END_ROD), new ItemStack(Material.EMERALD_BLOCK),
            Setup.TOKEN, new ItemStack(Material.EMERALD_BLOCK), SlimefunItems.STAFF_ELEMENTAL
        });
    }

    // Creates and returns handler
    private EntityInteractHandler getEntityInteractHandler() {
        return (e, i, offhand) -> {
            // Cancel event
            e.setCancelled(true);
            
            // Check if the clicked entity is a villager
            Entity en = e.getRightClicked();
            if (en instanceof Villager) {
                Villager v = (Villager) en;
                Player p = e.getPlayer();
                Inventory inv = p.getInventory();

                // Check for villager tokens and permission
                if (inv.contains(Setup.TOKEN) && 
                    Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), Interaction.INTERACT_ENTITY)) {

                    // Check if the villager's trades are locked
                    if (Utils.villagerTradesLocked(v)) {
                        // Ask user for confirmation to reset trades
                        sendWarning(p, v.getUniqueId());
                    
                        // Reference the villager
                        ref = new WeakReference<>(v);
                    } else {
                        // Remove profession
                        Utils.removeProfession(v);

                        // Consume villager token
                        inv.removeItem(new SlimefunItemStack(Setup.TOKEN, 1));
                    }
                } else {
                    p.sendMessage(ChatColors.color("&cInsufficient Villager Tokens!"));
                }
            }
        };
    }

    // Registers handler
    public void preRegister() {
        this.addItemHandler(getEntityInteractHandler());
    }

    // Sends warning to players trying to reset a locked villager
    private void sendWarning(Player p, UUID id) {
        // Send normal messages
        p.sendMessage(ChatColors.color("&cThis villager has its trades locked."));
        p.sendMessage(ChatColors.color("&cAre you sure you want to reset its trades?"));

        // Create component
        BaseComponent message = new TextComponent(ChatColors.color("&6Click here &7to reset this villager's trades"));
        message.setClickEvent(new ClickEvent(Action.RUN_COMMAND, 
                "resetvillager " + VillagerUtil.getCommandNumber()));

        // Send component
        p.spigot().sendMessage(message);
    }

    // Resets locked trades of the villager associated with the given UUID
    public static void resetLockedTrades(Player p) {
        // Get the villager from the reference
        Villager v = ref.get();

        // Check if the villager exists
        if (v == null) {
            p.sendMessage(ChatColors.color("&cConfirmation took too long!"));
            p.sendMessage(ChatColors.color("&cRight click the villager again and click on the chat message."));
        } else {
            Inventory inv = p.getInventory();

            // Check for villager tokens and permission
            if (inv.contains(Setup.TOKEN) && 
                Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), Interaction.INTERACT_ENTITY)) {

                // Reset trades and villager exp
                Utils.removeProfessionAndExp(v);

                // Consume villager token
                inv.removeItem(new SlimefunItemStack(Setup.TOKEN, 1));

                // Clean up the reference
                ref.clear();
            } else {
                p.sendMessage(ChatColors.color("&cInsufficient Villager Tokens!"));
            }
            
        }
    }
    
}
