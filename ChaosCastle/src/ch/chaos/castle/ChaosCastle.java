package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.GameStat;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.library.Checks;
import ch.chaos.library.Clock;
import ch.chaos.library.Dialogs;
import ch.chaos.library.Files;
import ch.chaos.library.Graphics;
import ch.chaos.library.Input;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Menus;
import ch.chaos.library.Registration;
import ch.chaos.library.Sounds;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;

/*
 * - [ok] background graphics scaling
 *   - [ok] assume continuous if side by side
 *   - [ok] boost isolated pixels (stars are currently not sparkling)
 *   - [no] multicore
 * - [ok] Incorporate https://github.com/VincenzoLaSpesa/hqxcli-java (2 - 4) or https://github.com/stanio/xbrz-java (2 - 6)
 * - [ok] messages incorrect, the last one gets sometimes repeated
 * - [ok] graphics modes?
 * - [ok] use two buffers and separate repaint thread
 * - [ok] Rectangular Turret: why firing so slowly?
 * - [ok] numpad keys
 * - [later] One of Fire's flames is dirty @SCALE=4
 * - [ok] Joypad support
 * - [ok] Full screen mode, incl. dialogs
 * - [ok] System tray icon, in case of hide
 * - [ok] Smoother scrolling (player is not fixed, but should be)
 * - [ok] Allow more explosions (eg Autumn)
 */
public class ChaosCastle {

    // Imports
    private final ChaosActions chaosActions = ChaosActions.instance();
    private final ChaosBase chaosBase = ChaosBase.instance();
    private final ChaosPlayer chaosPlayer = ChaosPlayer.instance();
    private final ChaosScreens chaosScreens = ChaosScreens.instance();
    private final Checks checks = Checks.instance();
    private final Memory memory = Memory.instance();


    // PROCEDURE

    private void InitPlayVars() {
        // VAR
        BasicTypes b = BasicTypes.NotBase;

        chaosBase.gunFiring = false;
        chaosBase.invinsibility = 0;
        chaosBase.sleeper = 0;
        chaosBase.maxPower = 0;
        chaosBase.freeFire = 0;
        chaosBase.magnet = 0;
        chaosBase.air = ChaosBase.Period * 60;
        chaosBase.doubleSpeed = 0;
        chaosBase.hurryUp = 0;
        chaosBase.screenInverted = 0;
        chaosBase.noMissile = 0;
        chaosBase.playerPower = 36;
        for (int _b = BasicTypes.Bonus.ordinal(); _b <= BasicTypes.Animal.ordinal(); _b++) {
            b = BasicTypes.values()[_b];
            chaosBase.basics[b.ordinal() - BasicTypes.Bonus.ordinal()].total = 0;
            chaosBase.basics[b.ordinal() - BasicTypes.Bonus.ordinal()].done = 0;
        }
        chaosBase.shoot.total = 0;
        chaosBase.shoot.done = 0;
        chaosBase.weaponSelected = false;
        chaosBase.bombActive = false;
        chaosBase.lastJoy = new Runtime.RangeSet(Memory.SET16_r);
    }

    private void InitVars() {
        // VAR
        Weapon w = Weapon.GUN;
        Zone z = Zone.Chaos;

        for (int _w = 0; _w < Weapon.values().length; _w++) {
            w = Weapon.values()[_w];
            { // WITH
                ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
                _weaponAttr.power = 0;
                _weaponAttr.nbBullet = 0;
                _weaponAttr.nbBomb = 0;
            }
        }
        { // WITH
            ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[Weapon.GUN.ordinal()];
            _weaponAttr.power = 1;
            _weaponAttr.nbBullet = 99;
        }
        chaosBase.powerCountDown = 16;
        chaosBase.difficulty = 1;
        chaosBase.snow = false;
        chaosBase.water = false;
        chaosBase.pLife = 7;
        chaosBase.nbDollar = 0;
        chaosBase.nbSterling = 0;
        chaosBase.zone = Zone.Chaos;
        for (int _z = 0; _z < Zone.values().length; _z++) {
            z = Zone.values()[_z];
            chaosBase.level[z.ordinal()] = 1;
        }
        chaosBase.specialStage = 0;
        chaosBase.stages = 5;
        chaosBase.addpt = 0;
        chaosBase.score = 0;
        setupCheat(); // FIXME remove
        InitPlayVars();
    }
    
    void setupCheat() {
        chaosBase.nbDollar = 200;
        chaosBase.nbSterling = 200;
        chaosBase.zone = Zone.Castle;
        chaosBase.level[Zone.Chaos.ordinal()] = 20;
        chaosBase.level[Zone.Castle.ordinal()] = 21;
        chaosBase.level[Zone.Family.ordinal()] = 5;
        chaosBase.level[Zone.Special.ordinal()] = 4;
        chaosBase.specialStage = 5; // 5 bonus levels
        chaosBase.stages = 0; // PMM active
        chaosBase.difficulty = 7;
        
        for (int _w = 0; _w < Weapon.values().length; _w++) {
            Weapon w = Weapon.values()[_w];
            { // WITH
                ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
                _weaponAttr.power = 4;
                _weaponAttr.nbBullet = 50;
                _weaponAttr.nbBomb = 5;
            }
        }
    }

    private void InitCold() {
        // VAR
        Weapon w = Weapon.GUN;

        for (int _w = 0; _w < Weapon.values().length; _w++) {
            w = Weapon.values()[_w];
            chaosBase.weaponKey = Runtime.setChar(chaosBase.weaponKey, w.ordinal(), ((char) 0));
        }
        InitVars();
    }

    private void Play() {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        short maxl = 0;
        Anims a = Anims.PLAYER;

        while ((chaosBase.gameStat != GameStat.Gameover) && (chaosBase.gameStat != GameStat.Break)) {
            chaosBase.FlushAllObjs();
            chaosScreens.ShopScreen();
            InitPlayVars();
            if ((chaosBase.gameStat != GameStat.Break) && (chaosBase.gameStat != GameStat.Gameover)) {
                chaosScreens.MakingScreen();
                checks.Warn(memory.Empty(chaosBase.animList[Anims.PLAYER.ordinal()]), Runtime.castToRef("Internal error", String.class), Runtime.castToRef("Level without player", String.class));
            }
            if (chaosBase.gameStat == GameStat.Playing) {
                chaosActions.HotInit();
                while (chaosBase.gameStat == GameStat.Playing) {
                    if (memory.Empty(chaosBase.animList[Anims.PLAYER.ordinal()]))
                        chaosBase.gameStat = GameStat.Finish;
                    for (int _a = 0; _a < Anims.values().length; _a++) {
                        a = Anims.values()[_a];
                        obj = (ChaosBase.Obj) memory.First(chaosBase.animList[a.ordinal()]);
                        tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[a.ordinal()]);
                        while ((obj != tail) && (obj.attr != null)) {
                            chaosBase.nextObj = (ChaosBase.Obj) memory.Next(obj.animNode);
                            obj.attr.Move.invoke(obj);
                            obj = chaosBase.nextObj;
                        }
                        chaosPlayer.CheckStick();
                    }
                    chaosActions.NextFrame();
                }
                chaosActions.HotFlush();
                chaosScreens.StatisticScreen();
            }
            if (chaosBase.gameStat != GameStat.Gameover) {
                if (chaosBase.zone == Zone.Chaos)
                    maxl = 100;
                else if (chaosBase.zone == Zone.Castle)
                    maxl = 21; // [NEW LEVELS]
                else if (chaosBase.zone == Zone.Family)
                    maxl = 10;
                else
                    maxl = 24;
                chaosBase.level[chaosBase.zone.ordinal()]++;
                if (chaosBase.level[chaosBase.zone.ordinal()] > maxl)
                    chaosBase.level[chaosBase.zone.ordinal()] = 1;
            } else {
                chaosScreens.GameOverScreen();
            }
        }
    }


    // Life Cycle

    private void begin() {
        Memory.instance().begin();
        Checks.instance().begin();
        Files.instance().begin();
        Dialogs.instance().begin();
        Registration.instance().begin();
        ChaosBase.instance().begin();
        Clock.instance().begin();
        Trigo.instance().begin();
        Languages.instance().begin();
        Input.instance().begin();
        Graphics.instance().begin();
        ChaosGraphics.instance().begin();
        ChaosActions.instance().begin();
        Sounds.instance().begin();
        ChaosSounds.instance().begin();
        Menus.instance().begin();
        ChaosImages.instance().begin();
        ChaosInterface.instance().begin();
        ChaosWeapon.instance().begin();
        ChaosPlayer.instance().begin();
        ChaosBonus.instance().begin();
        ChaosSmartBonus.instance().begin();
        ChaosAlien.instance().begin();
        ChaosDObj.instance().begin();
        ChaosMissile.instance().begin();
        ChaosCreator.instance().begin();
        ChaosMachine.instance().begin();
        ChaosObjects.instance().begin();
        ChaosGenerator.instance().begin();
        ChaosFire.instance().begin();
        ChaosBoss.instance().begin();
        Chaos1Zone.instance().begin();
        Chaos2Zone.instance().begin();
        ChaosDual.instance().begin();
        ChaosDead.instance().begin();
        ChaosStone.instance().begin();
        ChaosLevels.instance().begin();
        ChaosScreens.instance().begin();

        InitCold();
        chaosScreens.TitleScreen();
        do {
            InitVars();
            chaosScreens.StartScreen();
            Play();
        } while (chaosBase.gameStat != GameStat.Break);
        checks.Terminate();
    }

    private void close() {
        ChaosScreens.instance().close();
        ChaosLevels.instance().close();
        ChaosStone.instance().close();
        ChaosDead.instance().close();
        ChaosDual.instance().close();
        Chaos2Zone.instance().close();
        Chaos1Zone.instance().close();
        ChaosBoss.instance().close();
        ChaosFire.instance().close();
        ChaosGenerator.instance().close();
        ChaosObjects.instance().close();
        ChaosMachine.instance().close();
        ChaosCreator.instance().close();
        ChaosMissile.instance().close();
        ChaosDObj.instance().close();
        ChaosAlien.instance().close();
        ChaosSmartBonus.instance().close();
        ChaosBonus.instance().close();
        ChaosPlayer.instance().close();
        ChaosWeapon.instance().close();
        ChaosInterface.instance().close();
        ChaosImages.instance().close();
        Menus.instance().close();
        ChaosSounds.instance().close();
        Sounds.instance().close();
        ChaosActions.instance().close();
        ChaosGraphics.instance().close();
        Graphics.instance().close();
        Input.instance().close();
        Languages.instance().close();
        Trigo.instance().close();
        Clock.instance().close();
        ChaosBase.instance().close();
        Registration.instance().close();
        Dialogs.instance().close();
        Files.instance().close();
        Checks.instance().close();
        Memory.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        ChaosCastle instance = new ChaosCastle();
        try {
            instance.begin();
        } catch (HaltException ex) {
            // Normal termination
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            instance.close();
        }
    }

}
