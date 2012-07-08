package xabrain.mods.transport.client;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import xabrain.mods.transport.BlockPipe;
import xabrain.mods.transport.TileEntityPipe;

public class RenderPipe {
	public static boolean renderBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, BlockPipe block) {
		int type = block.getPipeType(world, x, y, z);
		float centerMin = 0.5f - (0.0625f * type) - 0.03125f;
		float centerMax = 0.5f + (0.0625f * type) + 0.03125f;

		/* Update the connections */
		block.setBlockBoundsBasedOnState(world, x, y, z);

		renderer.overrideBlockTexture = type - 1;
		if (type != 0) renderPipe(renderer, world, x, y, z, block, centerMin, centerMax);

		TileEntityPipe te = block.getTileEntity(world, x, y, z);
		if (te != null) {
			float sideMin = 0.2f;
			float sideMax = 0.8f;

			/* Draw the connectors */
			if (te.hasConnector(0)) {
				renderer.overrideBlockTexture = 0x10 + te.getConnector(0);
				block.setBlockBounds(sideMin, 0.0f, sideMin, sideMax, 0.1f, sideMax);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(1)) {
				renderer.overrideBlockTexture = 0x10 + te.getConnector(1);
				block.setBlockBounds(sideMin, 0.9f, sideMin, sideMax, 1.0f, sideMax);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(2)) {
				renderer.overrideBlockTexture = 0x10 + te.getConnector(2);
				block.setBlockBounds(sideMin, sideMin, 0.0f, sideMax, sideMax, 0.1f);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(3)) {
				renderer.overrideBlockTexture = 0x10 + te.getConnector(3);
				block.setBlockBounds(sideMin, sideMin, 0.9f, sideMax, sideMax, 1.0f);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(4)) {
				renderer.overrideBlockTexture = 0x10 + te.getConnector(4);
				block.setBlockBounds(0.0f, sideMin, sideMin, 0.1f, sideMax, sideMax);
				renderer.renderStandardBlock(block, x, y, z);
			}
			if (te.hasConnector(5)) {
				renderer.overrideBlockTexture = 0x10 + te.getConnector(5);
				block.setBlockBounds(0.9f, sideMin, sideMin, 1.0f, sideMax, sideMax);
				renderer.renderStandardBlock(block, x, y, z);
			}

			/* Draw the pipes from the center to the connectors */
			if (type != 0) {
				if (te.hasConnector(0)) {
					renderer.overrideBlockTexture = type - 1;
					block.setBlockBounds(centerMin, 0.1f, centerMin, centerMax, centerMin, centerMax);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if (te.hasConnector(1)) {
					renderer.overrideBlockTexture = type - 1;
					block.setBlockBounds(centerMin, centerMax, centerMin, centerMax, 0.9f, centerMax);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if (te.hasConnector(2)) {
					renderer.overrideBlockTexture = type - 1;
					block.setBlockBounds(centerMin, centerMin, 0.1f, centerMax, centerMax, centerMin);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if (te.hasConnector(3)) {
					renderer.overrideBlockTexture = type - 1;
					block.setBlockBounds(centerMin, centerMin, centerMax, centerMax, centerMax, 0.9f);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if (te.hasConnector(4)) {
					renderer.overrideBlockTexture = type - 1;
					block.setBlockBounds(0.1f, centerMin, centerMin, centerMin, centerMax, centerMax);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if (te.hasConnector(5)) {
					renderer.overrideBlockTexture = type - 1;
					block.setBlockBounds(centerMax, centerMin, centerMin, 0.9f, centerMax, centerMax);
					renderer.renderStandardBlock(block, x, y, z);
				}
			}
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
