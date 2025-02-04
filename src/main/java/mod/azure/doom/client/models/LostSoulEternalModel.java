package mod.azure.doom.client.models;

import mod.azure.azurelib.model.GeoModel;
import mod.azure.doom.DoomMod;
import mod.azure.doom.entity.tierfodder.LostSoulEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class LostSoulEternalModel extends GeoModel<LostSoulEntity> {

	private static final ResourceLocation[] TEX = { DoomMod.modResource("textures/entity/lostsould_eternal_1.png"), DoomMod.modResource("textures/entity/lostsould_eternal_2.png"), DoomMod.modResource("textures/entity/lostsould_eternal_3.png"), DoomMod.modResource("textures/entity/lostsould_eternal_4.png"), DoomMod.modResource("textures/entity/lostsould_eternal_5.png"), DoomMod.modResource("textures/entity/lostsould_eternal_6.png"), DoomMod.modResource("textures/entity/lostsould_eternal_7.png"),
			DoomMod.modResource("textures/entity/lostsould_eternal_8.png") };

	private static final ResourceLocation[] TEX1 = { DoomMod.modResource("textures/entity/lostsould_2016_1.png"), DoomMod.modResource("textures/entity/lostsould_2016_2.png"), DoomMod.modResource("textures/entity/lostsould_2016_3.png"), DoomMod.modResource("textures/entity/lostsould_2016_4.png"), DoomMod.modResource("textures/entity/lostsould_2016_5.png"), DoomMod.modResource("textures/entity/lostsould_2016_6.png"), DoomMod.modResource("textures/entity/lostsould_2016_7.png"),
			DoomMod.modResource("textures/entity/lostsould_2016_8.png") };

	@Override
	public ResourceLocation getModelResource(LostSoulEntity object) {
		return DoomMod.modResource("geo/lostsouleternal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LostSoulEntity object) {
		return object.getVariant() == 2 ? TEX1[(object.getFlameTimer())] : TEX[(object.getFlameTimer())];
	}

	@Override
	public ResourceLocation getAnimationResource(LostSoulEntity object) {
		return DoomMod.modResource("animations/lostsoul_animation.json");
	}

	@Override
	public RenderType getRenderType(LostSoulEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(getTextureResource(animatable));
	}
}