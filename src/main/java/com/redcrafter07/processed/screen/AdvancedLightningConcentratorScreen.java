package com.redcrafter07.processed.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.redcrafter07.processed.Processed;
import com.redcrafter07.processed.container.AdvancedLightningConcentratorContainer;
import com.redcrafter07.processed.container.LightningConcentratorContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;

public class AdvancedLightningConcentratorScreen extends ContainerScreen<AdvancedLightningConcentratorContainer> {
    private final ResourceLocation guiLocation = new ResourceLocation(Processed.MOD_ID, "textures/gui/advanced_lightning_concentrator.png");

    public AdvancedLightningConcentratorScreen(AdvancedLightningConcentratorContainer container, PlayerInventory inv, ITextComponent textComponent) {
        super(container, inv, textComponent);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrix, mouseX, mouseY);
    }

    public float formatFillState() {
        int fillState = container.getFillState();

        float newState = fillState / 100;

        float newState2 = newState / 10;

        return newState2;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bindTexture(guiLocation);
        int guiLeft = this.guiLeft;
        int guiTop = this.guiTop;
        this.blit(matrixStack, guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
        boolean ready = container.isFull();
        boolean warmingUp = !ready && container.getFillState() > 1;

        this.font.drawString(matrixStack,
                ready ? "Ready!" : warmingUp ? "Warming Up" : "Not Ready",
                guiLeft + 10,
                guiTop + 20,
                ready ? Color.fromHex("#00ff00").getColor() :
                        warmingUp ? Color.fromHex("#ff9900").getColor() :
                                Color.fromHex("#ff0000").getColor());

        this.font.drawString(matrixStack,
                formatFillState() + "/10 PU",
                guiLeft + 10, guiTop + 40,
                Color.fromHex("#555555").getColor());
    }
}
