package mod.azure.doom.client.render.armors;

import mod.azure.doom.client.models.armor.MidnightModel;
import mod.azure.doom.item.armor.MidnightDoomArmor;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.renderer.GeoArmorRenderer;

public class MidnightRender extends GeoArmorRenderer<MidnightDoomArmor> {
	public MidnightRender() {
		super(new MidnightModel());
	}

	@Override
	public GeoBone getLeftBootBone() {
		return this.model.getBone("armorRightBoot").orElse(null);
	}

	@Override
	public GeoBone getLeftLegBone() {
		return this.model.getBone("armorRightLeg").orElse(null);
	}

	@Override
	public GeoBone getRightBootBone() {
		return this.model.getBone("armorLeftBoot").orElse(null);
	}

	@Override
	public GeoBone getRightLegBone() {
		return this.model.getBone("armorLeftLeg").orElse(null);
	}
}