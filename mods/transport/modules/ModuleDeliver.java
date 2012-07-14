package xabrain.mods.transport.modules;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.forge.ISidedInventory;
import xabrain.mods.transport.Connector;

public class ModuleDeliver extends Module {
	public ModuleDeliver(Integer type, Connector parent) {
		super(type, parent);
	}

	@Override
	public String getName() {
		return "Deliver Module";
	}

	@Override
	public void update() {}

	@Override
	public int receive(ItemStack itemStack) {
		pushItem(itemStack);
		return -1;
	}

	@Override
	public boolean accepts(ItemStack itemStack) {
		return true;
	}

	private boolean canHaveItem(ItemStack itemStack) {
		TileEntity te = parent.parent.worldObj.getBlockTileEntity(xConnection, yConnection, zConnection);

		if (te instanceof IInventory) return addItemToInventory((IInventory) te, itemStack, parent.side, true);

		/* XXX -- In future, extend to types defined by other mods */

		return false;
	}

	private boolean addItemToInventory(IInventory inventory, ItemStack itemStack, int side, boolean test) {
		int start;
		int size;

		/* Figure out which inventory slots we can use when delivering from this side */
		if (inventory instanceof ISidedInventory) {
			ISidedInventory sidedInventory = (ISidedInventory) inventory;

			start = sidedInventory.getStartInventorySide(side);
			size = sidedInventory.getSizeInventorySide(side);
		} else {
			start = 0;
			size = inventory.getSizeInventory();
		}

		int total = itemStack.stackSize;

		for (int i = start; i < start + size; i++) {
			ItemStack is = inventory.getStackInSlot(i);

			/* If there is no stack yet, see how much we can place in this slot */
			if (is == null) {
				int diff = inventory.getInventoryStackLimit();
				if (diff > total) diff = total;

				total -= diff;
				if (!test) {
					is = itemStack.copy();
					is.stackSize = diff;
					itemStack.stackSize -= diff;

					inventory.setInventorySlotContents(i, is);
				}
			} else {
				if (is.getItem() != itemStack.getItem() || is.getItemDamage() != itemStack.getItemDamage()) continue;
	
				/* Add up all partial free stacks */
				int diff = is.getMaxStackSize() - is.stackSize;
				if (diff > inventory.getInventoryStackLimit() - is.stackSize) diff = inventory.getInventoryStackLimit() - is.stackSize;
				if (diff > total) diff = total;
	
				total -= diff;
				if (!test) {
					is.stackSize += diff;
					itemStack.stackSize -= diff;
				}
			}

			if (total <= 0) return true;
		}

		return false;
	}

	private void pushItem(ItemStack itemStack) {
		TileEntity te = parent.parent.worldObj.getBlockTileEntity(xConnection, yConnection, zConnection);

		if (te instanceof IInventory) addItemToInventory((IInventory) te, itemStack, parent.side, false);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {}
}
