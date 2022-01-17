package me.apeiros.CHANGEME;

import io.github.mooy1.infinitylib.core.AbstractAddon;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

public class ChangeMe extends AbstractAddon implements SlimefunAddon {

    // Instance
    private static ChangeMe instance;

    // Auto update things
    public ChangeMe() {
        super("Apeiros-46B", "ChangeMe", "main", "options.auto-update");
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

    public static ChangeMe i() {
        return instance;
    }

}
