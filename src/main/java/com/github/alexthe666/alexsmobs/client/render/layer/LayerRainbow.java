package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.item.ItemRainbowJelly;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class LayerRainbow<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private final RenderLayerParent<T, M> parent;

    @SuppressWarnings("unchecked")
    public LayerRainbow(RenderLayerParent<?, ?> parent) {
        super((RenderLayerParent<T, M>) parent);
        this.parent = (RenderLayerParent<T, M>) parent;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        int i = RainbowUtil.getRainbowType(entity);
        if(i > 0) {
            ItemRainbowJelly.RainbowType rainbowType = ItemRainbowJelly.RainbowType.values()[Mth.clamp(i - 1, 0,ItemRainbowJelly.RainbowType.values().length - 1)];
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(getRenderType(rainbowType));
            float alpha = 0.5F;
            matrixStackIn.pushPose();
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0), -1);
            matrixStackIn.popPose();
        }
    }

    private RenderType getRenderType(ItemRainbowJelly.RainbowType rainbowType) {
        return switch (rainbowType) {
            case TRANS -> AMRenderTypes.TRANS_GLINT;
            case NONBI -> AMRenderTypes.NONBI_GLINT;
            case BI -> AMRenderTypes.BI_GLINT;
            case ACE -> AMRenderTypes.ACE_GLINT;
            case WEEZER -> AMRenderTypes.WEEZER_GLINT;
            case BRAZIL -> AMRenderTypes.BRAZIL_GLINT;
            default -> AMRenderTypes.RAINBOW_GLINT;
        };
    }
}
