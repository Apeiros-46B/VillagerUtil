package me.apeiros.villagerutil.items;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;

import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.VillagerUtil;
import me.apeiros.villagerutil.util.UUIDTagType;
import me.apeiros.villagerutil.util.Utils;

public class TransportCharm extends SlimefunItem {

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
            null, new CustomItemStack(PlayerHead.getItemStack(PlayerSkin.fromBase64(Setup.VILLAGER)), "&aVillager"), null,
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

                // Null check
                if (meta != null) {
                    // Store PDC, player, and inventory
                    PersistentDataContainer pdc = meta.getPersistentDataContainer();
                    Player p = e.getPlayer();
                    Inventory inv = p.getInventory();

                    // Whether or not tokens are used
                    boolean useTokens = VillagerUtil.useTokens();

                    // Get stored UUID
                    UUID id = pdc.get(key, new UUIDTagType());

                    if (id == null) {
                        p.sendMessage(ChatColors.color("&cThere is no villager linked to this charm!"));

                        // Refund token
                        refundToken(useTokens, inv, item);

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
                        p.sendMessage(ChatColors.color("&cThe villager linked to this charm no longer exists!"));

                        // Refund token
                        refundToken(useTokens, inv, item);
                    } else {
                        // Store location
                        Location l = optionalBlock.get().getRelative(e.getClickedFace()).getLocation();

                        // Teleport villager to location
                        v.teleport(l.add(0.5, 0.5, 0.5));

                        // Play effects
                        World w = v.getWorld();
                        l = v.getLocation();
                        w.playSound(l, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1F, 1F);
                        w.spawnParticle(Particle.PORTAL, l, 150);

                        // Consume charm
                        inv.removeItem(new CustomItemStack(item, 1));
                    }
                }
            }
        };
    }

    // Registers handler
    public void preRegister() {
        this.addItemHandler(getItemUseHandler());
    }

    // Refund token if tokens are enabled
    private void refundToken(boolean useTokens, Inventory inv, ItemStack item) {
        // Check if tokens are enabled in config
        if (useTokens) {
            // Refund token if there is available space
            if (inv.addItem(Setup.TOKEN.clone()).isEmpty()) {
                // Consume charm
                inv.removeItem(new CustomItemStack(item, 1));
            }
        } else {
            // Consume charm
            inv.removeItem(new CustomItemStack(item, 1));
        }
    }
    
}
