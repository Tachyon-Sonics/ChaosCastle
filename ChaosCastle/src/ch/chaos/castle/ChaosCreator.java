package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.GameStat;
import ch.chaos.castle.ChaosBase.ObjFlags;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;
import java.util.EnumSet;


public class ChaosCreator {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosBonus chaosBonus;
    private final ChaosGraphics chaosGraphics;
    private final ChaosPlayer chaosPlayer;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Languages languages;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosCreator() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosBonus = ChaosBonus.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosPlayer = ChaosPlayer.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // CONST

    public static final int cAlienV = 0;
    public static final int cAlienA = 1;
    public static final int cCreatorR = 2;
    public static final int cCreatorC = 4;
    public static final int cCircle = 3;
    public static final int cController = 5;
    public static final int cHurryUp = 6;
    public static final int cChief = 7;
    public static final int cFour = 8;
    public static final int cAlienBox = 9;
    public static final int cNest = 10;
    public static final int cGrid = 11;
    public static final int cMissile = 12;
    public static final int cPopUp = 13;
    public static final int cGhost = 14;
    public static final int cQuad = 15;


    // TYPE

    private static class ChiefData { // RECORD

        private short dstx;
        private short dsty;
        private short tox;
        private short toy;
        private short crx;
        private short cry;
        private short angle;
        private boolean dir;
        private boolean used;


        public short getDstx() {
            return this.dstx;
        }

        public void setDstx(short dstx) {
            this.dstx = dstx;
        }

        public short getDsty() {
            return this.dsty;
        }

        public void setDsty(short dsty) {
            this.dsty = dsty;
        }

        public short getTox() {
            return this.tox;
        }

        public void setTox(short tox) {
            this.tox = tox;
        }

        public short getToy() {
            return this.toy;
        }

        public void setToy(short toy) {
            this.toy = toy;
        }

        public short getCrx() {
            return this.crx;
        }

        public void setCrx(short crx) {
            this.crx = crx;
        }

        public short getCry() {
            return this.cry;
        }

        public void setCry(short cry) {
            this.cry = cry;
        }

        public short getAngle() {
            return this.angle;
        }

        public void setAngle(short angle) {
            this.angle = angle;
        }

        public boolean isDir() {
            return this.dir;
        }

        public void setDir(boolean dir) {
            this.dir = dir;
        }

        public boolean isUsed() {
            return this.used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }


        public void copyFrom(ChiefData other) {
            this.dstx = other.dstx;
            this.dsty = other.dsty;
            this.tox = other.tox;
            this.toy = other.toy;
            this.crx = other.crx;
            this.cry = other.cry;
            this.angle = other.angle;
            this.dir = other.dir;
            this.used = other.used;
        }

        public ChiefData newCopy() {
            ChiefData copy = new ChiefData();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    private ChaosSounds.Effect[] hurryupEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] missileEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieCircleEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieCircleEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieHUEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieAlienEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieCreatorEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieHUEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieChiefEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieGhostEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] circleFireEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] diePopupEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] aieGhostEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieAlienVEffect = Runtime.initArray(new ChaosSounds.Effect[17]);
    private ChaosSounds.Effect[] dieAlienAEffect = Runtime.initArray(new ChaosSounds.Effect[17]);
    private ChaosSounds.Effect[] dieABEffect = Runtime.initArray(new ChaosSounds.Effect[12]);
    private ChaosSounds.Effect[] dieCreatorREffect = Runtime.initArray(new ChaosSounds.Effect[24]);
    private ChaosSounds.Effect[] dieNestEffect = Runtime.initArray(new ChaosSounds.Effect[24]);
    private ChaosSounds.Effect[] dieCreatorCEffect = Runtime.initArray(new ChaosSounds.Effect[8]);
    private ChaosSounds.Effect[] dieFourEffect = Runtime.initArray(new ChaosSounds.Effect[9]);
    private ChaosSounds.Effect[] popupEffect = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] aieGridEffect = Runtime.initArray(new ChaosSounds.Effect[4]);
    private int huCnt;
    private ChiefData[] chiefData = Runtime.initArray(new ChiefData[4]);


    public ChaosSounds.Effect[] getHurryupEffect() {
        return this.hurryupEffect;
    }

    public void setHurryupEffect(ChaosSounds.Effect[] hurryupEffect) {
        this.hurryupEffect = hurryupEffect;
    }

    public ChaosSounds.Effect[] getMissileEffect() {
        return this.missileEffect;
    }

    public void setMissileEffect(ChaosSounds.Effect[] missileEffect) {
        this.missileEffect = missileEffect;
    }

    public ChaosSounds.Effect[] getAieCircleEffect() {
        return this.aieCircleEffect;
    }

    public void setAieCircleEffect(ChaosSounds.Effect[] aieCircleEffect) {
        this.aieCircleEffect = aieCircleEffect;
    }

    public ChaosSounds.Effect[] getDieCircleEffect() {
        return this.dieCircleEffect;
    }

    public void setDieCircleEffect(ChaosSounds.Effect[] dieCircleEffect) {
        this.dieCircleEffect = dieCircleEffect;
    }

    public ChaosSounds.Effect[] getDieHUEffect() {
        return this.dieHUEffect;
    }

    public void setDieHUEffect(ChaosSounds.Effect[] dieHUEffect) {
        this.dieHUEffect = dieHUEffect;
    }

    public ChaosSounds.Effect[] getAieAlienEffect() {
        return this.aieAlienEffect;
    }

    public void setAieAlienEffect(ChaosSounds.Effect[] aieAlienEffect) {
        this.aieAlienEffect = aieAlienEffect;
    }

    public ChaosSounds.Effect[] getAieCreatorEffect() {
        return this.aieCreatorEffect;
    }

    public void setAieCreatorEffect(ChaosSounds.Effect[] aieCreatorEffect) {
        this.aieCreatorEffect = aieCreatorEffect;
    }

    public ChaosSounds.Effect[] getAieHUEffect() {
        return this.aieHUEffect;
    }

    public void setAieHUEffect(ChaosSounds.Effect[] aieHUEffect) {
        this.aieHUEffect = aieHUEffect;
    }

    public ChaosSounds.Effect[] getAieChiefEffect() {
        return this.aieChiefEffect;
    }

    public void setAieChiefEffect(ChaosSounds.Effect[] aieChiefEffect) {
        this.aieChiefEffect = aieChiefEffect;
    }

    public ChaosSounds.Effect[] getDieGhostEffect() {
        return this.dieGhostEffect;
    }

    public void setDieGhostEffect(ChaosSounds.Effect[] dieGhostEffect) {
        this.dieGhostEffect = dieGhostEffect;
    }

    public ChaosSounds.Effect[] getCircleFireEffect() {
        return this.circleFireEffect;
    }

    public void setCircleFireEffect(ChaosSounds.Effect[] circleFireEffect) {
        this.circleFireEffect = circleFireEffect;
    }

    public ChaosSounds.Effect[] getDiePopupEffect() {
        return this.diePopupEffect;
    }

    public void setDiePopupEffect(ChaosSounds.Effect[] diePopupEffect) {
        this.diePopupEffect = diePopupEffect;
    }

    public ChaosSounds.Effect[] getAieGhostEffect() {
        return this.aieGhostEffect;
    }

    public void setAieGhostEffect(ChaosSounds.Effect[] aieGhostEffect) {
        this.aieGhostEffect = aieGhostEffect;
    }

    public ChaosSounds.Effect[] getDieAlienVEffect() {
        return this.dieAlienVEffect;
    }

    public void setDieAlienVEffect(ChaosSounds.Effect[] dieAlienVEffect) {
        this.dieAlienVEffect = dieAlienVEffect;
    }

    public ChaosSounds.Effect[] getDieAlienAEffect() {
        return this.dieAlienAEffect;
    }

    public void setDieAlienAEffect(ChaosSounds.Effect[] dieAlienAEffect) {
        this.dieAlienAEffect = dieAlienAEffect;
    }

    public ChaosSounds.Effect[] getDieABEffect() {
        return this.dieABEffect;
    }

    public void setDieABEffect(ChaosSounds.Effect[] dieABEffect) {
        this.dieABEffect = dieABEffect;
    }

    public ChaosSounds.Effect[] getDieCreatorREffect() {
        return this.dieCreatorREffect;
    }

    public void setDieCreatorREffect(ChaosSounds.Effect[] dieCreatorREffect) {
        this.dieCreatorREffect = dieCreatorREffect;
    }

    public ChaosSounds.Effect[] getDieNestEffect() {
        return this.dieNestEffect;
    }

    public void setDieNestEffect(ChaosSounds.Effect[] dieNestEffect) {
        this.dieNestEffect = dieNestEffect;
    }

    public ChaosSounds.Effect[] getDieCreatorCEffect() {
        return this.dieCreatorCEffect;
    }

    public void setDieCreatorCEffect(ChaosSounds.Effect[] dieCreatorCEffect) {
        this.dieCreatorCEffect = dieCreatorCEffect;
    }

    public ChaosSounds.Effect[] getDieFourEffect() {
        return this.dieFourEffect;
    }

    public void setDieFourEffect(ChaosSounds.Effect[] dieFourEffect) {
        this.dieFourEffect = dieFourEffect;
    }

    public ChaosSounds.Effect[] getPopupEffect() {
        return this.popupEffect;
    }

    public void setPopupEffect(ChaosSounds.Effect[] popupEffect) {
        this.popupEffect = popupEffect;
    }

    public ChaosSounds.Effect[] getAieGridEffect() {
        return this.aieGridEffect;
    }

    public void setAieGridEffect(ChaosSounds.Effect[] aieGridEffect) {
        this.aieGridEffect = aieGridEffect;
    }

    public int getHuCnt() {
        return this.huCnt;
    }

    public void setHuCnt(int huCnt) {
        this.huCnt = huCnt;
    }

    public ChiefData[] getChiefData() {
        return this.chiefData;
    }

    public void setChiefData(ChiefData[] chiefData) {
        this.chiefData = chiefData;
    }


    // PROCEDURE

    private void MakeAlienV(ChaosBase.Obj alien) {
        // VAR
        short px = 0;

        if (alien.moveSeq < ChaosBase.Period)
            px = 20;
        else
            px = 0;
        chaosActions.SetObjLoc(alien, px, (short) 128, (short) 20, (short) 20);
        chaosActions.SetObjRect(alien, 0, 0, 20, 20);
    }

    private final ChaosBase.MakeProc MakeAlienV_ref = this::MakeAlienV;

    private void MakeAlienA(ChaosBase.Obj alien) {
        // VAR
        short px = 0;

        if ((alien.moveSeq < ChaosBase.Period) && (alien.shapeSeq == 0))
            px = 60;
        else
            px = 40;
        chaosActions.SetObjLoc(alien, px, (short) 128, (short) 20, (short) 20);
        chaosActions.SetObjRect(alien, 0, 0, 20, 20);
    }

    private final ChaosBase.MakeProc MakeAlienA_ref = this::MakeAlienA;

    private void MakeFour(ChaosBase.Obj four) {
        // VAR
        short py = 0;

        if (four.moveSeq < ChaosBase.Period)
            py = 184;
        else
            py = 160;
        chaosActions.SetObjLoc(four, (short) 200, py, (short) 24, (short) 24);
        chaosActions.SetObjRect(four, 0, 0, 24, 24);
    }

    private final ChaosBase.MakeProc MakeFour_ref = this::MakeFour;

    private void MakeQuad(ChaosBase.Obj quad) {
        chaosActions.SetObjLoc(quad, (short) 5, (short) 185, (short) 22, (short) 22);
        chaosActions.SetObjRect(quad, 0, 0, 22, 22);
    }

    private final ChaosBase.MakeProc MakeQuad_ref = this::MakeQuad;

    private void MakeCreatorR(ChaosBase.Obj creator) {
        chaosActions.SetObjLoc(creator, (short) 96, (short) 128, (short) 32, (short) 32);
        chaosActions.SetObjRect(creator, 0, 0, 32, 32);
    }

    private final ChaosBase.MakeProc MakeCreatorR_ref = this::MakeCreatorR;

    private void MakeCreatorC(ChaosBase.Obj creator) {
        if (creator.moveSeq >= ChaosBase.Period * 3) {
            chaosActions.SetObjLoc(creator, (short) 128, (short) 132, (short) 28, (short) 28);
            chaosActions.SetObjRect(creator, 0, 0, 28, 28);
        } else {
            chaosActions.SetObjLoc(creator, (short) 156, (short) 132, (short) 28, (short) 28);
        }
    }

    private final ChaosBase.MakeProc MakeCreatorC_ref = this::MakeCreatorC;

    private void MakeCircle(ChaosBase.Obj circle) {
        if (circle.shapeSeq == 0)
            chaosActions.SetObjLoc(circle, (short) 64, (short) 96, (short) 32, (short) 32);
        else
            chaosActions.SetObjLoc(circle, (short) 224, (short) 132, (short) 32, (short) 32);
        chaosActions.SetObjRect(circle, 1, 1, 31, 31);
    }

    private final ChaosBase.MakeProc MakeCircle_ref = this::MakeCircle;

    private void FireMeteorite(short px, short py, short mvx, short mvy, int ns) {
        // VAR
        ChaosBase.Obj mt = null;

        mt = chaosActions.CreateObj(Anims.DEADOBJ, (short) ChaosDObj.doMeteor, px, py, ns, 1);
        if (ns == ChaosDObj.domSmall) {
            mvx = (short) (mvx * 2);
            mvy = (short) (mvy * 2);
        }
        chaosActions.SetObjVXY(mt, mvx, mvy);
    }

    private void MakeController(ChaosBase.Obj ctrl) {
        // CONST
        final int MX = ChaosGraphics.PW / 2;
        final int MY = ChaosGraphics.PH / 2;

        // VAR
        ChaosBase.Obj hu = null;
        ChaosBase.Obj wv = null;
        ChaosBase.ObjAttr wa = null;
        int mvx = 0;
        int mvy = 0;
        int speed = 0;
        short px = 0;
        short py = 0;
        short nvx = 0;
        short angle = 0;
        int md = 0;
        int rt = 0;
        int dv = 0;
        int nb = 0;
        boolean mf = false;

        if (ctrl.moveSeq == 0) {
            if (chaosBase.zone != Zone.Castle) {
                if (ctrl.stat == 0) {
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Hurry up !"), String.class), (short) ChaosActions.statPos, (short) 1);
                    if ((huCnt == 0) && (chaosBase.stages == 3))
                        chaosActions.NextStage();
                    else if (huCnt > 0)
                        huCnt--;
                    if (trigo.RND() % 2 == 0) {
                        px = -ChaosGraphics.BW;
                        nvx = 800;
                    } else {
                        px = (short) (chaosGraphics.gameWidth + ChaosGraphics.BW);
                        nvx = -800;
                    }
                    md = (chaosGraphics.gameHeight / 2);
                    py = (short) (trigo.RND() % md);
                    hu = chaosActions.CreateObj(Anims.ALIEN2, (short) cHurryUp, px, py, 1, 100);
                    chaosActions.SetObjVXY(hu, nvx, (short) 0);
                    chaosSounds.SoundEffect(hu, hurryupEffect);
                }
                ctrl.stat = 0;
                if (chaosBase.pLife > 20)
                    ctrl.moveSeq = (ChaosBase.Period * 20);
                else
                    ctrl.moveSeq = ChaosBase.Period * (80 - chaosBase.pLife * 2) / 2;
                if (chaosBase.zone != Zone.Chaos)
                    ctrl.moveSeq = ctrl.moveSeq * 4;
            }
        }
        if (ctrl.shapeSeq == 0) {
            if (chaosBase.water) {
                wa = chaosBase.GetAnimAttr(Anims.DEADOBJ, (short) ChaosDObj.doWave);
                if (wa.nbObj < 12) {
                    angle = (short) (trigo.RND() % 360);
                    px = (short) (trigo.COS(angle) / 16 * ChaosGraphics.PW / 64);
                    py = (short) (trigo.SIN(angle) / 16 * ChaosGraphics.PH / 64);
                    px += chaosGraphics.backpx + MX;
                    py += chaosGraphics.backpy + MY;
                    speed = trigo.RND() % 512 + 512;
                    angle += trigo.RND() % 256;
                    angle -= 128;
                    mvx = trigo.COS(angle);
                    mvy = trigo.SIN(angle);
                    mvx = -(mvx * speed / 1024);
                    mvy = -(mvy * speed / 1024);
                    wv = chaosActions.CreateObj(Anims.DEADOBJ, (short) ChaosDObj.doWave, px, py, trigo.RND() % 15, 1);
                    chaosActions.SetObjVXY(wv, (short) mvx, (short) mvy);
                }
                ctrl.shapeSeq = ChaosBase.Period / 2;
            }
            if ((chaosBase.difficulty >= 3) && ((chaosBase.zone == Zone.Chaos) || ((chaosBase.zone == Zone.Family) && (chaosBase.level[Zone.Family.ordinal()] == 6)))) {
                mf = false;
                angle = (short) (trigo.RND() % 360);
                px = (short) (trigo.COS(angle) / 16 * ChaosGraphics.PW / 64);
                py = (short) (trigo.SIN(angle) / 16 * ChaosGraphics.PH / 64);
                px += chaosGraphics.backpx + MX;
                py += chaosGraphics.backpy + MY;
                speed = trigo.RND() % 1024 + 512;
                angle += trigo.RND() % 64;
                angle -= 32;
                mvx = trigo.COS(angle);
                mvy = trigo.SIN(angle);
                mvx = -(mvx * speed / 1024);
                mvy = -(mvy * speed / 1024);
                if (chaosBase.level[Zone.Castle.ordinal()] > 1) {
                    rt = trigo.SQRT(chaosBase.level[Zone.Chaos.ordinal()]);
                    dv = 2;
                    nb = 0;
                    while ((nb == 0) && (dv <= rt)) {
                        if (chaosBase.level[Zone.Chaos.ordinal()] % dv == 0)
                            nb++;
                        dv++;
                    }
                    if ((nb == 0) || (chaosBase.level[Zone.Chaos.ordinal()] % 33 == 0) || ((chaosBase.difficulty >= 6) && (chaosBase.level[Zone.Chaos.ordinal()] % 10 == 0))) {
                        FireMeteorite(px, py, (short) mvx, (short) mvy, ChaosDObj.domBig);
                        mf = true;
                    }
                    if (chaosBase.difficulty >= 5) {
                        md = chaosBase.level[Zone.Chaos.ordinal()] % 13;
                        if (md % 8 == 0) {
                            px = (short) (ChaosGraphics.PW - px);
                            mvx = -mvx;
                            py = (short) (ChaosGraphics.PH - py);
                            mvy = -mvy;
                            mf = true;
                            FireMeteorite(px, py, (short) mvx, (short) mvy, ChaosDObj.domMedium);
                        }
                    }
                    md = chaosBase.level[Zone.Chaos.ordinal()] % 10;
                    if ((md == 9) || ((chaosBase.difficulty >= 4) && (md == 3))) {
                        if (trigo.RND() % 2 == 0) {
                            px = (short) (ChaosGraphics.PW - px);
                            py = (short) (ChaosGraphics.PH - py);
                            mvx = -mvx;
                            mvy = -mvy;
                        } else {
                            px += mvy / 64;
                            py -= mvx / 64;
                            if (mf && (trigo.RND() % 8 == 0)) {
                                FireMeteorite(px, py, (short) mvx, (short) mvy, ChaosDObj.domSmall);
                                px -= mvy / 24;
                                py += mvx / 32;
                            }
                        }
                        FireMeteorite(px, py, (short) mvx, (short) mvy, ChaosDObj.domSmall);
                    }
                }
                if ((chaosBase.difficulty >= 7) && ((chaosBase.level[Zone.Chaos.ordinal()] % 7 == 0) || (chaosBase.level[Zone.Chaos.ordinal()] == 100))) {
                    py = (short) (ChaosGraphics.PH - py);
                    mvy = -mvy;
                    px = (short) (ChaosGraphics.PW - px);
                    mvx = -mvx;
                    FireMeteorite(px, py, (short) mvx, (short) mvy, ChaosDObj.domStone);
                }
                nb = (10 - chaosBase.difficulty / 2);
                if (chaosBase.level[Zone.Chaos.ordinal()] % 33 == 0)
                    nb = nb / 3;
                ctrl.shapeSeq = trigo.RND() % (ChaosBase.Period * nb);
            }
        }
    }

    private void MakeHurryUp(ChaosBase.Obj hu) {
        chaosActions.SetObjLoc(hu, (short) 40, (short) 80, (short) 24, (short) 16);
        chaosActions.SetObjRect(hu, 0, 0, 24, 16);
    }

    private final ChaosBase.MakeProc MakeHurryUp_ref = this::MakeHurryUp;

    private void MakeChief(ChaosBase.Obj chief) {
        chaosActions.SetObjLoc(chief, (short) 128, (short) 72, (short) 16, (short) 16);
        chaosActions.SetObjRect(chief, 0, 0, 16, 16);
    }

    private final ChaosBase.MakeProc MakeChief_ref = this::MakeChief;

    private void MakeABox(ChaosBase.Obj abox) {
        chaosActions.SetObjLoc(abox, (short) 0, (short) 180, (short) 32, (short) 32);
        chaosActions.SetObjRect(abox, 0, 0, 32, 32);
    }

    private final ChaosBase.MakeProc MakeABox_ref = this::MakeABox;

    private void MakeNest(ChaosBase.Obj nest) {
        chaosActions.SetObjLoc(nest, (short) 184, (short) 132, (short) 28, (short) 28);
        chaosActions.SetObjRect(nest, 3, 3, 25, 25);
    }

    private final ChaosBase.MakeProc MakeNest_ref = this::MakeNest;

    private void MakeGrid(ChaosBase.Obj grid) {
        chaosActions.SetObjLoc(grid, (short) 96, (short) 96, (short) 32, (short) 32);
        chaosActions.SetObjRect(grid, 2, 2, 30, 30);
    }

    private final ChaosBase.MakeProc MakeGrid_ref = this::MakeGrid;

    private void MakeMissile(ChaosBase.Obj missile) {
        // VAR
        short px = 0;

        missile.hitSubLife = 1;
        missile.fireSubLife = 1;
        if (missile.stat == 0)
            px = 84;
        else
            px = 64;
        chaosActions.SetObjLoc(missile, px, (short) 100, (short) 12, (short) 24);
        chaosActions.SetObjRect(missile, 0, 0, 12, 24);
    }

    private final ChaosBase.ResetProc MakeMissile_as_ChaosBase_ResetProc = this::MakeMissile;
    private final ChaosBase.MakeProc MakeMissile_as_ChaosBase_MakeProc = this::MakeMissile;

    private void MakePopUp(ChaosBase.Obj popup) {
        // VAR
        short sz = 0;

        if (popup.stat == 2)
            sz = 0;
        else
            sz = 12;
        chaosActions.SetObjLoc(popup, (short) 52, (short) 200, sz, sz);
        chaosActions.SetObjRect(popup, 0, 0, sz, sz);
    }

    private final ChaosBase.MakeProc MakePopUp_ref = this::MakePopUp;

    private void MakeGhost(ChaosBase.Obj ghost) {
        chaosActions.SetObjLoc(ghost, (short) 120, (short) 180, (short) 20, (short) 20);
        chaosActions.SetObjRect(ghost, 0, 0, 20, 20);
    }

    private final ChaosBase.MakeProc MakeGhost_ref = this::MakeGhost;

    private void ResetAlienV(ChaosBase.Obj alien) {
        // VAR
        short nvx = 0;
        short nvy = 0;
        int time = 0;

        if (chaosBase.pLife > 20)
            time = 60;
        else
            time = chaosBase.pLife * 3;
        time = 80 - time;
        if (chaosBase.difficulty >= time)
            time = 1;
        else
            time -= chaosBase.difficulty;
        if (alien.flags.contains(ObjFlags.displayed))
            alien.moveSeq = ChaosBase.Period * (trigo.RND() % time + 2);
        else
            alien.moveSeq = ChaosBase.Period + trigo.RND() % (ChaosBase.Period * 2);
        alien.shapeSeq = 0;
        alien.hitSubLife = alien.life;
        alien.fireSubLife = alien.life;
        nvx = (short) (trigo.RND() % 2048);
        nvy = (short) (trigo.RND() % 2048);
        alien.dvx = (short) (nvx - 1024);
        alien.dvy = (short) (nvy - 1024);
        MakeAlienV(alien);
    }

    private final ChaosBase.ResetProc ResetAlienV_ref = this::ResetAlienV;

    private void ResetAlienA(ChaosBase.Obj alien) {
        // VAR
        byte nax = 0;
        byte nay = 0;
        int time = 0;

        if (chaosBase.pLife > 20)
            time = 20;
        else
            time = chaosBase.pLife;
        time = 26 - time;
        if (chaosBase.difficulty >= time)
            time = 1;
        else
            time -= chaosBase.difficulty;
        if (alien.flags.contains(ObjFlags.displayed)) {
            alien.moveSeq = ChaosBase.Period * (trigo.RND() % time + 2);
            if (alien.shapeSeq > 3)
                alien.shapeSeq = 0;
            else
                alien.shapeSeq++;
        } else {
            alien.moveSeq = ChaosBase.Period * 2 + trigo.RND() % (ChaosBase.Period * 2);
            alien.shapeSeq = 0;
        }
        alien.hitSubLife = alien.life;
        alien.fireSubLife = alien.life;
        nax = (byte) (trigo.RND() % 48);
        nay = (byte) (trigo.RND() % 48);
        alien.ax = (byte) (nax - 24);
        alien.ay = (byte) (nay - 24);
        MakeAlienA(alien);
    }

    private final ChaosBase.ResetProc ResetAlienA_ref = this::ResetAlienA;

    private void ResetFour(ChaosBase.Obj four) {
        // VAR
        int time = 0;

        if (chaosBase.pLife > 20)
            time = 20;
        else
            time = chaosBase.pLife;
        time = 31 - time;
        time -= chaosBase.difficulty;
        if (four.flags.contains(ObjFlags.displayed))
            four.moveSeq = ChaosBase.Period * (trigo.RND() % time + 1);
        else
            four.moveSeq = ChaosBase.Period;
        four.shapeSeq = 0;
        four.hitSubLife = four.life;
        four.fireSubLife = four.life / 8 + 1;
        four.dvx = (short) (trigo.RND() % 2048);
        four.dvx -= 1024;
        four.dvy = (short) (trigo.RND() % 2048);
        four.dvy -= 1024;
        MakeFour(four);
    }

    private final ChaosBase.ResetProc ResetFour_ref = this::ResetFour;

    private void ResetQuad(ChaosBase.Obj quad) {
        // VAR
        int time = 0;

        quad.shapeSeq = 0;
        if (chaosBase.pLife > 20)
            time = 10;
        else
            time = chaosBase.pLife / 2;
        time = 20 - time;
        time -= chaosBase.difficulty / 2;
        if (quad.flags.contains(ObjFlags.displayed))
            quad.moveSeq = ChaosBase.Period / 2 * (trigo.RND() % time + 1);
        else
            quad.moveSeq = ChaosBase.Period;
        quad.fireSubLife = quad.life;
        quad.hitSubLife = quad.life / 8 + 1;
        quad.dvx = (short) (trigo.RND() % 2048);
        quad.dvx -= 1024;
        quad.dvy = (short) (trigo.RND() % 2048);
        quad.dvy -= 1024;
        MakeQuad(quad);
    }

    private final ChaosBase.ResetProc ResetQuad_ref = this::ResetQuad;

    private void ResetCreatorR(ChaosBase.Obj creator) {
        // VAR
        Runtime.IRef<Short> nvz = null;
        int time = 0;

        time = chaosBase.pLife + chaosBase.difficulty;
        if (time > 20)
            time = 20;
        time = (23 - time) * 2;
        creator.moveSeq = ChaosBase.Period * (trigo.RND() % time + 5);
        creator.hitSubLife = creator.life;
        creator.fireSubLife = creator.hitSubLife;
        creator.dvx = 0;
        creator.dvy = 0;
        if (trigo.RND() % 2 == 0)
            nvz = new Runtime.FieldRef<>(creator::getDvx, creator::setDvx);
        else
            nvz = new Runtime.FieldRef<>(creator::getDvy, creator::setDvy);
        if (trigo.RND() % 2 == 0)
            nvz.set((short) 1024);
        else
            nvz.set((short) -1024);
        MakeCreatorR(creator);
    }

    private final ChaosBase.ResetProc ResetCreatorR_ref = this::ResetCreatorR;

    private void ResetCreatorC(ChaosBase.Obj creator) {
        // VAR
        int time = 0;
        short angle = 0;

        time = chaosBase.pLife + chaosBase.difficulty;
        if (time > 20)
            time = 20;
        time = (25 - time) * 2;
        creator.moveSeq = ChaosBase.Period * (trigo.RND() % time + 3);
        creator.stat = 3;
        creator.shapeSeq = trigo.RND() % 15;
        creator.hitSubLife = creator.life;
        creator.fireSubLife = creator.hitSubLife;
        angle = (short) (trigo.RND() % 360);
        creator.dvx = trigo.COS(angle);
        creator.dvy = trigo.SIN(angle);
        MakeCreatorC(creator);
    }

    private final ChaosBase.ResetProc ResetCreatorC_ref = this::ResetCreatorC;

    private void ResetCircle(ChaosBase.Obj circle) {
        if (circle.stat == 0) {
            if (chaosBase.pLife >= 30)
                circle.stat = 0;
            else
                circle.stat = 15 - chaosBase.pLife / 2;
        }
        circle.moveSeq = trigo.RND() % (ChaosBase.Period * 2);
        circle.dvx = (short) (trigo.RND() % 4096);
        circle.dvx -= 2048;
        circle.dvy = (short) (trigo.RND() % 4096);
        circle.dvy -= 2048;
        if (!circle.flags.contains(ObjFlags.displayed)) {
            circle.shapeSeq = 0;
            MakeCircle(circle);
            circle.stat = 0;
        }
    }

    private final ChaosBase.ResetProc ResetCircle_ref = this::ResetCircle;

    private void ResetController(ChaosBase.Obj ctrl) {
        huCnt = 4;
        chaosActions.SetObjXY(ctrl, -ChaosGraphics.PW, -ChaosGraphics.PH);
        ctrl.hitSubLife = 0;
        ctrl.fireSubLife = 0;
        chaosActions.SetObjLoc(ctrl, (short) 0, (short) 0, (short) 0, (short) 0);
        chaosActions.SetObjRect(ctrl, 0, 0, 0, 0);
        ctrl.stat = 1;
        ctrl.moveSeq = 0;
        ctrl.shapeSeq = 0;
        MakeController(ctrl);
    }

    private final ChaosBase.ResetProc ResetController_ref = this::ResetController;

    private void ResetHurryUp(ChaosBase.Obj hu) {
        hu.moveSeq = 0;
        hu.stat = ChaosBase.Period * 3;
        MakeHurryUp(hu);
    }

    private final ChaosBase.ResetProc ResetHurryUp_ref = this::ResetHurryUp;

    private void ResetChief(ChaosBase.Obj chief) {
        // VAR
        int c = 0;

        for (c = 1; c <= 4; c++) {
            chiefData[c - 1].used = false;
        }
        chief.stat = 0;
        chief.shapeSeq = 0;
        chief.hitSubLife = chief.life / 2;
        chief.fireSubLife = chief.life - chief.hitSubLife;
        MakeChief(chief);
    }

    private final ChaosBase.ResetProc ResetChief_ref = this::ResetChief;

    private void ResetABox(ChaosBase.Obj abox) {
        abox.dvx = 0;
        abox.dvy = 0;
        abox.stat = abox.life;
        abox.life = chaosBase.difficulty * 10;
        abox.hitSubLife = 50;
        abox.fireSubLife = 50;
        MakeABox(abox);
    }

    private final ChaosBase.ResetProc ResetABox_ref = this::ResetABox;

    private void ResetNest(ChaosBase.Obj nest) {
        nest.stat = nest.life;
        nest.life = 80 + chaosBase.difficulty * 10;
        nest.hitSubLife = nest.life;
        nest.fireSubLife = nest.life;
        nest.moveSeq = trigo.RND() % (ChaosBase.Period * 10);
        MakeNest(nest);
    }

    private final ChaosBase.ResetProc ResetNest_ref = this::ResetNest;

    private void ResetGrid(ChaosBase.Obj grid) {
        grid.hitSubLife = grid.life;
        grid.fireSubLife = grid.life;
        grid.stat = ChaosBase.Period * (trigo.RND() % 10 + 5);
        grid.dvx = (short) (trigo.RND() % 1024 + 1024);
        grid.ax = 0;
        grid.dvy = (short) (trigo.RND() % 1024 + 1024);
        grid.ay = 0;
        grid.shapeSeq = trigo.RND() % 64 + 12;
        grid.moveSeq = trigo.RND() % 64 + 12;
        MakeGrid(grid);
    }

    private final ChaosBase.ResetProc ResetGrid_ref = this::ResetGrid;

    private void ResetPopUp(ChaosBase.Obj popup) {
        popup.hitSubLife = popup.life;
        popup.fireSubLife = popup.life;
        popup.stat = 2;
        popup.dvx = 0;
        popup.dvy = 0;
        MakePopUp(popup);
    }

    private final ChaosBase.ResetProc ResetPopUp_ref = this::ResetPopUp;

    private void ResetGhost(ChaosBase.Obj ghost) {
        ghost.hitSubLife = 0;
        ghost.fireSubLife = ghost.life;
        ghost.moveSeq = 0;
        MakeGhost(ghost);
    }

    private final ChaosBase.ResetProc ResetGhost_ref = this::ResetGhost;

    private void FireMissileV(ChaosBase.Obj src, short sk) {
        // VAR
        ChaosBase.Obj missile = null;
        int rx = 0;
        int ry = 0;
        int rl = 0;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> dx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> dy = new Runtime.Ref<>((short) 0);
        short nvx = 0;
        short nvy = 0;

        if (chaosBase.sleeper != 0)
            return;
        chaosSounds.SoundEffect(src, missileEffect);
        chaosActions.GetCenter(src, px, py);
        chaosActions.GetCenter(chaosBase.mainPlayer, dx, dy);
        rx = dx.get() - px.get();
        ry = dy.get() - py.get();
        rl = trigo.SQRT(rx * rx + ry * ry);
        if (rl == 0)
            rl = 1;
        nvx = (short) (rx * 2048 / rl);
        nvy = (short) (ry * 2048 / rl);
        missile = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien1, px.get(), py.get(), sk, 8 + sk * 2);
        chaosActions.SetObjVXY(missile, nvx, nvy);
    }

    private void FireMissileA(ChaosBase.Obj src, short ox, short oy, boolean snd) {
        // VAR
        ChaosBase.Obj missile = null;
        int rx = 0;
        int ry = 0;
        int rl = 0;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> dx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> dy = new Runtime.Ref<>((short) 0);
        short nvx = 0;
        short nvy = 0;
        byte nax = 0;
        byte nay = 0;
        short sk = 0;

        if (chaosBase.sleeper != 0)
            return;
        if (snd)
            chaosSounds.SoundEffect(src, missileEffect);
        chaosActions.GetCenter(src, px, py);
        px.inc(ox);
        py.inc(oy);
        chaosActions.GetCenter(chaosBase.mainPlayer, dx, dy);
        rx = dx.get() - px.get();
        ry = dy.get() - py.get();
        rl = trigo.SQRT(rx * rx + ry * ry);
        if (rl == 0)
            rl = 1;
        nvx = chaosBase.mainPlayer.dvx;
        nvy = chaosBase.mainPlayer.dvy;
        nax = (byte) (rx * 64 / rl);
        nay = (byte) (ry * 64 / rl);
        if (chaosBase.water)
            sk = ChaosMissile.mWhite;
        else
            sk = ChaosMissile.mRed;
        missile = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien1, px.get(), py.get(), sk, 10);
        chaosActions.SetObjVXY(missile, nvx, nvy);
        missile.ax = nax;
        missile.ay = nay;
    }

    private void MoveAlienAV(ChaosBase.Obj alien) {
        if (chaosActions.OutOfScreen(alien)) {
            chaosActions.Leave(alien);
            return;
        }
        chaosActions.UpdateXY(alien);
        chaosActions.LimitSpeed(alien, (short) 1024);
        chaosActions.AvoidBounds(alien, (short) 4);
        chaosActions.AvoidBackground(alien, (short) 4);
        chaosActions.Burn(alien);
        alien.flags.add(ObjFlags.displayed);
        if ((alien.moveSeq >= ChaosBase.Period) && (chaosBase.step > alien.moveSeq - ChaosBase.Period)) {
            alien.moveSeq -= chaosBase.step;
            alien.dvx = 0;
            alien.dvy = 0;
            alien.ax = 0;
            alien.ay = 0;
            alien.attr.Make.invoke(alien);
        } else if (chaosBase.step > alien.moveSeq) {
            if (alien.shapeSeq == 0) {
                if (alien.subKind == cAlienV) {
                    FireMissileV(alien, (short) ChaosMissile.mRed);
                } else if (alien.subKind == cQuad) {
                    FireMissileV(alien, (short) ChaosMissile.mBlue);
                } else if (alien.subKind == cAlienA) {
                    FireMissileA(alien, (short) 0, (short) 0, true);
                } else {
                    FireMissileA(alien, (short) -6, (short) -6, false);
                    FireMissileA(alien, (short) -6, (short) 6, false);
                    FireMissileA(alien, (short) 6, (short) -6, false);
                    FireMissileA(alien, (short) 6, (short) 6, true);
                }
            }
            alien.attr.Reset.invoke(alien);
        } else {
            alien.moveSeq -= chaosBase.step;
        }
        chaosActions.PlayerCollision(alien, new Runtime.FieldRef<>(alien::getLife, alien::setLife));
        if (alien.life == 0)
            chaosActions.Die(alien);
    }

    private final ChaosBase.MoveProc MoveAlienAV_ref = this::MoveAlienAV;

    private void MoveCreatorR(ChaosBase.Obj creator) {
        // VAR
        ChaosBase.Obj alien = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        int max = 0;
        int random = 0;
        Anims nKind = Anims.PLAYER;
        short sKind = 0;

        if (chaosActions.OutOfScreen(creator)) {
            chaosActions.Leave(creator);
            return;
        }
        chaosActions.UpdateXY(creator);
        chaosActions.AvoidBounds(creator, (short) 4);
        chaosActions.AvoidBackground(creator, (short) 4);
        chaosActions.Burn(creator);
        if (chaosBase.step > creator.moveSeq) {
            chaosActions.GetCenter(creator, px, py);
            if (chaosBase.difficulty < 4)
                max = chaosBase.difficulty;
            else
                max = 3;
            if (chaosBase.pLife >= 10)
                max++;
            if (chaosBase.pLife >= 20)
                max++;
            random = trigo.RND() % max;
            nKind = Anims.ALIEN1;
            sKind = ChaosAlien.aDbOval;
            if (random >= 1) {
                nKind = Anims.ALIEN2;
                sKind = cAlienV;
            }
            if (random >= 2)
                sKind = cAlienA;
            if (((chaosBase.nbDollar >= 200) || (chaosBase.nbSterling >= 180)) && (trigo.RND() % 2 == 0)) {
                nKind = Anims.ALIEN2;
                sKind = cGhost;
            } else if ((chaosBase.pLife >= 25) && (trigo.RND() % 2 == 0)) {
                nKind = Anims.ALIEN2;
                sKind = cChief;
            }
            if ((chaosBase.difficulty >= 4) && (trigo.RND() % 4 == 0)) {
                nKind = Anims.ALIEN1;
                if (trigo.RND() % 2 == 0)
                    sKind = ChaosAlien.aBumper;
                else
                    sKind = ChaosAlien.aTri;
            } else if ((chaosBase.difficulty >= 8) && (trigo.RND() % 4 == 0)) {
                nKind = Anims.ALIEN2;
                sKind = cCircle;
            } else if ((chaosBase.difficulty >= 8) && (trigo.RND() % 3 == 0)) {
                nKind = Anims.ALIEN2;
                sKind = cNest;
                if ((chaosBase.pLife >= 25) && (chaosBase.nbAnim[Anims.ALIEN3.ordinal()] == 0)) {
                    nKind = Anims.ALIEN3;
                    sKind = 1;
                }
            } else if ((chaosBase.difficulty >= 10) && (chaosBase.pLife >= 10) && (chaosBase.nbAnim[Anims.ALIEN3.ordinal()] == 0)) {
                nKind = Anims.ALIEN3;
                sKind = 0;
            }
            chaosSounds.SoundEffect(creator, hurryupEffect);
            alien = chaosActions.CreateObj(nKind, sKind, px.get(), py.get(), 0, chaosBase.pLife * 3);
            ResetCreatorR(creator);
        } else if (creator.moveSeq < ChaosBase.Period * 3) {
            creator.moveSeq -= chaosBase.step;
            creator.dvx = 0;
            creator.dvy = 0;
        } else {
            if (Math.abs(creator.vx) + Math.abs(creator.vy) > 1024) {
                creator.dvx = creator.vx;
                creator.dvy = creator.vy;
            }
            creator.moveSeq -= chaosBase.step;
        }
        chaosActions.PlayerCollision(creator, new Runtime.FieldRef<>(creator::getLife, creator::setLife));
        if (creator.life == 0)
            chaosActions.Die(creator);
    }

    private final ChaosBase.MoveProc MoveCreatorR_ref = this::MoveCreatorR;

    private void MoveCreatorC(ChaosBase.Obj creator) {
        // VAR
        ChaosBase.Obj alien = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short angle = 0;
        Anims nKind = Anims.PLAYER;
        short sKind = 0;

        if (chaosActions.OutOfScreen(creator)) {
            chaosActions.Leave(creator);
            return;
        }
        chaosActions.UpdateXY(creator);
        chaosActions.AvoidBounds(creator, (short) 4);
        chaosActions.AvoidBackground(creator, (short) 4);
        chaosActions.Burn(creator);
        if (chaosBase.step > creator.moveSeq) {
            chaosSounds.SoundEffect(creator, hurryupEffect);
            chaosActions.GetCenter(creator, px, py);
            nKind = Anims.ALIEN1;
            if ((chaosBase.difficulty >= 6) && (creator.stat == 0) && (trigo.RND() % 3 == 0)) {
                nKind = Anims.ALIEN2;
                sKind = cChief;
            } else if ((chaosBase.stages == 0) && (chaosBase.nbDollar == 200) && (trigo.RND() % 4 == 0)) {
                sKind = ChaosAlien.aTrefle;
            } else if ((chaosBase.difficulty == 10) && (creator.stat == 0) && (trigo.RND() % 2 == 0)) {
                if (trigo.RND() % 4 == 0) {
                    sKind = ChaosAlien.aSquare;
                } else if (trigo.RND() % 2 == 0) {
                    sKind = ChaosAlien.aBig;
                } else {
                    nKind = Anims.ALIEN2;
                    sKind = cAlienBox;
                }
            } else {
                sKind = ChaosAlien.aDiese;
            }
            alien = chaosActions.CreateObj(nKind, sKind, px.get(), py.get(), 0, chaosBase.pLife * 3);
            angle = (short) (creator.stat * 90 + creator.shapeSeq);
            chaosActions.SetObjVXY(alien, (short) (trigo.COS(angle) * 3), (short) (trigo.SIN(angle) * 3));
            if (creator.stat == 0) {
                ResetCreatorC(creator);
            } else {
                creator.moveSeq = ChaosBase.Period / 4;
                creator.stat--;
            }
        } else if (creator.moveSeq < ChaosBase.Period * 3) {
            creator.moveSeq -= chaosBase.step;
            creator.dvx = 0;
            creator.dvy = 0;
        } else {
            if (Math.abs(creator.vx) + Math.abs(creator.vy) > 1024) {
                creator.dvx = creator.vx;
                creator.dvy = creator.vy;
            }
            creator.moveSeq -= chaosBase.step;
            if (creator.moveSeq < ChaosBase.Period * 3)
                MakeCreatorC(creator);
        }
        chaosActions.PlayerCollision(creator, new Runtime.FieldRef<>(creator::getLife, creator::setLife));
        if (creator.life == 0)
            chaosActions.Die(creator);
    }

    private final ChaosBase.MoveProc MoveCreatorC_ref = this::MoveCreatorC;

    private void BoumMissile(ChaosBase.Obj obj, boolean force) {
        // VAR
        ChaosBase.Obj missile = null;
        int c = 0;
        short nvx = 0;
        short nvy = 0;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short angle = 0;
        short sk = 0;

        if (!force && (chaosBase.difficulty < 9))
            return;
        chaosSounds.SoundEffect(obj, circleFireEffect);
        chaosActions.GetCenter(obj, px, py);
        angle = (short) (trigo.RND() % 32);
        for (c = 1; c <= 20; c++) {
            nvx = (short) (trigo.COS(angle) * 3 / 2);
            nvy = (short) (trigo.SIN(angle) * 3 / 2);
            if (obj.subKind == cPopUp)
                sk = (short) (c % 5);
            else
                sk = ChaosMissile.mBlue;
            missile = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien1, px.get(), py.get(), sk, 9);
            chaosActions.SetObjVXY(missile, nvx, nvy);
            angle += 18;
        }
    }

    private void MoveCircle(ChaosBase.Obj circle) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);

        if (chaosActions.OutOfScreen(circle)) {
            chaosActions.Leave(circle);
            return;
        }
        chaosActions.UpdateXY(circle);
        chaosActions.AvoidBounds(circle, (short) 3);
        chaosActions.AvoidBackground(circle, (short) 3);
        chaosActions.Burn(circle);
        circle.flags.add(ObjFlags.displayed);
        if (chaosBase.step >= circle.shapeSeq) {
            if (circle.shapeSeq > 0) {
                circle.life--;
                if (circle.life == 0) {
                    chaosActions.Die(circle);
                    return;
                }
                circle.shapeSeq = 0;
                MakeCircle(circle);
            }
            circle.shapeSeq = 0;
            circle.hitSubLife = 40;
            circle.fireSubLife = 40;
        } else {
            circle.shapeSeq -= chaosBase.step;
        }
        if (chaosBase.step > circle.moveSeq) {
            if (circle.stat <= 1) {
                circle.stat = 0;
                BoumMissile(circle, true);
            } else {
                circle.stat--;
            }
            ResetCircle(circle);
        } else {
            circle.moveSeq -= chaosBase.step;
        }
        hit.set(50);
        chaosActions.PlayerCollision(circle, hit);
    }

    private final ChaosBase.MoveProc MoveCircle_ref = this::MoveCircle;

    private void MoveController(ChaosBase.Obj ctrl) {
        if (chaosBase.step > ctrl.moveSeq)
            ctrl.moveSeq = 0;
        else
            ctrl.moveSeq -= chaosBase.step;
        if (chaosBase.step > ctrl.shapeSeq)
            ctrl.shapeSeq = 0;
        else
            ctrl.shapeSeq -= chaosBase.step;
        if ((ctrl.moveSeq == 0) || (ctrl.shapeSeq == 0))
            MakeController(ctrl);
        if ((chaosBase.nbAnim[Anims.ALIEN3.ordinal()] + chaosBase.nbAnim[Anims.ALIEN2.ordinal()] + chaosBase.nbAnim[Anims.ALIEN1.ordinal()] <= 1) && ((chaosBase.zone == Zone.Chaos) || (chaosBase.zone == Zone.Family)))
            chaosActions.Die(ctrl);
    }

    private final ChaosBase.MoveProc MoveController_ref = this::MoveController;

    private void MoveHurryUp(ChaosBase.Obj hu) {
        // VAR
        ChaosBase.Obj missile = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short nay = 0;

        chaosActions.UpdateXY(hu);
        chaosActions.Burn(hu);
        if (chaosActions.OutOfBounds(hu)) {
            if (hu.stat == 0) {
                hu.stat = 7;
                chaosActions.Die(hu);
                return;
            }
            hu.hitSubLife = 0;
            hu.fireSubLife = 0;
        } else {
            hu.hitSubLife = hu.life;
            hu.fireSubLife = hu.life;
            if (chaosBase.step > hu.moveSeq) {
                chaosSounds.SoundEffect(hu, missileEffect);
                chaosActions.GetCenter(hu, px, py);
                if (py.get() > chaosGraphics.gameHeight / 2)
                    nay = -40;
                else
                    nay = 40;
                missile = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien1, px.get(), py.get(), ChaosMissile.mGreen, 8);
                chaosActions.SetObjAXY(missile, (byte) 0, (byte) nay);
                hu.moveSeq = trigo.RND() % ChaosBase.Period + ChaosBase.Period / 4 - chaosBase.step;
            } else {
                hu.moveSeq -= chaosBase.step;
            }
        }
        if (chaosBase.step > hu.stat)
            hu.stat = 0;
        else
            hu.stat -= chaosBase.step;
        hu.ax = 0;
        hu.ay = 0;
        if (hu.vx > 0) {
            hu.dvx = 800;
            hu.dvy = 0;
        } else if (hu.vx < 0) {
            hu.dvx = -800;
            hu.dvy = 0;
        } else {
            hu.dvx = 0;
            hu.dvy = 800;
        }
        chaosActions.PlayerCollision(hu, new Runtime.FieldRef<>(hu::getLife, hu::setLife));
        if (hu.life == 0)
            chaosActions.Die(hu);
    }

    private final ChaosBase.MoveProc MoveHurryUp_ref = this::MoveHurryUp;

    private void MoveChief(ChaosBase.Obj chief) {
        // VAR
        ChaosBase.Obj _new = null;
        int lx = 0;
        int ly = 0;
        int dl = 0;
        int speed = 0;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> mx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> my = new Runtime.Ref<>((short) 0);
        short dx = 0;
        short dy = 0;
        short nvx = 0;
        int rnd = 0;
        int sKind = 0;
        int nStat = 0;
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);
        Anims nKind = Anims.PLAYER;
        boolean bg = false;

        chaosActions.UpdateXY(chief);
        chaosActions.Burn(chief);
        nvx = (short) (5 + chaosBase.difficulty);
        speed = nvx;
        chaosActions.LimitSpeed(chief, (short) (nvx * 256));
        chaosActions.AvoidBounds(chief, (short) 0);
        bg = chaosActions.InBackground(chief);
        if (bg)
            chaosActions.AvoidBackground(chief, (short) 0);
        if (chief.stat == 0) {
            chaosActions.GetCenter(chief, mx, my);
            if (chaosBase.step > chief.shapeSeq) {
                chief.shapeSeq += ChaosBase.Period * (1 + trigo.RND() % 8);
                rnd = trigo.RND() % 3;
                if ((chaosBase.stages == 0) && (trigo.RND() % 4 == 0))
                    BoumMissile(chief, true);
                if (rnd == 0) {
                    FireMissileV(chief, (short) (trigo.RND() % 4));
                } else if (rnd == 1) {
                    FireMissileA(chief, (short) 0, (short) 0, true);
                } else {
                    nStat = trigo.RND() % 2;
                    if (nStat == 0)
                        nvx = 2700;
                    else
                        nvx = -2700;
                    if (rnd == 2) {
                        nKind = Anims.ALIEN2;
                        sKind = cMissile;
                    } else {
                        nKind = Anims.ALIEN1;
                        sKind = ChaosAlien.aFlame;
                        my.inc(16);
                    }
                    chaosSounds.SoundEffect(chief, hurryupEffect);
                    _new = chaosActions.CreateObj(nKind, (short) sKind, mx.get(), my.get(), nStat, chaosBase.pLife);
                    chaosActions.SetObjVXY(_new, nvx, (short) 0);
                }
            }
            if (!chaosActions.OutOfScreen(chief))
                chief.shapeSeq -= chaosBase.step;
            chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
            dx = (short) (px.get() - mx.get());
            dy = (short) (py.get() - my.get());
            lx = dx;
            ly = dy;
            dl = trigo.SQRT(lx * lx + ly * ly);
            if (dl != 0) {
                chief.dvx = (short) (lx * 128 / dl * speed);
                chief.dvy = (short) (ly * 128 / dl * speed);
            }
            if (dl < 56) {
                chief.dvx = (short) -chief.dvx;
                chief.dvy = (short) -chief.dvy;
            } else if (dl < 64) {
                chief.dvx = 0;
                chief.dvy = 0;
            }
            if (bg && (Math.abs(chief.vx) < 400) && (Math.abs(chief.vy) < 400)) {
                chief.moveSeq = 4;
                while ((chief.moveSeq > 0) && chiefData[chief.moveSeq - 1].used) {
                    chief.moveSeq--;
                }
                if (chief.moveSeq != 0) {
                    { // WITH
                        ChiefData _chiefData = chiefData[chief.moveSeq - 1];
                        if (Math.abs(dx) > Math.abs(dy)) {
                            if (dx > 0)
                                _chiefData.angle = 0;
                            else
                                _chiefData.angle = 180;
                            _chiefData.dir = trigo.SGN(dx) != trigo.SGN(dy);
                        } else {
                            if (dy > 0)
                                _chiefData.angle = 90;
                            else
                                _chiefData.angle = 270;
                            _chiefData.dir = trigo.SGN(dx) == trigo.SGN(dy);
                        }
                        if (_chiefData.dir)
                            _chiefData.angle = (short) ((_chiefData.angle - 90) % 360);
                        else
                            _chiefData.angle = (short) ((_chiefData.angle + 90) % 360);
                        _chiefData.dstx = (short) (mx.get() / ChaosGraphics.BW);
                        _chiefData.dsty = (short) (my.get() / ChaosGraphics.BH);
                        _chiefData.crx = _chiefData.dstx;
                        _chiefData.cry = _chiefData.dsty;
                        _chiefData.tox = _chiefData.crx;
                        _chiefData.toy = _chiefData.cry;
                        px.set((short) (px.get() / ChaosGraphics.BW));
                        py.set((short) (py.get() / ChaosGraphics.BH));
                        while (true) {
                            if ((_chiefData.dstx == px.get()) && (_chiefData.dsty == py.get()))
                                break;
                            if (Math.abs(px.get() - _chiefData.dstx) > Math.abs(py.get() - _chiefData.dsty)) {
                                if (px.get() > _chiefData.dstx)
                                    _chiefData.dstx++;
                                else
                                    _chiefData.dstx--;
                            } else {
                                if (py.get() > _chiefData.dsty)
                                    _chiefData.dsty++;
                                else
                                    _chiefData.dsty--;
                            }
                            if (_chiefData.dstx < 0)
                                _chiefData.dstx = 0;
                            if (_chiefData.dsty < 0)
                                _chiefData.dsty = 0;
                            chief.stat = 1;
                            _chiefData.used = true;
                            if (chaosGraphics.castle[_chiefData.dsty][_chiefData.dstx] < ChaosGraphics.NbBackground)
                                break;
                        }
                    }
                }
            }
        } else {
            { // WITH
                ChiefData _chiefData = chiefData[chief.moveSeq - 1];
                if (chaosBase.gameStat != GameStat.Playing)
                    _chiefData.used = false;
                chaosActions.GetCenter(chief, mx, my);
                px.set((short) (_chiefData.tox * ChaosGraphics.BW + ChaosGraphics.BW / 2));
                py.set((short) (_chiefData.toy * ChaosGraphics.BH + ChaosGraphics.BH / 2));
                lx = px.get() - mx.get();
                ly = py.get() - my.get();
                if ((Math.abs(lx) >= 64) || (Math.abs(ly) >= 64)) {
                    chief.stat = 0;
                    _chiefData.used = false;
                    dl = 0;
                } else {
                    dl = trigo.SQRT(lx * lx + ly * ly);
                }
                if (dl != 0) {
                    chief.dvx = (short) (lx * 128 / dl * speed);
                    chief.dvy = (short) (ly * 128 / dl * speed);
                }
                if (Math.abs(dl) <= 16) {
                    _chiefData.crx = _chiefData.tox;
                    _chiefData.cry = _chiefData.toy;
                    rnd = 6;
                    if (_chiefData.dir)
                        _chiefData.angle = (short) ((_chiefData.angle + 90) % 360);
                    else
                        _chiefData.angle = (short) ((_chiefData.angle - 90) % 360);
                    while (true) {
                        _chiefData.tox = (short) (_chiefData.crx + trigo.COS(_chiefData.angle) / 1024);
                        _chiefData.toy = (short) (_chiefData.cry + trigo.SIN(_chiefData.angle) / 1024);
                        if ((_chiefData.tox == 0) || (_chiefData.toy == 0) || (_chiefData.tox == chaosGraphics.castleWidth - 1) || (_chiefData.toy == chaosGraphics.castleHeight - 1))
                            _chiefData.dir = !_chiefData.dir;
                        if ((_chiefData.tox >= 0) && (_chiefData.toy >= 0) && (_chiefData.tox < chaosGraphics.castleWidth) && (_chiefData.toy < chaosGraphics.castleHeight) && (chaosGraphics.castle[_chiefData.toy][_chiefData.tox] < ChaosGraphics.NbBackground))
                            break;
                        rnd--;
                        if (rnd == 0) {
                            chief.stat = 0;
                            _chiefData.used = false;
                            break;
                        }
                        if (_chiefData.dir)
                            _chiefData.angle = (short) ((_chiefData.angle - 90) % 360);
                        else
                            _chiefData.angle = (short) ((_chiefData.angle + 90) % 360);
                    }
                    if ((_chiefData.crx == _chiefData.dstx) && (_chiefData.cry == _chiefData.dsty)) {
                        chief.stat = 0;
                        _chiefData.used = false;
                    }
                }
            }
        }
        dx = chief.dvx;
        dy = chief.dvy;
        chaosActions.AvoidAnims(chief, EnumSet.of(Anims.WEAPON));
        if (((dx != chief.dvx) || (dy != chief.dvy)) && (chief.stat == 1)) {
            chief.stat = 0;
            chiefData[chief.moveSeq - 1].used = false;
        }
        chaosActions.Gravity(chief, EnumSet.of(Anims.MISSILE));
        hit.set(chief.life / 2);
        fire.set(chief.life - hit.get());
        chaosActions.DoCollision(chief, EnumSet.of(Anims.PLAYER, Anims.ALIEN1), chaosActions.Aie_ref, hit, fire);
        if (chief.life == 0)
            chaosActions.Die(chief);
    }

    private final ChaosBase.MoveProc MoveChief_ref = this::MoveChief;

    private void MoveABox(ChaosBase.Obj abox) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);

        if (chaosActions.OutOfScreen(abox)) {
            chaosActions.Leave(abox);
            return;
        }
        chaosActions.UpdateXY(abox);
        chaosActions.AvoidBounds(abox, (short) 3);
        chaosActions.AvoidBackground(abox, (short) 3);
        chaosActions.Burn(abox);
        hit.set(5);
        chaosActions.PlayerCollision(abox, hit);
    }

    private final ChaosBase.MoveProc MoveABox_ref = this::MoveABox;

    private void MoveNest(ChaosBase.Obj nest) {
        // VAR
        ChaosBase.Obj _new = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short nvx = 0;
        int time = 0;
        int nstat = 0;
        int nlife = 0;
        int rnd = 0;
        Anims nkind = Anims.PLAYER;
        short nsk = 0;

        if (chaosActions.OutOfScreen(nest)) {
            chaosActions.Leave(nest);
            return;
        }
        nest.vx = 0;
        nest.vy = 0;
        nest.dvx = 0;
        nest.dvy = 0;
        chaosActions.UpdateXY(nest);
        chaosActions.Burn(nest);
        if (chaosBase.step > nest.moveSeq) {
            nlife = chaosBase.pLife * 2;
            if (nest.stat == 1) {
                nkind = Anims.ALIEN2;
                nsk = cMissile;
                nstat = trigo.RND() % 2;
            } else {
                rnd = trigo.RND() % 64;
                if ((rnd == 0) && (chaosBase.stages == 0)) {
                    nkind = Anims.ALIEN2;
                    nsk = cHurryUp;
                } else if (rnd < 10) {
                    if ((chaosBase.difficulty >= 7) && ((rnd % 2) != 0)) {
                        nkind = Anims.ALIEN1;
                        nsk = ChaosAlien.aBig;
                    } else {
                        nkind = Anims.ALIEN2;
                        if (trigo.RND() % 2 == 0)
                            nsk = cFour;
                        else
                            nsk = cQuad;
                    }
                } else {
                    nkind = Anims.ALIEN1;
                    if ((chaosBase.difficulty >= 3) && ((rnd % 2) != 0)) {
                        nsk = ChaosAlien.aTri;
                    } else {
                        nsk = ChaosAlien.aColor;
                        nlife = chaosBase.difficulty * 6 + trigo.RND() % 32;
                    }
                }
            }
            chaosActions.GetCenter(nest, px, py);
            _new = chaosActions.CreateObj(nkind, nsk, px.get(), py.get(), nstat, nlife);
            if (nest.stat == 1) {
                if (nstat == 0)
                    nvx = 3000;
                else
                    nvx = -3000;
                chaosActions.SetObjVXY(_new, nvx, (short) 0);
            }
            chaosSounds.SoundEffect(nest, hurryupEffect);
            if (chaosBase.pLife > 20)
                time = 10;
            else
                time = chaosBase.pLife / 2;
            time = 15 - time - chaosBase.difficulty / 3;
            nest.moveSeq += ChaosBase.Period * (trigo.RND() % time + 1);
        }
        nest.moveSeq -= chaosBase.step;
    }

    private final ChaosBase.MoveProc MoveNest_ref = this::MoveNest;

    private void MoveGrid(ChaosBase.Obj grid) {
        // VAR
        short ovx = 0;
        short ovy = 0;

        if (chaosActions.OutOfScreen(grid)) {
            chaosActions.Leave(grid);
            return;
        }
        ovx = (short) Math.abs(grid.dvx);
        ovy = (short) Math.abs(grid.dvy);
        chaosActions.UpdateXY(grid);
        chaosActions.AvoidBounds(grid, (short) 4);
        chaosActions.AvoidBackground(grid, (short) 4);
        grid.dvx = ovx;
        grid.dvy = ovy;
        if (grid.vx >= grid.dvx) {
            grid.ax = (byte) grid.shapeSeq;
            grid.ax = (byte) -grid.ax;
        } else if (-grid.vx >= grid.dvx) {
            grid.ax = (byte) grid.shapeSeq;
        }
        if (grid.vy >= grid.dvy) {
            grid.ay = (byte) grid.moveSeq;
            grid.ay = (byte) -grid.ay;
        } else if (-grid.vy >= grid.dvy) {
            grid.ay = (byte) grid.moveSeq;
        }
        if (chaosBase.step > grid.stat)
            ResetGrid(grid);
        grid.stat -= chaosBase.step;
        chaosActions.PlayerCollision(grid, new Runtime.FieldRef<>(grid::getLife, grid::setLife));
        if (grid.life == 0)
            chaosActions.Die(grid);
    }

    private final ChaosBase.MoveProc MoveGrid_ref = this::MoveGrid;

    private void MoveMissile(ChaosBase.Obj missile) {
        if (chaosActions.OutOfScreen(missile)) {
            chaosActions.Leave(missile);
            return;
        }
        chaosActions.UpdateXY(missile);
        if (chaosActions.OutOfBounds(missile) || chaosActions.InBackground(missile))
            chaosActions.Die(missile);
    }

    private final ChaosBase.MoveProc MoveMissile_ref = this::MoveMissile;

    private void MovePopUp(ChaosBase.Obj popup) {
        // VAR
        ChaosBase.Obj missile = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> mx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> my = new Runtime.Ref<>((short) 0);
        short nvx = 0;
        short nvy = 0;
        short angle = 0;

        if (chaosActions.OutOfScreen(popup)) {
            chaosActions.Leave(popup);
            return;
        }
        chaosActions.UpdateXY(popup);
        chaosActions.AvoidBackground(popup, (short) 1);
        chaosActions.AvoidBounds(popup, (short) 1);
        popup.dvx = 0;
        popup.dvy = 0;
        if (popup.stat == 2) {
            chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
            chaosActions.GetCenter(popup, mx, my);
            if ((Math.abs(px.get() - mx.get()) < 130) && (Math.abs(py.get() - my.get()) < 130)) {
                popup.stat = 1;
                chaosSounds.SoundEffect(popup, popupEffect);
                popup.moveSeq = ChaosBase.Period * 3 / 2;
                MakePopUp(popup);
            }
        } else {
            if (chaosBase.step > popup.moveSeq) {
                if (popup.stat == 1) {
                    chaosActions.GetCenter(popup, px, py);
                    chaosSounds.SoundEffect(popup, missileEffect);
                    for (angle = 0; angle <= 270; angle += 90) {
                        nvx = (short) (trigo.COS(angle) * 2);
                        nvy = (short) (trigo.SIN(angle) * 2);
                        missile = chaosActions.CreateObj(Anims.MISSILE, (short) ChaosMissile.mAlien1, px.get(), py.get(), ChaosMissile.mRed, 10);
                        chaosActions.SetObjVXY(missile, nvx, nvy);
                    }
                    popup.stat = 0;
                    popup.moveSeq += ChaosBase.Period * 3 / 2;
                } else {
                    chaosActions.Die(popup);
                    return;
                }
            }
            popup.moveSeq -= chaosBase.step;
        }
    }

    private final ChaosBase.MoveProc MovePopUp_ref = this::MovePopUp;

    private void RemMoney(ChaosBase.Obj player, ChaosBase.Obj ghost, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        // VAR
        byte md = 0;
        byte ms = 0;

        ghost.moveSeq = ChaosBase.Period / 2;
        if (chaosBase.nbDollar > 0)
            md = -1;
        else
            md = 0;
        if (chaosBase.nbSterling > 0)
            ms = -1;
        else
            ms = 0;
        if ((md != 0) || (ms != 0))
            chaosPlayer.AddMoney(player, md, ms);
    }

    private final ChaosBase.AieProc RemMoney_ref = this::RemMoney;

    private void MoveGhost(ChaosBase.Obj ghost) {
        // VAR
        Runtime.Ref<Short> mx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> my = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short speed = 0;

        if (chaosActions.OutOfScreen(ghost)) {
            chaosActions.Leave(ghost);
            return;
        }
        chaosActions.UpdateXY(ghost);
        chaosActions.AvoidBackground(ghost, (short) 0);
        chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
        chaosActions.GetCenter(ghost, mx, my);
        speed = (short) (800 + chaosBase.difficulty * 100);
        ghost.dvx = (short) (trigo.SGN((short) (px.get() - mx.get())) * speed);
        ghost.dvy = (short) (trigo.SGN((short) (py.get() - my.get())) * speed);
        if (ghost.moveSeq == 0)
            chaosActions.DoCollision(ghost, EnumSet.of(Anims.PLAYER), RemMoney_ref, new Runtime.FieldRef<>(ghost::getHitSubLife, ghost::setHitSubLife), new Runtime.FieldRef<>(ghost::getFireSubLife, ghost::setFireSubLife));
        else if (chaosBase.step > ghost.moveSeq)
            ghost.moveSeq = 0;
        else
            ghost.moveSeq -= chaosBase.step;
    }

    private final ChaosBase.MoveProc MoveGhost_ref = this::MoveGhost;

    private void AieAlienAV(ChaosBase.Obj alien, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(alien, hit, fire);
        if (alien.life > 0)
            chaosSounds.SoundEffect(alien, aieAlienEffect);
    }

    private final ChaosBase.AieProc AieAlienAV_ref = this::AieAlienAV;

    private void AieCreator(ChaosBase.Obj creator, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(creator, hit, fire);
        chaosSounds.SoundEffect(creator, aieCreatorEffect);
    }

    private final ChaosBase.AieProc AieCreator_ref = this::AieCreator;

    private void AieCircle(ChaosBase.Obj circle, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(circle, hit, fire);
        chaosSounds.SoundEffect(circle, aieCircleEffect);
        circle.shapeSeq = ChaosBase.Period * 2;
        MakeCircle(circle);
        if ((circle.life == 0) && (chaosBase.zone == Zone.Chaos))
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Get the money"), String.class), (short) ChaosActions.moneyPos, (short) 2);
        circle.life++;
        circle.hitSubLife = 0;
        circle.fireSubLife = 0;
    }

    private final ChaosBase.AieProc AieCircle_ref = this::AieCircle;

    private void AieHurryUp(ChaosBase.Obj hu, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(hu, hit, fire);
        chaosSounds.SoundEffect(hu, aieHUEffect);
    }

    private final ChaosBase.AieProc AieHurryUp_ref = this::AieHurryUp;

    private void AieChief(ChaosBase.Obj chief, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(chief, hit, fire);
        chaosSounds.SoundEffect(chief, aieChiefEffect);
    }

    private final ChaosBase.AieProc AieChief_ref = this::AieChief;

    private void AieABox(ChaosBase.Obj abox, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(abox, hit, fire);
        chaosSounds.SoundEffect(abox, aieCreatorEffect);
    }

    private final ChaosBase.AieProc AieABox_ref = this::AieABox;

    private void AieNest(ChaosBase.Obj nest, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosActions.DecLife(nest, hit, fire);
        chaosSounds.SoundEffect(nest, aieCreatorEffect);
    }

    private final ChaosBase.AieProc AieNest_ref = this::AieNest;

    private void AieMissile(ChaosBase.Obj missile, ChaosBase.Obj src, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        missile.life = 0;
    }

    private final ChaosBase.AieProc AieMissile_ref = this::AieMissile;

    private void AieGrid(ChaosBase.Obj grid, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        ResetGrid(grid);
        chaosSounds.SoundEffect(grid, aieGridEffect);
        chaosActions.DecLife(grid, hit, fire);
    }

    private final ChaosBase.AieProc AieGrid_ref = this::AieGrid;

    private void AiePopUp(ChaosBase.Obj popup, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosSounds.SoundEffect(popup, aieAlienEffect);
        chaosActions.DecLife(popup, hit, fire);
    }

    private final ChaosBase.AieProc AiePopUp_ref = this::AiePopUp;

    private void AieGhost(ChaosBase.Obj ghost, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        chaosSounds.SoundEffect(ghost, aieGhostEffect);
        chaosActions.DecLife(ghost, hit, fire);
    }

    private final ChaosBase.AieProc AieGhost_ref = this::AieGhost;

    private void DieAlienV(ChaosBase.Obj alien) {
        chaosBase.addpt++;
        chaosSounds.SoundEffect(alien, dieAlienVEffect);
        BoumMissile(alien, false);
        if (!alien.flags.contains(ObjFlags.nested))
            chaosBonus.BoumMoney(alien, EnumSet.of(Moneys.m1, Moneys.m2), 2, 1);
    }

    private final ChaosBase.DieProc DieAlienV_ref = this::DieAlienV;

    private void DieAlienA(ChaosBase.Obj alien) {
        chaosBase.addpt++;
        chaosSounds.SoundEffect(alien, dieAlienAEffect);
        BoumMissile(alien, false);
        if (!alien.flags.contains(ObjFlags.nested))
            chaosBonus.BoumMoney(alien, EnumSet.of(Moneys.m2), 1, 2);
    }

    private final ChaosBase.DieProc DieAlienA_ref = this::DieAlienA;

    private void DieFour(ChaosBase.Obj four) {
        BoumMissile(four, false);
        if (four.subKind == cQuad)
            chaosBonus.BoumMoney(four, EnumSet.of(Moneys.st), 1, 1);
        else
            chaosBase.addpt++;
        chaosSounds.SoundEffect(four, dieFourEffect);
    }

    private final ChaosBase.DieProc DieFour_ref = this::DieFour;

    private void DieCreatorR(ChaosBase.Obj creator) {
        // VAR
        ChaosBase.Obj diese = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short angle = 0;
        short cnt = 0;
        short c = 0;
        short sKind = 0;

        chaosBase.addpt += chaosBase.pLife;
        if (chaosBase.difficulty >= 5) {
            if (chaosBase.difficulty >= 7)
                cnt = 5;
            else
                cnt = 3;
            chaosActions.GetCenter(creator, px, py);
            angle = (short) (trigo.RND() % 360);
            c = cnt;
            while (c > 0) {
                if ((chaosBase.difficulty == 10) && (trigo.RND() % 4 == 0))
                    sKind = ChaosAlien.aFlame;
                else
                    sKind = ChaosAlien.aDiese;
                diese = chaosActions.CreateObj(Anims.ALIEN1, sKind, px.get(), py.get(), 0, chaosBase.pLife * 3);
                chaosActions.SetObjVXY(diese, (short) (trigo.COS(angle) * 3), (short) (trigo.SIN(angle) * 3));
                angle += 360 / cnt;
                c--;
            }
        }
        if ((chaosBase.difficulty < 7) && (chaosBase.zone == Zone.Chaos))
            chaosBonus.BoumMoney(creator, EnumSet.of(Moneys.m1, Moneys.m2, Moneys.m5), 3, 5);
        chaosSounds.SoundEffect(creator, dieCreatorREffect);
    }

    private final ChaosBase.DieProc DieCreatorR_ref = this::DieCreatorR;

    private void DieCreatorC(ChaosBase.Obj creator) {
        chaosSounds.SoundEffect(creator, dieCreatorCEffect);
        BoumMissile(creator, false);
        chaosBase.addpt += chaosBase.pLife;
    }

    private final ChaosBase.DieProc DieCreatorC_ref = this::DieCreatorC;

    private void DieCircle(ChaosBase.Obj circle) {
        // VAR
        ChaosBase.Obj chief = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short angle = 0;
        short cnt = 0;
        short c = 0;

        if (chaosBase.difficulty >= 9) {
            if (chaosBase.difficulty == 9)
                cnt = 2;
            else
                cnt = 3;
            angle = (short) (trigo.RND() % 360);
            c = cnt;
            chaosActions.GetCenter(circle, px, py);
            while (c > 0) {
                chief = chaosActions.CreateObj(Anims.ALIEN2, (short) cChief, px.get(), py.get(), 0, chaosBase.pLife * 4);
                chaosActions.SetObjVXY(chief, (short) (trigo.COS(angle) * 4), (short) (trigo.SIN(angle) * 4));
                angle += 360 / cnt;
                c--;
            }
        }
        chaosBase.addpt += 10;
        chaosSounds.SoundEffect(circle, dieCircleEffect);
        if (chaosBase.zone == Zone.Chaos)
            chaosBonus.BoumMoney(circle, EnumSet.of(Moneys.m5), 1, 10 - chaosBase.difficulty);
    }

    private final ChaosBase.DieProc DieCircle_ref = this::DieCircle;

    private void DieHurryUp(ChaosBase.Obj hu) {
        if (hu.stat != 7)
            chaosSounds.SoundEffect(hu, dieHUEffect);
    }

    private final ChaosBase.DieProc DieHurryUp_ref = this::DieHurryUp;

    private void DieChief(ChaosBase.Obj chief) {
        if (chief.stat == 1)
            chiefData[chief.moveSeq - 1].used = false;
        chaosSounds.SoundEffect(chief, dieCircleEffect);
        chaosBase.addpt += 8;
        BoumMissile(chief, false);
        if ((chaosBase.zone == Zone.Castle) || (chaosBase.zone == Zone.Chaos)) {
            if (chief.attr.nbObj == 1)
                chaosBonus.BoumMoney(chief, EnumSet.of(Moneys.m10), 1, 5 - (chaosBase.difficulty + 5) / 3);
        }
    }

    private final ChaosBase.DieProc DieChief_ref = this::DieChief;

    private void DieABox(ChaosBase.Obj abox) {
        // VAR
        ChaosBase.Obj _new = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        int nstat = 0;
        int nlife = 0;
        int rnd = 0;
        int cnt = 0;
        Anims nkind = Anims.PLAYER;
        short nsk = 0;

        chaosSounds.SoundEffect(abox, dieABEffect);
        for (cnt = 1; cnt <= 5 + chaosBase.difficulty / 3; cnt++) {
            rnd = trigo.RND() % 64;
            nlife = chaosBase.pLife;
            nkind = Anims.ALIEN1;
            nstat = 0;
            if (abox.stat == 0)
                nsk = ChaosAlien.aStar;
            else
                nsk = ChaosAlien.aBubble;
            if ((chaosBase.difficulty >= 8) && (rnd < 10)) {
                nsk = ChaosAlien.aKamikaze;
                nstat = 1;
            }
            if ((chaosBase.difficulty >= 10) && (rnd >= 32) && (cnt == 0)) {
                nkind = Anims.ALIEN2;
                nsk = cCircle;
                nstat = 0;
            }
            if ((chaosBase.difficulty >= 5) && (rnd == 0))
                nsk = ChaosAlien.aHospital;
            chaosActions.GetCenter(abox, px, py);
            _new = chaosActions.CreateObj(nkind, nsk, px.get(), py.get(), nstat, nlife);
        }
    }

    private final ChaosBase.DieProc DieABox_ref = this::DieABox;

    private void DieNest(ChaosBase.Obj nest) {
        // VAR
        ChaosBase.Obj bonus = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        int bullets = 0;
        Weapon w = Weapon.GUN;

        bullets = 0;
        for (int _w = 0; _w < Weapon.values().length; _w++) {
            w = Weapon.values()[_w];
            bullets += chaosBase.weaponAttr[w.ordinal()].nbBullet;
        }
        if (bullets < 130 + trigo.RND() % 256) {
            chaosActions.GetCenter(nest, px, py);
            bonus = chaosActions.CreateObj(Anims.BONUS, (short) ChaosBonus.TimedBonus, px.get(), py.get(), ChaosBonus.tbBullet, 1);
        }
        chaosSounds.SoundEffect(nest, dieNestEffect);
    }

    private final ChaosBase.DieProc DieNest_ref = this::DieNest;

    private void DieGrid(ChaosBase.Obj grid) {
        chaosBonus.BoumMoney(grid, EnumSet.of(Moneys.st), 1, 2);
        BoumMissile(grid, true);
    }

    private final ChaosBase.DieProc DieGrid_ref = this::DieGrid;

    private void DieMissile(ChaosBase.Obj missile) {
        // VAR
        ChaosBase.Obj _new = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        int nstat = 0;
        int nlife = 0;
        int rnd = 0;
        Anims nkind = Anims.PLAYER;
        short nsk = 0;

        rnd = trigo.RND() % 64;
        nstat = 0;
        nlife = chaosBase.difficulty * 6 + trigo.RND() % 32;
        nkind = Anims.ALIEN1;
        nsk = ChaosAlien.aColor;
        if ((chaosBase.difficulty >= 6) && (rnd < 25)) {
            nkind = Anims.ALIEN2;
            nsk = cGrid;
            nlife = chaosBase.pLife * 2;
        } else if ((chaosBase.difficulty >= 7) && (rnd < 50)) {
            nkind = Anims.ALIEN2;
            nsk = cCreatorR;
            nlife = (chaosBase.pLife + chaosBase.difficulty) * 4;
        } else if ((chaosBase.difficulty >= 8) && (rnd > 50)) {
            nsk = ChaosAlien.aSquare;
            nlife = chaosBase.pLife;
        }
        if ((chaosBase.difficulty >= 10) && (rnd > 65 - chaosBase.pLife)) {
            nlife = (chaosBase.pLife + chaosBase.difficulty) * 3;
            nkind = Anims.ALIEN2;
            nstat = 1;
            if (rnd == 64)
                nsk = cHurryUp;
            else
                nsk = cCreatorC;
        }
        chaosActions.GetCenter(missile, px, py);
        _new = chaosActions.CreateObj(nkind, nsk, px.get(), py.get(), nstat, nlife);
    }

    private final ChaosBase.DieProc DieMissile_ref = this::DieMissile;

    private void DiePopUp(ChaosBase.Obj popup) {
        BoumMissile(popup, false);
        chaosSounds.SoundEffect(popup, diePopupEffect);
    }

    private final ChaosBase.DieProc DiePopUp_ref = this::DiePopUp;

    private void DieGhost(ChaosBase.Obj ghost) {
        chaosSounds.SoundEffect(ghost, dieGhostEffect);
    }

    private final ChaosBase.DieProc DieGhost_ref = this::DieGhost;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;
        short c = 0;
        short v = 0;
        SoundList snd = SoundList.aCrash;

        chaosSounds.SetEffect(hurryupEffect[0], chaosSounds.soundList[SoundList.sHurryUp.ordinal()], 0, 0, (short) 200, (short) 1);
        chaosSounds.SetEffect(missileEffect[0], chaosSounds.soundList[SoundList.sMissile.ordinal()], 0, 0, (short) 110, (short) 4);
        chaosSounds.SetEffect(aieCircleEffect[0], chaosSounds.soundList[SoundList.sGong.ordinal()], 0, 16726, (short) 200, (short) 7);
        chaosSounds.SetEffect(dieCircleEffect[0], chaosSounds.soundList[SoundList.sCasserole.ordinal()], 0, 0, (short) 250, (short) 9);
        chaosSounds.SetEffect(aieAlienEffect[0], chaosSounds.soundList[SoundList.sCymbale.ordinal()], 0, 0, (short) 140, (short) 1);
        chaosSounds.SetEffect(aieHUEffect[0], chaosSounds.soundList[SoundList.sHurryUp.ordinal()], 0, 16726, (short) 180, (short) 1);
        chaosSounds.SetEffect(dieHUEffect[0], chaosSounds.soundList[SoundList.sCannon.ordinal()], 4500, 0, (short) 170, (short) 4);
        chaosSounds.SetEffect(aieCreatorEffect[0], chaosSounds.soundList[SoundList.sHHat.ordinal()], 0, 4181, (short) 180, (short) 3);
        chaosSounds.SetEffect(aieChiefEffect[0], chaosSounds.soundList[SoundList.sPoubelle.ordinal()], 0, 0, (short) 220, (short) 5);
        chaosSounds.SetEffect(dieAlienVEffect[0], chaosSounds.soundList[SoundList.aCrash.ordinal()], 0, 0, (short) 180, (short) 4);
        chaosSounds.SetEffect(dieAlienVEffect[1], chaosSounds.soundList[SoundList.wCrash.ordinal()], 582, 0, (short) 180, (short) 3);
        chaosSounds.SetEffect(dieAlienAEffect[0], chaosSounds.soundList[SoundList.aCrash.ordinal()], 0, 8860, (short) 180, (short) 4);
        chaosSounds.SetEffect(dieAlienAEffect[1], chaosSounds.soundList[SoundList.wCrash.ordinal()], 582, 7451, (short) 180, (short) 3);
        for (c = 2; c <= 16; c++) {
            v = (short) ((17 - c) * 11);
            chaosSounds.SetEffect(dieAlienAEffect[c], chaosSounds.nulSound, 582, 8860 - (c % 2) * 1409, v, (short) 3);
            v = (short) (v * (c % 2));
            chaosSounds.SetEffect(dieAlienVEffect[c], chaosSounds.nulSound, 582, 0, v, (short) 3);
        }
        for (c = 0; c <= 23; c++) {
            v = (short) (Math.abs(c % 4 - 2) + 1);
            chaosSounds.SetEffect(dieCreatorREffect[c], chaosSounds.soundList[SoundList.wWhite.ordinal()], 700, 8363 + v * 2788, (short) ((24 - c) * v * 7 / 2), (short) 5);
            chaosSounds.SetEffect(dieNestEffect[c], chaosSounds.soundList[SoundList.wNoise.ordinal()], 700, 8363 + v * 2788, (short) ((24 - c) * v * 3), (short) 5);
        }
        v = 0;
        for (c = 0; c <= 7; c++) {
            chaosSounds.SetEffect(dieCreatorCEffect[c], chaosSounds.soundList[SoundList.wWhite.ordinal()], 0, 5575 + 995 * v, (short) ((8 - c) * 24), (short) 5);
            v = (short) ((v * 9 + 5) % 8);
        }
        chaosSounds.SetEffect(dieFourEffect[0], chaosSounds.soundList[SoundList.aCrash.ordinal()], 0, 0, (short) 192, (short) 4);
        for (c = 1; c <= 8; c++) {
            chaosSounds.SetEffect(dieFourEffect[c], chaosSounds.soundList[SoundList.wCrash.ordinal()], 1500, 8363, (short) ((9 - c) * 24), (short) 4);
        }
        for (c = 0; c <= 11; c++) {
            v = (short) (c % 3);
            if (v == 0)
                snd = SoundList.wNoise;
            else if (v == 1)
                snd = SoundList.wWhite;
            else
                snd = SoundList.wCrash;
            chaosSounds.SetEffect(dieABEffect[c], chaosSounds.soundList[snd.ordinal()], 836, 8363, (short) ((12 - c) * 16), (short) 5);
        }
        chaosSounds.SetEffect(circleFireEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 5500, 0, (short) 190, (short) 6);
        chaosSounds.SetEffect(popupEffect[0], chaosSounds.soundList[SoundList.wVoice.ordinal()], 929, 8363, (short) 100, (short) 4);
        chaosSounds.SetEffect(popupEffect[1], chaosSounds.soundList[SoundList.wVoice.ordinal()], 1394, 12544, (short) 100, (short) 4);
        chaosSounds.SetEffect(popupEffect[2], chaosSounds.soundList[SoundList.wVoice.ordinal()], 1162, 10454, (short) 100, (short) 4);
        chaosSounds.SetEffect(diePopupEffect[0], chaosSounds.soundList[SoundList.sCymbale.ordinal()], 0, 4181, (short) 100, (short) 4);
        chaosSounds.SetEffect(aieGridEffect[0], chaosSounds.soundList[SoundList.wCrash.ordinal()], 836, 16726, (short) 130, (short) 2);
        chaosSounds.SetEffect(aieGridEffect[1], chaosSounds.soundList[SoundList.wCrash.ordinal()], 627, 12544, (short) 130, (short) 2);
        chaosSounds.SetEffect(aieGridEffect[2], chaosSounds.soundList[SoundList.wCrash.ordinal()], 523, 10454, (short) 130, (short) 2);
        chaosSounds.SetEffect(aieGridEffect[3], chaosSounds.soundList[SoundList.wCrash.ordinal()], 418, 8363, (short) 130, (short) 2);
        chaosSounds.SetEffect(aieGhostEffect[0], chaosSounds.soundList[SoundList.sHurryUp.ordinal()], 0, 4181, (short) 100, (short) 1);
        chaosSounds.SetEffect(dieGhostEffect[0], chaosSounds.soundList[SoundList.wWhite.ordinal()], 0, 0, (short) 160, (short) 4);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetAlienV_ref;
        attr.Make = MakeAlienV_ref;
        attr.Move = MoveAlienAV_ref;
        attr.Aie = AieAlienAV_ref;
        attr.Die = DieAlienV_ref;
        attr.charge = 40;
        attr.weight = 24;
        attr.inerty = 60;
        attr.priority = -58;
        attr.heatSpeed = 40;
        attr.refreshSpeed = 40;
        attr.coolSpeed = 25;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.gravityStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 7;
        attr.dieStStyle = ChaosBase.gravityStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetAlienA_ref;
        attr.Make = MakeAlienA_ref;
        attr.Move = MoveAlienAV_ref;
        attr.Aie = AieAlienAV_ref;
        attr.Die = DieAlienA_ref;
        attr.charge = 30;
        attr.weight = 48;
        attr.inerty = 60;
        attr.priority = -56;
        attr.heatSpeed = 25;
        attr.refreshSpeed = 25;
        attr.coolSpeed = 35;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.gravityStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 8;
        attr.dieStStyle = ChaosBase.gravityStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetCreatorR_ref;
        attr.Make = MakeCreatorR_ref;
        attr.Move = MoveCreatorR_ref;
        attr.Aie = AieCreator_ref;
        attr.Die = DieCreatorR_ref;
        attr.charge = 16;
        attr.weight = 100;
        attr.inerty = 14;
        attr.priority = -70;
        attr.heatSpeed = 80;
        attr.refreshSpeed = 90;
        attr.coolSpeed = 150;
        attr.aieStKinds = EnumSet.noneOf(Stones.class);
        attr.aieSKCount = 0;
        attr.aieStone = ChaosBase.FlameMult;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stCE);
        attr.dieSKCount = 1;
        attr.dieStone = 10;
        attr.dieStStyle = ChaosBase.gravityStyle;
        attr.basicType = BasicTypes.Vegetal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetCircle_ref;
        attr.Make = MakeCircle_ref;
        attr.Move = MoveCircle_ref;
        attr.Aie = AieCircle_ref;
        attr.Die = DieCircle_ref;
        attr.charge = 16;
        attr.weight = 100;
        attr.inerty = 256;
        attr.priority = -65;
        attr.heatSpeed = 20;
        attr.refreshSpeed = 40;
        attr.coolSpeed = 120;
        attr.aieStKinds = EnumSet.of(Stones.stFOG2);
        attr.aieSKCount = 1;
        attr.aieStone = 10;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stFOG4);
        attr.dieSKCount = 1;
        attr.dieStone = (short) (ChaosBase.FlameMult * 4 + 20);
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetCreatorC_ref;
        attr.Make = MakeCreatorC_ref;
        attr.Move = MoveCreatorC_ref;
        attr.Aie = AieCreator_ref;
        attr.Die = DieCreatorC_ref;
        attr.charge = 24;
        attr.weight = 80;
        attr.inerty = 16;
        attr.priority = -70;
        attr.heatSpeed = 60;
        attr.refreshSpeed = 60;
        attr.coolSpeed = 75;
        attr.aieStKinds = EnumSet.noneOf(Stones.class);
        attr.aieSKCount = 0;
        attr.aieStone = ChaosBase.FlameMult;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stRE);
        attr.dieSKCount = 1;
        attr.dieStone = 10;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Vegetal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetController_ref;
        attr.Move = MoveController_ref;
        attr.basicType = BasicTypes.NotBase;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetHurryUp_ref;
        attr.Make = MakeHurryUp_ref;
        attr.Move = MoveHurryUp_ref;
        attr.Aie = AieHurryUp_ref;
        attr.Die = DieHurryUp_ref;
        attr.charge = 12;
        attr.weight = 80;
        attr.inerty = 32;
        attr.priority = -68;
        attr.heatSpeed = 30;
        attr.refreshSpeed = 60;
        attr.coolSpeed = 5;
        attr.aieStKinds = EnumSet.of(Stones.stCROSS);
        attr.aieSKCount = 1;
        attr.aieStone = 1;
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stCROSS);
        attr.dieSKCount = 1;
        attr.dieStone = 12;
        attr.dieStStyle = ChaosBase.gravityStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetChief_ref;
        attr.Make = MakeChief_ref;
        attr.Move = MoveChief_ref;
        attr.Aie = AieChief_ref;
        attr.Die = DieChief_ref;
        attr.charge = 24;
        attr.weight = 75;
        attr.inerty = 300;
        attr.priority = -52;
        attr.heatSpeed = 0;
        attr.refreshSpeed = 10;
        attr.coolSpeed = 4;
        attr.aieStKinds = EnumSet.of(Stones.stCROSS);
        attr.aieSKCount = 1;
        attr.aieStone = 7;
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stFOG4);
        attr.dieSKCount = 1;
        attr.dieStone = 25;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Vegetal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetFour_ref;
        attr.Make = MakeFour_ref;
        attr.Move = MoveAlienAV_ref;
        attr.Aie = AieAlienAV_ref;
        attr.Die = DieFour_ref;
        attr.charge = 15;
        attr.weight = 64;
        attr.inerty = 50;
        attr.priority = -56;
        attr.heatSpeed = 70;
        attr.refreshSpeed = 70;
        attr.coolSpeed = 35;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 8;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetABox_ref;
        attr.Make = MakeABox_ref;
        attr.Move = MoveABox_ref;
        attr.Aie = AieABox_ref;
        attr.Die = DieABox_ref;
        attr.charge = 0;
        attr.weight = 100;
        attr.inerty = 10;
        attr.priority = -65;
        attr.heatSpeed = 200;
        attr.refreshSpeed = 90;
        attr.coolSpeed = 100;
        attr.aieStKinds = EnumSet.noneOf(Stones.class);
        attr.aieSKCount = 0;
        attr.aieStone = ChaosBase.FlameMult;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stRE);
        attr.dieSKCount = 1;
        attr.dieStone = 7;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Vegetal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetNest_ref;
        attr.Make = MakeNest_ref;
        attr.Move = MoveNest_ref;
        attr.Aie = AieNest_ref;
        attr.Die = DieNest_ref;
        attr.weight = 160;
        attr.priority = -76;
        attr.heatSpeed = 10;
        attr.refreshSpeed = 30;
        attr.coolSpeed = 60;
        attr.aieStKinds = EnumSet.noneOf(Stones.class);
        attr.aieSKCount = 0;
        attr.aieStone = ChaosBase.FlameMult;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stCE);
        attr.dieSKCount = 1;
        attr.dieStone = (short) (ChaosBase.FlameMult * 4 + 9);
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Vegetal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetGrid_ref;
        attr.Make = MakeGrid_ref;
        attr.Move = MoveGrid_ref;
        attr.Aie = AieGrid_ref;
        attr.Die = DieGrid_ref;
        attr.charge = -60;
        attr.weight = 90;
        attr.inerty = 100;
        attr.priority = -63;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 5;
        attr.aieStStyle = ChaosBase.returnStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeMissile_as_ChaosBase_ResetProc;
        attr.Make = MakeMissile_as_ChaosBase_MakeProc;
        attr.Move = MoveMissile_ref;
        attr.Aie = AieMissile_ref;
        attr.Die = DieMissile_ref;
        attr.charge = 2;
        attr.weight = 30;
        attr.inerty = 50;
        attr.priority = -30;
        attr.basicType = BasicTypes.Vegetal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetPopUp_ref;
        attr.Make = MakePopUp_ref;
        attr.Move = MovePopUp_ref;
        attr.Aie = AiePopUp_ref;
        attr.Die = DiePopUp_ref;
        attr.charge = 20;
        attr.weight = 20;
        attr.inerty = 40;
        attr.priority = -20;
        attr.heatSpeed = 25;
        attr.refreshSpeed = 25;
        attr.coolSpeed = 10;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.slowStyle;
        attr.dieStKinds = EnumSet.of(Stones.stFOG1);
        attr.dieSKCount = 1;
        attr.dieStone = 3;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetGhost_ref;
        attr.Make = MakeGhost_ref;
        attr.Move = MoveGhost_ref;
        attr.Aie = AieGhost_ref;
        attr.Die = DieGhost_ref;
        attr.charge = 0;
        attr.weight = 10;
        attr.inerty = 90;
        attr.priority = -64;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetQuad_ref;
        attr.Make = MakeQuad_ref;
        attr.Move = MoveAlienAV_ref;
        attr.Aie = AieAlienAV_ref;
        attr.Die = DieFour_ref;
        attr.charge = 15;
        attr.weight = 64;
        attr.inerty = 50;
        attr.priority = -56;
        attr.heatSpeed = 90;
        attr.refreshSpeed = 90;
        attr.coolSpeed = 10;
        attr.aieStKinds = EnumSet.of(Stones.stC35);
        attr.aieSKCount = 1;
        attr.aieStone = 3;
        attr.aieStStyle = ChaosBase.fastStyle;
        attr.dieStKinds = EnumSet.of(Stones.stC35);
        attr.dieSKCount = 1;
        attr.dieStone = 8;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.Animal;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.ALIEN2.ordinal()], attr.node);
    }


    // Support

    private static ChaosCreator instance;

    public static ChaosCreator instance() {
        if (instance == null)
            new ChaosCreator(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        InitParams();
    }

    public void close() {
    }

}
