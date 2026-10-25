package net.saphyra.fabric.platform;

import net.minecraft.world.item.CreativeModeTab;

/** One existing creative-tab slot, including Fabric's page index. */
public record FabricTabPosition(int page, CreativeModeTab.Row row, int column) {}
