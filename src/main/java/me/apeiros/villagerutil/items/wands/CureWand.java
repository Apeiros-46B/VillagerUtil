package me.apeiros.villagerutil.items.wands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
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
            Setup.TOKEN, Utils.makePotion(new PotionData(PotionType.WEAKNESS, false, false)), SlimefunItems.STAFF_ELEMENTAL
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
                // Store zombie villager, player, and inventory
                ZombieVillager zv = (ZombieVillager) en;
                Player p = e.getPlayer();
                Inventory inv = p.getInventory();

                // Check for permission
                if (!Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), Interaction.INTERACT_ENTITY)) {
                    p.sendMessage(ChatColors.color("&cYou don't have permission!"));
                    return;
                }

                // Check for villager tokens
                if (!Utils.hasToken(p, inv)) {
                    p.sendMessage(ChatColors.color("&cInsufficient Villager Tokens!"));
                    return;
                }

                // Effects
                World w = zv.getWorld();
                Location l = zv.getLocation();
                w.playSound(l, Sound.ITEM_TOTEM_USE, 0.3F, 1F);
                w.spawnParticle(Particle.TOTEM, l, 30);

                // Cure zombie villager
                zv.setConversionTime(1);
                zv.setConversionPlayer(p);

                // Consume villager token
                Utils.removeToken(p, inv);
            }
        };
    }

    // Registers handler
    public void preRegister() {
        this.addItemHandler(getEntityInteractHandler());
    }
    
}
