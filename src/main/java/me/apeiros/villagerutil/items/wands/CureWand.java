package me.apeiros.villagerutil.items.wands;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;

import me.apeiros.villagerutil.Setup;

public class CureWand extends SlimefunItem {

    public CureWand(ItemGroup ig) {
        super(ig, Setup.CURE_WAND, "VU_CURE_WAND", RecipeType.ANCIENT_ALTAR, new ItemStack[] {
            SlimefunItems.VILLAGER_RUNE, SlimefunItems.MAGICAL_ZOMBIE_PILLS, Setup.VILLAGER_TOKEN,
            SlimefunItems.MAGICAL_ZOMBIE_PILLS, new ItemStack(Material.END_ROD), new ItemStack(Material.GOLDEN_APPLE),
            Setup.VILLAGER_TOKEN, SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.STAFF_ELEMENTAL
        });
    }

    private EntityInteractHandler getEntityInteractHandler() {
        return (e, i, offhand) -> {
            // Cancel the right click event
            e.setCancelled(true);
            
            // Check if the clicked entity is a zombie villager
            Entity en = e.getRightClicked();
            if (en instanceof ZombieVillager) {
                ZombieVillager zv = (ZombieVillager) en;
                Player p = e.getPlayer();
                Inventory inv = p.getInventory();

                // Check if the player has at least one villager token
                if (inv.contains(Setup.VILLAGER_TOKEN)) {
                    // Cure zombie villager
                    zv.setConversionTime(1);
                    zv.setConversionPlayer(p);

                    // Consume token
                    inv.removeItem(new SlimefunItemStack(Setup.VILLAGER_TOKEN, 1));
                } else {
                    p.sendMessage(ChatColors.color("&cInsufficient Villager Tokens!"));
                }
            }
        };
    }

    public void preRegister() {
        this.addItemHandler(getEntityInteractHandler());
    }
    
}
