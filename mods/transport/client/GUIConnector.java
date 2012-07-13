package xabrain.mods.transport.client;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import xabrain.mods.transport.Connector;
import xabrain.mods.transport.ContainerConnector;

public class GUIConnector extends GuiContainer {
	private Connector connector;
	private int selected;

	public GUIConnector(InventoryPlayer inventoryPlayer, Connector connector) {
		super(new ContainerConnector(inventoryPlayer, connector));
		this.connector = connector;
		this.selected = -1;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		if (selected != -1 && connector.modules[selected] == null) selected = -1;

		if (selected == -1) {
			this.fontRenderer.drawString("No module selected", 56, 8, 4210752);
			return;
		}

		this.fontRenderer.drawString(connector.modules[selected].getName(), 56, 8, 4210752);
	}

	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);

		if (button != 0 && button != 1) return;

		int left = (this.width - this.xSize) / 2;
		int top = (this.height - this.ySize) / 2;
		top += (18 * 4 - 18 * connector.slots) / 2;

		for (int i = 0; i < connector.slots; i++) {
			if (connector.modules[i] == null) continue;
			if (!isMouseOverButton(i, mouseX - left, mouseY - top)) continue;
			selected = i;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		int textureId = this.mc.renderEngine.getTexture("/xabrain/mods/transport/resources/gui-connector.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(textureId);

		int left = (this.width - this.xSize) / 2;
		int top = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);

		top += (18 * 4 - 18 * connector.slots) / 2;
		for (int i = 0; i < connector.slots; i++) {
			this.drawTexturedModalRect(left + 7, top + 6 + i * 18, 176, 0, 18, 18);

			if (connector.modules[i] == null) continue;

			if (selected == i || isMouseOverButton(i, mouseX - left, mouseY - top)) {
				this.drawTexturedModalRect(left + 7 + 22, top + 6 + 4 + i * 18, 204, 0, 10, 11);
			} else {
				this.drawTexturedModalRect(left + 7 + 22, top + 6 + 4 + i * 18, 194, 0, 10, 11);
			}
		}
	}

	private boolean isMouseOverButton(int button, int mouseX, int mouseY) {
		if (mouseX < 7 + 22) return false;
		if (mouseX >= 7 + 22 + 10) return false;
		if (mouseY < 6 + 4 + button * 18) return false;
		if (mouseY >= 6 + 4 + 11 + button * 18) return false;

		return true;
	}
}
