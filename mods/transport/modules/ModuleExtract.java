package xabrain.mods.transport.modules;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.forge.ISidedInventory;
import xabrain.mods.transport.Connector;
import xabrain.mods.transport.EntityPacket;

public class ModuleExtract extends Module {
	public ModuleExtract(Integer type, Connector parent) {
		super(type, parent);
	}

	@Override
	public String getName() {
		return "Extract Module";
	}

	@Override
	public void update() {
		pullItem();
	}

	@Override
	public int receive(ItemStack itemStack) {
		return -1;
	}

	@Override
	public boolean accepts(ItemStack itemStack) {
		return false;
	}

	private void pullItem() {
		TileEntity te = parent.parent.worldObj.getBlockTileEntity(xConnection, yConnection, zConnection);

		if (te instanceof IInventory) tryDispatchingItem((IInventory) te, parent.side);

		/* XXX -- In future, extend to types defined by other mods */
	}

	private void tryDispatchingItem(IInventory inventory, int side) {
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

		for (int i = start; i < start + size; i++) {
			ItemStack is = inventory.decrStackSize(i, inventory.getInventoryStackLimit());
			if (is == null) continue;

			/* XXX -- Ask the network if he can handle this packet */

			dispatchItem(is);
		}
	}

	private void dispatchItem(ItemStack itemStack) {
		double xOffset, yOffset, zOffset;

		xOffset = 0.5f;
		yOffset = 0.5f;
		zOffset = 0.5f;

		switch (parent.side) {
			case 4:
				xOffset = 0.1f;
				break;

			case 5:
				xOffset = 0.9f;
				break;

			case 0:
				yOffset = 0.1f;
				break;

			case 1:
				yOffset = 0.9f;
				break;

			case 2:
				zOffset = 0.1f;
				break;

			case 3:
				zOffset = 0.9f;
				break;
		}

		EntityPacket entityPacket = new EntityPacket(parent.parent.worldObj, parent.parent.xCoord + xOffset, parent.parent.yCoord + yOffset, parent.parent.zCoord + zOffset, itemStack, parent.side ^ 0x1);
		parent.parent.worldObj.spawnEntityInWorld(entityPacket);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {}
}
