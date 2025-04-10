package ch.chaos.tools;

import ch.chaos.castle.Chaos1Zone;
import ch.chaos.castle.Chaos2Zone;
import ch.chaos.castle.ChaosActions;
import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.castle.ChaosBoss;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.ChaosDObj;
import ch.chaos.castle.ChaosDead;
import ch.chaos.castle.ChaosDual;
import ch.chaos.castle.ChaosFire;
import ch.chaos.castle.ChaosGenerator;
import ch.chaos.castle.ChaosGraphics;
import ch.chaos.castle.ChaosImages;
import ch.chaos.castle.ChaosInterface;
import ch.chaos.castle.ChaosLevels;
import ch.chaos.castle.ChaosMachine;
import ch.chaos.castle.ChaosMissile;
import ch.chaos.castle.ChaosObjects;
import ch.chaos.castle.ChaosPlayer;
import ch.chaos.castle.ChaosScreens;
import ch.chaos.castle.ChaosSmartBonus;
import ch.chaos.castle.ChaosSounds;
import ch.chaos.castle.ChaosStone;
import ch.chaos.castle.ChaosWeapon;
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
import ch.pitchtech.modula.runtime.Runtime;

public class GameSimulator {
    
    /*
With all skull bonuses:
*** Summary: 
Chaos: 301
Castle: 78
Family: 19
Special: 28

Without Labyrinth's skull bonus (except first one):
*** Summary: 
Chaos: 363
Castle: 121
Family: 31
Special: 46

Modified, tri -> 2, bumper -> 0

Chaos: 301
Castle: 83
Family: 14
Special: 29

Chaos: 401
Castle: 129
Family: 22
Special: 47

Modified, tri -> 2£, bumper -> 3$

Chaos: 221
Castle: 91
Family: 15
Special: 31

Chaos: 401
Castle: 137
Family: 24
Special: 51

     */
    
    private final static int NB_RUNS = 50; // # runs for averaging
    private final static boolean SKIP_SECOND_LABYRINTH_SKULL = false; // Only take labyrinth's skull bonus once
    
    private final ChaosLevels chaosLevels = ChaosLevels.instance();
    private final ChaosBase chaosBase = ChaosBase.instance();
    private final ChaosGraphics chaosGraphics = ChaosGraphics.instance();
    
    private final Graphics graphics = Graphics.instance();
    private final Memory memory = Memory.instance();
    
    private int nbDollars;
    private int nbSterlings;
    private int nbBonusLevels;
    private int nbSkulls;
    private int nbPowers;
    
    private int[] nbLevelsDone = new int[Zone.values().length];
    
    
    private void init() {
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
    }
    
    private void initVars() {
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
        
        // Graphics
        chaosGraphics.mainArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, 320, Graphics.aSIZEY, 240, Graphics.aCOLOR, 16));
        chaosGraphics.shapeArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, 256, Graphics.aSIZEY, 296, Graphics.aCOLOR, 16));
        chaosGraphics.imageArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, 256, Graphics.aSIZEY, 256, Graphics.aCOLOR, 16));
        chaosGraphics.maskArea = graphics.CreateArea((Memory.TagItem) memory.TAG4(Graphics.aSIZEX, 256, Graphics.aSIZEY, 296, Graphics.aCOLOR, 2, Graphics.aTYPE, Graphics.atMASK));
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
    
    private void playLevel() {
        // Kill all aliens to release their money
        for (Anims a : Anims.values()) {
            if (a != Anims.PLAYER && a != Anims.BONUS && a != Anims.SMARTBONUS) {
//                int nbKilled = 0;
                ChaosBase.Obj obj = (ChaosBase.Obj) memory.First(chaosBase.animList[a.ordinal()]);
                ChaosBase.Obj tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[a.ordinal()]);
                while ((obj != tail) && (obj.attr != null)) {
                    chaosBase.nextObj = (ChaosBase.Obj) memory.Next(obj.animNode);
                    if (obj.attr.Die != null) {
                        obj.life = 1;
                        obj.attr.Die.invoke(obj);
                        chaosBase.DisposeObj(obj);
//                        nbKilled++;
                    }
                    obj = chaosBase.nextObj;
                }
//                if (nbKilled > 0)
//                    System.out.println("  Killed " + nbKilled + " " + a);
            }
        }
        
        // Collect money and bonuses
        { // BONUS
            ChaosBase.Obj obj = (ChaosBase.Obj) memory.First(chaosBase.animList[Anims.BONUS.ordinal()]);
            ChaosBase.Obj tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[Anims.BONUS.ordinal()]);
            while ((obj != tail) && (obj.attr != null)) {
                if (obj.kind == Anims.BONUS && obj.subKind == ChaosBonus.Money) {
                    Moneys moneyType = Moneys.values()[obj.stat];
                    nbDollars += switch (moneyType) {
                        case m1 -> 1;
                        case m2 -> 2;
                        case m3 -> 3;
                        case m5 -> 5;
                        case m10 -> 10;
                        case st -> 0;
                    };
                    if (moneyType == Moneys.st)
                        nbSterlings++;
                } else if (obj.kind == Anims.BONUS && obj.subKind == ChaosBonus.BonusLevel && obj.stat == ChaosBonus.tbBonusLevel) {
                    nbBonusLevels++;
                } else if (obj.kind == Anims.BONUS && obj.subKind == ChaosBonus.TimedBonus && obj.stat == ChaosBonus.tbBonusLevel) {
                    nbBonusLevels++;
                } else if (obj.kind == Anims.BONUS && obj.subKind == ChaosBonus.TimedBonus && obj.stat == ChaosBonus.tbDifficulty) {
                    nbSkulls++;
                }
                obj = (ChaosBase.Obj) memory.Next(obj.animNode);
            }
        }
        
        { // SMARTBONUS
            ChaosBase.Obj obj = (ChaosBase.Obj) memory.First(chaosBase.animList[Anims.SMARTBONUS.ordinal()]);
            ChaosBase.Obj tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[Anims.SMARTBONUS.ordinal()]);
            while ((obj != tail) && (obj.attr != null)) {
                if (obj.kind == Anims.SMARTBONUS && obj.subKind == ChaosSmartBonus.sbExtraPower) {
                    nbPowers++;
                }
                obj = (ChaosBase.Obj) memory.Next(obj.animNode);
            }
        }
        
        chaosBase.FlushAllObjs();
    }

    private void collectBonus() {
        double percent = switch(chaosBase.zone) {
            case Chaos -> 0.6;
            case Castle -> 0.8;
            case Family -> 0.8;
            case Special -> 0.7;
        };
        chaosBase.nbDollar += Math.ceil(nbDollars * percent);
        chaosBase.nbSterling += Math.ceil(nbSterlings * percent);
        if (chaosBase.nbDollar > 200)
            chaosBase.nbDollar = 200;
        if (chaosBase.nbSterling > 200)
            chaosBase.nbSterling = 200;
        chaosBase.specialStage += nbBonusLevels;
        nbBonusLevels = 0;
        chaosBase.powerCountDown -= nbPowers;
        nbPowers = 0;
        chaosBase.difficulty += nbSkulls;
        nbSkulls = 0;
        
//        System.out.println("  $: " + nbDollars + "; £: " + nbSterlings);
//        System.out.println("  -> $: " + chaosBase.nbDollar + "; £: " + chaosBase.nbSterling 
//                + "; BL: " + nbBonusLevels + "; Skulls: " + nbSkulls + "; Power cd: " + chaosBase.powerCountDown);
        
        // Next
        int maxLevels = switch(chaosBase.zone) {
            case Chaos -> 100;
            case Castle -> 20;
            case Family -> 10;
            case Special -> 24;
        };
        int z = chaosBase.zone.ordinal();
        chaosBase.level[z]++;
        nbLevelsDone[z]++;
        if (chaosBase.level[z] > maxLevels)
            chaosBase.level[z] = 1;
        chaosBase.difficulty += nbSkulls;
    }
    
    private void shop() {
        if (chaosBase.specialStage > 0) {
            chaosBase.specialStage--;
            chaosBase.zone = Zone.Special;
        } else if (chaosBase.nbSterling >= 150) {
            chaosBase.zone = Zone.Family;
            chaosBase.nbSterling -= 150;
        } else if (chaosBase.nbDollar >= 100) {
            chaosBase.zone = Zone.Castle;
            chaosBase.nbDollar -= 100;
        } else {
            chaosBase.zone = Zone.Chaos;
        }
    }
    
    private void startSimulation() {
        for (int i = 0; i < nbLevelsDone.length; i++)
            nbLevelsDone[i] = 1;
        
        int k = 0;
        while (chaosBase.difficulty <= 10) {
            String levelName = switch(chaosBase.zone) {
                case Chaos -> LevelNamer.getChaosName(chaosBase.level[Zone.Chaos.ordinal()]);
                case Castle -> LevelNamer.getCastleName(chaosBase.level[Zone.Castle.ordinal()]);
                case Family -> LevelNamer.getFamilyName(chaosBase.level[Zone.Family.ordinal()]);
                case Special -> LevelNamer.getBonusLevelName(chaosBase.level[Zone.Special.ordinal()]);
            };
            String zoneName = chaosBase.zone.toString();
            if (chaosBase.zone != Zone.Chaos)
                zoneName = zoneName.toUpperCase();
            if (chaosBase.zone == Zone.Family)
                zoneName = "*** " + zoneName;
            else if (chaosBase.zone == Zone.Special)
                zoneName = "  %%% Bonus";
            
//            if (chaosBase.zone == Zone.Family) {
                System.out.println("" + k + " " + zoneName + " " + chaosBase.level[chaosBase.zone.ordinal()] 
                        + ": " + levelName
                        + " (" + nbLevelsDone[chaosBase.zone.ordinal()] + ") - diff " + chaosBase.difficulty);
//            }
            
            // Simulate playing
            nbDollars = 0;
            nbSterlings = 0;
            nbSkulls = 0;
            nbBonusLevels = 0;
            nbPowers = 0;
            for (int r = 0; r < NB_RUNS; r++) {
                // Create level
                chaosLevels.MakeCastle();

                // Play and gather bonuses
                playLevel();
            }
            
            // Average
            nbDollars = (nbDollars + NB_RUNS / 2) / NB_RUNS;
            nbSterlings = (nbSterlings + NB_RUNS / 2) / NB_RUNS;
            nbSkulls = (nbSkulls + NB_RUNS / 2) / NB_RUNS;
            nbBonusLevels = (nbBonusLevels + NB_RUNS / 2) / NB_RUNS;
            nbPowers = (nbPowers + NB_RUNS / 2) / NB_RUNS;
            
            if (SKIP_SECOND_LABYRINTH_SKULL) {
                if (levelName.equals("Labyrinth") && chaosBase.difficulty > 1)
                    nbSkulls = 0; // Do not take it
            }
            
            // Collect money and bonuses
            collectBonus();
            
            // Simulate shop
            shop();
            
            k++;
        }
        
        // Stats:
        System.out.println();
        System.out.println("*** Summary: ");
        for (Zone zone : Zone.values()) {
            System.out.println(zone.name() + ": " + nbLevelsDone[zone.ordinal()]);
        }
    }
    
    public static void main(String[] args) {
        GameSimulator simulator = new GameSimulator();
        try {
            simulator.init();
            simulator.initVars();
            simulator.startSimulation();
        } finally {
            simulator.close();
        }
    }

}
