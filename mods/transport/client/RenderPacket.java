package xabrain.mods.transport.client;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderItem;
import net.minecraft.src.Tessellator;
import net.minecraft.src.forge.ForgeHooksClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import xabrain.mods.transport.EntityPacket;

public class RenderPacket extends RenderItem {
	private RenderBlocks renderBlocks = new RenderBlocks();
	private Random random = new Random();

	public void doRenderItem(EntityPacket entityItem, double xCoord, double yCoord, double zCoord, float yaw, float partialTickTime) {
		this.random.setSeed(187L);

		ItemStack is = entityItem.item;

		byte amount = 1;
		if (is.stackSize > 1) amount = 2;
		if (is.stackSize > 5) amount = 3;
		if (is.stackSize > 20) amount = 4;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) xCoord, (float) yCoord + entityItem.yOffset, (float) zCoord);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);

		if (ForgeHooksClient.renderEntityItem(entityItem, is, 0.0f, 0.0f, random, renderManager.renderEngine, renderBlocks)) {
			;
		} else if (is.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.blocksList[is.itemID].getRenderType())) {
			float scale = entityItem.width;
			GL11.glScalef(scale, scale, scale);

			this.loadTexture(Block.blocksList[is.itemID].getTextureFile());

			for (int i = 0; i < amount; i++) {
				GL11.glPushMatrix();

				if (i > 0) {
					float x = (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f;
					float y = (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f;
					float z = (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f;
					GL11.glTranslatef(x, y, z);
				}

				this.renderBlocks.renderBlockAsItem(Block.blocksList[is.itemID], is.getItemDamage(), 1.0f);
				GL11.glPopMatrix();
			}
		} else if (is.getItem().requiresMultipleRenderPasses()) {
			float scale = entityItem.width;
			GL11.glScalef(scale, scale, scale);

			this.loadTexture(Item.itemsList[is.itemID].getTextureFile());

			for (int i = 0; i < is.getItem().getRenderPasses(is.getItemDamage()); i++) {
				this.random.setSeed(187L);

				int textureID = is.getItem().func_46057_a(is.getItemDamage(), i);

				if (this.field_27004_a) {
					int color = Item.itemsList[is.itemID].getColorFromDamage(is.getItemDamage(), i);
					float r = (float) (color >> 16 & 255) / 255.0F;
					float g = (float) (color >> 8 & 255) / 255.0F;
					float b = (float) (color & 255) / 255.0F;
					GL11.glColor4f(r, g, b, 1.0F);
				}

				this.renderAsBlock(textureID, amount);
			}
		} else {
			float scale = entityItem.width;
			GL11.glScalef(scale, scale, scale);

			this.loadTexture(is.getItem().getTextureFile());

			if (this.field_27004_a) {
				int color = Item.itemsList[is.itemID].getColorFromDamage(is.getItemDamage(), 0);
				float r = (float) (color >> 16 & 255) / 255.0F;
				float g = (float) (color >> 8 & 255) / 255.0F;
				float b = (float) (color & 255) / 255.0F;
				GL11.glColor4f(r, g, b, 1.0F);
			}

			this.renderAsBlock(is.getIconIndex(), amount);
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void renderAsBlock(int textureID, int amount) {
		Tessellator tes = Tessellator.instance;
		float left = (float) (textureID % 16 * 16 + 0) / 256.0F;
		float right = (float) (textureID % 16 * 16 + 16) / 256.0F;
		float top = (float) (textureID / 16 * 16 + 0) / 256.0F;
		float bottom = (float) (textureID / 16 * 16 + 16) / 256.0F;
		float var8 = 1.0F;
		float var9 = 0.5F;
		float var10 = 0.25F;

		for (int i = 0; i < amount; i++) {
			GL11.glPushMatrix();

			if (i > 0) {
				float x = (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f;
				float y = (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f;
				float z = (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f;
				GL11.glTranslatef(x, y, z);
			}

			GL11.glRotatef(180.0f - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
			tes.startDrawingQuads();
			tes.setNormal(0.0f, 1.0f, 0.0f);
			tes.addVertexWithUV((double) (0.0f - var9), (double) (0.0f - var10), 0.0f, (double) left, (double) bottom);
			tes.addVertexWithUV((double) (var8 - var9), (double) (0.0f - var10), 0.0f, (double) right, (double) bottom);
			tes.addVertexWithUV((double) (var8 - var9), (double) (1.0f - var10), 0.0f, (double) right, (double) top);
			tes.addVertexWithUV((double) (0.0f - var9), (double) (1.0f - var10), 0.0f, (double) left, (double) top);
			tes.draw();
			GL11.glPopMatrix();
		}
	}

	@Override
	public void doRenderShadowAndFire(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {}

	@Override
	public void doRender(Entity entity, double xCoord, double yCoord, double zCoord, float yaw, float partialTickTime) {
		doRenderItem((EntityPacket) entity, xCoord, yCoord, zCoord, yaw, partialTickTime);
	}
}
