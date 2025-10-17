package ch.chaos.castle;

import java.util.EnumSet;

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
        int py = 0;

        if (boss.hitSubLife + boss.fireSubLife == 0)
            py = 148;
        else
            py = 96;
        chaosActions.SetObjLoc(boss, 0, py, 64, 32);
        chaosActions.SetObjRect(boss, 1, 1, 63, 31);
    }

    private final ChaosBase.MakeProc MakeBrotherAlien_ref = this::MakeBrotherAlien;

    private void ResetBrotherAlien(ChaosBase.Obj boss) {
        boss.life = 1500;
        boss.fireSubLife = 10;
        boss.hitSubLife = 40;
        boss.moveSeq = 30;
        boss.shapeSeq = 0;
        boss.stat = 0;
        MakeBrotherAlien(boss);
    }

    private final ChaosBase.ResetProc ResetBrotherAlien_ref = this::ResetBrotherAlien;

    private void MoveBrotherAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> bx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> by = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBounds(boss, 3);
        chaosActions.AvoidBackground(boss, 3);
        chaosActions.Burn(boss);
        if (chaosBase.step >= boss.shapeSeq) {
            if (boss.hitSubLife + boss.fireSubLife == 0) {
                if ((boss.moveSeq < 10) && (boss.moveSeq > 0))
                    chaosFire.BoumE(boss, 30, 8, 16);
                boss.shapeSeq += ChaosBase.Period;
            } else {
                if (boss.moveSeq < 20)
                    chaosFire.BoumE(boss, 30, 16, 8);
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
                chaosFire.FireFlame(boss, 0, 0, true);
            else if (boss.moveSeq < 20)
                chaosFire.FireMissileV(boss, chaosBase.mainPlayer, 0, 0, true);
            else if (boss.moveSeq < 30)
                chaosFire.FireMissileA(boss, 0, 0);
            boss.dvx = trigo.RND() % 4096;
            boss.dvx -= 2048;
            boss.dvy = trigo.RND() % 4096;
            boss.dvy -= 2048;
            boss.stat += ChaosBase.Period / 4 + trigo.RND() % (ChaosBase.Period * 10 / (chaosBase.difficulty + 4));
        }
        boss.stat -= chaosBase.step;
        hit.set(50);
        chaosActions.PlayerCollision(boss, hit);
    }

    private final ChaosBase.MoveProc MoveBrotherAlien_ref = this::MoveBrotherAlien;

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

    private final ChaosBase.AieProc AieBrotherAlien_ref = this::AieBrotherAlien;

    private void DieBrotherAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);
        int cnt = 0;

        if (boss.moveSeq == 0) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, brotherEffect);
            chaosActions.Boum(boss, EnumSet.of(Stones.stFOG3, Stones.stFOG4), ChaosBase.slowStyle, 15, 2);
            chaosActions.Boum(boss, EnumSet.of(Stones.stCE, Stones.stRE, Stones.stCROSS), ChaosBase.gravityStyle, 15, 3);
            chaosActions.Boum(boss, EnumSet.of(Stones.stC26, Stones.stC35), ChaosBase.fastStyle, 15, 2);
            for (cnt = 1; cnt <= 10; cnt++) {
                boss.vx = trigo.COS(cnt * 36) * 4;
                boss.vy = trigo.SIN(cnt * 36) * 4;
                chaosFire.FireFlame(boss, 0, 0, false);
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

    private final ChaosBase.DieProc DieBrotherAlien_ref = this::DieBrotherAlien;

    private void MakeSisterAlien(ChaosBase.Obj boss) {
        // VAR
        int ss = 0;
        int sz = 0;
        int sx = 0;
        int sy = 0;

        if (boss.shapeSeq >= 512) {
            sz = 0;
        } else {
            ss = (boss.shapeSeq / 16) % 16;
            if (ss < 8)
                sz = 8 - ss;
            else
                sz = ss - 7;
        }
        sx = 28 - sz * 2;
        sy = 16 - sz * 2;
        chaosActions.SetObjLoc(boss, 60 + sz, 224 + sz, sx, sy);
        chaosActions.SetObjRect(boss, 0, 0, 28, 16);
    }

    private final ChaosBase.MakeProc MakeSisterAlien_ref = this::MakeSisterAlien;

    private void ResetSisterAlien(ChaosBase.Obj boss) {
        boss.life = 1500;
        boss.fireSubLife = 40;
        boss.hitSubLife = 10;
        boss.moveSeq = 30;
        boss.shapeSeq = 128;
        boss.stat = 0;
        MakeSisterAlien(boss);
    }

    private final ChaosBase.ResetProc ResetSisterAlien_ref = this::ResetSisterAlien;

    private void MoveSisterAlien(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.ObjAttr aAttr = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        int kCnt = 0;
        int sStat = 0;
        int cnt = 0;
        int mul = 0;
        Anims nKind = Anims.PLAYER;
        int sKind = 0;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBounds(boss, 4);
        chaosActions.AvoidBackground(boss, 4);
        chaosActions.Burn(boss);
        if ((boss.shapeSeq >= 512) && (chaosBase.step + 512 > boss.shapeSeq))
            chaosFire.BoumS(boss, chaosBase.mainPlayer, 0, 0, 0, 0, 15, 0, 24, 13, 0, false, false, true);
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
                boss.dvx = trigo.RND() % 3072;
                boss.dvx -= 1536;
                boss.dvy = trigo.RND() % 3072;
                boss.dvy -= 1536;
                boss.stat = trigo.RND() % 3 + 1;
            } else {
                boss.stat--;
                if (boss.stat == 0) {
                    boss.dvx = 0;
                    boss.dvy = 0;
                }
            }
            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, 0, 0, true);
            boss.shapeSeq += 512;
        }
        boss.shapeSeq -= chaosBase.step;
        MakeSisterAlien(boss);
        hit.set(50);
        chaosActions.PlayerCollision(boss, hit);
    }

    private final ChaosBase.MoveProc MoveSisterAlien_ref = this::MoveSisterAlien;

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

    private final ChaosBase.AieProc AieSisterAlien_ref = this::AieSisterAlien;

    private void DieSisterAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);

        if (boss.moveSeq == 0) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, sisterEffect);
            chaosFire.BoumS(boss, chaosBase.mainPlayer, 0, 0, 0, 0, 12, 18, 60, 6, 10, false, false, false);
        } else {
            boss.life = boss.moveSeq * 50 + 1;
            hit.set(10);
            fire.set(40);
            chaosActions.Aie(boss, boss, hit, fire);
        }
    }

    private final ChaosBase.DieProc DieSisterAlien_ref = this::DieSisterAlien;

    private void MakeMotherAlien(ChaosBase.Obj boss) {
        // VAR
        int px = 0;
        int py = 0;

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
        chaosActions.SetObjLoc(boss, px, py, 24, 24);
        chaosActions.SetObjRect(boss, 1, 1, 23, 23);
    }

    private final ChaosBase.MakeProc MakeMotherAlien_ref = this::MakeMotherAlien;

    private void ResetMotherAlien(ChaosBase.Obj boss) {
        boss.hitSubLife = 30;
        boss.fireSubLife = 20;
        boss.moveSeq = 9 + chaosBase.difficulty;
        boss.life = boss.moveSeq * 50 + 1;
        boss.shapeSeq = 0;
        boss.stat = 5;
        MakeMotherAlien(boss);
    }

    private final ChaosBase.ResetProc ResetMotherAlien_ref = this::ResetMotherAlien;

    private void MoveMotherAlien(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj alien = null;
        int oldSeq = 0;
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        int nLife = 0;
        int cnt = 0;
        int c = 0;
        int angle = 0;
        Runtime.Ref<Integer> bx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> by = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int nSpeed = 0;
        Anims nKind = Anims.PLAYER;
        int sKind = 0;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBackground(boss, 4);
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
                chaosFire.BoumS(boss, chaosBase.mainPlayer, 0, 0, 0, 0, 15, 0, 24, 15, 0, true, true, true);
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
                    angle = c * 360 / cnt;
                    px.set(bx.get() + trigo.COS(angle) / 64);
                    py.set(by.get() + trigo.SIN(angle) / 64);
                    alien = chaosActions.CreateObj(nKind, sKind, px.get(), py.get(), 0, nLife);
                    chaosActions.SetObjVXY(alien, trigo.COS(angle) * nSpeed, trigo.SIN(angle) * nSpeed);
                }
            } else if (boss.stat == 5) {
                chaosFire.BoumS(boss, chaosBase.mainPlayer, 0, 0, 0, 0, 15, 0, 24, 15, 0, true, true, true);
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
                    chaosActions.SetObjAXY(boss, trigo.SGN(px.get() - bx.get()) * 64, trigo.SGN(py.get() - by.get()) * 64);
                    chaosActions.LimitSpeed(boss, 2200);
                    if ((oldSeq / 32) != (boss.shapeSeq / 32)) {
                        chaosActions.GetCenter(boss, px, py);
                        c = trigo.RND() % 3;
                        if (c == 0) {
                            chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                            nKind = Anims.MISSILE;
                            sKind = ChaosMissile.mAlien2;
                            cnt = ChaosMissile.mAcc2;
                        } else if (c == 1) {
                            nKind = Anims.STONE;
                            cnt = Stones.stRBOX.ordinal();
                            sKind = 0;
                        } else {
                            nKind = Anims.STONE;
                            cnt = Stones.stCBOX.ordinal();
                            sKind = 0;
                        }
                        alien = chaosActions.CreateObj(nKind, sKind, px.get(), py.get(), cnt, 12);
                        bx.set(trigo.RND() % 2048);
                        by.set(trigo.RND() % 2048);
                        chaosActions.SetObjVXY(alien, boss.vx + bx.get() - 1024, boss.vy + by.get() - 1536);
                        chaosActions.SetObjAXY(alien, 0, 48);
                    }
                }
            }
            case 1, 3 -> {
                chaosActions.GetCenter(boss, bx, by);
                if (((boss.ax >= 0) && (bx.get() > chaosGraphics.gameWidth / 2 + 117)) || (boss.ax == 0)) {
                    boss.ax = trigo.RND() % 32;
                    boss.ax = -boss.ax - 16;
                } else if ((boss.ax < 0) && (bx.get() < chaosGraphics.gameWidth / 2 - 117)) {
                    boss.ax = trigo.RND() % 32 + 16;
                }
                if (((boss.ay >= 0) && (by.get() > chaosGraphics.gameHeight / 2 + 117)) || (boss.ay == 0)) {
                    boss.ay = trigo.RND() % 32;
                    boss.ay = -boss.ay - 16;
                } else if ((boss.ay < 0) && (by.get() < chaosGraphics.gameHeight / 2 - 117)) {
                    boss.ay = trigo.RND() % 32 + 16;
                }
                chaosActions.LimitSpeed(boss, 1536);
                chaosFire.ReturnWeapon(bx.get(), by.get());
            }
            case 2 -> {
                chaosActions.GetCenter(boss, px, py);
                chaosFire.ReturnWeapon(px.get(), py.get());
                if (chaosBase.step > boss.hitSubLife) {
                    chaosFire.FireMissileS(px.get(), py.get(), boss.vx, boss.vy, boss, chaosBase.mainPlayer, 0, 0, true, true);
                    boss.hitSubLife += ChaosBase.Period / 5 + trigo.RND() % (ChaosBase.Period * 8 / 5);
                    bx.set(trigo.RND() % 64);
                    by.set(trigo.RND() % 64);
                    px.inc(trigo.RND() % 2);
                    py.inc(trigo.RND() % 2);
                    chaosActions.SetObjAXY(boss, trigo.SGN(chaosGraphics.gameWidth / 2 - px.get()) * bx.get(), trigo.SGN(chaosGraphics.gameHeight / 2 - py.get()) * by.get());
                }
                chaosActions.LimitSpeed(boss, 1536);
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

    private final ChaosBase.MoveProc MoveMotherAlien_ref = this::MoveMotherAlien;

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

    private final ChaosBase.AieProc AieMotherAlien_ref = this::AieMotherAlien;

    private void DieMotherAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);

        if ((boss.moveSeq == 0) && (boss.stat == 0) && (boss.shapeSeq == 0)) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, motherEffect);
            boss.temperature = ChaosBase.MaxHot;
            chaosFire.turn = chaosBase.level[Zone.Family.ordinal()] > 4;
            chaosFire.BoumS(boss, boss, 0, 0, 0, 0, 12, 18, 60, 12, 10, true, true, false);
        } else {
            boss.life = boss.moveSeq * 50 + 1;
            hit.set(20);
            fire.set(30);
            chaosActions.Aie(boss, boss, hit, fire);
        }
    }

    private final ChaosBase.DieProc DieMotherAlien_ref = this::DieMotherAlien;

    private void MakeFatherAlien(ChaosBase.Obj boss) {
        // VAR
        int px = 0;
        int py = 0;
        int sz = 0;
        int dt = 0;
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

    private final ChaosBase.MakeProc MakeFatherAlien_ref = this::MakeFatherAlien;

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

    private final ChaosBase.ResetProc ResetFatherAlien_ref = this::ResetFatherAlien;

    private void MoveFatherAlien(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj alien = null;
        ChaosBase.ObjAttr cattr = null;
        long lx = 0L;
        long ly = 0L;
        long dl = 0L;
        int c = 0;
        int angle = 0;
        Runtime.Ref<Integer> bx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> by = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int mul = 0;
        int nvx = 0;
        int nvy = 0;
        int nax = 0;
        int nay = 0;
        int oldSeq = 0;
        int dv = 0;
        int skcnt = 0;
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        int nLife = 0;
        Runtime.RangeSet sList = new Runtime.RangeSet(Memory.SET16_r);
        Anims nKind = Anims.PLAYER;
        int sKind = 0;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBackground(boss, 4);
        chaosActions.AvoidBounds(boss, 4);
        chaosActions.LimitSpeed(boss, 3072);
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
                        chaosFire.KillObjs(Anims.MACHINE, ChaosMachine.mDoor);
                        chaosFire.KillObjs(Anims.DEADOBJ, ChaosDObj.doMirror);
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
                    cattr = chaosBase.GetAnimAttr(Anims.ALIEN2, ChaosCreator.cNest);
                    if ((cattr.nbObj < 3) && (chaosBase.level[Zone.Family.ordinal()] < 10))
                        alien = chaosActions.CreateObj(Anims.ALIEN2, ChaosCreator.cNest, 5 * ChaosGraphics.BW + ChaosGraphics.BW / 2, 3 * ChaosGraphics.BW / 2, 0, 100);
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
                    boss.dvx = (int) (lx * 128 / dl * 16);
                    boss.dvy = (int) (ly * 128 / dl * 16);
                }
                dv = (boss.shapeSeq / (ChaosBase.Period / 3));
                if (oldSeq / (ChaosBase.Period / 3) != dv) {
                    if (dv % 2 == 0) {
                        dv = trigo.RND() % 3;
                        if (dv == 0)
                            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, 0, 0, true);
                        else if (dv == 1)
                            chaosFire.FireMissileA(boss, 0, 0);
                        else
                            chaosFire.FireMissileS(bx.get(), by.get(), -boss.dvx, -boss.dvy, boss, chaosBase.mainPlayer, 0, 0, true, true);
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
                    boss.dvx = -boss.dvx;
                    boss.dvy = -boss.dvy;
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
                    boss.vx = (int) (lx * 1536 / dl);
                    boss.vy = (int) (ly * 1536 / dl);
                }
                boss.dvx = 0;
                boss.dvy = 0;
                boss.ax = -(boss.vy / 64);
                boss.ay = boss.vy / 64;
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
                            sKind = (sKind + 1) % 16;
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
                    angle = trigo.RND() % 360;
                    chaosActions.SetObjVXY(alien, trigo.COS(angle), trigo.SIN(angle));
                }
            }
            case 2 -> {
                if (((boss.ax >= 0) && (bx.get() > chaosGraphics.gameWidth / 2 + 118)) || (boss.ax == 0)) {
                    boss.ax = trigo.RND() % 32;
                    boss.ax = -boss.ax - 16;
                } else if ((boss.ax < 0) && (bx.get() < chaosGraphics.gameWidth / 2 - 118)) {
                    boss.ax = trigo.RND() % 32 + 16;
                }
                if (((boss.ay >= 0) && (by.get() > chaosGraphics.gameHeight / 2 + 118)) || (boss.ay == 0)) {
                    boss.ay = trigo.RND() % 32;
                    boss.ay = -boss.ay - 16;
                } else if ((boss.ay < 0) && (by.get() < chaosGraphics.gameHeight / 2 - 118)) {
                    boss.ay = trigo.RND() % 32 + 16;
                }
                chaosActions.LimitSpeed(boss, 1536);
                chaosFire.ReturnWeapon(bx.get(), by.get());
            }
            case 3 -> {
                chaosFire.GoCenter(boss);
                chaosFire.ReturnWeapon(bx.get(), by.get());
                dv = (boss.shapeSeq / (ChaosBase.Period / 3));
                if ((oldSeq / (ChaosBase.Period / 3)) != dv) {
                    chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                    if ((Math.abs(px.get() - bx.get()) < 56) && (Math.abs(py.get() - by.get()) < 56) && (trigo.RND() % 6 == 0))
                        chaosFire.BoumS(boss, boss, 0, 0, trigo.RND() % 18, 0, 18, 0, 20, 12, 0, false, false, false);
                    for (c = 1; c <= 4; c++) {
                        angle = trigo.RND() % 360;
                        nvx = trigo.COS(angle) * 2;
                        nvy = trigo.SIN(angle) * 2;
                        nax = trigo.RND() % 200;
                        nax -= 100;
                        nay = trigo.RND() % 200;
                        nay -= 100;
                        if (chaosFire.ParabolicDist(bx.get(), by.get(), nvx, nvy, nax, nay, px.get(), py.get()) > 24) {
                            alien = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien1, bx.get(), by.get(), trigo.RND() % 3, 12);
                            chaosActions.SetObjVXY(alien, nvx, nvy);
                            chaosActions.SetObjAXY(alien, nax, nay);
                        }
                    }
                }
            }
            case 4 -> {
                chaosFire.GoTo(boss, chaosGraphics.gameWidth / 2, chaosGraphics.gameHeight / 2 + 70);
                chaosFire.ReturnWeapon(bx.get(), by.get());
                dv = boss.shapeSeq / 60;
                if ((oldSeq / 60 != dv) && (Math.abs(boss.vx) < 64) && (Math.abs(boss.vy) < 64)) {
                    chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                    dv = (dv % 30);
                    if ((((boss.moveSeq / 2) % 2) != 0))
                        dv = 29 - dv;
                    angle = dv * 12 + boss.shapeSeq / 750;
                    mul = (boss.moveSeq / 3) + 1;
                    if (!((boss.moveSeq % 2) != 0))
                        mul = mul * 4;
                    alien = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien2, bx.get(), by.get(), ChaosMissile.mAcc2, 12);
                    chaosActions.SetObjVXY(alien, trigo.COS(angle) * 4 / mul, trigo.SIN(angle) / 2);
                    if (((boss.moveSeq % 2) != 0)) {
                        mul = mul * 16;
                        chaosActions.SetObjAXY(alien, -(trigo.COS(angle) / mul), -(trigo.SIN(angle) / 32));
                    }
                }
            }
            case 5 -> {
                if (boss.shapeSeq > ChaosBase.Period * 10) {
                    boss.ax = 0;
                    boss.ay = 0;
                    boss.dvx = trigo.SGN(px.get() - bx.get()) * 800;
                    boss.dvy = trigo.SGN(py.get() - by.get()) * 800;
                    dv = boss.shapeSeq / ChaosBase.Period;
                    if (oldSeq / ChaosBase.Period != dv) {
                        dv = trigo.RND() % 4;
                        if (dv == 0)
                            chaosFire.FireFlame(boss, 0, 0, true);
                        else if (dv == 1)
                            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, 0, 0, true);
                        else if (dv == 2)
                            chaosFire.FireMissileA(boss, 0, 0);
                        else
                            chaosFire.FireMissileS(bx.get(), by.get(), -boss.dvx, -boss.dvy, boss, chaosBase.mainPlayer, 0, 0, true, true);
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

    private final ChaosBase.MoveProc MoveFatherAlien_ref = this::MoveFatherAlien;

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

    private final ChaosBase.AieProc AieFatherAlien_ref = this::AieFatherAlien;

    private void DieFatherAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);
        int angle = 0;
        int ox = 0;
        int oy = 0;
        int c = 0;

        if ((boss.moveSeq == 0) && (boss.stat == 5) && (boss.shapeSeq == 0)) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, fatherEffect);
            chaosFire.BoumS(boss, boss, 0, 0, 6, 0, 18, 0, 20, 16, 0, true, false, false);
            angle = trigo.RND() % 120;
            for (c = 0; c <= 2; c++) {
                ox = trigo.COS(angle) / 16;
                oy = trigo.SIN(angle) / 16;
                chaosFire.BoumS(boss, boss, ox, oy, 0, 0, 18, 0, 20, 8, 0, true, false, false);
                angle += 120;
            }
        } else {
            boss.life = boss.moveSeq * 50 + 1;
            hit.set(30);
            fire.set(20);
            chaosActions.Aie(boss, boss, hit, fire);
        }
    }

    private final ChaosBase.DieProc DieFatherAlien_ref = this::DieFatherAlien;

    private void MakeMasterAlien(ChaosBase.Obj boss) {
        chaosActions.SetObjLoc(boss, 88, 200, 84, 36);
        chaosActions.SetObjRect(boss, 8, 4, 68, 28);
    }

    private final ChaosBase.MakeProc MakeMasterAlien_ref = this::MakeMasterAlien;

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

    private final ChaosBase.ResetProc ResetMasterAlien_ref = this::ResetMasterAlien;

    private void MoveMasterAlien(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj fObj = null;
        ChaosBase.Obj alien = null;
        long dx = 0L;
        long dy = 0L;
        long dl = 0L;
        int angle = 0;
        Runtime.Ref<Integer> bx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> by = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int mx = 0;
        int my = 0;
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        int oldSeq = 0;
        int sKind = 0;
        boolean speed = false;
        boolean inv = false;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBackground(boss, 1);
        chaosActions.LimitSpeed(boss, 2048);
        inv = (chaosBase.screenInverted > 0);
        if (chaosFire.laugh != inv) {
            if (chaosFire.laugh) {
                hit.set(chaosBase.nbDollar);
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
                        angle = trigo.RND() % 360;
                        switch (boss.moveSeq % 5) {
                            case 0 -> {
                                chaosFire.BoumS(boss, fObj, 0, 0, 0, angle, 12, 18, 60, 12, 10, true, speed, true);
                            }
                            case 4 -> {
                                chaosFire.BoumS(boss, fObj, 0, 0, 0, 0, 15, 0, 24, 14, 0, true, speed, true);
                            }
                            case 3 -> {
                                chaosFire.BoumS(boss, fObj, 0, 0, 0, angle, 15, 45, 24, 12, 10, true, speed, true);
                            }
                            case 2 -> {
                                chaosSounds.SoundEffect(boss, chaosFire.bombEffect);
                                boss.shapeSeq = 2;
                                chaosFire.BoumS(boss, fObj, -56, 8, 0, 0, 15, 0, 24, 13, 0, true, speed, false);
                                boss.shapeSeq = 1;
                                chaosFire.BoumS(boss, fObj, 56, 8, 0, 0, 15, 0, 24, 13, 0, true, speed, false);
                                boss.shapeSeq = 0;
                                chaosFire.BoumS(boss, fObj, 0, -60, 0, 0, 15, 0, 24, 13, 0, true, speed, false);
                            }
                            case 1 -> {
                                chaosFire.BoumS(boss, fObj, 0, 0, 0, 0, 15, 105, 24, 12, 10, true, speed, true);
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
                    mx = trigo.RND() % 64;
                    my = trigo.RND() % 64;
                    bx.inc(trigo.RND() % 2);
                    by.inc(trigo.RND() % 2);
                    chaosActions.SetObjAXY(boss, trigo.SGN(chaosGraphics.gameWidth / 2 - bx.get()) * mx, trigo.SGN(chaosGraphics.gameHeight / 2 - by.get()) * my);
                    switch ((boss.moveSeq % 5)) {
                        case 0 -> {
                            chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, -28, 4, false);
                            chaosFire.FireMissileV(boss, chaosBase.mainPlayer, 28, 4, false);
                            boss.hitSubLife += trigo.RND() % ChaosBase.Period;
                        }
                        case 4 -> {
                            chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                            angle = ((boss.shapeSeq / 60) % 30) * 12;
                            mx = trigo.COS(angle) * 2;
                            my = trigo.SIN(angle) * 2;
                            chaosFire.FireMissileS(bx.get() - 28, by.get() + 4, mx, my, boss, chaosBase.mainPlayer, 0, 0, true, false);
                            mx = -mx;
                            my = -my;
                            chaosFire.FireMissileS(bx.get() + 28, by.get() + 4, mx, my, boss, chaosBase.mainPlayer, 0, 0, true, false);
                            boss.hitSubLife += ChaosBase.Period / 3;
                        }
                        case 3 -> {
                            if (trigo.RND() % 2 == 0)
                                mx = 28;
                            else
                                mx = -28;
                            chaosFire.FireFlame(boss, mx, 4, true);
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
                            chaosFire.FireMissileS(bx.get() - 28, by.get() + 4, dx, dy, boss, chaosBase.mainPlayer, 0, 0, true, false);
                            dx = bx.get() + 28 - px.get();
                            dy = by.get() + 4 - py.get();
                            dl = trigo.SQRT(dx * dx + dy * dy);
                            if (dl != 0) {
                                dx = dx * 3072 / dl;
                                dy = dy * 3072 / dl;
                            }
                            chaosFire.FireMissileS(bx.get() + 28, by.get() + 4, dx, dy, boss, chaosBase.mainPlayer, 0, 0, true, false);
                            boss.hitSubLife += trigo.RND() % ChaosBase.Period;
                        }
                        case 1 -> {
                            chaosSounds.SoundEffect(boss, chaosFire.missileEffect);
                            angle = ((boss.shapeSeq / 60) % 30) * 12;
                            chaosFire.FireMissileS(bx.get(), by.get(), trigo.COS(angle) * 2, trigo.SIN(angle) * 2, boss, chaosBase.mainPlayer, 0, 0, true, false);
                            angle += 120;
                            if (boss.moveSeq < 5)
                                chaosFire.FireMissileS(bx.get(), by.get(), trigo.COS(angle) * 2, trigo.SIN(angle) * 2, boss, chaosBase.mainPlayer, 0, 0, true, false);
                            angle += 120;
                            if (boss.moveSeq < 10)
                                chaosFire.FireMissileS(bx.get(), by.get(), trigo.COS(angle) * 2, trigo.SIN(angle) * 2, boss, chaosBase.mainPlayer, 0, 0, true, false);
                            boss.hitSubLife += ChaosBase.Period / 4;
                        }
                        default -> throw new RuntimeException("Unhandled CASE value " + (boss.moveSeq % 5));
                    }
                }
                if (chaosBase.step > boss.hitSubLife)
                    boss.hitSubLife = 0;
                else
                    boss.hitSubLife -= chaosBase.step;
                chaosActions.LimitSpeed(boss, 1536);
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
                    sKind = trigo.RND() % 4;
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

    private final ChaosBase.MoveProc MoveMasterAlien_ref = this::MoveMasterAlien;

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

    private final ChaosBase.AieProc AieMasterAlien_ref = this::AieMasterAlien;

    private void DieMasterAlien(ChaosBase.Obj boss) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);

        if ((boss.moveSeq == 0) && (boss.stat == 6) && (boss.shapeSeq == 0)) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, sisterEffect);
            chaosSounds.SoundEffect(boss, motherEffect);
            boss.shapeSeq = 2;
            chaosFire.BoumS(boss, boss, -69, 40, 0, 0, 12, 18, 60, 12, 10, true, false, false);
            boss.shapeSeq = 1;
            chaosFire.BoumS(boss, boss, 69, 40, 0, 0, 12, 18, 60, 12, 10, true, false, false);
            boss.shapeSeq = 0;
            chaosFire.BoumS(boss, boss, 0, -80, 0, 0, 12, 18, 60, 12, 10, true, false, false);
        } else {
            boss.life = boss.moveSeq * 50 + 1;
            hit.set(25);
            fire.set(25);
            chaosActions.Aie(boss, boss, hit, fire);
        }
    }

    private final ChaosBase.DieProc DieMasterAlien_ref = this::DieMasterAlien;

    private void MakeIllusion(ChaosBase.Obj boss) {
        chaosActions.SetObjLoc(boss, 59, 243, 10, 10);
        chaosActions.SetObjRect(boss, -3, -3, 19, 19);
    }

    private final ChaosBase.MakeProc MakeIllusion_ref = this::MakeIllusion;

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

    private final ChaosBase.ResetProc ResetIllusion_ref = this::ResetIllusion;

    private void MoveIllusion(ChaosBase.Obj boss) {
        // VAR
        ChaosBase.Obj alien = null;
        int nLife = 0;
        int oldSeq = 0;
        int dv = 0;
        Runtime.Ref<Integer> bx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> by = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int angle = 0;
        int sKind = 0;
        Anims nKind = Anims.PLAYER;

        chaosActions.UpdateXY(boss);
        chaosActions.AvoidBounds(boss, 2);
        chaosActions.AvoidBackground(boss, 4);
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
                    chaosFire.KillObjs(Anims.DEADOBJ, ChaosDObj.doMagnetR);
                    chaosFire.KillObjs(Anims.DEADOBJ, ChaosDObj.doMagnetA);
                    chaosFire.KillObjs(Anims.DEADOBJ, ChaosDObj.doWindMaker);
                    chaosFire.KillObjs(Anims.DEADOBJ, ChaosDObj.doMirror);
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
                chaosFire.BoumX(boss, boss, 48, ((boss.moveSeq % 2) != 0));
                angle = trigo.RND() % 360;
                boss.vx = trigo.COS(angle) * 3 / 2;
                boss.vy = trigo.SIN(angle) * 3 / 2;
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

    private final ChaosBase.MoveProc MoveIllusion_ref = this::MoveIllusion;

    private void AieIllusion(ChaosBase.Obj boss, ChaosBase.Obj src, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        hit.set(0);
        fire.set(0);
        if (src.kind == Anims.WEAPON)
            chaosActions.Die(src);
    }

    private final ChaosBase.AieProc AieIllusion_ref = this::AieIllusion;

    private void DieIllusion(ChaosBase.Obj boss) {
        // VAR
        int off = 0;
        int rx = 0;
        int ry = 0;

        if ((boss.moveSeq == 0) && (boss.stat == 33) && (boss.shapeSeq == 0)) {
            chaosFire.Chain(boss);
            chaosSounds.SoundEffect(boss, brotherEffect);
            chaosSounds.SoundEffect(boss, fatherEffect);
            rx = 0;
            ry = 0;
            for (off = 0; off <= 3; off++) {
                chaosFire.turn = true;
                chaosFire.BoumS(boss, boss, rx, ry, 0, 0, 12, 0, 30, 12, 0, true, false, false);
                rx = trigo.COS(off * 120) / 16;
                ry = trigo.SIN(off * 120) / 16;
            }
            chaosFire.theIllusion = null;
            return;
        } else if ((boss.stat > 0) && (boss.stat <= 32)) {
            chaosActions.Boum(boss, EnumSet.of(Stones.stFOG3), ChaosBase.fastStyle, 31, 1);
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

    private final ChaosBase.DieProc DieIllusion_ref = this::DieIllusion;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;
        int c = 0;

        chaosSounds.SetEffect(chaosFire.missileEffect[0], chaosSounds.soundList[SoundList.sMissile.ordinal()], 0, 0, 100, 4);
        chaosSounds.SetEffect(chaosFire.flameEffect[0], chaosSounds.soundList[SoundList.sHHat.ordinal()], 0, 0, 180, 4);
        chaosSounds.SetEffect(chaosFire.bombEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 0, 0, 220, 7);
        chaosSounds.SetEffect(chaosFire.huEffect[0], chaosSounds.soundList[SoundList.sHurryUp.ordinal()], 0, 0, 255, 4);
        chaosSounds.SetEffect(chaosFire.poufEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 0, 4181, 255, 14);
        chaosSounds.SetEffect(chaosFire.createEffect[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 4181, 220, 12);
        chaosSounds.SetEffect(chaosFire.aieEffect[0], chaosSounds.soundList[SoundList.sGong.ordinal()], 0, 16726, 200, 13);
        chaosSounds.SetEffect(chaosFire.koEffect[0], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 0, 240, 14);
        chaosSounds.SetEffect(brotherEffect[0], chaosSounds.soundList[SoundList.sVerre.ordinal()], 0, 4181, 255, 15);
        chaosSounds.SetEffect(sisterEffect[0], chaosSounds.soundList[SoundList.sGong.ordinal()], 0, 4181, 255, 15);
        chaosSounds.SetEffect(motherEffect[0], chaosSounds.soundList[SoundList.sCasserole.ordinal()], 0, 4181, 255, 15);
        chaosSounds.SetEffect(fatherEffect[0], chaosSounds.soundList[SoundList.sCannon.ordinal()], 0, 4181, 255, 15);
        chaosSounds.SetEffect(haha1Effect[0], chaosSounds.soundList[SoundList.wJans.ordinal()], 0, 16726, 0, 8);
        chaosSounds.SetEffect(haha2Effect[0], chaosSounds.soundList[SoundList.wJans.ordinal()], 0, 0, 0, 8);
        for (c = 1; c <= 3; c++) {
            chaosSounds.SetEffect(haha1Effect[c], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 0, 160, 8);
            chaosSounds.SetEffect(haha2Effect[c], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 0, 250, 8);
        }
        chaosSounds.SetEffect(haha2Effect[4], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 7032, 250, 8);
        chaosSounds.SetEffect(haha2Effect[5], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 7032, 250, 8);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetBrotherAlien_ref;
        attr.Make = MakeBrotherAlien_ref;
        attr.Move = MoveBrotherAlien_ref;
        attr.Aie = AieBrotherAlien_ref;
        attr.Die = DieBrotherAlien_ref;
        attr.charge = 16;
        attr.weight = 100;
        attr.inerty = 192;
        attr.priority = -65;
        attr.heatSpeed = 20;
        attr.refreshSpeed = 40;
        attr.coolSpeed = 120;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.aieStone = ChaosBase.FlameMult * 7 + 15;
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stSTAR1, Stones.stSTAR2);
        attr.dieSKCount = 2;
        attr.dieStone = ChaosBase.FlameMult * 7 + 31;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetSisterAlien_ref;
        attr.Make = MakeSisterAlien_ref;
        attr.Move = MoveSisterAlien_ref;
        attr.Aie = AieSisterAlien_ref;
        attr.Die = DieSisterAlien_ref;
        attr.charge = 24;
        attr.weight = 60;
        attr.inerty = 192;
        attr.priority = -65;
        attr.heatSpeed = 80;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 25;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.aieStone = ChaosBase.FlameMult * 7 + 15;
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stSTAR1, Stones.stSTAR2);
        attr.dieSKCount = 2;
        attr.dieStone = ChaosBase.FlameMult * 7;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetMotherAlien_ref;
        attr.Make = MakeMotherAlien_ref;
        attr.Move = MoveMotherAlien_ref;
        attr.Aie = AieMotherAlien_ref;
        attr.Die = DieMotherAlien_ref;
        attr.charge = 16;
        attr.weight = 70;
        attr.inerty = 192;
        attr.priority = -65;
        attr.heatSpeed = 50;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 40;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.aieStone = ChaosBase.FlameMult * 6 + 15;
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stSTAR1, Stones.stSTAR2);
        attr.dieSKCount = 2;
        attr.dieStone = ChaosBase.FlameMult * 7;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetFatherAlien_ref;
        attr.Make = MakeFatherAlien_ref;
        attr.Move = MoveFatherAlien_ref;
        attr.Aie = AieFatherAlien_ref;
        attr.Die = DieFatherAlien_ref;
        attr.charge = 16;
        attr.weight = 70;
        attr.inerty = 300;
        attr.priority = -63;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.aieStone = ChaosBase.FlameMult * 6 + 15;
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetMasterAlien_ref;
        attr.Make = MakeMasterAlien_ref;
        attr.Move = MoveMasterAlien_ref;
        attr.Aie = AieMasterAlien_ref;
        attr.Die = DieMasterAlien_ref;
        attr.charge = 16;
        attr.weight = 70;
        attr.inerty = 100;
        attr.priority = -64;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetIllusion_ref;
        attr.Make = MakeIllusion_ref;
        attr.Move = MoveIllusion_ref;
        attr.Aie = AieIllusion_ref;
        attr.Die = DieIllusion_ref;
        attr.charge = 30;
        attr.weight = 100;
        attr.inerty = 20;
        attr.priority = 110;
        attr.aieStKinds = EnumSet.of(Stones.stSTAR1);
        attr.aieSKCount = 1;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = chaosFire.ResetPart_ref;
        attr.Move = chaosFire.MoveHeart_ref;
        attr.Die = chaosFire.DiePart_ref;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = chaosFire.ResetPart_ref;
        attr.Move = chaosFire.MoveEye_ref;
        attr.Die = chaosFire.DiePart_ref;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = chaosFire.ResetPart_ref;
        attr.Move = chaosFire.MoveMouth_ref;
        attr.Die = chaosFire.DiePart_ref;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN3.ordinal()], attr.node);
        for (c = 0; c <= 2; c++) {
            attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
            checks.CheckMem(attr);
            attr.Reset = chaosFire.ResetPart_ref;
            attr.Move = chaosFire.MovePart_ref;
            attr.Die = chaosFire.DiePart_ref;
            attr.basicType = BasicTypes.NotBase;
            attr.priority = 99 + c;
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
