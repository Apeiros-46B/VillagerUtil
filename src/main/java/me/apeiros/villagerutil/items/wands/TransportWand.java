package me.apeiros.villagerutil.items.wands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

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
import me.apeiros.villagerutil.util.UUIDTagType;
import me.apeiros.villagerutil.util.Utils;

public class TransportWand extends SlimefunItem {

    // Charm lore when there is no villager stored
    private final List<String> linkedVillagerLore = List.of(
        "&7A magical charm which will teleport",
        "&7the associated villager to its location",
        "&eRight Click &7to teleport the villager",
        "",
        "&aVillager linked!",
        "",
        "&bTool &9&o(Villager Utils)"
    );

    // NamespacedKey for PDC
    private final NamespacedKey key = Utils.key("stored_villager_uuid");

    // Creates Villager Transport Wand
    public TransportWand(ItemGroup ig) {
        super(ig, Setup.TRANSPORT_WAND, "VU_TRANSPORT_WAND", RecipeType.ANCIENT_ALTAR, new ItemStack[] {
            SlimefunItems.VILLAGER_RUNE, new ItemStack(Material.MINECART), Setup.TOKEN,
            new ItemStack(Material.MINECART), new ItemStack(Material.END_ROD), new ItemStack(Material.EMERALD_BLOCK),
            Setup.TOKEN, new ItemStack(Material.EMERALD_BLOCK), SlimefunItems.STAFF_ELEMENTAL
        });
    }

    // Creates and returns handler
    private EntityInteractHandler getEntityInteractHandler() {
        return (e, i, offhand) -> {
            // Cancel event
            e.setCancelled(true);

            Entity en = e.getRightClicked();
            if (en instanceof Villager) {
                Villager v = (Villager) en;
                Player p = e.getPlayer();
                Inventory inv = p.getInventory();

                // Check for villager tokens and permission
                if (inv.contains(Setup.TOKEN) && 
                    Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), Interaction.INTERACT_ENTITY)) {

                    // Create transport charm
                    ItemStack charm = Setup.TRANSPORT_CHARM;
                    ItemMeta meta = charm.getItemMeta();

                    // Null checks
                    if (meta != null) {
                        // Store PDC
                        PersistentDataContainer pdc = meta.getPersistentDataContainer();

                        // Add UUID to PDC
                        pdc.set(key, new UUIDTagType(), v.getUniqueId());

                        // Update lore of charm and set meta to charm
                        meta.setLore(linkedVillagerLore);
                        charm.setItemMeta(meta);

                        // Attempt to add charm to player inventory
                        if (inv.addItem(charm).isEmpty()) {
                            // Consume villager token
                            inv.removeItem(new SlimefunItemStack(Setup.TOKEN, 1));
                        } else {
                            p.sendMessage(ChatColors.color("&cYou don't have enough inventory space!"));
                        }
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
