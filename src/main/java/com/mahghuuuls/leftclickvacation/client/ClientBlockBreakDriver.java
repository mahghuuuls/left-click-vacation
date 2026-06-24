package com.mahghuuuls.leftclickvacation.client;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientBlockBreakDriver {

    private final ClientAutomationController automationController;
    private boolean wasDrivingBlock;

    public ClientBlockBreakDriver(ClientAutomationController automationController) {
        this.automationController = automationController;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (!automationController.canDriveBlockBreaking()) {
            resetDrivenBlockBreaking(minecraft);
            return;
        }

        driveCurrentBlockTarget(minecraft);
    }

    private void driveCurrentBlockTarget(Minecraft minecraft) {
        EntityPlayerSP player = minecraft.player;
        if (player == null || minecraft.world == null || minecraft.playerController == null
                || minecraft.currentScreen != null || player.isHandActive()) {
            resetDrivenBlockBreaking(minecraft);
            return;
        }

        RayTraceResult hit = minecraft.objectMouseOver;
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            resetDrivenBlockBreaking(minecraft);
            return;
        }

        BlockPos pos = hit.getBlockPos();
        if (pos == null || minecraft.world.isAirBlock(pos) || !isBreakableBlockTarget(minecraft, pos)) {
            resetDrivenBlockBreaking(minecraft);
            return;
        }

        if (minecraft.playerController.onPlayerDamageBlock(pos, hit.sideHit)) {
            wasDrivingBlock = true;
            minecraft.effectRenderer.addBlockHitEffects(pos, hit);
            player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    private boolean isBreakableBlockTarget(Minecraft minecraft, BlockPos pos) {
        IBlockState state = minecraft.world.getBlockState(pos);
        Material material = state.getMaterial();
        return material != Material.AIR && !material.isLiquid()
                && state.getBlockHardness(minecraft.world, pos) >= 0.0F;
    }

    private void resetBlockBreaking(Minecraft minecraft) {
        if (minecraft.playerController != null) {
            minecraft.playerController.resetBlockRemoving();
        }
    }

    private void resetDrivenBlockBreaking(Minecraft minecraft) {
        if (wasDrivingBlock) {
            resetBlockBreaking(minecraft);
            wasDrivingBlock = false;
        }
    }

}
