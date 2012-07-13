package xabrain.mods.transport.client;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import xabrain.mods.transport.BlockPipe;
import xabrain.mods.transport.TileEntityPipe;

public class RenderPipe {
	public static boolean renderBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, BlockPipe block) {
		boolean hasPipe = block.hasPipe(world, x, y, z);
		int textureId = world.getBlockMetadata(x, y, z);

		TileEntityPipe te = block.getTileEntity(world, x, y, z);

		/* Draw the connecting pipes */
		renderer.overrideBlockTexture = textureId;
		if (hasPipe) renderPipe(renderer, world, x, y, z, block, te);
		renderer.overrideBlockTexture = -1;

		if (te == null) return true;

		/* Draw the connectors */
		for (int i = 0; i < 6; i++) {
			if (!te.hasConnector(i)) continue;

			renderer.overrideBlockTexture = te.getConnector(i).getTextureID();

			float[] dim = block.getConnectorSize(i, new float[6]);
			block.setBlockBounds(dim[0], dim[1], dim[2], dim[3], dim[4], dim[5]);
			renderer.renderStandardBlock(block, x, y, z);
		}

		/* Draw the pipes from the center to the connectors */
		renderer.overrideBlockTexture = textureId;
		if (hasPipe) {
			for (int i = 0; i < 6; i++) {
				if (!te.hasConnector(i)) continue;

				float[] dim = block.getPipeSize(i, new float[6]);
				block.setBlockBounds(dim[0], dim[1], dim[2], dim[3], dim[4], dim[5]);
				renderer.renderStandardBlock(block, x, y, z);
			}
		}

		renderer.overrideBlockTexture = -1;
		return true;
	}

	public static void renderPipe(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, BlockPipe block, TileEntityPipe te) {
		float centerMin = 0.5f - 0.15625f;
		float centerMax = 0.5f + 0.15625f;

		/* Center of pipe */
		block.setBlockBounds(centerMin, centerMin, centerMin, centerMax, centerMax, centerMax);
		renderer.renderStandardBlock(block, x, y, z);

		/* Possible sides of pipe */

		if (block.canConnectPipeTo(world, x - 1, y, z, 5, te)) {
			block.setBlockBounds(0.0f, centerMin, centerMin, centerMin, centerMax, centerMax);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.canConnectPipeTo(world, x + 1, y, z, 4, te)) {
			block.setBlockBounds(centerMax, centerMin, centerMin, 1.0f, centerMax, centerMax);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.canConnectPipeTo(world, x, y - 1, z, 1, te)) {
			block.setBlockBounds(centerMin, 0.0f, centerMin, centerMax, centerMin, centerMax);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.canConnectPipeTo(world, x, y + 1, z, 0, te)) {
			block.setBlockBounds(centerMin, centerMax, centerMin, centerMax, 1.0f, centerMax);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.canConnectPipeTo(world, x, y, z - 1, 3, te)) {
			block.setBlockBounds(centerMin, centerMin, 0.0f, centerMax, centerMax, centerMin);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.canConnectPipeTo(world, x, y, z + 1, 2, te)) {
			block.setBlockBounds(centerMin, centerMin, centerMax, centerMax, centerMax, 1.0f);
			renderer.renderStandardBlock(block, x, y, z);
		}
	}
}
