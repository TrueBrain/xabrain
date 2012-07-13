package xabrain.mods.transport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import net.minecraft.src.forge.ITextureProvider;

public abstract class BlockPipe extends Block implements ITextureProvider {
	private Random random = new Random();

	public BlockPipe(int blockID) {
		super(blockID, 0, Material.glass);
		setBlockName("pipe");
		setRequiresSelfNotify();

		setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public String getTextureFile() {
		return "/xabrain/mods/transport/resources/terrain.png";
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 0;
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {}

	@Override
	public int getRenderType() {
		return mod_Transport.renderTypePipe;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {}

	@Override
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, ArrayList arraylist) {
		boolean hasPipe = this.hasPipe(world, x, y, z);
		float centerMin = 0.5f - (0.0625f * 2) - 0.03125f;
		float centerMax = 0.5f + (0.0625f * 2) + 0.03125f;

		TileEntityPipe te = getTileEntity(world, x, y, z);
		if (te != null) {
			/* Create collide boxes for the connectors */
			for (int i = 0; i < 6; i++) {
				if (!te.hasConnector(i)) continue;

				float[] dim = getConnectorSize(i, new float[6]);
				setBlockBounds(dim[0], dim[1], dim[2], dim[3], dim[4], dim[5]);
				super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
			}

			/* Create the collide boxes for the pipes to the connectors */
			if (hasPipe) {
				for (int i = 0; i < 6; i++) {
					if (!te.hasConnector(i)) continue;

					float[] dim = getPipeSize(i, new float[6]);
					setBlockBounds(dim[0], dim[1], dim[2], dim[3], dim[4], dim[5]);
					super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
				}
			}
		}

		if (!hasPipe) return;

		/* Center of pipe */
		setBlockBounds(centerMin, centerMin, centerMin, centerMax, centerMax, centerMax);
		super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);

		/* Possible sides of pipe */

		if (canConnectPipeTo(world, x - 1, y, z, 5, te)) {
			setBlockBounds(0.0f, centerMin, centerMin, centerMin, centerMax, centerMax);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (canConnectPipeTo(world, x + 1, y, z, 4, te)) {
			setBlockBounds(centerMax, centerMin, centerMin, 1.0f, centerMax, centerMax);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (canConnectPipeTo(world, x, y - 1, z, 1, te)) {
			setBlockBounds(centerMin, 0.0f, centerMin, centerMax, centerMin, centerMax);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (canConnectPipeTo(world, x, y + 1, z, 0, te)) {
			setBlockBounds(centerMin, centerMax, centerMin, centerMax, 1.0f, centerMax);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (canConnectPipeTo(world, x, y, z - 1, 3, te)) {
			setBlockBounds(centerMin, centerMin, 0.0f, centerMax, centerMax, centerMin);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (canConnectPipeTo(world, x, y, z + 1, 2, te)) {
			setBlockBounds(centerMin, centerMin, centerMax, centerMax, centerMax, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	public boolean canConnectPipeTo(IBlockAccess world, int x, int y, int z, int side, TileEntityPipe teFrom) {
		int blockID = world.getBlockId(x, y, z);

		if (!isPipe(blockID)) return false;
		BlockPipe block = (BlockPipe) Block.blocksList[blockID];

		if (!block.hasPipe(world, x, y, z)) return false;

		TileEntityPipe te = block.getTileEntity(world, x, y, z);
		if (te != null && te.hasConnector(side)) return false;
		if (teFrom != null && teFrom.hasConnector(side ^ 0x1)) return false;

		return true;
	}

	public float[] getDimensions(int side, float[] dim, float startMin, float startMax, float sideMin, float sideMax) {
		dim[0] = startMin;
		dim[1] = startMin;
		dim[2] = startMin;
		dim[3] = startMax;
		dim[4] = startMax;
		dim[5] = startMax;

		switch (side) {
			case 0:
				dim[1] = sideMin;
				dim[4] = sideMax;
				break;

			case 1:
				dim[1] = 1.0f - sideMax;
				dim[4] = 1.0f - sideMin;
				break;

			case 2:
				dim[2] = sideMin;
				dim[5] = sideMax;
				break;

			case 3:
				dim[2] = 1.0f - sideMax;
				dim[5] = 1.0f - sideMin;
				break;

			case 4:
				dim[0] = sideMin;
				dim[3] = sideMax;
				break;

			case 5:
				dim[0] = 1.0f - sideMax;
				dim[3] = 1.0f - sideMin;
				break;
		}

		return dim;
	}

	public float[] getPipeSize(int side, float[] dim) {
		float centerMin = 0.5f - 0.15625f;
		float centerMax = 0.5f + 0.15625f;

		return getDimensions(side, dim, centerMin, centerMax, 0.1f, centerMin);
	}

	public float[] getConnectorSize(int side, float[] dim) {
		return getDimensions(side, dim, 0.2f, 0.8f, 0.0f, 0.1f);
	}

	public boolean isPipe(int blockID) {
		if (blockID == mod_Transport.blockPipeSimple.blockID) return true;
		if (blockID == mod_Transport.blockPipeComplex.blockID) return true;
		return false;
	}

	public abstract boolean hasPipe(IBlockAccess world, int x, int y, int z);

	public abstract TileEntityPipe getTileEntity(IBlockAccess world, int x, int y, int z);

	@Override
	public abstract boolean hasTileEntity(int metadata);

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
		/*
		 * Don't remove the tile from the graph if we replaced it with another
		 * pipe
		 */
		if (isPipe(world.getBlockId(x, y, z))) return;

		Graph.getGraph(world).onPipeRemove(x, y, z);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityLiving) {
		if (mod_Transport.proxy.isRemote()) return;

		if (!hasPipe(world, x, y, z)) return;
		Graph.getGraph(world).onPipeAdd(x, y, z);
	}

	public int getPacketOrientation(World world, int x, int y, int z, int orientation, ItemStack itemStack) {
		LinkedList<Integer> directions = new LinkedList<Integer>();

		/* Check if we have connectors */
		TileEntityPipe te = getTileEntity(world, x, y, z);

		if (orientation != 5 && (canConnectPipeTo(world, x - 1, y, z, 5, te) || (te != null && te.acceptsItem(4, itemStack)))) directions.add(4);
		if (orientation != 4 && (canConnectPipeTo(world, x + 1, y, z, 4, te) || (te != null && te.acceptsItem(5, itemStack)))) directions.add(5);
		if (orientation != 1 && (canConnectPipeTo(world, x, y - 1, z, 1, te) || (te != null && te.acceptsItem(0, itemStack)))) directions.add(0);
		if (orientation != 0 && (canConnectPipeTo(world, x, y + 1, z, 0, te) || (te != null && te.acceptsItem(1, itemStack)))) directions.add(1);
		if (orientation != 3 && (canConnectPipeTo(world, x, y, z - 1, 3, te) || (te != null && te.acceptsItem(2, itemStack)))) directions.add(2);
		if (orientation != 2 && (canConnectPipeTo(world, x, y, z + 1, 2, te) || (te != null && te.acceptsItem(3, itemStack)))) directions.add(3);

		if (directions.size() == 0) {
			/* Drop the item, as we have nowhere to go */
			dropBlockAsItem_do(world, x, y, z, itemStack.copy());
			itemStack.stackSize = 0;
			return -1;
		}

		return directions.get(random.nextInt(directions.size()));
	}
}
