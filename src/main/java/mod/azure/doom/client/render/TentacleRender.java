package mod.azure.doom.client.render;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.azure.doom.client.models.TentacleModel;
import mod.azure.doom.entity.tierambient.TentacleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TentacleRender extends GeoEntityRenderer<TentacleEntity> {

	public TentacleRender(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn, new TentacleModel());
	}

	@Override
	protected float getDeathMaxRotation(TentacleEntity entityLivingBaseIn) {
		return 0.0F;
	}

}