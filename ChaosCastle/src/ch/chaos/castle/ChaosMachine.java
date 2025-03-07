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
        short px = 0;
        short py = 0;
        short sx = 0;
        short sy = 0;

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

    private void MakeCannon1(ChaosBase.Obj cannon) {
        // VAR
        short px = 0;
        short ex = 0;
        short sx = 0;

        if (cannon.stat == 0) {
            px = 0;
            sx = 0;
            ex = 14;
        } else {
            px = 16;
            sx = 2;
            ex = 16;
        }
        chaosActions.SetObjLoc(cannon, px, (short) 212, (short) 16, (short) 28);
        chaosActions.SetObjRect(cannon, sx, 0, ex, 28);
    }

    private void MakeCannon2(ChaosBase.Obj cannon) {
        // VAR
        short px = 0;
        short sy = 0;
        short ey = 0;

        if (cannon.stat == 0) {
            px = 28;
            sy = 0;
            ey = 14;
        } else {
            px = 0;
            sy = 2;
            ey = 16;
        }
        chaosActions.SetObjLoc(cannon, px, (short) 240, (short) 28, (short) 16);
        chaosActions.SetObjRect(cannon, 0, sy, 28, ey);
    }

    private void MakeCannon3(ChaosBase.Obj cannon) {
        chaosActions.SetObjLoc(cannon, (short) 152, (short) 180, (short) 20, (short) 20);
        chaosActions.SetObjRect(cannon, 0, 0, 20, 20);
    }

    private void MakeTurret(ChaosBase.Obj turret) {
        chaosActions.SetObjLoc(turret, (short) 32, (short) 212, (short) 28, (short) 28);
        chaosActions.SetObjRect(turret, 2, 2, 26, 26);
    }

    private void MakeReactor(ChaosBase.Obj reactor) {
        chaosActions.SetObjLoc(reactor, (short) 224, (short) 184, (short) 16, (short) 20);
        chaosActions.SetObjRect(reactor, 0, 0, 16, 20);
    }

    private void MakeDoor(ChaosBase.Obj door) {
        door.hitSubLife = 0;
        door.fireSubLife = 0;
        door.life = 100;
        chaosActions.SetObjLoc(door, (short) 224, (short) 84, (short) 32, (short) 6);
        chaosActions.SetObjRect(door, 0, -2, 32, 8);
    }

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

    private void ResetTurret(ChaosBase.Obj turret) {
        turret.life = 40 + chaosBase.difficulty * 10;
        turret.fireSubLife = turret.life;
        turret.hitSubLife = turret.life;
        turret.moveSeq = trigo.RND() % (ChaosBase.Period / 4);
        turret.shapeSeq = trigo.RND() % 8;
        MakeTurret(turret);
    }

    private void ResetReactor(ChaosBase.Obj reactor) {
        reactor.life = 100;
        reactor.hitSubLife = 1;
        reactor.fireSubLife = 1;
        reactor.dvx = 0;
        reactor.dvy = 0;
        reactor.moveSeq = 0;
        MakeReactor(reactor);
    }

    private void MoveTraverse(ChaosBase.Obj traverse) {
        if (chaosActions.OutOfScreen(traverse)) {
            chaosActions.Leave(traverse);
            return;
        }
        chaosActions.UpdateXY(traverse);
        chaosActions.AvoidBackground(traverse, (short) 4);
        traverse.life = 18;
        chaosActions.PlayerCollision(traverse, new Runtime.FieldExprRef<>(traverse, ChaosBase.Obj::getLife, ChaosBase.Obj::setLife));
    }

    private void MoveCannon(ChaosBase.Obj cannon) {
        // VAR
        ChaosBase.Obj missile = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short dx = 0;
        short dy = 0;
        short nvx = 0;
        short nvy = 0;
        short swp = 0;
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
                missile = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien1, (short) (px.get() + dx), (short) (py.get() + dy), ChaosMissile.mYellow, 12);
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

    private void MoveCannon3(ChaosBase.Obj cannon) {
        // VAR
        ChaosBase.Obj missile = null;
        int nvx = 0;
        int nvy = 0;
        int speed = 0;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short angle = 0;
        short dv1 = 0;
        short dv2 = 0;
        short m2 = 0;
        short cnt = 0;
        short st = 0;
        short bs = 0;
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
                dv1 = (short) (trigo.RND() % 5 + 1);
                if (dv1 == 5)
                    dv1 = 6;
                dv2 = (short) (trigo.RND() % 5 + 1);
                if (dv2 == 5)
                    dv2 = 6;
                bs = (short) (trigo.RND() % 360);
                st = (short) (360 / cnt);
                angle = 0;
                chaosSounds.SoundEffect(cannon, mediumFireEffect);
                while (cnt > 0) {
                    nvx = trigo.COS(angle);
                    nvy = trigo.SIN(angle);
                    speed = trigo.SIN((short) (angle * dv1 + bs)) + trigo.SIN((short) (angle * dv2 + bs)) * m2 / 2 + 2048;
                    nvx = nvx * speed / 1024;
                    nvy = nvy * speed / 1024;
                    missile = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien2, px.get(), py.get(), ChaosMissile.mAcc2, 12);
                    chaosActions.SetObjVXY(missile, (short) nvx, (short) nvy);
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

    private void MoveTurret(ChaosBase.Obj turret) {
        // VAR
        ChaosBase.Obj missile = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short nvx = 0;
        short nvy = 0;
        short angle = 0;
        short dx = 0;
        short dy = 0;
        int cnt = 0;
        int time = 0;
        short sk = 0;

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
            angle = (short) (turret.shapeSeq * 45);
            while (cnt > 0) {
                nvx = (short) (trigo.COS(angle) * 2);
                nvy = (short) (trigo.SIN(angle) * 2);
                dx = (short) (trigo.COS(angle) / 102);
                dy = (short) (trigo.SIN(angle) / 102);
                cnt--;
                sk = (short) ((cnt + 2) % 4);
                missile = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien1, (short) (px.get() + dx), (short) (py.get() + dy), sk, 8 + sk * 2);
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

    private void AieCannon(ChaosBase.Obj cannon, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(cannon, hit, fire);
        chaosSounds.SoundEffect(cannon, aieCannonEffect);
    }

    private void AieCannon3(ChaosBase.Obj cannon, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(cannon, hit, fire);
        chaosSounds.SoundEffect(cannon, aieCannon3Effect);
    }

    private void AieReactor(ChaosBase.Obj reactor, ChaosBase.Obj src, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        // VAR
        ChaosBase.Obj missile = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);

        hit.set(0);
        fire.set(0);
        if (reactor.moveSeq == 0) {
            chaosActions.GetCenter(reactor, px, py);
            missile = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien3, px.get(), py.get(), ChaosMissile.mBig, 20);
            chaosActions.SetObjVXY(missile, (short) 0, (short) -2400);
            chaosSounds.SoundEffect(reactor, bigFireEffect);
            reactor.moveSeq = ChaosBase.Period * 3;
        }
    }

    private void DieCannon(ChaosBase.Obj cannon) {
        chaosSounds.SoundEffect(cannon, dieCannonEffect);
    }

    private void DieCannon3(ChaosBase.Obj cannon) {
        chaosBase.addpt += chaosBase.difficulty / 2;
        chaosSounds.SoundEffect(cannon, dieCannon3Effect);
    }

    private void DieTurret(ChaosBase.Obj turret) {
        chaosBase.addpt++;
        chaosSounds.SoundEffect(turret, dieTurretEffect);
    }

    private void DieReactor(ChaosBase.Obj reactor) {
        reactor.life = 100;
    }

    private void Push(ChaosBase.Obj victim, ChaosBase.Obj door, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        // VAR
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> ox = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> oy = new Runtime.Ref<>((short) 0);

        if (victim.kind == Anims.MISSILE) {
            chaosActions.Die(victim);
            return;
        }
        chaosActions.GetCenter(door, px, py);
        chaosActions.GetCenter(victim, ox, oy);
        py.inc(door.cy + 1);
        ox.dec(victim.cx);
        chaosActions.SetObjXY(victim, ox.get(), py.get());
        victim.vy = (short) Math.abs(victim.vy);
        victim.dvy = (short) Math.abs(victim.dvy);
        victim.ay = (byte) Math.abs(victim.ay);
    }

    private void MoveDoor(ChaosBase.Obj door) {
        if (chaosActions.OutOfScreen(door)) {
            chaosActions.Leave(door);
            return;
        }
        chaosActions.DoCollision(door, Runtime.withRange(EnumSet.of(Anims.MISSILE, Anims.SMARTBONUS, Anims.BONUS, Anims.MACHINE), Anims.PLAYER, Anims.ALIEN1), Runtime.proc(this::Push, "ChaosMachine.Push"), new Runtime.FieldRef<>(door::getHitSubLife, door::setHitSubLife), new Runtime.FieldRef<>(door::getFireSubLife, door::setFireSubLife));
    }

    private void DieDoor(ChaosBase.Obj door) {
        chaosSounds.SoundEffect(door, dieDoorEffect);
        if (chaosGraphics.color) {
            chaosGraphics.SetTrans((short) 0, (short) 192);
            chaosGraphics.SetRGB((short) 0, (short) 255, (short) 127, (short) 0);
            chaosGraphics.SetRGB((short) 4, (short) 0, (short) 255, (short) 255);
        }
        chaosBase.screenInverted = (short) (ChaosBase.Period / 4);
    }

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;
        short c = 0;
        short v = 0;
        short f = 0;
        short r1 = 0;
        short r2 = 0;

        chaosSounds.SetEffect(smallFireEffect[0], chaosSounds.soundList[SoundList.sMissile.ordinal()], 0, 0, (short) 110, (short) 3);
        chaosSounds.SetEffect(mediumFireEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 5000, 0, (short) 190, (short) 8);
        chaosSounds.SetEffect(bigFireEffect[0], chaosSounds.soundList[SoundList.sCasserole.ordinal()], 0, 16726, (short) 240, (short) 9);
        chaosSounds.SetEffect(aieCannonEffect[0], chaosSounds.soundList[SoundList.sLaser.ordinal()], 0, 16726, (short) 80, (short) 1);
        chaosSounds.SetEffect(dieDoorEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 0, 4181, (short) 255, (short) 12);
        for (c = 0; c <= 15; c++) {
            if ((c / 4) % 2 == 0)
                v = 9377;
            else
                v = 7442;
            chaosSounds.SetEffect(dieCannonEffect[c], chaosSounds.soundList[SoundList.sHHat.ordinal()], v / 24, v, (short) ((16 - c) * 9), (short) 5);
        }
        chaosSounds.SetEffect(aieCannon3Effect[0], chaosSounds.soundList[SoundList.sHHat.ordinal()], 0, 0, (short) 140, (short) 1);
        r1 = 0;
        for (c = 0; c <= 15; c++) {
            r1 = (short) ((r1 * 17 + 5) % 8);
            v = (short) ((16 - c) * (r1 + 4));
            chaosSounds.SetEffect(dieTurretEffect[c], chaosSounds.soundList[SoundList.sHHat.ordinal()], 658, 10525, v, (short) 5);
        }
        r1 = 0;
        r2 = 0;
        for (c = 0; c <= 23; c++) {
            r1 = (short) ((r1 * 9 + 5) % 16);
            r2 = (short) ((r2 * 13 + 9) % 16);
            f = (short) (4181 + r1 * 836);
            v = (short) (f / (r2 + 8));
            chaosSounds.SetEffect(dieCannon3Effect[c], chaosSounds.soundList[SoundList.sHHat.ordinal()], v, f, (short) ((24 - c) * 6), (short) 6);
        }
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetTraverse, "ChaosMachine.ResetTraverse");
        attr.Make = Runtime.proc(this::MakeTraverse, "ChaosMachine.MakeTraverse");
        attr.Move = Runtime.proc(this::MoveTraverse, "ChaosMachine.MoveTraverse");
        attr.weight = 100;
        attr.inerty = 30;
        attr.priority = -75;
        attr.dieStKinds = EnumSet.of(Stones.stSTAR2);
        attr.dieSKCount = 1;
        attr.dieStone = 9;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.NotBase;
        memory.AddTail(chaosBase.attrList[Anims.MACHINE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetCannon, "ChaosMachine.ResetCannon");
        attr.Make = Runtime.proc(this::MakeCannon1, "ChaosMachine.MakeCannon1");
        attr.Move = Runtime.proc(this::MoveCannon, "ChaosMachine.MoveCannon");
        attr.Aie = Runtime.proc(this::AieCannon, "ChaosMachine.AieCannon");
        attr.Die = Runtime.proc(this::DieCannon, "ChaosMachine.DieCannon");
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
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetCannon, "ChaosMachine.ResetCannon");
        attr.Make = Runtime.proc(this::MakeCannon2, "ChaosMachine.MakeCannon2");
        attr.Move = Runtime.proc(this::MoveCannon, "ChaosMachine.MoveCannon");
        attr.Aie = Runtime.proc(this::AieCannon, "ChaosMachine.AieCannon");
        attr.Die = Runtime.proc(this::DieCannon, "ChaosMachine.DieCannon");
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
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetCannon, "ChaosMachine.ResetCannon");
        attr.Make = Runtime.proc(this::MakeCannon3, "ChaosMachine.MakeCannon3");
        attr.Move = Runtime.proc(this::MoveCannon3, "ChaosMachine.MoveCannon3");
        attr.Aie = Runtime.proc(this::AieCannon3, "ChaosMachine.AieCannon3");
        attr.Die = Runtime.proc(this::DieCannon3, "ChaosMachine.DieCannon3");
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
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetTurret, "ChaosMachine.ResetTurret");
        attr.Make = Runtime.proc(this::MakeTurret, "ChaosMachine.MakeTurret");
        attr.Move = Runtime.proc(this::MoveTurret, "ChaosMachine.MoveTurret");
        attr.Aie = Runtime.proc(this::AieCannon, "ChaosMachine.AieCannon");
        attr.Die = Runtime.proc(this::DieTurret, "ChaosMachine.DieTurret");
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
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetReactor, "ChaosMachine.ResetReactor");
        attr.Make = Runtime.proc(this::MakeReactor, "ChaosMachine.MakeReactor");
        attr.Move = Runtime.proc(this::MoveReactor, "ChaosMachine.MoveReactor");
        attr.Aie = Runtime.proc(this::AieReactor, "ChaosMachine.AieReactor");
        attr.Die = Runtime.proc(this::DieReactor, "ChaosMachine.DieReactor");
        attr.charge = 60;
        attr.aieStKinds = EnumSet.of(Stones.stFOG1, Stones.stFOG2, Stones.stFOG3);
        attr.aieSKCount = 3;
        attr.aieStone = (short) (ChaosBase.FlameMult + 12);
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = -19;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.MACHINE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::MakeDoor, "ChaosMachine.MakeDoor");
        attr.Make = Runtime.proc(this::MakeDoor, "ChaosMachine.MakeDoor");
        attr.Move = Runtime.proc(this::MoveDoor, "ChaosMachine.MoveDoor");
        attr.Die = Runtime.proc(this::DieDoor, "ChaosMachine.DieDoor");
        attr.dieStKinds = EnumSet.of(Stones.stSTAR2);
        attr.dieSKCount = 1;
        attr.dieStone = (short) (ChaosBase.FlameMult * 6 + 28);
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
