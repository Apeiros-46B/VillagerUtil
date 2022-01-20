package me.apeiros.villagerutil.items.wands;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
import me.apeiros.villagerutil.util.Utils;

public class NitwitWand extends SlimefunItem {

    // Creates Nitwit Wand
    public NitwitWand(ItemGroup ig) {
        super(ig, Setup.NITWIT_WAND, "VU_NITWIT_WAND", RecipeType.ANCIENT_ALTAR, new ItemStack[] {
            SlimefunItems.VILLAGER_RUNE, SlimefunItems.ADVANCED_CIRCUIT_BOARD, Setup.TOKEN,
            SlimefunItems.ADVANCED_CIRCUIT_BOARD, new ItemStack(Material.END_ROD), SlimefunItems.SYNTHETIC_DIAMOND,
            Setup.TOKEN, SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.STAFF_ELEMENTAL
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

                    // Check if villager is a nitwit
                    if (v.getProfession() == Profession.NITWIT) {
                        // Set villager's profession to NONE
                        Utils.removeProfession(v);

                        // Consume villager token
                        inv.removeItem(new SlimefunItemStack(Setup.TOKEN, 1));
                    } else {
                        p.sendMessage(ChatColors.color("&cThis villager is not a nitwit!"));
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
    
}
