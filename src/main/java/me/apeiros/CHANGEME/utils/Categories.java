package me.apeiros.CHANGEME.utils;

import io.github.mooy1.infinitylib.groups.MultiGroup;
import io.github.mooy1.infinitylib.groups.SubGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

@UtilityClass
public class Categories {

    // Category constants
    public static final ItemGroup GENERAL = new SubGroup("changeme_misc",
            new CustomItemStack(Material.BARRIER, Utils.legacyParse("General")));

    public static final ItemGroup MISC = new SubGroup("changeme_misc",
            new CustomItemStack(Material.BARRIER, Utils.legacyParse("Miscellaneous")));

    public static final ItemGroup MAIN = new MultiGroup("changeme_main",
            new CustomItemStack(Material.BARRIER, Utils.legacyParse("<red><underline>Change Me!")), GENERAL, MISC);

}
