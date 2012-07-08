package xabrain.mods.transport.modules;

import xabrain.mods.transport.Connector;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;

public class ModuleDeliver extends Module {
	public ModuleDeliver(Connector parent) {
		super(parent);
	}

	@Override
	public void update() {
	}

	@Override
	public int receive(ItemStack itemStack) {
		pushItem(itemStack);
		return -1;
	}

	public void pushItem(ItemStack itemStack) {
		int x = parent.parent.xCoord;
		int y = parent.parent.yCoord;
		int z = parent.parent.zCoord;

		switch (parent.side) {
			case 4:
				x--;
				break;

			case 5:
				x++;
				break;

			case 0:
				y--;
				break;

			case 1:
				y++;
				break;

			case 2:
				z--;
				break;

			case 3:
				z++;
				break;
		}

		TileEntity te = parent.parent.worldObj.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileEntityChest)) return;

		TileEntityChest tec = (TileEntityChest) te;
		for (int i = 0; i < tec.getSizeInventory(); i++) {
			ItemStack ie = tec.getStackInSlot(i);
			if (ie == null) {
				tec.setInventorySlotContents(i, itemStack.copy());
				itemStack.stackSize = 0;
				break;
			}
		}
	}
}
