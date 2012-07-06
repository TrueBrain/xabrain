package xabrain.mods.transport.client;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import xabrain.mods.transport.BlockPipe;

public class RenderPipe {
	public static boolean renderBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, BlockPipe block) {
		int type = world.getBlockMetadata(x, y, z);
		float centerMin = 0.5f - (0.0625f * type) - 0.03125f;
		float centerMax = 0.5f + (0.0625f * type) + 0.03125f;

		/* Update the connections */
		block.setBlockBoundsBasedOnState(world, x, y, z);

		renderer.overrideBlockTexture = type - 1;
		if (type != 0) renderPipe(renderer, world, x, y, z, block, centerMin, centerMax);

		if (type == 0) {
			renderer.overrideBlockTexture = 0x10;
			block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.1f);
			renderer.renderStandardBlock(block, x, y, z);
		}

		renderer.overrideBlockTexture = -1;
		return true;
	}

	public static void renderPipe(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, BlockPipe block, float centerMin, float centerMax) {
		/* Center of pipe */
		block.setBlockBounds(centerMin, centerMin, centerMin, centerMax, centerMax, centerMax);
		renderer.renderStandardBlock(block, x, y, z);

		/* Possible sides of pipe */

		if (block.connectedNorth) {
			block.setBlockBounds(0.0f, centerMin, centerMin, centerMin, centerMax, centerMax);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.connectedSouth) {
			block.setBlockBounds(centerMax, centerMin, centerMin, 1.0f, centerMax, centerMax);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.connectedTop) {
			block.setBlockBounds(centerMin, 0.0f, centerMin, centerMax, centerMin, centerMax);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.connectedBottom) {
			block.setBlockBounds(centerMin, centerMax, centerMin, centerMax, 1.0f, centerMax);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.connectedEast) {
			block.setBlockBounds(centerMin, centerMin, 0.0f, centerMax, centerMax, centerMin);
			renderer.renderStandardBlock(block, x, y, z);
		}

		if (block.connectedWest) {
			block.setBlockBounds(centerMin, centerMin, centerMax, centerMax, centerMax, 1.0f);
			renderer.renderStandardBlock(block, x, y, z);
		}
	}
}
