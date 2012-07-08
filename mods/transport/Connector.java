package xabrain.mods.transport;

import xabrain.mods.transport.modules.Module;
import xabrain.mods.transport.modules.ModuleDeliver;
import xabrain.mods.transport.modules.ModuleExtract;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;

public class Connector {
	public TileEntityPipe parent;
	public int side;
	public byte type;

	public int slots = 1;
	public Module[] modules;

	public Connector(TileEntityPipe parent, int side, byte type) {
		this.parent = parent;
		this.side = side;
		this.type = type;

		slots = type;
		modules = new Module[slots];

		if (type == 1) modules[0] = new ModuleExtract(this);
		if (type == 2) modules[0] = new ModuleDeliver(this);
	}

	public int getTextureID() {
		return 0x10 + type - 1;
	}

	public void update() {
		for (int i = 0; i < slots; i++) {
			if (modules[i] == null) continue;

			modules[i].update();
		}
	}
	
	public int receive(ItemStack item) {
		int orientation = -1;

		for (int i = 0; i < slots; i++) {
			if (modules[i] == null) continue;

			int or = modules[i].receive(item);
			if (or != -1) orientation = -1;
		}

		return orientation;
	}
}
