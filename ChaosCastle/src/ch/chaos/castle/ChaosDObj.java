package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.ObjFlags;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosDObj {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosDObj() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // CONST

    public static final int doCartoon = 0; // Dark-grey false block that is on top of everything else
    public static final int doMagnetA = 1;
    public static final int doMagnetR = 2;
    public static final int doMeteor = 3;
    public static final int domStone = 0;
    public static final int domSmall = 1;
    public static final int domMedium = 2;
    public static final int domBig = 3;
    public static final int doSand = 4;
    public static final int doWind = 5;
    public static final int doBubble = 6;
    public static final int doMirror = 7; // stat 0 = square, 1 = losange
    public static final int doWindMaker = 8;
    public static final int doBubbleMaker = 9;
    public static final int doFireMaker = 10;
    public static final int doFireWall = 11;
    public static final int doWave = 12;


    // VAR

    private ChaosSounds.Effect[] getAirEffect = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] bubbleCreateEffect = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] createFireEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] createWindEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieWindEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosBase.ObjAttr meteorObjAttr = new ChaosBase.ObjAttr();


    public ChaosSounds.Effect[] getGetAirEffect() {
        return this.getAirEffect;
    }

    public void setGetAirEffect(ChaosSounds.Effect[] getAirEffect) {
        this.getAirEffect = getAirEffect;
    }

    public ChaosSounds.Effect[] getBubbleCreateEffect() {
        return this.bubbleCreateEffect;
    }

    public void setBubbleCreateEffect(ChaosSounds.Effect[] bubbleCreateEffect) {
        this.bubbleCreateEffect = bubbleCreateEffect;
    }

    public ChaosSounds.Effect[] getCreateFireEffect() {
        return this.createFireEffect;
    }

    public void setCreateFireEffect(ChaosSounds.Effect[] createFireEffect) {
        this.createFireEffect = createFireEffect;
    }

    public ChaosSounds.Effect[] getCreateWindEffect() {
        return this.createWindEffect;
    }

    public void setCreateWindEffect(ChaosSounds.Effect[] createWindEffect) {
        this.createWindEffect = createWindEffect;
    }

    public ChaosSounds.Effect[] getDieWindEffect() {
        return this.dieWindEffect;
    }

    public void setDieWindEffect(ChaosSounds.Effect[] dieWindEffect) {
        this.dieWindEffect = dieWindEffect;
    }

    public ChaosBase.ObjAttr getMeteorObjAttr() {
        return this.meteorObjAttr;
    }

    public void setMeteorObjAttr(ChaosBase.ObjAttr meteorObjAttr) {
        this.meteorObjAttr = meteorObjAttr;
    }


    // PROCEDURE

    private void MakeCartoon(ChaosBase.Obj cartoon) {
        chaosActions.SetObjLoc(cartoon, (short) 128, (short) 88, (short) 32, (short) 32);
        chaosActions.SetObjRect(cartoon, 0, 0, 32, 32);
    }

    private final ChaosBase.ResetProc MakeCartoon_as_ChaosBase_ResetProc = this::MakeCartoon;
    private final ChaosBase.MakeProc MakeCartoon_as_ChaosBase_MakeProc = this::MakeCartoon;

    private void MakeMagnet(ChaosBase.Obj magnet) {
        // VAR
        short px = 0;

        if (magnet.moveSeq == 0)
            px = 128;
        else if (magnet.moveSeq == 1)
            px = 140;
        else if (magnet.moveSeq == 2)
            px = 152;
        else
            px = 164;
        chaosActions.SetObjLoc(magnet, px, (short) 120, (short) 12, (short) 12);
        chaosActions.SetObjRect(magnet, 0, 0, 12, 12);
    }

    private final ChaosBase.MakeProc MakeMagnet_ref = this::MakeMagnet;

    private void MakeMeteor(ChaosBase.Obj meteor) {
        // VAR
        short px = 0;
        short py = 0;
        short sz = 0;
        int shp = 0;

        shp = meteor.shapeSeq / (ChaosBase.Period / 8);
        switch (meteor.stat) {
            case domStone -> {
                px = 248;
                py = 120;
                sz = 8;
            }
            case domSmall -> {
                px = 224;
                py = 70;
                sz = 5;
            }
            case domMedium -> {
                px = (short) ((shp % 3) * 8 + 224);
                py = 120;
                sz = 8;
            }
            case domBig -> {
                px = (short) ((shp % 4) * 12 + 176);
                py = 120;
                sz = 12;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + meteor.stat);
        }
        chaosActions.SetObjLoc(meteor, px, py, sz, sz);
        if (!meteor.flags.contains(ObjFlags.displayed))
            chaosActions.SetObjRect(meteor, 0, 0, sz, sz);
    }

    private final ChaosBase.MakeProc MakeMeteor_ref = this::MakeMeteor;

    private void MakeSand(ChaosBase.Obj sand) {
        sand.life = 1;
        chaosActions.SetObjLoc(sand, (short) 240, (short) 184, (short) 16, (short) 16);
        chaosActions.SetObjRect(sand, 1, 1, 15, 15);
    }

    private final ChaosBase.ResetProc MakeSand_as_ChaosBase_ResetProc = this::MakeSand;
    private final ChaosBase.MakeProc MakeSand_as_ChaosBase_MakeProc = this::MakeSand;

    private void MakeWind(ChaosBase.Obj wind) {
        wind.hitSubLife = 0;
        wind.fireSubLife = 0;
        chaosActions.SetObjLoc(wind, (short) 224, (short) 68, (short) 8, (short) 8);
        chaosActions.SetObjLoc(wind, (short) 104, (short) 136, (short) 16, (short) 16);
        chaosActions.SetObjRect(wind, -4, -4, 12, 12);
    }

    private final ChaosBase.MakeProc MakeWind_ref = this::MakeWind;

    private void MakeBubble(ChaosBase.Obj bubble) {
        // VAR
        short px = 0;
        short py = 0;
        short sz = 0;

        bubble.hitSubLife = 0;
        bubble.fireSubLife = 0;
        switch (bubble.stat) {
            case 5 -> {
                px = 212;
                py = 144;
                sz = 12;
            }
            case 4 -> {
                px = 158;
                py = 62;
                sz = 10;
            }
            case 3 -> {
                px = 248;
                py = 164;
                sz = 8;
            }
            case 2 -> {
                px = 248;
                py = 172;
                sz = 6;
            }
            case 1 -> {
                px = 248;
                py = 178;
                sz = 4;
            }
            case 0 -> {
                px = 248;
                py = 182;
                sz = 2;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + bubble.stat);
        }
        chaosActions.SetObjLoc(bubble, px, py, sz, sz);
        chaosActions.SetObjRect(bubble, 0, 0, sz, sz);
    }

    private final ChaosBase.MakeProc MakeBubble_ref = this::MakeBubble;

    private void MakeMirror(ChaosBase.Obj mirror) {
        // VAR
        short px = 0;

        if (((mirror.stat % 2) != 0))
            px = 192;
        else
            px = 224;
        mirror.hitSubLife = 0;
        mirror.fireSubLife = 0;
        chaosActions.SetObjLoc(mirror, px, (short) 88, (short) 32, (short) 32);
        chaosActions.SetObjRect(mirror, 1, 1, 31, 31);
    }

    private final ChaosBase.ResetProc MakeMirror_as_ChaosBase_ResetProc = this::MakeMirror;
    private final ChaosBase.MakeProc MakeMirror_as_ChaosBase_MakeProc = this::MakeMirror;

    private void MakeWindMaker(ChaosBase.Obj maker) {
        if (((maker.stat % 2) != 0) || (maker.shapeSeq <= ChaosBase.Period * 4))
            chaosActions.SetObjLoc(maker, (short) 140, (short) 180, (short) 12, (short) 20);
        else
            chaosActions.SetObjLoc(maker, (short) (32 + (3 - (maker.shapeSeq / 32) % 4) * 12), (short) 180, (short) 12, (short) 20);
        chaosActions.SetObjRect(maker, 0, 0, 12, 20);
    }

    private final ChaosBase.MakeProc MakeWindMaker_ref = this::MakeWindMaker;

    private void MakeBubbleMaker(ChaosBase.Obj maker) {
        chaosActions.SetObjLoc(maker, (short) 80, (short) 180, (short) 12, (short) 20);
        chaosActions.SetObjRect(maker, 0, 0, 12, 20);
    }

    private final ChaosBase.MakeProc MakeBubbleMaker_ref = this::MakeBubbleMaker;

    private void MakeFireMaker(ChaosBase.Obj maker) {
        chaosActions.SetObjLoc(maker, (short) 32, (short) 200, (short) 20, (short) 12);
        chaosActions.SetObjRect(maker, 0, 0, 20, 12);
    }

    private final ChaosBase.MakeProc MakeFireMaker_ref = this::MakeFireMaker;

    private void MakeFireWall(ChaosBase.Obj fw) {
        chaosActions.SetObjLoc(fw, (short) 224, (short) 204, (short) 32, (short) 32);
        chaosActions.SetObjRect(fw, 4, 4, 28, 28);
    }

    private final ChaosBase.ResetProc MakeFireWall_as_ChaosBase_ResetProc = this::MakeFireWall;
    private final ChaosBase.MakeProc MakeFireWall_as_ChaosBase_MakeProc = this::MakeFireWall;

    private void MakeWave(ChaosBase.Obj wave) {
        // VAR
        short px = 0;
        short py = 0;
        short sz = 0;

        switch (wave.stat) {
            case 0 -> {
                px = 188;
                py = 210;
                sz = 2;
            }
            case 1 -> {
                px = 211;
                py = 208;
                sz = 3;
            }
            case 2 -> {
                px = 208;
                py = 208;
                sz = 3;
            }
            case 3 -> {
                px = 194;
                py = 209;
                sz = 3;
            }
            case 4 -> {
                px = 220;
                py = 216;
                sz = 4;
            }
            case 5 -> {
                px = 216;
                py = 216;
                sz = 4;
            }
            case 6 -> {
                px = 200;
                py = 208;
                sz = 4;
            }
            case 7 -> {
                px = 219;
                py = 208;
                sz = 5;
            }
            case 8 -> {
                px = 214;
                py = 208;
                sz = 5;
            }
            case 9 -> {
                px = 194;
                py = 204;
                sz = 5;
            }
            case 10 -> {
                px = 210;
                py = 214;
                sz = 6;
            }
            case 11 -> {
                px = 204;
                py = 214;
                sz = 6;
            }
            case 12 -> {
                px = 188;
                py = 204;
                sz = 6;
            }
            case 13 -> {
                px = 196;
                py = 212;
                sz = 8;
            }
            case 14 -> {
                px = 188;
                py = 212;
                sz = 8;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + wave.stat);
        }
        chaosActions.SetObjLoc(wave, px, py, sz, sz);
        chaosActions.SetObjRect(wave, -2, -2, sz + 2, sz + 2);
    }

    private final ChaosBase.MakeProc MakeWave_ref = this::MakeWave;

    private void ResetMagnet(ChaosBase.Obj magnet) {
        magnet.moveSeq = trigo.RND() % 4;
        magnet.shapeSeq = trigo.RND() % (ChaosBase.Period / 10);
        MakeMagnet(magnet);
    }

    private final ChaosBase.ResetProc ResetMagnet_ref = this::ResetMagnet;

    private void ResetMeteor(ChaosBase.Obj meteor) {
        meteor.shapeSeq = trigo.RND() % ChaosBase.Period;
        meteor.moveSeq = ChaosBase.Period * 2;
        meteor.flags.add(ObjFlags.of1);
        meteor.life = 10;
        MakeMeteor(meteor);
    }

    private final ChaosBase.ResetProc ResetMeteor_ref = this::ResetMeteor;

    private void ResetWind(ChaosBase.Obj wind) {
        wind.moveSeq = ChaosBase.Period / 4 + trigo.RND() % (ChaosBase.Period * 4);
        MakeWind(wind);
    }

    private final ChaosBase.ResetProc ResetWind_ref = this::ResetWind;

    private void ResetBubble(ChaosBase.Obj bubble) {
        bubble.moveSeq = ChaosBase.Period * 2 + trigo.RND() % ChaosBase.Period;
        bubble.life = 1;
        MakeBubble(bubble);
    }

    private final ChaosBase.ResetProc ResetBubble_ref = this::ResetBubble;

    private void ResetWindMaker(ChaosBase.Obj maker) {
        maker.shapeSeq = ChaosBase.Period / 10;
        maker.stat = maker.stat % 4;
        maker.moveSeq = trigo.RND() % (ChaosBase.Period / 2) + ChaosBase.Period / 5;
        MakeWindMaker(maker);
    }

    private final ChaosBase.ResetProc ResetWindMaker_ref = this::ResetWindMaker;

    private void ResetBubbleMaker(ChaosBase.Obj maker) {
        maker.stat = maker.stat % 2;
        maker.moveSeq = trigo.RND() % (ChaosBase.Period * 3);
        MakeBubbleMaker(maker);
    }

    private final ChaosBase.ResetProc ResetBubbleMaker_ref = this::ResetBubbleMaker;

    private void ResetFireMaker(ChaosBase.Obj maker) {
        maker.moveSeq = 0;
        MakeFireMaker(maker);
    }

    private final ChaosBase.ResetProc ResetFireMaker_ref = this::ResetFireMaker;

    private void ResetWave(ChaosBase.Obj wave) {
        wave.hitSubLife = 0;
        wave.fireSubLife = 0;
        wave.life = 1;
        wave.moveSeq = ChaosBase.Period * 4 + trigo.RND() % ChaosBase.Period;
        MakeWave(wave);
    }

    private final ChaosBase.ResetProc ResetWave_ref = this::ResetWave;

    private void MoveCartoon(ChaosBase.Obj cartoon) {
        if (chaosActions.OutOfScreen(cartoon)) {
            chaosActions.Leave(cartoon);
            return;
        }
    }

    private final ChaosBase.MoveProc MoveCartoon_ref = this::MoveCartoon;

    private void MoveMagnet(ChaosBase.Obj magnet) {
        if (chaosActions.OutOfScreen(magnet)) {
            chaosActions.Leave(magnet);
            return;
        }
        chaosActions.UpdateXY(magnet);
        magnet.vx = 0;
        magnet.vy = 0;
        magnet.dvx = 0;
        magnet.dvy = 0;
        if (chaosBase.step >= magnet.shapeSeq) {
            magnet.shapeSeq += ChaosBase.Period / ((magnet.stat + 1) * 4);
            if (magnet.subKind == doMagnetA)
                magnet.moveSeq = (magnet.moveSeq + 1) % 4;
            else if (magnet.moveSeq == 0)
                magnet.moveSeq = 3;
            else
                magnet.moveSeq--;
            MakeMagnet(magnet);
        }
        if (chaosBase.step >= magnet.shapeSeq)
            magnet.shapeSeq = 0;
        else
            magnet.shapeSeq -= chaosBase.step;
        magnet.attr.charge = (short) ((magnet.stat + 1) * 30);
        if (magnet.subKind == doMagnetA)
            magnet.attr.charge = (short) -magnet.attr.charge;
        chaosActions.Gravity(magnet, Runtime.withRange(EnumSet.noneOf(Anims.class), Anims.PLAYER, Anims.BONUS));
    }

    private final ChaosBase.MoveProc MoveMagnet_ref = this::MoveMagnet;

    private void MeteorCrash(ChaosBase.Obj m1, ChaosBase.Obj m2, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        if ((m1 != m2) && (m1.subKind == doMeteor) && (m1.stat >= domMedium) && (m1.stat <= m2.stat)) {
            if (m1.stat == m2.stat) {
                chaosActions.Boum(m2, EnumSet.noneOf(Stones.class), (short) ChaosBase.slowStyle, (short) ChaosBase.FlameMult, (short) 0);
                m2.life = 0;
            }
            chaosActions.Boum(m1, EnumSet.noneOf(Stones.class), (short) ChaosBase.slowStyle, (short) ChaosBase.FlameMult, (short) 0);
            chaosActions.Die(m1);
        }
    }

    private final ChaosBase.AieProc MeteorCrash_ref = this::MeteorCrash;

    private void MoveMeteor(ChaosBase.Obj meteor) {
        // VAR
        Memory.Node parent = null; /* PATCH */
        Memory.Node tail = null;
        Memory.Node pred = null;
        Memory.Node succ = null;
        ChaosBase.ObjAttr oldAttr = null;
        Runtime.Ref<Short> mx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> my = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        int dx = 0;
        int dy = 0;
        int dist = 0;
        EnumSet<Anims> anims = EnumSet.noneOf(Anims.class);
        boolean oob = false;

        meteor.dvx = meteor.vx;
        meteor.dvy = meteor.vy;
        chaosActions.UpdateXY(meteor);
        meteor.shapeSeq = (meteor.shapeSeq + chaosBase.step) % ((ChaosBase.Period / 8) * 12);
        MakeMeteor(meteor);
        if (meteor.stat != domBig) {
            if (meteor.flags.contains(ObjFlags.of1)) {
                parent = memory.NextNode(meteor.objNode);
                tail = memory.TailNode(chaosBase.objList);
            } else {
                parent = memory.PrevNode(meteor.objNode);
                tail = memory.FirstNode(chaosBase.objList);
            }
            if ((parent != tail) 
                    && (obj(parent) != null)
                    && (obj(parent).kind == Anims.DEADOBJ) 
                    && (obj(parent).subKind == doMeteor) 
                    && (obj(parent).stat > meteor.stat)) {
                chaosActions.GetCenter(meteor, mx, my);
                chaosActions.GetCenter(obj(parent), px, py);
                dx = px.get() - mx.get();
                dy = py.get() - my.get();
                dist = trigo.SQRT(dx * dx + dy * dy);
                if (meteor.flags.contains(ObjFlags.of2)) {
                    if (dist <= meteor.hitSubLife)
                        meteor.hitSubLife = dist;
                    else
                        meteor.flags.remove(ObjFlags.of2);
                } else {
                    if (dist >= meteor.hitSubLife) {
                        meteor.hitSubLife = dist;
                    } else {
                        meteor.flags.add(ObjFlags.of2);
                        if (meteor.flags.contains(ObjFlags.of1)) {
                            pred = memory.PrevNode(meteor.objNode);
                            succ = memory.NextNode(parent);
                            pred.next = parent;
                            succ.prev = meteor.objNode;
                            parent.prev = pred;
                            meteor.objNode.next = succ;
                            meteor.objNode.prev = parent;
                            parent.next = meteor.objNode;
                            meteor.flags.remove(ObjFlags.of1);
                        } else {
                            pred = memory.PrevNode(parent);
                            succ = memory.NextNode(meteor.objNode);
                            pred.next = meteor.objNode;
                            succ.prev = parent;
                            parent.next = succ;
                            meteor.objNode.prev = pred;
                            meteor.objNode.next = parent;
                            parent.prev = meteor.objNode;
                            meteor.flags.add(ObjFlags.of1);
                        }
                    }
                }
            }
        }
        oob = chaosActions.OutOfBounds(meteor);
        oldAttr = meteor.attr;
        meteor.attr = meteorObjAttr;
        meteor.attr.charge = (short) (meteor.subKind * 30);
        meteor.attr.charge = (short) -meteor.attr.charge;
        if (oob)
            anims = EnumSet.of(Anims.DEADOBJ);
        else
            anims = Runtime.withRange(EnumSet.of(Anims.WEAPON), Anims.MISSILE, Anims.DEADOBJ);
        if (meteor.stat == domSmall)
            anims.remove(Anims.DEADOBJ);
        if ((meteor.stat != domStone) && (!anims.equals(EnumSet.noneOf(Anims.class))))
            chaosActions.Gravity(meteor, anims);
        meteor.attr = oldAttr;
        meteor.attr.charge = 90;
        meteor.flags.add(ObjFlags.displayed);
        if (Math.abs(meteor.vx) + Math.abs(meteor.vy) < 50)
            meteor.ay = 1;
        else
            meteor.ay = 0;
        if (meteor.moveSeq == ChaosBase.Period * 2) {
            if (chaosBase.nbAnim[Anims.ALIEN2.ordinal()] == 0) {
                chaosActions.Die(meteor);
                return;
            }
            if (!oob)
                meteor.moveSeq--;
        } else if ((meteor.moveSeq == 0)) {
            if (oob) {
                chaosActions.Die(meteor);
                return;
            }
        } else {
            if (chaosBase.step >= meteor.moveSeq)
                meteor.moveSeq = 0;
            else
                meteor.moveSeq -= chaosBase.step;
        }
        if (meteor.stat >= domMedium) {
            chaosActions.DoCollision(meteor, EnumSet.of(Anims.DEADOBJ), MeteorCrash_ref, new Runtime.FieldRef<>(meteor::getHitSubLife, meteor::setHitSubLife), new Runtime.FieldRef<>(meteor::getFireSubLife, meteor::setFireSubLife));
        } else if (meteor.stat == domStone) {
            meteor.hitSubLife = 10;
            chaosActions.PlayerCollision(meteor, new Runtime.FieldRef<>(meteor::getHitSubLife, meteor::setHitSubLife));
        }
        if ((chaosBase.nbAnim[Anims.ALIEN2.ordinal()] + chaosBase.nbAnim[Anims.ALIEN1.ordinal()] == 0) && (Math.abs(meteor.vx) < 384) && (Math.abs(meteor.vy) < 384))
            meteor.ay = 1;
        if ((meteor.life == 0) || chaosActions.OutOfScreen(meteor))
            chaosActions.Die(meteor);
    }

    private final ChaosBase.MoveProc MoveMeteor_ref = this::MoveMeteor;

    private static ChaosBase.Obj obj(Memory.Node node) { /* PATCH */
        return (ChaosBase.Obj) node.data;
    }

    private void SlowDown(ChaosBase.Obj victim, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        // VAR
        short sw = 0;
        short vw = 0;

        if (src.subKind == doBubble)
            victim.temperature = 0;
        if ((Math.abs(victim.vx) < 512) && (Math.abs(victim.vy) < 512))
            return;
        chaosActions.Aie(victim, src, hit, fire);
        if ((src.subKind == doWind) && (victim.kind != Anims.STONE)) {
            src.moveSeq = 0;
            return;
        }
        sw = victim.attr.weight;
        vw = (short) (src.attr.weight * 5);
        if (sw + vw != 0) {
            src.vx = (short) ((src.vx / 64 * vw + victim.vx / 64 * sw) / (sw + vw) * 64);
            src.vy = (short) ((src.vy / 64 * vw + victim.vy / 64 * sw) / (sw + vw) * 64);
        }
    }

    private final ChaosBase.AieProc SlowDown_ref = this::SlowDown;

    private void KillOnMagnet(ChaosBase.Obj victim, ChaosBase.Obj src, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        if (victim.subKind == doMagnetA)
            src.life = 0;
    }

    private final ChaosBase.AieProc KillOnMagnet_ref = this::KillOnMagnet;

    private void MoveSand(ChaosBase.Obj sand) {
        if (chaosActions.OutOfScreen(sand)) {
            chaosActions.Leave(sand);
            return;
        }
        chaosActions.UpdateXY(sand);
        chaosActions.AvoidBackground(sand, (short) 0);
        sand.hitSubLife = 0;
        sand.fireSubLife = 0;
        chaosActions.DoCollision(sand, Runtime.withRange(EnumSet.of(Anims.PLAYER, Anims.BONUS, Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1), SlowDown_ref, new Runtime.FieldRef<>(sand::getHitSubLife, sand::setHitSubLife), new Runtime.FieldRef<>(sand::getFireSubLife, sand::setFireSubLife));
        chaosActions.DoCollision(sand, EnumSet.of(Anims.DEADOBJ), KillOnMagnet_ref, new Runtime.FieldRef<>(sand::getHitSubLife, sand::setHitSubLife), new Runtime.FieldRef<>(sand::getFireSubLife, sand::setFireSubLife));
        if (sand.life == 0)
            chaosActions.Die(sand);
    }

    private final ChaosBase.MoveProc MoveSand_ref = this::MoveSand;

    private void MoveWind(ChaosBase.Obj wind) {
        chaosActions.UpdateXY(wind);
        chaosActions.AvoidBackground(wind, (short) 0);
        chaosActions.DoCollision(wind, Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.PLAYER, Anims.BONUS), SlowDown_ref, new Runtime.FieldRef<>(wind::getHitSubLife, wind::setHitSubLife), new Runtime.FieldRef<>(wind::getFireSubLife, wind::setFireSubLife));
        if (chaosActions.OutOfScreen(wind) || (chaosBase.step >= wind.moveSeq) || (Math.abs(wind.vx) + Math.abs(wind.vy) < 2000)) {
            chaosSounds.SoundEffect(wind, dieWindEffect);
            chaosActions.Die(wind);
            return;
        }
        wind.moveSeq -= chaosBase.step;
    }

    private final ChaosBase.MoveProc MoveWind_ref = this::MoveWind;

    private void GiveAir(ChaosBase.Obj player, ChaosBase.Obj bubble, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        // VAR
        Runtime.Ref<String> msg = new Runtime.Ref<>("");
        int togive = 0;
        int val = 0;

        bubble.life = 0;
        if (chaosBase.water) {
            togive = ChaosBase.Period * bubble.stat * 12;
            if (togive == 0)
                return;
            if (chaosBase.air < 60000 - togive) {
                chaosSounds.SoundEffect(player, getAirEffect);
                chaosBase.air += togive;
            } else {
                chaosBase.air = 60000;
            }
            val = chaosBase.air / ChaosBase.Period;
            if (val < 10) {
                msg.set("()   ()");
                Runtime.setChar(msg, 3, (char) (48 + val));
            } else if (val < 100) {
                msg.set("()    ()");
                Runtime.setChar(msg, 3, (char) (48 + val / 10));
                Runtime.setChar(msg, 4, (char) (48 + val % 10));
            } else {
                msg.set("()     ()");
                Runtime.setChar(msg, 3, (char) (48 + val / 100));
                val = val % 100;
                Runtime.setChar(msg, 4, (char) (48 + val / 10));
                Runtime.setChar(msg, 5, (char) (48 + val % 10));
            }
            chaosActions.PopMessage(msg, (short) ChaosActions.lifePos, (short) ((bubble.stat + 1) / 2));
        }
    }

    private final ChaosBase.AieProc GiveAir_ref = this::GiveAir;

    private void MoveWave(ChaosBase.Obj wave) {
        if (chaosActions.OutOfScreen(wave) || chaosActions.InBackground(wave)) {
            chaosActions.Die(wave);
            return;
        }
        chaosActions.UpdateXY(wave);
        chaosActions.AvoidBackground(wave, (short) 1);
        chaosActions.DoCollision(wave, Runtime.withRange(EnumSet.of(Anims.PLAYER, Anims.BONUS), Anims.ALIEN3, Anims.ALIEN1), SlowDown_ref, new Runtime.FieldRef<>(wave::getHitSubLife, wave::setHitSubLife), new Runtime.FieldRef<>(wave::getFireSubLife, wave::setFireSubLife));
        chaosActions.LimitSpeed(wave, (short) 1600);
        wave.dvx = wave.vx;
        wave.dvy = wave.vy;
        if (wave.dvx > 1024)
            wave.dvx = 1024;
        else if (wave.dvx < -1024)
            wave.dvx = -1024;
        if (wave.dvy > 1024)
            wave.dvy = 1024;
        else if (wave.dvy < -1024)
            wave.dvy = -1024;
        if (chaosBase.step > wave.moveSeq) {
            if (wave.stat < 3) {
                chaosActions.Die(wave);
                return;
            } else {
                wave.stat -= 3;
            }
            ResetWave(wave);
        } else {
            wave.moveSeq -= chaosBase.step;
        }
    }

    private final ChaosBase.MoveProc MoveWave_ref = this::MoveWave;

    private void MoveBubble(ChaosBase.Obj bubble) {
        if (chaosActions.OutOfScreen(bubble)) {
            chaosActions.Die(bubble);
            return;
        }
        chaosActions.AvoidBackground(bubble, (short) 1);
        chaosActions.UpdateXY(bubble);
        chaosActions.DoCollision(bubble, Runtime.withRange(EnumSet.of(Anims.BONUS), Anims.ALIEN3, Anims.ALIEN1), SlowDown_ref, new Runtime.FieldRef<>(bubble::getHitSubLife, bubble::setHitSubLife), new Runtime.FieldRef<>(bubble::getFireSubLife, bubble::setFireSubLife));
        chaosActions.DoCollision(bubble, EnumSet.of(Anims.PLAYER), GiveAir_ref, new Runtime.FieldRef<>(bubble::getHitSubLife, bubble::setHitSubLife), new Runtime.FieldRef<>(bubble::getFireSubLife, bubble::setFireSubLife));
        if (chaosBase.step > bubble.moveSeq) {
            if (bubble.stat == 0)
                bubble.life = 0;
            else
                bubble.stat--;
            ResetBubble(bubble);
        } else {
            bubble.moveSeq -= chaosBase.step;
        }
        if (bubble.life == 0)
            chaosActions.Die(bubble);
    }

    private final ChaosBase.MoveProc MoveBubble_ref = this::MoveBubble;

    private void MedianM(ChaosBase.Obj victim, ChaosBase.Obj mirror, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        // VAR
        Runtime.Ref<Short> mx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> my = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> ox = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> oy = new Runtime.Ref<>((short) 0);
        short dx = 0;
        short dy = 0;

        chaosActions.GetCenter(mirror, mx, my);
        if (chaosBase.step > 30) {
            ox.set((short) (victim.midx / ChaosBase.Frac));
            ox.inc(victim.cx);
            oy.set((short) (victim.midy / ChaosBase.Frac));
            oy.inc(victim.cy);
        } else {
            chaosActions.GetCenter(victim, ox, oy);
        }
        dx = (short) (ox.get() - mx.get());
        dy = (short) (oy.get() - my.get());
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                victim.vx = (short) Math.abs(victim.vx);
                victim.dvx = (short) Math.abs(victim.dvx);
            } else {
                victim.vx = (short) -Math.abs(victim.vx);
                victim.dvx = (short) -Math.abs(victim.dvx);
            }
        } else {
            if (dy > 0) {
                victim.vy = (short) Math.abs(victim.vy);
                victim.dvy = (short) Math.abs(victim.dvy);
            } else {
                victim.vy = (short) -Math.abs(victim.vy);
                victim.dvy = (short) -Math.abs(victim.dvy);
            }
        }
    }

    private final ChaosBase.AieProc MedianM_ref = this::MedianM;

    private void DiagM(ChaosBase.Obj victim, ChaosBase.Obj mirror, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        // VAR
        Runtime.Ref<Short> mx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> my = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> ox = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> oy = new Runtime.Ref<>((short) 0);
        short dx = 0;
        short dy = 0;
        short tv = 0;
        short dtv = 0;
        int c = 0;

        chaosActions.GetCenter(mirror, mx, my);
        for (c = (chaosBase.step / 32); c >= 0; c -= 1) {
            if (c == 1) {
                ox.set((short) (victim.x / ChaosBase.Frac));
                ox.inc(victim.cx);
                oy.set((short) (victim.y / ChaosBase.Frac));
                oy.inc(victim.cy);
            } else {
                chaosActions.GetCenter(victim, ox, oy);
            }
            dx = (short) (ox.get() - mx.get());
            dy = (short) (oy.get() - my.get());
            if (Math.abs(dx) + Math.abs(dy) - victim.cx - victim.cy < 16) {
                if (dx * dy > 0) {
                    if (((dx >= 0) && (victim.vx + victim.vy <= 0)) || ((dx <= 0) && (victim.vx + victim.vy >= 0))) {
                        tv = victim.vy;
                        dtv = victim.dvy;
                        victim.vy = (short) -victim.vx;
                        victim.dvy = (short) -victim.dvx;
                        victim.vx = (short) -tv;
                        victim.dvx = (short) -dtv;
                        return;
                    }
                } else {
                    if (((dx >= 0) && (victim.vx <= victim.vy)) || ((dx <= 0) && (victim.vx >= victim.vy))) {
                        tv = victim.vy;
                        dtv = victim.dvy;
                        victim.vy = victim.vx;
                        victim.dvy = victim.dvx;
                        victim.vx = tv;
                        victim.dvx = dtv;
                        return;
                    }
                }
            }
        }
    }

    private final ChaosBase.AieProc DiagM_ref = this::DiagM;

    private void MoveMirror(ChaosBase.Obj mirror) {
        // VAR
        ChaosBase.AieProc What = null;

        if (chaosActions.OutOfScreen(mirror)) {
            chaosActions.Leave(mirror);
            return;
        }
        if (((mirror.stat % 2) != 0))
            What = DiagM_ref;
        else
            What = MedianM_ref;
        chaosActions.DoCollision(mirror, Runtime.withRange(EnumSet.noneOf(Anims.class), Anims.PLAYER, Anims.MACHINE), What, new Runtime.FieldRef<>(mirror::getHitSubLife, mirror::setHitSubLife), new Runtime.FieldRef<>(mirror::getFireSubLife, mirror::setFireSubLife));
    }

    private final ChaosBase.MoveProc MoveMirror_ref = this::MoveMirror;

    private void Blow(ChaosBase.Obj src, EnumSet<Anims> victims) {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> ox = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> oy = new Runtime.Ref<>((short) 0);
        short rx = 0;
        short ry = 0;
        short spd = 0;
        short lspd = 0;
        short is = 0;
        Anims k = Anims.PLAYER;

        chaosActions.GetCenter(src, px, py);
        is = (short) chaosBase.step;
        for (int _k = 0; _k < Anims.values().length; _k++) {
            k = Anims.values()[_k];
            if (victims.contains(k)) {
                obj = (ChaosBase.Obj) memory.First(chaosBase.animList[k.ordinal()]);
                tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[k.ordinal()]);
                while (obj != tail) {
                    chaosActions.GetCenter(obj, ox, oy);
                    rx = (short) Math.abs(ox.get() - px.get());
                    ry = (short) Math.abs(oy.get() - py.get());
                    if ((rx > ry) && (rx < 128)) {
                        spd = (short) (128 - rx);
                        spd -= spd * ry / rx;
                        spd = (short) (spd * is / 4);
                        lspd = (short) ((oy.get() - py.get()) * ry / rx * is / 8);
                        if (ox.get() < px.get())
                            spd = (short) -spd;
                        obj.vx += spd;
                        obj.vy += lspd;
                        if (obj.vx < -4096)
                            obj.vx = -4096;
                        else if (obj.vx > 4096)
                            obj.vx = 4096;
                        if (obj.vy < -4096)
                            obj.vy = -4096;
                        else if (obj.vy > 4096)
                            obj.vy = 4096;
                    }
                    obj = (ChaosBase.Obj) memory.Next(obj.animNode);
                }
            }
        }
    }

    private void MoveWindMaker(ChaosBase.Obj maker) {
        // VAR
        ChaosBase.Obj wind = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short nvy = 0;
        int oldSeq = 0;

        if (chaosActions.OutOfScreen(maker)) {
            chaosActions.Leave(maker);
            return;
        }
        oldSeq = maker.shapeSeq;
        if (chaosBase.step >= maker.shapeSeq)
            maker.shapeSeq += 5760;
        else
            maker.shapeSeq -= chaosBase.step;
        if (((oldSeq / 32) != (maker.shapeSeq / 32)) && !((maker.stat % 2) != 0))
            MakeWindMaker(maker);
        if (chaosBase.step >= maker.moveSeq) {
            if ((maker.shapeSeq > ChaosBase.Period * 4) && ((maker.stat % 2) != 0)) {
                chaosActions.GetCenter(maker, px, py);
                nvy = (short) (trigo.RND() % 1024);
                nvy -= 512;
                chaosSounds.SoundEffect(maker, createWindEffect);
                wind = chaosActions.CreateObj(Anims.DEADOBJ, (short) doWind, px.get(), py.get(), 0, 1);
                chaosActions.SetObjVXY(wind, (short) 3600, nvy);
                wind = chaosActions.CreateObj(Anims.DEADOBJ, (short) doWind, px.get(), py.get(), 0, 1);
                chaosActions.SetObjVXY(wind, (short) -3600, nvy);
            }
            maker.moveSeq += trigo.RND() % (ChaosBase.Period / 2) + ChaosBase.Period / 5;
        }
        if ((!((maker.stat % 2) != 0)) && (maker.shapeSeq > ChaosBase.Period * 4))
            Blow(maker, Runtime.withRange(EnumSet.noneOf(Anims.class), Anims.PLAYER, Anims.BONUS));
        maker.moveSeq -= chaosBase.step;
        maker.hitSubLife = 7;
        chaosActions.PlayerCollision(maker, new Runtime.FieldRef<>(maker::getHitSubLife, maker::setHitSubLife));
    }

    private final ChaosBase.MoveProc MoveWindMaker_ref = this::MoveWindMaker;

    private void MoveBubbleMaker(ChaosBase.Obj maker) {
        // VAR
        ChaosBase.Obj bubble = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short nvx = 0;
        short nvy = 0;

        if (chaosActions.OutOfScreen(maker)) {
            chaosActions.Leave(maker);
            return;
        }
        if (chaosBase.step >= maker.moveSeq) {
            chaosActions.GetCenter(maker, px, py);
            nvy = (short) (trigo.RND() % 1024);
            nvy -= 512;
            nvx = (short) (2048 + trigo.RND() % 512);
            if (maker.stat == 0) {
                nvx = (short) (nvx / 4);
                nvy = (short) (nvy / 4);
            }
            if (trigo.RND() % 2 == 0)
                nvx = (short) -nvx;
            chaosSounds.SoundEffect(maker, bubbleCreateEffect);
            bubble = chaosActions.CreateObj(Anims.DEADOBJ, (short) doBubble, px.get(), py.get(), trigo.RND() % 4 + 2, 1);
            chaosActions.SetObjVXY(bubble, nvx, nvy);
            maker.moveSeq += trigo.RND() % (ChaosBase.Period * (9 - maker.stat * 8)) + ChaosBase.Period / 4;
        }
        maker.moveSeq -= chaosBase.step;
        maker.hitSubLife = 7;
        chaosActions.PlayerCollision(maker, new Runtime.FieldRef<>(maker::getHitSubLife, maker::setHitSubLife));
    }

    private final ChaosBase.MoveProc MoveBubbleMaker_ref = this::MoveBubbleMaker;

    private void MoveFireMaker(ChaosBase.Obj maker) {
        // VAR
        ChaosBase.Obj fire = null;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short nvx = 0;
        short nvy = 0;
        int power = 0;

        if (chaosActions.OutOfScreen(maker)) {
            chaosActions.Leave(maker);
            return;
        }
        if (chaosBase.step >= maker.moveSeq) {
            chaosActions.GetCenter(maker, px, py);
            nvx = (short) (trigo.RND() % 1024);
            nvx -= 512;
            nvy = (short) (2048 + trigo.RND() % 512);
            if (trigo.RND() % 2 == 0)
                nvy = (short) -nvy;
            power = trigo.RND() % 4 + 1;
            chaosSounds.SoundEffect(maker, createFireEffect);
            fire = chaosActions.CreateObj(Anims.WEAPON, (short) Weapon.FIRE.ordinal(), px.get(), py.get(), 0, power);
            chaosActions.SetObjVXY(fire, nvx, nvy);
            maker.moveSeq += trigo.RND() % (ChaosBase.Period / 2) + ChaosBase.Period / 4;
        }
        maker.moveSeq -= chaosBase.step;
    }

    private final ChaosBase.MoveProc MoveFireMaker_ref = this::MoveFireMaker;

    private void MoveFireWall(ChaosBase.Obj fw) {
        // VAR
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);

        if (chaosActions.OutOfScreen(fw)) {
            chaosActions.Leave(fw);
            return;
        }
        hit.set(72);
        chaosActions.PlayerCollision(fw, hit);
    }

    private final ChaosBase.MoveProc MoveFireWall_ref = this::MoveFireWall;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;

        chaosSounds.SetEffect(getAirEffect[0], chaosSounds.soundList[SoundList.wVoice.ordinal()], 418, 8363, (short) 90, (short) 4);
        chaosSounds.SetEffect(getAirEffect[1], chaosSounds.soundList[SoundList.wVoice.ordinal()], 627, 12544, (short) 90, (short) 4);
        chaosSounds.SetEffect(getAirEffect[2], chaosSounds.soundList[SoundList.wVoice.ordinal()], 836, 16726, (short) 90, (short) 4);
        chaosSounds.SetEffect(bubbleCreateEffect[0], chaosSounds.soundList[SoundList.wWhite.ordinal()], 279, 8363, (short) 40, (short) 1);
        chaosSounds.SetEffect(bubbleCreateEffect[1], chaosSounds.soundList[SoundList.wWhite.ordinal()], 318, 12545, (short) 40, (short) 1);
        chaosSounds.SetEffect(bubbleCreateEffect[2], chaosSounds.soundList[SoundList.wWhite.ordinal()], 558, 16726, (short) 40, (short) 1);
        chaosSounds.SetEffect(createFireEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 300, 4181, (short) 40, (short) 1);
        chaosSounds.SetEffect(createWindEffect[0], chaosSounds.soundList[SoundList.wWhite.ordinal()], 600, 8363, (short) 40, (short) 1);
        chaosSounds.SetEffect(dieWindEffect[0], chaosSounds.soundList[SoundList.wCrash.ordinal()], 300, 4181, (short) 80, (short) 1);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeCartoon_as_ChaosBase_ResetProc;
        attr.Make = MakeCartoon_as_ChaosBase_MakeProc;
        attr.Move = MoveCartoon_ref;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 95;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetMagnet_ref;
        attr.Make = MakeMagnet_ref;
        attr.Move = MoveMagnet_ref;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = -80;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetMagnet_ref;
        attr.Make = MakeMagnet_ref;
        attr.Move = MoveMagnet_ref;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = -80;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetMeteor_ref;
        attr.Make = MakeMeteor_ref;
        attr.Move = MoveMeteor_ref;
        attr.basicType = BasicTypes.NotBase;
        attr.inerty = 200;
        attr.priority = -75;
        attr.toKill = true;
        meteorObjAttr.copyFrom(attr);
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeSand_as_ChaosBase_ResetProc;
        attr.Make = MakeSand_as_ChaosBase_MakeProc;
        attr.Move = MoveSand_ref;
        attr.inerty = 30;
        attr.weight = 20;
        attr.charge = -100;
        attr.priority = 30;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetWind_ref;
        attr.Make = MakeWind_ref;
        attr.Move = MoveWind_ref;
        attr.inerty = 0;
        attr.weight = 40;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetBubble_ref;
        attr.Make = MakeBubble_ref;
        attr.Move = MoveBubble_ref;
        attr.inerty = 20;
        attr.weight = 5;
        attr.charge = 10;
        attr.priority = 40;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeMirror_as_ChaosBase_ResetProc;
        attr.Make = MakeMirror_as_ChaosBase_MakeProc;
        attr.Move = MoveMirror_ref;
        attr.priority = -76;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetWindMaker_ref;
        attr.Make = MakeWindMaker_ref;
        attr.Move = MoveWindMaker_ref;
        attr.priority = -1;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetBubbleMaker_ref;
        attr.Make = MakeBubbleMaker_ref;
        attr.Move = MoveBubbleMaker_ref;
        attr.priority = -1;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetFireMaker_ref;
        attr.Make = MakeFireMaker_ref;
        attr.Move = MoveFireMaker_ref;
        attr.priority = -2;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeFireWall_as_ChaosBase_ResetProc;
        attr.Make = MakeFireWall_as_ChaosBase_MakeProc;
        attr.Move = MoveFireWall_ref;
        attr.priority = -90;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetWave_ref;
        attr.Make = MakeWave_ref;
        attr.Move = MoveWave_ref;
        attr.inerty = 20;
        attr.weight = 0;
        attr.charge = 10;
        attr.priority = 75;
        attr.basicType = BasicTypes.NotBase;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.DEADOBJ.ordinal()], attr.node);
    }


    // Support

    private static ChaosDObj instance;

    public static ChaosDObj instance() {
        if (instance == null)
            new ChaosDObj(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        InitParams();
    }

    public void close() {
    }

}
