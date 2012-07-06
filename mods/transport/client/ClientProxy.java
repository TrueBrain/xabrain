package xabrain.mods.transport.client;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;
import net.minecraft.src.forge.MinecraftForgeClient;
import xabrain.mods.transport.BlockPipe;
import xabrain.mods.transport.IProxy;
import xabrain.mods.transport.mod_Transport;

public class ClientProxy implements IProxy {
	@Override
	public void registerRenderInformation() {
		MinecraftForgeClient.preloadTexture("/xabrain/mods/transport/sprites/terrain.png");
		MinecraftForgeClient.preloadTexture("/xabrain/mods/transport/sprites/items.png");
	}
	
	@Override
	public File getMinecraftDir() {
		return Minecraft.getMinecraftDir();
	}
	
	@Override
	public boolean isRemote() {
		return ModLoader.getMinecraftInstance().theWorld.isRemote;
	}
	
	@Override
	public World getCurrentWorld() {
		return ModLoader.getMinecraftInstance().theWorld;
	}

	@Override
	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean renderPipe(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, BlockPipe block) {
		float centerMin = 0.4f;
		float centerMax = 0.6f;

		/* Update the connections */
		block.setBlockBoundsBasedOnState(world, x, y, z);

		renderer.overrideBlockTexture = 1;

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

		renderer.overrideBlockTexture = -1;
		return true;
	}
}
