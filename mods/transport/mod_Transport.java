package xabrain.mods.transport;

import java.io.File;

import xabrain.mods.ore.ItemIngot;

import net.minecraft.src.Block;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.SidedProxy;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;
import net.minecraft.src.forge.oredict.OreDictionary;

public class mod_Transport extends NetworkMod {
	public static final String[] pipeNames = new String[] { "Small", "Medium", "Large", "Huge" };
	public static final String[] connectorNames = new String[] { "Mk1", "Mk2", "Mk3", "Mk4" };

	@SidedProxy(clientSide = "xabrain.mods.transport.client.ClientProxy", serverSide = "xabrain.mods.transport.server.ServerProxy")
	public static IProxy proxy;

	public static Block blockPipe;
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

		cfg.save();

		proxy.registerRenderInformation();

		registerAll();

		renderTypePipe = ModLoader.getUniqueBlockModelID(this, false);

		/* Temporary for easy testing */
		ModLoader.addRecipe(new ItemStack(blockPipe, 1, 0), new Object[] { "X_", "__", 'X', Block.dirt });
		ModLoader.addRecipe(new ItemStack(blockPipe, 1, 1), new Object[] { "XX", "__", 'X', Block.dirt });
		ModLoader.addRecipe(new ItemStack(blockPipe, 1, 2), new Object[] { "XX", "X_", 'X', Block.dirt });
		ModLoader.addRecipe(new ItemStack(blockPipe, 1, 3), new Object[] { "XX", "XX", 'X', Block.dirt });
	}

	private void registerAll() {
		ModLoader.registerBlock(blockPipe, ItemPipe.class);

		for (int i = 0; i < pipeNames.length; i++) {
			ModLoader.addName(new ItemStack(blockPipe, 1, i), pipeNames[i] + " Pipe");
		}
	}

	@Override
	public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID) {
		return proxy.renderWorldBlock(renderer, world, x, y, z, block, modelID);
	}
}
