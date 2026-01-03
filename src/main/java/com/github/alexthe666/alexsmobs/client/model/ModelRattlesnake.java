package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

public class ModelRattlesnake extends AdvancedEntityModel<EntityRattlesnake> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail1;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox neck1;
    private final AdvancedModelBox neck2;
    private final AdvancedModelBox head;
    private final AdvancedModelBox tongue;

    public ModelRattlesnake() {
        texWidth = 64;
        texHeight = 64;

        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, 24.0F, 0.0F);
        body.setTextureOffset(0, 0).addBox(-2.0F, -3.0F, -4.0F, 4.0F, 3.0F, 7.0F, 0.0F, false);

        tail1 = new AdvancedModelBox(this, "tail1");
        tail1.setPos(0.0F, -1.75F, 2.95F);
        body.addChild(tail1);
        tail1.setTextureOffset(0, 11).addBox(-1.5F, -1.25F, 0.05F, 3.0F, 3.0F, 7.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this, "tail2");
        tail2.setPos(0.0F, 0.45F, 7.05F);
        tail1.addChild(tail2);
        tail2.setTextureOffset(15, 16).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);

        neck1 = new AdvancedModelBox(this, "neck1");
        neck1.setPos(0.0F, -1.5F, -4.0F);
        body.addChild(neck1);
        neck1.setTextureOffset(18, 6).addBox(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 5.0F, 0.0F, false);

        neck2 = new AdvancedModelBox(this, "neck2");
        neck2.setPos(0.0F, 0.0F, -4.9F);
        neck1.addChild(neck2);
        neck2.setTextureOffset(12, 25).addBox(-1.0F, -1.5F, -5.1F, 2.0F, 3.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, 0.0F, -5.0F);
        neck2.addChild(head);
        head.setTextureOffset(0, 22).addBox(-2.0F, -1.0F, -3.8F, 4.0F, 2.0F, 4.0F, 0.0F, false);

        tongue = new AdvancedModelBox(this, "tongue");
        tongue.setPos(0.0F, 0.0F, -3.8F);
        head.addChild(tongue);
        tongue.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int color) {
        if (this.young) {
            float f = 1.75F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.75D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, color);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, color);
            });
            matrixStackIn.popPose();
        }
    }

    @Override
    public void setupAnim(EntityRattlesnake entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        
        float walkSpeed = 0.8F;
        float walkDegree = 0.3F;
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        float curlProgress = entity.prevCurlProgress + (entity.curlProgress - entity.prevCurlProgress) * partialTick;
        
        boolean isBiting = entity.getAnimation() == EntityRattlesnake.ANIMATION_BITE;
        int biteTick = isBiting ? entity.getAnimationTick() : 0;
        
        // Calculate curl fade - fade out curl during first 5 ticks of bite
        float curlFade = 1.0F;
        if (isBiting && biteTick < 5) {
            curlFade = 1.0F - (biteTick / 5.0F);
        } else if (isBiting) {
            curlFade = 0.0F;
        }
        
        float effectiveCurl = curlProgress * curlFade;
        if (effectiveCurl > 0) {
            progressPositionPrev(body, effectiveCurl, 0, 0, 3, 5F);
            progressRotationPrev(body, effectiveCurl, 0, Maths.rad(-90), 0, 5F);
            progressRotationPrev(tail1, effectiveCurl, Maths.rad(-10), Maths.rad(-70), 0, 5F);
            progressRotationPrev(neck1, effectiveCurl, Maths.rad(-20), Maths.rad(60), 0, 5F);
            
            if (entity.isRattling() && !isBiting) {
                progressRotationPrev(tail2, effectiveCurl, Maths.rad(70), Maths.rad(-60), 0, 5F);
                this.walk(tail2, 18, 0.1F, false, 1F, 0.2F, ageInTicks, 1);
                this.swing(tail2, 18, 0.1F, false, 0f, 0f, ageInTicks, 1);
            } else {
                progressRotationPrev(tail2, effectiveCurl, Maths.rad(10), Maths.rad(-90), 0, 5F);
            }
        }
        
        if (isBiting) {
            float biteBlend = Math.min(1.0F, biteTick / 5.0F);
            
            if (biteTick < 7) {
                float windUp = biteTick / 7.0F;
                body.rotationPointZ += 2.0F * windUp * biteBlend;
                neck1.rotateAngleX += Maths.rad(-45) * windUp * biteBlend;
                neck2.rotateAngleX += Maths.rad(15) * windUp * biteBlend;
                tail1.rotateAngleY += Maths.rad(15) * windUp * biteBlend;
                tail2.rotateAngleY += Maths.rad(-15) * windUp * biteBlend;
                head.rotateAngleX += Maths.rad(20) * windUp * biteBlend;
            } else if (biteTick < 12) {
                float strikeProgress = (biteTick - 7) / 5.0F;
                body.rotationPointZ += 2.0F - (4.0F * strikeProgress);
                neck1.rotateAngleX += Maths.rad(-45) * (1.0F - strikeProgress);
                neck2.rotateAngleX += Maths.rad(15) * (1.0F - strikeProgress);
                tail1.rotateAngleY += Maths.rad(15) * (1.0F - strikeProgress);
                tail2.rotateAngleY += Maths.rad(-15) * (1.0F - strikeProgress);
                head.rotateAngleX += Maths.rad(20) * (1.0F - strikeProgress);
            } else if (biteTick < 15) {
                float resetProgress = (biteTick - 12) / 3.0F;
                body.rotationPointZ += -2.0F * (1.0F - resetProgress);
            }
        }
        
        if (entity.randomToungeTick > 0) {
            tongue.showModel = true;
            this.walk(tongue, 1, 0.5F, false, 1F, 0f, ageInTicks, 1);
        } else {
            tongue.showModel = false;
        }
        
        if (curlProgress < 0.5F && limbSwingAmount > 0.01F && !isBiting) {
            this.swing(body, walkSpeed, walkDegree * 0.5F, false, 0F, 0F, limbSwing, limbSwingAmount);
            this.swing(neck1, walkSpeed, walkDegree * 0.4F, false, 1F, 0F, limbSwing, limbSwingAmount);
            this.swing(tail1, walkSpeed, walkDegree * 0.6F, false, -1F, 0F, limbSwing, limbSwingAmount);
            this.swing(tail2, walkSpeed, walkDegree * 0.5F, false, -2F, 0F, limbSwing, limbSwingAmount);
        }
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, tail1, tail2, neck1, neck2, head, tongue);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}