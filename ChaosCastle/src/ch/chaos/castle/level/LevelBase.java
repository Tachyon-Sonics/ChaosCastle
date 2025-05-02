package ch.chaos.castle.level;

import ch.chaos.castle.Chaos1Zone;
import ch.chaos.castle.Chaos2Zone;
import ch.chaos.castle.ChaosBase;
import ch.chaos.castle.ChaosGenerator;
import ch.chaos.castle.ChaosGraphics;
import ch.chaos.castle.ChaosObjects;
import ch.chaos.library.Trigo;


public class LevelBase {

    // Imports
    protected final Chaos1Zone chaos1Zone;
    protected final Chaos2Zone chaos2Zone;
    protected final ChaosBase chaosBase;
    protected final ChaosGenerator chaosGenerator;
    protected final ChaosGraphics chaosGraphics;
    protected final ChaosObjects chaosObjects;
    protected final Trigo trigo;


    protected LevelBase() {
        instance = this; // Set early to handle circular dependencies
        chaos1Zone = Chaos1Zone.instance();
        chaos2Zone = Chaos2Zone.instance();
        chaosBase = ChaosBase.instance();
        chaosGenerator = ChaosGenerator.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosObjects = ChaosObjects.instance();
        trigo = Trigo.instance();
    }


    // CONST

    protected static final int fKmk = 0;
    protected static final int fPic = 1;
    protected static final int fMoneyS = 2;
    protected static final int fMoneyMix = 3;
    protected static final int fAlienColor = 4;
    protected static final int fAlienFour = 5;
    protected static final int fCannon1 = 6;
    protected static final int fCannon2 = 7;
    protected static final int fCartoon = 8;
    protected static final int fNone = 9;
    protected static final int fAnims1 = 10;
    protected static final int fAnims2 = 11;
    protected static final int fAnims3 = 12;
    protected static final int fAnims4 = 13;
    protected static final int fCrunchY = 14;
    protected static final int fCrunchX = 15;
    protected static final int Back4x4 = 8;
    protected static final int BackNone = 9;
    protected static final int Back2x2 = 10;
    protected static final int BackSmall = 11;
    protected static final int BackBig = 12;
    protected static final int Back8x8 = 13;
    protected static final int Tar = 14;
    protected static final int Ground = 15;
    protected static final int Ground2 = 16;
    protected static final int Ice = 17;
    protected static final int Light = 18;
    protected static final int Balls = 19;
    protected static final int Round4 = 20;
    protected static final int FalseBlock = 21;
    protected static final int FalseEmpty = 23;
    protected static final int EmptyBlock = 24;
    protected static final int Sq1Block = 25;
    protected static final int Sq4Block = 26;
    protected static final int Sq4TravBlock = 27;
    protected static final int TravBlock = 28;
    protected static final int Fact1Block = 29;
    protected static final int Fact2Block = 30;
    protected static final int Fact3Block = 31;
    protected static final int SimpleBlock = 32;
    protected static final int Granit1 = 33;
    protected static final int Granit2 = 34;
    protected static final int BigBlock = 35;
    protected static final int Bricks = 36;
    protected static final int Fade1 = 37;
    protected static final int Fade2 = 38;
    protected static final int Fade3 = 39;
    protected static final int FBig1 = 40;
    protected static final int FBig2 = 41;
    protected static final int FSmall1 = 42;
    protected static final int FSmall2 = 43;
    protected static final int FRound = 44;
    protected static final int FStar = 45;
    protected static final int FPanic = 46;
    protected static final int F9x9 = 47;
    protected static final int Forest1 = 48;
    protected static final int Forest2 = 49;
    protected static final int Forest3 = 50;
    protected static final int Forest4 = 51;
    protected static final int Forest5 = 52;
    protected static final int Forest6 = 53;
    protected static final int Forest7 = 54;
    protected static final int Leaf1 = 55;
    protected static final int Leaf2 = 56;
    protected static final int Leaf3 = 57;
    protected static final int Leaf4 = 58;
    protected static final int BarLight = 59;
    protected static final int BarDark = 60;
    protected static final int TravLight = 61;
    protected static final int RGBBlock = 62;
    protected static final int IceBlock = 63;


    // PROCEDURE
    
    // Support

    private static LevelBase instance;

    public static LevelBase instance() {
        if (instance == null)
            new LevelBase(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
