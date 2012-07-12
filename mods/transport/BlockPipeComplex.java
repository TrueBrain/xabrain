package xabrain.mods.transport;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockPipeComplex extends BlockPipe {
	public BlockPipeComplex(int blockID) {
		super(blockID);
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 0;
	}

	@Override
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		/* Don't open the GUI if we are sneaking */
		if (entityPlayer.isSneaking()) return false;

		TileEntityPipe te = getTileEntity(world, x, y, z);

		/*
		 * XXX -- Temporary, till I figured out how I can see which side you
		 * selected
		 */
		int i;
		for (i = 0; i < 6; i++) {
			if (te.hasConnector(i)) break;
		}
		entityPlayer.openGui(mod_Transport.instance, i, world, x, y, z);
		return true;
	}

	@Override
	public boolean hasPipe(IBlockAccess world, int x, int y, int z) {
		TileEntityPipe te = getTileEntity(world, x, y, z);
		return te.hasPipe();
	}

	public void makePipe(World world, int x, int y, int z, int metadata) {
		TileEntityPipe te = getTileEntity(world, x, y, z);
		te.makePipe();

		world.setBlockMetadataWithNotify(x, y, z, metadata);

		Graph.getGraph(world).onPipeAdd(x, y, z);
	}

	public boolean placeConnector(World world, int x, int y, int z, int side, byte type) {
		TileEntityPipe te = getTileEntity(world, x, y, z);

		if (te.hasConnector(side)) return false;
		te.setConnector(side, type);
		world.notifyBlockChange(x, y, z, this.blockID);

		return true;
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!player.capabilities.isCreativeMode) {
			/* Drop the connectors */
			TileEntityPipe te = getTileEntity(world, x, y, z);
			if (te != null) {
				for (int i = 0; i < 6; i++) {
					if (!te.hasConnector(i)) continue;

					dropBlockAsItem_do(world, x, y, z, new ItemStack(mod_Transport.itemConnector.shiftedIndex, 1, mod_Transport.itemConnector.damageDropped(te.getConnector(i).type)));
					/*
					 * TODO -- We should also drop the modules inside the
					 * connectors
					 */
				}
			}

			/* Drop the pipe if there was one */
			if (hasPipe(world, x, y, z)) dropBlockAsItem_do(world, x, y, z, new ItemStack(mod_Transport.blockPipe.blockID, 1, world.getBlockMetadata(x, y, z)));
		}

		return super.removeBlockByPlayer(world, player, x, y, z);
	}

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
		if (!hasPipe(world, x, y, z)) return;

		super.onBlockRemoval(world, x, y, z);
	}

	public TileEntityPipe getTileEntity(IBlockAccess world, int x, int y, int z) {
		return (TileEntityPipe) world.getBlockTileEntity(x, y, z);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	public TileEntity getTileEntity(int metadata) {
		return new TileEntityPipe();
	}
}
