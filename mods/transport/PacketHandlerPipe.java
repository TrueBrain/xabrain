package xabrain.mods.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;
import net.minecraft.src.forge.IConnectionHandler;
import net.minecraft.src.forge.IPacketHandler;
import net.minecraft.src.forge.MessageManager;

public class PacketHandlerPipe implements IPacketHandler, IConnectionHandler {
	private static String channelName = "pipe";

	@Override
	public void onConnect(NetworkManager network) {
		MessageManager.getInstance().registerChannel(network, this, channelName);
	}

	@Override
	public void onDisconnect(NetworkManager network, String message, Object[] args) {
		MessageManager.getInstance().removeConnection(network);
	}

	@Override
	public void onLogin(NetworkManager network, Packet1Login login) {}

	@Override
	public void onPacketData(NetworkManager network, String channel, byte[] data) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		int packetType;
		int x;
		int y;
		int z;

		try {
			x = dis.readInt();
			y = dis.readInt();
			z = dis.readInt();
			packetType = dis.readByte();
		} catch (IOException e) {
			return;
		}

		World world = mod_Transport.proxy.getCurrentWorld();
		TileEntityPipe te = (TileEntityPipe) world.getBlockTileEntity(x, y, z);
		if (te == null) return;

		switch (packetType) {
			case 0:
				try {
					te.handlePacketData(dis);
				} catch (IOException e) {
					return;
				}

				world.notifyBlockChange(x, y, z, mod_Transport.blockPipeSimple.blockID);
				break;

			case 1:
				int side;
				try {
					side = dis.readByte();
					te.getConnector(side).handlePacketData(dis);
				} catch (IOException e) {
					return;
				}

				break;
		}
	}

	public static Packet getPacket(TileEntityPipe te) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		DataOutputStream dos = new DataOutputStream(bos);

		/* Write the position and connectors state */
		try {
			dos.writeInt(te.xCoord);
			dos.writeInt(te.yCoord);
			dos.writeInt(te.zCoord);
			dos.writeByte(0);
			te.writePacketData(dos);
		} catch (IOException e) {
			return null;
		}

		/* Create the packet with the update */
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		pkt.channel = channelName;
		pkt.data = bos.toByteArray();
		pkt.length = bos.size();
		pkt.isChunkDataPacket = true;
		return pkt;
	}

	public static Packet getPacket(Connector connector) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		DataOutputStream dos = new DataOutputStream(bos);

		/* Write the position and connectors state */
		try {
			dos.writeInt(connector.parent.xCoord);
			dos.writeInt(connector.parent.yCoord);
			dos.writeInt(connector.parent.zCoord);
			dos.writeByte(1);
			connector.writePacketData(dos);
		} catch (IOException e) {
			return null;
		}

		/* Create the packet with the update */
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		pkt.channel = channelName;
		pkt.data = bos.toByteArray();
		pkt.length = bos.size();
		pkt.isChunkDataPacket = true;
		return pkt;
	}
}
