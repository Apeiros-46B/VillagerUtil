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
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;

import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.utils.Utils;

public class NitwitWand extends SlimefunItem {

    public NitwitWand(ItemGroup ig) {
        super(ig, Setup.NITWIT_WAND, "VU_NITWIT_WAND", RecipeType.ANCIENT_ALTAR, new ItemStack[] {
            SlimefunItems.VILLAGER_RUNE, SlimefunItems.ADVANCED_CIRCUIT_BOARD, Setup.VILLAGER_TOKEN,
            SlimefunItems.ADVANCED_CIRCUIT_BOARD, new ItemStack(Material.END_ROD), SlimefunItems.SYNTHETIC_DIAMOND,
            Setup.VILLAGER_TOKEN, SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.STAFF_ELEMENTAL
        });
    }

    private EntityInteractHandler getEntityInteractHandler() {
        return (e, i, offhand) -> {
            e.setCancelled(true);
            
            Entity en = e.getRightClicked();
            if (en instanceof Villager) {
                Villager v = (Villager) en;
                Player p = e.getPlayer();
                Inventory inv = p.getInventory();

                if (inv.contains(Setup.VILLAGER_TOKEN)) {
                    if (v.getProfession() == Profession.NITWIT) {
                        Utils.removeProfession(v);
                        inv.removeItem(new SlimefunItemStack(Setup.VILLAGER_TOKEN, 1));
                    } else {
                        p.sendMessage(ChatColors.color("&cThis villager is not a nitwit!"));
                    }
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
