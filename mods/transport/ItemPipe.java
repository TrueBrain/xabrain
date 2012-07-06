package xabrain.mods.transport;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemPipe extends ItemBlock {
	public ItemPipe(int itemID) {
		super(itemID);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	// @Override (client-only)
	public int getIconFromDamage(int metadata) {
		return metadata - 1;
	}

	@Override
	public int getMetadata(int metadata) {
		return metadata;
	}

	@Override
	public String getItemNameIS(ItemStack itemStack) {
		return super.getItemName() + "." + mod_Transport.pipeNames[itemStack.getItemDamage() - 1];
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side) {
		if (itemStack.stackSize == 0) return false;

		int blockID = world.getBlockId(x, y, z);

		/* Find the right block we tried to place something on */
		if (blockID == Block.snow.blockID) {
			side = 1;
		} else if (blockID != Block.vine.blockID && blockID != Block.tallGrass.blockID && blockID != Block.deadBush.blockID
				&& (Block.blocksList[blockID] != null && !Block.blocksList[blockID].isBlockReplaceable(world, x, y, z))) {
			if (side == 0) --y;
			if (side == 1) ++y;
			if (side == 2) --z;
			if (side == 3) ++z;
			if (side == 4) --x;
			if (side == 5) ++x;
		}

		/*
		 * If it was a pipe block, but it contains a placeholder, make it a real
		 * pipe now
		 */
		if (world.getBlockId(x, y, z) == this.shiftedIndex && world.getBlockMetadata(x, y, z) == 0) {
			world.setBlockMetadataWithNotify(x, y, z, this.getMetadata(itemStack.getItemDamage()));
			world.markBlockNeedsUpdate(x, y, z);

			if (!entityPlayer.capabilities.isCreativeMode) --itemStack.stackSize;
			return true;
		}

		return false;
	}
}
