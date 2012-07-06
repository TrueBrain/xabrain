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
	public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID) {
		if (modelID == mod_Transport.renderTypePipe) {
			RenderPipe.renderBlock(renderer, world, x, y, z, (BlockPipe)block);
		}

		return false;
	}
}
