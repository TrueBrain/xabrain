package xabrain.mods.transport;

import java.util.ArrayList;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.forge.ITextureProvider;

public class ItemModule extends Item implements ITextureProvider {
	protected ItemModule(int itemID) {
		super(itemID);
		setIconIndex(0);
		setItemName("module");
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	public String getTextureFile() {
		return "/xabrain/mods/transport/resources/items.png";
	}

	// @Override (client-only)
	public int getIconFromDamage(int metadata) {
		return metadata + 0x10;
	}

	@Override
	public int getMetadata(int metadata) {
		return metadata;
	}

	@Override
	public String getItemNameIS(ItemStack itemStack) {
		return super.getItemName() + "." + mod_Transport.moduleNames[itemStack.getItemDamage()];
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {
		for (int x = 0; x < mod_Transport.moduleNames.length; x++) {
			itemList.add(new ItemStack(this, 1, x));
		}
	}
}
