package com.tonywww.applied_arcanus.screen;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.StyleManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tonywww.applied_arcanus.AppliedArcanus;
import com.tonywww.applied_arcanus.menu.AdvancedClibanoMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AdvancedClibanoScreen extends UpgradeableScreen<AdvancedClibanoMenu>
{
    private static final ResourceLocation TEXTURE = AppliedArcanus.makeId("textures/gui/advanced_clibano.png");

    public AdvancedClibanoScreen(AdvancedClibanoMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, StyleManager.loadStyleDoc("/screens/advanced_clibano_menu.json"));
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY)
    {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // 绘制进度箭头
        if (menu.cookingTime > 0) {
            int progress = menu.cookingProgress;
            int totalTime = menu.cookingTime;
            int arrowWidth = progress * 24 / totalTime;
            guiGraphics.blit(TEXTURE, x + 79, y + 35, 176, 14, arrowWidth, 17);
        }
    }
}