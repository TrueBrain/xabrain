package xabrain.mods.ore;

import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ItemOre extends ItemBlock {
	public ItemOre(int itemID) {
		super(itemID);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	// @Override (client-only)
	public int getIconFromDamage(int metadata) {
		return metadata;
	}

	@Override
	public int getMetadata(int metadata) {
		return metadata;
	}

	@Override
	public String getItemNameIS(ItemStack itemStack) {
		return super.getItemName() + "." + mod_Ore.oreNames[itemStack.getItemDamage()];
	}
}
