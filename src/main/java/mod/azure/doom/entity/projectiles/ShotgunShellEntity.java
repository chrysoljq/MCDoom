package mod.azure.doom.entity.projectiles;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager.ControllerRegistrar;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.network.packet.EntityPacket;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.doom.entity.tierboss.IconofsinEntity;
import mod.azure.doom.util.registry.DoomItems;
import mod.azure.doom.util.registry.DoomProjectiles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ShotgunShellEntity extends AbstractArrow implements GeoEntity {
	protected int timeInAir;
	protected boolean inAir;
	private int ticksInAir;
	public float shelldamage;
	private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
	public SoundEvent hitSound = getDefaultHitGroundSoundEvent();

	public ShotgunShellEntity(EntityType<? extends ShotgunShellEntity> type, Level world) {
		super(type, world);
		pickup = AbstractArrow.Pickup.DISALLOWED;
	}

	public ShotgunShellEntity(Level world, LivingEntity owner, float damage) {
		super(DoomProjectiles.SHOTGUN_SHELL, owner, world);
		shelldamage = damage;
	}

	protected ShotgunShellEntity(EntityType<? extends ShotgunShellEntity> type, double x, double y, double z, Level world) {
		this(type, world);
	}

	protected ShotgunShellEntity(EntityType<? extends ShotgunShellEntity> type, LivingEntity owner, Level world) {
		this(type, owner.getX(), owner.getEyeY() - 0.10000000149011612D, owner.getZ(), world);
		setOwner(owner);
		if (owner instanceof Player)
			pickup = AbstractArrow.Pickup.DISALLOWED;
	}

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, event -> PlayState.CONTINUE));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return EntityPacket.createPacket(this);
	}

	@Override
	protected void tickDespawn() {
		++ticksInAir;
		if (tickCount >= 40)
			remove(RemovalReason.KILLED);
	}

	@Override
	protected void doPostHurtEffects(LivingEntity living) {
		super.doPostHurtEffects(living);
		if (!(living instanceof Player) && !(living instanceof IconofsinEntity)) {
			living.setDeltaMovement(0, 0, 0);
			living.invulnerableTime = 0;
		}
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		super.shoot(x, y, z, velocity, inaccuracy);
		ticksInAir = 0;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putShort("life", (short) ticksInAir);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		ticksInAir = compound.getShort("life");
	}

	@Override
	public void tick() {
		super.tick();
		++ticksInAir;
		if (ticksInAir >= 80)
			remove(Entity.RemovalReason.DISCARDED);
		if (level.isClientSide())
			level.addParticle(ParticleTypes.SMOKE, true, this.getX() + (random.nextDouble() * 2.0D - 1.0D) * getBbWidth() * 0.5D, this.getY(), this.getZ() + (random.nextDouble() * 2.0D - 1.0D) * getBbWidth() * 0.5D, 0, 0, 0);
	}

	@Override
	public ItemStack getPickupItem() {
		return new ItemStack(DoomItems.SHOTGUN_SHELLS);
	}

	public void initFromStack(ItemStack stack) {
		if (stack.getItem() == DoomItems.SHOTGUN_SHELLS) {
			// this.dataTracker.isDirty();
		}

	}

	@Override
	public boolean isNoGravity() {
		if (isInWater())
			return false;
		return true;
	}

	@Override
	public void setSoundEvent(SoundEvent soundIn) {
		hitSound = soundIn;
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundEvents.ARMOR_EQUIP_IRON;
	}

	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		if (!level.isClientSide())
			remove(Entity.RemovalReason.DISCARDED);
		setSoundEvent(SoundEvents.ARMOR_EQUIP_IRON);
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		final var entity = entityHitResult.getEntity();
		if (entityHitResult.getType() != HitResult.Type.ENTITY || !entityHitResult.getEntity().is(entity))
			if (!level.isClientSide)
				remove(RemovalReason.KILLED);
		final var entity1 = getOwner();
		DamageSource damagesource;
		if (entity1 == null)
			damagesource = damageSources().arrow(this, this);
		else {
			damagesource = damageSources().arrow(this, entity1);
			if (entity1 instanceof LivingEntity)
				((LivingEntity) entity1).setLastHurtMob(entity);
		}
		if (entity.hurt(damagesource, shelldamage)) {
			if (entity instanceof LivingEntity livingentity) {
				if (!level.isClientSide && entity1 instanceof LivingEntity) {
					EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
					EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity);
					remove(RemovalReason.KILLED);
				}
				doPostHurtEffects(livingentity);
				if (entity1 != null && livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !isSilent())
					((ServerPlayer) entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
			}
		} else if (!level.isClientSide)
			remove(RemovalReason.KILLED);
	}

	@Override
	public boolean displayFireAnimation() {
		return false;
	}

}