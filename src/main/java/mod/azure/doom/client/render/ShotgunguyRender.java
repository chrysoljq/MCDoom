package mod.azure.doom.client.render;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.azure.azurelib.renderer.layer.BlockAndItemGeoLayer;
import mod.azure.doom.client.models.ShotgunguyModel;
import mod.azure.doom.entity.tierfodder.ShotgunguyEntity;
import mod.azure.doom.util.registry.DoomItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ShotgunguyRender extends GeoEntityRenderer<ShotgunguyEntity> {

	public ShotgunguyRender(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn, new ShotgunguyModel());
		addRenderLayer(new BlockAndItemGeoLayer<>(this) {
			@Nullable
			@Override
			protected ItemStack getStackForBone(GeoBone bone, ShotgunguyEntity animatable) {
				return switch (bone.getName()) {
				case "bipedLeftArm_1" -> new ItemStack(DoomItems.SG);
				default -> null;
				};
			}

			@Override
			protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, ShotgunguyEntity animatable) {
				return switch (bone.getName()) {
				default -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
				};
			}

			@Override
			protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, ShotgunguyEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
				poseStack.mulPose(Axis.XP.rotationDegrees(-110));
				poseStack.mulPose(Axis.YP.rotationDegrees(0));
				poseStack.mulPose(Axis.ZP.rotationDegrees(0));
				poseStack.translate(0.0D, 0.0D, -0.4D);
				super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
			}
		});
	}

	@Override
	protected float getDeathMaxRotation(ShotgunguyEntity entityLivingBaseIn) {
		return 0.0F;
	}

}