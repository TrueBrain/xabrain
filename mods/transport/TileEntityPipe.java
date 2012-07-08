package xabrain.mods.transport;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;

public class TileEntityPipe extends TileEntity {
	private byte[] connectors = new byte[6];

	public void setConnector(int side, byte type) {
		connectors[side] = type;
	}

	public byte getConnector(int side) {
		return connectors[side];
	}

	public boolean hasConnector(int side) {
		return connectors[side] != 0;
	}

	public void handlePacketData(byte[] connectors) {
		/* Read the state of our connectors */
		for (int i = 0; i < 6; i++) {
			this.connectors[i] = connectors[i];
		}
	}

	// @Override (Server-only)
	public Packet getDescriptionPacket() {
		/* Send the state of our connectors */
		return PacketHandlerPipe.getPacket(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);

		/* Get where our connectors are */
		for (int i = 0; i < connectors.length; i++) {
			connectors[i] = par1NBTTagCompound.getByte("connector_" + i);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);

		/* Set where our connectors are */
		for (int i = 0; i < connectors.length; i++) {
			par1NBTTagCompound.setByte("connector_" + i, connectors[i]);
		}
	}

	@Override
	public void updateEntity() {
		if (mod_Transport.proxy.isRemote()) return;

		for (int i = 0; i < connectors.length; i++) {
			if (connectors[i] == 0) continue;

			switch (connectors[i]) {
				case 1:
					pullItem(i);
					break;

				case 2:
				case 3:
				case 4:
					break;
			}
		}
	}

	public int processConnector(int side, ItemStack item) {
		if (!hasConnector(side)) return -1;

		switch (connectors[side]) {
			case 2:
				pushItem(item, side);
				item.stackSize = 0;
				break;
		}

		return -1;
	}

	public void pushItem(ItemStack itemStack, int side) {
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;

		switch (side) {
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

		TileEntity te = worldObj.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileEntityChest)) return;

		TileEntityChest tec = (TileEntityChest) te;
		for (int i = 0; i < tec.getSizeInventory(); i++) {
			ItemStack ie = tec.getStackInSlot(i);
			if (ie == null) {
				tec.setInventorySlotContents(i, itemStack.copy());
				break;
			}
		}
	}

	private void pullItem(int side) {
		double xOffset, yOffset, zOffset;

		int x = xCoord;
		int y = yCoord;
		int z = zCoord;

		xOffset = 0.5f;
		yOffset = 0.5f;
		zOffset = 0.5f;

		switch (side) {
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

		TileEntity te = worldObj.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileEntityChest)) return;

		TileEntityChest tec = (TileEntityChest) te;
		for (int i = 0; i < tec.getSizeInventory(); i++) {
			ItemStack is = tec.decrStackSize(i, 64);
			if (is == null) continue;

			EntityPacket ei = new EntityPacket(worldObj, xCoord + xOffset, yCoord + yOffset, zCoord + zOffset, is, side ^ 0x1);
			worldObj.spawnEntityInWorld(ei);
		}
	}
}
