package me.apeiros.villagerutil;

import io.github.mooy1.infinitylib.core.AbstractAddon;

public class VillagerUtil extends AbstractAddon {

    // Instance
    private static VillagerUtil instance;

    // Auto update things
    public VillagerUtil() {
        super("Apeiros-46B", "VillagerUtil", "main", "options.auto-update");
    }

    @Override
    public void enable() {
        // Instance
        instance = this;

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

}
