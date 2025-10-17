package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.library.Checks;
import ch.chaos.library.Dialogs;
import ch.chaos.library.Files;
import ch.chaos.library.Memory;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosBase {

    // Imports
    private final Checks checks;
    private final Dialogs dialogs;
    private final Files files;
    private final Memory memory;


    private ChaosBase() {
        instance = this; // Set early to handle circular dependencies
        checks = Checks.instance();
        dialogs = Dialogs.instance();
        files = Files.instance();
        memory = Memory.instance();
    }


    // CONST

    public static final int Frac = 4096;
    public static final int Period = 300;
    public static final int slowStyle = 0;
    public static final int gravityStyle = 1;
    public static final int returnStyle = 2;
    public static final int fastStyle = 3;
    public static final int FlameMult = 32;
    public static final int DeadObj = 0;
    public static final int Message = 1;
    public static final EnumSet<Anims> AnimAlienSet = EnumSet.of(Anims.ALIEN3, Anims.ALIEN2, Anims.ALIEN1, Anims.MISSILE, Anims.MACHINE);
    public static final int Cold = 0;
    public static final int Warm = 8192;
    public static final int Hot = 16384;
    public static final int MaxHot = 24575;
    public static final int GunFireSpeed = Period / 3;


    // TYPE

    public static enum Stones {
        stC26,
        stC34,
        stC35,
        stCBOX,
        stRBOX,
        stCE,
        stRE,
        stCROSS,
        stSTAR1,
        stSTAR2,
        stFLAME1,
        stFLAME2,
        stFOG1,
        stFOG2,
        stFOG3,
        stFOG4;
    }

    public static enum Anims {
        PLAYER,
        WEAPON,
        ALIEN3, // Family alien
        ALIEN2, // Firing or creating alien. ChaosCreator
        ALIEN1, // Basic alien. ChaosAlien
        MISSILE, // Bullet fired by ALIEN2 or ALIEN3
        STONE, // Animation item when hit or dying
        SMARTBONUS, // Extra life + Extra power
        BONUS, // ChaosBonus.bXxx
        DEADOBJ, // ChaosDObj.doXxx (Magnets, wind maker, sand, etc)
        MACHINE, // Turrets, ChaosMachine.mXxx
        DEAD;
    }

    public static enum BasicTypes {
        NotBase,
        Bonus,
        Mineral,
        Vegetal,
        Animal;
    }

    public static enum ObjFlags {
        displayed,
        nested,
        of1,
        of2;
    }

    public static class Obj { // RECORD

        public Memory.Node animNode = new Memory.Node(this);
        public Memory.Node objNode = new Memory.Node(this);
        public ObjAttr attr /* POINTER */;
        public long left;
        public long top;
        public long right;
        public long bottom;
        public long x;
        public long y;
        public long midx;
        public long midy;
        public int vx;
        public int vy;
        public int dvx;
        public int dvy;
        public int ax;
        public int ay;
        public int posX;
        public int posY;
        public int width;
        public int height;
        public int cx;
        public int cy;
        public int life;
        public int shapeSeq;
        public int moveSeq;
        public int stat;
        public int hitSubLife;
        public int fireSubLife;
        public int temperature;
        public int adelay;
        public Anims kind;
        public int subKind;
        public int priority;
        public EnumSet<ObjFlags> flags = EnumSet.noneOf(ObjFlags.class);


        public Memory.Node getAnimNode() {
            return this.animNode;
        }

        public void setAnimNode(Memory.Node animNode) {
            this.animNode = animNode;
        }

        public Memory.Node getObjNode() {
            return this.objNode;
        }

        public void setObjNode(Memory.Node objNode) {
            this.objNode = objNode;
        }

        public ObjAttr getAttr() {
            return this.attr;
        }

        public void setAttr(ObjAttr attr) {
            this.attr = attr;
        }

        public long getLeft() {
            return this.left;
        }

        public void setLeft(long left) {
            this.left = left;
        }

        public long getTop() {
            return this.top;
        }

        public void setTop(long top) {
            this.top = top;
        }

        public long getRight() {
            return this.right;
        }

        public void setRight(long right) {
            this.right = right;
        }

        public long getBottom() {
            return this.bottom;
        }

        public void setBottom(long bottom) {
            this.bottom = bottom;
        }

        public long getX() {
            return this.x;
        }

        public void setX(long x) {
            this.x = x;
        }

        public long getY() {
            return this.y;
        }

        public void setY(long y) {
            this.y = y;
        }

        public long getMidx() {
            return this.midx;
        }

        public void setMidx(long midx) {
            this.midx = midx;
        }

        public long getMidy() {
            return this.midy;
        }

        public void setMidy(long midy) {
            this.midy = midy;
        }

        public int getVx() {
            return this.vx;
        }

        public void setVx(int vx) {
            this.vx = vx;
        }

        public int getVy() {
            return this.vy;
        }

        public void setVy(int vy) {
            this.vy = vy;
        }

        public int getDvx() {
            return this.dvx;
        }

        public void setDvx(int dvx) {
            this.dvx = dvx;
        }

        public int getDvy() {
            return this.dvy;
        }

        public void setDvy(int dvy) {
            this.dvy = dvy;
        }

        public int getAx() {
            return this.ax;
        }

        public void setAx(int ax) {
            this.ax = ax;
        }

        public int getAy() {
            return this.ay;
        }

        public void setAy(int ay) {
            this.ay = ay;
        }

        public int getPosX() {
            return this.posX;
        }

        public void setPosX(int posX) {
            this.posX = posX;
        }

        public int getPosY() {
            return this.posY;
        }

        public void setPosY(int posY) {
            this.posY = posY;
        }

        public int getWidth() {
            return this.width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getCx() {
            return this.cx;
        }

        public void setCx(int cx) {
            this.cx = cx;
        }

        public int getCy() {
            return this.cy;
        }

        public void setCy(int cy) {
            this.cy = cy;
        }

        public int getLife() {
            return this.life;
        }

        public void setLife(int life) {
            this.life = life;
        }

        public int getShapeSeq() {
            return this.shapeSeq;
        }

        public void setShapeSeq(int shapeSeq) {
            this.shapeSeq = shapeSeq;
        }

        public int getMoveSeq() {
            return this.moveSeq;
        }

        public void setMoveSeq(int moveSeq) {
            this.moveSeq = moveSeq;
        }

        public int getStat() {
            return this.stat;
        }

        public void setStat(int stat) {
            this.stat = stat;
        }

        public int getHitSubLife() {
            return this.hitSubLife;
        }

        public void setHitSubLife(int hitSubLife) {
            this.hitSubLife = hitSubLife;
        }

        public int getFireSubLife() {
            return this.fireSubLife;
        }

        public void setFireSubLife(int fireSubLife) {
            this.fireSubLife = fireSubLife;
        }

        public int getTemperature() {
            return this.temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public int getAdelay() {
            return this.adelay;
        }

        public void setAdelay(int adelay) {
            this.adelay = adelay;
        }

        public Anims getKind() {
            return this.kind;
        }

        public void setKind(Anims kind) {
            this.kind = kind;
        }

        public int getSubKind() {
            return this.subKind;
        }

        public void setSubKind(int subKind) {
            this.subKind = subKind;
        }

        public int getPriority() {
            return this.priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public EnumSet<ObjFlags> getFlags() {
            return this.flags;
        }

        public void setFlags(EnumSet<ObjFlags> flags) {
            this.flags = flags;
        }


        public void copyFrom(Obj other) {
            this.animNode.copyFrom(other.animNode);
            this.objNode.copyFrom(other.objNode);
            this.attr = other.attr;
            this.left = other.left;
            this.top = other.top;
            this.right = other.right;
            this.bottom = other.bottom;
            this.x = other.x;
            this.y = other.y;
            this.midx = other.midx;
            this.midy = other.midy;
            this.vx = other.vx;
            this.vy = other.vy;
            this.dvx = other.dvx;
            this.dvy = other.dvy;
            this.ax = other.ax;
            this.ay = other.ay;
            this.posX = other.posX;
            this.posY = other.posY;
            this.width = other.width;
            this.height = other.height;
            this.cx = other.cx;
            this.cy = other.cy;
            this.life = other.life;
            this.shapeSeq = other.shapeSeq;
            this.moveSeq = other.moveSeq;
            this.stat = other.stat;
            this.hitSubLife = other.hitSubLife;
            this.fireSubLife = other.fireSubLife;
            this.temperature = other.temperature;
            this.adelay = other.adelay;
            this.kind = other.kind;
            this.subKind = other.subKind;
            this.priority = other.priority;
            this.flags = EnumSet.copyOf(other.flags);
        }

        public Obj newCopy() {
            Obj copy = new Obj();
            copy.copyFrom(this);
            return copy;
        }

    }

    @FunctionalInterface
    public static interface ResetProc { // PROCEDURE Type
        public void invoke(Obj arg1);
    }

    @FunctionalInterface
    public static interface MakeProc { // PROCEDURE Type
        public void invoke(Obj arg1);
    }

    @FunctionalInterface
    public static interface MoveProc { // PROCEDURE Type
        public void invoke(Obj arg1);
    }

    @FunctionalInterface
    public static interface AieProc { // PROCEDURE Type
        public void invoke(Obj arg1, Obj arg2, /* VAR */ Runtime.IRef<Integer> arg3, /* VAR */ Runtime.IRef<Integer> arg4);
    }

    @FunctionalInterface
    public static interface DieProc { // PROCEDURE Type
        public void invoke(Obj arg1);
    }

    public static class ObjAttr { // RECORD

        public Memory.Node node = new Memory.Node(this);
        public ResetProc Reset;
        public MakeProc Make;
        public MoveProc Move;
        public AieProc Aie;
        public DieProc Die;
        public int inerty;
        public int weight;
        public int charge;
        public int heatSpeed;
        public int coolSpeed;
        public int refreshSpeed;
        public EnumSet<Stones> aieStKinds = EnumSet.noneOf(Stones.class);
        public EnumSet<Stones> dieStKinds = EnumSet.noneOf(Stones.class);
        public int aieStone;
        public int aieStStyle;
        public int aieSKCount;
        public int dieStone;
        public int dieStStyle;
        public int dieSKCount;
        public BasicTypes basicType;
        public int priority;
        public int nbObj;
        public boolean toKill;


        public Memory.Node getNode() {
            return this.node;
        }

        public void setNode(Memory.Node node) {
            this.node = node;
        }

        public ResetProc getReset() {
            return this.Reset;
        }

        public void setReset(ResetProc Reset) {
            this.Reset = Reset;
        }

        public MakeProc getMake() {
            return this.Make;
        }

        public void setMake(MakeProc Make) {
            this.Make = Make;
        }

        public MoveProc getMove() {
            return this.Move;
        }

        public void setMove(MoveProc Move) {
            this.Move = Move;
        }

        public AieProc getAie() {
            return this.Aie;
        }

        public void setAie(AieProc Aie) {
            this.Aie = Aie;
        }

        public DieProc getDie() {
            return this.Die;
        }

        public void setDie(DieProc Die) {
            this.Die = Die;
        }

        public int getInerty() {
            return this.inerty;
        }

        public void setInerty(int inerty) {
            this.inerty = inerty;
        }

        public int getWeight() {
            return this.weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getCharge() {
            return this.charge;
        }

        public void setCharge(int charge) {
            this.charge = charge;
        }

        public int getHeatSpeed() {
            return this.heatSpeed;
        }

        public void setHeatSpeed(int heatSpeed) {
            this.heatSpeed = heatSpeed;
        }

        public int getCoolSpeed() {
            return this.coolSpeed;
        }

        public void setCoolSpeed(int coolSpeed) {
            this.coolSpeed = coolSpeed;
        }

        public int getRefreshSpeed() {
            return this.refreshSpeed;
        }

        public void setRefreshSpeed(int refreshSpeed) {
            this.refreshSpeed = refreshSpeed;
        }

        public EnumSet<Stones> getAieStKinds() {
            return this.aieStKinds;
        }

        public void setAieStKinds(EnumSet<Stones> aieStKinds) {
            this.aieStKinds = aieStKinds;
        }

        public EnumSet<Stones> getDieStKinds() {
            return this.dieStKinds;
        }

        public void setDieStKinds(EnumSet<Stones> dieStKinds) {
            this.dieStKinds = dieStKinds;
        }

        public int getAieStone() {
            return this.aieStone;
        }

        public void setAieStone(int aieStone) {
            this.aieStone = aieStone;
        }

        public int getAieStStyle() {
            return this.aieStStyle;
        }

        public void setAieStStyle(int aieStStyle) {
            this.aieStStyle = aieStStyle;
        }

        public int getAieSKCount() {
            return this.aieSKCount;
        }

        public void setAieSKCount(int aieSKCount) {
            this.aieSKCount = aieSKCount;
        }

        public int getDieStone() {
            return this.dieStone;
        }

        public void setDieStone(int dieStone) {
            this.dieStone = dieStone;
        }

        public int getDieStStyle() {
            return this.dieStStyle;
        }

        public void setDieStStyle(int dieStStyle) {
            this.dieStStyle = dieStStyle;
        }

        public int getDieSKCount() {
            return this.dieSKCount;
        }

        public void setDieSKCount(int dieSKCount) {
            this.dieSKCount = dieSKCount;
        }

        public BasicTypes getBasicType() {
            return this.basicType;
        }

        public void setBasicType(BasicTypes basicType) {
            this.basicType = basicType;
        }

        public int getPriority() {
            return this.priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public int getNbObj() {
            return this.nbObj;
        }

        public void setNbObj(int nbObj) {
            this.nbObj = nbObj;
        }

        public boolean isToKill() {
            return this.toKill;
        }

        public void setToKill(boolean toKill) {
            this.toKill = toKill;
        }


        public void copyFrom(ObjAttr other) {
            this.node.copyFrom(other.node);
            this.Reset = other.Reset;
            this.Make = other.Make;
            this.Move = other.Move;
            this.Aie = other.Aie;
            this.Die = other.Die;
            this.inerty = other.inerty;
            this.weight = other.weight;
            this.charge = other.charge;
            this.heatSpeed = other.heatSpeed;
            this.coolSpeed = other.coolSpeed;
            this.refreshSpeed = other.refreshSpeed;
            this.aieStKinds = EnumSet.copyOf(other.aieStKinds);
            this.dieStKinds = EnumSet.copyOf(other.dieStKinds);
            this.aieStone = other.aieStone;
            this.aieStStyle = other.aieStStyle;
            this.aieSKCount = other.aieSKCount;
            this.dieStone = other.dieStone;
            this.dieStStyle = other.dieStStyle;
            this.dieSKCount = other.dieSKCount;
            this.basicType = other.basicType;
            this.priority = other.priority;
            this.nbObj = other.nbObj;
            this.toKill = other.toKill;
        }

        public ObjAttr newCopy() {
            ObjAttr copy = new ObjAttr();
            copy.copyFrom(this);
            return copy;
        }

    }

    public static enum Weapon {
        GUN,
        FB,
        LASER,
        BUBBLE,
        FIRE,
        BALL,
        STAR,
        GRENADE;
    }

    public static class WeaponAttr { // RECORD

        public int power;
        public int nbBullet;
        public int nbBomb;


        public int getPower() {
            return this.power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        public int getNbBullet() {
            return this.nbBullet;
        }

        public void setNbBullet(int nbBullet) {
            this.nbBullet = nbBullet;
        }

        public int getNbBomb() {
            return this.nbBomb;
        }

        public void setNbBomb(int nbBomb) {
            this.nbBomb = nbBomb;
        }


        public void copyFrom(WeaponAttr other) {
            this.power = other.power;
            this.nbBullet = other.nbBullet;
            this.nbBomb = other.nbBomb;
        }

        public WeaponAttr newCopy() {
            WeaponAttr copy = new WeaponAttr();
            copy.copyFrom(this);
            return copy;
        }

    }

    @FunctionalInterface
    public static interface FireProc { // PROCEDURE Type
        public void invoke(Obj arg1);
    }

    public static enum GameStat {
        Start,
        Playing,
        Finish,
        Gameover,
        Break;
    }

    public static enum Zone {
        Chaos,
        Castle,
        Family,
        Special;
    }

    public static class Statistic { // RECORD

        public int total;
        public int done;


        public int getTotal() {
            return this.total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getDone() {
            return this.done;
        }

        public void setDone(int done) {
            this.done = done;
        }


        public void copyFrom(Statistic other) {
            this.total = other.total;
            this.done = other.done;
        }

        public Statistic newCopy() {
            Statistic copy = new Statistic();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    public Memory.List[] attrList = Runtime.initArray(new Memory.List[Anims.values().length]);
    public Memory.List[] animList = Runtime.initArray(new Memory.List[Anims.values().length]);
    public int[] nbAnim = new int[Anims.values().length];
    public Memory.List objList = new Memory.List();
    public Memory.List leftObjList = new Memory.List();
    public Memory.List emptyObjList = new Memory.List();
    public Obj nextObj /* POINTER */;
    public int nbEmpty;
    public int nbObj;
    public int nbToKill;
    public long lasttime;
    public int step;
    public WeaponAttr[] weaponAttr = Runtime.initArray(new WeaponAttr[Weapon.values().length]);
    public FireProc[] Fire = new FireProc[Weapon.values().length];
    public FireProc[] Bomb = new FireProc[Weapon.values().length];
    public long nextGunFireTime;
    public boolean gunFiring;
    public boolean password;
    public Obj mainPlayer /* POINTER */;
    public int invinsibility;
    public int sleeper;
    public int doubleSpeed;
    public int hurryUp;
    public int difficulty;
    public int magnet;
    public int air;
    public int freeFire;
    public int maxPower;
    public int noMissile;
    public int playerPower;
    public int screenInverted;
    public int specialStage;
    public int stages;
    public int powerCountDown;
    public int pLife;
    public int nbDollar;
    public int nbSterling;
    public boolean snow;
    public boolean water;
    public GameStat gameStat;
    public Zone zone;
    public int[] level = new int[Zone.values().length];
    public Statistic[] basics = Runtime.initArray(new Statistic[BasicTypes.Animal.ordinal() - BasicTypes.Bonus.ordinal() + 1]);
    public Statistic shoot = new Statistic();
    public long addpt;
    public long score;
    public long gameSeed;
    public Weapon[] buttonAssign = Runtime.initArray(new Weapon[16]);
    public String weaponKey = "";
    public Weapon selectedWeapon;
    public boolean weaponSelected;
    public boolean bombActive;
    public Runtime.RangeSet lastJoy = new Runtime.RangeSet(Memory.SET16_r);
    public Files.FilePtr file;
    public Dialogs.GadgetPtr d;


    public Memory.List[] getAttrList() {
        return this.attrList;
    }

    public void setAttrList(Memory.List[] attrList) {
        this.attrList = attrList;
    }

    public Memory.List[] getAnimList() {
        return this.animList;
    }

    public void setAnimList(Memory.List[] animList) {
        this.animList = animList;
    }

    public int[] getNbAnim() {
        return this.nbAnim;
    }

    public void setNbAnim(int[] nbAnim) {
        this.nbAnim = nbAnim;
    }

    public Memory.List getObjList() {
        return this.objList;
    }

    public void setObjList(Memory.List objList) {
        this.objList = objList;
    }

    public Memory.List getLeftObjList() {
        return this.leftObjList;
    }

    public void setLeftObjList(Memory.List leftObjList) {
        this.leftObjList = leftObjList;
    }

    public Memory.List getEmptyObjList() {
        return this.emptyObjList;
    }

    public void setEmptyObjList(Memory.List emptyObjList) {
        this.emptyObjList = emptyObjList;
    }

    public Obj getNextObj() {
        return this.nextObj;
    }

    public void setNextObj(Obj nextObj) {
        this.nextObj = nextObj;
    }

    public int getNbEmpty() {
        return this.nbEmpty;
    }

    public void setNbEmpty(int nbEmpty) {
        this.nbEmpty = nbEmpty;
    }

    public int getNbObj() {
        return this.nbObj;
    }

    public void setNbObj(int nbObj) {
        this.nbObj = nbObj;
    }

    public int getNbToKill() {
        return this.nbToKill;
    }

    public void setNbToKill(int nbToKill) {
        this.nbToKill = nbToKill;
    }

    public long getLasttime() {
        return this.lasttime;
    }

    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }

    public int getStep() {
        return this.step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public WeaponAttr[] getWeaponAttr() {
        return this.weaponAttr;
    }

    public void setWeaponAttr(WeaponAttr[] weaponAttr) {
        this.weaponAttr = weaponAttr;
    }

    public FireProc[] getFire() {
        return this.Fire;
    }

    public void setFire(FireProc[] Fire) {
        this.Fire = Fire;
    }

    public FireProc[] getBomb() {
        return this.Bomb;
    }

    public void setBomb(FireProc[] Bomb) {
        this.Bomb = Bomb;
    }

    public long getNextGunFireTime() {
        return this.nextGunFireTime;
    }

    public void setNextGunFireTime(long nextGunFireTime) {
        this.nextGunFireTime = nextGunFireTime;
    }

    public boolean isGunFiring() {
        return this.gunFiring;
    }

    public void setGunFiring(boolean gunFiring) {
        this.gunFiring = gunFiring;
    }

    public boolean isPassword() {
        return this.password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public Obj getMainPlayer() {
        return this.mainPlayer;
    }

    public void setMainPlayer(Obj mainPlayer) {
        this.mainPlayer = mainPlayer;
    }

    public int getInvinsibility() {
        return this.invinsibility;
    }

    public void setInvinsibility(int invinsibility) {
        this.invinsibility = invinsibility;
    }

    public int getSleeper() {
        return this.sleeper;
    }

    public void setSleeper(int sleeper) {
        this.sleeper = sleeper;
    }

    public int getDoubleSpeed() {
        return this.doubleSpeed;
    }

    public void setDoubleSpeed(int doubleSpeed) {
        this.doubleSpeed = doubleSpeed;
    }

    public int getHurryUp() {
        return this.hurryUp;
    }

    public void setHurryUp(int hurryUp) {
        this.hurryUp = hurryUp;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getMagnet() {
        return this.magnet;
    }

    public void setMagnet(int magnet) {
        this.magnet = magnet;
    }

    public int getAir() {
        return this.air;
    }

    public void setAir(int air) {
        this.air = air;
    }

    public int getFreeFire() {
        return this.freeFire;
    }

    public void setFreeFire(int freeFire) {
        this.freeFire = freeFire;
    }

    public int getMaxPower() {
        return this.maxPower;
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }

    public int getNoMissile() {
        return this.noMissile;
    }

    public void setNoMissile(int noMissile) {
        this.noMissile = noMissile;
    }

    public int getPlayerPower() {
        return this.playerPower;
    }

    public void setPlayerPower(int playerPower) {
        this.playerPower = playerPower;
    }

    public int getScreenInverted() {
        return this.screenInverted;
    }

    public void setScreenInverted(int screenInverted) {
        this.screenInverted = screenInverted;
    }

    public int getSpecialStage() {
        return this.specialStage;
    }

    public void setSpecialStage(int specialStage) {
        this.specialStage = specialStage;
    }

    public int getStages() {
        return this.stages;
    }

    public void setStages(int stages) {
        this.stages = stages;
    }

    public int getPowerCountDown() {
        return this.powerCountDown;
    }

    public void setPowerCountDown(int powerCountDown) {
        this.powerCountDown = powerCountDown;
    }

    public int getPLife() {
        return this.pLife;
    }

    public void setPLife(int pLife) {
        this.pLife = pLife;
    }

    public int getNbDollar() {
        return this.nbDollar;
    }

    public void setNbDollar(int nbDollar) {
        this.nbDollar = nbDollar;
    }

    public int getNbSterling() {
        return this.nbSterling;
    }

    public void setNbSterling(int nbSterling) {
        this.nbSterling = nbSterling;
    }

    public boolean isSnow() {
        return this.snow;
    }

    public void setSnow(boolean snow) {
        this.snow = snow;
    }

    public boolean isWater() {
        return this.water;
    }

    public void setWater(boolean water) {
        this.water = water;
    }

    public GameStat getGameStat() {
        return this.gameStat;
    }

    public void setGameStat(GameStat gameStat) {
        this.gameStat = gameStat;
    }

    public Zone getZone() {
        return this.zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public int[] getLevel() {
        return this.level;
    }

    public void setLevel(int[] level) {
        this.level = level;
    }

    public Statistic[] getBasics() {
        return this.basics;
    }

    public void setBasics(Statistic[] basics) {
        this.basics = basics;
    }

    public Statistic getShoot() {
        return this.shoot;
    }

    public void setShoot(Statistic shoot) {
        this.shoot = shoot;
    }

    public long getAddpt() {
        return this.addpt;
    }

    public void setAddpt(long addpt) {
        this.addpt = addpt;
    }

    public long getScore() {
        return this.score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getGameSeed() {
        return this.gameSeed;
    }

    public void setGameSeed(long gameSeed) {
        this.gameSeed = gameSeed;
    }

    public Weapon[] getButtonAssign() {
        return this.buttonAssign;
    }

    public void setButtonAssign(Weapon[] buttonAssign) {
        this.buttonAssign = buttonAssign;
    }

    public String getWeaponKey() {
        return this.weaponKey;
    }

    public void setWeaponKey(String weaponKey) {
        this.weaponKey = weaponKey;
    }

    public Weapon getSelectedWeapon() {
        return this.selectedWeapon;
    }

    public void setSelectedWeapon(Weapon selectedWeapon) {
        this.selectedWeapon = selectedWeapon;
    }

    public boolean isWeaponSelected() {
        return this.weaponSelected;
    }

    public void setWeaponSelected(boolean weaponSelected) {
        this.weaponSelected = weaponSelected;
    }

    public boolean isBombActive() {
        return this.bombActive;
    }

    public void setBombActive(boolean bombActive) {
        this.bombActive = bombActive;
    }

    public Runtime.RangeSet getLastJoy() {
        return this.lastJoy;
    }

    public void setLastJoy(Runtime.RangeSet lastJoy) {
        this.lastJoy = lastJoy;
    }

    public Files.FilePtr getFile() {
        return this.file;
    }

    public void setFile(Files.FilePtr file) {
        this.file = file;
    }

    public Dialogs.GadgetPtr getD() {
        return this.d;
    }

    public void setD(Dialogs.GadgetPtr d) {
        this.d = d;
    }


    // CONST

//    private static final int MAXOBJ = ((1 << 8) - 1) /* MAX(SHORTCARD) */;
//    private static final int MAXALIEN = MAXOBJ - 32;
//    public static final int MAXSTONES = MAXOBJ - 64;
    private static final int MAXOBJ = 1024;
    public static final int MAXALIEN = MAXOBJ - 128;
    public static final int MAXSTONES = MAXOBJ - 256;


    // PROCEDURE

    public ObjAttr GetAnimAttr(Anims kind, int subKind) {
        // VAR
        Memory.List list = null;
        ObjAttr objAttr = null;
        ObjAttr tail = null;

        list = attrList[kind.ordinal()];
        objAttr = (ObjAttr) memory.First(list);
        tail = (ObjAttr) memory.Tail(list);
        while (true) {
            if (objAttr == tail)
                return null;
            if (subKind == 0)
                break;
            objAttr = (ObjAttr) memory.Next(objAttr.node);
            subKind--;
        }
        return objAttr;
    }

    public Obj NewObj(Anims kind, int subKind) {
        // VAR
        ObjAttr attr = null;
        Obj obj = null;
        Memory.Node at = null; /* PATCH */
        Memory.Node tail = null;
        int max = 0;
        int pri = 0;

        if (Runtime.minusSet(Runtime.plusSet(AnimAlienSet, EnumSet.of(Anims.STONE, Anims.DEADOBJ)), EnumSet.of(Anims.ALIEN3)).contains(kind))
            max = MAXALIEN;
        else
            max = MAXOBJ;
        if (nbObj >= max)
            return null;
        attr = GetAnimAttr(kind, subKind);
        if (attr == null)
            return null;
        if (attr.toKill)
            nbToKill++;
        pri = attr.priority;
        attr.nbObj++;
        obj = (Obj) FirstObj(emptyObjList);
        memory.Remove(obj.objNode);
        nbEmpty--;
        nbObj++;
        obj.kind = kind;
        obj.subKind = subKind;
        obj.attr = attr;
        at = memory.FirstNode(objList);
        tail = memory.TailNode(objList);
        while ((at != tail) && (pri > ((Obj) at.data).priority)) {
            at = memory.NextNode(at);
        }
        obj.priority = pri;
        memory.AddBefore(at, obj.objNode);
        memory.AddHead(animList[kind.ordinal()], obj.animNode);
        nbAnim[kind.ordinal()]++;
        return obj;
    }

    public void DisposeObj(Obj obj) {
        if (obj == nextObj)
            nextObj = (Obj) memory.Next(obj.animNode);
        if (obj.attr.toKill) {
            nbToKill--;
            if ((nbToKill == 0) && (gameStat == GameStat.Playing))
                gameStat = GameStat.Finish;
        }
        obj.attr.nbObj--;
        nbAnim[obj.kind.ordinal()]--;
        memory.Remove(obj.objNode);
        nbEmpty++;
        nbObj--;
        memory.Remove(obj.animNode);
        obj.kind = Anims.DEAD;
        obj.attr = null;
        memory.AddHead(emptyObjList, obj.objNode);
    }

    public void ConvertObj(Obj obj, Anims kind, int subKind) {
        // VAR
        ObjAttr attr = null;

        if (obj == nextObj)
            nextObj = (Obj) memory.Next(obj.animNode);
        attr = obj.attr;
        attr.nbObj--;
        if (obj.attr.toKill)
            nbToKill--;
        nbAnim[obj.kind.ordinal()]--;
        memory.Remove(obj.animNode);
        attr = GetAnimAttr(kind, subKind);
        obj.kind = kind;
        obj.subKind = subKind;
        obj.attr = attr;
        attr.nbObj++;
        if (attr.toKill)
            nbToKill++;
        memory.AddHead(animList[kind.ordinal()], obj.animNode);
        nbAnim[kind.ordinal()]++;
        if (nbToKill == 0)
            gameStat = GameStat.Finish;
    }

    public void LeaveObj(Obj obj) {
        if (obj == nextObj)
            nextObj = (Obj) memory.Next(obj.animNode);
        memory.Remove(obj.animNode);
        memory.Remove(obj.objNode);
        memory.AddHead(leftObjList, obj.objNode);
    }

    public void RestartObj(Obj obj) {
        // VAR
        Memory.Node at = null; /* PATCH */
        Memory.Node tail = null;
        int pri = 0;

        memory.Remove(obj.objNode);
        memory.AddHead(animList[obj.kind.ordinal()], obj.animNode);
        pri = obj.priority;
        at = memory.FirstNode(objList);
        tail = memory.TailNode(objList);
        while ((at != tail) && (pri > ((Obj) at.data).priority)) {
            at = memory.NextNode(at);
        }
        memory.AddBefore(at, obj.objNode);
    }

    public Object FirstObj(/* VAR+WRT */ Memory.List list) {
        return memory.First(list);
    }

    public Object NextObj(/* VAR+WRT */ Memory.Node node) {
        return memory.Next(node);
    }

    public Object PrevObj(/* VAR+WRT */ Memory.Node node) {
        return memory.Prev(node);
    }

    public Object TailObj(/* VAR+WRT */ Memory.List list) {
        return memory.Tail(list);
    }

    public void FlushAllObjs() {
        nextObj = null;
        while (!memory.Empty(leftObjList)) {
            RestartObj((Obj) FirstObj(leftObjList));
        }
        while (!memory.Empty(objList)) {
            DisposeObj((Obj) FirstObj(objList));
        }
    }

    private void InitLists() {
        // VAR
        Anims a = Anims.PLAYER;
        int c = 0;
        Obj newObj = null;

        memory.InitList(objList);
        memory.InitList(leftObjList);
        memory.InitList(emptyObjList);
        for (int _a = 0; _a < Anims.values().length; _a++) {
            a = Anims.values()[_a];
            memory.InitList(attrList[a.ordinal()]);
            memory.InitList(animList[a.ordinal()]);
            nbAnim[a.ordinal()] = 0;
        }
        for (c = 0; c <= MAXOBJ; c++) {
            newObj = (Obj) memory.AllocMem(Runtime.sizeOf(213, Obj.class));
            checks.CheckMem(newObj);
            memory.AddHead(emptyObjList, newObj.objNode);
        }
        nextObj = null;
        nbObj = 0;
        nbEmpty = MAXOBJ;
        nbToKill = 0;
        file = files.noFile;
        d = dialogs.noGadget;
    }

    private void FlushLists() {
        // VAR
        Anims a = Anims.PLAYER;
        Runtime.Ref<Obj> freeObj = new Runtime.Ref<>(null);
        Runtime.Ref<ObjAttr> freeAttr = new Runtime.Ref<>(null);

        FlushAllObjs();
        while (!memory.Empty(emptyObjList)) {
            freeObj.set((Obj) FirstObj(emptyObjList));
            memory.Remove(freeObj.get().objNode);
            memory.FreeMem(freeObj.asAdrRef());
        }
        for (int _a = 0; _a < Anims.values().length; _a++) {
            a = Anims.values()[_a];
            while (!memory.Empty(attrList[a.ordinal()])) {
                freeAttr.set((ObjAttr) memory.First(attrList[a.ordinal()]));
                memory.Remove(freeAttr.get().node);
                memory.FreeMem(freeAttr.asAdrRef());
            }
        }
    }

    private void Close() {
        FlushLists();
        files.CloseFile(new Runtime.FieldRef<>(this::getFile, this::setFile));
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(this::getD, this::setD));
    }

    private final Runnable Close_ref = this::Close;

    
    // Support

    private static ChaosBase instance;

    public static ChaosBase instance() {
        if (instance == null)
            new ChaosBase(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        password = false;
        InitLists();
        checks.AddTermProc(Close_ref);
    }

    public void close() {
    }

}
