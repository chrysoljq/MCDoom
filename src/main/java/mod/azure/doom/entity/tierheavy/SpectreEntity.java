package mod.azure.doom.entity.tierheavy;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager.ControllerRegistrar;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.doom.DoomMod;
import mod.azure.doom.entity.DemonEntity;
import mod.azure.doom.entity.DoomAnimationsDefault;
import mod.azure.doom.util.registry.DoomSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.UnreachableTargetSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;

public class SpectreEntity extends DemonEntity implements SmartBrainOwner<SpectreEntity> {

	private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
	public static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SpectreEntity.class, EntityDataSerializers.INT);

	public SpectreEntity(EntityType<SpectreEntity> entityType, Level worldIn) {
		super(entityType, worldIn);
	}

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "livingController", 0, event -> {
			if (event.isMoving() && !isAggressive())
				return event.setAndContinue(DoomAnimationsDefault.WALKING);
			if (isAggressive() && !(dead || getHealth() < 0.01 || isDeadOrDying()))
				return event.setAndContinue(DoomAnimationsDefault.ATTACKING);
			if (dead || getHealth() < 0.01 || isDeadOrDying())
				return event.setAndContinue(DoomAnimationsDefault.DEATH);
			return event.setAndContinue(DoomAnimationsDefault.IDLE);
		}).setSoundKeyframeHandler(event -> {
			if (event.getKeyframeData().getSound().matches("walk"))
				if (level.isClientSide())
					getLevel().playLocalSound(this.getX(), this.getY(), this.getZ(), DoomSounds.PINKY_STEP, SoundSource.HOSTILE, 0.25F, 1.0F, false);
			if (event.getKeyframeData().getSound().matches("yell"))
				if (level.isClientSide())
					getLevel().playLocalSound(this.getX(), this.getY(), this.getZ(), DoomSounds.PINKY_YELL, SoundSource.HOSTILE, 0.25F, 1.0F, false);
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	protected void tickDeath() {
		++deathTime;
		if (deathTime == 80) {
			remove(RemovalReason.KILLED);
			dropExperience();
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(VARIANT, 0);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setVariant(tag.getInt("Variant"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("Variant", getVariant());
	}

	public int getVariant() {
		return Mth.clamp(entityData.get(VARIANT), 1, 3);
	}

	public void setVariant(int variant) {
		entityData.set(VARIANT, variant);
	}

	public int getVariants() {
		return 3;
	}

	@Override
	protected void customServerAiStep() {
		tickBrain(this);
		super.customServerAiStep();
	}

	@Override
	protected Brain.Provider<?> brainProvider() {
		return new SmartBrainProvider<>(this);
	}

	@Override
	public List<ExtendedSensor<SpectreEntity>> getSensors() {
		return ObjectArrayList.of(new NearbyLivingEntitySensor<SpectreEntity>().setPredicate((target, entity) -> target.isAlive() && entity.hasLineOfSight(target) && !(target instanceof DemonEntity)), new HurtBySensor<>(), new UnreachableTargetSensor<SpectreEntity>());
	}

	@Override
	public BrainActivityGroup<SpectreEntity> getCoreTasks() {
		return BrainActivityGroup.coreTasks(new LookAtTarget<>(), new LookAtTargetSink(40, 300), new FloatToSurfaceOfFluid<>(), new MoveToWalkTarget<>());
	}

	@Override
	public BrainActivityGroup<SpectreEntity> getIdleTasks() {
		return BrainActivityGroup.idleTasks(new FirstApplicableBehaviour<SpectreEntity>(new TargetOrRetaliate<>().alertAlliesWhen((mob, entity) -> this.isAggressive()), new SetPlayerLookTarget<>().stopIf(target -> !target.isAlive() || target instanceof Player && ((Player) target).isCreative()), new SetRandomLookTarget<>()), new OneRandomBehaviour<>(new SetRandomWalkTarget<>().setRadius(20).speedModifier(0.75f), new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))));
	}

	@Override
	public BrainActivityGroup<SpectreEntity> getFightTasks() {
		return BrainActivityGroup.fightTasks(new InvalidateAttackTarget<>().invalidateIf((target, entity) -> !target.isAlive()), new SetWalkTargetToAttackTarget<>().speedMod(1.75F), new AnimatableMeleeAttack<>(0));
	}

	@Override
	public double getMeleeAttackRangeSqr(LivingEntity livingEntity) {
		return this.getBbWidth() * 1.5f * (this.getBbWidth() * 1.5f + livingEntity.getBbWidth());
	}

	@Override
	public boolean isWithinMeleeAttackRange(LivingEntity livingEntity) {
		var distance = this.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
		return distance <= this.getMeleeAttackRangeSqr(livingEntity);
	}

	@Override
	protected void registerGoals() {
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag) {
		spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
		setVariant(random.nextInt());
		return spawnDataIn;
	}

	public static AttributeSupplier.Builder createMobAttributes() {
		return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 40.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.6f).add(Attributes.MAX_HEALTH, DoomMod.config.spectre_health).add(Attributes.ATTACK_DAMAGE, DoomMod.config.spectre_melee_damage).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_KNOCKBACK, 10.0D);
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return DoomSounds.PINKY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return DoomSounds.PINKY_DEATH;
	}

	protected SoundEvent getStepSound() {
		return DoomSounds.PINKY_STEP;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(getStepSound(), 0.15F, 1.0F);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 7;
	}

}