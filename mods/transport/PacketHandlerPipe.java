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
import net.minecraft.src.TileEntity;
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

		int x;
		int y;
		int z;
		byte[] connectors = new byte[6];

		/* Read the position and the connectors */
		try {
			x = dis.readInt();
			y = dis.readInt();
			z = dis.readInt();
			for (int i = 0; i < 6; i++)
				connectors[i] = dis.readByte();
		} catch (IOException e) {
			return;
		}

		/* Set the connectors */
		World world = mod_Transport.proxy.getCurrentWorld();
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityPipe) {
			TileEntityPipe tepipe = (TileEntityPipe) te;
			tepipe.handlePacketData(connectors);

			world.notifyBlockChange(x, y, z, mod_Transport.blockPipe.blockID);
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
			for (int i = 0; i < 6; i++) {
				dos.writeByte(te.hasConnector(i) ? te.getConnector(i).type : 0);
			}
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
