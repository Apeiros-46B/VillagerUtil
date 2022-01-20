package me.apeiros.villagerutil.util;

import lombok.experimental.UtilityClass;
import me.apeiros.villagerutil.VillagerUtil;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

@UtilityClass
public class Utils {
    
    // Check if villager trades are locked
    public static boolean villagerTradesLocked(Villager v) {
        return v.getVillagerLevel() != 0 || v.getVillagerExperience() != 0;
    }

    // Remove profession from a villager
    public static void removeProfession(Villager v) {
        v.setProfession(Profession.NONE);
    }

    // Remove profession from a villager and clear its experience
    public static void removeProfessionAndExp(Villager v) {
        removeProfessionAndExp(v);
        v.setVillagerExperience(0);
        v.setVillagerLevel(0);
    }

    // Create a NamespacedKey
    public static NamespacedKey key(String s) {
        return VillagerUtil.createKey(s);
    }

    // Create a potion
    public static ItemStack makePotion(PotionData data) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(data);
        potion.setItemMeta(meta);

        return potion;
    }

}
