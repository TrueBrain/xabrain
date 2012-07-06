package xabrain.mods.transport.server;

import java.io.File;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;
import xabrain.mods.transport.BlockPipe;
import xabrain.mods.transport.IProxy;

public class ServerProxy implements IProxy {
	@Override
	public void registerRenderInformation() {
	}

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

	@Override
	public boolean renderPipe(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, BlockPipe block) {
		return false;
	}
}
