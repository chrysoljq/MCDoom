package mod.azure.doom.item.weapons;

import java.util.function.Consumer;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.doom.DoomMod;
import mod.azure.doom.client.ClientInit;
import mod.azure.doom.client.render.weapons.BallistaRender;
import mod.azure.doom.entity.projectiles.ArgentBoltEntity;
import mod.azure.doom.util.enums.DoomTier;
import mod.azure.doom.util.registry.DoomItems;
import mod.azure.doom.util.registry.DoomSounds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Ballista extends DoomBaseItem {

	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

	public Ballista() {
		super(new Item.Properties().stacksTo(1).durability(11));
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		return DoomTier.BALLISTA.getRepairIngredient().test(repair) || super.isValidRepairItem(toRepair, repair);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof Player playerentity) {
			if (stack.getDamageValue() < stack.getMaxDamage() - 1) {
				playerentity.getCooldowns().addCooldown(this, 17);
				if (!worldIn.isClientSide) {
					final var bolt = createArrow(worldIn, stack, playerentity);
					bolt.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot(), 0.0F, 1.0F * 3.0F, 1.0F);
					bolt.setParticle(true);
					bolt.isNoGravity();
					worldIn.addFreshEntity(bolt);

					stack.hurtAndBreak(1, entityLiving, p -> p.broadcastBreakEvent(entityLiving.getUsedItemHand()));
					worldIn.playSound((Player) null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), DoomSounds.BALLISTA_FIRING, SoundSource.PLAYERS, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + 0.25F * 0.5F);
					triggerAnim(playerentity, GeoItem.getOrAssignId(stack, (ServerLevel) worldIn), "shoot_controller", "firing");
				}
				final var isInsideWaterBlock = playerentity.level.isWaterAt(playerentity.blockPosition());
				spawnLightSource(entityLiving, isInsideWaterBlock);
			} else
				worldIn.playSound((Player) null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), DoomSounds.EMPTY, SoundSource.PLAYERS, 1.0F, 1.5F);
		}
	}

	public static float getArrowVelocity(int charge) {
		var f = charge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F)
			f = 1.0F;

		return f;
	}

	public static void reload(Player user, InteractionHand hand) {
		if (user.getItemInHand(hand).getItem() instanceof Ballista) {
			while (!user.isCreative() && user.getItemInHand(hand).getDamageValue() != 0 && user.getInventory().countItem(DoomItems.ARGENT_BOLT) > 0) {
				removeAmmo(DoomItems.ARGENT_BOLT, user);
				user.getItemInHand(hand).hurtAndBreak(-1, user, s -> user.broadcastBreakEvent(hand));
				user.getItemInHand(hand).setPopTime(3);
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (world.isClientSide)
			if (((Player) entity).getMainHandItem().getItem() instanceof Ballista && ClientInit.reload.consumeClick() && selected) {
				final var passedData = new FriendlyByteBuf(Unpooled.buffer());
				passedData.writeBoolean(true);
				ClientPlayNetworking.send(DoomMod.BALLISTA, passedData);
			}
	}

	public ArgentBoltEntity createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
		final var bolt = new ArgentBoltEntity(worldIn, shooter);
		return bolt;
	}

	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(new RenderProvider() {
			private BallistaRender renderer = null;

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				if (renderer == null)
					return new BallistaRender();
				return this.renderer;
			}
		});
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return renderProvider;
	}
}