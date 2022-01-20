package me.apeiros.villagerutil.items.wands;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

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

public class CureWand extends SlimefunItem {

    // Creates Villager Cure Wand
    public CureWand(ItemGroup ig) {
        super(ig, Setup.CURE_WAND, "VU_CURE_WAND", RecipeType.ANCIENT_ALTAR, new ItemStack[] {
            SlimefunItems.VILLAGER_RUNE, SlimefunItems.MAGICAL_ZOMBIE_PILLS, Setup.TOKEN,
            SlimefunItems.MAGICAL_ZOMBIE_PILLS, new ItemStack(Material.END_ROD), new ItemStack(Material.GOLDEN_APPLE),
            Setup.TOKEN, Utils.makePotion(new PotionData(PotionType.WEAKNESS, false, true)), SlimefunItems.STAFF_ELEMENTAL
        });
    }

    // Creates and returns handler
    private EntityInteractHandler getEntityInteractHandler() {
        return (e, i, offhand) -> {
            // Cancel event
            e.setCancelled(true);
            
            // Check if the clicked entity is a zombie villager
            Entity en = e.getRightClicked();
            if (en instanceof ZombieVillager) {
                ZombieVillager zv = (ZombieVillager) en;
                Player p = e.getPlayer();
                Inventory inv = p.getInventory();

                // Check for villager tokens and permission
                if (inv.contains(Setup.TOKEN) && 
                    Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), Interaction.INTERACT_ENTITY)) {

                    // Cure zombie villager
                    zv.setConversionTime(1);
                    zv.setConversionPlayer(p);

                    // Consume villager token
                    inv.removeItem(new SlimefunItemStack(Setup.TOKEN, 1));
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
