package me.apeiros.villagerutil;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.scheduler.BukkitTask;

import io.github.mooy1.infinitylib.core.AbstractAddon;

public class VillagerUtil extends AbstractAddon {

    // Instance
    private static VillagerUtil instance;

    // Number for confirmation of villager reset command
    private static int commandNumber;

    // Auto update things
    public VillagerUtil() {
        super("Apeiros-46B", "VillagerUtil", "main", "options.auto-update");
    }

    @Override
    public void enable() {
        // Instance
        instance = this;

        // Command number
        commandNumber = ThreadLocalRandom.current().nextInt(0, 1_000_000);

        // Setup
        Setup.setup(this);
    }

    @Override
    public void disable() {
        // Clean up instance
        instance = null;
    }

    public static VillagerUtil i() {
        return instance;
    }

    public static int getCommandNumber() {
        return commandNumber;
    }

    @Nullable
    public static BukkitTask runSync(@Nonnull Runnable runnable) {
        Validate.notNull(runnable, "Cannot run null");
        
        if (instance == null || !instance.isEnabled()) {
            return null;
        }

        return instance.getServer().getScheduler().runTask(instance, runnable);
    }

}
