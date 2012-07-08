package xabrain.mods.transport;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;

public class TileEntityPipe extends TileEntity {
	private Connector[] connectors = new Connector[6];

	public void setConnector(int side, byte type) {
		if (type == 0) {
			connectors[side] = null;
			return;
		}
		connectors[side] = new Connector(this, side, type);
	}

	public Connector getConnector(int side) {
		return connectors[side];
	}

	public boolean hasConnector(int side) {
		return connectors[side] != null;
	}

	public void handlePacketData(byte[] connectors) {
		/* Read the state of our connectors */
		for (int i = 0; i < 6; i++) {
			if (connectors[i] == 0) continue;
			this.connectors[i] = new Connector(this, i, connectors[i]);
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
			byte type = par1NBTTagCompound.getByte("connector_" + i);
			if (type == 0) continue;
			connectors[i] = new Connector(this, i, type);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);

		/* Set where our connectors are */
		for (int i = 0; i < connectors.length; i++) {
			par1NBTTagCompound.setByte("connector_" + i, connectors[i] == null ? 0 : connectors[i].type);
		}
	}

	@Override
	public void updateEntity() {
		if (mod_Transport.proxy.isRemote()) return;

		for (int i = 0; i < connectors.length; i++) {
			if (connectors[i] == null) continue;

			connectors[i].update();
		}
	}

	public int processConnector(int side, ItemStack item) {
		if (connectors[side] == null) return -1;

		return connectors[side].receive(item);
	}
}
