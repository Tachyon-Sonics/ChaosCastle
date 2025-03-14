package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.ObjFlags;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;
import java.util.EnumSet;


public class ChaosBoss {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosFire chaosFire;
    private final ChaosGraphics chaosGraphics;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Languages languages;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosBoss() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosFire = ChaosFire.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // CONST

    public static final int bBrotherAlien = 0;
    public static final int bSisterAlien = 1;
    public static final int bMotherAlien = 2;
    public static final int bFatherAlien = 3;
    public static final int bMasterAlien1 = 4;
    public static final int bMasterAlien2 = 5;
    public static final int bFatherHeart = 6;
    public static final int bMasterEye = 7;
    public static final int bMasterMouth = 8;
    public static final int bMasterPart0 = 9;
    public static final int bMasterPart1 = 10;
    public static final int bMasterPart2 = 11;


    // VAR

    private ChaosSounds.Effect[] brotherEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] sisterEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] motherEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] fatherEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] haha1Effect = Runtime.initArray(new ChaosSounds.Effect[4]);
    private ChaosSounds.Effect[] haha2Effect = Runtime.initArray(new ChaosSounds.Effect[6]);


    public ChaosSounds.Effect[] getBrotherEffect() {
        return this.brotherEffect;
    }

    public void setBrotherEffect(ChaosSounds.Effect[] brotherEffect) {
        this.brotherEffect = brotherEffect;
    }

    public ChaosSounds.Effect[] getSisterEffect() {
        return this.sisterEffect;
    }

    public void setSisterEffect(ChaosSounds.Effect[] sisterEffect) {
        this.sisterEffect = sisterEffect;
    }

    public ChaosSounds.Effect[] getMotherEffect() {
        return this.motherEffect;
    }

    public void setMotherEffect(ChaosSounds.Effect[] motherEffect) {
        this.motherEffect = motherEffect;
    }

    public ChaosSounds.Effect[] getFatherEffect() {
        return this.fatherEffect;
    }

    public void setFatherEffect(ChaosSounds.Effect[] fatherEffect) {
        this.fatherEffect = fatherEffect;
    }

    public ChaosSounds.Effect[] getHaha1Effect() {
        return this.haha1Effect;
    }

    public void setHaha1Effect(ChaosSounds.Effect[] haha1Effect) {
        this.haha1Effect = haha1Effect;
    }

    public ChaosSounds.Effect[] getHaha2Effect() {
        return this.haha2Effect;
    }

    public void setHaha2Effect(ChaosSounds.Effect[] haha2Effect) {
        this.haha2Effect = haha2Effect;
    }


    // PROCEDURE

    private void MakeBrotherAlien(ChaosBase.Obj boss) {
        // VAR
        short py = 0;

        if (boss.hitSubLife + boss.fireSubLife == 0)
            py = 148;
        else
            py = 96;
        chaosActions.SetObjLoc(boss, (short) 0, py, (short) 64, (short) 32);
        chaosActions.SetObjRect(boss, 1, 1, 63, 31);
    }

    private void ResetBrotherAlien(ChaosBase.Obj boss) {
        boss.life = 1500;
        boss.fireSubLife = 10;
        boss.hitSubLife = 40;
        boss.moveSeq = 30;
        boss.shapeSeq = 0;
        boss.stat = 0;
        MakeBrotherAlien(boss);
    }

    private void MoveBrotherAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Short> bx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> by = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBounds(boss, (short) 3);
        chaosActions.AvoidBackground(boss, (short) 3);
        chaosActions.Burn(boss);
        if (chaosBase.step >= boss.shapeSeq) {
            if (boss.hitSubLife + boss.fireSubLife == 0) {
                if ((boss.moveSeq < 10) && (boss.moveSeq > 0))
                    chaosFire.BoumE(boss, (short) 30, (short) 8, (short) 16);
                boss.shapeSeq += ChaosBase.Period;
            } else {
                if (boss.moveSeq < 20)
                    chaosFire.BoumE(boss, (short) 30, (short) 16, (short) 8);
                boss.shapeSeq += ChaosBase.Period * (trigo.RND() % 8 + 5);
            }
        }
        boss.shapeSeq -= chaosBase.step;
        if (boss.moveSeq == 0)
            chaosFire.GoCenter(boss);
        if (chaosBase.step > boss.stat) {
            if (boss.hitSubLife + boss.fireSubLife == 0) {
                if (boss.moveSeq == 0) {
                    chaosActions.GetCenter(boss, bx, by);
                    chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
                    if (chaosFire.CloseEnough(bx.get(), by.get(), px.get(), py.get()))
                        chaosActions.Die(boss);
                    return;
                } else {
                    boss.hitSubLife = 40;
                    boss.fireSubLife = 10;
                }
                MakeBrotherAlien(boss);
            }
            if (boss.moveSeq < 10)
                chaosFire.FireFlame(boss, (short) 0, (short) 0, true);
            else if (boss.moveSeq < 20)
                chaosFire.FireMissileV(boss, chaosBase.mainPlayer, (short) 0, (short) 0, true);
            else if (boss.moveSeq < 30)
                chaosFire.FireMissileA(boss, (short) 0, (short) 0);
            boss.dvx = (short) (trigo.RND() % 4096);
            boss.dvx -= 2048;
            boss.dvy = (short) (trigo.RND() % 4096);
            boss.dvy -= 2048;
            boss.stat += ChaosBase.Period / 4 + trigo.RND() % (ChaosBase.Period * 10 / (chaosBase.difficulty + 4));
        }
        boss.stat -= chaosBase.step;
        hit.set(50);
        chaosActions.PlayerCollision(boss, hit);
    }

    private void AieBrotherAlien(ChaosBase.Obj boss, ChaosBase.Obj src, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        hit.set(0);
        fire.set(0);
        if (boss.hitSubLife + boss.fireSubLife == 0)
            return;
        boss.moveSeq--;
        chaosSounds.SoundEffect(boss, chaosFire.aieEffect);
        boss.hitSubLife = 0;
        boss.fireSubLife = 0;
        MakeBrotherAlien(boss);
        if (boss.life > 0)
            boss.stat = ChaosBase.Period * 2;
        else
            boss.stat = ChaosBase.Period * 10;
        boss.shapeSeq = ChaosBase.Period + ChaosBase.Period / 4;
        boss.life = boss.moveSeq * 50 + 1;
        chaosFire.ShowStat(Runtime.castToRef(languages.ADL("Brother: ##"), String.class), boss.moveSeq);
    }

    private void DieBrotherAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);
        short cnt = 0;

        if (boss.moveSeq == 0) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, brotherEffect);
            chaosActions.Boum(boss, EnumSet.of(Stones.stFOG3, Stones.stFOG4), (short) ChaosBase.slowStyle, (short) 15, (short) 2);
            chaosActions.Boum(boss, EnumSet.of(Stones.stCE, Stones.stRE, Stones.stCROSS), (short) ChaosBase.gravityStyle, (short) 15, (short) 3);
            chaosActions.Boum(boss, EnumSet.of(Stones.stC26, Stones.stC35), (short) ChaosBase.fastStyle, (short) 15, (short) 2);
            for (cnt = 1; cnt <= 10; cnt++) {
                boss.vx = (short) (trigo.COS((short) (cnt * 36)) * 4);
                boss.vy = (short) (trigo.SIN((short) (cnt * 36)) * 4);
                chaosFire.FireFlame(boss, (short) 0, (short) 0, false);
            }
            boss.vx = 0;
            boss.vy = 0;
        } else {
            boss.life = boss.moveSeq * 50 + 1;
            hit.set(40);
            fire.set(10);
            chaosActions.Aie(boss, boss, hit, fire);
        }
    }

    private void MakeSisterAlien(ChaosBase.Obj boss) {
        // VAR
        short ss = 0;
        short sz = 0;
        short sx = 0;
        short sy = 0;

        if (boss.shapeSeq >= 512) {
            sz = 0;
        } else {
            ss = (short) ((boss.shapeSeq / 16) % 16);
            if (ss < 8)
                sz = (short) (8 - ss);
            else
                sz = (short) (ss - 7);
        }
        sx = (short) (28 - sz * 2);
        sy = (short) (16 - sz * 2);
        chaosActions.SetObjLoc(boss, (short) (60 + sz), (short) (224 + sz), sx, sy);
        chaosActions.SetObjRect(boss, 0, 0, 28, 16);
    }

    private void ResetSisterAlien(ChaosBase.Obj boss) {
        boss.life = 1500;
        boss.fireSubLife = 40;
        boss.hitSubLife = 10;
        boss.moveSeq = 30;
        boss.shapeSeq = 128;
        boss.stat = 0;
        MakeSisterAlien(boss);
    }

    private void MoveSisterAlien(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.ObjAttr aAttr = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        int kCnt = 0;
        int sStat = 0;
        int cnt = 0;
        int mul = 0;
        Anims nKind = Anims.PLAYER;
        short sKind = 0;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBounds(boss, (short) 4);
        chaosActions.AvoidBackground(boss, (short) 4);
        chaosActions.Burn(boss);
        if ((boss.shapeSeq >= 512) && (chaosBase.step + 512 > boss.shapeSeq))
            chaosFire.BoumS(boss, chaosBase.mainPlayer, (short) 0, (short) 0, (short) 0, (short) 0, (short) 15, (short) 0, (short) 24, (short) 13, (short) 0, false, false, true);
        if (chaosBase.step > boss.shapeSeq) {
            if (boss.hitSubLife + boss.fireSubLife == 0) {
                if (boss.moveSeq == 0) {
                    chaosActions.Die(boss);
                    return;
                } else {
                    boss.hitSubLife = 10;
                    boss.fireSubLife = 40;
                }
            }
            if (boss.stat == 0) {
                kCnt = 0;
                mul = 1;
                while (true) {
                    if (kCnt > 6) {
                        kCnt = 0;
                        mul++;
                    }
                    sStat = 20 + chaosBase.pLife * 5;
                    cnt = 1;
                    switch (kCnt) {
                        case 0 -> {
                            nKind = Anims.MACHINE;
                            sKind = ChaosMachine.mCannon3;
                        }
                        case 1 -> {
                            nKind = Anims.ALIEN2;
                            sKind = ChaosCreator.cFour;
                        }
                        case 2 -> {
                            nKind = Anims.ALIEN2;
                            sKind = ChaosCreator.cCreatorR;
                        }
                        case 3 -> {
                            nKind = Anims.ALIEN2;
                            sKind = ChaosCreator.cQuad;
                        }
                        case 4 -> {
                            nKind = Anims.MACHINE;
                            sKind = ChaosMachine.mTurret;
                        }
                        case 5 -> {
                            nKind = Anims.ALIEN1;
                            sKind = ChaosAlien.aHospital;
                        }
                        case 6 -> {
                            nKind = Anims.ALIEN2;
                            sKind = ChaosCreator.cNest;
                        }
                        default -> throw new RuntimeException("Unhandled CASE value " + kCnt);
                    }
                    aAttr = chaosBase.GetAnimAttr(nKind, sKind);
                    if (aAttr.nbObj < cnt * mul) {
                        chaosActions.GetCenter(boss, px, py);
                        chaosSounds.SoundEffect(boss, chaosFire.huEffect);
                        obj = chaosActions.CreateObj(nKind, sKind, px.get(), py.get(), sStat, sStat);
                        break;
                    }
                    kCnt++;
                }
                boss.dvx = (short) (trigo.RND() % 3072);
                boss.dvx -= 1536;
                boss.dvy = (short) (trigo.RND() % 3072);
                boss.dvy -= 1536;
                boss.stat = trigo.RND() % 3 + 1;
            } else {
                boss.stat--;
                if (boss.stat == 0) {
                    boss.dvx = 0;
                    boss.dvy = 0;
                }
            }
            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, (short) 0, (short) 0, true);
            boss.shapeSeq += 512;
        }
        boss.shapeSeq -= chaosBase.step;
        MakeSisterAlien(boss);
        hit.set(50);
        chaosActions.PlayerCollision(boss, hit);
    }

    private void AieSisterAlien(ChaosBase.Obj boss, ChaosBase.Obj src, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        hit.set(0);
        fire.set(0);
        if (boss.hitSubLife + boss.fireSubLife == 0)
            return;
        boss.moveSeq--;
        chaosSounds.SoundEffect(boss, chaosFire.aieEffect);
        boss.hitSubLife = 0;
        boss.fireSubLife = 0;
        if (boss.moveSeq > 0) {
            boss.shapeSeq = 1024;
        } else {
            boss.shapeSeq = 2048;
            boss.dvx = 0;
            boss.dvy = 0;
        }
        boss.stat = 0;
        boss.life = boss.moveSeq * 50 + 1;
        chaosFire.ShowStat(Runtime.castToRef(languages.ADL("Sister: ##"), String.class), boss.moveSeq);
    }

    private void DieSisterAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);

        if (boss.moveSeq == 0) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, sisterEffect);
            chaosFire.BoumS(boss, chaosBase.mainPlayer, (short) 0, (short) 0, (short) 0, (short) 0, (short) 12, (short) 18, (short) 60, (short) 6, (short) 10, false, false, false);
        } else {
            boss.life = boss.moveSeq * 50 + 1;
            hit.set(10);
            fire.set(40);
            chaosActions.Aie(boss, boss, hit, fire);
        }
    }

    private void MakeMotherAlien(ChaosBase.Obj boss) {
        // VAR
        short px = 0;
        short py = 0;

        switch (boss.stat) {
            case 0, 1, 3, 4 -> {
                px = 172;
                py = 180;
            }
            case 2 -> {
                if (boss.flags.contains(ObjFlags.nested)) {
                    px = 172;
                    py = 180;
                    boss.flags.remove(ObjFlags.nested);
                } else {
                    px = 64;
                    py = 200;
                    boss.flags.add(ObjFlags.nested);
                }
            }
            case 5 -> {
                px = 64;
                py = 200;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + boss.stat);
        }
        chaosActions.SetObjLoc(boss, px, py, (short) 24, (short) 24);
        chaosActions.SetObjRect(boss, 1, 1, 23, 23);
    }

    private void ResetMotherAlien(ChaosBase.Obj boss) {
        boss.hitSubLife = 30;
        boss.fireSubLife = 20;
        boss.moveSeq = 9 + chaosBase.difficulty;
        boss.life = boss.moveSeq * 50 + 1;
        boss.shapeSeq = 0;
        boss.stat = 5;
        MakeMotherAlien(boss);
    }

    private void MoveMotherAlien(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj alien = null;
        int oldSeq = 0;
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        int nLife = 0;
        short cnt = 0;
        short c = 0;
        short angle = 0;
        Runtime.Ref<Short> bx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> by = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short nSpeed = 0;
        Anims nKind = Anims.PLAYER;
        short sKind = 0;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBackground(boss, (short) 4);
        chaosActions.Burn(boss);
        chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
        chaosActions.GetCenter(boss, bx, by);
        if (chaosBase.step > boss.shapeSeq) {
            if ((boss.stat == 0) && chaosFire.CloseEnough(bx.get(), by.get(), px.get(), py.get())) {
                boss.shapeSeq = 0;
                chaosActions.Die(boss);
                return;
            } else if ((boss.stat == 1) && (chaosBase.nbAnim[Anims.ALIEN2.ordinal()] + chaosBase.nbAnim[Anims.ALIEN1.ordinal()] == 0)) {
                chaosSounds.SoundEffect(boss, chaosFire.poufEffect);
                boss.stat = 0;
                boss.shapeSeq = ChaosBase.Period * 40;
            } else if (boss.stat == 2) {
                boss.hitSubLife = 20;
                boss.fireSubLife = 30;
                boss.shapeSeq = ChaosBase.Period * 2;
                boss.stat = 5;
            } else if ((boss.stat == 3) && (chaosBase.nbAnim[Anims.ALIEN2.ordinal()] + chaosBase.nbAnim[Anims.ALIEN1.ordinal()] == 0)) {
                chaosFire.BoumS(boss, chaosBase.mainPlayer, (short) 0, (short) 0, (short) 0, (short) 0, (short) 15, (short) 0, (short) 24, (short) 15, (short) 0, true, true, true);
                boss.stat = 2;
                boss.shapeSeq = ChaosBase.Period * (20 + trigo.RND() % 16);
            } else if (boss.stat == 4) {
                if (boss.moveSeq == 0)
                    boss.stat = 1;
                else
                    boss.stat = 3;
                chaosSounds.SoundEffect(boss, chaosFire.createEffect);
                nKind = Anims.ALIEN2;
                nSpeed = 4;
                nLife = chaosBase.pLife * 2 + 40;
                switch (boss.moveSeq) {
                    case 0 -> {
                        sKind = ChaosCreator.cChief;
                        cnt = 4;
                        nLife += 30;
                    }
                    case 1 -> {
                        sKind = ChaosCreator.cCircle;
                        cnt = 4;
                        nLife += 100;
                        nSpeed = 3;
                    }
                    case 2 -> {
                        sKind = ChaosCreator.cHurryUp;
                        cnt = 6;
                        nSpeed = 2;
                    }
                    case 3 -> {
                        sKind = ChaosCreator.cCreatorC;
                        cnt = 7;
                        nLife += 90;
                        nSpeed = 1;
                    }
                    case 4 -> {
                        sKind = ChaosCreator.cCreatorR;
                        cnt = 5;
                        nLife += 60;
                        nSpeed = 1;
                    }
                    case 5 -> {
                        sKind = ChaosCreator.cAlienBox;
                        cnt = 3;
                        nLife += 90;
                        nSpeed = 2;
                    }
                    case 6 -> {
                        sKind = ChaosCreator.cNest;
                        cnt = 9;
                        nLife += 60;
                    }
                    case 7 -> {
                        sKind = ChaosCreator.cFour;
                        cnt = 12;
                    }
                    case 8 -> {
                        sKind = ChaosCreator.cQuad;
                        cnt = 11;
                    }
                    case 9 -> {
                        sKind = ChaosCreator.cAlienA;
                        cnt = 12;
                    }
                    case 10 -> {
                        sKind = ChaosCreator.cAlienV;
                        cnt = 11;
                    }
                    case 11 -> {
                        sKind = ChaosCreator.cGrid;
                        cnt = 6;
                    }
                    case 12 -> {
                        sKind = ChaosAlien.aBumper;
                        cnt = 11;
                        nKind = Anims.ALIEN1;
                    }
                    case 13 -> {
                        sKind = ChaosCreator.cChief;
                        cnt = 1;
                    }
                    case 14 -> {
                        sKind = ChaosCreator.cGhost;
                        cnt = 5;
                    }
                    case 15 -> {
                        sKind = ChaosCreator.cPopUp;
                        cnt = 12;
                    }
                    case 16 -> {
                        sKind = ChaosAlien.aBig;
                        cnt = 7;
                        nKind = Anims.ALIEN1;
                    }
                    case 17 -> {
                        sKind = ChaosAlien.aSquare;
                        cnt = 10;
                        nKind = Anims.ALIEN1;
                    }
                    case 18 -> {
                        sKind = bBrotherAlien;
                        cnt = 1;
                        nKind = Anims.ALIEN3;
                    }
                    default -> throw new RuntimeException("Unhandled CASE value " + boss.moveSeq);
                }
                for (c = 1; c <= cnt; c++) {
                    angle = (short) (c * 360 / cnt);
                    px.set((short) (bx.get() + trigo.COS(angle) / 64));
                    py.set((short) (by.get() + trigo.SIN(angle) / 64));
                    alien = chaosActions.CreateObj(nKind, sKind, px.get(), py.get(), 0, nLife);
                    chaosActions.SetObjVXY(alien, (short) (trigo.COS(angle) * nSpeed), (short) (trigo.SIN(angle) * nSpeed));
                }
            } else if (boss.stat == 5) {
                chaosFire.BoumS(boss, chaosBase.mainPlayer, (short) 0, (short) 0, (short) 0, (short) 0, (short) 15, (short) 0, (short) 24, (short) 15, (short) 0, true, true, true);
                boss.hitSubLife = 0;
                boss.fireSubLife = 0;
                boss.stat = 2;
                boss.shapeSeq = ChaosBase.Period * (20 + trigo.RND() % 16);
            }
        }
        oldSeq = boss.shapeSeq;
        if (chaosBase.step > boss.shapeSeq)
            boss.shapeSeq = 0;
        else
            boss.shapeSeq -= chaosBase.step;
        switch (boss.stat) {
            case 0 -> {
                if (boss.shapeSeq < ChaosBase.Period * 10) {
                    chaosFire.GoCenter(boss);
                } else {
                    chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
                    chaosActions.GetCenter(boss, bx, by);
                    chaosFire.ReturnWeapon(bx.get(), by.get());
                    chaosActions.SetObjAXY(boss, (byte) (trigo.SGN((short) (px.get() - bx.get())) * 64), (byte) (trigo.SGN((short) (py.get() - by.get())) * 64));
                    chaosActions.LimitSpeed(boss, (short) 2200);
                    if ((oldSeq / 32) != (boss.shapeSeq / 32)) {
                        chaosActions.GetCenter(boss, px, py);
                        c = (short) (trigo.RND() % 3);
                        if (c == 0) {
                            chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                            nKind = Anims.MISSILE;
                            sKind = ChaosMissile.mAlien2;
                            cnt = ChaosMissile.mAcc2;
                        } else if (c == 1) {
                            nKind = Anims.STONE;
                            cnt = (short) Stones.stRBOX.ordinal();
                            sKind = 0;
                        } else {
                            nKind = Anims.STONE;
                            cnt = (short) Stones.stCBOX.ordinal();
                            sKind = 0;
                        }
                        alien = chaosActions.CreateObj(nKind, sKind, px.get(), py.get(), cnt, 12);
                        bx.set((short) (trigo.RND() % 2048));
                        by.set((short) (trigo.RND() % 2048));
                        chaosActions.SetObjVXY(alien, (short) (boss.vx + bx.get() - 1024), (short) (boss.vy + by.get() - 1536));
                        chaosActions.SetObjAXY(alien, (byte) 0, (byte) 48);
                    }
                }
            }
            case 1, 3 -> {
                chaosActions.GetCenter(boss, bx, by);
                if (((boss.ax >= 0) && (bx.get() > chaosGraphics.gameWidth / 2 + 117)) || (boss.ax == 0)) {
                    boss.ax = (byte) (trigo.RND() % 32);
                    boss.ax = (byte) (-boss.ax - 16);
                } else if ((boss.ax < 0) && (bx.get() < chaosGraphics.gameWidth / 2 - 117)) {
                    boss.ax = (byte) (trigo.RND() % 32 + 16);
                }
                if (((boss.ay >= 0) && (by.get() > chaosGraphics.gameHeight / 2 + 117)) || (boss.ay == 0)) {
                    boss.ay = (byte) (trigo.RND() % 32);
                    boss.ay = (byte) (-boss.ay - 16);
                } else if ((boss.ay < 0) && (by.get() < chaosGraphics.gameHeight / 2 - 117)) {
                    boss.ay = (byte) (trigo.RND() % 32 + 16);
                }
                chaosActions.LimitSpeed(boss, (short) 1536);
                chaosFire.ReturnWeapon(bx.get(), by.get());
            }
            case 2 -> {
                chaosActions.GetCenter(boss, px, py);
                chaosFire.ReturnWeapon(px.get(), py.get());
                if (chaosBase.step > boss.hitSubLife) {
                    chaosFire.FireMissileS(px.get(), py.get(), boss.vx, boss.vy, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, true);
                    boss.hitSubLife += ChaosBase.Period / 5 + trigo.RND() % (ChaosBase.Period * 8 / 5);
                    bx.set((short) (trigo.RND() % 64));
                    by.set((short) (trigo.RND() % 64));
                    px.inc(trigo.RND() % 2);
                    py.inc(trigo.RND() % 2);
                    chaosActions.SetObjAXY(boss, (byte) (trigo.SGN((short) (chaosGraphics.gameWidth / 2 - px.get())) * bx.get()), (byte) (trigo.SGN((short) (chaosGraphics.gameHeight / 2 - py.get())) * by.get()));
                }
                chaosActions.LimitSpeed(boss, (short) 1536);
                boss.hitSubLife -= chaosBase.step;
            }
            case 4, 5 -> {
                if ((boss.stat == 4) && (boss.shapeSeq < ChaosBase.Period) && (oldSeq >= ChaosBase.Period))
                    chaosSounds.SoundEffect(boss, chaosFire.huEffect);
                chaosFire.GoCenter(boss);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + boss.stat);
        }
        MakeMotherAlien(boss);
        hit.set(50);
        chaosActions.PlayerCollision(boss, hit);
    }

    private void AieMotherAlien(ChaosBase.Obj boss, ChaosBase.Obj src, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        if (src.kind == Anims.WEAPON)
            chaosActions.Die(src);
        if (boss.fireSubLife == 0)
            return;
        boss.moveSeq--;
        boss.life = boss.moveSeq * 50 + 1;
        chaosFire.ShowStat(Runtime.castToRef(languages.ADL("Mother: ##"), String.class), boss.moveSeq);
        if (boss.moveSeq != 0)
            chaosSounds.SoundEffect(boss, chaosFire.aieEffect);
        boss.hitSubLife = 0;
        boss.fireSubLife = 0;
        boss.stat = 4;
        boss.shapeSeq = ChaosBase.Period * 2;
    }

    private void DieMotherAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);

        if ((boss.moveSeq == 0) && (boss.stat == 0) && (boss.shapeSeq == 0)) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, motherEffect);
            boss.temperature = ChaosBase.MaxHot;
            chaosFire.turn = chaosBase.level[Zone.Family.ordinal()] > 4;
            chaosFire.BoumS(boss, boss, (short) 0, (short) 0, (short) 0, (short) 0, (short) 12, (short) 18, (short) 60, (short) 12, (short) 10, true, true, false);
        } else {
            boss.life = boss.moveSeq * 50 + 1;
            hit.set(20);
            fire.set(30);
            chaosActions.Aie(boss, boss, hit, fire);
        }
    }

    private void MakeFatherAlien(ChaosBase.Obj boss) {
        // VAR
        short px = 0;
        short py = 0;
        short sz = 0;
        short dt = 0;
        int v = 0;

        sz = 16;
        dt = 4;
        v = (boss.shapeSeq / 32) % 4;
        if (((boss.moveSeq % 2) != 0))
            v = 3 - v;
        switch (v) {
            case 0 -> {
                px = 172;
                py = 208;
                sz = 12;
                dt = 2;
            }
            case 1 -> {
                px = 172;
                py = 220;
            }
            case 2 -> {
                px = 188;
                py = 220;
            }
            case 3 -> {
                px = 204;
                py = 220;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + v);
        }
        chaosActions.SetObjLoc(boss, px, py, sz, sz);
        chaosActions.SetObjRect(boss, dt, dt, dt + 8, dt + 8);
    }

    private void ResetFatherAlien(ChaosBase.Obj boss) {
        chaosFire.theFather = boss;
        boss.hitSubLife = 20;
        boss.fireSubLife = 30;
        boss.moveSeq = 9 + chaosBase.difficulty / 2;
        boss.life = boss.moveSeq * 50 + 1;
        boss.shapeSeq = 4;
        boss.stat = 4;
        MakeFatherAlien(boss);
    }

    private void MoveFatherAlien(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj alien = null;
        ChaosBase.ObjAttr cattr = null;
        int lx = 0;
        int ly = 0;
        int dl = 0;
        short c = 0;
        short angle = 0;
        Runtime.Ref<Short> bx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> by = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short mul = 0;
        short nvx = 0;
        short nvy = 0;
        short nax = 0;
        short nay = 0;
        int oldSeq = 0;
        int dv = 0;
        int skcnt = 0;
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        int nLife = 0;
        Runtime.RangeSet sList = new Runtime.RangeSet(Memory.SET16_r);
        Anims nKind = Anims.PLAYER;
        short sKind = 0;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBackground(boss, (short) 4);
        chaosActions.AvoidBounds(boss, (short) 4);
        chaosActions.LimitSpeed(boss, (short) 3072);
        chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
        chaosActions.GetCenter(boss, bx, by);
        if (chaosBase.step > boss.shapeSeq) {
            chaosSounds.SoundEffect(boss, chaosFire.huEffect);
            switch (boss.stat) {
                case 0 -> {
                    boss.stat = 3 + trigo.RND() % 2;
                    boss.shapeSeq = ChaosBase.Period * (20 + trigo.RND() % 16);
                }
                case 1 -> {
                    boss.stat = 2;
                    boss.shapeSeq = ChaosBase.Period * 10;
                }
                case 2 -> {
                    if ((chaosBase.nbAnim[Anims.ALIEN2.ordinal()] < 8) && (chaosBase.nbAnim[Anims.ALIEN1.ordinal()] < 6)) {
                        if (boss.moveSeq % 3 == 0)
                            boss.stat = 3;
                        else
                            boss.stat = 4;
                        boss.shapeSeq = ChaosBase.Period * (20 + trigo.RND() % 16);
                    } else {
                        boss.shapeSeq = ChaosBase.Period * 10;
                    }
                }
                case 3 -> {
                    if (boss.moveSeq == 0) {
                        chaosFire.KillObjs(Anims.MACHINE, (short) ChaosMachine.mDoor);
                        chaosFire.KillObjs(Anims.DEADOBJ, (short) ChaosDObj.doMirror);
                        boss.stat = 5;
                        boss.shapeSeq = ChaosBase.Period * 50;
                    } else {
                        boss.stat = 6;
                        boss.shapeSeq = ChaosBase.Period * 8;
                    }
                }
                case 4 -> {
                    boss.stat = 6;
                    boss.shapeSeq = ChaosBase.Period * 8;
                }
                case 5 -> {
                    if (chaosFire.CloseEnough(bx.get(), by.get(), px.get(), py.get()) && (chaosBase.nbAnim[Anims.ALIEN2.ordinal()] + chaosBase.nbAnim[Anims.ALIEN1.ordinal()] == 0)) {
                        boss.shapeSeq = 0;
                        chaosActions.Die(boss);
                        return;
                    } else {
                        boss.shapeSeq = ChaosBase.Period * 2;
                    }
                }
                case 6 -> {
                    cattr = chaosBase.GetAnimAttr(Anims.ALIEN2, (short) ChaosCreator.cNest);
                    if ((cattr.nbObj < 3) && (chaosBase.level[Zone.Family.ordinal()] < 10))
                        alien = chaosActions.CreateObj(Anims.ALIEN2, (short) ChaosCreator.cNest, (short) (5 * ChaosGraphics.BW + ChaosGraphics.BW / 2), (short) (3 * ChaosGraphics.BW / 2), 0, 100);
                    boss.stat = 0;
                    boss.shapeSeq = ChaosBase.Period * (20 + trigo.RND() % 16);
                }
                case 7 -> {
                    boss.stat = 1;
                    boss.shapeSeq = ChaosBase.Period * (7 + (boss.moveSeq == 1 ? 1 : 0) * 6);
                }
                default -> throw new RuntimeException("Unhandled CASE value " + boss.stat);
            }
            if (boss.stat == 0) {
                boss.hitSubLife = 30;
                boss.fireSubLife = 20;
            } else {
                boss.hitSubLife = 0;
                boss.fireSubLife = 0;
            }
        }
        oldSeq = boss.shapeSeq;
        if (chaosBase.step > boss.shapeSeq)
            boss.shapeSeq = 0;
        else
            boss.shapeSeq -= chaosBase.step;
        switch (boss.stat) {
            case 0 -> {
                lx = px.get() - bx.get();
                ly = py.get() - by.get();
                dl = trigo.SQRT(lx * lx + ly * ly);
                if (dl != 0) {
                    boss.dvx = (short) (lx * 128 / dl * 16);
                    boss.dvy = (short) (ly * 128 / dl * 16);
                }
                dv = (boss.shapeSeq / (ChaosBase.Period / 3));
                if (oldSeq / (ChaosBase.Period / 3) != dv) {
                    if (dv % 2 == 0) {
                        dv = trigo.RND() % 3;
                        if (dv == 0)
                            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, (short) 0, (short) 0, true);
                        else if (dv == 1)
                            chaosFire.FireMissileA(boss, (short) 0, (short) 0);
                        else
                            chaosFire.FireMissileS(bx.get(), by.get(), -boss.dvx, -boss.dvy, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, true);
                    } else if (((bx.get() < 64) || (by.get() < 128) || (bx.get() > chaosGraphics.gameWidth - 64) || (by.get() > chaosGraphics.gameHeight - 64)) && (boss.shapeSeq > ChaosBase.Period * 2)) {
                        dv = trigo.RND() % 8;
                        if (dv < 4) {
                            nKind = Anims.ALIEN1;
                            sKind = ChaosAlien.aFlame;
                        } else if (dv < 6) {
                            nKind = Anims.ALIEN1;
                            sKind = ChaosAlien.aDiese;
                        } else {
                            nKind = Anims.ALIEN2;
                            sKind = ChaosCreator.cFour;
                        }
                        chaosSounds.SoundEffect(boss, chaosFire.flameEffect);
                        alien = chaosActions.CreateObj(nKind, sKind, bx.get(), by.get(), 0, chaosBase.pLife + 10);
                        chaosActions.SetObjVXY(alien, boss.dvx, boss.dvy);
                    }
                }
                if (dl < 56) {
                    boss.dvx = (short) -boss.dvx;
                    boss.dvy = (short) -boss.dvy;
                } else if (dl < 64) {
                    boss.dvx = 0;
                    boss.dvy = 0;
                }
                chaosActions.AvoidAnims(boss, EnumSet.of(Anims.WEAPON));
            }
            case 1 -> {
                lx = boss.vx;
                ly = boss.vy;
                dl = trigo.SQRT(lx * lx + ly * ly);
                if (dl == 0) {
                    boss.vx = 1536;
                    boss.vy = 0;
                } else {
                    boss.vx = (short) (lx * 1536 / dl);
                    boss.vy = (short) (ly * 1536 / dl);
                }
                boss.dvx = 0;
                boss.dvy = 0;
                boss.ax = (byte) -(boss.vy / 64);
                boss.ay = (byte) (boss.vy / 64);
                dv = (boss.shapeSeq / (ChaosBase.Period / 2));
                if ((oldSeq / (ChaosBase.Period / 2)) != dv) {
                    nKind = Anims.ALIEN2;
                    skcnt = 3;
                    switch (boss.moveSeq) {
                        case 0, 14 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cChief, ChaosCreator.cCircle, ChaosCreator.cCreatorC);
                        }
                        case 1, 13 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cCreatorR, ChaosCreator.cCreatorC, ChaosCreator.cNest);
                        }
                        case 2 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cChief, ChaosCreator.cCreatorC, ChaosCreator.cAlienBox);
                        }
                        case 3 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cNest, ChaosCreator.cCreatorR, ChaosCreator.cQuad);
                        }
                        case 4 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosAlien.aTrefle, ChaosAlien.aTri, ChaosAlien.aHospital);
                            nKind = Anims.ALIEN1;
                        }
                        case 5 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cQuad, ChaosCreator.cFour, ChaosCreator.cCreatorC);
                        }
                        case 6 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cCreatorR, ChaosCreator.cAlienV, ChaosCreator.cAlienA);
                        }
                        case 7 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cNest, ChaosCreator.cFour, ChaosCreator.cAlienA);
                        }
                        case 8 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cAlienBox, ChaosCreator.cQuad, ChaosCreator.cAlienV);
                        }
                        case 9 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cAlienV, ChaosCreator.cAlienA, ChaosCreator.cFour);
                        }
                        case 10 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cGrid, ChaosCreator.cChief, ChaosCreator.cHurryUp);
                        }
                        case 11 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cPopUp, ChaosCreator.cGhost);
                            skcnt = 2;
                        }
                        case 12 -> {
                            sList = new Runtime.RangeSet(Memory.SET16_r).with(ChaosAlien.aKamikaze, ChaosAlien.aPic, ChaosAlien.aStar);
                            nKind = Anims.ALIEN1;
                        }
                        default -> throw new RuntimeException("Unhandled CASE value " + boss.moveSeq);
                    }
                    skcnt = trigo.RND() % skcnt;
                    sKind = 0;
                    while (!sList.contains(sKind)) {
                        sKind++;
                    }
                    while (skcnt > 0) {
                        do {
                            sKind = (short) ((sKind + 1) % 16);
                        } while (!sList.contains(sKind));
                        skcnt--;
                    }
                    if ((nKind == Anims.ALIEN2) && (new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cCircle, ChaosCreator.cCreatorR, ChaosCreator.cCreatorC, ChaosCreator.cNest).contains(sKind)))
                        nLife = chaosBase.pLife * 2 + 120;
                    else
                        nLife = chaosBase.pLife * 3 + 10;
                    chaosSounds.SoundEffect(boss, chaosFire.flameEffect);
                    nLife += trigo.RND() % 4;
                    alien = chaosActions.CreateObj(nKind, sKind, bx.get(), by.get(), trigo.RND() % 4, nLife);
                    angle = (short) (trigo.RND() % 360);
                    chaosActions.SetObjVXY(alien, trigo.COS(angle), trigo.SIN(angle));
                }
            }
            case 2 -> {
                if (((boss.ax >= 0) && (bx.get() > chaosGraphics.gameWidth / 2 + 118)) || (boss.ax == 0)) {
                    boss.ax = (byte) (trigo.RND() % 32);
                    boss.ax = (byte) (-boss.ax - 16);
                } else if ((boss.ax < 0) && (bx.get() < chaosGraphics.gameWidth / 2 - 118)) {
                    boss.ax = (byte) (trigo.RND() % 32 + 16);
                }
                if (((boss.ay >= 0) && (by.get() > chaosGraphics.gameHeight / 2 + 118)) || (boss.ay == 0)) {
                    boss.ay = (byte) (trigo.RND() % 32);
                    boss.ay = (byte) (-boss.ay - 16);
                } else if ((boss.ay < 0) && (by.get() < chaosGraphics.gameHeight / 2 - 118)) {
                    boss.ay = (byte) (trigo.RND() % 32 + 16);
                }
                chaosActions.LimitSpeed(boss, (short) 1536);
                chaosFire.ReturnWeapon(bx.get(), by.get());
            }
            case 3 -> {
                chaosFire.GoCenter(boss);
                chaosFire.ReturnWeapon(bx.get(), by.get());
                dv = (boss.shapeSeq / (ChaosBase.Period / 3));
                if ((oldSeq / (ChaosBase.Period / 3)) != dv) {
                    chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                    if ((Math.abs(px.get() - bx.get()) < 56) && (Math.abs(py.get() - by.get()) < 56) && (trigo.RND() % 6 == 0))
                        chaosFire.BoumS(boss, boss, (short) 0, (short) 0, (short) (trigo.RND() % 18), (short) 0, (short) 18, (short) 0, (short) 20, (short) 12, (short) 0, false, false, false);
                    for (c = 1; c <= 4; c++) {
                        angle = (short) (trigo.RND() % 360);
                        nvx = (short) (trigo.COS(angle) * 2);
                        nvy = (short) (trigo.SIN(angle) * 2);
                        nax = (short) (trigo.RND() % 200);
                        nax -= 100;
                        nay = (short) (trigo.RND() % 200);
                        nay -= 100;
                        if (chaosFire.ParabolicDist(bx.get(), by.get(), nvx, nvy, nax, nay, px.get(), py.get()) > 24) {
                            alien = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien1, bx.get(), by.get(), trigo.RND() % 3, 12);
                            chaosActions.SetObjVXY(alien, nvx, nvy);
                            chaosActions.SetObjAXY(alien, (byte) nax, (byte) nay);
                        }
                    }
                }
            }
            case 4 -> {
                chaosFire.GoTo(boss, (short) (chaosGraphics.gameWidth / 2), (short) (chaosGraphics.gameHeight / 2 + 70));
                chaosFire.ReturnWeapon(bx.get(), by.get());
                dv = boss.shapeSeq / 60;
                if ((oldSeq / 60 != dv) && (Math.abs(boss.vx) < 64) && (Math.abs(boss.vy) < 64)) {
                    chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                    dv = (dv % 30);
                    if ((((boss.moveSeq / 2) % 2) != 0))
                        dv = 29 - dv;
                    angle = (short) (dv * 12 + boss.shapeSeq / 750);
                    mul = (short) ((boss.moveSeq / 3) + 1);
                    if (!((boss.moveSeq % 2) != 0))
                        mul = (short) (mul * 4);
                    alien = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien2, bx.get(), by.get(), ChaosMissile.mAcc2, 12);
                    chaosActions.SetObjVXY(alien, (short) (trigo.COS(angle) * 4 / mul), (short) (trigo.SIN(angle) / 2));
                    if (((boss.moveSeq % 2) != 0)) {
                        mul = (short) (mul * 16);
                        chaosActions.SetObjAXY(alien, (byte) -(trigo.COS(angle) / mul), (byte) -(trigo.SIN(angle) / 32));
                    }
                }
            }
            case 5 -> {
                if (boss.shapeSeq > ChaosBase.Period * 10) {
                    boss.ax = 0;
                    boss.ay = 0;
                    boss.dvx = (short) (trigo.SGN((short) (px.get() - bx.get())) * 800);
                    boss.dvy = (short) (trigo.SGN((short) (py.get() - by.get())) * 800);
                    dv = boss.shapeSeq / ChaosBase.Period;
                    if (oldSeq / ChaosBase.Period != dv) {
                        dv = trigo.RND() % 4;
                        if (dv == 0)
                            chaosFire.FireFlame(boss, (short) 0, (short) 0, true);
                        else if (dv == 1)
                            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, (short) 0, (short) 0, true);
                        else if (dv == 2)
                            chaosFire.FireMissileA(boss, (short) 0, (short) 0);
                        else
                            chaosFire.FireMissileS(bx.get(), by.get(), -boss.dvx, -boss.dvy, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, true);
                    }
                } else {
                    chaosFire.GoCenter(boss);
                    if ((boss.shapeSeq == 0) && chaosFire.CloseEnough(bx.get(), by.get(), px.get(), py.get())) {
                        boss.shapeSeq = 0;
                        chaosActions.Die(boss);
                        return;
                    }
                }
            }
            case 6, 7 -> {
                chaosFire.GoCenter(boss);
                chaosFire.ReturnWeapon(bx.get(), by.get());
            }
            default -> throw new RuntimeException("Unhandled CASE value " + boss.stat);
        }
        MakeFatherAlien(boss);
        hit.set(50);
        chaosActions.PlayerCollision(boss, hit);
    }

    private void AieFatherAlien(ChaosBase.Obj boss, ChaosBase.Obj src, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        hit.set(0);
        fire.set(0);
        if (src.kind == Anims.WEAPON)
            chaosActions.Die(src);
        if (boss.fireSubLife == 0)
            return;
        boss.moveSeq--;
        boss.life = boss.moveSeq * 50 + 1;
        chaosFire.ShowStat(Runtime.castToRef(languages.ADL("FATHER: ##"), String.class), boss.moveSeq);
        if (boss.moveSeq != 0)
            chaosSounds.SoundEffect(boss, chaosFire.aieEffect);
        boss.hitSubLife = 0;
        boss.fireSubLife = 0;
        boss.stat = 7;
        boss.shapeSeq = ChaosBase.Period;
    }

    private void DieFatherAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);
        short angle = 0;
        short ox = 0;
        short oy = 0;
        short c = 0;

        if ((boss.moveSeq == 0) && (boss.stat == 5) && (boss.shapeSeq == 0)) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, fatherEffect);
            chaosFire.BoumS(boss, boss, (short) 0, (short) 0, (short) 6, (short) 0, (short) 18, (short) 0, (short) 20, (short) 16, (short) 0, true, false, false);
            angle = (short) (trigo.RND() % 120);
            for (c = 0; c <= 2; c++) {
                ox = (short) (trigo.COS(angle) / 16);
                oy = (short) (trigo.SIN(angle) / 16);
                chaosFire.BoumS(boss, boss, ox, oy, (short) 0, (short) 0, (short) 18, (short) 0, (short) 20, (short) 8, (short) 0, true, false, false);
                angle += 120;
            }
        } else {
            boss.life = boss.moveSeq * 50 + 1;
            hit.set(30);
            fire.set(20);
            chaosActions.Aie(boss, boss, hit, fire);
        }
    }

    private void MakeMasterAlien(ChaosBase.Obj boss) {
        chaosActions.SetObjLoc(boss, (short) 88, (short) 200, (short) 84, (short) 36);
        chaosActions.SetObjRect(boss, 8, 4, 68, 28);
    }

    private void ResetMasterAlien(ChaosBase.Obj boss) {
        chaosFire.theMaster = boss;
        chaosFire.laugh = false;
        boss.hitSubLife = 0;
        boss.fireSubLife = 0;
        boss.moveSeq = 15;
        boss.life = boss.moveSeq * 50 + 1;
        boss.stat = 3;
        boss.shapeSeq = 1;
        MakeMasterAlien(boss);
    }

    private void MoveMasterAlien(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj fObj = null;
        ChaosBase.Obj alien = null;
        int dx = 0;
        int dy = 0;
        int dl = 0;
        short angle = 0;
        Runtime.Ref<Short> bx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> by = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short mx = 0;
        short my = 0;
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        int oldSeq = 0;
        short sKind = 0;
        boolean speed = false;
        boolean inv = false;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBackground(boss, (short) 1);
        chaosActions.LimitSpeed(boss, (short) 2048);
        inv = (chaosBase.screenInverted > 0);
        if (chaosFire.laugh != inv) {
            if (chaosFire.laugh) {
                hit.set((int) chaosBase.nbDollar);
                hit.inc(chaosBase.nbSterling);
                if ((chaosBase.pLife == 0) && (hit.get() < 20))
                    chaosSounds.SoundEffect(boss, haha2Effect);
                else
                    chaosSounds.SoundEffect(boss, haha1Effect);
            }
            chaosFire.laugh = !chaosFire.laugh;
        }
        chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
        chaosActions.GetCenter(boss, bx, by);
        if (chaosBase.step >= boss.shapeSeq) {
            switch (boss.stat) {
                case 1 -> {
                    boss.stat = 2;
                    boss.shapeSeq = ChaosBase.Period * (20 + trigo.RND() % 20);
                }
                case 2 -> {
                    boss.stat = 3;
                    boss.shapeSeq = ChaosBase.Period * 3;
                }
                case 3, 4 -> {
                    if (boss.moveSeq == 0) {
                        boss.stat = 5;
                        boss.shapeSeq = ChaosBase.Period * 30;
                    } else {
                        if (boss.moveSeq > 10) {
                            fObj = chaosBase.mainPlayer;
                            speed = true;
                        } else if (boss.moveSeq > 5) {
                            fObj = boss;
                            speed = false;
                        } else {
                            fObj = chaosBase.mainPlayer;
                            speed = false;
                        }
                        angle = (short) (trigo.RND() % 360);
                        switch (boss.moveSeq % 5) {
                            case 0 -> {
                                chaosFire.BoumS(boss, fObj, (short) 0, (short) 0, (short) 0, angle, (short) 12, (short) 18, (short) 60, (short) 12, (short) 10, true, speed, true);
                            }
                            case 4 -> {
                                chaosFire.BoumS(boss, fObj, (short) 0, (short) 0, (short) 0, (short) 0, (short) 15, (short) 0, (short) 24, (short) 14, (short) 0, true, speed, true);
                            }
                            case 3 -> {
                                chaosFire.BoumS(boss, fObj, (short) 0, (short) 0, (short) 0, angle, (short) 15, (short) 45, (short) 24, (short) 12, (short) 10, true, speed, true);
                            }
                            case 2 -> {
                                chaosSounds.SoundEffect(boss, chaosFire.bombEffect);
                                boss.shapeSeq = 2;
                                chaosFire.BoumS(boss, fObj, (short) -56, (short) 8, (short) 0, (short) 0, (short) 15, (short) 0, (short) 24, (short) 13, (short) 0, true, speed, false);
                                boss.shapeSeq = 1;
                                chaosFire.BoumS(boss, fObj, (short) 56, (short) 8, (short) 0, (short) 0, (short) 15, (short) 0, (short) 24, (short) 13, (short) 0, true, speed, false);
                                boss.shapeSeq = 0;
                                chaosFire.BoumS(boss, fObj, (short) 0, (short) -60, (short) 0, (short) 0, (short) 15, (short) 0, (short) 24, (short) 13, (short) 0, true, speed, false);
                            }
                            case 1 -> {
                                chaosFire.BoumS(boss, fObj, (short) 0, (short) 0, (short) 0, (short) 0, (short) 15, (short) 105, (short) 24, (short) 12, (short) 10, true, speed, true);
                            }
                            default -> throw new RuntimeException("Unhandled CASE value " + boss.moveSeq % 5);
                        }
                        boss.stat = 1;
                        boss.shapeSeq = ChaosBase.Period * 4;
                    }
                }
                case 5 -> {
                    boss.stat = 6;
                }
                case 6 -> {
                    if (chaosFire.CloseEnough(bx.get(), by.get(), px.get(), py.get()) && ((chaosBase.nbAnim[Anims.ALIEN2.ordinal()] + chaosBase.nbAnim[Anims.ALIEN1.ordinal()] + chaosBase.nbAnim[Anims.MISSILE.ordinal()] + chaosBase.nbAnim[Anims.STONE.ordinal()] == 0) || (chaosBase.level[Zone.Family.ordinal()] > 6))) {
                        boss.shapeSeq = 0;
                        chaosActions.Die(boss);
                        return;
                    } else {
                        boss.shapeSeq = ChaosBase.Period * 2;
                    }
                }
                default -> throw new RuntimeException("Unhandled CASE value " + boss.stat);
            }
        }
        if (boss.stat == 3) {
            boss.hitSubLife = 25;
            boss.fireSubLife = 25;
        } else {
            boss.fireSubLife = 0;
        }
        oldSeq = boss.shapeSeq;
        if (chaosBase.step >= boss.shapeSeq)
            boss.shapeSeq = 0;
        else
            boss.shapeSeq -= chaosBase.step;
        switch (boss.stat) {
            case 1 -> {
                chaosFire.GoCenter(boss);
                chaosFire.ReturnWeapon(bx.get(), by.get());
            }
            case 2 -> {
                chaosFire.ReturnWeapon(bx.get(), by.get());
                if (chaosBase.step > boss.hitSubLife) {
                    mx = (short) (trigo.RND() % 64);
                    my = (short) (trigo.RND() % 64);
                    bx.inc(trigo.RND() % 2);
                    by.inc(trigo.RND() % 2);
                    chaosActions.SetObjAXY(boss, (byte) (trigo.SGN((short) (chaosGraphics.gameWidth / 2 - bx.get())) * mx), (byte) (trigo.SGN((short) (chaosGraphics.gameHeight / 2 - by.get())) * my));
                    switch ((boss.moveSeq % 5)) {
                        case 0 -> {
                            chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, (short) -28, (short) 4, false);
                            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, (short) 28, (short) 4, false);
                            boss.hitSubLife += trigo.RND() % ChaosBase.Period;
                        }
                        case 4 -> {
                            chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                            angle = (short) (((boss.shapeSeq / 60) % 30) * 12);
                            mx = (short) (trigo.COS(angle) * 2);
                            my = (short) (trigo.SIN(angle) * 2);
                            chaosFire.FireMissileS(bx.get() - 28, by.get() + 4, mx, my, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, false);
                            mx = (short) -mx;
                            my = (short) -my;
                            chaosFire.FireMissileS(bx.get() + 28, by.get() + 4, mx, my, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, false);
                            boss.hitSubLife += ChaosBase.Period / 3;
                        }
                        case 3 -> {
                            if (trigo.RND() % 2 == 0)
                                mx = 28;
                            else
                                mx = -28;
                            chaosFire.FireFlame(boss, mx, (short) 4, true);
                            boss.hitSubLife += trigo.RND() % ChaosBase.Period + ChaosBase.Period / 2;
                        }
                        case 2 -> {
                            chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                            dx = bx.get() - 28 - px.get();
                            dy = by.get() + 4 - py.get();
                            dl = trigo.SQRT(dx * dx + dy * dy);
                            if (dl != 0) {
                                dx = dx * 3072 / dl;
                                dy = dy * 3072 / dl;
                            }
                            chaosFire.FireMissileS(bx.get() - 28, by.get() + 4, dx, dy, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, false);
                            dx = bx.get() + 28 - px.get();
                            dy = by.get() + 4 - py.get();
                            dl = trigo.SQRT(dx * dx + dy * dy);
                            if (dl != 0) {
                                dx = dx * 3072 / dl;
                                dy = dy * 3072 / dl;
                            }
                            chaosFire.FireMissileS(bx.get() + 28, by.get() + 4, dx, dy, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, false);
                            boss.hitSubLife += trigo.RND() % ChaosBase.Period;
                        }
                        case 1 -> {
                            chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                            angle = (short) (((boss.shapeSeq / 60) % 30) * 12);
                            chaosFire.FireMissileS(bx.get(), by.get(), trigo.COS(angle) * 2, trigo.SIN(angle) * 2, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, false);
                            angle += 120;
                            if (boss.moveSeq < 5)
                                chaosFire.FireMissileS(bx.get(), by.get(), trigo.COS(angle) * 2, trigo.SIN(angle) * 2, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, false);
                            angle += 120;
                            if (boss.moveSeq < 10)
                                chaosFire.FireMissileS(bx.get(), by.get(), trigo.COS(angle) * 2, trigo.SIN(angle) * 2, boss, chaosBase.mainPlayer, (short) 0, (short) 0, true, false);
                            boss.hitSubLife += ChaosBase.Period / 4;
                        }
                        default -> throw new RuntimeException("Unhandled CASE value " + (boss.moveSeq % 5));
                    }
                }
                if (chaosBase.step > boss.hitSubLife)
                    boss.hitSubLife = 0;
                else
                    boss.hitSubLife -= chaosBase.step;
                chaosActions.LimitSpeed(boss, (short) 1536);
            }
            case 3 -> {
                chaosFire.GoCenter(boss);
            }
            case 4 -> {
                if ((oldSeq >= ChaosBase.Period) && (boss.shapeSeq < ChaosBase.Period))
                    chaosSounds.SoundEffect(boss, chaosFire.huEffect);
                chaosFire.GoCenter(boss);
                chaosFire.ReturnWeapon(bx.get(), by.get());
            }
            case 5 -> {
                if ((boss.shapeSeq / ChaosBase.Period) != (oldSeq / ChaosBase.Period)) {
                    chaosSounds.SoundEffect(boss, chaosFire.createEffect);
                    sKind = (short) (trigo.RND() % 4);
                    if (sKind == 0)
                        sKind = ChaosCreator.cNest;
                    else if (sKind == 1)
                        sKind = ChaosCreator.cCircle;
                    else if (sKind == 2)
                        sKind = ChaosCreator.cCreatorR;
                    else
                        sKind = ChaosCreator.cCreatorC;
                    alien = chaosActions.CreateObj(Anims.ALIEN2, sKind, bx.get(), by.get(), 0, 80);
                }
                chaosFire.GoTo(boss, px.get(), py.get());
            }
            case 6 -> {
                chaosFire.GoCenter(boss);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + boss.stat);
        }
        MakeMasterAlien(boss);
        hit.set(50);
        chaosActions.PlayerCollision(boss, hit);
    }

    private void AieMasterAlien(ChaosBase.Obj boss, ChaosBase.Obj src, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        hit.set(0);
        fire.set(0);
        if (src.kind == Anims.WEAPON)
            chaosActions.Die(src);
        if (boss.fireSubLife == 0)
            return;
        boss.moveSeq--;
        boss.life = boss.moveSeq * 50 + 1;
        chaosFire.ShowStat(Runtime.castToRef(languages.ADL("MASTER: ##"), String.class), boss.moveSeq);
        if (boss.moveSeq != 0)
            chaosSounds.SoundEffect(boss, chaosFire.aieEffect);
        boss.hitSubLife = 0;
        boss.fireSubLife = 0;
        boss.stat = 4;
        boss.shapeSeq = ChaosBase.Period * 2;
    }

    private void DieMasterAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);

        if ((boss.moveSeq == 0) && (boss.stat == 6) && (boss.shapeSeq == 0)) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, sisterEffect);
            chaosSounds.SoundEffect(boss, motherEffect);
            boss.shapeSeq = 2;
            chaosFire.BoumS(boss, boss, (short) -69, (short) 40, (short) 0, (short) 0, (short) 12, (short) 18, (short) 60, (short) 12, (short) 10, true, false, false);
            boss.shapeSeq = 1;
            chaosFire.BoumS(boss, boss, (short) 69, (short) 40, (short) 0, (short) 0, (short) 12, (short) 18, (short) 60, (short) 12, (short) 10, true, false, false);
            boss.shapeSeq = 0;
            chaosFire.BoumS(boss, boss, (short) 0, (short) -80, (short) 0, (short) 0, (short) 12, (short) 18, (short) 60, (short) 12, (short) 10, true, false, false);
        } else {
            boss.life = boss.moveSeq * 50 + 1;
            hit.set(25);
            fire.set(25);
            chaosActions.Aie(boss, boss, hit, fire);
        }
    }

    private void MakeIllusion(ChaosBase.Obj boss) {
        chaosActions.SetObjLoc(boss, (short) 59, (short) 243, (short) 10, (short) 10);
        chaosActions.SetObjRect(boss, -3, -3, 19, 19);
    }

    private void ResetIllusion(ChaosBase.Obj boss) {
        chaosFire.theIllusion = boss;
        boss.hitSubLife = 10;
        boss.fireSubLife = 10;
        boss.moveSeq = 10 + chaosBase.difficulty / 2;
        boss.life = boss.moveSeq * 50 + 1;
        boss.stat = 0;
        boss.shapeSeq = 1;
        MakeIllusion(boss);
    }

    private void MoveIllusion(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj alien = null;
        int nLife = 0;
        int oldSeq = 0;
        int dv = 0;
        Runtime.Ref<Short> bx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> by = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short angle = 0;
        short sKind = 0;
        Anims nKind = Anims.PLAYER;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBounds(boss, (short) 2);
        chaosActions.AvoidBackground(boss, (short) 4);
        chaosActions.GetCenter(boss, bx, by);
        chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
        if ((boss.stat != 33) || (boss.shapeSeq > ChaosBase.Period)) {
            boss.dvx = boss.vx;
            boss.dvy = boss.vy;
            if (boss.dvx < -1800)
                boss.dvx = -1800;
            else if (boss.dvx > 1800)
                boss.dvx = 1800;
            if (boss.dvy < -1800)
                boss.dvy = -1800;
            else if (boss.dvy > 1800)
                boss.dvy = 1800;
        } else {
            if (chaosBase.level[Zone.Family.ordinal()] <= 6) {
                boss.dvx = 0;
                boss.dvy = 0;
            } else {
                chaosFire.GoCenter(boss);
            }
        }
        if (chaosBase.step >= boss.shapeSeq) {
            if (boss.stat < 33) {
                boss.shapeSeq = ChaosBase.Period * (20 + trigo.RND() % 16);
                if (boss.moveSeq == 0)
                    boss.stat = 33;
                else
                    boss.stat = trigo.RND() % 32 + 1;
            } else {
                if (chaosFire.CloseEnough(bx.get(), by.get(), px.get(), py.get())) {
                    chaosFire.KillObjs(Anims.DEADOBJ, (short) ChaosDObj.doMagnetR);
                    chaosFire.KillObjs(Anims.DEADOBJ, (short) ChaosDObj.doMagnetA);
                    chaosFire.KillObjs(Anims.DEADOBJ, (short) ChaosDObj.doWindMaker);
                    chaosFire.KillObjs(Anims.DEADOBJ, (short) ChaosDObj.doMirror);
                    boss.shapeSeq = 0;
                    chaosActions.Die(boss);
                    return;
                } else {
                    boss.shapeSeq = 511;
                }
            }
        }
        oldSeq = boss.shapeSeq;
        if (chaosBase.step >= boss.shapeSeq)
            boss.shapeSeq = 0;
        else
            boss.shapeSeq -= chaosBase.step;
        dv = ChaosBase.Period * (3 + boss.moveSeq / 3);
        if (boss.stat == 0)
            chaosFire.GoCenter(boss);
        if ((boss.shapeSeq / dv) != (oldSeq / dv)) {
            if ((trigo.RND() % 8 == 0) || (boss.vx == 0)) {
                chaosFire.BoumX(boss, boss, (short) 48, ((boss.moveSeq % 2) != 0));
                angle = (short) (trigo.RND() % 360);
                boss.vx = (short) (trigo.COS(angle) * 3 / 2);
                boss.vy = (short) (trigo.SIN(angle) * 3 / 2);
            }
            if ((boss.stat > 0) && (boss.stat <= 29)) {
                if (boss.stat <= 12) {
                    nKind = Anims.MACHINE;
                    nLife = trigo.RND() % 2;
                } else if (boss.stat <= 15) {
                    nKind = Anims.ALIEN2;
                    nLife = 80;
                } else if (boss.stat <= 19) {
                    nKind = Anims.ALIEN2;
                    nLife = chaosBase.pLife * 2;
                } else if (boss.stat <= 27) {
                    nKind = Anims.ALIEN1;
                    nLife = chaosBase.pLife * 2;
                } else {
                    nKind = Anims.ALIEN1;
                    nLife = trigo.RND() % 4;
                }
                switch (boss.stat) {
                    case 1, 2, 3 -> {
                        sKind = ChaosMachine.mCannon1;
                    }
                    case 4, 5, 6 -> {
                        sKind = ChaosMachine.mCannon2;
                    }
                    case 7, 8, 9 -> {
                        sKind = ChaosMachine.mCannon3;
                    }
                    case 10, 11, 12 -> {
                        sKind = ChaosMachine.mTurret;
                    }
                    case 13 -> {
                        sKind = ChaosCreator.cCreatorR;
                    }
                    case 14 -> {
                        sKind = ChaosCreator.cCreatorC;
                    }
                    case 15 -> {
                        sKind = ChaosCreator.cNest;
                    }
                    case 16 -> {
                        sKind = ChaosCreator.cAlienV;
                    }
                    case 17 -> {
                        sKind = ChaosCreator.cAlienA;
                    }
                    case 18 -> {
                        sKind = ChaosCreator.cFour;
                    }
                    case 19 -> {
                        sKind = ChaosCreator.cQuad;
                    }
                    case 20 -> {
                        sKind = ChaosAlien.aTri;
                    }
                    case 21 -> {
                        sKind = ChaosAlien.aDbOval;
                    }
                    case 22 -> {
                        sKind = ChaosAlien.aHospital;
                    }
                    case 23 -> {
                        sKind = ChaosAlien.aDiese;
                    }
                    case 24 -> {
                        sKind = ChaosAlien.aTrefle;
                    }
                    case 25 -> {
                        sKind = ChaosAlien.aColor;
                    }
                    case 26 -> {
                        sKind = ChaosAlien.aStar;
                    }
                    case 27 -> {
                        sKind = ChaosAlien.aBubble;
                    }
                    case 28 -> {
                        sKind = ChaosAlien.aKamikaze;
                    }
                    case 29 -> {
                        sKind = ChaosAlien.aPic;
                    }
                    default -> {
                    }
                }
                alien = chaosActions.CreateObj(nKind, sKind, bx.get(), by.get(), nLife, nLife);
                chaosSounds.SoundEffect(boss, chaosFire.huEffect);
            } else if (boss.stat == 33) {
                chaosSounds.SoundEffect(boss, chaosFire.createEffect);
                if (trigo.RND() % 3 == 0)
                    sKind = ChaosCreator.cCircle;
                else
                    sKind = ChaosCreator.cChief;
                alien = chaosActions.CreateObj(Anims.ALIEN2, sKind, bx.get(), by.get(), 0, chaosBase.pLife * 3);
            }
        }
        MakeIllusion(boss);
    }

    private void AieIllusion(ChaosBase.Obj boss, ChaosBase.Obj src, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        hit.set(0);
        fire.set(0);
        if (src.kind == Anims.WEAPON)
            chaosActions.Die(src);
    }

    private void DieIllusion(ChaosBase.Obj boss) {
        // VAR
        short off = 0;
        short rx = 0;
        short ry = 0;

        if ((boss.moveSeq == 0) && (boss.stat == 33) && (boss.shapeSeq == 0)) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, brotherEffect);
            chaosSounds.SoundEffect(boss, fatherEffect);
            rx = 0;
            ry = 0;
            for (off = 0; off <= 3; off++) {
                chaosFire.turn = true;
                chaosFire.BoumS(boss, boss, rx, ry, (short) 0, (short) 0, (short) 12, (short) 0, (short) 30, (short) 12, (short) 0, true, false, false);
                rx = (short) (trigo.COS((short) (off * 120)) / 16);
                ry = (short) (trigo.SIN((short) (off * 120)) / 16);
            }
            chaosFire.theIllusion = null;
            return;
        } else if ((boss.stat > 0) && (boss.stat <= 32)) {
            chaosActions.Boum(boss, EnumSet.of(Stones.stFOG3), (short) ChaosBase.fastStyle, (short) 31, (short) 1);
            boss.moveSeq--;
            boss.life = boss.moveSeq * 50;
            chaosFire.ShowStat(Runtime.castToRef(languages.ADL("Illusion: ##"), String.class), boss.moveSeq);
            if (boss.moveSeq != 0)
                chaosSounds.SoundEffect(boss, chaosFire.aieEffect);
            boss.ax = 0;
            boss.ay = 0;
            boss.dvx = 0;
            boss.dvy = 0;
            boss.vx = 0;
            boss.vy = -4000;
            boss.stat = 0;
            boss.shapeSeq = 511;
        }
        boss.life = boss.moveSeq * 50 + 1;
    }

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;
        int c = 0;

        chaosSounds.SetEffect(chaosFire.missileEffect[0], chaosSounds.soundList[SoundList.sMissile.ordinal()], 0, 0, (short) 100, (short) 4);
        chaosSounds.SetEffect(chaosFire.flameEffect[0], chaosSounds.soundList[SoundList.sHHat.ordinal()], 0, 0, (short) 180, (short) 4);
        chaosSounds.SetEffect(chaosFire.bombEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 0, 0, (short) 220, (short) 7);
        chaosSounds.SetEffect(chaosFire.huEffect[0], chaosSounds.soundList[SoundList.sHurryUp.ordinal()], 0, 0, (short) 255, (short) 4);
        chaosSounds.SetEffect(chaosFire.poufEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 0, 4181, (short) 255, (short) 14);
        chaosSounds.SetEffect(chaosFire.createEffect[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 4181, (short) 220, (short) 12);
        chaosSounds.SetEffect(chaosFire.aieEffect[0], chaosSounds.soundList[SoundList.sGong.ordinal()], 0, 16726, (short) 200, (short) 13);
        chaosSounds.SetEffect(chaosFire.koEffect[0], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 0, (short) 240, (short) 14);
        chaosSounds.SetEffect(brotherEffect[0], chaosSounds.soundList[SoundList.sVerre.ordinal()], 0, 4181, (short) 255, (short) 15);
        chaosSounds.SetEffect(sisterEffect[0], chaosSounds.soundList[SoundList.sGong.ordinal()], 0, 4181, (short) 255, (short) 15);
        chaosSounds.SetEffect(motherEffect[0], chaosSounds.soundList[SoundList.sCasserole.ordinal()], 0, 4181, (short) 255, (short) 15);
        chaosSounds.SetEffect(fatherEffect[0], chaosSounds.soundList[SoundList.sCannon.ordinal()], 0, 4181, (short) 255, (short) 15);
        chaosSounds.SetEffect(haha1Effect[0], chaosSounds.soundList[SoundList.wJans.ordinal()], 0, 16726, (short) 0, (short) 8);
        chaosSounds.SetEffect(haha2Effect[0], chaosSounds.soundList[SoundList.wJans.ordinal()], 0, 0, (short) 0, (short) 8);
        for (c = 1; c <= 3; c++) {
            chaosSounds.SetEffect(haha1Effect[c], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 0, (short) 160, (short) 8);
            chaosSounds.SetEffect(haha2Effect[c], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 0, (short) 250, (short) 8);
        }
        chaosSounds.SetEffect(haha2Effect[4], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 7032, (short) 250, (short) 8);
        chaosSounds.SetEffect(haha2Effect[5], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 7032, (short) 250, (short) 8);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetBrotherAlien, "ChaosBoss.ResetBrotherAlien");
        attr.Make = Runtime.proc(this::MakeBrotherAlien, "ChaosBoss.MakeBrotherAlien");
        attr.Move = Runtime.proc(this::MoveBrotherAlien, "ChaosBoss.MoveBrotherAlien");
        attr.Aie = Runtime.proc(this::AieBrotherAlien, "ChaosBoss.AieBrotherAlien");
        attr.Die = Runtime.proc(this::DieBrotherAlien, "ChaosBoss.DieBrotherAlien");
        attr.charge = 16;
        attr.weight = 100;
        attr.inerty = 192;
        attr.priority = -65;
        attr.heatSpeed = 20;
        attr.refreshSpeed = 40;
        attr.coolSpeed = 120;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.aieStone = (short) (ChaosBase.FlameMult * 7 + 15);
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stSTAR1, Stones.stSTAR2);
        attr.dieSKCount = 2;
        attr.dieStone = (short) (ChaosBase.FlameMult * 7 + 31);
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetSisterAlien, "ChaosBoss.ResetSisterAlien");
        attr.Make = Runtime.proc(this::MakeSisterAlien, "ChaosBoss.MakeSisterAlien");
        attr.Move = Runtime.proc(this::MoveSisterAlien, "ChaosBoss.MoveSisterAlien");
        attr.Aie = Runtime.proc(this::AieSisterAlien, "ChaosBoss.AieSisterAlien");
        attr.Die = Runtime.proc(this::DieSisterAlien, "ChaosBoss.DieSisterAlien");
        attr.charge = 24;
        attr.weight = 60;
        attr.inerty = 192;
        attr.priority = -65;
        attr.heatSpeed = 80;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 25;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.aieStone = (short) (ChaosBase.FlameMult * 7 + 15);
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stSTAR1, Stones.stSTAR2);
        attr.dieSKCount = 2;
        attr.dieStone = (short) (ChaosBase.FlameMult * 7);
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetMotherAlien, "ChaosBoss.ResetMotherAlien");
        attr.Make = Runtime.proc(this::MakeMotherAlien, "ChaosBoss.MakeMotherAlien");
        attr.Move = Runtime.proc(this::MoveMotherAlien, "ChaosBoss.MoveMotherAlien");
        attr.Aie = Runtime.proc(this::AieMotherAlien, "ChaosBoss.AieMotherAlien");
        attr.Die = Runtime.proc(this::DieMotherAlien, "ChaosBoss.DieMotherAlien");
        attr.charge = 16;
        attr.weight = 70;
        attr.inerty = 192;
        attr.priority = -65;
        attr.heatSpeed = 50;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 40;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.aieStone = (short) (ChaosBase.FlameMult * 6 + 15);
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stSTAR1, Stones.stSTAR2);
        attr.dieSKCount = 2;
        attr.dieStone = (short) (ChaosBase.FlameMult * 7);
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetFatherAlien, "ChaosBoss.ResetFatherAlien");
        attr.Make = Runtime.proc(this::MakeFatherAlien, "ChaosBoss.MakeFatherAlien");
        attr.Move = Runtime.proc(this::MoveFatherAlien, "ChaosBoss.MoveFatherAlien");
        attr.Aie = Runtime.proc(this::AieFatherAlien, "ChaosBoss.AieFatherAlien");
        attr.Die = Runtime.proc(this::DieFatherAlien, "ChaosBoss.DieFatherAlien");
        attr.charge = 16;
        attr.weight = 70;
        attr.inerty = 300;
        attr.priority = -63;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.aieStone = (short) (ChaosBase.FlameMult * 6 + 15);
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetMasterAlien, "ChaosBoss.ResetMasterAlien");
        attr.Make = Runtime.proc(this::MakeMasterAlien, "ChaosBoss.MakeMasterAlien");
        attr.Move = Runtime.proc(this::MoveMasterAlien, "ChaosBoss.MoveMasterAlien");
        attr.Aie = Runtime.proc(this::AieMasterAlien, "ChaosBoss.AieMasterAlien");
        attr.Die = Runtime.proc(this::DieMasterAlien, "ChaosBoss.DieMasterAlien");
        attr.charge = 16;
        attr.weight = 70;
        attr.inerty = 100;
        attr.priority = -64;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(this::ResetIllusion, "ChaosBoss.ResetIllusion");
        attr.Make = Runtime.proc(this::MakeIllusion, "ChaosBoss.MakeIllusion");
        attr.Move = Runtime.proc(this::MoveIllusion, "ChaosBoss.MoveIllusion");
        attr.Aie = Runtime.proc(this::AieIllusion, "ChaosBoss.AieIllusion");
        attr.Die = Runtime.proc(this::DieIllusion, "ChaosBoss.DieIllusion");
        attr.charge = 30;
        attr.weight = 100;
        attr.inerty = 20;
        attr.priority = 110;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(chaosFire::ResetPart, "ChaosFire.ResetPart");
        attr.Move = Runtime.proc(chaosFire::MoveHeart, "ChaosFire.MoveHeart");
        attr.Die = Runtime.proc(chaosFire::DiePart, "ChaosFire.DiePart");
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(chaosFire::ResetPart, "ChaosFire.ResetPart");
        attr.Move = Runtime.proc(chaosFire::MoveEye, "ChaosFire.MoveEye");
        attr.Die = Runtime.proc(chaosFire::DiePart, "ChaosFire.DiePart");
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = Runtime.proc(chaosFire::ResetPart, "ChaosFire.ResetPart");
        attr.Move = Runtime.proc(chaosFire::MoveMouth, "ChaosFire.MoveMouth");
        attr.Die = Runtime.proc(chaosFire::DiePart, "ChaosFire.DiePart");
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        for (c = 0; c <= 2; c++) {
            attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
            checks.CheckMem(attr);
            attr.Reset = Runtime.proc(chaosFire::ResetPart, "ChaosFire.ResetPart");
            attr.Move = Runtime.proc(chaosFire::MovePart, "ChaosFire.MovePart");
            attr.Die = Runtime.proc(chaosFire::DiePart, "ChaosFire.DiePart");
            attr.basicType = BasicTypes.NotBase;
            attr.priority = (byte) (99 + c);
            attr.toKill = false;
            memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        }
    }


    // Support

    private static ChaosBoss instance;

    public static ChaosBoss instance() {
        if (instance == null)
            new ChaosBoss(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        InitParams();
    }

    public void close() {
    }

}
