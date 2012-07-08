package xabrain.mods.transport;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotModule extends Slot {
	public SlotModule(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	public boolean isItemValid(ItemStack itemStack) {
		if (itemStack.itemID != mod_Transport.itemModule.shiftedIndex) return false;

		return true;
	}
}
