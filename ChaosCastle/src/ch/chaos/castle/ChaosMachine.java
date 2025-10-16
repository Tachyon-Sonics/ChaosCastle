package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosMachine {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosMachine() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // CONST

    public static final int mTraverse = 0;
    public static final int mCannon1 = 1;
    public static final int mCannon2 = 2;
    public static final int mCannon3 = 3;
    public static final int mTurret = 4;
    public static final int mReactor = 5;
    public static final int mDoor = 6;


    // VAR

    private ChaosSounds.Effect[] smallFireEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] mediumFireEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] bigFireEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieCannonEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieCannon3Effect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieDoorEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieCannonEffect = Runtime.initArray(new ChaosSounds.Effect[16]);
    private ChaosSounds.Effect[] dieCannon3Effect = Runtime.initArray(new ChaosSounds.Effect[24]);
    private ChaosSounds.Effect[] dieTurretEffect = Runtime.initArray(new ChaosSounds.Effect[16]);


    public ChaosSounds.Effect[] getSmallFireEffect() {
        return this.smallFireEffect;
    }

    public void setSmallFireEffect(ChaosSounds.Effect[] smallFireEffect) {
        this.smallFireEffect = smallFireEffect;
    }

    public ChaosSounds.Effect[] getMediumFireEffect() {
        return this.mediumFireEffect;
    }

    public void setMediumFireEffect(ChaosSounds.Effect[] mediumFireEffect) {
        this.mediumFireEffect = mediumFireEffect;
    }

    public ChaosSounds.Effect[] getBigFireEffect() {
        return this.bigFireEffect;
    }

    public void setBigFireEffect(ChaosSounds.Effect[] bigFireEffect) {
        this.bigFireEffect = bigFireEffect;
    }

    public ChaosSounds.Effect[] getAieCannonEffect() {
        return this.aieCannonEffect;
    }

    public void setAieCannonEffect(ChaosSounds.Effect[] aieCannonEffect) {
        this.aieCannonEffect = aieCannonEffect;
    }

    public ChaosSounds.Effect[] getAieCannon3Effect() {
        return this.aieCannon3Effect;
    }

    public void setAieCannon3Effect(ChaosSounds.Effect[] aieCannon3Effect) {
        this.aieCannon3Effect = aieCannon3Effect;
    }

    public ChaosSounds.Effect[] getDieDoorEffect() {
        return this.dieDoorEffect;
    }

    public void setDieDoorEffect(ChaosSounds.Effect[] dieDoorEffect) {
        this.dieDoorEffect = dieDoorEffect;
    }

    public ChaosSounds.Effect[] getDieCannonEffect() {
        return this.dieCannonEffect;
    }

    public void setDieCannonEffect(ChaosSounds.Effect[] dieCannonEffect) {
        this.dieCannonEffect = dieCannonEffect;
    }

    public ChaosSounds.Effect[] getDieCannon3Effect() {
        return this.dieCannon3Effect;
    }

    public void setDieCannon3Effect(ChaosSounds.Effect[] dieCannon3Effect) {
        this.dieCannon3Effect = dieCannon3Effect;
    }

    public ChaosSounds.Effect[] getDieTurretEffect() {
        return this.dieTurretEffect;
    }

    public void setDieTurretEffect(ChaosSounds.Effect[] dieTurretEffect) {
        this.dieTurretEffect = dieTurretEffect;
    }


    // PROCEDURE

    private void MakeTraverse(ChaosBase.Obj traverse) {
        // VAR
        int px = 0;
        int py = 0;
        int sx = 0;
        int sy = 0;

        if (traverse.stat == 0) {
            px = 109;
            py = 68;
            sx = 19;
            sy = 8;
        } else {
            px = 184;
            py = 53;
            sx = 8;
            sy = 19;
        }
        chaosActions.SetObjLoc(traverse, px, py, sx, sy);
        chaosActions.SetObjRect(traverse, 0, 0, sx, sy);
    }

    private final ChaosBase.MakeProc MakeTraverse_ref = this::MakeTraverse;

    private void MakeCannon1(ChaosBase.Obj cannon) {
        // VAR
        int px = 0;
        int ex = 0;
        int sx = 0;

        if (cannon.stat == 0) {
            px = 0;
            sx = 0;
            ex = 14;
        } else {
            px = 16;
            sx = 2;
            ex = 16;
        }
        chaosActions.SetObjLoc(cannon, px, 212, 16, 28);
        chaosActions.SetObjRect(cannon, sx, 0, ex, 28);
    }

    private final ChaosBase.MakeProc MakeCannon1_ref = this::MakeCannon1;

    private void MakeCannon2(ChaosBase.Obj cannon) {
        // VAR
        int px = 0;
        int sy = 0;
        int ey = 0;

        if (cannon.stat == 0) {
            px = 28;
            sy = 0;
            ey = 14;
        } else {
            px = 0;
            sy = 2;
            ey = 16;
        }
        chaosActions.SetObjLoc(cannon, px, 240, 28, 16);
        chaosActions.SetObjRect(cannon, 0, sy, 28, ey);
    }

    private final ChaosBase.MakeProc MakeCannon2_ref = this::MakeCannon2;

    private void MakeCannon3(ChaosBase.Obj cannon) {
        chaosActions.SetObjLoc(cannon, 152, 180, 20, 20);
        chaosActions.SetObjRect(cannon, 0, 0, 20, 20);
    }

    private final ChaosBase.MakeProc MakeCannon3_ref = this::MakeCannon3;

    private void MakeTurret(ChaosBase.Obj turret) {
        chaosActions.SetObjLoc(turret, 32, 212, 28, 28);
        chaosActions.SetObjRect(turret, 2, 2, 26, 26);
    }

    private final ChaosBase.MakeProc MakeTurret_ref = this::MakeTurret;

    private void MakeReactor(ChaosBase.Obj reactor) {
        chaosActions.SetObjLoc(reactor, 224, 184, 16, 20);
        chaosActions.SetObjRect(reactor, 0, 0, 16, 20);
    }

    private final ChaosBase.MakeProc MakeReactor_ref = this::MakeReactor;

    private void MakeDoor(ChaosBase.Obj door) {
        door.hitSubLife = 0;
        door.fireSubLife = 0;
        door.life = 100;
        chaosActions.SetObjLoc(door, 224, 84, 32, 6);
        chaosActions.SetObjRect(door, 0, -2, 32, 8);
    }

    private final ChaosBase.ResetProc MakeDoor_as_ChaosBase_ResetProc = this::MakeDoor;
    private final ChaosBase.MakeProc MakeDoor_as_ChaosBase_MakeProc = this::MakeDoor;

    private void ResetTraverse(ChaosBase.Obj traverse) {
        traverse.dvx = 0;
        traverse.dvy = 0;
        traverse.stat = traverse.life;
        traverse.life = 1;
        traverse.hitSubLife = 0;
        traverse.fireSubLife = 0;
        if (trigo.RND() % 2 == 0) {
            if (traverse.stat == 1)
                traverse.dvy = 1200;
            else
                traverse.dvx = 1200;
        } else {
            if (traverse.stat == 1)
                traverse.dvy = -1200;
            else
                traverse.dvx = -1200;
        }
        MakeTraverse(traverse);
    }

    private final ChaosBase.ResetProc ResetTraverse_ref = this::ResetTraverse;

    private void ResetCannon(ChaosBase.Obj cannon) {
        cannon.stat = cannon.life;
        cannon.life = 40 + chaosBase.difficulty * 10;
        if (cannon.subKind == mCannon3)
            cannon.life = cannon.life * 2;
        cannon.fireSubLife = cannon.life;
        cannon.hitSubLife = cannon.life;
        cannon.moveSeq = trigo.RND() % ChaosBase.Period;
        cannon.attr.Make.invoke(cannon);
    }

    private final ChaosBase.ResetProc ResetCannon_ref = this::ResetCannon;

    private void ResetTurret(ChaosBase.Obj turret) {
        turret.life = 40 + chaosBase.difficulty * 10;
        turret.fireSubLife = turret.life;
        turret.hitSubLife = turret.life;
        turret.moveSeq = trigo.RND() % (ChaosBase.Period / 4);
        turret.shapeSeq = trigo.RND() % 8;
        MakeTurret(turret);
    }

    private final ChaosBase.ResetProc ResetTurret_ref = this::ResetTurret;

    private void ResetReactor(ChaosBase.Obj reactor) {
        reactor.life = 100;
        reactor.hitSubLife = 1;
        reactor.fireSubLife = 1;
        reactor.dvx = 0;
        reactor.dvy = 0;
        reactor.moveSeq = 0;
        MakeReactor(reactor);
    }

    private final ChaosBase.ResetProc ResetReactor_ref = this::ResetReactor;

    private void MoveTraverse(ChaosBase.Obj traverse) {
        if (chaosActions.OutOfScreen(traverse)) {
            chaosActions.Leave(traverse);
            return;
        }
        chaosActions.UpdateXY(traverse);
        chaosActions.AvoidBackground(traverse, 4);
        traverse.life = 18;
        chaosActions.PlayerCollision(traverse, new Runtime.FieldExprRef<>(traverse, ChaosBase.Obj::getLife, ChaosBase.Obj::setLife));
    }

    private final ChaosBase.MoveProc MoveTraverse_ref = this::MoveTraverse;

    private void MoveCannon(ChaosBase.Obj cannon) {
        // VAR
        ChaosBase.Obj missile = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int dx = 0;
        int dy = 0;
        int nvx = 0;
        int nvy = 0;
        int swp = 0;
        int time = 0;

        if (chaosActions.OutOfScreen(cannon)) {
            chaosActions.Leave(cannon);
            return;
        }
        chaosActions.Burn(cannon);
        if (chaosBase.step > cannon.moveSeq) {
            chaosActions.GetCenter(cannon, px, py);
            nvy = 0;
            dy = 0;
            if (cannon.stat == 0) {
                dx = 6;
                nvx = 2048;
            } else {
                dx = -6;
                nvx = -2048;
            }
            if (cannon.subKind == mCannon2) {
                swp = dx;
                dx = dy;
                dy = swp;
                nvy = nvx;
                nvx = 0;
            }
            if (chaosBase.sleeper == 0) {
                missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien1, px.get() + dx, py.get() + dy, ChaosMissile.mYellow, 12);
                chaosActions.SetObjVXY(missile, nvx, nvy);
                chaosSounds.SoundEffect(cannon, smallFireEffect);
            }
            time = chaosBase.pLife + 16 - chaosBase.powerCountDown;
            if (time >= 20)
                time = 1;
            else
                time = (21 - time);
            cannon.moveSeq += ChaosBase.Period * time - ChaosBase.Period / 4 - trigo.RND() % (ChaosBase.Period / 8);
        }
        cannon.moveSeq -= chaosBase.step;
        chaosActions.PlayerCollision(cannon, new Runtime.FieldRef<>(cannon::getLife, cannon::setLife));
        if (cannon.life == 0)
            chaosActions.Die(cannon);
    }

    private final ChaosBase.MoveProc MoveCannon_ref = this::MoveCannon;

    private void MoveCannon3(ChaosBase.Obj cannon) {
        // VAR
        ChaosBase.Obj missile = null;
        long nvx = 0L;
        long nvy = 0L;
        long speed = 0L;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int angle = 0;
        int dv1 = 0;
        int dv2 = 0;
        int m2 = 0;
        int cnt = 0;
        int st = 0;
        int bs = 0;
        int time = 0;

        if (chaosActions.OutOfScreen(cannon)) {
            chaosActions.Leave(cannon);
            return;
        }
        chaosActions.Burn(cannon);
        if (chaosBase.step > cannon.moveSeq) {
            chaosActions.GetCenter(cannon, px, py);
            if (chaosBase.difficulty >= 2) {
                if (chaosBase.difficulty >= 8) {
                    cnt = 36;
                    m2 = 1;
                } else if (chaosBase.difficulty >= 5) {
                    cnt = 24;
                    m2 = 0;
                } else {
                    cnt = 15;
                    m2 = 0;
                }
                dv1 = trigo.RND() % 5 + 1;
                if (dv1 == 5)
                    dv1 = 6;
                dv2 = trigo.RND() % 5 + 1;
                if (dv2 == 5)
                    dv2 = 6;
                bs = trigo.RND() % 360;
                st = 360 / cnt;
                angle = 0;
                chaosSounds.SoundEffect(cannon, mediumFireEffect);
                while (cnt > 0) {
                    nvx = trigo.COS(angle);
                    nvy = trigo.SIN(angle);
                    speed = trigo.SIN(angle * dv1 + bs) + trigo.SIN(angle * dv2 + bs) * m2 / 2 + 2048;
                    nvx = nvx * speed / 1024;
                    nvy = nvy * speed / 1024;
                    missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien2, px.get(), py.get(), ChaosMissile.mAcc2, 12);
                    chaosActions.SetObjVXY(missile, (int) nvx, (int) nvy);
                    cnt--;
                    angle += st;
                }
            }
            if (chaosBase.pLife >= 20)
                time = 4;
            else
                time = 24 - chaosBase.pLife;
            cannon.moveSeq += ChaosBase.Period * time;
        }
        cannon.moveSeq -= chaosBase.step;
        chaosActions.PlayerCollision(cannon, new Runtime.FieldRef<>(cannon::getLife, cannon::setLife));
        if (cannon.life == 0)
            chaosActions.Die(cannon);
    }

    private final ChaosBase.MoveProc MoveCannon3_ref = this::MoveCannon3;

    private void MoveTurret(ChaosBase.Obj turret) {
        // VAR
        ChaosBase.Obj missile = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int nvx = 0;
        int nvy = 0;
        int angle = 0;
        int dx = 0;
        int dy = 0;
        int cnt = 0;
        int time = 0;
        int sk = 0;

        if (chaosActions.OutOfScreen(turret)) {
            chaosActions.Leave(turret);
            return;
        }
        chaosActions.Burn(turret);
        if (chaosBase.step > turret.moveSeq) {
            if (chaosBase.difficulty <= 2)
                cnt = 1;
            else
                cnt = chaosBase.difficulty - 2;
            if (chaosBase.sleeper != 0)
                cnt = (cnt - 1) / 2;
            chaosActions.GetCenter(turret, px, py);
            missile = chaosActions.nlObj;
            angle = turret.shapeSeq * 45;
            while (cnt > 0) {
                nvx = trigo.COS(angle) * 2;
                nvy = trigo.SIN(angle) * 2;
                dx = trigo.COS(angle) / 102;
                dy = trigo.SIN(angle) / 102;
                cnt--;
                sk = (cnt + 2) % 4;
                missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien1, px.get() + dx, py.get() + dy, sk, 8 + sk * 2);
                chaosActions.SetObjVXY(missile, nvx, nvy);
                angle += 45;
            }
            if (missile != chaosActions.nlObj)
                chaosSounds.SoundEffect(missile, smallFireEffect);
            if (chaosBase.pLife > 20)
                time = 8;
            else
                time = 28 - chaosBase.pLife;
            turret.moveSeq += ChaosBase.Period * time / 28 + trigo.RND() % 16;
            turret.shapeSeq = (turret.shapeSeq + 1) % 8;
        }
        turret.moveSeq -= chaosBase.step;
        if (chaosBase.powerCountDown < 5)
            chaosActions.PlayerCollision(turret, new Runtime.FieldRef<>(turret::getLife, turret::setLife));
        if (turret.life == 0)
            chaosActions.Die(turret);
    }

    private final ChaosBase.MoveProc MoveTurret_ref = this::MoveTurret;

    private void MoveReactor(ChaosBase.Obj reactor) {
        if (chaosActions.OutOfScreen(reactor)) {
            chaosActions.Leave(reactor);
            return;
        }
        if (chaosBase.step >= reactor.moveSeq)
            reactor.moveSeq = 0;
        else
            reactor.moveSeq -= chaosBase.step;
    }

    private final ChaosBase.MoveProc MoveReactor_ref = this::MoveReactor;

    private void AieCannon(ChaosBase.Obj cannon, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(cannon, hit, fire);
        chaosSounds.SoundEffect(cannon, aieCannonEffect);
    }

    private final ChaosBase.AieProc AieCannon_ref = this::AieCannon;

    private void AieCannon3(ChaosBase.Obj cannon, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(cannon, hit, fire);
        chaosSounds.SoundEffect(cannon, aieCannon3Effect);
    }

    private final ChaosBase.AieProc AieCannon3_ref = this::AieCannon3;

    private void AieReactor(ChaosBase.Obj reactor, ChaosBase.Obj src, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        // VAR
        ChaosBase.Obj missile = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);

        hit.set(0);
        fire.set(0);
        if (reactor.moveSeq == 0) {
            chaosActions.GetCenter(reactor, px, py);
            missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien3, px.get(), py.get(), ChaosMissile.mBig, 20);
            chaosActions.SetObjVXY(missile, 0, -2400);
            chaosSounds.SoundEffect(reactor, bigFireEffect);
            reactor.moveSeq = ChaosBase.Period * 3;
        }
    }

    private final ChaosBase.AieProc AieReactor_ref = this::AieReactor;

    private void DieCannon(ChaosBase.Obj cannon) {
        chaosSounds.SoundEffect(cannon, dieCannonEffect);
    }

    private final ChaosBase.DieProc DieCannon_ref = this::DieCannon;

    private void DieCannon3(ChaosBase.Obj cannon) {
        chaosBase.addpt += chaosBase.difficulty / 2;
        chaosSounds.SoundEffect(cannon, dieCannon3Effect);
    }

    private final ChaosBase.DieProc DieCannon3_ref = this::DieCannon3;

    private void DieTurret(ChaosBase.Obj turret) {
        chaosBase.addpt++;
        chaosSounds.SoundEffect(turret, dieTurretEffect);
    }

    private final ChaosBase.DieProc DieTurret_ref = this::DieTurret;

    private void DieReactor(ChaosBase.Obj reactor) {
        reactor.life = 100;
    }

    private final ChaosBase.DieProc DieReactor_ref = this::DieReactor;

    private void Push(ChaosBase.Obj victim, ChaosBase.Obj door, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ox = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> oy = new Runtime.Ref<>(0);

        if (victim.kind == Anims.MISSILE) {
            chaosActions.Die(victim);
            return;
        }
        chaosActions.GetCenter(door, px, py);
        chaosActions.GetCenter(victim, ox, oy);
        py.inc(door.cy + 1);
        ox.dec(victim.cx);
        chaosActions.SetObjXY(victim, ox.get(), py.get());
        victim.vy = (int) Math.abs(victim.vy);
        victim.dvy = (int) Math.abs(victim.dvy);
        victim.ay = (int) Math.abs(victim.ay);
    }

    private final ChaosBase.AieProc Push_ref = this::Push;

    private void MoveDoor(ChaosBase.Obj door) {
        if (chaosActions.OutOfScreen(door)) {
            chaosActions.Leave(door);
            return;
        }
        chaosActions.DoCollision(door, Runtime.withRange(EnumSet.of(Anims.MISSILE, Anims.SMARTBONUS, Anims.BONUS, Anims.MACHINE), Anims.PLAYER, Anims.ALIEN1), Push_ref, new Runtime.FieldRef<>(door::getHitSubLife, door::setHitSubLife), new Runtime.FieldRef<>(door::getFireSubLife, door::setFireSubLife));
    }

    private final ChaosBase.MoveProc MoveDoor_ref = this::MoveDoor;

    private void DieDoor(ChaosBase.Obj door) {
        chaosSounds.SoundEffect(door, dieDoorEffect);
        if (chaosGraphics.color) {
            chaosGraphics.SetTrans(0, 192);
            chaosGraphics.SetRGB(0, 255, 127, 0);
            chaosGraphics.SetRGB(4, 0, 255, 255);
        }
        chaosBase.screenInverted = ChaosBase.Period / 4;
    }

    private final ChaosBase.DieProc DieDoor_ref = this::DieDoor;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;
        int c = 0;
        int v = 0;
        int f = 0;
        int r1 = 0;
        int r2 = 0;

        chaosSounds.SetEffect(smallFireEffect[0], chaosSounds.soundList[SoundList.sMissile.ordinal()], 0, 0, 110, 3);
        chaosSounds.SetEffect(mediumFireEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 5000, 0, 190, 8);
        chaosSounds.SetEffect(bigFireEffect[0], chaosSounds.soundList[SoundList.sCasserole.ordinal()], 0, 16726, 240, 9);
        chaosSounds.SetEffect(aieCannonEffect[0], chaosSounds.soundList[SoundList.sLaser.ordinal()], 0, 16726, 80, 1);
        chaosSounds.SetEffect(dieDoorEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 0, 4181, 255, 12);
        for (c = 0; c <= 15; c++) {
            if ((c / 4) % 2 == 0)
                v = 9377;
            else
                v = 7442;
            chaosSounds.SetEffect(dieCannonEffect[c], chaosSounds.soundList[SoundList.sHHat.ordinal()], v / 24, v, (16 - c) * 9, 5);
        }
        chaosSounds.SetEffect(aieCannon3Effect[0], chaosSounds.soundList[SoundList.sHHat.ordinal()], 0, 0, 140, 1);
        r1 = 0;
        for (c = 0; c <= 15; c++) {
            r1 = (r1 * 17 + 5) % 8;
            v = (16 - c) * (r1 + 4);
            chaosSounds.SetEffect(dieTurretEffect[c], chaosSounds.soundList[SoundList.sHHat.ordinal()], 658, 10525, v, 5);
        }
        r1 = 0;
        r2 = 0;
        for (c = 0; c <= 23; c++) {
            r1 = (r1 * 9 + 5) % 16;
            r2 = (r2 * 13 + 9) % 16;
            f = 4181 + r1 * 836;
            v = f / (r2 + 8);
            chaosSounds.SetEffect(dieCannon3Effect[c], chaosSounds.soundList[SoundList.sHHat.ordinal()], v, f, (24 - c) * 6, 6);
        }
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetTraverse_ref;
        attr.Make = MakeTraverse_ref;
        attr.Move = MoveTraverse_ref;
        attr.weight = 100;
        attr.inerty = 30;
        attr.priority = -75;
        attr.dieStKinds = EnumSet.of(Stones.stSTAR2);
        attr.dieSKCount = 1;
        attr.dieStone = 9;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.NotBase;
        memory.AddTail(chaosBase.attrList[Anims.MACHINE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetCannon_ref;
        attr.Make = MakeCannon1_ref;
        attr.Move = MoveCannon_ref;
        attr.Aie = AieCannon_ref;
        attr.Die = DieCannon_ref;
        attr.weight = 120;
        attr.heatSpeed = 10;
        attr.coolSpeed = 10;
        attr.refreshSpeed = 30;
        attr.aieStKinds = EnumSet.of(Stones.stFOG2);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC26);
        attr.dieSKCount = 1;
        attr.dieStone = 9;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Mineral;
        attr.priority = -20;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.MACHINE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetCannon_ref;
        attr.Make = MakeCannon2_ref;
        attr.Move = MoveCannon_ref;
        attr.Aie = AieCannon_ref;
        attr.Die = DieCannon_ref;
        attr.weight = 120;
        attr.heatSpeed = 10;
        attr.coolSpeed = 10;
        attr.refreshSpeed = 30;
        attr.aieStKinds = EnumSet.of(Stones.stFOG2);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC26);
        attr.dieSKCount = 1;
        attr.dieStone = 9;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Mineral;
        attr.priority = -20;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.MACHINE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetCannon_ref;
        attr.Make = MakeCannon3_ref;
        attr.Move = MoveCannon3_ref;
        attr.Aie = AieCannon3_ref;
        attr.Die = DieCannon3_ref;
        attr.weight = 120;
        attr.heatSpeed = 8;
        attr.coolSpeed = 10;
        attr.refreshSpeed = 30;
        attr.aieStKinds = EnumSet.of(Stones.stFOG3);
        attr.aieSKCount = 1;
        attr.aieStone = 5;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC26);
        attr.dieSKCount = 1;
        attr.dieStone = 18;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Mineral;
        attr.priority = -20;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.MACHINE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetTurret_ref;
        attr.Make = MakeTurret_ref;
        attr.Move = MoveTurret_ref;
        attr.Aie = AieCannon_ref;
        attr.Die = DieTurret_ref;
        attr.weight = 140;
        attr.heatSpeed = 10;
        attr.coolSpeed = 10;
        attr.refreshSpeed = 30;
        attr.aieStKinds = EnumSet.of(Stones.stFOG3);
        attr.aieSKCount = 1;
        attr.aieStone = 5;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC26);
        attr.dieSKCount = 1;
        attr.dieStone = 12;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Mineral;
        attr.priority = -20;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.MACHINE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetReactor_ref;
        attr.Make = MakeReactor_ref;
        attr.Move = MoveReactor_ref;
        attr.Aie = AieReactor_ref;
        attr.Die = DieReactor_ref;
        attr.charge = 60;
        attr.aieStKinds = EnumSet.of(Stones.stFOG1, Stones.stFOG2, Stones.stFOG3);
        attr.aieSKCount = 3;
        attr.aieStone = ChaosBase.FlameMult + 12;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = -19;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.MACHINE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeDoor_as_ChaosBase_ResetProc;
        attr.Make = MakeDoor_as_ChaosBase_MakeProc;
        attr.Move = MoveDoor_ref;
        attr.Die = DieDoor_ref;
        attr.dieStKinds = EnumSet.of(Stones.stSTAR2);
        attr.dieSKCount = 1;
        attr.dieStone = ChaosBase.FlameMult * 6 + 28;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 92;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.MACHINE.ordinal()], attr.node);
    }


    // Support

    private static ChaosMachine instance;

    public static ChaosMachine instance() {
        if (instance == null)
            new ChaosMachine(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        InitParams();
    }

    public void close() {
    }

}
