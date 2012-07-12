package xabrain.mods.transport.client;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import xabrain.mods.transport.BlockPipe;
import xabrain.mods.transport.TileEntityPipe;

public class RenderPipe {
	public static boolean renderBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, BlockPipe block) {
		boolean hasPipe = block.hasPipe(world, x, y, z);
		float centerMin = 0.5f - (0.0625f * 2) - 0.03125f;
		float centerMax = 0.5f + (0.0625f * 2) + 0.03125f;
		int textureId = world.getBlockMetadata(x, y, z);

		/* Update the connections */
		block.setBlockBoundsBasedOnState(world, x, y, z);
		TileEntityPipe te = block.getTileEntity(world, x, y, z);

		/* Draw the connecting pipes */
		renderer.overrideBlockTexture = textureId;
		if (hasPipe) renderPipe(renderer, world, x, y, z, block, centerMin, centerMax, te);
		renderer.overrideBlockTexture = -1;

		if (te == null) return true;

		float sideMin = 0.2f;
		float sideMax = 0.8f;

		/* Draw the connectors */
		if (te.hasConnector(0)) {
			renderer.overrideBlockTexture = te.getConnector(0).getTextureID();
			block.setBlockBounds(sideMin, 0.0f, sideMin, sideMax, 0.1f, sideMax);
			renderer.renderStandardBlock(block, x, y, z);
		}
		if (te.hasConnector(1)) {
			renderer.overrideBlockTexture = te.getConnector(1).getTextureID();
			block.setBlockBounds(sideMin, 0.9f, sideMin, sideMax, 1.0f, sideMax);
			renderer.renderStandardBlock(block, x, y, z);
		}
		if (te.hasConnector(2)) {
			renderer.overrideBlockTexture = te.getConnector(2).getTextureID();
			block.setBlockBounds(sideMin, sideMin, 0.0f, sideMax, sideMax, 0.1f);
			renderer.renderStandardBlock(block, x, y, z);
		}
		if (te.hasConnector(3)) {
			renderer.overrideBlockTexture = te.getConnector(3).getTextureID();
			block.setBlockBounds(sideMin, sideMin, 0.9f, sideMax, sideMax, 1.0f);
			renderer.renderStandardBlock(block, x, y, z);
		}
		if (te.hasConnector(4)) {
			renderer.overrideBlockTexture = te.getConnector(4).getTextureID();
			block.setBlockBounds(0.0f, sideMin, sideMin, 0.1f, sideMax, sideMax);
			renderer.renderStandardBlock(block, x, y, z);
		}
		if (te.hasConnector(5)) {
			renderer.overrideBlockTexture = te.getConnector(5).getTextureID();
			block.setBlockBounds(0.9f, sideMin, sideMin, 1.0f, sideMax, sideMax);
			renderer.renderStandardBlock(block, x, y, z);
		}

		/* Draw the pipes from the center to the connectors */
		renderer.overrideBlockTexture = textureId;
		if (hasPipe) {
			if (te.hasConnector(0)) {
				block.setBlockBounds(centerMin, 0.1f, centerMin, centerMax, centerMin, centerMax);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(1)) {
				block.setBlockBounds(centerMin, centerMax, centerMin, centerMax, 0.9f, centerMax);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(2)) {
				block.setBlockBounds(centerMin, centerMin, 0.1f, centerMax, centerMax, centerMin);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(3)) {
				block.setBlockBounds(centerMin, centerMin, centerMax, centerMax, centerMax, 0.9f);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(4)) {
				block.setBlockBounds(0.1f, centerMin, centerMin, centerMin, centerMax, centerMax);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(5)) {
				block.setBlockBounds(centerMax, centerMin, centerMin, 0.9f, centerMax, centerMax);
				renderer.renderStandardBlock(block, x, y, z);
			}
		}

		renderer.overrideBlockTexture = -1;
		return true;
	}

	public static void renderPipe(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, BlockPipe block, float centerMin, float centerMax, TileEntityPipe te) {
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
