package me.apeiros.CHANGEME;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import lombok.experimental.UtilityClass;
import me.apeiros.CHANGEME.utils.Categories;
import me.apeiros.CHANGEME.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Setup {

    // Enchantment map (will be used to add enchantments to items)
    private static Map<Enchantment, Integer> enchs = new HashMap<>();

    // Item constants
    public static final SlimefunItemStack ITEM = Utils.shine(new SlimefunItemStack(
            "ITEM", Material.DIAMOND, Utils.legacyParse("<aqua>Magical Diamond"),
            Utils.legacyParse("<gray>Shimmering")));

    // Setup methods
    public static void setup(ChangeMe p) {
        // Setup category, researches, listeners, and commands
        Categories.MAIN.register(p);
        setupResearches(p);
        setupListeners(p);
        setupCommands(p);

        // Setup items
        new SlimefunItem(Categories.GENERAL, ITEM, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            null, new ItemStack(Material.DIAMOND_BLOCK), null,
            new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.BEACON), new ItemStack(Material.DIAMOND_BLOCK),
            null, new ItemStack(Material.DIAMOND_BLOCK), null
        }).register(p);
    }

    private static void setupListeners(ChangeMe p) {

    }

    private static void setupCommands(ChangeMe p) {

    }

    private static void setupResearches(ChangeMe p) {
        new Research(Utils.key("changeme"), 77777, "Change Me!", 30).addItems(ITEM).register();
    }
}
