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
		return metadata;
	}

	@Override
	public int getMetadata(int metadata) {
		return metadata + 1;
	}

	@Override
	public String getItemNameIS(ItemStack itemStack) {
		return super.getItemName() + "." + mod_Transport.pipeNames[itemStack.getItemDamage()];
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side) {
		if (entityPlayer.isSneaking()) return onItemUse(itemStack, entityPlayer, world, x, y, z, side);
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side) {
		if (itemStack.stackSize == 0) return false;

		int blockID = world.getBlockId(x, y, z);

		/* Find the right block we tried to place something on */
		if (blockID == this.shiftedIndex && mod_Transport.blockPipe.getPipeType(world, x, y, z) == 0) {

		} else if (blockID != Block.snow.blockID && blockID != Block.vine.blockID && blockID != Block.tallGrass.blockID && blockID != Block.deadBush.blockID
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
		if (world.getBlockId(x, y, z) == this.shiftedIndex && mod_Transport.blockPipe.getPipeType(world, x, y, z) == 0) {
			mod_Transport.blockPipe.setPipeType(world, x, y, z, this.getMetadata(itemStack.getItemDamage()));
			Graph.getGraph(world).onPipeAdd(x, y, z);

			if (!entityPlayer.capabilities.isCreativeMode) --itemStack.stackSize;
			return true;
		}

		if (!entityPlayer.canPlayerEdit(x, y, z)) return false;
		if (!world.canBlockBePlacedAt(mod_Transport.blockPipe.blockID, x, y, z, false, side)) return false;

		if (!world.setBlockAndMetadataWithNotify(x, y, z, mod_Transport.blockPipe.blockID, this.getMetadata(itemStack.getItemDamage()))) return false;

		if (world.getBlockId(x, y, z) == mod_Transport.blockPipe.blockID) {
			mod_Transport.blockPipe.onBlockPlaced(world, x, y, z, side);
			mod_Transport.blockPipe.onBlockPlacedBy(world, x, y, z, entityPlayer);
		}

		world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), mod_Transport.blockPipe.stepSound.getStepSound(),
				(mod_Transport.blockPipe.stepSound.getVolume() + 1.0F) / 2.0F, mod_Transport.blockPipe.stepSound.getPitch() * 0.8F);
		if (!entityPlayer.capabilities.isCreativeMode) --itemStack.stackSize;
		return true;
	}
}
