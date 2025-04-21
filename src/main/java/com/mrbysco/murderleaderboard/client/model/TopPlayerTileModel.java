package com.mrbysco.murderleaderboard.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

public class TopPlayerTileModel extends PlayerModel {
	public TopPlayerTileModel(ModelPart part, boolean slim) {
		super(part, slim);
		this.hat.setRotation(0.0F, -1.75F, 0.0F);
		this.rightSleeve.setRotation(-5.0F, 2.0F, 0.0F);
	}


	public static MeshDefinition createPlayerMesh(CubeDeformation cubeDeformation, boolean slim) {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, 0.0F);
		PartDefinition partdefinition = meshdefinition.getRoot();
		float f = 0.25F;
		if (slim) {
			PartDefinition partdefinition1 = partdefinition.addOrReplaceChild(
					"left_arm",
					CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation),
					PartPose.offset(5.0F, 2.0F, 0.0F)
			);
			PartDefinition partdefinition2 = partdefinition.addOrReplaceChild(
					"right_arm",
					CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation),
					PartPose.offset(-5.0F, 2.0F, 0.0F)
			);
			partdefinition1.addOrReplaceChild(
					"left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.ZERO
			);
			partdefinition2.addOrReplaceChild(
					"right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.ZERO
			);
		} else {
			PartDefinition partdefinition4 = partdefinition.addOrReplaceChild(
					"left_arm",
					CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation),
					PartPose.offset(5.0F, 2.0F, 0.0F)
			);
			PartDefinition partdefinition6 = partdefinition.getChild("right_arm");
			partdefinition4.addOrReplaceChild(
					"left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.ZERO
			);
			partdefinition6.addOrReplaceChild(
					"right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.ZERO
			);
		}

		PartDefinition partdefinition5 = partdefinition.addOrReplaceChild(
				"left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(1.9F, 12.0F, 0.0F)
		);
		PartDefinition partdefinition7 = partdefinition.getChild("right_leg");
		partdefinition5.addOrReplaceChild(
				"left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.ZERO
		);
		partdefinition7.addOrReplaceChild(
				"right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.ZERO
		);
		PartDefinition partdefinition3 = partdefinition.getChild("body");
		partdefinition3.addOrReplaceChild(
				"jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.ZERO
		);
		return meshdefinition;
	}

	@Override
	public void setupAnim(PlayerRenderState p_365286_) {
		super.setupAnim(p_365286_);
//		this.setAllVisible(true);
//		this.hat.visible = true;
//		this.jacket.visible = true;
//		this.leftPants.visible = true;
//		this.rightPants.visible = true;
//		this.leftSleeve.visible = true;
//		this.rightSleeve.visible = true;
	}
}
