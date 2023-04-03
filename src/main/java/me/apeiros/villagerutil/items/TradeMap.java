package me.apeiros.villagerutil.items;

import de.jeff_media.morepersistentdatatypes.DataType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TradeMap extends SimpleSlimefunItem<EntityInteractHandler> {
    public TradeMap(ItemGroup itemGroup) {
        super(itemGroup, Setup.TRADE_MAP, RecipeType.ANCIENT_ALTAR, new ItemStack[] {

        });
    }

    @NotNull
    @Override
    public EntityInteractHandler getItemHandler() {
        return (e, item, offhand) -> {
            e.setCancelled(true);

            // Check if the clicked entity is a villager
            Player p = e.getPlayer();
            Entity en = e.getRightClicked();
            ItemMeta meta = item.getItemMeta();
            if (Utils.isValidVillager(p, en) && meta != null) {
                List<String> lore = Setup.TRADE_MAP.getItemMeta() != null && Setup.TRADE_MAP.getItemMeta().getLore() != null ? Setup.TRADE_MAP.getItemMeta().getLore() : new ArrayList<>();
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                Villager v = (Villager) en;
                if (v.getRecipes().isEmpty()) {
                    return;
                }

                for (NamespacedKey key : new HashSet<>(pdc.getKeys())) {
                    if (key.getKey().contains("trade_input") || key.getKey().contains("trade_output")) {
                        pdc.remove(key);
                    }
                }

                int i = 1;
                for (MerchantRecipe r : v.getRecipes()) {
                    ItemStack[] input = new ItemStack[r.getIngredients().size()];
                    ItemStack result = r.getResult();
                    r.getIngredients().toArray(input);
                    pdc.set(Utils.key("trade_input" + i), DataType.ITEM_STACK_ARRAY, input);
                    pdc.set(Utils.key("trade_output" + i), DataType.ITEM_STACK, result);
                    StringBuilder s = new StringBuilder();
                    for (ItemStack is : input) {
                        if (is == null || is.getType().isAir()) {
                            continue;
                        }

                        if (s.length() != 0) {
                            s.append(ChatColor.GRAY).append(", ");
                        }
                        s.append(ChatColor.YELLOW).append(Utils.getItemName(is));
                    }
                    s.append(ChatColor.GRAY).append(" -> ").append(ChatColor.YELLOW).append(Utils.getItemName(result));
                    lore.add(2 + i - 1, s.toString());
                    i++;
                }
                lore.add(2, " ");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        };
    }

    public static List<Pair<ItemStack[], ItemStack>> getTrades(ItemStack item) {
        List<Pair<ItemStack[], ItemStack>> trades = new ArrayList<>();
        if (item == null || item.getItemMeta() == null || !(SlimefunItem.getByItem(item) instanceof TradeMap)) {
            return trades;
        }

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        int i = 1;
        while (pdc.has(Utils.key("trade_input" + i), DataType.ITEM_STACK_ARRAY) && pdc.has(Utils.key("trade_output" + i), DataType.ITEM_STACK)) {
            ItemStack[] inputs = pdc.get(Utils.key("trade_input" + i), DataType.ITEM_STACK_ARRAY);
            ItemStack output = pdc.get(Utils.key("trade_output" + i), DataType.ITEM_STACK);
            trades.add(new Pair<>(inputs == null ? new ItemStack[0] : inputs, output == null ? new ItemStack(Material.AIR) : output));
            i++;
        }
        return trades;
    }
}
