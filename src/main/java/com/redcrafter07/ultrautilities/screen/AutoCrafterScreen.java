package com.redcrafter07.ultrautilities.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.redcrafter07.ultrautilities.UltraUtilities;
import com.redcrafter07.ultrautilities.container.AutoCrafterContainer;
import com.redcrafter07.ultrautilities.container.CraftingStationContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class AutoCrafterScreen extends ContainerScreen<AutoCrafterContainer> {
    private final ResourceLocation guiLocation = new ResourceLocation(UltraUtilities.MOD_ID, "textures/gui/crafting_station.png");

    public AutoCrafterScreen(AutoCrafterContainer container, PlayerInventory inv, ITextComponent textComponent) {
        super(container, inv, textComponent);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f ,1f, 1f);
        this.minecraft.getTextureManager().bindTexture(guiLocation);
        int guiLeft = this.guiLeft;
        int guiTop = this.guiTop;
        this.blit(matrixStack, guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    }
}
