package xabrain.mods.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;

public class TileEntityPipe extends TileEntity {
	private Connector[] connectors = new Connector[6];
	private boolean hasPipe = false;

	public boolean hasPipe() {
		return hasPipe;
	}

	public void makePipe() {
		hasPipe = true;
	}

	public void setConnector(int side, byte type) {
		connectors[side] = (type == 0) ? null : new Connector(this, side, type);
	}

	public Connector getConnector(int side) {
		return connectors[side];
	}

	public boolean hasConnector(int side) {
		return connectors[side] != null;
	}

	public void handlePacketData(DataInputStream dis) throws IOException {
		this.hasPipe = dis.readBoolean();

		for (int i = 0; i < 6; i++) {
			byte type = dis.readByte();
			this.connectors[i] = (type == 0) ? null : new Connector(this, i, type);
		}
	}

	public void writePacketData(DataOutputStream dos) throws IOException {
		dos.writeBoolean(this.hasPipe);

		for (int i = 0; i < 6; i++) {
			dos.writeByte(connectors[i] == null ? 0 : connectors[i].type);
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
		hasPipe = par1NBTTagCompound.getBoolean("HasPipe");
		NBTTagList list = par1NBTTagCompound.getTagList("Connectors");

		/* Get where our connectors are */
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) list.tagAt(i);
			int slot = tag.getByte("Slot");
			byte type = tag.getByte("Type");

			connectors[slot] = new Connector(this, slot, type);
			connectors[slot].readFromNBT(tag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setBoolean("HasPipe", hasPipe);
		NBTTagList list = new NBTTagList();

		/* Set where our connectors are */
		for (byte i = 0; i < connectors.length; i++) {
			if (connectors[i] == null) continue;

			NBTTagCompound tag = new NBTTagCompound();
			tag.setByte("Slot", i);
			tag.setByte("Type", connectors[i].type);
			connectors[i].writeToNBT(tag);

			list.appendTag(tag);
		}

		par1NBTTagCompound.setTag("Connectors", list);
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
		if (connectors[side] == null) return -2;

		return connectors[side].receive(item);
	}

	public boolean acceptsItem(int side, ItemStack item) {
		if (connectors[side] == null) return false;

		return connectors[side].accepts(item);
	}
}
