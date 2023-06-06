package me.apeiros.villagerutil.items.wands;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
            Player p = e.getPlayer();
            Entity en = e.getRightClicked();
            if (Utils.isValidVillager(p, en)) {
                // Store villager, player, and inventory
                Villager v = (Villager) en;
                Inventory inv = p.getInventory();

                // Check if villager is a nitwit
                if (v.getProfession() != Profession.NITWIT) {
                    p.sendMessage(ChatColors.color("&cThis villager is not a nitwit!"));
                    v.shakeHead();
                    return;
                }

                // Check for villager tokens
                if (!Utils.hasToken(p, inv)) {
                    p.sendMessage(ChatColors.color("&cInsufficient Villager Tokens!"));
                    v.shakeHead();
                    return;
                }

                // Set villager's profession to NONE
                Utils.removeProfession(v);

                // Play effects
                World w = v.getWorld();
                Location l = v.getLocation();
                w.playSound(l, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1F, 1.5F);
                w.playSound(l, Sound.BLOCK_BEACON_POWER_SELECT, 1F, 1F);
                w.spawnParticle(Particle.VILLAGER_HAPPY, l, 50);

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
