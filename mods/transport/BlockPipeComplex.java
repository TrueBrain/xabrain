package xabrain.mods.transport;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3D;
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
		if (mod_Transport.proxy.isRemote()) return false;

		/* Don't open the GUI if we are sneaking */
		if (entityPlayer.isSneaking()) return false;

		int part = tracePartHit(world, x, y, z, entityPlayer);
		if (part == -1) return false;

		entityPlayer.openGui(mod_Transport.instance, part, world, x, y, z);
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

	private void dropConnector(TileEntityPipe te, int side, World world, int x, int y, int z) {
		dropBlockAsItem_do(world, x, y, z, new ItemStack(mod_Transport.itemConnector.shiftedIndex, 1, mod_Transport.itemConnector.damageDropped(te.getConnector(side).type)));
	}

	private void dropModules(TileEntityPipe te, int side, World world, int x, int y, int z) {
		Connector connector = te.getConnector(side);
		for (int i = 0; i < connector.slots; i++) {
			if (connector.modulesItemStack[i] == null) continue;

			dropBlockAsItem_do(world, x, y, z, connector.modulesItemStack[i]);
		}
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int part = tracePartHit(world, x, y, z, player);

		if (part != -1) {
			TileEntityPipe te = getTileEntity(world, x, y, z);
			if (!player.capabilities.isCreativeMode) dropConnector(te, part, world, x, y, z);
			dropModules(te, part, world, x, y, z);

			te.setConnector(part, (byte) 0);

			boolean hasConnector = false;
			for (int i = 0; i < 6; i++) {
				if (!te.hasConnector(i)) continue;
				hasConnector = true;
				break;
			}

			/* This tile has no connectors anymore; downgrade to a normal block */
			if (!hasConnector) {
				world.setBlockAndMetadataWithNotify(x, y, z, mod_Transport.blockPipe.blockID, world.getBlockMetadata(x, y, z));
			} else {
				world.notifyBlockChange(x, y, z, this.blockID);
			}

			return true;
		}

		/* Drop the connectors and modules */
		TileEntityPipe te = getTileEntity(world, x, y, z);
		if (te != null) {
			for (int i = 0; i < 6; i++) {
				if (!te.hasConnector(i)) continue;

				if (!player.capabilities.isCreativeMode) dropConnector(te, part, world, x, y, z);
				dropModules(te, part, world, x, y, z);
			}
		}

		/* Drop the pipe if there was one */
		if (!player.capabilities.isCreativeMode && hasPipe(world, x, y, z)) dropBlockAsItem_do(world, x, y, z, new ItemStack(mod_Transport.blockPipe.blockID, 1, world.getBlockMetadata(x, y, z)));

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

	private int tracePartHit(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		TileEntityPipe te = getTileEntity(world, x, y, z);

		double distance = 5.0f;
		Vec3D look = entityPlayer.getLookVec();
		Vec3D position = Vec3D.createVector(entityPlayer.posX, entityPlayer.posY + 1.62D - (double) entityPlayer.yOffset, entityPlayer.posZ);
		Vec3D lookDistance = position.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);

		/* Do a raytrace to see which connector we want to open */
		MovingObjectPosition res = world.rayTraceBlocks_do(position, lookDistance, true);
		if (res == null) return -1;

		return res.subHit;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3D par5Vec3D, Vec3D par6Vec3D) {
		BlockPipe block = (BlockPipe) Block.blocksList[world.getBlockId(x, y, z)];
		TileEntityPipe te = block.getTileEntity(world, x, y, z);
		MovingObjectPosition best = null;

		float sideMin = 0.2f;
		float sideMax = 0.8f;

		if (te == null) return null;

		if (te.hasConnector(0)) {
			block.setBlockBounds(sideMin, 0.0f, sideMin, sideMax, 0.1f, sideMax);
			MovingObjectPosition res = super.collisionRayTrace(world, x, y, z, par5Vec3D, par6Vec3D);
			if (res != null) {
				res.subHit = 0;
				if (res.sideHit == 0) return res;
			}

			if (best == null) best = res;
		}
		if (te.hasConnector(1)) {
			block.setBlockBounds(sideMin, 0.9f, sideMin, sideMax, 1.0f, sideMax);
			MovingObjectPosition res = super.collisionRayTrace(world, x, y, z, par5Vec3D, par6Vec3D);
			if (res != null) {
				res.subHit = 1;
				if (res.sideHit == 1) return res;
			}

			if (best == null) best = res;
		}
		if (te.hasConnector(2)) {
			block.setBlockBounds(sideMin, sideMin, 0.0f, sideMax, sideMax, 0.1f);
			MovingObjectPosition res = super.collisionRayTrace(world, x, y, z, par5Vec3D, par6Vec3D);
			if (res != null) {
				res.subHit = 2;
				if (res.sideHit == 2) return res;
			}

			if (best == null) best = res;
		}
		if (te.hasConnector(3)) {
			block.setBlockBounds(sideMin, sideMin, 0.9f, sideMax, sideMax, 1.0f);
			MovingObjectPosition res = super.collisionRayTrace(world, x, y, z, par5Vec3D, par6Vec3D);
			if (res != null) {
				res.subHit = 3;
				if (res.sideHit == 3) return res;
			}

			if (best == null) best = res;
		}
		if (te.hasConnector(4)) {
			block.setBlockBounds(0.0f, sideMin, sideMin, 0.1f, sideMax, sideMax);
			MovingObjectPosition res = super.collisionRayTrace(world, x, y, z, par5Vec3D, par6Vec3D);
			if (res != null) {
				res.subHit = 4;
				if (res.sideHit == 4) return res;
			}

			if (best == null) best = res;
		}
		if (te.hasConnector(5)) {
			block.setBlockBounds(0.9f, sideMin, sideMin, 1.0f, sideMax, sideMax);
			MovingObjectPosition res = super.collisionRayTrace(world, x, y, z, par5Vec3D, par6Vec3D);
			if (res != null) {
				res.subHit = 5;
				if (res.sideHit == 5) return res;
			}

			if (best == null) best = res;
		}

		return best;
	}

}
