package net.funkpla.guntotingillagers;

import ewewukek.musketmod.GunItem;
import ewewukek.musketmod.Items;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Musketeer extends Pillager implements PistolAttackMob {

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
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeGoal(new RangedCrossbowAttackGoal<Pillager>(this, 1.0, 8.0f));
        this.goalSelector.removeGoal(new Raider.HoldGroundAttackGoal(this, 10.0f));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Raider.class, 4.0f, 1.0, 1.2));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 4.0f, 1.0, 1.2));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, IronGolem.class, 4.0f, 1.0, 1.2));
        this.goalSelector.addGoal(3, new RangedPistolAttackGoal<>(this, 1.0, 16.0f));
    }

    @Override
    public AbstractIllager.@NotNull IllagerArmPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
        }
        if (this.isPistolLoaded()){
            return IllagerArmPose.CROSSBOW_HOLD;
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
        LivingEntity target = getTarget();
        double vecY = target.getBoundingBox().minY + target.getBbHeight() * 0.7f - this.getY() - this.getEyeHeight();
        return new Vec3(target.getX() - this.getX(), vecY, target.getZ() - this.getZ()).normalize();
    }

    public boolean hasAmmo(){
        return true;
    }

    public boolean isPistolLoaded(){
        if (isHolding(Items.PISTOL)){
            ItemStack pistol = getPistol();
            return GunItem.isLoaded(pistol);
        }
        return false;

    }
    private ItemStack getPistol(){
        InteractionHand interactionHand = ProjectileUtil.getWeaponHoldingHand(this, Items.PISTOL);
        return this.getItemInHand(interactionHand);
    }

    public void performPistolAttack() {
        if (isHolding(Items.PISTOL)) {
            GunItem gun = (GunItem) getPistol().getItem();
            gun.fire(this, this.getTargetVector());
            playSound(gun.fireSound(), 3.5F, 1.0F);
        }
    }
}