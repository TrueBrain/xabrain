package xabrain.mods.ore.client;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import net.minecraft.src.forge.MinecraftForgeClient;
import xabrain.mods.ore.IProxy;

public class ClientProxy implements IProxy {
	@Override
	public void registerRenderInformation() {
		MinecraftForgeClient.preloadTexture("/xabrain/mods/ore/resources/terrain.png");
		MinecraftForgeClient.preloadTexture("/xabrain/mods/ore/resources/items.png");
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
}
