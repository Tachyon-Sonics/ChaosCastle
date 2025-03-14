package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.library.Checks;
import ch.chaos.library.Memory;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosSmartBonus {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosPlayer chaosPlayer;
    private final Checks checks;
    private final Memory memory;


    private ChaosSmartBonus() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosPlayer = ChaosPlayer.instance();
        checks = Checks.instance();
        memory = Memory.instance();
    }


    // CONST

    public static final int sbExtraLife = 0;
    public static final int sbExtraPower = 1;


    // PROCEDURE

    private void MakeBonus(ChaosBase.Obj bonus) {
        // VAR
        short py = 0;

        bonus.hitSubLife = 1;
        if (bonus.subKind == sbExtraLife)
            py = 32;
        else
            py = 44;
        chaosActions.SetObjLoc(bonus, (short) 52, py, (short) 12, (short) 12);
        chaosActions.SetObjRect(bonus, 0, 0, 12, 12);
    }

    private final ChaosBase.ResetProc MakeBonus_as_ChaosBase_ResetProc = this::MakeBonus;
    private final ChaosBase.MakeProc MakeBonus_as_ChaosBase_MakeProc = this::MakeBonus;

    private void Life(ChaosBase.Obj player, ChaosBase.Obj bonus) {
        chaosPlayer.AddLife(player);
        chaosActions.Die(bonus);
    }

    private final ChaosActions.DoToPlayerProc Life_ref = this::Life;

    private void Power(ChaosBase.Obj player, ChaosBase.Obj bonus) {
        chaosPlayer.AddPower(player, (short) 1);
        if (chaosBase.powerCountDown > 0)
            chaosBase.powerCountDown--;
        chaosActions.Die(bonus);
    }

    private final ChaosActions.DoToPlayerProc Power_ref = this::Power;

    private void MoveBonus(ChaosBase.Obj bonus) {
        // VAR
        ChaosActions.DoToPlayerProc What = null;
        Runtime.Ref<Integer> h = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> f = new Runtime.Ref<>(0);

        if (chaosActions.OutOfScreen(bonus)) {
            chaosActions.Leave(bonus);
            return;
        }
        chaosActions.UpdateXY(bonus);
        if (chaosActions.OutOfBounds(bonus)) {
            chaosActions.Die(bonus);
            return;
        }
        chaosActions.AvoidBackground(bonus, (short) 1);
        if (bonus.subKind == sbExtraLife)
            What = Life_ref;
        else
            What = Power_ref;
        h.set(200);
        f.set(200);
        chaosActions.DoCollision(bonus, EnumSet.of(Anims.ALIEN3, Anims.ALIEN2, Anims.ALIEN1, Anims.MISSILE, Anims.DEADOBJ, Anims.MACHINE), chaosActions.Aie_ref, h, f);
        if ((h.get() < 200) || (f.get() < 200)) {
            bonus.vy = (short) -Math.abs(bonus.vy);
            if (bonus.vy < -1024)
                bonus.vy = -1024;
            bonus.dvy = bonus.vy;
        }
        chaosActions.DoToPlayer(bonus, What);
    }

    private final ChaosBase.MoveProc MoveBonus_ref = this::MoveBonus;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;

        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(109, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeBonus_as_ChaosBase_ResetProc;
        attr.Make = MakeBonus_as_ChaosBase_MakeProc;
        attr.Move = MoveBonus_ref;
        attr.charge = 4;
        attr.weight = 32;
        attr.basicType = BasicTypes.Bonus;
        attr.dieStone = 12;
        attr.dieStStyle = ChaosBase.gravityStyle;
        attr.dieSKCount = 1;
        attr.dieStKinds = EnumSet.of(Stones.stCBOX);
        attr.priority = 90;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.SMARTBONUS.ordinal()], attr.node);
    }


    // Support

    private static ChaosSmartBonus instance;

    public static ChaosSmartBonus instance() {
        if (instance == null)
            new ChaosSmartBonus(); // will set 'instance'
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
