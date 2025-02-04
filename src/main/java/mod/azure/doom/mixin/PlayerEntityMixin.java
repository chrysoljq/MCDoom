package mod.azure.doom.mixin;

import static mod.azure.doom.DoomMod.DataTrackers.MEATHOOK_TRACKER;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.azure.doom.util.PlayerProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerProperties {

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void readNbt(CompoundTag tag, CallbackInfo info) {
		entityData.set(MEATHOOK_TRACKER, tag.getBoolean("hasHook"));
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	public void writeNbt(CompoundTag tag, CallbackInfo info) {
		tag.putBoolean("hasHook", entityData.get(MEATHOOK_TRACKER));
	}

	@Inject(method = "defineSynchedData", at = @At("HEAD"))
	public void initTracker(CallbackInfo info) {
		entityData.define(MEATHOOK_TRACKER, false);
	}

	@Override
	public boolean hasMeatHook() {
		return entityData.get(MEATHOOK_TRACKER);
	}

	@Override
	public void setHasMeatHook(boolean hasHook) {
		entityData.set(MEATHOOK_TRACKER, hasHook);
	}
}