package xabrain.mods.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import xabrain.mods.transport.modules.Module;

public class Connector implements IInventory {
	public TileEntityPipe parent;
	public int side;
	public byte type;

	public int slots = 1;
	public Module[] modules;
	private ItemStack[] modulesItemStack;

	public Connector(TileEntityPipe parent, int side, byte type) {
		this.parent = parent;
		this.side = side;
		this.type = type;

		slots = type;
		modules = new Module[slots];
		modulesItemStack = new ItemStack[slots];
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

	public boolean accepts(ItemStack item) {
		boolean accept = false;

		for (int i = 0; i < slots; i++) {
			if (modules[i] == null) continue;

			if (modules[i].accepts(item)) accept = true;
		}

		return accept;
	}

	@Override
	public int getSizeInventory() {
		return slots;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return modulesItemStack[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		ItemStack res = modulesItemStack[slot];

		modulesItemStack[slot] = null;
		modules[slot] = null;

		return res;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		if (item == null) {
			modulesItemStack[slot] = null;
			modules[slot] = null;
			return;
		}

		modulesItemStack[slot] = item;
		modules[slot] = Module.getModuleByType(mod_Transport.itemModule.getMetadata(item.getItemDamage()), this);
		if (modules[slot] == null) {
			System.out.println("Unable to load module of type '" + mod_Transport.itemModule.getMetadata(item.getItemDamage()) + "'. Destroying object.");
			modulesItemStack[slot] = null;
		}
	}

	@Override
	public String getInvName() {
		return "container.connector";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void onInventoryChanged() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	public void handlePacketData(DataInputStream dis) throws IOException {}

	public void writePacketData(DataOutputStream dos) throws IOException {}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		NBTTagList list = par1NBTTagCompound.getTagList("Modules");

		/* Load the stored modules; create the item for it */
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) list.tagAt(i);
			int slot = tag.getByte("Slot");
			int type = tag.getByte("Type");

			modules[slot] = Module.getModuleByType(type, this);
			modulesItemStack[slot] = new ItemStack(mod_Transport.itemModule.shiftedIndex, 1, type);
			modules[slot].readFromNBT(tag);
		}
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		NBTTagList list = new NBTTagList();

		/* Store the modules; don't store the item, we will recover it on load */
		for (byte i = 0; i < slots; i++) {
			if (modules[i] == null) continue;

			NBTTagCompound tag = new NBTTagCompound();
			tag.setByte("Slot", i);
			tag.setByte("Type", (byte) modules[i].type);
			modules[i].writeToNBT(tag);

			list.appendTag(tag);
		}

		par1NBTTagCompound.setTag("Modules", list);
	}
}
