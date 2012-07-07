package xabrain.mods.transport;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;

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
}
