package xabrain.mods.ore;

import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.SidedProxy;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;
import net.minecraft.src.forge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;

public class mod_Ore extends NetworkMod {
	public static final String[] oreNames = new String[] { "Copper", "Tin" };

	@SidedProxy(clientSide="xabrain.mods.ore.client.ClientProxy", serverSide="xabrain.mods.ore.server.ServerProxy")
	public static IProxy proxy;

	public static Block blockOre;
	public static Item itemIngot;
	public static WorldGenerator worldGenerator = new WorldGenerator();

	@Override
	public String getVersion()
	{
		return "0.1";
	}
	
	@Override
	public boolean clientSideRequired()
	{
		return true;
	}

	@Override
	public void load()
	{
		MinecraftForge.versionDetect("Ore", 3, 3, 8);

		File cfgFile = new File(proxy.getMinecraftDir(), "config/XaBrain.cfg");
		Configuration cfg = new Configuration(cfgFile);
		cfg.load();

		blockOre = new BlockOre(cfg.getOrCreateBlockIdProperty("ores", 2000).getInt(2000));
		itemIngot = new ItemIngot(cfg.getOrCreateIntProperty("ingots", Configuration.CATEGORY_ITEM, 20001).getInt(20001));

		cfg.save();

		proxy.registerRenderInformation();

		ModLoader.registerBlock(blockOre, ItemOre.class);
		FMLCommonHandler.instance().registerWorldGenerator(worldGenerator);

		addOres();
	}

	public void addOres() {
		for (int i = 0; i < oreNames.length; i++) {
			FurnaceRecipes.smelting().addSmelting(blockOre.blockID, i, new ItemStack(itemIngot, 1, i));

			ModLoader.addName(new ItemStack(blockOre, 1, i), oreNames[i] + " Ore");
			ModLoader.addName(new ItemStack(itemIngot, 1, i), oreNames[i] + " Ingot");

			OreDictionary.registerOre("ore" + oreNames[i], new ItemStack(blockOre, 1, i));
			OreDictionary.registerOre("ingot" + oreNames[i], new ItemStack(itemIngot, 1, i));

			MinecraftForge.setBlockHarvestLevel(blockOre, i, "pickaxe", 1);
		}
	}
}
