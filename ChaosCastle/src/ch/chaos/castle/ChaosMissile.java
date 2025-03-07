package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Memory;
import ch.pitchtech.modula.runtime.Runtime;
import java.util.EnumSet;


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
            chaosActions.SetObjLoc(missile, (short) 78, (short) 67, (short) 7, (short) 7);
        else if (missile.stat == 4)
            chaosActions.SetObjLoc(missile, (short) 92, (short) 180, (short) 7, (short) 7);
        else
            chaosActions.SetObjLoc(missile, (short) (missile.stat * 7 + 131), (short) 65, (short) 7, (short) 7);
        chaosActions.SetObjRect(missile, 0, 0, 7, 7);
    }

    private void MakeMissile2(ChaosBase.Obj missile) {
        if (missile.stat >= 2)
            missile.stat = 0;
        else
            missile.stat++;
        chaosActions.SetObjLoc(missile, (short) (missile.stat * 11 + 78), (short) 67, (short) 7, (short) 7);
        chaosActions.SetObjRect(missile, 0, 0, 7, 7);
    }

    private void MakeMissile3(ChaosBase.Obj missile) {
        if (missile.stat >= 2)
            missile.stat = 0;
        else
            missile.stat++;
        chaosActions.SetObjLoc(missile, (short) (missile.stat * 11 + 76), (short) 65, (short) 11, (short) 11);
        chaosActions.SetObjRect(missile, 0, 0, 11, 11);
    }

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
            chaosActions.Boum(missile, ss, (short) ChaosBase.slowStyle, (short) cnt, (short) 1);
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

    private void MoveMissile1(ChaosBase.Obj missile) {
        MoveMissile(missile, EnumSet.of(Anims.PLAYER, Anims.ALIEN1), Runtime.proc(chaosActions::Aie, "ChaosActions.Aie"));
    }

    private void MoveMissile2(ChaosBase.Obj missile) {
        MoveMissile(missile, EnumSet.of(Anims.PLAYER, Anims.ALIEN1, Anims.ALIEN2), Runtime.proc(chaosActions::Aie, "ChaosActions.Aie"));
    }

    private void MoveMissile3(ChaosBase.Obj missile) {
        MoveMissile(missile, EnumSet.of(Anims.ALIEN1, Anims.ALIEN2, Anims.ALIEN3, Anims.MACHINE), Runtime.proc(this::KillIt, "ChaosMissile.KillIt"));
    }

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;

        chaosSounds.SetEffect(missileDieEffect[0], chaosSounds.soundList[SoundList.wCrash.ordinal()], 1673, 16726, (short) 30, (short) 0);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetMissile, "ChaosMissile.ResetMissile");
        attr.Make = Runtime.proc(this::MakeMissile1, "ChaosMissile.MakeMissile1");
        attr.Move = Runtime.proc(this::MoveMissile1, "ChaosMissile.MoveMissile1");
        attr.weight = 18;
        attr.charge = 120;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 20;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.MISSILE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetMissile, "ChaosMissile.ResetMissile");
        attr.Make = Runtime.proc(this::MakeMissile2, "ChaosMissile.MakeMissile2");
        attr.Move = Runtime.proc(this::MoveMissile2, "ChaosMissile.MoveMissile2");
        attr.weight = 30;
        attr.charge = 90;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 22;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.MISSILE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetMissile, "ChaosMissile.ResetMissile");
        attr.Make = Runtime.proc(this::MakeMissile3, "ChaosMissile.MakeMissile3");
        attr.Move = Runtime.proc(this::MoveMissile3, "ChaosMissile.MoveMissile3");
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
