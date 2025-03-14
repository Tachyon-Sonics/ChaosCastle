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


public class ChaosStone {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosStone() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // VAR

    private ChaosSounds.Effect[] stoneEffect = Runtime.initArray(new ChaosSounds.Effect[1]);


    public ChaosSounds.Effect[] getStoneEffect() {
        return this.stoneEffect;
    }

    public void setStoneEffect(ChaosSounds.Effect[] stoneEffect) {
        this.stoneEffect = stoneEffect;
    }


    // PROCEDURE

    private void MakeStone0_Set(short nl, short nt, short nr, short nb, /* VAR */ Runtime.IRef<Short> l, /* VAR */ Runtime.IRef<Short> t, /* VAR */ Runtime.IRef<Short> r, /* VAR */ Runtime.IRef<Short> b) {
        l.set((short) (nl + 128));
        r.set((short) (nr + 128));
        t.set(nt);
        b.set(nb);
    }

    private void MakeStone0(ChaosBase.Obj stone) {
        // VAR
        Runtime.Ref<Short> l = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> t = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> r = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> b = new Runtime.Ref<>((short) 0);

        switch (stone.stat) {
            case 0 -> MakeStone0_Set((short) 0, (short) 0, (short) 5, (short) 5, l, t, r, b);
            case 1 -> MakeStone0_Set((short) 0, (short) 20, (short) 6, (short) 26, l, t, r, b);
            case 2 -> MakeStone0_Set((short) 10, (short) 16, (short) 12, (short) 18, l, t, r, b);
            case 3 -> MakeStone0_Set((short) 6, (short) 20, (short) 12, (short) 26, l, t, r, b);
            case 4 -> MakeStone0_Set((short) 12, (short) 20, (short) 18, (short) 26, l, t, r, b);
            case 5 -> MakeStone0_Set((short) 10, (short) 0, (short) 17, (short) 7, l, t, r, b);
            case 6 -> MakeStone0_Set((short) 10, (short) 8, (short) 18, (short) 16, l, t, r, b);
            case 7 -> MakeStone0_Set((short) 6, (short) 26, (short) 9, (short) 29, l, t, r, b);
            case 8 -> MakeStone0_Set((short) -88, (short) 56, (short) -76, (short) 68, l, t, r, b);
            case 9 -> MakeStone0_Set((short) -76, (short) 56, (short) -64, (short) 68, l, t, r, b);
            case 10 -> MakeStone0_Set((short) 58, (short) 26, (short) 60, (short) 28, l, t, r, b);
            case 11 -> MakeStone0_Set((short) 58, (short) 24, (short) 59, (short) 25, l, t, r, b);
            case 12 -> MakeStone0_Set((short) 18, (short) 24, (short) 26, (short) 32, l, t, r, b);
            case 13 -> MakeStone0_Set((short) 42, (short) 24, (short) 50, (short) 32, l, t, r, b);
            case 14 -> MakeStone0_Set((short) 60, (short) 0, (short) 72, (short) 12, l, t, r, b);
            case 15 -> MakeStone0_Set((short) 30, (short) 0, (short) 38, (short) 8, l, t, r, b);
            default -> throw new RuntimeException("Unhandled CASE value " + stone.stat);
        }
        r.dec(l.get());
        b.dec(t.get());
        chaosActions.SetObjLoc(stone, l.get(), t.get(), r.get(), b.get());
        chaosActions.SetObjRect(stone, 1, 1, r.get() - 1, b.get() - 1);
    }

    private final ChaosBase.MakeProc MakeStone0_ref = this::MakeStone0;

    private void MakeStone1(ChaosBase.Obj stone) {
        // VAR
        short py = 0;
        int cnt = 0;

        cnt = 18;
        py = 0;
        while (cnt > stone.shapeSeq) {
            py += cnt;
            cnt -= 3;
        }
        chaosActions.SetObjLoc(stone, (short) 224, py, (short) cnt, (short) cnt);
    }

    private final ChaosBase.MakeProc MakeStone1_ref = this::MakeStone1;

    private void ResetStone0(ChaosBase.Obj stone) {
        stone.hitSubLife = 1;
        stone.shapeSeq = 0;
        stone.moveSeq = ChaosBase.Period / 20;
        stone.life = ChaosBase.Period + trigo.RND() % (ChaosBase.Period * 3);
        if (stone.stat == 15) {
            stone.moveSeq = (ChaosBase.Period / 4) + trigo.RND() % (ChaosBase.Period / 4);
            stone.life = stone.moveSeq * 8 + trigo.RND() % (ChaosBase.Period / 2);
        } else if (stone.stat == 14) {
            stone.moveSeq = (ChaosBase.Period / 4) + trigo.RND() % (ChaosBase.Period / 4);
            stone.life = stone.moveSeq * 5 + trigo.RND() % (ChaosBase.Period / 2);
        } else if (stone.stat >= 12) {
            stone.moveSeq = (ChaosBase.Period / 4) + trigo.RND() % (ChaosBase.Period / 4);
            stone.life = stone.moveSeq * 3 + trigo.RND() % (ChaosBase.Period / 3);
        } else if (stone.stat >= 10) {
            stone.life = trigo.RND() % (ChaosBase.Period * 3);
            stone.shapeSeq = trigo.RND() % 3;
        } else {
            stone.shapeSeq = trigo.RND() % 2;
        }
        MakeStone0(stone);
    }

    private final ChaosBase.ResetProc ResetStone0_ref = this::ResetStone0;

    private void ResetStone1(ChaosBase.Obj stone) {
        stone.hitSubLife = 1;
        stone.life = ChaosBase.Period / 5;
        MakeStone1(stone);
    }

    private final ChaosBase.ResetProc ResetStone1_ref = this::ResetStone1;

    private void MoveStone0_Switch(int os, int ns, short nx, short ny, ChaosBase.Obj stone, /* VAR */ Runtime.IRef<Boolean> switched) {
        if (!switched.get() && (os == stone.shapeSeq)) {
            switched.set(true);
            stone.shapeSeq = ns;
            chaosActions.SetObjPos(stone, (short) (nx + 128), ny);
        }
    }

    private void MoveStone0(ChaosBase.Obj stone) {
        // VAR
        Runtime.Ref<Boolean> switched = new Runtime.Ref<>(false);
        boolean inbg = false;

        chaosActions.UpdateXY(stone);
        inbg = chaosActions.InBackground(stone);
        if ((stone.stat < 10) && inbg) {
            chaosSounds.SoundEffect(stone, stoneEffect);
            chaosActions.Die(stone);
            return;
        } else if (chaosActions.OutOfScreen(stone) || (stone.life < chaosBase.step) || chaosActions.OutOfBounds(stone)) {
            chaosActions.Die(stone);
            return;
        }
        stone.life -= chaosBase.step;
        if (chaosBase.step > stone.moveSeq) {
            switched.set(false);
            switch (stone.stat) {
                case 0 -> {
                    MoveStone0_Switch(0, 1, (short) 0, (short) 5, stone, switched);
                    MoveStone0_Switch(1, 0, (short) 0, (short) 0, stone, switched);
                }
                case 1 -> {
                    MoveStone0_Switch(0, 1, (short) 0, (short) 26, stone, switched);
                    MoveStone0_Switch(1, 0, (short) 0, (short) 20, stone, switched);
                }
                case 2 -> {
                    MoveStone0_Switch(0, 1, (short) 14, (short) 16, stone, switched);
                    MoveStone0_Switch(1, 0, (short) 10, (short) 16, stone, switched);
                }
                case 7 -> {
                    MoveStone0_Switch(0, 1, (short) 12, (short) 26, stone, switched);
                    MoveStone0_Switch(1, 0, (short) 6, (short) 26, stone, switched);
                }
                case 8 -> {
                    MoveStone0_Switch(0, 1, (short) -88, (short) 68, stone, switched);
                    MoveStone0_Switch(1, 0, (short) -88, (short) 56, stone, switched);
                }
                case 9 -> {
                    MoveStone0_Switch(0, 1, (short) -76, (short) 68, stone, switched);
                    MoveStone0_Switch(1, 0, (short) -76, (short) 56, stone, switched);
                }
                case 10 -> {
                    MoveStone0_Switch(0, 1, (short) 58, (short) 28, stone, switched);
                    MoveStone0_Switch(1, 2, (short) 58, (short) 30, stone, switched);
                    MoveStone0_Switch(2, 0, (short) 58, (short) 26, stone, switched);
                }
                case 11 -> {
                    MoveStone0_Switch(0, 1, (short) 58, (short) 25, stone, switched);
                    MoveStone0_Switch(1, 2, (short) 59, (short) 24, stone, switched);
                    MoveStone0_Switch(2, 3, (short) 59, (short) 25, stone, switched);
                    MoveStone0_Switch(3, 0, (short) 58, (short) 24, stone, switched);
                }
                case 12 -> {
                    MoveStone0_Switch(0, 1, (short) 26, (short) 24, stone, switched);
                    MoveStone0_Switch(1, 2, (short) 34, (short) 24, stone, switched);
                }
                case 13 -> {
                    MoveStone0_Switch(0, 1, (short) 50, (short) 24, stone, switched);
                    MoveStone0_Switch(1, 2, (short) 52, (short) 16, stone, switched);
                }
                case 14 -> {
                    MoveStone0_Switch(0, 1, (short) 72, (short) 0, stone, switched);
                    MoveStone0_Switch(1, 2, (short) 60, (short) 12, stone, switched);
                    MoveStone0_Switch(2, 3, (short) 72, (short) 12, stone, switched);
                    MoveStone0_Switch(3, 4, (short) 84, (short) 0, stone, switched);
                }
                case 15 -> {
                    MoveStone0_Switch(0, 1, (short) 38, (short) 0, stone, switched);
                    MoveStone0_Switch(1, 2, (short) 46, (short) 0, stone, switched);
                    MoveStone0_Switch(2, 3, (short) 30, (short) 8, stone, switched);
                    MoveStone0_Switch(3, 4, (short) 38, (short) 8, stone, switched);
                    MoveStone0_Switch(4, 5, (short) 46, (short) 8, stone, switched);
                    MoveStone0_Switch(5, 6, (short) 30, (short) 16, stone, switched);
                    MoveStone0_Switch(6, 7, (short) 38, (short) 16, stone, switched);
                }
                default -> {
                }
            }
            if (stone.stat >= 12)
                stone.moveSeq = (ChaosBase.Period / 4);
            else
                stone.moveSeq = (ChaosBase.Period / 20);
        } else {
            stone.moveSeq -= chaosBase.step;
        }
    }

    private final ChaosBase.MoveProc MoveStone0_ref = this::MoveStone0;

    private void MoveStone1(ChaosBase.Obj stone) {
        chaosActions.UpdateXY(stone);
        if (chaosBase.step > stone.life) {
            if (stone.shapeSeq == stone.moveSeq)
                stone.moveSeq = 0;
            if (stone.shapeSeq < stone.moveSeq)
                stone.shapeSeq += 3;
            else
                stone.shapeSeq -= 3;
            if (stone.shapeSeq == 0) {
                chaosActions.Die(stone);
                return;
            }
            MakeStone1(stone);
            if (chaosBase.step <= ChaosBase.Period / 5)
                stone.life += ChaosBase.Period / 5 - chaosBase.step;
        } else {
            stone.life -= chaosBase.step;
        }
    }

    private final ChaosBase.MoveProc MoveStone1_ref = this::MoveStone1;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;

        chaosSounds.SetEffect(stoneEffect[0], chaosSounds.soundList[SoundList.sGun.ordinal()], 0, 0, (short) 12, (short) 1);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetStone0_ref;
        attr.Make = MakeStone0_ref;
        attr.Move = MoveStone0_ref;
        attr.charge = 96;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 80;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.STONE.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetStone1_ref;
        attr.Make = MakeStone1_ref;
        attr.Move = MoveStone1_ref;
        attr.charge = 50;
        attr.inerty = 12;
        attr.dieStKinds = EnumSet.of(Stones.stFOG1, Stones.stFLAME2);
        attr.dieSKCount = 2;
        attr.dieStone = 5;
        attr.dieStStyle = ChaosBase.slowStyle;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 70;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.STONE.ordinal()], attr.node);
    }


    // Support

    private static ChaosStone instance;

    public static ChaosStone instance() {
        if (instance == null)
            new ChaosStone(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        InitParams();
    }

    public void close() {
    }

}
