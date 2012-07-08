package xabrain.mods.transport;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.forge.ITextureProvider;

public class BlockPipe extends Block implements ITextureProvider {
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
		return (metadata & 3) - 1;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return (meta & 7) == 0 ? 0 : 1;
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {
		for (int i = 0; i < mod_Transport.pipeNames.length; i++) {
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
		int type = this.getPipeType(world, x, y, z);
		float centerMin = 0.5f - (0.0625f * 2) - 0.03125f;
		float centerMax = 0.5f + (0.0625f * 2) + 0.03125f;

		TileEntityPipe te = getTileEntity(world, x, y, z);

		connectedNorth = false;
		connectedSouth = false;
		connectedTop = false;
		connectedBottom = false;
		connectedEast = false;
		connectedWest = false;

		if (type != 0) {
			connectedNorth = canConnectPipeTo(world, x - 1, y, z, 5) && (te == null || !te.hasConnector(4));
			connectedSouth = canConnectPipeTo(world, x + 1, y, z, 4) && (te == null || !te.hasConnector(5));
			connectedTop = canConnectPipeTo(world, x, y - 1, z, 1) && (te == null || !te.hasConnector(0));
			connectedBottom = canConnectPipeTo(world, x, y + 1, z, 0) && (te == null || !te.hasConnector(1));
			connectedEast = canConnectPipeTo(world, x, y, z - 1, 3) && (te == null || !te.hasConnector(2));
			connectedWest = canConnectPipeTo(world, x, y, z + 1, 2) && (te == null || !te.hasConnector(3));
		}

		if (type != 0) {
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
		int type = this.getPipeType(world, x, y, z);
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
			if (type != 0) {
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

		if (type == 0) return;

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

	public boolean canConnectPipeTo(IBlockAccess world, int x, int y, int z, int side) {
		int blockID = world.getBlockId(x, y, z);
		if (this.blockID != blockID) return false;
		if (getPipeType(world, x, y, z) == 0) return false;

		TileEntityPipe te = getTileEntity(world, x, y, z);
		if (te != null && te.hasConnector(side)) return false;

		return true;
	}

	public int getPipeType(IBlockAccess world, int x, int y, int z) {
		/* The high bit is used to indicate we have a TE attached */
		int meta = world.getBlockMetadata(x, y, z);
		return meta & 7;
	}

	public void setPipeType(World world, int x, int y, int z, int type) {
		/* Reset the type, but don't touch the high bit */
		int meta = world.getBlockMetadata(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, (type & 7) | (meta & 8));
	}

	public boolean placeConnector(World world, int x, int y, int z, int side, byte type) {
		/* Set the high bit if needed */
		int meta = world.getBlockMetadata(x, y, z);
		if (!this.hasTileEntity(meta)) world.setBlockMetadataWithNotify(x, y, z, meta | 8);

		/* Get the TE */
		TileEntityPipe te = getTileEntity(world, x, y, z);

		/* Set the connector */
		if (te.hasConnector(side)) return false;
		te.setConnector(side, type);
		world.notifyBlockChange(x, y, z, this.blockID);

		return true;
	}

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
		TileEntityPipe te = getTileEntity(world, x, y, z);
		if (te != null) {
			for (int i = 0; i < 6; i++) {
				if (!te.hasConnector(i)) continue;

				this.dropBlockAsItem_do(world, x, y, z, new ItemStack(mod_Transport.itemConnector.shiftedIndex, 1, mod_Transport.itemConnector.damageDropped(te.getConnector(i).type)));
			}
		}
	}

	public TileEntityPipe getTileEntity(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (!hasTileEntity(meta)) return null;
		return (TileEntityPipe) world.getBlockTileEntity(x, y, z);
	}

	public boolean hasTileEntity(int metadata) {
		return (metadata & 8) != 0;
	}

	public TileEntity getTileEntity(int metadata) {
		return new TileEntityPipe();
	}
}
