package net.funkpla.guntotingillagers;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GunTotingIllagersMod implements ModInitializer {
	public static final String MOD_ID = "guntotingillagers";
	public static ResourceLocation locate(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static final EntityType<Musketeer> MUSKETEER = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			GunTotingIllagersMod.locate("musketeer"),
			FabricEntityTypeBuilder.create(MobCategory.MONSTER, Musketeer::new).build()
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing GunTotingIllagers");
		FabricDefaultAttributeRegistry.register(MUSKETEER, Musketeer.createAttributes());
	}
}