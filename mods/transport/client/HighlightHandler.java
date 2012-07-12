package xabrain.mods.transport.client;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.Tessellator;
import net.minecraft.src.World;
import net.minecraft.src.forge.IHighlightHandler;

import org.lwjgl.opengl.GL11;

import xabrain.mods.transport.mod_Transport;

public class HighlightHandler implements IHighlightHandler {
	@Override
	public boolean onBlockHighlight(RenderGlobal render, EntityPlayer player, MovingObjectPosition target, int i, ItemStack currentItem, float partialTicks) {
		if (i != 0) return false;
		if (target.typeOfHit != EnumMovingObjectType.TILE) return false;

		World world = mod_Transport.proxy.getCurrentWorld();
		int blockId = world.getBlockId(target.blockX, target.blockY, target.blockZ);
		if (!mod_Transport.blockPipe.isPipe(blockId)) return false;
		if (blockId == mod_Transport.blockPipe.blockID) return false;

		render.drawBlockBreaking(player, target, i, currentItem, partialTicks);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);

		float sideMin = 0.2f;
		float sideMax = 0.8f;

		double x = target.blockX - player.lastTickPosX - (player.posX - player.lastTickPosX) * partialTicks;
		double y = target.blockY - player.lastTickPosY - (player.posY - player.lastTickPosY) * partialTicks;
		double z = target.blockZ - player.lastTickPosZ - (player.posZ - player.lastTickPosZ) * partialTicks;

		switch (target.subHit) {
			case 0:
				this.drawBox(x, y, z, sideMin, 0.0f, sideMin, sideMax, 0.1f, sideMax);
				break;

			case 1:
				this.drawBox(x, y, z, sideMin, 0.9f, sideMin, sideMax, 1.0f, sideMax);
				break;

			case 2:
				this.drawBox(x, y, z, sideMin, sideMin, 0.0f, sideMax, sideMax, 0.1f);
				break;

			case 3:
				this.drawBox(x, y, z, sideMin, sideMin, 0.9f, sideMax, sideMax, 1.0f);
				break;

			case 4:
				this.drawBox(x, y, z, 0.0f, sideMin, sideMin, 0.1f, sideMax, sideMax);
				break;

			case 5:
				this.drawBox(x, y, z, 0.9f, sideMin, sideMin, 1.0f, sideMax, sideMax);
				break;
		}

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);

		return true;
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
