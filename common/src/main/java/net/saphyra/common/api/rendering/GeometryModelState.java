package net.saphyra.common.api.rendering;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;
import org.jetbrains.annotations.NotNull;

/** Reusable client rendering utility, independent of consuming mods. */
public final class GeometryModelState implements ModelState {

    private final Transformation rotation;
    private final boolean uvLocked;

    public GeometryModelState(@NotNull Transformation rotation, boolean uvLocked)
    {this.rotation = rotation;this.uvLocked = uvLocked;}

    @Override
    public @NotNull Transformation getRotation() {
        return rotation;
    }

    @Override
    public boolean isUvLocked() {
        return uvLocked;
    }
}
