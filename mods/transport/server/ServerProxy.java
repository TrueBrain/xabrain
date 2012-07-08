package xabrain.mods.transport.server;

import java.io.File;
import java.util.Map;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import xabrain.mods.transport.ContainerConnector;
import xabrain.mods.transport.IProxy;
import xabrain.mods.transport.TileEntityPipe;

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
	public Object getGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z) {
		switch (ID) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				TileEntity te = world.getBlockTileEntity(x, y, z);
				if (te == null) return null;
				if (!(te instanceof TileEntityPipe)) return null;

				return new ContainerConnector(entityPlayer.inventory, ((TileEntityPipe) te).getConnector(ID));
		}

		return null;
	}

	@Override
	public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID) {
		return false;
	}

	@Override
	public void addRenderer(Map map) {}
}
