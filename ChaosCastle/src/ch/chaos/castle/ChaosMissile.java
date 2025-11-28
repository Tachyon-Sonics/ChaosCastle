package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Memory;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosMissile {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Memory memory;


    private ChaosMissile() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        memory = Memory.instance();
    }


    // CONST

    public static final int mAlien1 = 0;
    public static final int mAlien2 = 1;
    public static final int mAlien3 = 2;
    public static final int mBlue = 0;
    public static final int mRed = 1;
    public static final int mYellow = 2;
    public static final int mWhite = 3;
    public static final int mGreen = 4;
    /* cycle */
    public static final int mAcc2 = 0;
    public static final int mBig = 0;


    // VAR

    private ChaosSounds.Effect[] missileDieEffect = Runtime.initArray(new ChaosSounds.Effect[1]);


    public ChaosSounds.Effect[] getMissileDieEffect() {
        return this.missileDieEffect;
    }

    public void setMissileDieEffect(ChaosSounds.Effect[] missileDieEffect) {
        this.missileDieEffect = missileDieEffect;
    }


    // PROCEDURE

    private void MakeMissile1(ChaosBase.Obj missile) {
        if (missile.stat == 3)
            chaosActions.SetObjLoc(missile, 78, 67, 7, 7);
        else if (missile.stat == 4)
            chaosActions.SetObjLoc(missile, 92, 180, 7, 7);
        else
            chaosActions.SetObjLoc(missile, missile.stat * 7 + 131, 65, 7, 7);
        chaosActions.SetObjRect(missile, 0, 0, 7, 7);
    }

    private final ChaosBase.MakeProc MakeMissile1_ref = this::MakeMissile1;

    private void MakeMissile2(ChaosBase.Obj missile) {
        if (missile.stat >= 2)
            missile.stat = 0;
        else
            missile.stat++;
        chaosActions.SetObjLoc(missile, missile.stat * 11 + 78, 67, 7, 7);
        chaosActions.SetObjRect(missile, 0, 0, 7, 7);
    }

    private final ChaosBase.MakeProc MakeMissile2_ref = this::MakeMissile2;

    private void MakeMissile3(ChaosBase.Obj missile) {
        if (missile.stat >= 2)
            missile.stat = 0;
        else
            missile.stat++;
        chaosActions.SetObjLoc(missile, missile.stat * 11 + 76, 65, 11, 11);
        chaosActions.SetObjRect(missile, 0, 0, 11, 11);
    }

    private final ChaosBase.MakeProc MakeMissile3_ref = this::MakeMissile3;

    private void ResetMissile(ChaosBase.Obj missile) {
        missile.shapeSeq = ChaosBase.Period / 10;
        missile.moveSeq = ChaosBase.Period * 20;
        if (missile.subKind == mYellow)
            missile.fireSubLife = missile.life * 2 / 3;
        else if (missile.subKind == mRed)
            missile.fireSubLife = missile.life / 2;
        else
            missile.fireSubLife = missile.life / 3;
        missile.hitSubLife = missile.life - missile.fireSubLife;
        missile.attr.Make.invoke(missile);
    }

    private final ChaosBase.ResetProc ResetMissile_ref = this::ResetMissile;

    private void MoveMissile(ChaosBase.Obj missile, EnumSet<Anims> victims, ChaosBase.AieProc proc) {
        // VAR
        EnumSet<Stones> ss = EnumSet.noneOf(Stones.class);
        int cnt = 0;

        chaosActions.UpdateXY(missile);
        if (missile.subKind != mAlien1) {
            if (chaosBase.step >= missile.shapeSeq) {
                missile.attr.Make.invoke(missile);
                missile.shapeSeq += ChaosBase.Period / 10;
            }
            if (chaosBase.step >= missile.shapeSeq)
                missile.shapeSeq = 0;
            else
                missile.shapeSeq -= chaosBase.step;
        }
        if (chaosActions.InBackground(missile)) {
            if (missile.subKind == mAlien3) {
                ss = EnumSet.of(Stones.stFOG3);
                cnt = 20;
            } else {
                ss = EnumSet.of(Stones.stFLAME2);
                cnt = missile.subKind + 1;
            }
            chaosActions.Boum(missile, ss, ChaosBase.slowStyle, cnt, 1);
            chaosSounds.SoundEffect(missile, missileDieEffect);
            chaosActions.Die(missile);
            return;
        } else if (chaosActions.OutOfScreen(missile) || (chaosActions.OutOfBounds(missile) && (missile.subKind != mAlien2)) || (chaosBase.step > missile.moveSeq) || ((missile.subKind == mAlien1) && (chaosBase.noMissile > 0))) {
            chaosActions.Die(missile);
            return;
        } else {
            missile.moveSeq -= chaosBase.step;
        }
        chaosActions.DoCollision(missile, victims, proc, new Runtime.FieldRef<>(missile::getHitSubLife, missile::setHitSubLife), new Runtime.FieldRef<>(missile::getFireSubLife, missile::setFireSubLife));
        if (missile.life != missile.hitSubLife + missile.fireSubLife)
            chaosActions.Die(missile);
    }

    private void KillIt(ChaosBase.Obj victim, ChaosBase.Obj src, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        chaosActions.Die(victim);
    }

    private final ChaosBase.AieProc KillIt_ref = this::KillIt;

    private void MoveMissile1(ChaosBase.Obj missile) {
        MoveMissile(missile, EnumSet.of(Anims.PLAYER, Anims.ALIEN1), chaosActions.Aie_ref);
    }

    private final ChaosBase.MoveProc MoveMissile1_ref = this::MoveMissile1;

    private void MoveMissile2(ChaosBase.Obj missile) {
        MoveMissile(missile, EnumSet.of(Anims.PLAYER, Anims.ALIEN1, Anims.ALIEN2), chaosActions.Aie_ref);
    }

    private final ChaosBase.MoveProc MoveMissile2_ref = this::MoveMissile2;

    private void MoveMissile3(ChaosBase.Obj missile) {
        MoveMissile(missile, EnumSet.of(Anims.ALIEN1, Anims.ALIEN2, Anims.ALIEN3, Anims.MACHINE), KillIt_ref);
    }

    private final ChaosBase.MoveProc MoveMissile3_ref = this::MoveMissile3;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;

        chaosSounds.SetEffect(missileDieEffect[0], chaosSounds.soundList[SoundList.wCrash.ordinal()], 1673, 16726, 30, 0);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetMissile_ref;
        attr.Make = MakeMissile1_ref;
        attr.Move = MoveMissile1_ref;
        attr.weight = 18;
        attr.charge = 120;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 20;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.MISSILE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetMissile_ref;
        attr.Make = MakeMissile2_ref;
        attr.Move = MoveMissile2_ref;
        attr.weight = 30;
        attr.charge = 90;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 22;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.MISSILE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetMissile_ref;
        attr.Make = MakeMissile3_ref;
        attr.Move = MoveMissile3_ref;
        attr.weight = 100;
        attr.charge = 80;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 24;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.MISSILE.ordinal()], attr.node);
    }


    // Support

    private static ChaosMissile instance;

    public static ChaosMissile instance() {
        if (instance == null)
            new ChaosMissile(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        InitParams();
    }

    public void close() {
    }

}
