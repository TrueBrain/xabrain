package xabrain.mods.transport;

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
				if (te.hasPipe()) {
					world.setBlockAndMetadataWithNotify(x, y, z, mod_Transport.blockPipeSimple.blockID, world.getBlockMetadata(x, y, z));
				} else {
					world.setBlockWithNotify(x, y, z, 0);
				}
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

				if (!player.capabilities.isCreativeMode) dropConnector(te, i, world, x, y, z);
				dropModules(te, i, world, x, y, z);
			}
		}

		/* Drop the pipe if there was one */
		if (!player.capabilities.isCreativeMode && hasPipe(world, x, y, z))
			dropBlockAsItem_do(world, x, y, z, new ItemStack(mod_Transport.blockPipeSimple.blockID, 1, world.getBlockMetadata(x, y, z)));

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
		MovingObjectPosition res = collisionRayTrace(world, x, y, z, position, lookDistance);
		if (res == null) return -1;

		return res.subHit;
	}

	private MovingObjectPosition traceConnector(TileEntityPipe te, int side, World world, int x, int y, int z, Vec3D par5Vec3D, Vec3D par6Vec3D) {
		if (!te.hasConnector(side)) return null;

		float xMin = 0.2f;
		float yMin = 0.2f;
		float zMin = 0.2f;
		float xMax = 0.8f;
		float yMax = 0.8f;
		float zMax = 0.8f;

		switch (side) {
			case 0:
				yMin = 0.0f;
				yMax = 0.1f;
				break;

			case 1:
				yMin = 0.9f;
				yMax = 1.0f;
				break;

			case 2:
				zMin = 0.0f;
				zMax = 0.1f;
				break;

			case 3:
				zMin = 0.9f;
				zMax = 1.0f;
				break;

			case 4:
				xMin = 0.0f;
				xMax = 0.1f;
				break;

			case 5:
				xMin = 0.9f;
				xMax = 1.0f;
				break;
		}

		this.setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);
		MovingObjectPosition res = super.collisionRayTrace(world, x, y, z, par5Vec3D, par6Vec3D);
		if (res != null) res.subHit = side;

		return res;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3D par5Vec3D, Vec3D par6Vec3D) {
		TileEntityPipe te = this.getTileEntity(world, x, y, z);
		MovingObjectPosition res, best = null;

		float pipeMin = 0.5f - 0.15625f - 0.05f;
		float pipeMax = 0.5f + 0.15625f + 0.05f;

		if (te.hasPipe()) {
			this.setBlockBounds(pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
			best = super.collisionRayTrace(world, x, y, z, par5Vec3D, par6Vec3D);
		}

		for (int i = 0; i < 6; i++) {
			res = traceConnector(te, i, world, x, y, z, par5Vec3D, par6Vec3D);
			if (res != null && res.sideHit == i) return res;
			if (best == null) best = res;
		}

		return best;
	}
}
