package xabrain.mods.transport;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Vec3D;
import net.minecraft.src.World;

public class BlockPipeSimple extends BlockPipe {
	public BlockPipeSimple(int blockID) {
		super(blockID);
	}

	@Override
	public void addCreativeItems(ArrayList itemList) {
		for (int i = 0; i < ItemDye.dyeColorNames.length; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 1;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3D par5Vec3D, Vec3D par6Vec3D) {
		float pipeMin = 0.5f - 0.15625f - 0.05f;
		float pipeMax = 0.5f + 0.15625f + 0.05f;

		this.setBlockBounds(pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
		return super.collisionRayTrace(world, x, y, z, par5Vec3D, par6Vec3D);
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
}
