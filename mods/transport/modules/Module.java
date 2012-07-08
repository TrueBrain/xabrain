package xabrain.mods.transport.modules;

import java.lang.reflect.Constructor;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import xabrain.mods.transport.Connector;

public abstract class Module {
	public static final Class[] moduleTypes = new Class[] { ModuleExtract.class, ModuleDeliver.class, };

	public static Module getModuleByType(int type, Connector parent) {
		if (type >= moduleTypes.length) return null;

		try {
			Constructor ctor = moduleTypes[type].getConstructor(new Class[] { Integer.class, Connector.class });
			return (Module) ctor.newInstance(new Object[] { type, parent });
		} catch (Throwable t) {}

		return null;
	}

	protected Connector parent;
	public int type;

	public Module(Integer type, Connector parent) {
		this.type = type;
		this.parent = parent;
	}

	public abstract void update();

	public abstract int receive(ItemStack itemStack);

	public abstract boolean accepts(ItemStack itemStack);

	public abstract void readFromNBT(NBTTagCompound par1NBTTagCompound);

	public abstract void writeToNBT(NBTTagCompound par1NBTTagCompound);
}
