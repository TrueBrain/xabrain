package xabrain.mods.ore;

import java.io.File;

import net.minecraft.src.World;
import net.minecraft.src.forge.IGuiHandler;

public interface IProxy extends IGuiHandler  {
	public abstract void registerRenderInformation();
	
	public abstract File getMinecraftDir();
	
	public abstract boolean isRemote();
	
	public abstract World getCurrentWorld();
}
