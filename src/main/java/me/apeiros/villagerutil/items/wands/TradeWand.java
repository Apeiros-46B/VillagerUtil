package me.apeiros.villagerutil.items.wands;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.VillagerUtil;
import me.apeiros.villagerutil.util.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.WeakReference;

public class TradeWand extends SlimefunItem implements Listener {

    // Weak Reference to a villager
    private static WeakReference<Villager> villagerRef;

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
            Player p = e.getPlayer();
            Entity en = e.getRightClicked();
            if (Utils.isValidVillager(p, en)) {
                // Store villager, profession, player, and inventory
                Villager v = (Villager) en;
                Profession prof = v.getProfession();
                Inventory inv = p.getInventory();

                // Check if the villager has no job or is a nitwit
                if (prof == Profession.NONE || prof == Profession.NITWIT) {
                    p.sendMessage(ChatColors.color("&cThis villager does not have a job!"));
                    v.shakeHead();
                    return;
                }

                // Check for villager tokens
                if (!Utils.hasToken(p, inv)) {
                    p.sendMessage(ChatColors.color("&cInsufficient Villager Tokens!"));
                    v.shakeHead();
                    return;
                }
                
                // Check if the villager's trades are locked
                if (Utils.villagerTradesLocked(v)) {
                    // Reference the villager and its profession
                    villagerRef = new WeakReference<>(v);
                    
                    // Ask user for confirmation to reset trades
                    sendWarning(p);
                } else {
                    // Remove profession
                    Utils.removeProfession(v);

                    // Add back profession
                    v.setProfession(prof);

                    // Play sounds
                    World w = v.getWorld();
                    Location l = v.getLocation();
                    w.playSound(l, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1F, 1F);
                    w.playSound(l, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1F, 1F);

                    // Consume villager token
                    Utils.removeToken(p, inv);
                }
            }
        };
    }

    // Registers handler
    public void preRegister() {
        this.addItemHandler(getEntityInteractHandler());
    }

    // Sends warning to players trying to reset a locked villager
    private void sendWarning(Player p) {
        // Send normal messages
        p.sendMessage(ChatColors.color("&cThis villager has its trades locked."));
        p.sendMessage(ChatColors.color("&cAre you sure you want to reset its trades?"));

        // Create component
        BaseComponent message = new TextComponent(ChatColors.color("&6Click here &7to reset this villager's trades"));
        message.setClickEvent(new ClickEvent(Action.RUN_COMMAND, 
                "/resetvillager " + VillagerUtil.getCommandNumber()));

        // Send component
        p.spigot().sendMessage(message);
    }

    // Resets locked trades of the villager
    public static void resetLockedTrades(Player p) {
        // Get the villager from the reference
        Villager v = villagerRef.get();

        // Check if the villager exists
        if (v == null) {
            p.sendMessage(ChatColors.color("&cConfirmation took too long!"));
            p.sendMessage(ChatColors.color("&cRight click the villager again and click on the chat message."));
        } else {
            // Store the profession and the player's inventory
            Inventory inv = p.getInventory();
            Profession prof = v.getProfession();

            // Whether or not tokens are used
            boolean useTokens = VillagerUtil.useTokens();

            // Check for permission
            if (!Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), Interaction.INTERACT_ENTITY)) {
                p.sendMessage(ChatColors.color("&cYou don't have permission!"));
                v.shakeHead();
                return;
            }

            // Check if the villager has no job or is a nitwit
            if (prof == Profession.NONE || prof == Profession.NITWIT) {
                p.sendMessage(ChatColors.color("&cThis villager does not have a job!"));
                v.shakeHead();
                return;
            }

            // Check for villager tokens
            if (!Utils.hasToken(p, inv)) {
                p.sendMessage(ChatColors.color("&cInsufficient Villager Tokens!"));
                v.shakeHead();
                return;
            }

            // Reset trades and villager exp
            Utils.removeProfessionAndExp(v);

            // Set back the profession
            v.setProfession(prof);

            // Play sounds
            World w = v.getWorld();
            Location l = v.getLocation();
            w.playSound(l, Sound.BLOCK_BEACON_ACTIVATE, 1F, 1F);
            w.playSound(l, Sound.BLOCK_BEACON_POWER_SELECT, 1F, 1F);

            // Consume villager token
            Utils.removeToken(p, inv);

            // Clean up the reference
            villagerRef.clear();
        }
    }
    
}
