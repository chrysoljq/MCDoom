package mod.azure.doom.client.render;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.azure.doom.client.models.SummonerModel;
import mod.azure.doom.entity.tiersuperheavy.SummonerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SummonerRender extends GeoEntityRenderer<SummonerEntity> {

	public SummonerRender(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn, new SummonerModel());
	}

	@Override
	protected float getDeathMaxRotation(SummonerEntity entityLivingBaseIn) {
		return 0.0F;
	}

}