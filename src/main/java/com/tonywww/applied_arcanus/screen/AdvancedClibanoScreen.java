package com.tonywww.applied_arcanus.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tonywww.applied_arcanus.AppliedArcanus;
import com.tonywww.applied_arcanus.menu.AdvancedClibanoMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AdvancedClibanoScreen extends AbstractContainerScreen<AdvancedClibanoMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AppliedArcanus.MODID, "textures/gui/advanced_clibano.png");

    public AdvancedClibanoScreen(AdvancedClibanoMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // 绘制进度箭头
        if (menu.getCookingTime() > 0) {
            int progress = menu.getCookingProgress();
            int totalTime = menu.getCookingTime();
            int arrowWidth = progress * 24 / totalTime;
            guiGraphics.blit(TEXTURE, x + 79, y + 35, 176, 14, arrowWidth, 17);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}