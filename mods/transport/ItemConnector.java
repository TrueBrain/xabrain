package xabrain.mods.transport;

import java.util.ArrayList;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraft.src.forge.ITextureProvider;

public class ItemConnector extends Item implements ITextureProvider {
	protected ItemConnector(int itemID) {
		super(itemID);
		setIconIndex(0);
		setItemName("connector");
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	public String getTextureFile() {
		return "/xabrain/mods/transport/resources/items.png";
	}

	// @Override (client-only)
	public int getIconFromDamage(int metadata) {
		return metadata;
	}

	@Override
	public String getItemNameIS(ItemStack itemStack) {
		return super.getItemName() + "." + mod_Transport.connectorNames[itemStack.getItemDamage()];
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {
		for (int x = 0; x < mod_Transport.connectorNames.length; x++) {
			itemList.add(new ItemStack(this, 1, x));
		}
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side) {
		/* There is already a pipe on this tile; now place the connector. */
		if (world.getBlockId(x, y, z) == mod_Transport.blockPipe.blockID) {
			placeConnector(world, x, y, z, side);
			return true;
		}

		/* Check if we can place an (empty) pipe on the tile */
		if (itemsList[mod_Transport.blockPipe.blockID].onItemUse(new ItemStack(mod_Transport.blockPipe, 1, 0), entityPlayer, world, x, y, z, side)) {
			/* Now place the connector here too */
			placeConnector(world, x, y, z, side);
			return true;
		}

		return false;
	}
	
	public void placeConnector(World world, int x, int y, int z, int side) {

	}
}
