package xabrain.mods.transport;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Slot;

public class ContainerConnector extends Container {
	private Connector connector;
	public int selected;

	public ContainerConnector(InventoryPlayer inventoryPlayer, Connector connector) {
		this.connector = connector;
		this.selected = -1;

		/* Module slots */
		int top = (18 * 4 - 18 * connector.slots) / 2;
		for (int i = 0; i < connector.slots; i++) {
			this.addSlot(new SlotModule(connector, i, 8, 7 + top + i * 18));
		}

		/* Inventory slots */
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		/* Equipped slots */
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}
}
