package xabrain.mods.transport.modules;

import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import xabrain.mods.transport.Connector;
import xabrain.mods.transport.EntityPacket;

public class ModuleExtract extends Module {
	public ModuleExtract(Connector parent) {
		super(parent);
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
		double xOffset, yOffset, zOffset;

		int x = parent.parent.xCoord;
		int y = parent.parent.yCoord;
		int z = parent.parent.zCoord;

		xOffset = 0.5f;
		yOffset = 0.5f;
		zOffset = 0.5f;

		switch (parent.side) {
			case 4:
				x--;
				xOffset = 0.1f;
				break;

			case 5:
				x++;
				xOffset = 0.9f;
				break;

			case 0:
				y--;
				yOffset = 0.1f;
				break;

			case 1:
				y++;
				yOffset = 0.9f;
				break;

			case 2:
				z--;
				zOffset = 0.1f;
				break;

			case 3:
				z++;
				zOffset = 0.9f;
				break;
		}

		TileEntity te = parent.parent.worldObj.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileEntityChest)) return;

		TileEntityChest tec = (TileEntityChest) te;
		for (int i = 0; i < tec.getSizeInventory(); i++) {
			ItemStack is = tec.decrStackSize(i, 64);
			if (is == null) continue;

			EntityPacket ei = new EntityPacket(parent.parent.worldObj, parent.parent.xCoord + xOffset, parent.parent.yCoord + yOffset, parent.parent.zCoord + zOffset, is, parent.side ^ 0x1);
			parent.parent.worldObj.spawnEntityInWorld(ei);
		}
	}
}
