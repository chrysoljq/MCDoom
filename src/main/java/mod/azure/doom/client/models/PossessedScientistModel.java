package mod.azure.doom.client.models;

import mod.azure.azurelib.constant.DataTickets;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.doom.DoomMod;
import mod.azure.doom.entity.tierfodder.PossessedScientistEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class PossessedScientistModel extends GeoModel<PossessedScientistEntity> {

	@Override
	public ResourceLocation getModelResource(PossessedScientistEntity object) {
		return DoomMod.modResource("geo/scientistpossessed.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PossessedScientistEntity object) {
		return DoomMod.modResource("textures/entity/possessedscientist.png");
	}

	@Override
	public ResourceLocation getAnimationResource(PossessedScientistEntity object) {
		return DoomMod.modResource("animations/possessed_scientist_animation.json");
	}

	@Override
	public void setCustomAnimations(PossessedScientistEntity animatable, long instanceId, AnimationState<PossessedScientistEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);

		var head = getAnimationProcessor().getBone("Head");
		var entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

		if (head != null) {
			head.setRotX(entityData.headPitch() * ((float) Math.PI / 360F));
			head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 340F));
		}
	}

	@Override
	public RenderType getRenderType(PossessedScientistEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(getTextureResource(animatable));
	}
}