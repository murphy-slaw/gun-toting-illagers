package net.funkpla.guntotingillagers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class GunTotingIllagersClient implements ClientModInitializer {
	public static final ModelLayerLocation MODEL_MUSKETEER = new ModelLayerLocation(GunTotingIllagersMod.locate("musketeer"),"main");

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(GunTotingIllagersMod.MUSKETEER, MusketeerRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(MODEL_MUSKETEER, IllagerModel::createBodyLayer);
	}
}