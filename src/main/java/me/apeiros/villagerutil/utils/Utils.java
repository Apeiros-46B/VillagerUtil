package me.apeiros.villagerutil.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import me.apeiros.villagerutil.VillagerUtil;

import lombok.experimental.UtilityClass;

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
        v.setProfession(Profession.NONE);
        v.setVillagerExperience(0);
        v.setVillagerLevel(0);
    }

    // Create a NamespacedKey
    public static NamespacedKey key(String s) {
        return VillagerUtil.createKey(s);
    }

}
