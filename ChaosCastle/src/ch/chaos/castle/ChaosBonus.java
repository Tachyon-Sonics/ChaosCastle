package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.GameStat;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosBonus {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosPlayer chaosPlayer;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Languages languages;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosBonus() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosPlayer = ChaosPlayer.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // CONST

    public static final int Money = 0;
    public static final int TimedBonus = 1;
    public static final int BonusLevel = 2;
    public static final int tbDBSpeed = 0;
    public static final int tbSGSpeed = 1;
    public static final int tbMagnet = 2;
    public static final int tbInvinsibility = 3;
    public static final int tbSleeper = 4;
    public static final int tbBullet = 5;
    public static final int tbBonusLevel = 6;
    public static final int tbHospital = 7;
    public static final int tbFreeFire = 8;
    public static final int tbMaxPower = 9;
    public static final int tbNoMissile = 10;
    public static final int tbDifficulty = 11;
    public static final int tbExit = 12;
    public static final int tbBomb = 13;
    public static final int tbHelp = 14;


    // TYPE

    public static enum Moneys {
        m1,
        m2,
        m3,
        m5,
        m10,
        st;
    }


    // VAR

    private ChaosSounds.Effect[] sleeperEffectL = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] sleeperEffectR = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] blvEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] magnetEffect = Runtime.initArray(new ChaosSounds.Effect[9]);
    private ChaosSounds.Effect[] invEffectL = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] invEffectR = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] diffEffect = Runtime.initArray(new ChaosSounds.Effect[4]);
    private ChaosSounds.Effect[] ffEffectL = Runtime.initArray(new ChaosSounds.Effect[2]);
    private ChaosSounds.Effect[] ffEffectR = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] sgSpeedEffectL = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] sgSpeedEffectR = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] dbSpeedEffectL = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] dbSpeedEffectR = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] hospitalEffect = Runtime.initArray(new ChaosSounds.Effect[13]);
    private ChaosSounds.Effect[] maxPowerEffect = Runtime.initArray(new ChaosSounds.Effect[4]);


    public ChaosSounds.Effect[] getSleeperEffectL() {
        return this.sleeperEffectL;
    }

    public void setSleeperEffectL(ChaosSounds.Effect[] sleeperEffectL) {
        this.sleeperEffectL = sleeperEffectL;
    }

    public ChaosSounds.Effect[] getSleeperEffectR() {
        return this.sleeperEffectR;
    }

    public void setSleeperEffectR(ChaosSounds.Effect[] sleeperEffectR) {
        this.sleeperEffectR = sleeperEffectR;
    }

    public ChaosSounds.Effect[] getBlvEffect() {
        return this.blvEffect;
    }

    public void setBlvEffect(ChaosSounds.Effect[] blvEffect) {
        this.blvEffect = blvEffect;
    }

    public ChaosSounds.Effect[] getMagnetEffect() {
        return this.magnetEffect;
    }

    public void setMagnetEffect(ChaosSounds.Effect[] magnetEffect) {
        this.magnetEffect = magnetEffect;
    }

    public ChaosSounds.Effect[] getInvEffectL() {
        return this.invEffectL;
    }

    public void setInvEffectL(ChaosSounds.Effect[] invEffectL) {
        this.invEffectL = invEffectL;
    }

    public ChaosSounds.Effect[] getInvEffectR() {
        return this.invEffectR;
    }

    public void setInvEffectR(ChaosSounds.Effect[] invEffectR) {
        this.invEffectR = invEffectR;
    }

    public ChaosSounds.Effect[] getDiffEffect() {
        return this.diffEffect;
    }

    public void setDiffEffect(ChaosSounds.Effect[] diffEffect) {
        this.diffEffect = diffEffect;
    }

    public ChaosSounds.Effect[] getFfEffectL() {
        return this.ffEffectL;
    }

    public void setFfEffectL(ChaosSounds.Effect[] ffEffectL) {
        this.ffEffectL = ffEffectL;
    }

    public ChaosSounds.Effect[] getFfEffectR() {
        return this.ffEffectR;
    }

    public void setFfEffectR(ChaosSounds.Effect[] ffEffectR) {
        this.ffEffectR = ffEffectR;
    }

    public ChaosSounds.Effect[] getSgSpeedEffectL() {
        return this.sgSpeedEffectL;
    }

    public void setSgSpeedEffectL(ChaosSounds.Effect[] sgSpeedEffectL) {
        this.sgSpeedEffectL = sgSpeedEffectL;
    }

    public ChaosSounds.Effect[] getSgSpeedEffectR() {
        return this.sgSpeedEffectR;
    }

    public void setSgSpeedEffectR(ChaosSounds.Effect[] sgSpeedEffectR) {
        this.sgSpeedEffectR = sgSpeedEffectR;
    }

    public ChaosSounds.Effect[] getDbSpeedEffectL() {
        return this.dbSpeedEffectL;
    }

    public void setDbSpeedEffectL(ChaosSounds.Effect[] dbSpeedEffectL) {
        this.dbSpeedEffectL = dbSpeedEffectL;
    }

    public ChaosSounds.Effect[] getDbSpeedEffectR() {
        return this.dbSpeedEffectR;
    }

    public void setDbSpeedEffectR(ChaosSounds.Effect[] dbSpeedEffectR) {
        this.dbSpeedEffectR = dbSpeedEffectR;
    }

    public ChaosSounds.Effect[] getHospitalEffect() {
        return this.hospitalEffect;
    }

    public void setHospitalEffect(ChaosSounds.Effect[] hospitalEffect) {
        this.hospitalEffect = hospitalEffect;
    }

    public ChaosSounds.Effect[] getMaxPowerEffect() {
        return this.maxPowerEffect;
    }

    public void setMaxPowerEffect(ChaosSounds.Effect[] maxPowerEffect) {
        this.maxPowerEffect = maxPowerEffect;
    }


    // PROCEDURE

    public void BoumMoney(ChaosBase.Obj obj, EnumSet<Moneys> coins, int ns, int nb) {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int vx = 0;
        int vy = 0;
        ChaosBase.Obj money = null;
        int rnd = 0;
        Moneys kind = Moneys.m1;

        if (chaosBase.zone == Zone.Family)
            return;
        chaosActions.GetCenter(obj, px, py);
        while (nb > 0) {
            nb--;
            rnd = trigo.RND() % ns;
            kind = Moneys.m1 /* MIN(Moneys) */;
            while (true) {
                while (!coins.contains(kind)) {
                    kind = Runtime.next(kind);
                }
                if (rnd == 0)
                    break;
                kind = Runtime.next(kind);
                rnd--;
            }
            vx = trigo.RND() % 1024;
            vx -= 512;
            vx += obj.vx / 4;
            vy = trigo.RND() % 1024;
            vy -= 896;
            vy += obj.vy / 4;
            money = chaosActions.CreateObj(Anims.BONUS, Money, px.get(), py.get(), kind.ordinal(), 1);
            chaosActions.SetObjVXY(money, vx, vy);
            if (chaosBase.zone != Zone.Castle) {
                chaosActions.SetObjAXY(money, 0, 16);
            } else {
                money.dvx = 0;
                money.dvy = 0;
            }
        }
    }

    private void MakeMoney(ChaosBase.Obj money) {
        // VAR
        int px = 0;
        int py = 0;
        int ps = 0;

        money.hitSubLife = 1;
        if (money.stat > 4) {
            px = 212;
            py = 12;
            ps = 12;
        } else {
            px = 246;
            py = money.stat * 10;
            ps = 10;
        }
        chaosActions.SetObjLoc(money, px, py, ps, ps);
        chaosActions.SetObjRect(money, 0, 0, ps, ps);
    }

    private final ChaosBase.ResetProc MakeMoney_as_ChaosBase_ResetProc = this::MakeMoney;
    private final ChaosBase.MakeProc MakeMoney_as_ChaosBase_MakeProc = this::MakeMoney;

    private void MakeBonus(ChaosBase.Obj bonus) {
        // VAR
        int px = 0;
        int py = 0;
        int pw = 0;
        int ph = 0;

        pw = 12;
        ph = 12;
        py = 32;
        bonus.hitSubLife = 1;
        switch (bonus.stat) {
            case tbDBSpeed -> {
                px = 0;
                pw = 14;
                ph = 24;
            }
            case tbSGSpeed -> {
                px = 14;
                pw = 14;
                ph = 24;
            }
            case tbMagnet -> {
                px = 28;
            }
            case tbInvinsibility -> {
                px = 28;
                py = 44;
            }
            case tbSleeper -> {
                px = 40;
            }
            case tbBullet -> {
                px = 40;
                py = 44;
            }
            case tbBonusLevel -> {
                px = 64;
            }
            case tbHospital -> {
                px = 64;
                py = 44;
            }
            case tbFreeFire -> {
                px = 64;
                py = 56;
            }
            case tbMaxPower -> {
                px = 64;
                py = 68;
            }
            case tbNoMissile -> {
                px = 212;
                py = 132;
            }
            case tbDifficulty -> {
                px = 64;
                py = 80;
            }
            case tbExit -> {
                px = 192;
                py = 24;
                pw = 32;
                ph = 32;
            }
            case tbBomb -> {
                px = 224;
                py = 76;
                pw = 8;
                ph = 8;
            }
            case tbHelp -> {
                px = 240;
                py = 256;
                pw = 16;
                ph = 16;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + bonus.stat);
        }
        chaosActions.SetObjLoc(bonus, px, py, pw, ph);
        chaosActions.SetObjRect(bonus, 0, 0, pw, ph);
    }

    private final ChaosBase.ResetProc MakeBonus_as_ChaosBase_ResetProc = this::MakeBonus;
    private final ChaosBase.MakeProc MakeBonus_as_ChaosBase_MakeProc = this::MakeBonus;

    private void GiveToPlayer(ChaosBase.Obj p, ChaosBase.Obj m) {
        // VAR
        int dollar = 0;
        int sterling = 0;

        dollar = 0;
        sterling = 0;
        switch (m.stat) {
            case 0 -> dollar = 1;
            case 1 -> dollar = 2;
            case 2 -> dollar = 3;
            case 3 -> dollar = 5;
            case 4 -> dollar = 10;
            case 5 -> sterling = 1;
            default -> throw new RuntimeException("Unhandled CASE value " + m.stat);
        }
        chaosPlayer.AddMoney(p, dollar, sterling);
        chaosActions.Die(m);
    }

    private final ChaosActions.DoToPlayerProc GiveToPlayer_ref = this::GiveToPlayer;

    private void MoveMoney(ChaosBase.Obj money) {
        if (chaosActions.OutOfScreen(money)) {
            if (chaosActions.OutOfBounds(money))
                chaosActions.Die(money);
            else
                chaosActions.Leave(money);
            return;
        }
        if (chaosActions.OutOfBounds(money) && (money.y > 0)) {
            chaosActions.Die(money);
            return;
        }
        if (chaosBase.zone != Zone.Chaos)
            chaosActions.AvoidBackground(money, 2);
        chaosActions.UpdateXY(money);
        chaosActions.LimitSpeed(money, 1920);
        chaosActions.DoToPlayer(money, GiveToPlayer_ref);
    }

    private final ChaosBase.MoveProc MoveMoney_ref = this::MoveMoney;

    private void DBSpeed(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosBase.doubleSpeed = 1;
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("High speed"), String.class), ChaosActions.statPos, 2);
        chaosSounds.StereoEffect();
        chaosSounds.SoundEffect(p, dbSpeedEffectR);
        chaosSounds.SoundEffect(p, dbSpeedEffectL);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc DBSpeed_ref = this::DBSpeed;

    private void SGSpeed(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosBase.doubleSpeed = 0;
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Normal speed"), String.class), ChaosActions.statPos, 1);
        chaosSounds.StereoEffect();
        chaosSounds.SoundEffect(p, sgSpeedEffectL);
        chaosSounds.SoundEffect(p, sgSpeedEffectR);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc SGSpeed_ref = this::SGSpeed;

    private void Magnet(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosBase.magnet += ChaosBase.Period * 40;
        chaosSounds.SoundEffect(p, magnetEffect);
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Magnet"), String.class), ChaosActions.statPos, 3);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc Magnet_ref = this::Magnet;

    private void Invinsibility(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosPlayer.MakeInvinsible(p, ChaosBase.Period * 30);
        chaosSounds.StereoEffect();
        chaosSounds.SoundEffect(p, invEffectR);
        chaosSounds.SoundEffect(p, invEffectL);
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Invinsibility"), String.class), ChaosActions.statPos, 3);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc Invinsibility_ref = this::Invinsibility;

    private void Sleeper(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosBase.sleeper += ChaosBase.Period * 50;
        chaosSounds.StereoEffect();
        chaosSounds.SoundEffect(p, sleeperEffectL);
        chaosSounds.SoundEffect(p, sleeperEffectR);
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("* Soporific *"), String.class), ChaosActions.statPos, 3);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc Sleeper_ref = this::Sleeper;

    private void Bullet(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosPlayer.AddWeapon(p, 40);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc Bullet_ref = this::Bullet;

    private void Bomb(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosPlayer.AddBomb(p, 1);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc Bomb_ref = this::Bomb;

    private void AddSS(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosSounds.SoundEffect(p, blvEffect);
        if (chaosBase.specialStage < 40)
            chaosBase.specialStage++;
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc AddSS_ref = this::AddSS;

    private void Hospital(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosBase.playerPower = 36;
        chaosSounds.SoundEffect(p, hospitalEffect);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc Hospital_ref = this::Hospital;

    private void FreeFire(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosBase.freeFire += ChaosBase.Period * 50;
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Free Fire"), String.class), ChaosActions.statPos, 3);
        chaosSounds.StereoEffect();
        chaosSounds.SoundEffect(p, ffEffectL);
        chaosSounds.SoundEffect(p, ffEffectR);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc FreeFire_ref = this::FreeFire;

    private void MaxPower(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosBase.maxPower += ChaosBase.Period * 40;
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Maximum Power"), String.class), ChaosActions.statPos, 3);
        chaosSounds.SoundEffect(p, maxPowerEffect);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc MaxPower_ref = this::MaxPower;

    private void NoMissile(ChaosBase.Obj p, ChaosBase.Obj o) {
        chaosBase.noMissile += ChaosBase.Period * 60;
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("No missile"), String.class), ChaosActions.statPos, 3);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc NoMissile_ref = this::NoMissile;

    private void Difficulty(ChaosBase.Obj p, ChaosBase.Obj o) {
        // VAR
        Runtime.Ref<String> str = new Runtime.Ref<>("");
        int c = 0;
        int a = 0;
        Weapon w = Weapon.GUN;

        chaosSounds.SoundEffect(p, diffEffect);
        if (chaosBase.difficulty <= 9) {
            if ((chaosBase.zone == Zone.Castle) && (chaosBase.stages == 1))
                chaosActions.NextStage();
            chaosBase.difficulty++;
            memory.CopyStr(Runtime.castToRef(languages.ADL("Current level: #"), String.class), str, Runtime.sizeOf(33, String.class));
            c = 0;
            while ((c < 30) && (Runtime.getChar(str, c) != '#')) {
                c++;
            }
            if (c < 30) {
                if (chaosBase.difficulty < 10) {
                    Runtime.setChar(str, c, (char) ('0' + chaosBase.difficulty));
                } else {
                    Runtime.setChar(str, c, 'M');
                    Runtime.setChar(str, c + 1, 'A');
                    Runtime.setChar(str, c + 2, 'X');
                    Runtime.setChar(str, c + 3, ((char) 0));
                }
            }
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Increasing difficulty"), String.class), ChaosActions.lifePos, 8);
            chaosActions.PopMessage(str, ChaosActions.actionPos, 8);
        } else {
            checks.Check(chaosBase.password, Runtime.castToRef(languages.ADL("Ok, you finished the game."), String.class), Runtime.castToRef(languages.ADL("But now, try it without the password !"), String.class));
            a = 0;
            for (int _w = 0; _w < Weapon.values().length; _w++) {
                w = Weapon.values()[_w];
                if (chaosBase.weaponAttr[w.ordinal()].power > 1)
                    a = 1;
            }
            for (int _w = 0; _w < Weapon.values().length; _w++) {
                w = Weapon.values()[_w];
                { // WITH
                    ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
                    if (_weaponAttr.power > 1) {
                        if (w == Weapon.GUN)
                            _weaponAttr.power = 1;
                        else
                            _weaponAttr.power = a;
                    }
                }
            }
            chaosBase.gameStat = GameStat.Finish;
        }
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc Difficulty_ref = this::Difficulty;

    private void Exit(ChaosBase.Obj p, ChaosBase.Obj o) {
        if ((chaosBase.gameStat == GameStat.Playing) && (chaosBase.zone != Zone.Family))
            chaosBase.gameStat = GameStat.Finish;
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc Exit_ref = this::Exit;

    private void Help(ChaosBase.Obj p, ChaosBase.Obj o) {
        // VAR
        Runtime.IRef<String> str1 = null;
        Runtime.IRef<String> str2 = null;

        str1 = null;
        str2 = null;
        if (chaosBase.zone == Zone.Castle) {
            if (chaosBase.level[Zone.Castle.ordinal()] == 1) {
                if (chaosBase.difficulty == 1) {
                    str1 = Runtime.castToRef(languages.ADL("Target:"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("Find the EXIT"), String.class);
                } else {
                    str1 = Runtime.castToRef(languages.ADL("The hospital-aliens may"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("help you if you're broken"), String.class);
                }
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 2) {
                str1 = Runtime.castToRef(languages.ADL("1st stage before PMM:"), String.class);
                str2 = Runtime.castToRef(languages.ADL("Get a score of 1000"), String.class);
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 3) {
                str1 = Runtime.castToRef(languages.ADL("Diamond-bonuses enable"), String.class);
                str2 = Runtime.castToRef(languages.ADL("bonus levels"), String.class);
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 7) {
                if (chaosBase.stages >= 4) {
                    str1 = Runtime.castToRef(languages.ADL("2nd stage before PMM:"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("Get 20 lives"), String.class);
                } else {
                    str1 = Runtime.castToRef(languages.ADL("The Mother Alien"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("doesn't like gun bombs"), String.class);
                }
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 4) {
                str1 = Runtime.castToRef(languages.ADL("The EXIT is"), String.class);
                str2 = Runtime.castToRef(languages.ADL("near the center"), String.class);
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 5) {
                if (chaosBase.difficulty == 1) {
                    str1 = Runtime.castToRef(languages.ADL("Many levels contains"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("hidden passages"), String.class);
                } else {
                    str1 = Runtime.castToRef(languages.ADL("The 1st Skull Bonus is"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("hidden in Chaos Level 100"), String.class);
                }
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 6) {
                str1 = Runtime.castToRef(languages.ADL("Only the canon near you"), String.class);
                str2 = Runtime.castToRef(languages.ADL("can destroy some items"), String.class);
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 8) {
                if (chaosBase.stages >= 3) {
                    str1 = Runtime.castToRef(languages.ADL("3rd stage before PMM:"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("Wait for five 'hurry up's"), String.class);
                } else {
                    str1 = Runtime.castToRef(languages.ADL("The cash can save you if"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("you are out of lives"), String.class);
                }
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 9) {
                str1 = Runtime.castToRef(languages.ADL("Balance yourself if you"), String.class);
                str2 = Runtime.castToRef(languages.ADL("are blocked by a magnet"), String.class);
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 11) {
                if (chaosBase.stages >= 2) {
                    str1 = Runtime.castToRef(languages.ADL("4th stage before PMM:"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("Get 100$"), String.class);
                } else if (chaosBase.weaponAttr[Weapon.GUN.ordinal()].power != 4) {
                    str1 = Runtime.castToRef(languages.ADL("Hint:"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("Increase gun's power"), String.class);
                } else {
                    str1 = Runtime.castToRef(languages.ADL("Hint: Let the Sister Alien"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("create hospital-aliens"), String.class);
                }
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 15) {
                if (chaosBase.stages >= 1) {
                    str1 = Runtime.castToRef(languages.ADL("Last stage before PMM:"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("Find the 2nd Skull Bonus"), String.class);
                } else {
                    str1 = Runtime.castToRef(languages.ADL("Aliens release a heart if"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("lives=1 and level MOD 7=0"), String.class);
                }
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 16) {
                str1 = Runtime.castToRef(languages.ADL("3rd Skull Bonus is hidden in"), String.class);
                str2 = Runtime.castToRef(languages.ADL("the zone 'Family' (last level)"), String.class);
            } else if (chaosBase.level[Zone.Castle.ordinal()] == 20) {
                if (chaosBase.difficulty <= 2) {
                    str1 = Runtime.castToRef(languages.ADL("The Skull Bonus raises"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("the game's difficulty"), String.class);
                } else {
                    str1 = Runtime.castToRef(languages.ADL("Mind bonus level 24"), String.class);
                    str2 = Runtime.castToRef(languages.ADL("hehehehehehe..."), String.class);
                }
            }
        } else if (chaosBase.zone == Zone.Family) {
            if (chaosBase.level[Zone.Family.ordinal()] == 1) {
                str1 = Runtime.castToRef(languages.ADL("After you've killed him"), String.class);
                str2 = Runtime.castToRef(languages.ADL("go near the center"), String.class);
            } else if (chaosBase.level[Zone.Family.ordinal()] == 4) {
                str1 = Runtime.castToRef(languages.ADL("Sorry, no help available"), String.class);
                str2 = Runtime.castToRef(languages.ADL("ha ha ha"), String.class);
            } else {
                str1 = Runtime.castToRef(languages.ADL("Make sure there's NOTHING"), String.class);
                str2 = Runtime.castToRef(languages.ADL("left before going on"), String.class);
            }
        } else if (chaosBase.zone == Zone.Special) {
            if (chaosBase.level[Zone.Special.ordinal()] == 24) {
                str1 = Runtime.castToRef(languages.ADL("Try to collect bonuses"), String.class);
                str2 = Runtime.castToRef(languages.ADL("rather than to kill"), String.class);
            } else {
                str1 = Runtime.castToRef(languages.ADL("The 2nd Skull Bonus is"), String.class);
                str2 = Runtime.castToRef(languages.ADL("hidden in the Labyrinth"), String.class);
            }
        }
        if (str1 != null)
            chaosActions.PopMessage(str1, ChaosActions.statPos, 8);
        if (str2 != null)
            chaosActions.PopMessage(str2, ChaosActions.moneyPos, 8);
        chaosActions.Die(o);
    }

    private final ChaosActions.DoToPlayerProc Help_ref = this::Help;

    private void MoveBonus(ChaosBase.Obj bonus) {
        // VAR
        ChaosActions.DoToPlayerProc What = null;

        if (chaosActions.OutOfScreen(bonus)) {
            chaosActions.Leave(bonus);
            return;
        }
        chaosActions.UpdateXY(bonus);
        chaosActions.AvoidBackground(bonus, 2);
        if (bonus.stat == tbDifficulty)
            chaosActions.AvoidBounds(bonus, 3);
        if (chaosActions.OutOfBounds(bonus) || (chaosActions.InBackground(bonus) && (bonus.stat != tbBomb))) {
            chaosActions.Die(bonus);
            return;
        }
        bonus.dvx = 0;
        bonus.dvy = 0;
        switch (bonus.stat) {
            case tbDBSpeed -> What = DBSpeed_ref;
            case tbSGSpeed -> What = SGSpeed_ref;
            case tbMagnet -> What = Magnet_ref;
            case tbInvinsibility -> What = Invinsibility_ref;
            case tbSleeper -> What = Sleeper_ref;
            case tbBullet -> What = Bullet_ref;
            case tbBonusLevel -> What = AddSS_ref;
            case tbHospital -> What = Hospital_ref;
            case tbFreeFire -> What = FreeFire_ref;
            case tbMaxPower -> What = MaxPower_ref;
            case tbNoMissile -> What = NoMissile_ref;
            case tbDifficulty -> What = Difficulty_ref;
            case tbExit -> What = Exit_ref;
            case tbBomb -> What = Bomb_ref;
            case tbHelp -> What = Help_ref;
            default -> throw new RuntimeException("Unhandled CASE value " + bonus.stat);
        }
        chaosActions.DoToPlayer(bonus, What);
    }

    private final ChaosBase.MoveProc MoveBonus_ref = this::MoveBonus;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;
        int c = 0;
        int d = 0;
        int v = 0;

        chaosSounds.SetEffect(sleeperEffectL[0], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 0, 4181, 80, 14);
        chaosSounds.SetEffect(sleeperEffectR[0], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 0, 4694, 80, 12);
        chaosSounds.SetEffect(blvEffect[0], chaosSounds.soundList[SoundList.wJans.ordinal()], 0, 0, 200, 13);
        chaosSounds.SetEffect(magnetEffect[0], chaosSounds.soundList[SoundList.wWhite.ordinal()], 697, 0, 8, 13);
        chaosSounds.SetEffect(magnetEffect[8], chaosSounds.nulSound, 0, 0, 8, 1);
        for (c = 1; c <= 4; c++) {
            d = 8 - c;
            v = (c + 1) * 50;
            v = (v * v) / 280;
            chaosSounds.SetEffect(magnetEffect[c], chaosSounds.nulSound, 697, 0, v, 13);
            chaosSounds.SetEffect(magnetEffect[d], chaosSounds.nulSound, 0, 0, v, c * 3 + 1);
        }
        chaosSounds.SetEffect(sgSpeedEffectL[0], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 16726, 25089, 110, 6);
        chaosSounds.SetEffect(sgSpeedEffectL[1], chaosSounds.nulSound, 1742, 20908, 110, 6);
        chaosSounds.SetEffect(sgSpeedEffectL[2], chaosSounds.nulSound, 1394, 16726, 110, 6);
        chaosSounds.SetEffect(sgSpeedEffectR[0], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 11152, 16726, 110, 4);
        chaosSounds.SetEffect(sgSpeedEffectR[1], chaosSounds.nulSound, 1565, 18774, 110, 4);
        chaosSounds.SetEffect(sgSpeedEffectR[2], chaosSounds.nulSound, 1394, 16726, 110, 4);
        chaosSounds.SetEffect(dbSpeedEffectR[0], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 11151, 16726, 220, 11);
        chaosSounds.SetEffect(dbSpeedEffectR[1], chaosSounds.nulSound, 1742, 20908, 220, 11);
        chaosSounds.SetEffect(dbSpeedEffectR[2], chaosSounds.nulSound, 2091, 25089, 220, 11);
        chaosSounds.SetEffect(dbSpeedEffectL[0], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 16726, 25089, 220, 9);
        chaosSounds.SetEffect(dbSpeedEffectL[1], chaosSounds.nulSound, 1863, 22352, 220, 9);
        chaosSounds.SetEffect(dbSpeedEffectL[2], chaosSounds.nulSound, 2091, 25089, 220, 9);
        chaosSounds.SetEffect(invEffectL[0], chaosSounds.soundList[SoundList.sGong.ordinal()], 0, 16726, 240, 12);
        chaosSounds.SetEffect(invEffectR[0], chaosSounds.soundList[SoundList.sGong.ordinal()], 0, 14901, 240, 14);
        chaosSounds.SetEffect(ffEffectL[0], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 2000, 12544, 140, 11);
        chaosSounds.SetEffect(ffEffectL[1], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 0, 12544, 100, 11);
        chaosSounds.SetEffect(ffEffectR[0], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 160, 8363, 0, 11);
        chaosSounds.SetEffect(ffEffectR[1], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 2000, 12544, 140, 11);
        chaosSounds.SetEffect(ffEffectR[2], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 0, 12544, 100, 11);
        for (c = 0; c <= 3; c++) {
            chaosSounds.SetEffect(diffEffect[c], chaosSounds.soundList[SoundList.sHa.ordinal()], 0, 0, 240 - c * 60, 15 - c * 4);
        }
        chaosSounds.SetEffect(maxPowerEffect[0], chaosSounds.soundList[SoundList.wShakuhachi.ordinal()], 929, 8363, 150, 11);
        chaosSounds.SetEffect(maxPowerEffect[1], chaosSounds.nulSound, 1595, 11163, 150, 11);
        chaosSounds.SetEffect(maxPowerEffect[2], chaosSounds.nulSound, 1043, 9387, 150, 11);
        chaosSounds.SetEffect(maxPowerEffect[3], chaosSounds.nulSound, 1790, 12530, 150, 11);
        chaosSounds.SetEffect(hospitalEffect[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 16726, 240, 14);
        chaosSounds.SetEffect(hospitalEffect[1], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1440, 17623, 30, 13);
        for (c = 2; c <= 12; c++) {
            chaosSounds.SetEffect(hospitalEffect[c], chaosSounds.nulSound, 1440, 16726 + 897 * c, (16 - c) * 2, 12);
        }
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeMoney_as_ChaosBase_ResetProc;
        attr.Make = MakeMoney_as_ChaosBase_MakeProc;
        attr.Move = MoveMoney_ref;
        attr.charge = -64;
        attr.weight = 8;
        attr.inerty = 6;
        attr.basicType = BasicTypes.Bonus;
        attr.priority = 60;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.BONUS.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeBonus_as_ChaosBase_ResetProc;
        attr.Make = MakeBonus_as_ChaosBase_MakeProc;
        attr.Move = MoveBonus_ref;
        attr.charge = 32;
        attr.weight = 16;
        attr.inerty = 4;
        attr.dieStone = 8;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.dieSKCount = 1;
        attr.dieStKinds = EnumSet.of(Stones.stRBOX);
        attr.basicType = BasicTypes.Bonus;
        attr.priority = 75;
        attr.toKill = true;
        memory.AddTail(chaosBase.attrList[Anims.BONUS.ordinal()], attr.node);
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = MakeBonus_as_ChaosBase_ResetProc;
        attr.Make = MakeBonus_as_ChaosBase_MakeProc;
        attr.Move = MoveBonus_ref;
        attr.charge = 32;
        attr.weight = 16;
        attr.inerty = 4;
        attr.dieStone = 8;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.dieSKCount = 1;
        attr.dieStKinds = EnumSet.of(Stones.stRBOX);
        attr.basicType = BasicTypes.Bonus;
        attr.priority = 75;
        attr.toKill = false;
        memory.AddTail(chaosBase.attrList[Anims.BONUS.ordinal()], attr.node);
    }


    // Support

    private static ChaosBonus instance;

    public static ChaosBonus instance() {
        if (instance == null)
            new ChaosBonus(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        InitParams();
    }

    public void close() {
    }

}
