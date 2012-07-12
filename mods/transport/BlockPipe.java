package xabrain.mods.transport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import net.minecraft.src.forge.ITextureProvider;

public class BlockPipe extends Block implements ITextureProvider {
	private Random random = new Random();

	public boolean connectedNorth = false;
	public boolean connectedSouth = false;
	public boolean connectedEast = false;
	public boolean connectedWest = false;
	public boolean connectedTop = false;
	public boolean connectedBottom = false;

	public BlockPipe(int blockID) {
		super(blockID, 0, Material.glass);
		setBlockName("pipe");
		setRequiresSelfNotify();
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
		return 1;
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {
		for (int i = 0; i < ItemDye.dyeColorNames.length; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

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
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		boolean hasPipe = this.hasPipe(world, x, y, z);
		float centerMin = 0.5f - (0.0625f * 2) - 0.03125f;
		float centerMax = 0.5f + (0.0625f * 2) + 0.03125f;

		TileEntityPipe te = getTileEntity(world, x, y, z);

		connectedNorth = false;
		connectedSouth = false;
		connectedTop = false;
		connectedBottom = false;
		connectedEast = false;
		connectedWest = false;

		if (hasPipe) {
			connectedNorth = canConnectPipeTo(world, x - 1, y, z, 5, te);
			connectedSouth = canConnectPipeTo(world, x + 1, y, z, 4, te);
			connectedTop = canConnectPipeTo(world, x, y - 1, z, 1, te);
			connectedBottom = canConnectPipeTo(world, x, y + 1, z, 0, te);
			connectedEast = canConnectPipeTo(world, x, y, z - 1, 3, te);
			connectedWest = canConnectPipeTo(world, x, y, z + 1, 2, te);
		}

		if (hasPipe) {
			minX = centerMin;
			minY = centerMin;
			minZ = centerMin;
			maxX = centerMax;
			maxY = centerMax;
			maxZ = centerMax;
		} else {
			minX = 1.0f;
			minY = 1.0f;
			minZ = 1.0f;
			maxX = 0.0f;
			maxY = 0.0f;
			maxZ = 0.0f;
		}

		if (te != null) {
			if (te.hasConnector(4)) {
				minX = 0.0f;
				maxX = Math.max(maxX, 0.1f);

				minY = Math.min(minY, 0.2f);
				minZ = Math.min(minZ, 0.2f);
				maxY = Math.max(maxY, 0.8f);
				maxZ = Math.max(maxZ, 0.8f);
			}
			if (te.hasConnector(0)) {
				minY = 0.0f;
				maxY = Math.max(maxY, 0.1f);

				minX = Math.min(minX, 0.2f);
				minZ = Math.min(minZ, 0.2f);
				maxX = Math.max(maxX, 0.8f);
				maxZ = Math.max(maxZ, 0.8f);
			}
			if (te.hasConnector(2)) {
				minZ = 0.0f;
				maxZ = Math.max(maxZ, 0.1f);

				minX = Math.min(minX, 0.2f);
				minY = Math.min(minY, 0.2f);
				maxX = Math.max(maxX, 0.8f);
				maxY = Math.max(maxY, 0.8f);
			}

			if (te.hasConnector(5)) {
				minX = Math.min(minX, 0.9f);
				maxX = 1.0f;

				minY = Math.min(minY, 0.2f);
				minZ = Math.min(minZ, 0.2f);
				maxY = Math.max(maxY, 0.8f);
				maxZ = Math.max(maxZ, 0.8f);
			}
			if (te.hasConnector(1)) {
				minY = Math.min(minY, 0.9f);
				maxY = 1.0f;

				minX = Math.min(minX, 0.2f);
				minZ = Math.min(minZ, 0.2f);
				maxX = Math.max(maxX, 0.8f);
				maxZ = Math.max(maxZ, 0.8f);
			}
			if (te.hasConnector(3)) {
				minZ = Math.min(minZ, 0.9f);
				maxZ = 1.0f;

				minX = Math.min(minX, 0.2f);
				minY = Math.min(minY, 0.2f);
				maxX = Math.max(maxX, 0.8f);
				maxY = Math.max(maxY, 0.8f);
			}
		}

		if (connectedNorth) minX = 0.0f;
		if (connectedSouth) maxX = 1.0f;
		if (connectedTop) minY = 0.0f;
		if (connectedBottom) maxY = 1.0f;
		if (connectedEast) minZ = 0.0f;
		if (connectedWest) maxZ = 1.0f;
	}

	@Override
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, ArrayList arraylist) {
		boolean hasPipe = this.hasPipe(world, x, y, z);
		float centerMin = 0.5f - (0.0625f * 2) - 0.03125f;
		float centerMax = 0.5f + (0.0625f * 2) + 0.03125f;

		/* Update the connections */
		setBlockBoundsBasedOnState(world, x, y, z);

		TileEntityPipe te = getTileEntity(world, x, y, z);
		if (te != null) {
			float sideMin = 0.2f;
			float sideMax = 0.8f;

			/* Draw the connectors */
			if (te.hasConnector(0)) {
				setBlockBounds(sideMin, 0.0f, sideMin, sideMax, 0.1f, sideMax);
				super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
			}
			if (te.hasConnector(1)) {
				setBlockBounds(sideMin, 0.9f, sideMin, sideMax, 1.0f, sideMax);
				super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
			}
			if (te.hasConnector(2)) {
				setBlockBounds(sideMin, sideMin, 0.0f, sideMax, sideMax, 0.1f);
				super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
			}
			if (te.hasConnector(3)) {
				setBlockBounds(sideMin, sideMin, 0.9f, sideMax, sideMax, 1.0f);
				super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
			}
			if (te.hasConnector(4)) {
				setBlockBounds(0.0f, sideMin, sideMin, 0.1f, sideMax, sideMax);
				super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
			}
			if (te.hasConnector(5)) {
				setBlockBounds(0.9f, sideMin, sideMin, 1.0f, sideMax, sideMax);
				super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
			}

			/* Draw the pipes from the center to the connectors */
			if (hasPipe) {
				if (te.hasConnector(0)) {
					setBlockBounds(centerMin, 0.1f, centerMin, centerMax, centerMin, centerMax);
					super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
				}
				if (te.hasConnector(1)) {
					setBlockBounds(centerMin, centerMax, centerMin, centerMax, 0.9f, centerMax);
					super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
				}
				if (te.hasConnector(2)) {
					setBlockBounds(centerMin, centerMin, 0.1f, centerMax, centerMax, centerMin);
					super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
				}
				if (te.hasConnector(3)) {
					setBlockBounds(centerMin, centerMin, centerMax, centerMax, centerMax, 0.9f);
					super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
				}
				if (te.hasConnector(4)) {
					setBlockBounds(0.1f, centerMin, centerMin, centerMin, centerMax, centerMax);
					super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
				}
				if (te.hasConnector(5)) {
					setBlockBounds(centerMax, centerMin, centerMin, 0.9f, centerMax, centerMax);
					super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
				}
			}
		}

		if (!hasPipe) return;

		/* Center of pipe */
		setBlockBounds(centerMin, centerMin, centerMin, centerMax, centerMax, centerMax);
		super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);

		/* Possible sides of pipe */

		if (connectedNorth) {
			setBlockBounds(0.0f, centerMin, centerMin, centerMin, centerMax, centerMax);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (connectedSouth) {
			setBlockBounds(centerMax, centerMin, centerMin, 1.0f, centerMax, centerMax);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (connectedTop) {
			setBlockBounds(centerMin, 0.0f, centerMin, centerMax, centerMin, centerMax);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (connectedBottom) {
			setBlockBounds(centerMin, centerMax, centerMin, centerMax, 1.0f, centerMax);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (connectedEast) {
			setBlockBounds(centerMin, centerMin, 0.0f, centerMax, centerMax, centerMin);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		if (connectedWest) {
			setBlockBounds(centerMin, centerMin, centerMax, centerMax, centerMax, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, axisalignedbb, arraylist);
		}

		this.setBlockBounds((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ);
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

	public boolean isPipe(int blockID) {
		if (blockID == mod_Transport.blockPipe.blockID) return true;
		if (blockID == mod_Transport.blockPipeComplex.blockID) return true;
		return false;
	}

	public boolean hasPipe(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	public TileEntityPipe getTileEntity(IBlockAccess world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return false;
	}

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
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
