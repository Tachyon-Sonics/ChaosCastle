package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.ObjFlags;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosAlien {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosBonus chaosBonus;
    private final ChaosGraphics chaosGraphics;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosAlien() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosBonus = ChaosBonus.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // CONST

    public static final int aCartoon = 0;
    public static final int aDbOval = 1;
    public static final int aSmallDrawer = 2;
    public static final int aBigDrawer = 3;
    public static final int aHospital = 4;
    public static final int aDiese = 5;
    public static final int aKamikaze = 6;
    public static final int aStar = 7;
    public static final int aBubble = 8;
    public static final int aBumper = 9;
    public static final int aPic = 10;
    public static final int aTri = 11;
    public static final int aTrefle = 12;
    public static final int aBig = 13;
    public static final int aSquare = 14;
    public static final int aFlame = 15;
    public static final int aColor = 16;


    // VAR

    private int zeroFire;
    private ChaosSounds.Effect[] aieAlien0Effect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieHospitalEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieCartoonEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieBigEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieBigEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieSquareEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieTriEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieSquareEffect = Runtime.initArray(new ChaosSounds.Effect[14]);
    private ChaosSounds.Effect[] aieDrawerEffect = Runtime.initArray(new ChaosSounds.Effect[2]);
    private ChaosSounds.Effect[] aieTrefleEffect = Runtime.initArray(new ChaosSounds.Effect[2]);
    private ChaosSounds.Effect[] dieAlien0Effect = Runtime.initArray(new ChaosSounds.Effect[9]);
    private ChaosSounds.Effect[] dieSBEffect = Runtime.initArray(new ChaosSounds.Effect[9]);
    private ChaosSounds.Effect[] dieDieseEffect = Runtime.initArray(new ChaosSounds.Effect[17]);
    private ChaosSounds.Effect[] dieBigDrawerEffect = Runtime.initArray(new ChaosSounds.Effect[17]);
    private ChaosSounds.Effect[] dieSmallDrawerEffect = Runtime.initArray(new ChaosSounds.Effect[17]);
    private ChaosSounds.Effect[] aieKmkEffect = Runtime.initArray(new ChaosSounds.Effect[10]);
    private ChaosSounds.Effect[] aiePicEffect = Runtime.initArray(new ChaosSounds.Effect[10]);
    private ChaosSounds.Effect[] dieKmkEffect = Runtime.initArray(new ChaosSounds.Effect[8]);
    private ChaosSounds.Effect[] dieFlameEffect = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] dieHospitalEffect = Runtime.initArray(new ChaosSounds.Effect[16]);
    private ChaosSounds.Effect[] dieBumperEffect = Runtime.initArray(new ChaosSounds.Effect[16]);
    private ChaosSounds.Effect[] dieSCartoonEffect = Runtime.initArray(new ChaosSounds.Effect[9]);
    private ChaosSounds.Effect[] dieCartoonEffect = Runtime.initArray(new ChaosSounds.Effect[12]);
    private ChaosSounds.Effect[] dieTriEffect = Runtime.initArray(new ChaosSounds.Effect[12]);
    private ChaosSounds.Effect[] dieTrefleEffect = Runtime.initArray(new ChaosSounds.Effect[24]);


    public int getZeroFire() {
        return this.zeroFire;
    }

    public void setZeroFire(int zeroFire) {
        this.zeroFire = zeroFire;
    }

    public ChaosSounds.Effect[] getAieAlien0Effect() {
        return this.aieAlien0Effect;
    }

    public void setAieAlien0Effect(ChaosSounds.Effect[] aieAlien0Effect) {
        this.aieAlien0Effect = aieAlien0Effect;
    }

    public ChaosSounds.Effect[] getAieHospitalEffect() {
        return this.aieHospitalEffect;
    }

    public void setAieHospitalEffect(ChaosSounds.Effect[] aieHospitalEffect) {
        this.aieHospitalEffect = aieHospitalEffect;
    }

    public ChaosSounds.Effect[] getAieCartoonEffect() {
        return this.aieCartoonEffect;
    }

    public void setAieCartoonEffect(ChaosSounds.Effect[] aieCartoonEffect) {
        this.aieCartoonEffect = aieCartoonEffect;
    }

    public ChaosSounds.Effect[] getAieBigEffect() {
        return this.aieBigEffect;
    }

    public void setAieBigEffect(ChaosSounds.Effect[] aieBigEffect) {
        this.aieBigEffect = aieBigEffect;
    }

    public ChaosSounds.Effect[] getDieBigEffect() {
        return this.dieBigEffect;
    }

    public void setDieBigEffect(ChaosSounds.Effect[] dieBigEffect) {
        this.dieBigEffect = dieBigEffect;
    }

    public ChaosSounds.Effect[] getAieSquareEffect() {
        return this.aieSquareEffect;
    }

    public void setAieSquareEffect(ChaosSounds.Effect[] aieSquareEffect) {
        this.aieSquareEffect = aieSquareEffect;
    }

    public ChaosSounds.Effect[] getAieTriEffect() {
        return this.aieTriEffect;
    }

    public void setAieTriEffect(ChaosSounds.Effect[] aieTriEffect) {
        this.aieTriEffect = aieTriEffect;
    }

    public ChaosSounds.Effect[] getDieSquareEffect() {
        return this.dieSquareEffect;
    }

    public void setDieSquareEffect(ChaosSounds.Effect[] dieSquareEffect) {
        this.dieSquareEffect = dieSquareEffect;
    }

    public ChaosSounds.Effect[] getAieDrawerEffect() {
        return this.aieDrawerEffect;
    }

    public void setAieDrawerEffect(ChaosSounds.Effect[] aieDrawerEffect) {
        this.aieDrawerEffect = aieDrawerEffect;
    }

    public ChaosSounds.Effect[] getAieTrefleEffect() {
        return this.aieTrefleEffect;
    }

    public void setAieTrefleEffect(ChaosSounds.Effect[] aieTrefleEffect) {
        this.aieTrefleEffect = aieTrefleEffect;
    }

    public ChaosSounds.Effect[] getDieAlien0Effect() {
        return this.dieAlien0Effect;
    }

    public void setDieAlien0Effect(ChaosSounds.Effect[] dieAlien0Effect) {
        this.dieAlien0Effect = dieAlien0Effect;
    }

    public ChaosSounds.Effect[] getDieSBEffect() {
        return this.dieSBEffect;
    }

    public void setDieSBEffect(ChaosSounds.Effect[] dieSBEffect) {
        this.dieSBEffect = dieSBEffect;
    }

    public ChaosSounds.Effect[] getDieDieseEffect() {
        return this.dieDieseEffect;
    }

    public void setDieDieseEffect(ChaosSounds.Effect[] dieDieseEffect) {
        this.dieDieseEffect = dieDieseEffect;
    }

    public ChaosSounds.Effect[] getDieBigDrawerEffect() {
        return this.dieBigDrawerEffect;
    }

    public void setDieBigDrawerEffect(ChaosSounds.Effect[] dieBigDrawerEffect) {
        this.dieBigDrawerEffect = dieBigDrawerEffect;
    }

    public ChaosSounds.Effect[] getDieSmallDrawerEffect() {
        return this.dieSmallDrawerEffect;
    }

    public void setDieSmallDrawerEffect(ChaosSounds.Effect[] dieSmallDrawerEffect) {
        this.dieSmallDrawerEffect = dieSmallDrawerEffect;
    }

    public ChaosSounds.Effect[] getAieKmkEffect() {
        return this.aieKmkEffect;
    }

    public void setAieKmkEffect(ChaosSounds.Effect[] aieKmkEffect) {
        this.aieKmkEffect = aieKmkEffect;
    }

    public ChaosSounds.Effect[] getAiePicEffect() {
        return this.aiePicEffect;
    }

    public void setAiePicEffect(ChaosSounds.Effect[] aiePicEffect) {
        this.aiePicEffect = aiePicEffect;
    }

    public ChaosSounds.Effect[] getDieKmkEffect() {
        return this.dieKmkEffect;
    }

    public void setDieKmkEffect(ChaosSounds.Effect[] dieKmkEffect) {
        this.dieKmkEffect = dieKmkEffect;
    }

    public ChaosSounds.Effect[] getDieFlameEffect() {
        return this.dieFlameEffect;
    }

    public void setDieFlameEffect(ChaosSounds.Effect[] dieFlameEffect) {
        this.dieFlameEffect = dieFlameEffect;
    }

    public ChaosSounds.Effect[] getDieHospitalEffect() {
        return this.dieHospitalEffect;
    }

    public void setDieHospitalEffect(ChaosSounds.Effect[] dieHospitalEffect) {
        this.dieHospitalEffect = dieHospitalEffect;
    }

    public ChaosSounds.Effect[] getDieBumperEffect() {
        return this.dieBumperEffect;
    }

    public void setDieBumperEffect(ChaosSounds.Effect[] dieBumperEffect) {
        this.dieBumperEffect = dieBumperEffect;
    }

    public ChaosSounds.Effect[] getDieSCartoonEffect() {
        return this.dieSCartoonEffect;
    }

    public void setDieSCartoonEffect(ChaosSounds.Effect[] dieSCartoonEffect) {
        this.dieSCartoonEffect = dieSCartoonEffect;
    }

    public ChaosSounds.Effect[] getDieCartoonEffect() {
        return this.dieCartoonEffect;
    }

    public void setDieCartoonEffect(ChaosSounds.Effect[] dieCartoonEffect) {
        this.dieCartoonEffect = dieCartoonEffect;
    }

    public ChaosSounds.Effect[] getDieTriEffect() {
        return this.dieTriEffect;
    }

    public void setDieTriEffect(ChaosSounds.Effect[] dieTriEffect) {
        this.dieTriEffect = dieTriEffect;
    }

    public ChaosSounds.Effect[] getDieTrefleEffect() {
        return this.dieTrefleEffect;
    }

    public void setDieTrefleEffect(ChaosSounds.Effect[] dieTrefleEffect) {
        this.dieTrefleEffect = dieTrefleEffect;
    }


    // PROCEDURE

    private void MakeCartoon(ChaosBase.Obj cartoon) {
        // VAR
        int px = 0;
        int py = 0;
        int sz = 0;

        if (cartoon.stat == 0) {
            px = 160;
            py = 88;
            sz = 32;
        } else if (cartoon.stat == 1) {
            px = 176;
            py = 72;
            sz = 16;
        } else {
            px = 160;
            py = 72;
            sz = 16;
        }
        chaosActions.SetObjLoc(cartoon, px, py, sz, sz);
        chaosActions.SetObjRect(cartoon, 0, 0, sz, sz);
    }

    private final ChaosBase.MakeProc MakeCartoon_ref = this::MakeCartoon;

    private void MakeAlien0(ChaosBase.Obj alien) {
        chaosActions.SetObjLoc(alien, 0, 76, 20, 20);
        chaosActions.SetObjRect(alien, 0, 0, 20, 20);
    }

    private final ChaosBase.MakeProc MakeAlien0_ref = this::MakeAlien0;

    private void MakeSmallDrawer(ChaosBase.Obj drawer) {
        chaosActions.SetObjLoc(drawer, 168, 60, 16, 12);
        chaosActions.SetObjRect(drawer, 0, 0, 16, 12);
    }

    private final ChaosBase.MakeProc MakeSmallDrawer_ref = this::MakeSmallDrawer;

    private void MakeBigDrawer(ChaosBase.Obj drawer) {
        chaosActions.SetObjLoc(drawer, 96, 76, 32, 20);
        chaosActions.SetObjRect(drawer, 0, 0, 32, 20);
    }

    private final ChaosBase.MakeProc MakeBigDrawer_ref = this::MakeBigDrawer;

    private void MakeHospital(ChaosBase.Obj hospital) {
        chaosActions.SetObjLoc(hospital, 20, 76, 20, 20);
        chaosActions.SetObjRect(hospital, 0, 0, 20, 20);
    }

    private final ChaosBase.MakeProc MakeHospital_ref = this::MakeHospital;

    private void MakeDiese(ChaosBase.Obj diese) {
        chaosActions.SetObjLoc(diese, 144, 72, 15, 15);
        chaosActions.SetObjRect(diese, 0, 0, 15, 15);
    }

    private final ChaosBase.MakeProc MakeDiese_ref = this::MakeDiese;

    private void MakeKamikaze(ChaosBase.Obj kamikaze) {
        // VAR
        int px = 0;
        int py = 0;

        py = (kamikaze.stat / 2) * 16;
        px = (kamikaze.stat % 2) * 16;
        chaosActions.SetObjLoc(kamikaze, px + 96, py + 96, 16, 16);
        chaosActions.SetObjRect(kamikaze, 0, 0, 16, 16);
    }

    private final ChaosBase.MakeProc MakeKamikaze_ref = this::MakeKamikaze;

    private void MakePic(ChaosBase.Obj pic) {
        // VAR
        int py = 0;

        py = pic.stat * 16 + 128;
        chaosActions.SetObjLoc(pic, 80, py, 16, 16);
        chaosActions.SetObjRect(pic, 0, 0, 16, 16);
    }

    private final ChaosBase.MakeProc MakePic_ref = this::MakePic;

    private void MakeStar(ChaosBase.Obj star) {
        chaosActions.SetObjLoc(star, 64, 148, 16, 16);
        chaosActions.SetObjRect(star, 0, 0, 16, 16);
    }

    private final ChaosBase.MakeProc MakeStar_ref = this::MakeStar;

    private void MakeBubble(ChaosBase.Obj bubble) {
        chaosActions.SetObjLoc(bubble, 64, 164, 16, 16);
        chaosActions.SetObjRect(bubble, 1, 1, 15, 15);
    }

    private final ChaosBase.MakeProc MakeBubble_ref = this::MakeBubble;

    private void MakeBumper(ChaosBase.Obj bumper) {
        chaosActions.SetObjLoc(bumper, 76, 76, 20, 20);
        chaosActions.SetObjRect(bumper, 0, 0, 20, 20);
    }

    private final ChaosBase.MakeProc MakeBumper_ref = this::MakeBumper;

    private void MakeTri(ChaosBase.Obj tri) {
        chaosActions.SetObjLoc(tri, 224, 164, 24, 20);
        chaosActions.SetObjRect(tri, 4, 4, 20, 16);
    }

    private final ChaosBase.MakeProc MakeTri_ref = this::MakeTri;

    private void MakeTrefle(ChaosBase.Obj trefle) {
        chaosActions.SetObjLoc(trefle, 80, 160, 20, 20);
        chaosActions.SetObjRect(trefle, 0, 0, 20, 20);
    }

    private final ChaosBase.MakeProc MakeTrefle_ref = this::MakeTrefle;

    private void MakeBig(ChaosBase.Obj big) {
        chaosActions.SetObjLoc(big, 100, 180, 20, 20);
        chaosActions.SetObjRect(big, 1, 1, 19, 19);
    }

    private final ChaosBase.MakeProc MakeBig_ref = this::MakeBig;

    private void MakeSquare(ChaosBase.Obj square) {
        chaosActions.SetObjLoc(square, 101, 101, 22, 22);
        chaosActions.SetObjRect(square, 0, 0, 22, 22);
    }

    private final ChaosBase.MakeProc MakeSquare_ref = this::MakeSquare;

    private void MakeFlame(ChaosBase.Obj flame) {
        // VAR
        int py = 0;

        if (flame.stat == 0)
            py = 172;
        else
            py = 184;
        chaosActions.SetObjLoc(flame, 200, py, 12, 12);
        chaosActions.SetObjRect(flame, 0, 0, 12, 12);
    }

    private final ChaosBase.MakeProc MakeFlame_ref = this::MakeFlame;

    private void MakeColor(ChaosBase.Obj color) {
        // VAR
        int px = 0;

        px = color.life / 20;
        if (px > 4)
            px = 4;
        px = px * 20 + 100;
        chaosActions.SetObjLoc(color, px, 160, 20, 20);
        chaosActions.SetObjRect(color, 0, 0, 20, 20);
    }

    private final ChaosBase.MakeProc MakeColor_ref = this::MakeColor;

    private void ResetCartoon(ChaosBase.Obj cartoon) {
        cartoon.stat = cartoon.life % 3;
        if (cartoon.stat == 0)
            cartoon.life = 60;
        else
            cartoon.life = 20;
        cartoon.hitSubLife = cartoon.life;
        cartoon.fireSubLife = cartoon.life;
        MakeCartoon(cartoon);
    }

    private final ChaosBase.ResetProc ResetCartoon_ref = this::ResetCartoon;

    private void ResetAlien0(ChaosBase.Obj alien) {
        // VAR
        int nvx = 0;
        int nvy = 0;

        alien.moveSeq = ChaosBase.Period * 5 + ChaosBase.Period * (trigo.RND() % 10);
        alien.hitSubLife = alien.life;
        alien.fireSubLife = alien.life;
        nvx = trigo.RND() % 1024;
        nvy = trigo.RND() % 1024;
        alien.dvx = nvx - 512;
        alien.dvy = nvy - 512;
        alien.attr.Make.invoke(alien);
    }

    private final ChaosBase.ResetProc ResetAlien0_ref = this::ResetAlien0;

    private void ResetSmallDrawer(ChaosBase.Obj drawer) {
        drawer.life = (8 - chaosBase.powerCountDown / 2 + chaosBase.difficulty) * 5;
        drawer.hitSubLife = drawer.life;
        drawer.fireSubLife = drawer.life;
        MakeSmallDrawer(drawer);
    }

    private final ChaosBase.ResetProc ResetSmallDrawer_ref = this::ResetSmallDrawer;

    private void ResetBigDrawer(ChaosBase.Obj drawer) {
        drawer.life = (9 - chaosBase.powerCountDown / 2 + chaosBase.difficulty) * 8;
        drawer.hitSubLife = drawer.life;
        drawer.fireSubLife = drawer.life;
        MakeBigDrawer(drawer);
    }

    private final ChaosBase.ResetProc ResetBigDrawer_ref = this::ResetBigDrawer;

    private void ResetHospital(ChaosBase.Obj hospital) {
        // VAR
        int angle = 0;

        hospital.hitSubLife = hospital.life;
        hospital.fireSubLife = hospital.life;
        angle = trigo.RND() % 360;
        hospital.vx = trigo.COS(angle);
        hospital.vy = trigo.SIN(angle);
        MakeHospital(hospital);
    }

    private final ChaosBase.ResetProc ResetHospital_ref = this::ResetHospital;

    private void ResetDiese(ChaosBase.Obj diese) {
        diese.fireSubLife = diese.life;
        diese.hitSubLife = diese.life;
        MakeDiese(diese);
    }

    private final ChaosBase.ResetProc ResetDiese_ref = this::ResetDiese;

    private void ResetKamikaze(ChaosBase.Obj kamikaze) {
        kamikaze.stat = kamikaze.life % 4;
        kamikaze.life = 30;
        kamikaze.hitSubLife = kamikaze.life;
        kamikaze.fireSubLife = kamikaze.life;
        kamikaze.moveSeq = 2;
        MakeKamikaze(kamikaze);
    }

    private final ChaosBase.ResetProc ResetKamikaze_ref = this::ResetKamikaze;

    private void ResetPic(ChaosBase.Obj pic) {
        pic.stat = pic.life % 2;
        pic.life = 40 + chaosBase.difficulty * 16;
        pic.hitSubLife = pic.life;
        pic.fireSubLife = pic.life;
        pic.moveSeq = 2;
        MakePic(pic);
    }

    private final ChaosBase.ResetProc ResetPic_ref = this::ResetPic;

    private void ResetStar(ChaosBase.Obj star) {
        star.moveSeq = 0;
        star.hitSubLife = star.life;
        star.fireSubLife = 0;
        MakeStar(star);
    }

    private final ChaosBase.ResetProc ResetStar_ref = this::ResetStar;

    private void ResetBubble(ChaosBase.Obj bubble) {
        bubble.moveSeq = 0;
        bubble.hitSubLife = 0;
        bubble.fireSubLife = bubble.life;
        MakeBubble(bubble);
    }

    private final ChaosBase.ResetProc ResetBubble_ref = this::ResetBubble;

    private void ResetBumper(ChaosBase.Obj bumper) {
        bumper.moveSeq = 0;
        bumper.hitSubLife = bumper.life;
        bumper.fireSubLife = bumper.life;
        MakeBumper(bumper);
    }

    private final ChaosBase.ResetProc ResetBumper_ref = this::ResetBumper;

    private void ResetTri(ChaosBase.Obj tri) {
        tri.fireSubLife = tri.life / 3;
        tri.hitSubLife = tri.life - tri.fireSubLife;
        MakeTri(tri);
    }

    private final ChaosBase.ResetProc ResetTri_ref = this::ResetTri;

    private void ResetTrefle(ChaosBase.Obj trefle) {
        trefle.hitSubLife = trefle.life / 3;
        trefle.fireSubLife = trefle.life - trefle.hitSubLife;
        MakeTrefle(trefle);
    }

    private final ChaosBase.ResetProc ResetTrefle_ref = this::ResetTrefle;

    private void ResetBig(ChaosBase.Obj big) {
        big.moveSeq = 0;
        big.fireSubLife = big.life / 4;
        big.hitSubLife = big.life - big.fireSubLife;
        MakeBig(big);
    }

    private final ChaosBase.ResetProc ResetBig_ref = this::ResetBig;

    private void ResetSquare(ChaosBase.Obj square) {
        square.moveSeq = 0;
        square.hitSubLife = square.life / 4;
        square.fireSubLife = square.life - square.hitSubLife;
        MakeSquare(square);
    }

    private final ChaosBase.ResetProc ResetSquare_ref = this::ResetSquare;

    private void ResetFlame(ChaosBase.Obj flame) {
        flame.fireSubLife = flame.life / 2;
        flame.hitSubLife = flame.life;
        flame.shapeSeq = 0;
        flame.stat = 0;
        MakeFlame(flame);
    }

    private final ChaosBase.ResetProc ResetFlame_ref = this::ResetFlame;

    private void ResetColor(ChaosBase.Obj color) {
        color.hitSubLife = 20;
        color.fireSubLife = 10;
        color.moveSeq = 0;
        MakeColor(color);
    }

    private final ChaosBase.ResetProc ResetColor_ref = this::ResetColor;

    private void Stop(ChaosBase.Obj victim, ChaosBase.Obj cartoon, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        // CONST
        final int RDST = ChaosBase.Frac * 4;

        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ox = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> oy = new Runtime.Ref<>(0);
        long sx = 0L;
        long sy = 0L;
        long sz = 0L;
        long iz = 0L;

        if ((victim.kind == Anims.ALIEN1) && (victim.subKind == aCartoon))
            return;
        sx = cartoon.x;
        sy = cartoon.y;
        sz = cartoon.right;
        chaosActions.GetCenter(victim, ox, oy);
        chaosActions.GetCenter(cartoon, px, py);
        if (Math.abs(ox.get() - px.get()) > Math.abs(oy.get() - py.get())) {
            if (ox.get() < px.get()) {
                iz = victim.x + victim.right - sx;
                if (iz > RDST)
                    iz = RDST;
                victim.x -= iz;
                victim.vx = (int) -Math.abs(victim.vx / 4);
            } else {
                iz = sx + sz - victim.x;
                if (iz > RDST)
                    iz = RDST;
                victim.x += iz;
                victim.vx = (int) Math.abs(victim.vx / 4);
            }
        } else {
            if (oy.get() < py.get()) {
                iz = victim.y + victim.bottom - sy;
                if (iz > RDST)
                    iz = RDST;
                victim.y -= iz;
                victim.vy = (int) -Math.abs(victim.vy / 4);
            } else {
                iz = sy + sz - victim.y;
                if (iz > RDST)
                    iz = RDST;
                victim.y += iz;
                victim.vy = (int) Math.abs(victim.vy / 4);
            }
        }
    }

    private final ChaosBase.AieProc Stop_ref = this::Stop;

    private void MoveCartoon(ChaosBase.Obj cartoon) {
        if (chaosActions.OutOfScreen(cartoon)) {
            chaosActions.Leave(cartoon);
            return;
        }
        cartoon.vx = 0;
        cartoon.vy = 0;
        cartoon.dvx = 0;
        cartoon.dvy = 0;
        chaosActions.DoCollision(cartoon, Runtime.withRange(EnumSet.of(Anims.PLAYER, Anims.SMARTBONUS, Anims.BONUS), Anims.ALIEN3, Anims.MISSILE), Stop_ref, new Runtime.FieldRef<>(cartoon::getHitSubLife, cartoon::setHitSubLife), new Runtime.FieldRef<>(cartoon::getFireSubLife, cartoon::setFireSubLife));
        chaosActions.UpdateXY(cartoon);
        chaosActions.Burn(cartoon);
    }

    private final ChaosBase.MoveProc MoveCartoon_ref = this::MoveCartoon;

    private void MoveDrawer(ChaosBase.Obj drawer) {
        if (chaosActions.OutOfScreen(drawer)) {
            chaosActions.Leave(drawer);
            return;
        }
        chaosActions.AvoidBackground(drawer, 2);
        chaosActions.AvoidBounds(drawer, 2);
        drawer.dvx = 0;
        drawer.dvy = 0;
        chaosActions.DoCollision(drawer, Runtime.withRange(EnumSet.of(Anims.PLAYER, Anims.SMARTBONUS, Anims.BONUS), Anims.ALIEN3, Anims.MISSILE), Stop_ref, new Runtime.FieldRef<>(drawer::getHitSubLife, drawer::setHitSubLife), new Runtime.FieldRef<>(drawer::getFireSubLife, drawer::setFireSubLife));
        chaosActions.UpdateXY(drawer);
        chaosActions.Burn(drawer);
    }

    private final ChaosBase.MoveProc MoveDrawer_ref = this::MoveDrawer;

    private void MoveAlien0(ChaosBase.Obj alien) {
        if (chaosActions.OutOfScreen(alien)) {
            chaosActions.Leave(alien);
            return;
        }
        chaosActions.UpdateXY(alien);
        chaosActions.LimitSpeed(alien, 1536);
        chaosActions.AvoidBounds(alien, 4);
        chaosActions.AvoidBackground(alien, 4);
        chaosActions.Burn(alien);
        if (chaosBase.step > alien.moveSeq)
            ResetAlien0(alien);
        else
            alien.moveSeq -= chaosBase.step;
        chaosActions.PlayerCollision(alien, new Runtime.FieldRef<>(alien::getLife, alien::setLife));
        if (alien.life == 0)
            chaosActions.Die(alien);
    }

    private final ChaosBase.MoveProc MoveAlien0_ref = this::MoveAlien0;

    private void MoveHospital(ChaosBase.Obj hospital) {
        // VAR
        int nvx = 0;
        int nvy = 0;
        int nvl = 0;

        if (chaosActions.OutOfScreen(hospital)) {
            chaosActions.Leave(hospital);
            return;
        }
        nvx = hospital.vx / 32;
        nvy = hospital.vy / 32;
        nvl = trigo.SQRT(nvx * nvx + nvy * nvy);
        if (nvl == 0) {
            ResetHospital(hospital);
        } else if (Math.abs(nvl - 1024) > 256) {
            hospital.vx = hospital.vx * 4 / nvl * 8;
            hospital.vy = hospital.vy * 4 / nvl * 8;
        }
        hospital.dvx = 0;
        hospital.dvy = 0;
        hospital.ax = hospital.vy / 64;
        hospital.ay = -(hospital.vx / 64);
        chaosActions.UpdateXY(hospital);
        chaosActions.AvoidBounds(hospital, 4);
        chaosActions.AvoidBackground(hospital, 4);
        chaosActions.Burn(hospital);
        chaosActions.PlayerCollision(hospital, new Runtime.FieldExprRef<>(hospital, ChaosBase.Obj::getLife, ChaosBase.Obj::setLife));
        if (hospital.life == 0)
            chaosActions.Die(hospital);
    }

    private final ChaosBase.MoveProc MoveHospital_ref = this::MoveHospital;

    private void MoveDiese(ChaosBase.Obj diese) {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> mx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> my = new Runtime.Ref<>(0);
        int dx = 0;
        int dy = 0;
        int dl = 0;
        int speed = 0;

        if (chaosBase.zone != Zone.Castle) {
            chaosActions.AvoidBounds(diese, 0);
        } else if (chaosActions.OutOfScreen(diese)) {
            chaosActions.Leave(diese);
            return;
        }
        chaosActions.UpdateXY(diese);
        chaosActions.AvoidBackground(diese, 0);
        chaosActions.Burn(diese);
        chaosActions.GetCenter(diese, mx, my);
        chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
        dx = px.get() - mx.get();
        dy = py.get() - my.get();
        if ((Math.abs(dx) < 128) && (Math.abs(dy) < 128)) {
            dl = trigo.SQRT(dx * dx + dy * dy);
            if (dl != 0) {
                speed = 4 + chaosBase.difficulty / 2;
                diese.dvx = dx * 128 / dl * speed;
                diese.dvy = dy * 128 / dl * speed;
            }
        } else {
            diese.dvx = 0;
            diese.dvy = 0;
        }
        chaosActions.PlayerCollision(diese, new Runtime.FieldRef<>(diese::getLife, diese::setLife));
        if (diese.life == 0)
            chaosActions.Die(diese);
    }

    private final ChaosBase.MoveProc MoveDiese_ref = this::MoveDiese;

    private void MovePic(ChaosBase.Obj pic) {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> kx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ky = new Runtime.Ref<>(0);
        int dx = 0;

        if (chaosActions.OutOfScreen(pic)) {
            chaosActions.Leave(pic);
            return;
        }
        chaosActions.UpdateXY(pic);
        dx = (pic.stat * 2);
        dx--;
        if (pic.moveSeq == 2) {
            pic.dvx = 0;
            pic.dvy = 0;
            chaosActions.GetCenter(pic, kx, ky);
            chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
            kx.dec(px.get());
            ky.dec(py.get());
            if ((kx.get() * dx > 0) && (Math.abs(ky.get()) < Math.abs(kx.get())) && (Math.abs(ky.get()) < 120))
                pic.moveSeq = 1;
        } else if (pic.moveSeq == 1) {
            chaosSounds.SoundEffect(pic, aiePicEffect);
            pic.moveSeq = 0;
            pic.dvx = -(dx * 4096);
        } else {
            chaosActions.DoCollision(pic, EnumSet.of(Anims.PLAYER, Anims.ALIEN1), chaosActions.Aie_ref, new Runtime.FieldRef<>(pic::getHitSubLife, pic::setHitSubLife), new Runtime.FieldRef<>(pic::getFireSubLife, pic::setFireSubLife));
            if ((pic.hitSubLife == 0) || (pic.fireSubLife == 0) || chaosActions.InBackground(pic) || chaosActions.OutOfBounds(pic))
                chaosActions.Die(pic);
        }
    }

    private final ChaosBase.MoveProc MovePic_ref = this::MovePic;

    private void MoveKamikaze(ChaosBase.Obj kamikaze) {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> kx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ky = new Runtime.Ref<>(0);
        int dx = 0;
        int dy = 0;

        if (chaosActions.OutOfScreen(kamikaze)) {
            chaosActions.Leave(kamikaze);
            return;
        }
        chaosActions.UpdateXY(kamikaze);
        dy = (kamikaze.stat / 2) * 2;
        dy--;
        dx = (kamikaze.stat % 2) * 2;
        dx--;
        if (kamikaze.moveSeq == 2) {
            kamikaze.dvx = 0;
            kamikaze.dvy = 0;
            chaosActions.GetCenter(kamikaze, kx, ky);
            chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
            kx.dec(px.get());
            ky.dec(py.get());
            if ((kx.get() * dx > 0) && (ky.get() * dy > 0)) {
                if ((Math.abs(kx.get()) < 120) && (Math.abs(ky.get()) < 120))
                    kamikaze.moveSeq = 1;
            }
        } else if (kamikaze.moveSeq == 1) {
            chaosSounds.SoundEffect(kamikaze, aieKmkEffect);
            kamikaze.moveSeq = 0;
            kamikaze.dvx = -(dx * 2896);
            kamikaze.dvy = -(dy * 2896);
        }
        chaosActions.PlayerCollision(kamikaze, new Runtime.FieldRef<>(kamikaze::getHitSubLife, kamikaze::setHitSubLife));
        if ((kamikaze.hitSubLife != kamikaze.life) || chaosActions.InBackground(kamikaze) || chaosActions.OutOfBounds(kamikaze))
            chaosActions.Die(kamikaze);
    }

    private final ChaosBase.MoveProc MoveKamikaze_ref = this::MoveKamikaze;

    private void MoveBumper(ChaosBase.Obj bumper) {
        // VAR
        int nax = 0;
        int nay = 0;

        if (chaosActions.OutOfScreen(bumper)) {
            chaosActions.Leave(bumper);
            return;
        }
        chaosActions.LimitSpeed(bumper, 1600);
        chaosActions.UpdateXY(bumper);
        chaosActions.AvoidBounds(bumper, 4);
        chaosActions.AvoidBackground(bumper, 4);
        chaosActions.Burn(bumper);
        if (chaosBase.step > bumper.moveSeq) {
            nax = trigo.RND() % 256;
            nay = trigo.RND() % 256;
            chaosActions.SetObjAXY(bumper, nax - 128, nay - 128);
            bumper.moveSeq += ChaosBase.Period / 3 + trigo.RND() % (ChaosBase.Period * 2);
        }
        bumper.moveSeq -= chaosBase.step;
        chaosActions.PlayerCollision(bumper, new Runtime.FieldRef<>(bumper::getLife, bumper::setLife));
        if (bumper.life == 0)
            chaosActions.Die(bumper);
    }

    private final ChaosBase.MoveProc MoveBumper_ref = this::MoveBumper;

    private void MoveStarBubble(ChaosBase.Obj obj) {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> mx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> my = new Runtime.Ref<>(0);
        long dx = 0L;
        long dy = 0L;
        long dl = 0L;
        long speed = 0L;

        if (chaosActions.OutOfScreen(obj)) {
            chaosActions.Leave(obj);
            return;
        }
        chaosActions.UpdateXY(obj);
        chaosActions.AvoidBounds(obj, 2);
        chaosActions.AvoidBackground(obj, 3);
        chaosActions.Burn(obj);
        if (chaosBase.step > obj.moveSeq) {
            chaosActions.GetCenter(obj, mx, my);
            chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
            dx = px.get() - mx.get();
            dy = py.get() - my.get();
            dl = trigo.SQRT(dx * dx + dy * dy);
            if (dl != 0) {
                speed = 7 + chaosBase.difficulty / 3;
                obj.dvx = (int) (dx * 64 / dl * speed - 512);
                obj.dvy = (int) (dy * 64 / dl * speed - 512);
                obj.dvx += trigo.RND() % 1024;
                obj.dvy += trigo.RND() % 1024;
            }
            obj.moveSeq += ChaosBase.Period * (10 - chaosBase.difficulty + trigo.RND() % 8) + ChaosBase.Period / 3;
        }
        obj.moveSeq -= chaosBase.step;
        chaosActions.PlayerCollision(obj, new Runtime.FieldRef<>(obj::getLife, obj::setLife));
        if (obj.life == 0)
            chaosActions.Die(obj);
    }

    private final ChaosBase.MoveProc MoveStarBubble_ref = this::MoveStarBubble;

    private void MoveTri(ChaosBase.Obj tri) {
        // VAR
        long dx = 0L;
        long dy = 0L;
        long dl = 0L;
        long speed = 0L;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> mx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> my = new Runtime.Ref<>(0);

        if (chaosActions.OutOfScreen(tri)) {
            chaosActions.Leave(tri);
            return;
        }
        chaosActions.UpdateXY(tri);
        chaosActions.AvoidBackground(tri, 0);
        chaosActions.Burn(tri);
        if ((tri.vx == tri.dvx) && (tri.vy == tri.dvy)) {
            if ((tri.dvx == 0) && (tri.dvy == 0)) {
                chaosActions.GetCenter(tri, mx, my);
                chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
                dx = px.get() - mx.get();
                dy = py.get() - my.get();
                dl = trigo.SQRT(dx * dx + dy * dy);
                if (dl != 0) {
                    speed = 1300 + chaosBase.difficulty * 80;
                    tri.dvx = (int) (dx * speed / dl);
                    tri.dvy = (int) (dy * speed / dl);
                }
            } else {
                tri.dvx = 0;
                tri.dvy = 0;
            }
        }
        chaosActions.PlayerCollision(tri, new Runtime.FieldRef<>(tri::getLife, tri::setLife));
        if (tri.life == 0)
            chaosActions.Die(tri);
    }

    private final ChaosBase.MoveProc MoveTri_ref = this::MoveTri;

    private void MoveTrefle(ChaosBase.Obj trefle) {
        // VAR
        Runtime.Ref<Integer> tx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ty = new Runtime.Ref<>(0);

        if (chaosActions.OutOfScreen(trefle)) {
            chaosActions.Leave(trefle);
            return;
        }
        chaosActions.UpdateXY(trefle);
        chaosActions.AvoidBounds(trefle, 0);
        chaosActions.AvoidBackground(trefle, 0);
        chaosActions.AvoidAnims(trefle, EnumSet.of(Anims.WEAPON, Anims.ALIEN3, Anims.MISSILE));
        chaosActions.LimitSpeed(trefle, 1300);
        chaosActions.Burn(trefle);
        if (chaosBase.step > trefle.moveSeq) {
            trefle.dvx = trigo.RND() % 512;
            trefle.dvy = trigo.RND() % 512;
            chaosActions.GetCenter(trefle, tx, ty);
            if (tx.get() > chaosGraphics.gameWidth / 2)
                trefle.dvx = -trefle.dvx;
            if (ty.get() > chaosGraphics.gameHeight / 2)
                trefle.dvy = -trefle.dvy;
            trefle.moveSeq += ChaosBase.Period + trigo.RND() % ChaosBase.Period;
        }
        trefle.moveSeq -= chaosBase.step;
        chaosActions.PlayerCollision(trefle, new Runtime.FieldRef<>(trefle::getLife, trefle::setLife));
        if (trefle.life == 0)
            chaosActions.Die(trefle);
    }

    private final ChaosBase.MoveProc MoveTrefle_ref = this::MoveTrefle;

    private void MoveBig(ChaosBase.Obj big) {
        // VAR
        int angle = 0;
        EnumSet<Anims> anims = EnumSet.noneOf(Anims.class);

        if (chaosActions.OutOfScreen(big)) {
            chaosActions.Leave(big);
            return;
        }
        chaosActions.UpdateXY(big);
        chaosActions.AvoidBounds(big, 4);
        chaosActions.AvoidBackground(big, 4);
        chaosActions.LimitSpeed(big, 1024);
        chaosActions.Burn(big);
        if (big.moveSeq == 0) {
            big.moveSeq = 1;
            angle = trigo.RND() % 360;
            big.dvx = trigo.COS(angle);
            big.dvy = trigo.SIN(angle);
        }
        if (big.subKind == aBig)
            anims = EnumSet.of(Anims.MACHINE);
        else
            anims = EnumSet.noneOf(Anims.class);
        chaosActions.DoCollision(big, Runtime.plusSet(EnumSet.of(Anims.PLAYER), anims), chaosActions.Aie_ref, new Runtime.FieldRef<>(this::getZeroFire, this::setZeroFire), new Runtime.FieldRef<>(big::getLife, big::setLife));
        if (big.life == 0)
            chaosActions.Die(big);
    }

    private final ChaosBase.MoveProc MoveBig_ref = this::MoveBig;

    private void MoveFlame(ChaosBase.Obj flame) {
        // VAR
        long dx = 0L;
        long dy = 0L;
        long dl = 0L;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fy = new Runtime.Ref<>(0);
        int dv = 0;
        int nax = 0;
        int nay = 0;

        if (chaosActions.OutOfScreen(flame)) {
            chaosActions.Leave(flame);
            return;
        }
        chaosActions.UpdateXY(flame);
        chaosActions.AvoidBackground(flame, 1);
        chaosActions.LimitSpeed(flame, 1536);
        chaosActions.Burn(flame);
        chaosActions.GetCenter(flame, fx, fy);
        chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
        dx = px.get() - fx.get();
        dy = py.get() - fy.get();
        dl = trigo.SQRT(dx * dx + dy * dy);
        if (dl != 0) {
            nax = (int) (dx * 64 / dl);
            nay = (int) (dy * 64 / dl);
        } else {
            nax = 0;
            nay = 0;
        }
        fx.set(flame.vx / 32);
        fy.set(flame.vy / 32);
        dv = trigo.SQRT(fx.get() * fx.get() + fy.get() * fy.get());
        if (dv != 0) {
            nax -= fx.get() * 32 / dv;
            nay -= fy.get() * 32 / dv;
        }
        flame.ax = nax;
        flame.ay = nay;
        if (chaosBase.step > flame.shapeSeq) {
            flame.shapeSeq += ChaosBase.Period / 10;
            if (trigo.RND() % 8 != 0)
                flame.stat = 1 - flame.stat;
            MakeFlame(flame);
        }
        if (chaosBase.step >= flame.shapeSeq)
            flame.shapeSeq = 0;
        else
            flame.shapeSeq -= chaosBase.step;
        chaosActions.PlayerCollision(flame, new Runtime.FieldRef<>(flame::getLife, flame::setLife));
        if (flame.life == 0)
            chaosActions.Die(flame);
    }

    private final ChaosBase.MoveProc MoveFlame_ref = this::MoveFlame;

    private void AieCartoon(ChaosBase.Obj cartoon, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(cartoon, hit, fire);
        chaosSounds.SoundEffect(cartoon, aieCartoonEffect);
    }

    private final ChaosBase.AieProc AieCartoon_ref = this::AieCartoon;

    private void AieDrawer(ChaosBase.Obj drawer, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(drawer, hit, fire);
        chaosSounds.SoundEffect(drawer, aieDrawerEffect);
    }

    private final ChaosBase.AieProc AieDrawer_ref = this::AieDrawer;

    private void AieAlien0(ChaosBase.Obj alien, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(alien, hit, fire);
        alien.attr.Make.invoke(alien);
        if (alien.life > 0)
            chaosSounds.SoundEffect(alien, aieAlien0Effect);
    }

    private final ChaosBase.AieProc AieAlien0_ref = this::AieAlien0;

    private void AieDiese(ChaosBase.Obj diese, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(diese, hit, fire);
        if (diese.life > 0)
            chaosSounds.SoundEffect(diese, aieAlien0Effect);
    }

    private final ChaosBase.AieProc AieDiese_ref = this::AieDiese;

    private void AieHospital(ChaosBase.Obj hospital, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosSounds.SoundEffect(hospital, aieHospitalEffect);
        chaosActions.DecLife(hospital, hit, fire);
    }

    private final ChaosBase.AieProc AieHospital_ref = this::AieHospital;

    private void AieKamikaze(ChaosBase.Obj kamikaze, ChaosBase.Obj src, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        if (kamikaze.moveSeq == 2)
            kamikaze.moveSeq = 1;
        else
            chaosActions.Die(kamikaze);
        hit.set(0);
        fire.set(0);
    }

    private final ChaosBase.AieProc AieKamikaze_ref = this::AieKamikaze;

    private void AiePic(ChaosBase.Obj pic, ChaosBase.Obj src, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        if (pic.moveSeq == 2)
            pic.moveSeq = 1;
    }

    private final ChaosBase.AieProc AiePic_ref = this::AiePic;

    private void AieSBB(ChaosBase.Obj obj, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        if (((obj.hitSubLife > 0) && (hit.get() > 0)) || ((obj.fireSubLife > 0) && (fire.get() > 0)))
            chaosSounds.SoundEffect(obj, aieAlien0Effect);
        chaosActions.DecLife(obj, hit, fire);
    }

    private final ChaosBase.AieProc AieSBB_ref = this::AieSBB;

    private void AieTri(ChaosBase.Obj tri, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosSounds.SoundEffect(tri, aieTriEffect);
        chaosActions.DecLife(tri, hit, fire);
    }

    private final ChaosBase.AieProc AieTri_ref = this::AieTri;

    private void AieTrefle(ChaosBase.Obj trefle, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosBase.addpt += 1;
        chaosSounds.SoundEffect(trefle, aieTrefleEffect);
        chaosActions.DecLife(trefle, hit, fire);
    }

    private final ChaosBase.AieProc AieTrefle_ref = this::AieTrefle;

    private void AieBig(ChaosBase.Obj big, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosBase.addpt += 4;
        chaosSounds.SoundEffect(big, aieBigEffect);
        big.moveSeq = 0;
        chaosActions.DecLife(big, hit, fire);
    }

    private final ChaosBase.AieProc AieBig_ref = this::AieBig;

    private void AieSquare(ChaosBase.Obj square, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        square.moveSeq = 0;
        chaosSounds.SoundEffect(square, aieSquareEffect);
        chaosActions.DecLife(square, hit, fire);
    }

    private final ChaosBase.AieProc AieSquare_ref = this::AieSquare;

    private void DieCartoon(ChaosBase.Obj cartoon) {
        if (cartoon.stat == 0)
            chaosSounds.SoundEffect(cartoon, dieCartoonEffect);
        else
            chaosSounds.SoundEffect(cartoon, dieSCartoonEffect);
    }

    private final ChaosBase.DieProc DieCartoon_ref = this::DieCartoon;

    private void DieAlien0(ChaosBase.Obj alien) {
        // VAR
        ChaosBase.Obj heart = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);

        chaosBase.addpt++;
        chaosSounds.SoundEffect(alien, dieAlien0Effect);
        if ((chaosBase.zone == Zone.Chaos) && !alien.flags.contains(ObjFlags.nested))
            chaosBonus.BoumMoney(alien, EnumSet.of(Moneys.m1), 1, 1);
        if ((chaosBase.level[chaosBase.zone.ordinal()] % 7 == 0) && (chaosBase.difficulty < 10) && (chaosBase.pLife == 1)) {
            chaosActions.GetCenter(alien, px, py);
            heart = chaosActions.CreateObj(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraLife, px.get(), py.get(), 1, 1);
            heart.ay = 8;
            heart.vy = -512;
            heart.dvy = heart.vy;
        }
    }

    private final ChaosBase.DieProc DieAlien0_ref = this::DieAlien0;

    private void DieDiese(ChaosBase.Obj diese) {
        chaosBase.addpt++;
        chaosSounds.SoundEffect(diese, dieDieseEffect);
    }

    private final ChaosBase.DieProc DieDiese_ref = this::DieDiese;

    private void DieKamikaze(ChaosBase.Obj kamikaze) {
        chaosSounds.SoundEffect(kamikaze, dieKmkEffect);
    }

    private final ChaosBase.DieProc DieKamikaze_ref = this::DieKamikaze;

    private void DieStarBubble(ChaosBase.Obj obj) {
        chaosBase.addpt += 2;
        chaosSounds.SoundEffect(obj, dieSBEffect);
    }

    private final ChaosBase.DieProc DieStarBubble_ref = this::DieStarBubble;

    private void DieBumper(ChaosBase.Obj bumper) {
        chaosBase.addpt += 3;
        if (chaosBase.zone != Zone.Chaos)
            chaosBonus.BoumMoney(bumper, EnumSet.of(Moneys.m1), 1, 3);
        chaosSounds.SoundEffect(bumper, dieBumperEffect);
    }

    private final ChaosBase.DieProc DieBumper_ref = this::DieBumper;

    private void DieTri(ChaosBase.Obj tri) {
        if (!tri.flags.contains(ObjFlags.nested))
            chaosBonus.BoumMoney(tri, EnumSet.of(Moneys.st), 1, 2);
        chaosSounds.SoundEffect(tri, dieTriEffect);
    }

    private final ChaosBase.DieProc DieTri_ref = this::DieTri;

    private void DieTrefle(ChaosBase.Obj trefle) {
        chaosSounds.SoundEffect(trefle, dieTrefleEffect);
        if (!trefle.flags.contains(ObjFlags.nested))
            chaosBonus.BoumMoney(trefle, EnumSet.of(Moneys.st), 1, 1);
    }

    private final ChaosBase.DieProc DieTrefle_ref = this::DieTrefle;

    private void DieBig(ChaosBase.Obj big) {
        chaosSounds.SoundEffect(big, dieBigEffect);
    }

    private final ChaosBase.DieProc DieBig_ref = this::DieBig;

    private void DieSquare(ChaosBase.Obj square) {
        if (!square.flags.contains(ObjFlags.nested))
            chaosBonus.BoumMoney(square, EnumSet.of(Moneys.m3), 1, 3);
        chaosSounds.SoundEffect(square, dieSquareEffect);
    }

    private final ChaosBase.DieProc DieSquare_ref = this::DieSquare;

    private void DieFlame(ChaosBase.Obj flame) {
        chaosSounds.SoundEffect(flame, dieFlameEffect);
    }

    private final ChaosBase.DieProc DieFlame_ref = this::DieFlame;

    private void DieSmallDrawer(ChaosBase.Obj drawer) {
        // VAR
        ChaosBase.Obj heart = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);

        chaosSounds.SoundEffect(drawer, dieSmallDrawerEffect);
        if (chaosBase.difficulty < 9) {
            chaosActions.GetCenter(drawer, px, py);
            heart = chaosActions.CreateObj(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraLife, px.get(), py.get(), 1, 1);
            heart.ay = 8;
            heart.vy = -384;
            heart.dvy = heart.vy;
        }
        if (chaosBase.difficulty < 10)
            chaosBonus.BoumMoney(drawer, EnumSet.of(Moneys.m1, Moneys.m2, Moneys.m5), 3, 6);
    }

    private final ChaosBase.DieProc DieSmallDrawer_ref = this::DieSmallDrawer;

    private void DieBigDrawer(ChaosBase.Obj drawer) {
        // VAR
        ChaosBase.Obj bonus = null;
        int random = 0;
        int max = 0;
        int ssKind = 0;
        int bullets = 0;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Weapon w = Weapon.GUN;

        chaosSounds.SoundEffect(drawer, dieBigDrawerEffect);
        max = 30;
        if (chaosBase.difficulty < 10)
            chaosBonus.BoumMoney(drawer, EnumSet.of(Moneys.m5), 1, 7);
        chaosActions.GetCenter(drawer, px, py);
        random = trigo.RND() % max;
        ssKind = ChaosBonus.tbHospital;
        if ((random >= 6) && (chaosBase.powerCountDown <= 14))
            ssKind = ChaosBonus.tbBullet;
        if (random >= 16)
            ssKind = ChaosBonus.tbMagnet;
        if (random >= 26)
            ssKind = ChaosBonus.tbInvinsibility;
        if (random >= 29)
            ssKind = ChaosBonus.tbSleeper;
        bullets = 0;
        for (int _w = 0; _w < Weapon.values().length; _w++) {
            w = Weapon.values()[_w];
            bullets += chaosBase.weaponAttr[w.ordinal()].nbBullet;
        }
        if ((bullets < 100 + trigo.RND() % 64) && (chaosBase.powerCountDown <= 14))
            ssKind = ChaosBonus.tbBullet;
        bonus = chaosActions.CreateObj(Anims.BONUS, ChaosBonus.TimedBonus, px.get(), py.get(), ssKind, 1);
        if (chaosBase.zone != Zone.Castle)
            chaosActions.SetObjAXY(bonus, 0, 12);
    }

    private final ChaosBase.DieProc DieBigDrawer_ref = this::DieBigDrawer;

    private void DieHospital(ChaosBase.Obj hospital) {
        // VAR
        ChaosBase.Obj bonus = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);

        chaosSounds.SoundEffect(hospital, dieHospitalEffect);
        chaosActions.GetCenter(hospital, px, py);
        if (chaosBase.difficulty < 10) {
            bonus = chaosActions.CreateObj(Anims.BONUS, ChaosBonus.TimedBonus, px.get(), py.get(), ChaosBonus.tbHospital, 1);
            chaosActions.SetObjVXY(bonus, hospital.vx / 2, hospital.vy / 2);
            if (chaosBase.zone != Zone.Castle)
                chaosActions.SetObjAXY(bonus, 0, 12);
        }
        if (chaosBase.nbDollar == 0)
            chaosBonus.BoumMoney(hospital, EnumSet.of(Moneys.m1, Moneys.m2, Moneys.m5), 3, 11 - chaosBase.difficulty);
    }

    private final ChaosBase.DieProc DieHospital_ref = this::DieHospital;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;
        int c = 0;
        int v = 0;
        int f = 0;

        chaosSounds.SetEffect(aieAlien0Effect[0], chaosSounds.soundList[SoundList.sCymbale.ordinal()], 0, 0, 120, 1);
        chaosSounds.SetEffect(dieAlien0Effect[0], chaosSounds.soundList[SoundList.aCrash.ordinal()], 0, 0, 160, 4);
        chaosSounds.SetEffect(dieSBEffect[0], chaosSounds.soundList[SoundList.aCrash.ordinal()], 0, 0, 160, 4);
        chaosSounds.SetEffect(dieAlien0Effect[1], chaosSounds.soundList[SoundList.wCrash.ordinal()], 1164, 0, 160, 2);
        chaosSounds.SetEffect(dieSBEffect[1], chaosSounds.soundList[SoundList.wCrash.ordinal()], 388, 0, 160, 2);
        for (c = 7; c >= 1; c -= 1) {
            chaosSounds.SetEffect(dieAlien0Effect[9 - c], chaosSounds.nulSound, 1164, 0, c * 20, 2);
            chaosSounds.SetEffect(dieSBEffect[9 - c], chaosSounds.nulSound, 388, 0, c * 20, 2);
        }
        chaosSounds.SetEffect(dieDieseEffect[0], chaosSounds.soundList[SoundList.aCrash.ordinal()], 0, 0, 180, 4);
        chaosSounds.SetEffect(dieDieseEffect[1], chaosSounds.soundList[SoundList.wCrash.ordinal()], 582, 0, 180, 3);
        for (c = 2; c <= 16; c++) {
            v = ((17 - c) * 11) * (Math.abs(3 - (c - 1) % 6) + 1) / 4;
            chaosSounds.SetEffect(dieDieseEffect[c], chaosSounds.nulSound, 582, 0, v, 3);
        }
        chaosSounds.SetEffect(aieKmkEffect[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 12544, 192, 2);
        chaosSounds.SetEffect(aieKmkEffect[1], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 25088, 12544, 24, 1);
        chaosSounds.SetEffect(aiePicEffect[0], chaosSounds.soundList[SoundList.aCrash.ordinal()], 0, 10537, 100, 2);
        chaosSounds.SetEffect(aiePicEffect[1], chaosSounds.soundList[SoundList.wCrash.ordinal()], 21074, 10537, 100, 2);
        for (c = 2; c <= 9; c++) {
            chaosSounds.SetEffect(aieKmkEffect[c], chaosSounds.nulSound, 1568, 12544, (10 - c) * 3, 1);
            chaosSounds.SetEffect(aiePicEffect[c], chaosSounds.nulSound, 1317, 10537, (10 - c) * 12, 1);
        }
        v = 0;
        for (c = 0; c <= 7; c++) {
            chaosSounds.SetEffect(dieKmkEffect[c], chaosSounds.soundList[SoundList.aCrash.ordinal()], 0, 5575 + 995 * v, (8 - c) * 24, 4);
            v = (v * 9 + 5) % 8;
        }
        chaosSounds.SetEffect(aieHospitalEffect[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 16726, 80, 1);
        for (c = 0; c <= 15; c++) {
            v = ((c + 1) % 2) * 2987 + 15787;
            chaosSounds.SetEffect(dieHospitalEffect[c], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1045, v, (16 - c) * 11, 4);
        }
        chaosSounds.SetEffect(aieTrefleEffect[0], chaosSounds.soundList[SoundList.sCymbale.ordinal()], 0, 12544, 100, 1);
        chaosSounds.SetEffect(aieTrefleEffect[1], chaosSounds.soundList[SoundList.sCymbale.ordinal()], 0, 6272, 100, 1);
        chaosSounds.SetEffect(dieCartoonEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 418, 4181, 100, 5);
        chaosSounds.SetEffect(dieCartoonEffect[1], chaosSounds.soundList[SoundList.wNoise.ordinal()], 836, 8363, 100, 5);
        chaosSounds.SetEffect(dieCartoonEffect[2], chaosSounds.soundList[SoundList.wWhite.ordinal()], 1673, 16726, 150, 5);
        chaosSounds.SetEffect(dieCartoonEffect[3], chaosSounds.soundList[SoundList.wNoise.ordinal()], 836, 8363, 100, 5);
        chaosSounds.SetEffect(dieSCartoonEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 1673, 16726, 100, 3);
        for (c = 4; c <= 11; c++) {
            v = 418 + (c % 2) * 418;
            f = 4181 + (c % 2) * 4181;
            chaosSounds.SetEffect(dieCartoonEffect[c], chaosSounds.soundList[SoundList.wNoise.ordinal()], v, f, (12 - c) * 11, 4);
            chaosSounds.SetEffect(dieSCartoonEffect[c - 3], chaosSounds.soundList[SoundList.wNoise.ordinal()], v, f, (12 - c) * 11, 4);
        }
        for (c = 0; c <= 23; c++) {
            v = Math.abs(c % 4 - 2) + 1;
            chaosSounds.SetEffect(dieTrefleEffect[c], chaosSounds.soundList[SoundList.wCrash.ordinal()], 700, 4182 + v * 3485, (24 - c) * v * 3, 4);
        }
        chaosSounds.SetEffect(aieCartoonEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 800, 8363, 40, 1);
        chaosSounds.SetEffect(aieDrawerEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 400, 12544, 80, 1);
        chaosSounds.SetEffect(aieDrawerEffect[1], chaosSounds.soundList[SoundList.wNoise.ordinal()], 400, 6272, 80, 1);
        chaosSounds.SetEffect(dieBigDrawerEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 582, 8860, 160, 5);
        chaosSounds.SetEffect(dieSmallDrawerEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 582, 0, 160, 5);
        for (c = 1; c <= 16; c++) {
            v = (17 - c) * 8;
            chaosSounds.SetEffect(dieBigDrawerEffect[c], chaosSounds.nulSound, 582, 12544 - (c % 2) * 6969, v, 4);
            v = v * (c % 2);
            chaosSounds.SetEffect(dieSmallDrawerEffect[c], chaosSounds.nulSound, 582, 0, v, 4);
        }
        chaosSounds.SetEffect(aieBigEffect[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 4181, 100, 2);
        chaosSounds.SetEffect(dieBigEffect[0], chaosSounds.soundList[SoundList.sVerre.ordinal()], 0, 16726, 192, 4);
        chaosSounds.SetEffect(aieSquareEffect[0], chaosSounds.soundList[SoundList.wVoice.ordinal()], 300, 0, 200, 2);
        chaosSounds.SetEffect(dieSquareEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 209, 4181, 192, 4);
        chaosSounds.SetEffect(dieSquareEffect[1], chaosSounds.soundList[SoundList.wNoise.ordinal()], 418, 8363, 192, 4);
        chaosSounds.SetEffect(dieSquareEffect[2], chaosSounds.soundList[SoundList.wNoise.ordinal()], 836, 16726, 192, 4);
        chaosSounds.SetEffect(dieSquareEffect[3], chaosSounds.soundList[SoundList.sHHat.ordinal()], 0, 8363, 192, 4);
        chaosSounds.SetEffect(dieSquareEffect[4], chaosSounds.soundList[SoundList.sHHat.ordinal()], 0, 16726, 192, 4);
        chaosSounds.SetEffect(dieSquareEffect[5], chaosSounds.soundList[SoundList.sCymbale.ordinal()], 0, 8363, 192, 4);
        chaosSounds.SetEffect(dieSquareEffect[6], chaosSounds.soundList[SoundList.wCrash.ordinal()], 418, 8363, 220, 4);
        chaosSounds.SetEffect(dieSquareEffect[7], chaosSounds.soundList[SoundList.wCrash.ordinal()], 279, 5575, 220, 4);
        chaosSounds.SetEffect(dieSquareEffect[8], chaosSounds.soundList[SoundList.wCrash.ordinal()], 418, 4181, 220, 4);
        chaosSounds.SetEffect(dieSquareEffect[9], chaosSounds.soundList[SoundList.sHurryUp.ordinal()], 0, 16726, 220, 4);
        chaosSounds.SetEffect(dieSquareEffect[10], chaosSounds.soundList[SoundList.sGun.ordinal()], 0, 8363, 160, 4);
        chaosSounds.SetEffect(dieSquareEffect[11], chaosSounds.soundList[SoundList.wVoice.ordinal()], 6000, 9387, 210, 4);
        chaosSounds.SetEffect(dieSquareEffect[12], chaosSounds.soundList[SoundList.wWhite.ordinal()], 523, 4181, 192, 4);
        chaosSounds.SetEffect(dieSquareEffect[13], chaosSounds.soundList[SoundList.wWhite.ordinal()], 8363, 16726, 192, 4);
        chaosSounds.SetEffect(aieTriEffect[0], chaosSounds.soundList[SoundList.wCrash.ordinal()], 522, 4181, 180, 1);
        for (c = 0; c <= 11; c++) {
            v = (12 - c) * 13 * ((c + 1) % 2);
            chaosSounds.SetEffect(dieTriEffect[c], chaosSounds.soundList[SoundList.wNoise.ordinal()], 896, 12544, v, 4);
        }
        for (c = 0; c <= 15; c++) {
            f = (c % 4);
            v = (16 - c) * 12;
            if (f == 3)
                v = 0;
            if (f == 0)
                f = 16726;
            else if (f == 1)
                f = 8363;
            else
                f = 4181;
            chaosSounds.SetEffect(dieBumperEffect[c], chaosSounds.soundList[SoundList.wNoise.ordinal()], f / 20, f, v, 4);
        }
        chaosSounds.SetEffect(dieFlameEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 836, 16726, 150, 3);
        chaosSounds.SetEffect(dieFlameEffect[1], chaosSounds.soundList[SoundList.wNoise.ordinal()], 418, 8363, 150, 3);
        chaosSounds.SetEffect(dieFlameEffect[2], chaosSounds.soundList[SoundList.wNoise.ordinal()], 209, 4181, 150, 3);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetCartoon_ref;
        attr.Make = MakeCartoon_ref;
        attr.Move = MoveCartoon_ref;
        attr.Aie = AieCartoon_ref;
        attr.Die = DieCartoon_ref;
        attr.weight = 100;
        attr.heatSpeed = 170;
        attr.refreshSpeed = 140;
        attr.coolSpeed = 0;
        attr.aieStKinds = EnumSet.of(Stones.stFOG3);
        attr.aieSKCount = 1;
        attr.aieStone = 1;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stFOG3);
        attr.dieSKCount = 1;
        attr.dieStone = 8;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Mineral;
        attr.priority = -70;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetAlien0_ref;
        attr.Make = MakeAlien0_ref;
        attr.Move = MoveAlien0_ref;
        attr.Aie = AieAlien0_ref;
        attr.Die = DieAlien0_ref;
        attr.charge = 70;
        attr.weight = 12;
        attr.inerty = 24;
        attr.heatSpeed = 100;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 10;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.gravityStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 6;
        attr.dieStStyle = ChaosBase.gravityStyle;
        attr.basicType = BasicTypes.Animal;
        attr.priority = -60;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetSmallDrawer_ref;
        attr.Make = MakeSmallDrawer_ref;
        attr.Move = MoveDrawer_ref;
        attr.Aie = AieDrawer_ref;
        attr.Die = DieSmallDrawer_ref;
        attr.charge = 20;
        attr.weight = 50;
        attr.inerty = 30;
        attr.priority = 8;
        attr.heatSpeed = 75;
        attr.refreshSpeed = 50;
        attr.coolSpeed = 10;
        attr.aieStKinds = EnumSet.of(Stones.stFLAME2);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stCBOX, Stones.stFOG1);
        attr.dieSKCount = 2;
        attr.dieStone = 7;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Bonus;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetBigDrawer_ref;
        attr.Make = MakeBigDrawer_ref;
        attr.Move = MoveDrawer_ref;
        attr.Aie = AieDrawer_ref;
        attr.Die = DieBigDrawer_ref;
        attr.charge = 10;
        attr.weight = 100;
        attr.inerty = 60;
        attr.priority = 10;
        attr.heatSpeed = 75;
        attr.refreshSpeed = 50;
        attr.coolSpeed = 10;
        attr.aieStKinds = EnumSet.of(Stones.stFLAME1);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stCBOX, Stones.stRBOX, Stones.stFOG1);
        attr.dieSKCount = 3;
        attr.dieStone = 13;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Bonus;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetHospital_ref;
        attr.Make = MakeHospital_ref;
        attr.Move = MoveHospital_ref;
        attr.Aie = AieHospital_ref;
        attr.Die = DieHospital_ref;
        attr.charge = 15;
        attr.weight = 40;
        attr.priority = -30;
        attr.heatSpeed = 30;
        attr.refreshSpeed = 30;
        attr.coolSpeed = 30;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 5;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetDiese_ref;
        attr.Make = MakeDiese_ref;
        attr.Move = MoveDiese_ref;
        attr.Aie = AieDiese_ref;
        attr.Die = DieDiese_ref;
        attr.charge = 50;
        attr.weight = 50;
        attr.inerty = 50;
        attr.priority = -59;
        attr.heatSpeed = 40;
        attr.refreshSpeed = 35;
        attr.coolSpeed = 15;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 1;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 4;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetKamikaze_ref;
        attr.Make = MakeKamikaze_ref;
        attr.Move = MoveKamikaze_ref;
        attr.Aie = AieKamikaze_ref;
        attr.Die = DieKamikaze_ref;
        attr.charge = 80;
        attr.weight = 100;
        attr.inerty = 100;
        attr.priority = -52;
        attr.dieStKinds = EnumSet.of(Stones.stCROSS, Stones.stFLAME1, Stones.stFLAME2, Stones.stFOG1);
        attr.dieSKCount = 4;
        attr.dieStone = 20;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetStar_ref;
        attr.Make = MakeStar_ref;
        attr.Move = MoveStarBubble_ref;
        attr.Aie = AieSBB_ref;
        attr.Die = DieStarBubble_ref;
        attr.charge = 10;
        attr.weight = 50;
        attr.inerty = 32;
        attr.priority = -55;
        attr.aieStKinds = EnumSet.of(Stones.stC35, Stones.stFLAME1, Stones.stFLAME2);
        attr.aieSKCount = 3;
        attr.aieStone = 5;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stFOG1, Stones.stFLAME1, Stones.stFLAME2);
        attr.dieSKCount = 3;
        attr.dieStone = 9;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetBubble_ref;
        attr.Make = MakeBubble_ref;
        attr.Move = MoveStarBubble_ref;
        attr.Aie = AieSBB_ref;
        attr.Die = DieStarBubble_ref;
        attr.charge = 100;
        attr.weight = 50;
        attr.inerty = 32;
        attr.priority = -55;
        attr.heatSpeed = 80;
        attr.refreshSpeed = 5;
        attr.aieStKinds = EnumSet.of(Stones.stC35, Stones.stFLAME1, Stones.stFLAME2);
        attr.aieSKCount = 3;
        attr.aieStone = 5;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stRE, Stones.stFLAME1, Stones.stFLAME2);
        attr.dieSKCount = 3;
        attr.dieStone = 9;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetBumper_ref;
        attr.Make = MakeBumper_ref;
        attr.Move = MoveBumper_ref;
        attr.Aie = AieSBB_ref;
        attr.Die = DieBumper_ref;
        attr.charge = 30;
        attr.weight = 60;
        attr.inerty = 60;
        attr.priority = -54;
        attr.heatSpeed = 30;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 20;
        attr.aieStKinds = EnumSet.of(Stones.stFLAME1, Stones.stFLAME2);
        attr.aieSKCount = 2;
        attr.aieStone = 6;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 12;
        attr.dieStStyle = ChaosBase.returnStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetPic_ref;
        attr.Make = MakePic_ref;
        attr.Move = MovePic_ref;
        attr.Aie = AiePic_ref;
        attr.Die = DieKamikaze_ref;
        attr.charge = 40;
        attr.weight = 120;
        attr.inerty = 160;
        attr.priority = -52;
        attr.dieStKinds = EnumSet.of(Stones.stFLAME2);
        attr.dieSKCount = 1;
        attr.dieStone = 31;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetTri_ref;
        attr.Make = MakeTri_ref;
        attr.Move = MoveTri_ref;
        attr.Aie = AieTri_ref;
        attr.Die = DieTri_ref;
        attr.charge = 8;
        attr.weight = 40;
        attr.inerty = 100;
        attr.priority = -54;
        attr.heatSpeed = 30;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 15;
        attr.aieStKinds = EnumSet.of(Stones.stFLAME2);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 5;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetTrefle_ref;
        attr.Make = MakeTrefle_ref;
        attr.Move = MoveTrefle_ref;
        attr.Aie = AieTrefle_ref;
        attr.Die = DieTrefle_ref;
        attr.charge = 55;
        attr.weight = 20;
        attr.inerty = 200;
        attr.priority = -59;
        attr.heatSpeed = 100;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 10;
        attr.aieStKinds = EnumSet.of(Stones.stFLAME1);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stCROSS);
        attr.dieSKCount = 1;
        attr.dieStone = 5;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetBig_ref;
        attr.Make = MakeBig_ref;
        attr.Move = MoveBig_ref;
        attr.Aie = AieBig_ref;
        attr.Die = DieBig_ref;
        attr.charge = 5;
        attr.weight = 80;
        attr.inerty = 100;
        attr.priority = -40;
        attr.heatSpeed = 100;
        attr.refreshSpeed = 20;
        attr.coolSpeed = 10;
        attr.aieStKinds = EnumSet.of(Stones.stFOG1, Stones.stFOG2);
        attr.aieSKCount = 2;
        attr.aieStone = 4;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stFOG3);
        attr.dieSKCount = 1;
        attr.dieStone = ChaosBase.FlameMult + 16;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Mineral;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetSquare_ref;
        attr.Make = MakeSquare_ref;
        attr.Move = MoveBig_ref;
        attr.Aie = AieSquare_ref;
        attr.Die = DieSquare_ref;
        attr.charge = 60;
        attr.weight = 30;
        attr.inerty = 60;
        attr.priority = -40;
        attr.heatSpeed = 20;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 60;
        attr.aieStKinds = EnumSet.of(Stones.stC26);
        attr.aieSKCount = 1;
        attr.aieStone = 5;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.noneOf(Stones.class);
        attr.dieSKCount = 0;
        attr.dieStone = ChaosBase.FlameMult * 7;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetFlame_ref;
        attr.Make = MakeFlame_ref;
        attr.Move = MoveFlame_ref;
        attr.Aie = AieAlien0_ref;
        attr.Die = DieFlame_ref;
        attr.charge = 50;
        attr.weight = 30;
        attr.inerty = 40;
        attr.heatSpeed = 50;
        attr.refreshSpeed = 50;
        attr.coolSpeed = 12;
        attr.aieStKinds = EnumSet.of(Stones.stFLAME2);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stFLAME1);
        attr.dieSKCount = 1;
        attr.dieStone = 6;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Animal;
        attr.priority = -30;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetColor_ref;
        attr.Make = MakeColor_ref;
        attr.Move = MoveAlien0_ref;
        attr.Aie = AieAlien0_ref;
        attr.Die = DieAlien0_ref;
        attr.charge = 70;
        attr.weight = 12;
        attr.inerty = 24;
        attr.heatSpeed = 100;
        attr.refreshSpeed = 100;
        attr.coolSpeed = 10;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.gravityStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 6;
        attr.dieStStyle = ChaosBase.gravityStyle;
        attr.basicType = BasicTypes.Animal;
        attr.priority = -60;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN1.ordinal()], attr.node);
    }


    // Support

    private static ChaosAlien instance;

    public static ChaosAlien instance() {
        if (instance == null)
            new ChaosAlien(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        zeroFire = 0;
        InitParams();
    }

    public void close() {
    }

}
