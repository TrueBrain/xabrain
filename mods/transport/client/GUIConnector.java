package xabrain.mods.transport.client;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import xabrain.mods.transport.Connector;
import xabrain.mods.transport.ContainerConnector;

public class GUIConnector extends GuiContainer {
	private Connector connector;

	public GUIConnector(InventoryPlayer inventoryPlayer, Connector connector) {
		super(new ContainerConnector(inventoryPlayer, connector));
		this.connector = connector;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int var4 = this.mc.renderEngine.getTexture("/gui/furnace.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
	}
}
