package xabrain.mods.ore;

import java.util.ArrayList;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.forge.ITextureProvider;

public class BlockOre extends Block implements ITextureProvider {
	public BlockOre(int blockID) {
		super(blockID, 0, Material.rock);
		setBlockName("ores");
		setHardness(3.0F);
		setResistance(5.0F);
		setStepSound(Block.soundStoneFootstep);
		setRequiresSelfNotify();
	}

	@Override
	public String getTextureFile() {
		return "/xabrain/mods/ore/resources/terrain.png";
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		return metadata;
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public void addCreativeItems(ArrayList itemList)
	{	   
		for (int i = 0; i < mod_Ore.oreNames.length; i++)
		{
			itemList.add(new ItemStack(this, 1, i));
		}
	}
}
