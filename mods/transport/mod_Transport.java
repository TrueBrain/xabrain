package xabrain.mods.transport;

import java.io.File;
import java.util.Map;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.SidedProxy;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;

public class mod_Transport extends NetworkMod {
	public static final String[] connectorNames = new String[] { "Mk1", "Mk2", "Mk3", "Mk4" };
	public static final String[] moduleNames = new String[] { "Extract", "Deliver" };

	@SidedProxy(clientSide = "xabrain.mods.transport.client.ClientProxy", serverSide = "xabrain.mods.transport.server.ServerProxy")
	public static IProxy proxy;
	public static mod_Transport instance;

	public static BlockPipeSimple blockPipeSimple;
	public static BlockPipeComplex blockPipeComplex;
	public static ItemConnector itemConnector;
	public static ItemModule itemModule;
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
		instance = this;

		MinecraftForge.versionDetect("Transport", 3, 3, 8);

		File cfgFile = new File(proxy.getMinecraftDir(), "config/XaBrain.cfg");
		Configuration cfg = new Configuration(cfgFile);
		cfg.load();

		blockPipeSimple = new BlockPipeSimple(cfg.getOrCreateBlockIdProperty("pipeSimple", 2001).getInt(2001));
		blockPipeComplex = new BlockPipeComplex(cfg.getOrCreateBlockIdProperty("pipeComplex", 2002).getInt(2002));
		itemConnector = new ItemConnector(cfg.getOrCreateIntProperty("connectors", Configuration.CATEGORY_ITEM, 20001).getInt(20001));
		itemModule = new ItemModule(cfg.getOrCreateIntProperty("modules", Configuration.CATEGORY_ITEM, 20002).getInt(20002));

		cfg.save();

		proxy.registerRenderInformation();

		registerAll();

		renderTypePipe = ModLoader.getUniqueBlockModelID(this, false);
		MinecraftForge.registerConnectionHandler(new PacketHandlerPipe());
		MinecraftForge.setGuiHandler(this, proxy);
		MinecraftForge.registerSaveHandler(new SaveLoadHandler());
	}

	private void registerAll() {
		ModLoader.registerBlock(blockPipeSimple, ItemPipe.class);
		ModLoader.registerBlock(blockPipeComplex, ItemPipe.class);
		ModLoader.registerTileEntity(TileEntityPipe.class, "transport.pipe");
		ModLoader.registerEntityID(EntityPacket.class, "entity.packet", ModLoader.getUniqueEntityId());
		MinecraftForge.registerEntity(EntityPacket.class, this, 1, 64, 20, true);

		for (int i = 0; i < ItemDye.dyeColorNames.length; i++) {
			ModLoader.addName(new ItemStack(blockPipeSimple, 1, i), ItemDye.dyeColorNames[i].substring(0, 1).toUpperCase().concat(ItemDye.dyeColorNames[i].substring(1)) + " Pipe");
		}
		for (int i = 0; i < connectorNames.length; i++) {
			ModLoader.addName(new ItemStack(itemConnector, 1, i), "Connector " + connectorNames[i]);
		}
		for (int i = 0; i < moduleNames.length; i++) {
			ModLoader.addName(new ItemStack(itemModule, 1, i), "Module " + moduleNames[i]);
		}
	}

	@Override
	public void addRenderer(Map map) {
		proxy.addRenderer(map);
	}

	@Override
	public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID) {
		return proxy.renderWorldBlock(renderer, world, x, y, z, block, modelID);
	}
}
