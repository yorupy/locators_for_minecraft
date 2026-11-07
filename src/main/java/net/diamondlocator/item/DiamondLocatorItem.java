package net.diamondlocator.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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

public class DiamondLocatorItem extends Item {
    public DiamondLocatorItem(Properties properties) {
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
            
            BlockPos closestDiamond = null;
            double shortestDistance = Double.MAX_VALUE;

            // 1. Ищем БЛИЖАЙШИЙ алмаз в радиусе
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos checkPos = playerPos.offset(x, y, z);
                        BlockState state = serverLevel.getBlockState(checkPos);

                        if (state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)) {
                            double dist = playerPos.distSqr(checkPos);
                            if (dist < shortestDistance) {
                                shortestDistance = dist;
                                closestDiamond = checkPos;
                            }
                        }
                    }
                }
            }

            // 2. Если алмаз найден, строим линию из частиц
            if (closestDiamond != null) {
                // Точка старта — на уровне глаз игрока
                Vec3 start = new Vec3(player.getX(), player.getEyeY() - 0.2, player.getZ());
                // Точка финиша — центр блока руды
                Vec3 end = new Vec3(closestDiamond.getX() + 0.5, closestDiamond.getY() + 0.5, closestDiamond.getZ() + 0.5);
                
                Vec3 direction = end.subtract(start);
                double distance = direction.length();
                direction = direction.normalize();

                // Спавним частицы вдоль линии каждые 0.5 блоков
                for (double d = 0.5; d < distance; d += 0.5) {
                    Vec3 particlePos = start.add(direction.scale(d));
                    
                    serverLevel.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME, // Бирюзовые огоньки укажут путь
                        particlePos.x, particlePos.y, particlePos.z, 
                        1, 0.0, 0.0, 0.0, 0.0
                    );
                }

                // Звук успеха
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                        SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.3F);
            } else {
                // Звук неудачи (бас)
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                        SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.PLAYERS, 1.0F, 0.6F);
            }

            // Перезарядка 5 секунд
            player.getCooldowns().addCooldown(this, 100);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}