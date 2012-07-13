package xabrain.mods.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.Block;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
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
		float speed = 0.1f;

		this.orientation = orientation;

		/* Set the motion depending on the orientation */
		motionX = 0.0f;
		motionY = 0.0f;
		motionZ = 0.0f;
		if (orientation == 4) motionX = -speed;
		if (orientation == 5) motionX = speed;
		if (orientation == 0) motionY = -speed;
		if (orientation == 1) motionY = speed;
		if (orientation == 2) motionZ = -speed;
		if (orientation == 3) motionZ = speed;

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
			if (orientation == 4 && diffX - epsilon > 0.1f) return;
			if (orientation == 5 && diffX + epsilon < 0.9f) return;
			if (orientation == 0 && diffY - epsilon > 0.1f) return;
			if (orientation == 1 && diffY + epsilon < 0.9f) return;
			if (orientation == 2 && diffZ - epsilon > 0.1f) return;
			if (orientation == 3 && diffZ + epsilon < 0.9f) return;

			/* We now reached the connector */
			connectorReached = true;
			centerReached = false;

			int newOrientation = onConnectorReached(x, y, z, orientation);
			if (newOrientation != -1 && newOrientation != orientation) setOrientation(newOrientation, true);
			return;
		}

		/* Check if we reached the center of the tile */
		if (centerReached) return;
		if (orientation == 4 && diffX - epsilon > 0.5f) return;
		if (orientation == 5 && diffX + epsilon < 0.5f) return;
		if (orientation == 0 && diffY - epsilon > 0.5f) return;
		if (orientation == 1 && diffY + epsilon < 0.5f) return;
		if (orientation == 2 && diffZ - epsilon > 0.5f) return;
		if (orientation == 3 && diffZ + epsilon < 0.5f) return;

		/* We now reached the center */
		centerReached = true;
		connectorReached = false;

		/* Set the last position to this new tile */
		lastX = x;
		lastY = y;
		lastZ = z;

		int newOrientation = onCenterReached(x, y, z, orientation);
		if (newOrientation != -1 && newOrientation != orientation) setOrientation(newOrientation, true);
	}

	public int onCenterReached(int x, int y, int z, int orientation) {
		int blockID = worldObj.getBlockId(x, y, z);

		/* Sanity check */
		if (!mod_Transport.blockPipeSimple.isPipe(blockID)) {
			System.out.println("ERROR: EntityPacket outside BlockPipe. Killing content.");
			setDead();
			return -1;
		}
		BlockPipe block = (BlockPipe) Block.blocksList[blockID];

		orientation = block.getPacketOrientation(worldObj, x, y, z, orientation, item);
		if (item.stackSize == 0) setDead();
		return orientation;
	}

	public int onConnectorReached(int x, int y, int z, int orientation) {
		int blockID = worldObj.getBlockId(x, y, z);

		/* Sanity check */
		if (!mod_Transport.blockPipeSimple.isPipe(blockID)) {
			System.out.println("ERROR: EntityPacket outside BlockPipe. Killing content.");
			setDead();
			return -1;
		}
		BlockPipe block = (BlockPipe) Block.blocksList[blockID];

		/* Check if we have connectors */
		TileEntityPipe te = block.getTileEntity(worldObj, x, y, z);
		if (te == null) return -1;

		/* Let the connector handle the item */
		orientation = te.processConnector(orientation, item);
		if (item.stackSize == 0) setDead();
		return orientation;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
		return false;
	}

	@Override
	protected void dealFireDamage(int par1) {}

	@Override
	public void applyEntityCollision(Entity par1Entity) {}

	@Override
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {}

	// @Override (Client-only)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int par9) {
		setPositionAndRotation(x, y, z, yaw, pitch);
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
