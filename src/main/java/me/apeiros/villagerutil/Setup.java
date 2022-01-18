package me.apeiros.villagerutil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import lombok.experimental.UtilityClass;
import me.apeiros.villagerutil.items.wands.CureWand;
import me.apeiros.villagerutil.items.wands.NitwitWand;
import me.apeiros.villagerutil.utils.Utils;

@UtilityClass
public class Setup {

    // Item constants
    public static final SlimefunItemStack VILLAGER_ESSENCE = new SlimefunItemStack(
            "VU_VILLAGER_ESSENCE", Material.GLOWSTONE_DUST, "&dVillager Essence",
            "&7A rare, mysterious dust which is a key",
            "&7component of Villager Incantations",
            "",
            "&eIngredient &9&o(Villager Utils)");

    public static final SlimefunItemStack VILLAGER_TOKEN = new SlimefunItemStack(
            "VU_VILLAGER_TOKEN", Material.EMERALD, "&bVillager Token",
            "&7A special item that is needed",
            "&7to cast Villager Incantations",
            "",
            "&aConsumable &9&o(Villager Utils)");

    public static final SlimefunItemStack CAPTURE_WAND = new SlimefunItemStack(
            "VU_CAPTURE_WAND", Material.BLAZE_ROD, "&cVillager Capture Wand",
            "&eRight Click &7on a villager",
            "&7to capture it",
            "",
            "&bTool &9&o(Villager Utils)");

    public static final SlimefunItemStack TRADE_WAND = new SlimefunItemStack(
            "VU_TRADE_WAND", Material.BLAZE_ROD, "&6Villager Trade Wand",
            "&eRight Click &7on a villager",
            "&7to cycle its trades",
            "",
            "&bTool &9&o(Villager Utils)");

    public static final SlimefunItemStack CURE_WAND = new SlimefunItemStack(
            "VU_CURE_WAND", Material.BLAZE_ROD, "&aVillager Cure Wand",
            "&eRight Click &7on a zombified villager",
            "&7to cure it from its ailment",
            "",
            "&bTool &9&o(Villager Utils)");

    public static final SlimefunItemStack NITWIT_WAND = new SlimefunItemStack(
            "VU_NITWIT_WAND", Material.BLAZE_ROD, "&5Villager De-nitwit-ifier",
            "&eRight Click &7on a nitwit-type villager",
            "&7to allow it to get a job",
            "",
            "&bTool &9&o(Villager Utils)");

    // Setup methods
    public static void setup(VillagerUtil p) {
        // Setup category, researches, listeners, and commands
        ItemGroup ig = new ItemGroup(Utils.key("villager_util"), new ItemStack(Material.EMERALD));
        ig.register(p);
        setupResearches(p);

        // Setup items
        new SlimefunItem(ig, VILLAGER_ESSENCE, RecipeType.ANCIENT_ALTAR, new ItemStack[] {
            SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.GLASS_PANE), SlimefunItems.ENDER_LUMP_2,
            new ItemStack(Material.EMERALD), SlimefunItems.VILLAGER_RUNE, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE,
            SlimefunItems.ENDER_LUMP_2, new ItemStack(Material.GLASS_PANE), SlimefunItems.MAGIC_LUMP_2
        }, new SlimefunItemStack(VILLAGER_ESSENCE, 16)).register(p);

        new SlimefunItem(ig, VILLAGER_TOKEN, RecipeType.MAGIC_WORKBENCH, new ItemStack[] {
            null, SlimefunItems.STRANGE_NETHER_GOO, null,
            SlimefunItems.STRANGE_NETHER_GOO, VILLAGER_ESSENCE, SlimefunItems.STRANGE_NETHER_GOO,
            null, SlimefunItems.STRANGE_NETHER_GOO, null
        }, new SlimefunItemStack(VILLAGER_TOKEN, 2)).register(p);

        // TODO: Add captured villager item

        // TODO: Do a permissions check for all wands
        new NitwitWand(ig).register(p);
        new CureWand(ig).register(p);
    }

    private static void setupResearches(VillagerUtil p) {
        new Research(Utils.key("changeme"), 77777, "Change Me!", 30).addItems(CAPTURE_WAND).register();
    }
}
