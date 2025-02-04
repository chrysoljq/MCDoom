package mod.azure.doom.client.models.tile;

import mod.azure.azurelib.model.GeoModel;
import mod.azure.doom.DoomMod;
import mod.azure.doom.entity.tileentity.GunBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GunCraftingModel extends GeoModel<GunBlockEntity> {
	@Override
	public ResourceLocation getAnimationResource(GunBlockEntity entity) {
		return DoomMod.modResource("animations/gun_table.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GunBlockEntity animatable) {
		return DoomMod.modResource("geo/gun_table.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GunBlockEntity entity) {
		return DoomMod.modResource("textures/block/gun_table.png");
	}
	
	@Override
	public RenderType getRenderType(GunBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(getTextureResource(animatable));
	}
}
