package xabrain.mods.transport.modules;

import xabrain.mods.transport.Connector;
import net.minecraft.src.ItemStack;

public abstract class Module {
	protected Connector parent;

	public Module(Connector parent) {
		this.parent = parent;
	}

	public abstract void update();

	public abstract int receive(ItemStack itemStack);
}
