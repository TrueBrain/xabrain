package xabrain.mods.transport.client;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.Tessellator;
import net.minecraft.src.World;
import net.minecraft.src.forge.ForgeHooksClient;
import net.minecraft.src.forge.IHighlightHandler;

import org.lwjgl.opengl.GL11;

import xabrain.mods.transport.BlockPipe;
import xabrain.mods.transport.TileEntityPipe;
import xabrain.mods.transport.mod_Transport;

public class HighlightHandler implements IHighlightHandler {
	@Override
	public boolean onBlockHighlight(RenderGlobal render, EntityPlayer player, MovingObjectPosition target, int i, ItemStack currentItem, float partialTicks) {
		if (i != 0) return false;
		if (target.typeOfHit != EnumMovingObjectType.TILE) return false;

		World world = mod_Transport.proxy.getCurrentWorld();
		int blockID = world.getBlockId(target.blockX, target.blockY, target.blockZ);

		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		boolean res = false;

		if (mod_Transport.blockPipeSimple.isPipe(blockID)) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);

			drawBox(target.subHit, target.blockX - x, target.blockY - y, target.blockZ - z);

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);

			res = true;
		}

		if (currentItem != null && target.subHit == -1 && currentItem.itemID == mod_Transport.itemConnector.shiftedIndex) {
			if (mod_Transport.blockPipeSimple.isPipe(blockID)) {

			} else if (blockID == Block.snow.blockID || blockID == Block.vine.blockID || blockID == Block.tallGrass.blockID || blockID == Block.deadBush.blockID
					|| (Block.blocksList[blockID] != null && Block.blocksList[blockID].isBlockReplaceable(world, target.blockX, target.blockY, target.blockZ))) {
				target.sideHit = 0;
			} else {
				if (target.sideHit == 0) --target.blockY;
				if (target.sideHit == 1) ++target.blockY;
				if (target.sideHit == 2) --target.blockZ;
				if (target.sideHit == 3) ++target.blockZ;
				if (target.sideHit == 4) --target.blockX;
				if (target.sideHit == 5) ++target.blockX;

				target.sideHit ^= 0x1;
			}

			blockID = world.getBlockId(target.blockX, target.blockY, target.blockZ);
			if (mod_Transport.blockPipeSimple.isPipe(blockID)) {
				BlockPipe block = (BlockPipe) Block.blocksList[blockID];
				TileEntityPipe te = block.getTileEntity(world, target.blockX, target.blockY, target.blockZ);
				if (te != null && te.hasConnector(target.sideHit)) return false;
			}

			drawConnector(target.sideHit, render, target, currentItem, x, y, z);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			drawBox(target.sideHit, target.blockX - x, target.blockY - y, target.blockZ - z);

			return res;
		}

		return res;
	}

	private void drawConnector(int side, RenderGlobal render, MovingObjectPosition target, ItemStack currentItem, double x, double y, double z) {
		BlockPipe block = mod_Transport.blockPipeComplex;

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.2F);

		ForgeHooksClient.beforeRenderPass(0);

		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setTranslation(-x, -y, -z);

		ForgeHooksClient.beforeBlockRender(block, render.globalRenderBlocks);

		float[] dim = block.getConnectorSize(target.sideHit, new float[6]);
		block.setBlockBounds(dim[0], dim[1], dim[2], dim[3], dim[4], dim[5]);

		render.globalRenderBlocks.overrideBlockTexture = 0x10 + currentItem.getItemDamage();
		render.globalRenderBlocks.renderStandardBlock(block, target.blockX, target.blockY, target.blockZ);
		render.globalRenderBlocks.overrideBlockTexture = -1;

		ForgeHooksClient.afterBlockRender(block, render.globalRenderBlocks);
		ForgeHooksClient.afterRenderPass(0);

		Tessellator.instance.draw();
		Tessellator.instance.setTranslation(0.0D, 0.0D, 0.0D);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	private void drawBox(int side, double x, double y, double z) {
		float[] dim = mod_Transport.blockPipeSimple.getConnectorSize(side, new float[6]);

		float pipeMin = 0.5f - 0.15625f - 0.1f;
		float pipeMax = 0.5f + 0.15625f + 0.1f;

		switch (side) {
			case -1:
				this.drawBox(x, y, z, pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
				break;

			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				this.drawBox(x, y, z, dim[0], dim[1], dim[2], dim[3], dim[4], dim[5]);
				break;
		}

	}

	private void drawBox(double x, double y, double z, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		float delta = 0.002f;

		xMin += x - delta;
		yMin += y - delta;
		zMin += z - delta;
		xMax += x + delta;
		yMax += y + delta;
		zMax += z + delta;

		Tessellator tesselator = Tessellator.instance;
		tesselator.startDrawing(3);
		tesselator.addVertex(xMin, yMin, zMin);
		tesselator.addVertex(xMax, yMin, zMin);
		tesselator.addVertex(xMax, yMin, zMax);
		tesselator.addVertex(xMin, yMin, zMax);
		tesselator.addVertex(xMin, yMin, zMin);
		tesselator.draw();
		tesselator.startDrawing(3);
		tesselator.addVertex(xMin, yMax, zMin);
		tesselator.addVertex(xMax, yMax, zMin);
		tesselator.addVertex(xMax, yMax, zMax);
		tesselator.addVertex(xMin, yMax, zMax);
		tesselator.addVertex(xMin, yMax, zMin);
		tesselator.draw();
		tesselator.startDrawing(1);
		tesselator.addVertex(xMin, yMin, zMin);
		tesselator.addVertex(xMin, yMax, zMin);
		tesselator.addVertex(xMax, yMin, zMin);
		tesselator.addVertex(xMax, yMax, zMin);
		tesselator.addVertex(xMax, yMin, zMax);
		tesselator.addVertex(xMax, yMax, zMax);
		tesselator.addVertex(xMin, yMin, zMax);
		tesselator.addVertex(xMin, yMax, zMax);
		tesselator.draw();
	}
}
