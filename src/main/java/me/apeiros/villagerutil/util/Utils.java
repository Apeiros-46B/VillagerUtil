package me.apeiros.villagerutil.util;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.experimental.UtilityClass;
import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.VillagerUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

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

    public static String getItemName(ItemStack i) {
        if (i == null || i.getType().isAir()) {
            return "Air";
        }

        ItemMeta meta = i.getItemMeta();
        if (meta != null && (meta.hasDisplayName() || meta.hasLocalizedName())) {
            return meta.hasDisplayName() ? meta.getDisplayName() : meta.getLocalizedName();
        }

        return ChatUtils.humanize(i.getType().name().toLowerCase());
    }

    // Check for token (Automatically true when: Creative mode, Tokens disabled)
    public static boolean hasToken(Player p, Inventory inv) {
        return p.getGameMode() == GameMode.CREATIVE || !VillagerUtil.useTokens() || inv.containsAtLeast(Setup.TOKEN, 1);
    }

    // Check if an entity is a villager that the player can manipulate
    public static boolean isValidVillager(Player p, Entity en) {
        if (en instanceof Villager) {
            // Store villager
            Villager v = (Villager) en;

            // Check for permission
            if (! Slimefun.getProtectionManager().hasPermission(p, p.getLocation(), Interaction.INTERACT_ENTITY)) {
                p.sendMessage(ChatColors.color("&cYou don't have permission!"));
                v.shakeHead();
                return false;
            }

            return true;
        }
        return false;
    }

    // Consume token (Token not consumed when: Creative mode, Tokens disabled)
    public static void removeToken(Player p, Inventory inv) {
        if (p.getGameMode() != GameMode.CREATIVE && VillagerUtil.useTokens()) {
            inv.removeItem(new SlimefunItemStack(Setup.TOKEN, 1));
        }
    }

    // Fill the slots in a menu with a set item
    public static void fillSlots(ChestMenu menu, int[] slots, ItemStack stack) {
        for (int i : slots) {
            menu.addItem(i, stack, ChestMenuUtils.getEmptyClickHandler());
        }
    }

}
