package me.apeiros.villagerutil.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.apeiros.villagerutil.Setup;
import me.apeiros.villagerutil.util.Utils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AutoTrader extends AContainer {
    protected static final int[] PREVIEW_BORDER = {3, 4, 5, 6, 7, 8, 12, 17, 21, 22, 23, 24, 25, 26};
    protected static final int[] INPUT_BORDER = {27, 28, 29, 30, 36, 39, 45, 46, 47, 48};
    protected static final int[] OUTPUT_BORDER = {32, 33, 34, 35, 41, 44, 50, 51, 52, 53};
    protected static final int[] MAP_BORDER = {0, 1, 2, 9, 11, 18, 19, 20};
    protected static final int[] PREVIEW_INPUTS = {13, 14};
    protected static final int[] INPUT_SLOTS = {37, 38};
    protected static final int[] OUTPUT_SLOTS = {42, 43};
    protected static final int PREVIEW_OUTPUT = 16;
    protected static final int TRADE_MAP_INPUT = 10;
    protected static final int PREVIEW_ARROW = 15;
    protected static final int SCROLL_UP = 31;
    protected static final int PROGRESS_SLOT = 40;
    protected static final int SCROLL_DOWN = 49;
    private static final Set<BlockPosition> updatePreviews = new HashSet<>();
    private final MachineProcessor<CraftingOperation> processor = this.getMachineProcessor();

    public AutoTrader(ItemGroup itemGroup) {
        super(itemGroup, Setup.AUTO_TRADER, RecipeType.ANCIENT_ALTAR, new ItemStack[] {

        });

        new BlockMenuPreset(getId(), getItemName()) {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(@NotNull Block b, @NotNull Player p) {
                return p.hasPermission("slimefun.inventory.bypass") || Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return flow == ItemTransportFlow.INSERT ? INPUT_SLOTS : OUTPUT_SLOTS;
            }

            @Override
            @ParametersAreNonnullByDefault
            public void newInstance(BlockMenu menu, Block b) {
                int trade = getTrade(b.getLocation());
                updateControls(menu, trade);
                fillPreview(menu);
                menu.addMenuClickHandler(TRADE_MAP_INPUT, (p, s, i, a) -> {
                    updatePreviews.add(new BlockPosition(b));
                    return true;
                });
            }
        };
    }

    @Override
    protected void tick(Block b) {
        BlockMenu menu = BlockStorage.getInventory(b);
        CraftingOperation operation = processor.getOperation(b);

        if (updatePreviews.contains(new BlockPosition(b))) {
            int trade = getTrade(b.getLocation());
            fillPreview(menu);
            updateControls(menu, trade);
            updatePreviews.remove(new BlockPosition(b));
        }

        if (operation != null && takeCharge(b.getLocation())) {
            if (!operation.isFinished()) {
                processor.updateProgressBar(menu, PROGRESS_SLOT, operation);
                operation.addProgress(1);
            } else {
                menu.replaceExistingItem(PROGRESS_SLOT, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "));

                for (ItemStack item : operation.getResults()) {
                    menu.pushItem(item.clone(), getOutputSlots());
                }

                processor.endOperation(b);
            }
        } else {
            MachineRecipe next = findNextRecipe(menu);
            if (next != null) {
                operation = new CraftingOperation(next);
                processor.startOperation(b, operation);
                processor.updateProgressBar(menu, PROGRESS_SLOT, operation);
            }
        }
    }

    @Override
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        Pair<ItemStack[], ItemStack> tradePair = getTradePair(menu);
        if (tradePair == null) {
            return null;
        }

        ItemStack[] inputs = new ItemStack[2];
        Map<Integer, Integer> found = new HashMap<>();

        int i = 0;
        for (ItemStack input : tradePair.getFirstValue()) {
            if (input == null || input.getType().isAir()) {
                continue;
            }

            inputs[i] = input;
            for (int slot : getInputSlots()) {
                if (SlimefunUtils.isItemSimilar(menu.getItemInSlot(slot), input, true)) {
                    found.put(slot, input.getAmount());
                    break;
                }
            }
            i++;
        }

        if (i != found.size() || !InvUtils.fitAll(menu.toInventory(), new ItemStack[] {tradePair.getSecondValue()}, getOutputSlots())) {
            return null;
        }

        for (Map.Entry<Integer, Integer> entry : found.entrySet()) {
            menu.consumeItem(entry.getKey(), entry.getValue());
        }

        return new MachineRecipe(8, inputs, new ItemStack[] {tradePair.getSecondValue()});
    }

    @Override
    public void constructMenu(BlockMenuPreset preset) {
        Utils.fillSlots(preset, MAP_BORDER, Setup.TRADE_MAP_BORDER);
        Utils.fillSlots(preset, PREVIEW_BORDER, Setup.PREVIEW_BORDER);
        Utils.fillSlots(preset, INPUT_BORDER, ChestMenuUtils.getInputSlotTexture());
        Utils.fillSlots(preset, OUTPUT_BORDER, ChestMenuUtils.getOutputSlotTexture());
        Utils.fillSlots(preset, PREVIEW_INPUTS, Setup.FILLER_ITEM);
        Utils.fillSlots(preset, new int[] {PREVIEW_OUTPUT}, Setup.FILLER_ITEM);
        preset.addItem(PREVIEW_ARROW, Setup.ARROW_ITEM);
        preset.addMenuClickHandler(PREVIEW_ARROW, ChestMenuUtils.getEmptyClickHandler());
        preset.addItem(PROGRESS_SLOT, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "), ChestMenuUtils.getEmptyClickHandler());
        preset.addMenuClickHandler(PROGRESS_SLOT, ChestMenuUtils.getEmptyClickHandler());
        preset.addItem(SCROLL_UP, getScrollStack(null, 1, -1));
        preset.addMenuClickHandler(SCROLL_UP, ChestMenuUtils.getEmptyClickHandler());
        preset.addItem(SCROLL_DOWN, getScrollStack(null, 1, 1));
        preset.addMenuClickHandler(SCROLL_DOWN, ChestMenuUtils.getEmptyClickHandler());
    }

    public void updateControls(BlockMenu menu, int trade) {
        menu.replaceExistingItem(SCROLL_UP, getScrollStack(menu, trade, -1));
        menu.addMenuClickHandler(SCROLL_UP, (p, s, i, a) -> {
            if (trade - 1 > 0) {
                BlockStorage.addBlockInfo(menu.getBlock(), "trade", String.valueOf(trade - 1));
                fillPreview(menu);
                updateControls(menu, trade - 1);
            }
            return false;
        });
        menu.replaceExistingItem(SCROLL_DOWN, getScrollStack(menu, trade, 1));
        menu.addMenuClickHandler(SCROLL_DOWN, (p, s, i, a) -> {
            if (trade + 1 <= getTrades(menu)) {
                BlockStorage.addBlockInfo(menu.getBlock(), "trade", String.valueOf(trade + 1));
                fillPreview(menu);
                updateControls(menu, trade + 1);
            }
            return false;
        });
    }

    public void fillPreview(BlockMenu menu) {
        Pair<ItemStack[], ItemStack> entry = getTradePair(menu);
        if (entry == null) {
            menu.replaceExistingItem(PREVIEW_INPUTS[0], Setup.FILLER_ITEM);
            menu.replaceExistingItem(PREVIEW_INPUTS[1], Setup.FILLER_ITEM);
            menu.replaceExistingItem(PREVIEW_OUTPUT, Setup.FILLER_ITEM);
            return;
        }

        int index = 0;
        for (ItemStack input : entry.getFirstValue()) {
            menu.replaceExistingItem(PREVIEW_INPUTS[index], input == null || input.getType().isAir() ? Setup.FILLER_ITEM : new CustomItemStack(input, Utils.getItemName(input)));
            index++;
        }

        menu.replaceExistingItem(PREVIEW_OUTPUT, new CustomItemStack(entry.getSecondValue(), Utils.getItemName(entry.getSecondValue())));
    }

    public ItemStack getScrollStack(BlockMenu menu, int trade, int change) {
        final boolean canUse = trade + change <= getTrades(menu) && trade + change >= 1;
        final boolean increase = trade + change > trade;
        return new CustomItemStack(
                canUse ? Material.LIME_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE,
                (canUse ? "&f" : "&8") + (increase ? "↓ Next page" : "↑ Previous page"),
                "&7(" + trade + "/" + getTrades(menu) + ")"
        );
    }

    @Override
    @Nonnull
    protected BlockBreakHandler onBlockBreak() {
        return new SimpleBlockBreakHandler() {
            @Override
            public void onBlockBreak(@NotNull Block b) {
                BlockMenu menu = BlockStorage.getInventory(b);
                if (menu != null) {
                    menu.dropItems(b.getLocation(), getInputSlots());
                    menu.dropItems(b.getLocation(), getOutputSlots());
                    menu.dropItems(b.getLocation(), TRADE_MAP_INPUT);
                }
                processor.endOperation(b);
            }
        };
    }

    public int getTrades(BlockMenu menu) {
        if (menu == null) {
            return 1;
        }

        return Math.max(1, TradeMap.getTrades(menu.getItemInSlot(TRADE_MAP_INPUT)).size());
    }

    public int getTrade(Location l) {
        return Integer.parseInt(BlockStorage.hasBlockInfo(l) && BlockStorage.getLocationInfo(l, "trade") != null ? BlockStorage.getLocationInfo(l, "trade") : "1");
    }

    public Pair<ItemStack[], ItemStack> getTradePair(BlockMenu menu) {
        int trade = getTrade(menu.getLocation());
        List<Pair<ItemStack[], ItemStack>> trades = TradeMap.getTrades(menu.getItemInSlot(TRADE_MAP_INPUT));
        if (trades.size() < trade) {
            return null;
        }

        return trades.get(trade - 1);
    }

    @Override
    public int[] getInputSlots() {
        return INPUT_SLOTS;
    }

    @Override
    public int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.VILLAGER_SPAWN_EGG);
    }

    @NotNull
    @Override
    public String getMachineIdentifier() {
        return "VU_AUTO_TRADER";
    }
}
