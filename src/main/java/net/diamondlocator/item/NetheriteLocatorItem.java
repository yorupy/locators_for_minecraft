package net.diamondlocator.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class NetheriteLocatorItem extends Item {
    public NetheriteLocatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            BlockPos playerPos = player.blockPosition();
            int radius = 10;
            
            BlockPos closestDebris = null;
            double shortestDistance = Double.MAX_VALUE;

            // 1. Ищем БЛИЖАЙШИЕ древние обломки
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos checkPos = playerPos.offset(x, y, z);
                        BlockState state = serverLevel.getBlockState(checkPos);

                        if (state.is(Blocks.ANCIENT_DEBRIS)) {
                            double dist = playerPos.distSqr(checkPos);
                            if (dist < shortestDistance) {
                                shortestDistance = dist;
                                closestDebris = checkPos;
                            }
                        }
                    }
                }
            }

            // 2. Если обломки найдены, строим линию из КРАСНЫХ частиц
            if (closestDebris != null) {
                Vec3 start = new Vec3(player.getX(), player.getEyeY() - 0.2, player.getZ());
                Vec3 end = new Vec3(closestDebris.getX() + 0.5, closestDebris.getY() + 0.5, closestDebris.getZ() + 0.5);
                
                Vec3 direction = end.subtract(start);
                double distance = direction.length();
                direction = direction.normalize();

                // Создаем параметры для красной пыли (RGB: 1.0, 0.0, 0.0), размер частиц — 1.5
                DustParticleOptions redDust = new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), 1.5f);

                for (double d = 0.5; d < distance; d += 0.5) {
                    Vec3 particlePos = start.add(direction.scale(d));
                    
                    serverLevel.sendParticles(
                        redDust, 
                        particlePos.x, particlePos.y, particlePos.z, 
                        1, 0.0, 0.0, 0.0, 0.0
                    );
                }

                // Магический звук незера
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                        SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 1.0F, 1.2F);
            } else {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                        SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.PLAYERS, 1.0F, 0.6F);
            }

            player.getCooldowns().addCooldown(this, 100);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}