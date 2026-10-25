package net.saphyra.fabric.platform;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Loader-local metadata; no client classes are loaded during mod registration. */
public final class FabricTabSettings {
    private static final Map<CreativeModeTab, FabricTabConfiguration> SETTINGS =
            new IdentityHashMap<>();
    private static final Map<ResourceLocation, CreativeModeTab> REGISTERED = new LinkedHashMap<>();

    private FabricTabSettings() {}

    static void configure(CreativeModeTab tab, boolean searchBar, ResourceLocation predecessor) {
        SETTINGS.put(tab, new FabricTabConfiguration(searchBar, predecessor));
    }

    static void register(ResourceLocation id, CreativeModeTab tab) {
        REGISTERED.put(id, tab);
    }

    public static boolean hasSearchBar(CreativeModeTab tab) {
        FabricTabConfiguration settings = SETTINGS.get(tab);
        return settings != null && settings.searchBar();
    }

    public static List<CreativeModeTab> orderedTabs() {
        List<CreativeModeTab> result = new ArrayList<>();
        for (CreativeModeTab tab : REGISTERED.values()) {
            FabricTabConfiguration settings = SETTINGS.get(tab);
            // Matches NeoForge withTabsBefore: the supplied tab precedes this tab.
            CreativeModeTab predecessor =
                    settings == null ? null : REGISTERED.get(settings.predecessor());
            int index = result.indexOf(predecessor);
            if (index < 0) result.add(tab);
            else result.add(index + 1, tab);
        }
        return result;
    }
}
