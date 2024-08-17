package net.funkpla.guntotingillagers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Pillager;
import org.jetbrains.annotations.NotNull;

public class MusketeerRenderer extends PillagerRenderer {
    private static final ResourceLocation MUSKETEER = GunTotingIllagersMod.locate("textures/entity/illager/musketeer.png");
    public MusketeerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    public @NotNull ResourceLocation getTextureLocation(Pillager entity) {
        return MUSKETEER;
    }
}
