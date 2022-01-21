package me.apeiros.villagerutil.items.wands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
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

    // Charm lore when there is a villager linked
    private final List<String> linkedVillagerLore = List.of(
        ChatColors.color("&7A magical charm which will teleport"),
        ChatColors.color("&7the associated villager to its location"),
        ChatColors.color("&eRight Click &7to teleport the villager"),
        "",
        ChatColors.color("&aVillager linked!"),
        "",
        ChatColors.color("&bTool &9&o(Villager Utils)")
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

            // Check if the clicked entity is a villager
            Entity en = e.getRightClicked();
            if (en instanceof Villager) {
                // Store villager, player, and inventory
                Villager v = (Villager) en;
                Player p = e.getPlayer();
                Inventory inv = p.getInventory();

                // Check for permission
                if (!Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), Interaction.INTERACT_ENTITY)) {
                    p.sendMessage(ChatColors.color("&cYou don't have permission!"));
                    v.shakeHead();
                    return;
                }

                // Check for villager tokens
                if (!Utils.hasToken(p, inv)) {
                    p.sendMessage(ChatColors.color("&cInsufficient Villager Tokens!"));
                    v.shakeHead();
                    return;
                }

                // Create transport charm
                ItemStack charm = Setup.TRANSPORT_CHARM.clone();
                ItemMeta meta = charm.getItemMeta();

                // Null check
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
                        Utils.removeToken(p, inv);
                    } else {
                        p.sendMessage(ChatColors.color("&cYou don't have enough inventory space!"));
                    }

                    // Play sounds
                    World w = v.getWorld();
                    Location l = v.getLocation();
                    w.playSound(l, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1F, 1.5F);
                    w.playSound(l, Sound.BLOCK_BEACON_POWER_SELECT, 1F, 1F);
                }
            }
        };
    }

    // Registers handler
    public void preRegister() {
        this.addItemHandler(getEntityInteractHandler());
    }
    
}
