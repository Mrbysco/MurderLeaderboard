package com.mrbysco.murderleaderboard.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class TopPlayerTileModel<T extends LivingEntity> extends PlayerModel<T> {
	public TopPlayerTileModel(ModelPart part, boolean slim) {
		super(part, slim);
		this.hat.setPos(0.0F, -1.75F, 0.0F);
		this.rightSleeve.setPos(-5.0F, 2.0F, 0.0F);
	}

	public static MeshDefinition createPlayerMesh(CubeDeformation cubeDeformation, boolean slim) {
		MeshDefinition meshdefinition = PlayerModel.createMesh(cubeDeformation, slim);
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation.extend(2.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		if (slim) {
			partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(-5.0F, 2.5F, 0.0F));
		} else {
			partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
		}
		return meshdefinition;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.setAllVisible(true);
		this.hat.visible = true;
		this.jacket.visible = true;
		this.leftPants.visible = true;
		this.rightPants.visible = true;
		this.leftSleeve.visible = true;
		this.rightSleeve.visible = true;
		super.renderToBuffer(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
