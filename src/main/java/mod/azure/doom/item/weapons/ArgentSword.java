package mod.azure.doom.item.weapons;

import java.util.List;

import mod.azure.doom.util.enums.DoomTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ArgentSword extends SwordItem {

	public ArgentSword() {
		super(DoomTier.DOOM, 6, -2.4F, new Item.Properties().stacksTo(1).durability(9000));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> list, TooltipFlag tooltipFlag) {
		list.add(Component.translatable("doom.argent_powered.text").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
		super.appendHoverText(itemStack, level, list, tooltipFlag);
	}
}