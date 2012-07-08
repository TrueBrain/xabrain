package xabrain.mods.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.minecraft.src.forge.ISpawnHandler;

public class EntityPacket extends EntityItem implements ISpawnHandler {
	private static final float epsilon = 0.0001f;

	private int lastX, lastY, lastZ;
	private boolean centerReached, connectorReached;

	private int orientation;

	public EntityPacket(World world) {
		super(world);
		entityInit();
	}

	public EntityPacket(World world, double x, double y, double z, ItemStack item, int orientation) {
		super(world);
		entityInit();

		setPositionAndRotation(x, y, z, 0, 0);
		this.item = item;
		setOrientation(orientation, false);

		/* Act like we come from the neighbour tile */
		lastX = MathHelper.floor_double(posX) + (orientation == 4 ? -1 : (orientation == 5 ? 1 : 0));
		lastY = MathHelper.floor_double(posY) + (orientation == 0 ? -1 : (orientation == 1 ? 1 : 0));
		lastZ = MathHelper.floor_double(posZ) + (orientation == 2 ? -1 : (orientation == 3 ? 1 : 0));

		/* We come from a connector */
		centerReached = false;
		connectorReached = true;
	}

	private void setOrientation(int orientation, boolean recenter) {
		this.orientation = orientation;

		/* Set the motion depending on the orientation */
		motionX = 0.0f;
		motionY = 0.0f;
		motionZ = 0.0f;
		if (orientation == 4) motionX = 0.05f;
		if (orientation == 5) motionX = -0.05f;
		if (orientation == 0) motionY = 0.05f;
		if (orientation == 1) motionY = -0.05f;
		if (orientation == 2) motionZ = 0.05f;
		if (orientation == 3) motionZ = -0.05f;

		/* If requested, move to the exact middle of the tile */
		if (recenter) {
			double diffX = posX - (int) posX;
			double diffY = posY - (int) posY;
			double diffZ = posZ - (int) posZ;
			if (diffX < 0 && diffX != -0.5f) posX += -0.5f - diffX;
			if (diffX > 0 && diffX != 0.5f) posX += 0.5f - diffX;
			if (diffY < 0 && diffY != -0.5f) posY += -0.5f - diffY;
			if (diffY > 0 && diffY != 0.5f) posY += 0.5f - diffY;
			if (diffZ < 0 && diffZ != -0.5f) posZ += -0.5f - diffZ;
			if (diffZ > 0 && diffZ != 0.5f) posZ += 0.5f - diffZ;
		}

		/* Force update of this entity */
		this.isAirBorne = true;
	}

	@Override
	protected void entityInit() {
		noClip = true;
		setSize(0.15f, 0.15f);
		yOffset = 0.00f;
	}

	@Override
	public void onUpdate() {
		this.moveEntity(motionX, motionY, motionZ);

		if (mod_Transport.proxy.isRemote()) return;

		/* Calculate the x/y/z of the block */
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);

		/* Find the offset in the block */
		double diffX = Math.abs(posX - (int) posX);
		double diffY = Math.abs(posY - (int) posY);
		double diffZ = Math.abs(posZ - (int) posZ);
		if (x < 0) diffX = 1.0f - diffX;
		if (y < 0) diffY = 1.0f - diffY;
		if (z < 0) diffZ = 1.0f - diffZ;

		if (x == lastX && y == lastY && z == lastZ) {
			/* Check if we reached the connectors */
			if (connectorReached) return;
			if (orientation == 4 && diffX + epsilon < 0.9f) return;
			if (orientation == 5 && diffX - epsilon > 0.1f) return;
			if (orientation == 0 && diffY + epsilon < 0.9f) return;
			if (orientation == 1 && diffY - epsilon > 0.1f) return;
			if (orientation == 2 && diffZ + epsilon < 0.9f) return;
			if (orientation == 3 && diffZ - epsilon > 0.1f) return;

			/* We now reached the connector */
			connectorReached = true;
			centerReached = false;

			onConnectorReached(x, y, z, orientation ^ 0x1);
			return;
		}

		/* Check if we reached the center of the tile */
		if (centerReached) return;
		if (orientation == 4 && diffX + epsilon < 0.5f) return;
		if (orientation == 5 && diffX - epsilon > 0.5f) return;
		if (orientation == 0 && diffY + epsilon < 0.5f) return;
		if (orientation == 1 && diffY - epsilon > 0.5f) return;
		if (orientation == 2 && diffZ + epsilon < 0.5f) return;
		if (orientation == 3 && diffZ - epsilon > 0.5f) return;

		/* We now reached the center */
		centerReached = true;
		connectorReached = false;

		/* Set the last position to this new tile */
		lastX = x;
		lastY = y;
		lastZ = z;

		int newOrientation = onCenterReached(x, y, z, orientation);
		if (newOrientation != orientation) setOrientation(newOrientation, true);
	}

	public int onCenterReached(int x, int y, int z, int orientation) {
		/* Sanity check */
		if (worldObj.getBlockId(x, y, z) != mod_Transport.blockPipe.blockID) {
			System.out.println("ERROR: EntityPacket outside BlockPipe. Killing content.");
			setDead();
			return orientation;
		}

		/* Check if we have connectors */
		TileEntityPipe te = mod_Transport.blockPipe.getTileEntity(worldObj, x, y, z);

		if (orientation != 4 && (mod_Transport.blockPipe.canConnectPipeTo(worldObj, x - 1, y, z, 5) || (te != null && te.hasConnector(4)))) return 5;
		if (orientation != 5 && (mod_Transport.blockPipe.canConnectPipeTo(worldObj, x + 1, y, z, 4) || (te != null && te.hasConnector(5)))) return 4;
		if (orientation != 0 && (mod_Transport.blockPipe.canConnectPipeTo(worldObj, x, y - 1, z, 1) || (te != null && te.hasConnector(0)))) return 1;
		if (orientation != 1 && (mod_Transport.blockPipe.canConnectPipeTo(worldObj, x, y + 1, z, 0) || (te != null && te.hasConnector(1)))) return 0;
		if (orientation != 2 && (mod_Transport.blockPipe.canConnectPipeTo(worldObj, x, y, z - 1, 3) || (te != null && te.hasConnector(2)))) return 3;
		if (orientation != 3 && (mod_Transport.blockPipe.canConnectPipeTo(worldObj, x, y, z + 1, 2) || (te != null && te.hasConnector(3)))) return 2;

		return orientation;
	}

	public void onConnectorReached(int x, int y, int z, int orientation) {
		/* Sanity check */
		if (worldObj.getBlockId(x, y, z) != mod_Transport.blockPipe.blockID) {
			System.out.println("ERROR: EntityPacket outside BlockPipe. Killing content.");
			setDead();
			return;
		}

		/* Check if we have connectors */
		TileEntityPipe te = mod_Transport.blockPipe.getTileEntity(worldObj, x, y, z);
		if (te == null) return;

		switch (te.getConnector(orientation)) {
			case 0:
				/* We have no connector on that side */
				return;

			case 1:
			case 3:
			case 4:
				break;

			case 2:
				te.pushItem(item, orientation);
				setDead();
				break;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
		return false;
	}

	@Override
	protected void dealFireDamage(int par1) {}

	@Override
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {}

	// @Override (Client-only)
	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
		this.setPosition(par1, par3, par5);
		this.setRotation(par7, par8);
	}

	@Override
	public void writeSpawnData(DataOutputStream data) throws IOException {
		data.writeInt(item.itemID);
		data.writeInt(item.stackSize);
		data.writeInt(item.getItemDamage());
	}

	@Override
	public void readSpawnData(DataInputStream data) throws IOException {
		int itemID = data.readInt();
		int stackSize = data.readInt();
		int damage = data.readInt();

		item = new ItemStack(itemID, stackSize, damage);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setCompoundTag("Item", this.item.writeToNBT(new NBTTagCompound()));

		par1NBTTagCompound.setInteger("LastX", lastX);
		par1NBTTagCompound.setInteger("LastY", lastY);
		par1NBTTagCompound.setInteger("LastZ", lastZ);

		par1NBTTagCompound.setInteger("Orientation", orientation);

		par1NBTTagCompound.setBoolean("CenterReached", centerReached);
		par1NBTTagCompound.setBoolean("ConnectorReached", connectorReached);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("Item");
		this.item = ItemStack.loadItemStackFromNBT(var2);

		lastX = par1NBTTagCompound.getInteger("LastX");
		lastY = par1NBTTagCompound.getInteger("LastY");
		lastZ = par1NBTTagCompound.getInteger("LastZ");

		orientation = par1NBTTagCompound.getInteger("Orientation");

		centerReached = par1NBTTagCompound.getBoolean("CenterReached");
		connectorReached = par1NBTTagCompound.getBoolean("ConnectorReached");

		if (this.item == null) this.setDead();
	}
}