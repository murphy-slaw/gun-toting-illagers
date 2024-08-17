package net.funkpla.guntotingillagers;
import java.util.EnumSet;

import ewewukek.musketmod.GunItem;
import ewewukek.musketmod.Sounds;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import ewewukek.musketmod.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class RangedPistolAttackGoal<T extends Monster & PistolAttackMob>
        extends Goal {
    public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
    private final T mob;
    private PistolState pistolState = PistolState.UNLOADED;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public RangedPistolAttackGoal(T mob, double speedModifier, float attackRadius) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingPistol();
    }

    private boolean isHoldingPistol() {
        return this.mob.isHolding(Items.PISTOL);
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingPistol() && this.mob.hasAmmo();
    }

    private boolean isValidTarget() {
        return mob.getTarget() != null && mob.getTarget().isAlive();
    }

    @Override
    public void stop() {

        super.stop();
        this.mob.setAggressive(false);
        this.mob.setTarget(null);
        this.seeTime = 0;
        if (this.mob.isUsingItem()) {
            this.mob.stopUsingItem();
            ((CrossbowAttackMob)this.mob).setChargingCrossbow(false);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return;
        }
        this.mob.getLookControl().setLookAt(target, 10.0f, 30.0f);
        boolean canSee = this.mob.getSensing().hasLineOfSight(target);
        boolean hasSeen = this.seeTime > 0;
        if (canSee != hasSeen) {
            this.seeTime = 0;
        }
        this.seeTime = canSee ? this.seeTime + 1  : this.seeTime - 1;
        double d = this.mob.distanceToSqr(target);
        boolean notReady = (d > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
        if (notReady) {
            --this.updatePathDelay;
            if (this.updatePathDelay <= 0) {
                this.mob.getNavigation().moveTo(target, this.canRun() ? this.speedModifier : this.speedModifier * 0.5);
                this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
            }
        } else {
            this.updatePathDelay = 0;
            this.mob.getNavigation().stop();
        }
        InteractionHand gunHand = ProjectileUtil.getWeaponHoldingHand(this.mob,Items.PISTOL);
        ItemStack gunStack = this.mob.getItemInHand(gunHand);
        if (this.pistolState == PistolState.UNLOADED){
            if (!notReady) {
                this.mob.startUsingItem(gunHand);
                this.pistolState = PistolState.LOADING;
                ((CrossbowAttackMob)this.mob).setChargingCrossbow(true);
            }
        } else if (this.pistolState == PistolState.LOADING){
            if (!this.mob.isUsingItem()) {
                ((CrossbowAttackMob)this.mob).setChargingCrossbow(false);
                this.pistolState = PistolState.UNLOADED;
            }
            if (GunItem.isLoaded(gunStack)){
                this.mob.releaseUsingItem();
                this.mob.playSound(Sounds.MUSKET_READY);
                this.pistolState = PistolState.LOADED;
                ((CrossbowAttackMob)this.mob).setChargingCrossbow(false);
                this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
            }
        } else if (this.pistolState == PistolState.LOADED){
            --this.attackDelay;
            if (this.attackDelay == 0) {
                this.pistolState = PistolState.READY_TO_ATTACK;
            }
        } else if (this.pistolState == PistolState.READY_TO_ATTACK && canSee) {
            AABB aABB = target.getBoundingBox().expandTowards(this.mob.position());
            HitResult hit = ProjectileUtil.getEntityHitResult(this.mob,this.mob.position(),target.position(),aABB, entity -> (!entity.isAlliedTo(this.mob)),32.0);
            if (hit == null) {
                this.mob.performRangedAttack(target, 1.0f);
                this.pistolState = PistolState.UNLOADED;
                GunItem.setLoaded(gunStack, false);
            }
        }
    }

    private boolean canRun() {
        return this.pistolState == PistolState.UNLOADED;
    }

    enum PistolState {
        UNLOADED,
        LOADING,
        LOADED,
        READY_TO_ATTACK
    }
}


