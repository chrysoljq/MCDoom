package mod.azure.doom.client.models;

import mod.azure.azurelib.model.GeoModel;
import mod.azure.doom.DoomMod;
import mod.azure.doom.entity.tierboss.SpiderMastermind2016Entity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class SpiderMastermind2016Model extends GeoModel<SpiderMastermind2016Entity> {

	@Override
	public ResourceLocation getModelResource(SpiderMastermind2016Entity object) {
		return DoomMod.modResource("geo/spidermastermind2016.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpiderMastermind2016Entity object) {
		return DoomMod.modResource("textures/entity/spidermastermind2016.png");
	}

	@Override
	public ResourceLocation getAnimationResource(SpiderMastermind2016Entity object) {
		return DoomMod.modResource("animations/spidermastermind2016.animation.json");
	}

	@Override
	public RenderType getRenderType(SpiderMastermind2016Entity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(getTextureResource(animatable));
	}
}