package xabrain.mods.transport;

import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.SidedProxy;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;

public class mod_Transport extends NetworkMod {
	public static final String[] pipeNames = new String[] { "Small", "Medium", "Large", "Huge" };
	public static final String[] connectorNames = new String[] { "Mk1", "Mk2", "Mk3", "Mk4" };

	@SidedProxy(clientSide = "xabrain.mods.transport.client.ClientProxy", serverSide = "xabrain.mods.transport.server.ServerProxy")
	public static IProxy proxy;

	public static BlockPipe blockPipe;
	public static ItemConnector itemConnector;
	public static int renderTypePipe;

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public boolean clientSideRequired() {
		return true;
	}

	@Override
	public void load() {
		MinecraftForge.versionDetect("Transport", 3, 3, 8);

		File cfgFile = new File(proxy.getMinecraftDir(), "config/XaBrain.cfg");
		Configuration cfg = new Configuration(cfgFile);
		cfg.load();

		blockPipe = new BlockPipe(cfg.getOrCreateBlockIdProperty("pipe", 2001).getInt(2001));
		itemConnector = new ItemConnector(cfg.getOrCreateIntProperty("connectors", Configuration.CATEGORY_ITEM, 20001).getInt(20001));

		cfg.save();

		proxy.registerRenderInformation();

		registerAll();

		renderTypePipe = ModLoader.getUniqueBlockModelID(this, false);
		MinecraftForge.registerConnectionHandler(new PacketHandlerPipe());
	}

	private void registerAll() {
		ModLoader.registerBlock(blockPipe, ItemPipe.class);
		ModLoader.registerTileEntity(TileEntityPipe.class, "transport.pipe");

		for (int i = 0; i < pipeNames.length; i++) {
			ModLoader.addName(new ItemStack(blockPipe, 1, i), pipeNames[i] + " Pipe");
		}
		for (int i = 0; i < connectorNames.length; i++) {
			ModLoader.addName(new ItemStack(itemConnector, 1, i), "Connector " + connectorNames[i]);
		}
	}

	@Override
	public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID) {
		return proxy.renderWorldBlock(renderer, world, x, y, z, block, modelID);
	}
}
