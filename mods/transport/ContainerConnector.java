package xabrain.mods.transport;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Slot;

public class ContainerConnector extends Container {
	private Connector connector;

	public ContainerConnector(InventoryPlayer inventoryPlayer, Connector connector) {
		this.connector = connector;

		this.addSlot(new SlotModule(connector, 0, 56, 17));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}
}
