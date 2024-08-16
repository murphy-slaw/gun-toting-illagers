package net.funkpla.guntotingillagers;

import ewewukek.musketmod.GunItem;
import ewewukek.musketmod.Items;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Musketeer extends Pillager {
    public Musketeer(EntityType<? extends Pillager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem projectileWeapon) {
        return projectileWeapon == Items.PISTOL;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.PISTOL));
    }

    @Override
    public AbstractIllager.@NotNull IllagerArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
        }
        if (this.isHolding(Items.PISTOL)) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
        }
        if (this.isAggressive()) {
            return AbstractIllager.IllagerArmPose.ATTACKING;
        }
        return AbstractIllager.IllagerArmPose.NEUTRAL;
    }

    @Override
    protected void enchantSpawnedWeapon(RandomSource random, float chanceMultiplier) {
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        this.performPistolAttack();
    }

    public Vec3 getTargetVector() {
        LivingEntity target = this.getTarget();
        assert target != null;
        double x = target.getX() - this.getX();
        double z = target.getZ() - this.getZ();
        double f = Math.sqrt(x * x + z * z);
        double y = target.getY(0.3333333333333333) - this.getY() + f * (double) 0.2f;
        return new Vec3(x, y, z);
    }

    public void performPistolAttack() {
        InteractionHand interactionHand = ProjectileUtil.getWeaponHoldingHand(this, Items.PISTOL);
        ItemStack itemStack = this.getItemInHand(interactionHand);
        if (this.isHolding(Items.PISTOL)) {
            GunItem gun = (GunItem) itemStack.getItem();
            gun.fire(this, this.getTargetVector());
        }
    }
}