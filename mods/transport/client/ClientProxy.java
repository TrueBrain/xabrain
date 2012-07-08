package xabrain.mods.transport.client;

import java.io.File;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.forge.MinecraftForgeClient;
import xabrain.mods.transport.BlockPipe;
import xabrain.mods.transport.EntityPacket;
import xabrain.mods.transport.IProxy;
import xabrain.mods.transport.TileEntityPipe;
import xabrain.mods.transport.mod_Transport;

public class ClientProxy implements IProxy {
	@Override
	public void registerRenderInformation() {
		MinecraftForgeClient.preloadTexture("/xabrain/mods/transport/resources/terrain.png");
		MinecraftForgeClient.preloadTexture("/xabrain/mods/transport/resources/items.png");
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

				return new GUIConnector(entityPlayer.inventory, ((TileEntityPipe) te).getConnector(ID));
		}

		return null;
	}

	@Override
	public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID) {
		if (modelID == mod_Transport.renderTypePipe) {
			RenderPipe.renderBlock(renderer, world, x, y, z, (BlockPipe) block);
		}

		return false;
	}

	@Override
	public void addRenderer(Map map) {
		map.put(EntityPacket.class, new RenderPacket());
	}
}
