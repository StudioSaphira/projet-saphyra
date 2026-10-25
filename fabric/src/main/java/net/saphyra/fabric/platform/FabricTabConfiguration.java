package net.saphyra.fabric.platform;

import net.minecraft.resources.ResourceLocation;

/** Search and ordering options associated with one Fabric creative tab. */
record FabricTabConfiguration(boolean searchBar, ResourceLocation predecessor) {}
