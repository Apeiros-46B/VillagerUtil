package me.apeiros.villagerutil;

import lombok.experimental.UtilityClass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import me.apeiros.villagerutil.commands.ResetVillagerCommand;
import me.apeiros.villagerutil.items.TransportCharm;
import me.apeiros.villagerutil.items.wands.CureWand;
import me.apeiros.villagerutil.items.wands.NitwitWand;
import me.apeiros.villagerutil.items.wands.TradeWand;
import me.apeiros.villagerutil.items.wands.TransportWand;
import me.apeiros.villagerutil.util.Utils;

@UtilityClass
public class Setup {

    // Skull texture for Villager Transport Charm
    public static final String VILLAGER = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhOGVmMjQ1OGEyYjEwMjYwYjg3NTY1NThmNzY3OWJjYjdlZjY5MWQ0MWY1MzRlZmVhMmJhNzUxMDczMTVjYyJ9fX0=";

    // Item constants
    public static final SlimefunItemStack ESSENCE = new SlimefunItemStack(
            "VU_ESSENCE", Material.GLOWSTONE_DUST, "&dVillager Essence",
            "&7A rare, mysterious dust which is a key",
            "&7component of Villager Magic",
            "",
            "&eIngredient &9&o(Villager Utils)");

    public static final SlimefunItemStack TOKEN = new SlimefunItemStack(
            "VU_TOKEN", Material.EMERALD, "&bVillager Token",
            "&7A special item that is needed",
            "&7to cast Villager Magic",
            "",
            "&aConsumable &9&o(Villager Utils)");

    public static final SlimefunItemStack TRANSPORT_CHARM = new SlimefunItemStack(
            "VU_TRANSPORT_CHARM", VILLAGER, "&a&lVillager Charm",
            "&7A magical charm which will teleport",
            "&7the associated villager to its location",
            "&eRight Click &7to teleport the villager",
            "",
            "&7No villager linked",
            "",
            "&bTool &9&o(Villager Utils)");

    public static final SlimefunItemStack TRANSPORT_WAND = new SlimefunItemStack(
            "VU_TRANSPORT_WAND", Material.BLAZE_ROD, "&cVillager Transport Wand",
            "&eRight Click &7on a villager",
            "&7to recieve a Villager Charm",
            "&7linked to that villager",
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
        // Setup category and researches
        ItemGroup ig = new ItemGroup(Utils.key("villager_util"), new ItemStack(Material.EMERALD));
        ig.register(p);

        // Setup /resetvillager command
        new ResetVillagerCommand(p);

        // Setup items
        new SlimefunItem(ig, ESSENCE, RecipeType.ANCIENT_ALTAR, new ItemStack[] {
            SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.GLASS_PANE), SlimefunItems.ENDER_LUMP_2,
            new ItemStack(Material.EMERALD), SlimefunItems.VILLAGER_RUNE, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE,
            SlimefunItems.ENDER_LUMP_2, new ItemStack(Material.GLASS_PANE), SlimefunItems.MAGIC_LUMP_2
        }, new SlimefunItemStack(ESSENCE, 16)).register(p);

        new SlimefunItem(ig, TOKEN, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            ESSENCE, SlimefunItems.STRANGE_NETHER_GOO, new ItemStack(Material.EMERALD),
            null, null, null,
            null, null, null
        }, new SlimefunItemStack(TOKEN, 2)).register(p);

        // Setup Villager Charm
        new TransportCharm(ig).register(p);

        // Setup wands
        new CureWand(ig).register(p);
        new NitwitWand(ig).register(p);
        new TradeWand(ig).register(p);
        new TransportWand(ig).register(p);
    }
}
