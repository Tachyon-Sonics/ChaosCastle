package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.library.Checks;
import ch.chaos.library.Memory;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosDead {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Memory memory;


    private ChaosDead() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        memory = Memory.instance();
    }


    // PROCEDURE

    private void MakeDead(ChaosBase.Obj d) {
        if (d.subKind == ChaosBase.Message)
            d.width = 0;
    }

    private final ChaosBase.MakeProc MakeDead_ref = this::MakeDead;

    private void MoveDead(ChaosBase.Obj d) {
        // VAR
        int py = 0;
        int c = 0;

        chaosActions.UpdateXY(d);
        if (d.subKind == ChaosBase.Message) {
            d.height = chaosGraphics.H.invoke(10);
            py = d.stat;
            py += chaosGraphics.backpy;
            chaosActions.SetObjXY(d, chaosGraphics.backpx, py);
            if (chaosBase.step >= d.moveSeq) {
                chaosActions.msgObj[d.shapeSeq] = null;
                chaosActions.priorities[d.shapeSeq] = 0;
                chaosBase.DisposeObj(d);
            } else {
                d.moveSeq -= chaosBase.step;
                chaosActions.priorities[d.shapeSeq] = (d.moveSeq + 255) / 256;
            }
        } else {
            c = chaosSounds.nbChannel;
            while (c > 0) {
                c--;
                if (chaosSounds.channel[c].sndObj == d)
                    return;
            }
            chaosBase.DisposeObj(d);
        }
    }

    private final ChaosBase.MoveProc MoveDead_ref = this::MoveDead;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;

        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Move = MoveDead_ref;
        attr.Make = MakeDead_ref;
        attr.basicType = BasicTypes.NotBase;
        attr.priority = 100;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.DEAD.ordinal()], attr.node);
    }


    // Support

    private static ChaosDead instance;

    public static ChaosDead instance() {
        if (instance == null)
            new ChaosDead(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        InitParams();
        InitParams();
    }

    public void close() {
    }

}
