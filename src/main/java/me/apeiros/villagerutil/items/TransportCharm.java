package me.apeiros.villagerutil.items;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;

import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.util.UUIDTagType;
import me.apeiros.villagerutil.util.Utils;

public class TransportCharm extends SlimefunItem {

    // Charm lore when there is no villager stored
    private final List<String> noVillagerLore = List.of(
        "&7A magical charm which will teleport",
        "&7the associated villager to its location",
        "&eRight Click &7to teleport the villager",
        "",
        "&7No villager linked",
        "",
        "&bTool &9&o(Villager Utils)"
    );

    // NamespacedKey for PDC
    private final NamespacedKey key = Utils.key("stored_villager_uuid");

    // Custom Transport Wand recipe type
    private static final RecipeType transportWandRecipeType = new RecipeType(
            Utils.key("transport_wand_recipe_type"), 
            Setup.TRANSPORT_WAND,
            "&7&oRight click a villager with a",
            "&bVillager Transport Wand &7&oto",
            "&7&orecieve a &bVillager Transport Charm",
            "&7&olinked to that villager");

    // Creates Villager Charm
    public TransportCharm(ItemGroup ig) {
        super(ig, Setup.TRANSPORT_CHARM, "VU_TRANSPORT_CHARM", transportWandRecipeType, new ItemStack[] {
            null, null, null,
            null, PlayerHead.getItemStack(PlayerSkin.fromBase64(Setup.VILLAGER)), null,
            null, null, null
        });
    }

    // Creates and returns handler
    private ItemUseHandler getItemUseHandler() {
        return (e) -> {
            // Cancel the right click event
            e.cancel();

            // Check if clicked block exists
            Optional<Block> optionalBlock = e.getClickedBlock();
            if (optionalBlock.isPresent()) {
                // Store the item and its meta
                ItemStack item = e.getItem();
                ItemMeta meta = item.getItemMeta();

                // Null checking
                if (meta != null) {
                    if (meta.getPersistentDataContainer() != null) {
                        // Store the container
                        PersistentDataContainer pdc = meta.getPersistentDataContainer();

                        // Get stored UUID
                        UUID id = pdc.get(key, new UUIDTagType());

                        if (id == null) {
                            ChatColors.color("&cThere is no villager linked to this charm!");
                            return;
                        }

                        // Attempt to get the villager
                        Villager v = null;

                        // Loop through all entities
                        for (World world : Bukkit.getWorlds()) {
                            for (Entity en : world.getEntities()) {
                                // Check if the entity is a villager
                                if (en instanceof Villager) {
                                    // Check if the UUIDs are the same
                                    if (en.getUniqueId().equals(id)) {
                                        v = (Villager) en;
                                        break;
                                    }
                                }
                            }
                        }

                        // Check if villager exists
                        if (v == null) {
                            e.getPlayer().sendMessage(ChatColors.color("&cThe villager linked to this charm no longer exists!"));
                        } else {
                            // Store location
                            Location l = optionalBlock.get().getRelative(e.getClickedFace()).getLocation();

                            // Teleport villager to location
                            v.teleport(l);

                            // Remove entry from PDC
                            pdc.remove(key);

                            // Reset lore of the item
                            meta.setLore(noVillagerLore);

                            // Set meta to the item
                            item.setItemMeta(meta);
                        }
                    }
                }
            }
        };
    }

    // Registers handler
    public void preRegister() {
        this.addItemHandler(getItemUseHandler());
    }
    
}
