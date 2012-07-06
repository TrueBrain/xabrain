package xabrain.mods.ore;

import java.util.ArrayList;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.forge.ITextureProvider;

public class ItemIngot extends Item implements ITextureProvider {
	protected ItemIngot(int itemID) {
		super(itemID);
		setIconIndex(0);
		setItemName("ingot");
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	public String getTextureFile() {
		return "/xabrain/mods/ore/resources/items.png";
	}

	// @Override (client-only)
	public int getIconFromDamage(int metadata) {
		return metadata;
	}

	@Override
	public String getItemNameIS(ItemStack itemStack) {
		return super.getItemName() + "." + mod_Ore.oreNames[itemStack.getItemDamage()];
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {
		for (int x = 0; x < mod_Ore.oreNames.length; x++) {
			itemList.add(new ItemStack(this, 1, x));
		}
	}
}
