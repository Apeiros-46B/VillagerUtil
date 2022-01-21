package me.apeiros.villagerutil.util;

import lombok.experimental.UtilityClass;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.VillagerUtil;

@UtilityClass
public class Utils {
    
    // Check if villager trades are locked
    public static boolean villagerTradesLocked(Villager v) {
        return v.getVillagerLevel() != 1 || v.getVillagerExperience() != 0;
    }

    // Remove profession from a villager
    public static void removeProfession(Villager v) {
        v.setProfession(Profession.NONE);
    }

    // Remove profession from a villager and clear its experience
    public static void removeProfessionAndExp(Villager v) {
        removeProfession(v);
        v.setVillagerExperience(0);
        v.setVillagerLevel(1);
    }

    // Create a NamespacedKey
    public static NamespacedKey key(String s) {
        return VillagerUtil.createKey(s);
    }

    // Create a potion
    public static ItemStack makePotion(PotionData data) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        // Null check
        if (meta != null) {
            meta.setBasePotionData(data);
            potion.setItemMeta(meta);
        }

        return potion;
    }

    // Check for token (Automatically true when: Creative mode, Tokens disabled)
    public static boolean hasToken(Player p, Inventory inv) {
        return p.getGameMode() == GameMode.CREATIVE || !VillagerUtil.useTokens() || inv.containsAtLeast(Setup.TOKEN, 1);
    }

    // Consume token (Token not consumed when: Creative mode, Tokens disabled)
    public static void removeToken(Player p, Inventory inv) {
        if (p.getGameMode() != GameMode.CREATIVE && VillagerUtil.useTokens()) {
            inv.removeItem(new SlimefunItemStack(Setup.TOKEN, 1));
        }
    }

}
