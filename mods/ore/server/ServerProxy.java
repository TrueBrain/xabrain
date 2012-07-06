package xabrain.mods.ore.server;

import java.io.File;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import xabrain.mods.ore.IProxy;

public class ServerProxy implements IProxy {
	@Override
	public void registerRenderInformation() {}

	@Override
	public File getMinecraftDir() {
		return new File(".");
	}

	@Override
	public boolean isRemote() {
		return false;
	}

	@Override
	public World getCurrentWorld() {
		return null;
	}

	@Override
	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}
