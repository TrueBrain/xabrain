package xabrain.mods.transport;

import java.util.ArrayList;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
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

	/*
	 * XXX -- Purely for naming convention, this is named the same as the Block
	 * variant
	 */
	public int damageDropped(int metadata) {
		return metadata - 1;
	}

	@Override
	public int getMetadata(int metadata) {
		return metadata + 1;
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
		if (entityPlayer.isSneaking()) return onItemUse(itemStack, entityPlayer, world, x, y, z, side);
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side) {
		if (itemStack.stackSize == 0) return false;

		int blockID = world.getBlockId(x, y, z);

		/* Find the right block we tried to place something on */
		if (mod_Transport.blockPipeSimple.isPipe(blockID)) {

		} else if (blockID == Block.snow.blockID || blockID == Block.vine.blockID || blockID == Block.tallGrass.blockID || blockID == Block.deadBush.blockID
				|| (Block.blocksList[blockID] != null && Block.blocksList[blockID].isBlockReplaceable(world, x, y, z))) {
			side = 0;
		} else {
			if (side == 0) --y;
			if (side == 1) ++y;
			if (side == 2) --z;
			if (side == 3) ++z;
			if (side == 4) --x;
			if (side == 5) ++x;

			side ^= 0x1;
		}

		/* If there isn't a pipe here yet, try to place a complex one */
		if (!mod_Transport.blockPipeSimple.isPipe(world.getBlockId(x, y, z))) {
			if (!entityPlayer.canPlayerEdit(x, y, z)) return false;
			if (!world.canBlockBePlacedAt(mod_Transport.blockPipeComplex.blockID, x, y, z, false, side)) return false;

			if (!world.setBlockAndMetadataWithNotify(x, y, z, mod_Transport.blockPipeComplex.blockID, 0)) return false;

			if (world.getBlockId(x, y, z) == mod_Transport.blockPipeComplex.blockID) {
				mod_Transport.blockPipeComplex.onBlockPlaced(world, x, y, z, side);
				mod_Transport.blockPipeComplex.onBlockPlacedBy(world, x, y, z, entityPlayer);
			}
		}
		/* If the current block is a simple pipe, upgrade it to a complex */
		if (world.getBlockId(x, y, z) != mod_Transport.blockPipeComplex.blockID) {
			if (!world.setBlockAndMetadataWithNotify(x, y, z, mod_Transport.blockPipeComplex.blockID, world.getBlockMetadata(x, y, z))) return false;
			TileEntityPipe te = mod_Transport.blockPipeComplex.getTileEntity(world, x, y, z);
			te.makePipe();
		}

		/* If we already have one, don't place a new one */
		TileEntityPipe te = mod_Transport.blockPipeComplex.getTileEntity(world, x, y, z);
		if (te.hasConnector(side)) return false;

		/* Now place the connector here too */
		mod_Transport.blockPipeComplex.placeConnector(world, x, y, z, side, (byte) this.getMetadata(itemStack.getItemDamage()));

		world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), mod_Transport.blockPipeSimple.stepSound.getStepSound(),
				(mod_Transport.blockPipeSimple.stepSound.getVolume() + 1.0F) / 2.0F, mod_Transport.blockPipeSimple.stepSound.getPitch() * 0.8F);
		if (!entityPlayer.capabilities.isCreativeMode) --itemStack.stackSize;
		return true;
	}
}
