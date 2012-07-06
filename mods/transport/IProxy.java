package xabrain.mods.transport;

import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;
import net.minecraft.src.forge.IGuiHandler;

public interface IProxy extends IGuiHandler  {
	public abstract void registerRenderInformation();
	
	public abstract File getMinecraftDir();
	
	public abstract boolean isRemote();
	
	public abstract World getCurrentWorld();

	public abstract boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, Block block, int modelID);
}
