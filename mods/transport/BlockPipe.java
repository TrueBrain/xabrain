package xabrain.mods.transport;

import java.util.ArrayList;

import net.minecraft.src.*;
import net.minecraft.src.forge.*;

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
	}

	@Override
	public String getTextureFile() {
		return "/xabrain/mods/transport/sprites/terrain.png";
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		return metadata;
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public void addCreativeItems(ArrayList itemList)
	{	   
		for (int i = 0; i < mod_Transport.pipeNames.length; i++)
		{
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
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
		double centerMin = 0.4f;
		double centerMax = 0.6f;

		minX = centerMin;
		minY = centerMin;
		minZ = centerMin;
		maxX = centerMax;
		maxY = centerMax;
		maxZ = centerMax;

		connectedNorth  = canConnectPipeTo(world, x - 1, y, z);
		connectedSouth  = canConnectPipeTo(world, x + 1, y, z);
		connectedTop    = canConnectPipeTo(world, x, y - 1, z);
		connectedBottom = canConnectPipeTo(world, x, y + 1, z);
		connectedEast   = canConnectPipeTo(world, x, y, z - 1);
		connectedWest   = canConnectPipeTo(world, x, y, z + 1);

		if (connectedNorth)  minX = 0.0f;
		if (connectedSouth)  maxX = 1.0f;
		if (connectedTop)    minY = 0.0f;
		if (connectedBottom) maxY = 1.0f;
		if (connectedEast)   minZ = 0.0f;
		if (connectedWest)   maxZ = 1.0f;

		this.setBlockBounds((float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ);
    }

	@Override
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, ArrayList arraylist) {
		float centerMin = 0.4f;
		float centerMax = 0.6f;

		/* Update the connections */
		setBlockBoundsBasedOnState(world, x, y, z);

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

		this.setBlockBounds((float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ);
	}


	public boolean canConnectPipeTo(IBlockAccess world, int x, int y, int z) {
		int blockID = world.getBlockId(x, y, z);
		return (this.blockID == blockID);
	}
}
