package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.ObjFlags;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosWeapon {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Languages languages;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosWeapon() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // CONST

    private static final int MAXF = 32;


    // TYPE

    private static class FollowData { // RECORD

        private Memory.Node currentNode /* POINTER */; /* PATCH */
        private Memory.Node tailNode /* POINTER */;
        private ChaosBase.Obj bestObj /* POINTER */;
        private long lasttime;
        private int bestCount;
        private int bestDist;
        private int wait;
        private Anims anim;
        private boolean finish;
        private boolean used;


        public ChaosBase.Obj getBestObj() {
            return this.bestObj;
        }

        public void setBestObj(ChaosBase.Obj bestObj) {
            this.bestObj = bestObj;
        }

        public long getLasttime() {
            return this.lasttime;
        }

        public void setLasttime(long lasttime) {
            this.lasttime = lasttime;
        }

        public int getBestCount() {
            return this.bestCount;
        }

        public void setBestCount(int bestCount) {
            this.bestCount = bestCount;
        }

        public int getBestDist() {
            return this.bestDist;
        }

        public void setBestDist(int bestDist) {
            this.bestDist = bestDist;
        }

        public int getWait() {
            return this.wait;
        }

        public void setWait(int wait) {
            this.wait = wait;
        }

        public Anims getAnim() {
            return this.anim;
        }

        public void setAnim(Anims anim) {
            this.anim = anim;
        }

        public boolean isFinish() {
            return this.finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }

        public boolean isUsed() {
            return this.used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }


        public void copyFrom(FollowData other) {
            this.currentNode = other.currentNode;
            this.tailNode = other.tailNode;
            this.bestObj = other.bestObj;
            this.lasttime = other.lasttime;
            this.bestCount = other.bestCount;
            this.bestDist = other.bestDist;
            this.wait = other.wait;
            this.anim = other.anim;
            this.finish = other.finish;
            this.used = other.used;
        }

        public FollowData newCopy() {
            FollowData copy = new FollowData();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    private ChaosBase.Obj newWeapon /* POINTER */;
    private FollowData[] follow = Runtime.initArray(new FollowData[MAXF]);
    private ChaosSounds.Effect[] gunFireEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] gunBombEffectL = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] gunKillEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] fbKillEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] laserFireEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] bubbleBombEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] starFireEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] grenadeBombEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] ballFireEffect1 = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] ballFireEffect2 = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] ballFireEffect3 = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] gunBombEffectR = Runtime.initArray(new ChaosSounds.Effect[2]);
    private ChaosSounds.Effect[] fbFireEffect = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] laserBombEffectL = Runtime.initArray(new ChaosSounds.Effect[9]);
    private ChaosSounds.Effect[] laserBombEffectR = Runtime.initArray(new ChaosSounds.Effect[9]);
    private ChaosSounds.Effect[] bubbleFireEffectL = Runtime.initArray(new ChaosSounds.Effect[8]);
    private ChaosSounds.Effect[] bubbleFireEffectR = Runtime.initArray(new ChaosSounds.Effect[13]);
    private ChaosSounds.Effect[] fireFireEffect = Runtime.initArray(new ChaosSounds.Effect[8]);
    private ChaosSounds.Effect[] ballBombEffectR = Runtime.initArray(new ChaosSounds.Effect[31]);
    private ChaosSounds.Effect[] ballBombEffectL = Runtime.initArray(new ChaosSounds.Effect[16]);
    private ChaosSounds.Effect[] grenadeFireEffect = Runtime.initArray(new ChaosSounds.Effect[4]);


    public ChaosBase.Obj getNewWeapon() {
        return this.newWeapon;
    }

    public void setNewWeapon(ChaosBase.Obj newWeapon) {
        this.newWeapon = newWeapon;
    }

    public FollowData[] getFollow() {
        return this.follow;
    }

    public void setFollow(FollowData[] follow) {
        this.follow = follow;
    }

    public ChaosSounds.Effect[] getGunFireEffect() {
        return this.gunFireEffect;
    }

    public void setGunFireEffect(ChaosSounds.Effect[] gunFireEffect) {
        this.gunFireEffect = gunFireEffect;
    }

    public ChaosSounds.Effect[] getGunBombEffectL() {
        return this.gunBombEffectL;
    }

    public void setGunBombEffectL(ChaosSounds.Effect[] gunBombEffectL) {
        this.gunBombEffectL = gunBombEffectL;
    }

    public ChaosSounds.Effect[] getGunKillEffect() {
        return this.gunKillEffect;
    }

    public void setGunKillEffect(ChaosSounds.Effect[] gunKillEffect) {
        this.gunKillEffect = gunKillEffect;
    }

    public ChaosSounds.Effect[] getFbKillEffect() {
        return this.fbKillEffect;
    }

    public void setFbKillEffect(ChaosSounds.Effect[] fbKillEffect) {
        this.fbKillEffect = fbKillEffect;
    }

    public ChaosSounds.Effect[] getLaserFireEffect() {
        return this.laserFireEffect;
    }

    public void setLaserFireEffect(ChaosSounds.Effect[] laserFireEffect) {
        this.laserFireEffect = laserFireEffect;
    }

    public ChaosSounds.Effect[] getBubbleBombEffect() {
        return this.bubbleBombEffect;
    }

    public void setBubbleBombEffect(ChaosSounds.Effect[] bubbleBombEffect) {
        this.bubbleBombEffect = bubbleBombEffect;
    }

    public ChaosSounds.Effect[] getStarFireEffect() {
        return this.starFireEffect;
    }

    public void setStarFireEffect(ChaosSounds.Effect[] starFireEffect) {
        this.starFireEffect = starFireEffect;
    }

    public ChaosSounds.Effect[] getGrenadeBombEffect() {
        return this.grenadeBombEffect;
    }

    public void setGrenadeBombEffect(ChaosSounds.Effect[] grenadeBombEffect) {
        this.grenadeBombEffect = grenadeBombEffect;
    }

    public ChaosSounds.Effect[] getBallFireEffect1() {
        return this.ballFireEffect1;
    }

    public void setBallFireEffect1(ChaosSounds.Effect[] ballFireEffect1) {
        this.ballFireEffect1 = ballFireEffect1;
    }

    public ChaosSounds.Effect[] getBallFireEffect2() {
        return this.ballFireEffect2;
    }

    public void setBallFireEffect2(ChaosSounds.Effect[] ballFireEffect2) {
        this.ballFireEffect2 = ballFireEffect2;
    }

    public ChaosSounds.Effect[] getBallFireEffect3() {
        return this.ballFireEffect3;
    }

    public void setBallFireEffect3(ChaosSounds.Effect[] ballFireEffect3) {
        this.ballFireEffect3 = ballFireEffect3;
    }

    public ChaosSounds.Effect[] getGunBombEffectR() {
        return this.gunBombEffectR;
    }

    public void setGunBombEffectR(ChaosSounds.Effect[] gunBombEffectR) {
        this.gunBombEffectR = gunBombEffectR;
    }

    public ChaosSounds.Effect[] getFbFireEffect() {
        return this.fbFireEffect;
    }

    public void setFbFireEffect(ChaosSounds.Effect[] fbFireEffect) {
        this.fbFireEffect = fbFireEffect;
    }

    public ChaosSounds.Effect[] getLaserBombEffectL() {
        return this.laserBombEffectL;
    }

    public void setLaserBombEffectL(ChaosSounds.Effect[] laserBombEffectL) {
        this.laserBombEffectL = laserBombEffectL;
    }

    public ChaosSounds.Effect[] getLaserBombEffectR() {
        return this.laserBombEffectR;
    }

    public void setLaserBombEffectR(ChaosSounds.Effect[] laserBombEffectR) {
        this.laserBombEffectR = laserBombEffectR;
    }

    public ChaosSounds.Effect[] getBubbleFireEffectL() {
        return this.bubbleFireEffectL;
    }

    public void setBubbleFireEffectL(ChaosSounds.Effect[] bubbleFireEffectL) {
        this.bubbleFireEffectL = bubbleFireEffectL;
    }

    public ChaosSounds.Effect[] getBubbleFireEffectR() {
        return this.bubbleFireEffectR;
    }

    public void setBubbleFireEffectR(ChaosSounds.Effect[] bubbleFireEffectR) {
        this.bubbleFireEffectR = bubbleFireEffectR;
    }

    public ChaosSounds.Effect[] getFireFireEffect() {
        return this.fireFireEffect;
    }

    public void setFireFireEffect(ChaosSounds.Effect[] fireFireEffect) {
        this.fireFireEffect = fireFireEffect;
    }

    public ChaosSounds.Effect[] getBallBombEffectR() {
        return this.ballBombEffectR;
    }

    public void setBallBombEffectR(ChaosSounds.Effect[] ballBombEffectR) {
        this.ballBombEffectR = ballBombEffectR;
    }

    public ChaosSounds.Effect[] getBallBombEffectL() {
        return this.ballBombEffectL;
    }

    public void setBallBombEffectL(ChaosSounds.Effect[] ballBombEffectL) {
        this.ballBombEffectL = ballBombEffectL;
    }

    public ChaosSounds.Effect[] getGrenadeFireEffect() {
        return this.grenadeFireEffect;
    }

    public void setGrenadeFireEffect(ChaosSounds.Effect[] grenadeFireEffect) {
        this.grenadeFireEffect = grenadeFireEffect;
    }


    // PROCEDURE

    public int GetBulletPrice(Weapon w) {
        switch (w) {
            case GUN -> {
                return 0;
            }
            case FB -> {
                return 3;
            }
            case LASER -> {
                return 6;
            }
            case BUBBLE -> {
                return 2;
            }
            case FIRE -> {
                return 1;
            }
            case STAR -> {
                return 7;
            }
            case BALL -> {
                return 4;
            }
            case GRENADE -> {
                return 9;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + w);
        }
    }

    private boolean CheckBullet(Weapon w) {
        { // WITH
            ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
            if ((_weaponAttr.nbBullet > 0) && (_weaponAttr.power > 0)) {
                if (chaosBase.freeFire == 0)
                    _weaponAttr.nbBullet--;
                if (_weaponAttr.power < 4)
                    chaosBase.shoot.total += _weaponAttr.power;
                else
                    chaosBase.shoot.total += 3;
                return true;
            } else {
                if (_weaponAttr.power == 0)
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("not enough power"), String.class), ChaosActions.actionPos, 1);
                else
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("no bullet"), String.class), ChaosActions.actionPos, 1);
                return false;
            }
        }
    }

    private boolean CheckBomb(Weapon w) {
        { // WITH
            ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
            if ((_weaponAttr.nbBomb > 0) && (_weaponAttr.power > 0)) {
                _weaponAttr.nbBomb--;
                return true;
            } else {
                return false;
            }
        }
    }

    private int GetPower(Weapon w) {
        if (chaosBase.maxPower == 0)
            return chaosBase.weaponAttr[w.ordinal()].power;
        else
            return 4;
    }

    private void StdFire(ChaosBase.Obj player, Weapon w, int speed, boolean addV, boolean stdV) {
        // CONST
        final int rOff = 7;
        final int dOff = 5;

        // VAR
        ChaosBase.ObjAttr wattr = null;
        int nvx = 0;
        int nvy = 0;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int diaspeed = 0;

        chaosActions.GetCenter(player, px, py);
        if (stdV || ((Math.abs(player.vx) < 64) && (Math.abs(player.vy) < 64))) {
            nvx = 0;
            nvy = 0;
            diaspeed = speed * 5 / 7;
            switch (player.shapeSeq) {
                case 0 -> {
                    nvy = -speed;
                    py.dec(rOff);
                }
                case 1 -> {
                    nvy = -diaspeed;
                    nvx = diaspeed;
                    py.dec(dOff);
                    px.inc(dOff);
                }
                case 2 -> {
                    nvx = speed;
                    px.inc(rOff);
                }
                case 3 -> {
                    nvx = diaspeed;
                    nvy = nvx;
                    px.inc(dOff);
                    py.inc(dOff);
                }
                case 4 -> {
                    nvy = speed;
                    py.inc(rOff);
                }
                case 5 -> {
                    nvy = diaspeed;
                    nvx = -diaspeed;
                    px.dec(dOff);
                    py.inc(dOff);
                }
                case 6 -> {
                    nvx = -speed;
                    px.dec(rOff);
                }
                case 7 -> {
                    nvx = -diaspeed;
                    nvy = nvx;
                    px.dec(dOff);
                    py.dec(dOff);
                }
                default -> throw new RuntimeException("Unhandled CASE value " + player.shapeSeq);
            }
        } else {
            nvx = player.vx / 32;
            nvy = player.vy / 32;
            diaspeed = trigo.SQRT(nvx * nvx + nvy * nvy);
            speed = speed / 32;
            nvx = (nvx * speed / diaspeed) * 32;
            nvy = (nvy * speed / diaspeed) * 32;
        }
        if (addV) {
            nvx += player.vx;
            nvy += player.vy;
            if (nvx >= 4096)
                nvx = 4095;
            else if (nvx <= -4096)
                nvx = -4095;
            if (nvy >= 4096)
                nvy = 4095;
            else if (nvy <= -4096)
                nvy = -4095;
        }
        if (w != Weapon.BUBBLE) {
            wattr = chaosBase.GetAnimAttr(Anims.WEAPON, w.ordinal());
            player.vx -= nvx / 128 * wattr.weight;
            player.vy -= nvy / 128 * wattr.weight;
        }
        newWeapon = chaosActions.CreateObj(Anims.WEAPON, w.ordinal(), px.get(), py.get(), 0, 0);
        chaosActions.SetObjVXY(newWeapon, nvx, nvy);
    }

    private void StdBomb(ChaosBase.Obj player, Weapon w, int speed, int nstat, int count, boolean addV) {
        // VAR
        ChaosBase.Obj obj = null;
        int cnt = 0;
        int angle = 0;
        int base = 0;
        int power = 0;
        Runtime.Ref<Integer> cx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> cy = new Runtime.Ref<>(0);
        int nvx = 0;
        int nvy = 0;

        power = GetPower(w);
        chaosActions.GetCenter(player, cx, cy);
        speed = speed / 128;
        base = trigo.RND() % (60 / count + 1);
        for (cnt = 0; cnt < count; cnt++) {
            angle = cnt * 360 / count + base;
            nvx = (speed * trigo.COS(angle)) / 8;
            nvy = (speed * trigo.SIN(angle)) / 8;
            if (addV) {
                nvx += player.vx;
                nvy += player.vy;
                if (nvx >= 4096)
                    nvx = 4095;
                else if (nvx <= -4096)
                    nvx = -4095;
                if (nvy >= 4096)
                    nvy = 4095;
                else if (nvy <= -4096)
                    nvy = -4095;
            }
            obj = chaosActions.CreateObj(Anims.WEAPON, w.ordinal(), cx.get(), cy.get(), nstat, 0);
            chaosActions.SetObjVXY(obj, nvx, nvy);
        }
    }

    private void SetNextFollowAnim(int num) {
        { // WITH
            FollowData _followData = follow[num - 1];
            do {
                if (_followData.anim == Anims.DEAD /* MAX(Anims) */) {
                    _followData.anim = Anims.PLAYER /* MIN(Anims) */;
                    _followData.finish = true;
                } else {
                    _followData.anim = Runtime.next(_followData.anim);
                }
            } while (!Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1).contains(_followData.anim));
            _followData.currentNode = memory.FirstNode(chaosBase.animList[_followData.anim.ordinal()]);
            _followData.tailNode = memory.TailNode(chaosBase.animList[_followData.anim.ordinal()]);
        }
    }

    private void ResetFollow(int num, long time) {
        { // WITH
            FollowData _followData = follow[num - 1];
            _followData.anim = Anims.PLAYER /* MIN(Anims) */;
            _followData.finish = false;
            SetNextFollowAnim(num);
            _followData.bestObj = null;
            _followData.bestCount = ((1 << 16) - 1) /* MAX(CARDINAL) */;
            _followData.bestDist = ((1 << 16) - 1) /* MAX(CARDINAL) */;
            _followData.lasttime = time;
            _followData.wait = 10;
        }
    }

    private int AllocFollow(long time) {
        // VAR
        int num = 0;

        num = MAXF;
        while (num > 0) {
            if (!follow[num - 1].used) {
                follow[num - 1].used = true;
                ResetFollow(num, time);
                return num;
            }
            num--;
        }
        return 0;
    }

    private void FreeFollow(int num) {
        if (num != 0)
            follow[num - 1].used = false;
    }

    private void CheckFollow(int num, long newtime, long step, ChaosBase.Obj src, boolean chkall) {
        // VAR
        long rx = 0L;
        long ry = 0L;
        Runtime.Ref<Integer> sx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> sy = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> cx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> cy = new Runtime.Ref<>(0);
        int count = 0;
        int dist = 0;
        int cnt = 0;
        int rem = 0;

        { // WITH
            FollowData _followData = follow[num - 1];
            if ((_followData.wait == 0) && (!Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1).contains(_followData.bestObj.kind) || (_followData.bestObj.hitSubLife + _followData.bestObj.fireSubLife == 0)))
                ResetFollow(num, _followData.lasttime);
            while (_followData.lasttime < newtime) {
                _followData.lasttime += step;
                if (!_followData.finish) {
                    if ((_followData.wait > 0) && (_followData.bestObj != null))
                        _followData.wait--;
                    if ((_followData.currentNode == null) || (_followData.currentNode == _followData.tailNode)) {
                        SetNextFollowAnim(num);
                    } else if (obj(_followData.currentNode).kind != _followData.anim) {
                        _followData.currentNode = memory.FirstNode(chaosBase.animList[_followData.anim.ordinal()]);
                    } else {
                        count = 0;
                        if (chkall) {
                            for (cnt = 1; cnt <= MAXF; cnt++) {
                                if (follow[cnt - 1].used && (follow[cnt - 1].bestObj == obj(_followData.currentNode)))
                                    count++;
                            }
                            if (count > 4)
                                count = 4;
                        }
                        chaosActions.GetCenter(src, sx, sy);
                        chaosActions.GetCenter(obj(_followData.currentNode), cx, cy);
                        rem = obj(_followData.currentNode).hitSubLife + obj(_followData.currentNode).fireSubLife;
                        if (rem > 100)
                            rem = 100;
                        rx = sx.get() + src.vx / 32 - cx.get();
                        ry = sy.get() + src.vy / 32 - cy.get();
                        dist = trigo.SQRT(rx * rx + ry * ry);
                        dist += 100 - rem;
                        dist += count * 128;
                        if ((dist < _followData.bestDist) && (rem > 0)) {
                            _followData.bestObj = obj(_followData.currentNode);
                            _followData.bestCount = count;
                            _followData.bestDist = dist;
                        }
                        _followData.currentNode = memory.NextNode(_followData.currentNode);
                    }
                } else if (_followData.bestObj != null) {
                    _followData.wait = 0;
                } else {
                    ResetFollow(num, _followData.lasttime);
                }
            }
        }
    }
    
    private static ChaosBase.Obj obj(Memory.Node node) { /* PATCH */
        return (ChaosBase.Obj) node.data;
    }

    private void MakeGun(ChaosBase.Obj gun) {
        chaosActions.SetObjLoc(gun, 240, 20, 6, 6);
        chaosActions.SetObjRect(gun, 0, 0, 6, 6);
    }

    private final ChaosBase.MakeProc MakeGun_ref = this::MakeGun;

    private void MakeFB(ChaosBase.Obj fb) {
        // VAR
        int px = 0;
        int py = 0;

        if (fb.stat == 0) {
            if (fb.temperature < 15000) {
                px = 0;
                py = 56;
            } else if (fb.temperature < 21000) {
                px = 0;
                py = 66;
            } else if (fb.shapeSeq == 0) {
                px = 10;
                py = 56;
            } else {
                px = 10;
                py = 66;
            }
            chaosActions.SetObjLoc(fb, px, py, 10, 10);
            chaosActions.SetObjRect(fb, 0, 0, 10, 10);
        } else {
            chaosActions.SetObjLoc(fb, 192, 56, 32, 32);
            chaosActions.SetObjRect(fb, 0, 0, 32, 32);
        }
    }

    private final ChaosBase.MakeProc MakeFB_ref = this::MakeFB;

    private void MakeLaser(ChaosBase.Obj laser) {
        // VAR
        int dt = 0;
        int sz = 0;
        int py = 0;

        if (laser.life <= 70)
            dt = 1;
        else if (laser.life <= 100)
            dt = 2;
        else if (laser.life <= 130)
            dt = 3;
        else
            dt = 4;
        sz = dt * 2 + 2;
        if (laser.shapeSeq == 0)
            py = 50;
        else
            py = 60;
        chaosActions.SetObjLoc(laser, 238 - dt, py - dt, sz, sz);
        chaosActions.SetObjRect(laser, 0, 0, sz, sz);
    }

    private final ChaosBase.MakeProc MakeLaser_ref = this::MakeLaser;

    private void MakeBubble(ChaosBase.Obj bubble) {
        // VAR
        int px = 0;
        int py = 0;
        int sz = 0;

        sz = 10;
        if (bubble.life <= 20) {
            px = 20;
            py = 56;
            sz = 8;
        } else if (bubble.life <= 30) {
            px = 20;
            py = 66;
        } else if (bubble.life <= 40) {
            px = 30;
            py = 56;
        } else {
            px = 30;
            py = 66;
        }
        chaosActions.SetObjLoc(bubble, px, py, sz, sz);
        chaosActions.SetObjRect(bubble, 0, 0, sz, sz);
    }

    private final ChaosBase.MakeProc MakeBubble_ref = this::MakeBubble;

    private void MakeFire(ChaosBase.Obj fire) {
        // VAR
        int px = 0;
        int sz = 0;

        if (fire.life <= 1) {
            px = 169;
            sz = 5;
        } else if (fire.life == 2) {
            px = 153;
            sz = 8;
        } else {
            px = 131;
            sz = 11;
        }
        if (fire.shapeSeq != 0)
            px += sz;
        if (px != 161)
            chaosActions.SetObjLoc(fire, px, 54, sz, sz);
        else // Replace 161x54 by 248x236, because 161x54 overlaps Small Drawer and has been moved
            chaosActions.SetObjLoc(fire, 248, 236, sz, sz);
        sz++;
        chaosActions.SetObjRect(fire, -1, -1, sz, sz);
    }

    private final ChaosBase.MakeProc MakeFire_ref = this::MakeFire;

    private void MakeBall(ChaosBase.Obj ball) {
        // VAR
        int offset = 0;
        int px = 0;
        int py = 0;
        int max = 0;

        if (ball.life > 50) {
            offset = 22;
            max = 3;
        } else if (ball.life > 40) {
            offset = 18;
            max = 4;
        } else if (ball.life > 30) {
            offset = 12;
            max = 6;
        } else {
            offset = 0;
            max = 12;
        }
        offset += ball.stat % max;
        px = (offset % 10) * 11 + 76;
        py = (offset / 10) * 11 + 32;
        chaosActions.SetObjLoc(ball, px, py, 11, 11);
        chaosActions.SetObjRect(ball, 0, 0, 11, 11);
    }

    private final ChaosBase.MakeProc MakeBall_ref = this::MakeBall;

    private void MakeStar(ChaosBase.Obj star) {
        // VAR
        int py = 0;

        if (star.shapeSeq / 32 == 0)
            py = 0;
        else
            py = 12;
        chaosActions.SetObjLoc(star, 146, py, 12, 12);
        chaosActions.SetObjRect(star, 0, 0, 12, 12);
    }

    private final ChaosBase.MakeProc MakeStar_ref = this::MakeStar;

    private void MakeGrenade(ChaosBase.Obj grenade) {
        // VAR
        int px = 0;
        int py = 0;

        if (grenade.stat == 0) {
            chaosActions.SetObjLoc(grenade, 244, 52, 12, 8);
            chaosActions.SetObjRect(grenade, 0, 0, 12, 8);
        } else {
            if (grenade.stat == 1) {
                px = 244;
                py = 60;
            } else if (grenade.stat == 2) {
                px = 244;
                py = 72;
            } else {
                px = 232;
                py = 72;
            }
            chaosActions.SetObjLoc(grenade, px, py, 12, 12);
            chaosActions.SetObjRect(grenade, 0, 0, 12, 12);
        }
    }

    private final ChaosBase.MakeProc MakeGrenade_ref = this::MakeGrenade;

    private void ResetGun(ChaosBase.Obj gun) {
        // VAR
        int power = 0;

        power = GetPower(Weapon.GUN);
        gun.life = 8 + power * 2;
        if (gun.life == 16)
            gun.life = 24;
        gun.moveSeq = ChaosBase.Period * 2 + ChaosBase.Period / 2 * power;
        gun.hitSubLife = gun.life / 2;
        gun.fireSubLife = gun.life - gun.hitSubLife;
        MakeGun(gun);
    }

    private final ChaosBase.ResetProc ResetGun_ref = this::ResetGun;

    private void ResetFB(ChaosBase.Obj fb) {
        // VAR
        int p = 0;

        fb.moveSeq = ChaosBase.Period * 30;
        fb.shapeSeq = trigo.RND() % 2;
        p = GetPower(Weapon.FB);
        fb.life = 37 + p * 5;
        fb.hitSubLife = 7;
        fb.fireSubLife = 28;
        if ((fb.stat == 0) && (p >= 3))
            fb.moveSeq = 1;
        else
            fb.moveSeq = 0;
        if (fb.stat == 0) {
            fb.temperature = p * 6000;
        } else {
            fb.hitSubLife = 0;
            fb.fireSubLife = 0;
        }
        MakeFB(fb);
    }

    private final ChaosBase.ResetProc ResetFB_ref = this::ResetFB;

    private void ResetLaser(ChaosBase.Obj laser) {
        // VAR
        int power = 0;

        power = GetPower(Weapon.LASER);
        laser.shapeSeq = trigo.RND() % 2;
        laser.moveSeq = ChaosBase.Period / 8;
        laser.life = power * 30 + 40;
        if (laser.stat == 1)
            laser.life += 50;
        if (power == 4)
            laser.stat = AllocFollow(chaosBase.lasttime);
        else
            laser.stat = 0;
        laser.fireSubLife = laser.life / 5;
        laser.hitSubLife = laser.life - laser.fireSubLife;
        MakeLaser(laser);
    }

    private final ChaosBase.ResetProc ResetLaser_ref = this::ResetLaser;

    private void ResetBubble(ChaosBase.Obj bubble) {
        // VAR
        int power = 0;

        power = GetPower(Weapon.BUBBLE);
        bubble.moveSeq = ChaosBase.Period * 30;
        bubble.life = power * 10 + 8;
        bubble.fireSubLife = 0;
        bubble.hitSubLife = bubble.life;
        MakeBubble(bubble);
    }

    private final ChaosBase.ResetProc ResetBubble_ref = this::ResetBubble;

    private void ResetFire(ChaosBase.Obj fire) {
        fire.hitSubLife = 0;
        fire.fireSubLife = fire.life;
        fire.moveSeq = ChaosBase.Period / 3;
        fire.shapeSeq = trigo.RND() % 2;
        MakeFire(fire);
    }

    private final ChaosBase.ResetProc ResetFire_ref = this::ResetFire;

    private void ResetBall(ChaosBase.Obj ball) {
        ball.life = GetPower(Weapon.BALL) * 10 + 20;
        ball.hitSubLife = ball.life * 2 / 5;
        ball.fireSubLife = ball.life - ball.hitSubLife;
        ball.stat = trigo.RND() % 12;
        if (trigo.RND() % 2 == 0)
            ball.flags.add(ObjFlags.nested);
        else
            ball.flags.remove(ObjFlags.nested);
        ball.shapeSeq = ChaosBase.Period / 10;
        ball.moveSeq = AllocFollow(chaosBase.lasttime);
        MakeBall(ball);
    }

    private final ChaosBase.ResetProc ResetBall_ref = this::ResetBall;

    private void ResetStar(ChaosBase.Obj star) {
        // VAR
        int power = 0;

        power = GetPower(Weapon.STAR);
        star.life = power * 20;
        star.fireSubLife = star.life * 2 / 5;
        star.hitSubLife = star.life - star.fireSubLife;
        if (star.flags.contains(ObjFlags.displayed)) {
            star.stat = ChaosBase.Period / 6;
        } else {
            star.moveSeq = ChaosBase.Period * 29 + ChaosBase.Period / 2;
            star.shapeSeq = (trigo.RND() % 2) * 32;
            star.stat = 0;
        }
        MakeStar(star);
    }

    private final ChaosBase.ResetProc ResetStar_ref = this::ResetStar;

    private void ResetGrenade(ChaosBase.Obj grenade) {
        grenade.shapeSeq = ChaosBase.Period / 5;
        if (grenade.stat != 0) {
            grenade.temperature = ChaosBase.MaxHot;
            grenade.moveSeq = ChaosBase.Period * 30;
        } else {
            grenade.moveSeq = ChaosBase.Period * 3;
        }
        grenade.life = 1;
        MakeGrenade(grenade);
    }

    private final ChaosBase.ResetProc ResetGrenade_ref = this::ResetGrenade;

    private void MoveGun(ChaosBase.Obj gun) {
        // VAR
        boolean kill = false;

        chaosActions.DoCollision(gun, Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1), chaosActions.Aie_ref, new Runtime.FieldRef<>(gun::getHitSubLife, gun::setHitSubLife), new Runtime.FieldRef<>(gun::getFireSubLife, gun::setFireSubLife));
        kill = chaosActions.InBackground(gun);
        if (kill) {
            chaosSounds.SoundEffect(gun, gunKillEffect);
            gun.vx = 0;
            gun.vy = 0;
            chaosActions.Boum(gun, EnumSet.of(Stones.stFLAME2), ChaosBase.slowStyle, GetPower(Weapon.GUN), 1);
        }
        if (kill || chaosActions.OutOfScreen(gun) || chaosActions.OutOfBounds(gun) || (chaosBase.step > gun.moveSeq) || (gun.hitSubLife + gun.fireSubLife < gun.life)) {
            chaosActions.Die(gun);
            return;
        }
        gun.moveSeq -= chaosBase.step;
        chaosActions.UpdateXY(gun);
    }

    private final ChaosBase.MoveProc MoveGun_ref = this::MoveGun;

    private void KillIt(ChaosBase.Obj victim, ChaosBase.Obj src, /* var */ Runtime.IRef<Integer> hit, /* var */ Runtime.IRef<Integer> fire) {
        src.hitSubLife = 100;
        src.fireSubLife = 100;
        chaosActions.Die(victim);
    }

    private final ChaosBase.AieProc KillIt_ref = this::KillIt;

    private void MoveFB(ChaosBase.Obj fb) {
        // VAR
        ChaosBase.AieProc Do = null;
        boolean kill = false;

        if (fb.stat == 0)
            Do = chaosActions.Aie_ref;
        else
            Do = KillIt_ref;
        chaosActions.DoCollision(fb, Runtime.withRange(EnumSet.of(Anims.MISSILE, Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1), Do, new Runtime.FieldRef<>(fb::getHitSubLife, fb::setHitSubLife), new Runtime.FieldRef<>(fb::getFireSubLife, fb::setFireSubLife));
        kill = (fb.stat == 0) && chaosActions.InBackground(fb);
        if (kill) {
            chaosSounds.SoundEffect(fb, fbKillEffect);
            fb.vx = fb.vx / 4;
            fb.vy = fb.vy / 4;
            chaosActions.Boum(fb, EnumSet.of(Stones.stFOG2), ChaosBase.slowStyle, GetPower(Weapon.FB) * 2, 1);
        }
        if (kill || chaosActions.OutOfScreen(fb) || chaosActions.OutOfBounds(fb) || ((fb.stat == 0) && (fb.hitSubLife + fb.fireSubLife == 0))) {
            chaosActions.Die(fb);
            return;
        }
        if (fb.moveSeq > 0) {
            if (chaosBase.step >= fb.moveSeq) {
                if (fb.stat == 0) {
                    fb.shapeSeq = 1 - fb.shapeSeq;
                    MakeFB(fb);
                    if (fb.hitSubLife < 7)
                        fb.hitSubLife++;
                    if (fb.fireSubLife < 28)
                        fb.fireSubLife++;
                    fb.moveSeq += ChaosBase.Period / 16;
                } else {
                    chaosActions.Die(fb);
                    return;
                }
            }
            if (chaosBase.step >= fb.moveSeq)
                fb.moveSeq = 1;
            else
                fb.moveSeq -= chaosBase.step;
        }
        chaosActions.Burn(fb);
        chaosActions.UpdateXY(fb);
    }

    private final ChaosBase.MoveProc MoveFB_ref = this::MoveFB;

    private void MoveLaser(ChaosBase.Obj laser) {
        // VAR
        long rx = 0L;
        long ry = 0L;
        long rl = 0L;
        Runtime.Ref<Integer> bx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> by = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> lx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ly = new Runtime.Ref<>(0);
        boolean kill = false;

        chaosActions.DoCollision(laser, Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1), chaosActions.Aie_ref, new Runtime.FieldRef<>(laser::getHitSubLife, laser::setHitSubLife), new Runtime.FieldRef<>(laser::getFireSubLife, laser::setFireSubLife));
        laser.life = laser.hitSubLife + laser.fireSubLife;
        kill = chaosActions.InBackground(laser);
        if (kill) {
            chaosSounds.SoundEffect(laser, gunKillEffect);
            laser.vx = 0;
            laser.vy = 0;
            chaosActions.Boum(laser, EnumSet.of(Stones.stFLAME1), ChaosBase.slowStyle, GetPower(Weapon.LASER) * 2, 1);
        }
        if (kill || chaosActions.OutOfScreen(laser) || chaosActions.OutOfBounds(laser) || (laser.life == 0)) {
            FreeFollow(laser.stat);
            chaosActions.Die(laser);
            return;
        }
        if (chaosBase.step >= laser.moveSeq) {
            laser.shapeSeq = 1 - laser.shapeSeq;
            MakeLaser(laser);
            if ((chaosBase.nbAnim[Anims.ALIEN3.ordinal()] + chaosBase.nbAnim[Anims.ALIEN2.ordinal()] + chaosBase.nbAnim[Anims.ALIEN1.ordinal()] == 0) && (Math.abs(laser.vx) < 384) && (Math.abs(laser.vy) < 384))
                laser.ay = 1;
            laser.moveSeq += ChaosBase.Period / 8;
        }
        if (chaosBase.step > laser.moveSeq)
            laser.moveSeq = 0;
        else
            laser.moveSeq -= chaosBase.step;
        if (laser.stat != 0) {
            CheckFollow(laser.stat, chaosBase.lasttime, ChaosBase.Period / 50, laser, true);
            { // WITH
                FollowData _followData = follow[laser.stat - 1];
                if (_followData.wait == 0) {
                    chaosActions.GetCenter(_followData.bestObj, bx, by);
                    bx.inc(_followData.bestObj.vx / 128);
                    by.inc(_followData.bestObj.vy / 128);
                    chaosActions.GetCenter(laser, lx, ly);
                    rx = bx.get() - lx.get();
                    ry = by.get() - ly.get();
                    rl = trigo.SQRT(rx * rx + ry * ry);
                    if (rl != 0) {
                        rx = rx * 95 / rl;
                        ry = ry * 95 / rl;
                    }
                    rx -= laser.vx / 128;
                    ry -= laser.vy / 128;
                    if (rx > 127)
                        rx = 127;
                    else if (rx < -127)
                        rx = -127;
                    if (ry > 127)
                        ry = 127;
                    else if (ry < -127)
                        ry = -127;
                    chaosActions.SetObjAXY(laser, (int) rx, (int) ry);
                }
                _followData.finish = false;
            }
        }
        chaosActions.UpdateXY(laser);
    }

    private final ChaosBase.MoveProc MoveLaser_ref = this::MoveLaser;

    private void MoveBubble(ChaosBase.Obj bubble) {
        // VAR
        ChaosBase.Obj clone = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int nvx = 0;
        int nvy = 0;

        chaosActions.DoCollision(bubble, Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1), chaosActions.Aie_ref, new Runtime.FieldRef<>(bubble::getHitSubLife, bubble::setHitSubLife), new Runtime.FieldRef<>(bubble::getFireSubLife, bubble::setFireSubLife));
        if ((bubble.hitSubLife == 0) || chaosActions.OutOfScreen(bubble) || chaosActions.OutOfBounds(bubble) || (chaosBase.step > bubble.moveSeq)) {
            chaosActions.Die(bubble);
            return;
        } else if ((bubble.life > bubble.hitSubLife) || chaosActions.InBackground(bubble)) {
            if (bubble.stat == 0) {
                if ((bubble.life > bubble.hitSubLife) || (bubble.hitSubLife <= 5)) {
                    if (bubble.life == bubble.hitSubLife)
                        chaosSounds.SoundEffect(bubble, gunKillEffect);
                    chaosActions.Die(bubble);
                    return;
                } else {
                    chaosActions.AvoidBackground(bubble, 4);
                    bubble.life -= 5;
                    bubble.hitSubLife -= 5;
                }
            } else {
                bubble.stat--;
                bubble.life = bubble.hitSubLife;
                MakeBubble(bubble);
                chaosActions.AvoidBackground(bubble, 4);
                chaosActions.GetCenter(bubble, px, py);
                nvx = bubble.vx - 1024;
                nvy = bubble.vy - 1024;
                nvx += trigo.RND() % 2048;
                nvy += trigo.RND() % 2048;
                clone = chaosActions.CreateObj(Anims.WEAPON, Weapon.BUBBLE.ordinal(), px.get(), py.get(), bubble.stat, 0);
                chaosActions.SetObjVXY(clone, nvx, nvy);
            }
        }
        bubble.moveSeq -= chaosBase.step;
        chaosActions.UpdateXY(bubble);
    }

    private final ChaosBase.MoveProc MoveBubble_ref = this::MoveBubble;

    private void MoveFire(ChaosBase.Obj fire) {
        if (fire.stat == 0)
            chaosActions.DoCollision(fire, Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1), chaosActions.Aie_ref, new Runtime.FieldRef<>(fire::getHitSubLife, fire::setHitSubLife), new Runtime.FieldRef<>(fire::getFireSubLife, fire::setFireSubLife));
        if ((fire.life > fire.fireSubLife) || chaosActions.InBackground(fire) || (fire.life == 0) || chaosBase.water) {
            fire.vx = fire.vx / 2;
            fire.vy = fire.vy / 2;
            chaosActions.Die(fire);
            return;
        }
        fire.shapeSeq = 1 - fire.shapeSeq;
        if (chaosBase.step > fire.stat)
            fire.stat = 0;
        else
            fire.stat -= chaosBase.step;
        if (chaosBase.step >= fire.moveSeq) {
            fire.moveSeq += ChaosBase.Period / 4;
            fire.life -= (fire.life / 5) + 1;
            fire.fireSubLife = fire.life;
        }
        fire.moveSeq -= chaosBase.step;
        MakeFire(fire);
        chaosActions.UpdateXY(fire);
    }

    private final ChaosBase.MoveProc MoveFire_ref = this::MoveFire;

    private void MoveBall(ChaosBase.Obj ball) {
        // VAR
        long rx = 0L;
        long ry = 0L;
        long rl = 0L;
        int dx = 0;
        int dy = 0;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fy = new Runtime.Ref<>(0);
        int dv = 0;
        int ds = 0;
        int spd = 0;
        boolean kill = false;

        chaosActions.DoCollision(ball, Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1), chaosActions.Aie_ref, new Runtime.FieldRef<>(ball::getHitSubLife, ball::setHitSubLife), new Runtime.FieldRef<>(ball::getFireSubLife, ball::setFireSubLife));
        ball.life = ball.hitSubLife + ball.fireSubLife;
        ball.hitSubLife = ball.life * 2 / 5;
        ball.fireSubLife = ball.life - ball.hitSubLife;
        kill = chaosActions.InBackground(ball);
        if (kill) {
            chaosSounds.SoundEffect(ball, fbKillEffect);
            ball.vx = ball.vx / 4;
            ball.vy = ball.vy / 4;
            chaosActions.Boum(ball, EnumSet.of(Stones.stFOG2), ChaosBase.slowStyle, ball.life / 16 + 1, 1);
        }
        if (chaosActions.OutOfScreen(ball) || chaosActions.OutOfBounds(ball) || kill || (ball.life == 0) || ((ball.vx == 0) && (ball.vy == 0))) {
            FreeFollow(ball.moveSeq);
            chaosActions.Die(ball);
            return;
        }
        if (chaosBase.step >= ball.shapeSeq) {
            ball.shapeSeq += ChaosBase.Period / 10;
            if (ball.flags.contains(ObjFlags.nested))
                ball.stat = (ball.stat + 1) % 12;
            else if (ball.stat == 0)
                ball.stat = 11;
            else
                ball.stat--;
            MakeBall(ball);
        }
        if (chaosBase.step > ball.shapeSeq)
            ball.shapeSeq = 0;
        else
            ball.shapeSeq -= chaosBase.step;
        rx = ball.vx;
        ry = ball.vy;
        rl = trigo.SQRT(rx * rx + ry * ry);
        if (rl != 0) {
            ball.vx = (int) (rx * 2200 / rl);
            ball.vy = (int) (ry * 2200 / rl);
        } else {
            ball.vx = 2200;
        }
        if (ball.moveSeq == 0) {
            ball.moveSeq = AllocFollow(chaosBase.lasttime);
        } else {
            if (ball.life > 50)
                spd = ChaosBase.Period / 30;
            else
                spd = ChaosBase.Period / 12;
            CheckFollow(ball.moveSeq, chaosBase.lasttime, spd, ball, (ball.life > 50));
            { // WITH
                FollowData _followData = follow[ball.moveSeq - 1];
                if (_followData.wait == 0) {
                    chaosActions.GetCenter(_followData.bestObj, fx, fy);
                    chaosActions.GetCenter(ball, px, py);
                    dx = fx.get() - px.get();
                    dy = fy.get() - py.get();
                    while (Math.abs(dx) > 256) {
                        dx = dx / 2;
                    }
                    while (Math.abs(dy) > 256) {
                        dy = dy / 2;
                    }
                    fx.set(ball.vy / 64);
                    fy.set(ball.vx / 64);
                    if ((fx.get() * dx) < (fy.get() * dy))
                        fx.set(-fx.get());
                    else
                        fy.set(-fy.get());
                    dv = chaosBase.step;
                    if (ball.life > 50)
                        ds = 9;
                    else if (ball.life > 40)
                        ds = 7;
                    else if (ball.life > 30)
                        ds = 5;
                    else
                        ds = 3;
                    ball.vx += fx.get() * dv * ds / 16;
                    ball.vy += fy.get() * dv * ds / 16;
                    ball.dvx = ball.vx;
                    ball.dvy = ball.vy;
                }
            }
        }
        chaosActions.UpdateXY(ball);
    }

    private final ChaosBase.MoveProc MoveBall_ref = this::MoveBall;

    private void MoveStar(ChaosBase.Obj star) {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> sx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> sy = new Runtime.Ref<>(0);
        long nvx = 0L;
        long nvy = 0L;
        long nvl = 0L;
        EnumSet<Anims> anims = EnumSet.noneOf(Anims.class);

        if ((star.ax != 0) || (star.ay != 0))
            throw new HaltException();
        if (star.stat == 0) {
            anims = Runtime.withRange(EnumSet.noneOf(Anims.class), Anims.ALIEN3, Anims.ALIEN1);
            if (chaosBase.weaponAttr[Weapon.STAR.ordinal()].power > 2)
                anims.add(Anims.MACHINE);
            chaosActions.DoCollision(star, anims, chaosActions.Aie_ref, new Runtime.FieldRef<>(star::getHitSubLife, star::setHitSubLife), new Runtime.FieldRef<>(star::getFireSubLife, star::setFireSubLife));
            if (star.life > star.hitSubLife + star.fireSubLife)
                ResetStar(star);
        } else if (chaosBase.step > star.stat) {
            star.stat = 0;
        } else {
            star.stat -= chaosBase.step;
        }
        if ((chaosBase.step > star.moveSeq) || chaosActions.OutOfScreen(star) || ((star.moveSeq < ChaosBase.Period * 29) && chaosActions.Collision(star, chaosBase.mainPlayer))) {
            chaosActions.Die(star);
            return;
        }
        star.moveSeq -= chaosBase.step;
        star.shapeSeq = (star.shapeSeq + chaosBase.step) % 64;
        if (star.moveSeq < ChaosBase.Period * 29) {
            chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
            chaosActions.GetCenter(star, sx, sy);
            nvx = px.get() - sx.get();
            nvy = py.get() - sy.get();
            nvl = trigo.SQRT(nvx * nvx + nvy * nvy);
            if (nvl != 0) {
                nvx = nvx * 3000 / nvl;
                nvy = nvy * 3000 / nvl;
            }
            star.dvx = (int) nvx;
            star.dvy = (int) nvy;
        }
        MakeStar(star);
        chaosActions.UpdateXY(star);
        star.flags.add(ObjFlags.displayed);
    }

    private final ChaosBase.MoveProc MoveStar_ref = this::MoveStar;

    private void MoveGrenade(ChaosBase.Obj grenade) {
        grenade.dvx = 0;
        grenade.dvy = 0;
        chaosActions.AvoidBounds(grenade, 4);
        chaosActions.AvoidBackground(grenade, 4);
        if (grenade.stat != 0) {
            if (chaosBase.step >= grenade.shapeSeq) {
                grenade.shapeSeq += ChaosBase.Period / 5;
                if (grenade.stat == 3)
                    grenade.stat = 1;
                else
                    grenade.stat++;
                MakeGrenade(grenade);
            }
            chaosBase.mainPlayer = grenade;
            grenade.shapeSeq -= chaosBase.step;
            chaosActions.Burn(grenade);
            chaosActions.Gravity(grenade, Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1));
        }
        if (chaosBase.step > grenade.moveSeq) {
            if (grenade.stat == 0) {
                if (GetPower(Weapon.GRENADE) == 4) {
                    StdBomb(grenade, Weapon.BUBBLE, 3000, 0, 15, false);
                    chaosSounds.SoundEffect(grenade, bubbleBombEffect);
                } else {
                    chaosSounds.SoundEffect(grenade, grenadeBombEffect);
                    StdBomb(grenade, Weapon.GUN, 3250, 1, 3, false);
                }
            }
            chaosActions.Die(grenade);
            return;
        } else {
            grenade.moveSeq -= chaosBase.step;
        }
        chaosActions.UpdateXY(grenade);
    }

    private final ChaosBase.MoveProc MoveGrenade_ref = this::MoveGrenade;

    private void FireGun(ChaosBase.Obj player) {
        chaosSounds.SoundEffect(player, gunFireEffect);
        chaosBase.shoot.total++;
        StdFire(player, Weapon.GUN, 3250, false, false);
    }

    private final ChaosBase.FireProc FireGun_ref = this::FireGun;

    private void FireFB(ChaosBase.Obj player) {
        if (CheckBullet(Weapon.FB)) {
            chaosSounds.SoundEffect(player, fbFireEffect);
            StdFire(player, Weapon.FB, 2600, false, false);
        }
    }

    private final ChaosBase.FireProc FireFB_ref = this::FireFB;

    private void FireLaser(ChaosBase.Obj player) {
        if (CheckBullet(Weapon.LASER)) {
            chaosSounds.SoundEffect(player, laserFireEffect);
            StdFire(player, Weapon.LASER, 4090, false, true);
        }
    }

    private final ChaosBase.FireProc FireLaser_ref = this::FireLaser;

    private void FireBubble(ChaosBase.Obj player) {
        // VAR
        int bvx = 0;
        int bvy = 0;

        if (CheckBullet(Weapon.BUBBLE)) {
            chaosSounds.StereoEffect();
            chaosSounds.SoundEffect(player, bubbleFireEffectL);
            chaosSounds.SoundEffect(player, bubbleFireEffectR);
            StdFire(player, Weapon.BUBBLE, 2000, true, false);
            if ((GetPower(Weapon.BUBBLE) == 4) && (newWeapon != chaosActions.nlObj)) {
                bvx = newWeapon.vx / 4;
                bvy = newWeapon.vy / 4;
                StdFire(player, Weapon.BUBBLE, 2000, true, false);
                chaosActions.SetObjVXY(newWeapon, newWeapon.vx + bvy, newWeapon.vy - bvx);
                StdFire(player, Weapon.BUBBLE, 2000, true, false);
                chaosActions.SetObjVXY(newWeapon, newWeapon.vx - bvy, newWeapon.vy + bvx);
            }
        }
    }

    private final ChaosBase.FireProc FireBubble_ref = this::FireBubble;

    private void FireFire(ChaosBase.Obj player) {
        // CONST
        final int speed = 4000;
        final int diaspeed = 2828;

        // VAR
        ChaosBase.Obj fire = null;
        int power = 0;
        int cnt = 0;
        int nvx = 0;
        int nvy = 0;
        int svx = 0;
        int svy = 0;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);

        if (CheckBullet(Weapon.FIRE)) {
            chaosSounds.SoundEffect(player, fireFireEffect);
            power = GetPower(Weapon.FIRE);
            chaosBase.shoot.total += power;
            cnt = power * 2;
            chaosActions.GetCenter(player, px, py);
            nvx = 0;
            nvy = 0;
            switch (player.shapeSeq) {
                case 0 -> {
                    nvy = -speed;
                }
                case 1 -> {
                    nvy = -diaspeed;
                    nvx = diaspeed;
                }
                case 2 -> {
                    nvx = speed;
                }
                case 3 -> {
                    nvx = diaspeed;
                    nvy = nvx;
                }
                case 4 -> {
                    nvy = speed;
                }
                case 5 -> {
                    nvy = diaspeed;
                    nvx = -diaspeed;
                }
                case 6 -> {
                    nvx = -speed;
                }
                case 7 -> {
                    nvx = -diaspeed;
                    nvy = nvx;
                }
                default -> throw new RuntimeException("Unhandled CASE value " + player.shapeSeq);
            }
            if (power >= 4)
                power = 5;
            while (cnt > 0) {
                svx = nvx - 512;
                svx += trigo.RND() % 1024;
                svy = nvy - 512;
                svy += trigo.RND() % 1024;
                fire = chaosActions.CreateObj(Anims.WEAPON, Weapon.FIRE.ordinal(), px.get(), py.get(), 0, power);
                fire.flags.remove(ObjFlags.nested);
                chaosActions.SetObjVXY(fire, svx, svy);
                cnt--;
            }
        }
    }

    private final ChaosBase.FireProc FireFire_ref = this::FireFire;

    private void FireBall(ChaosBase.Obj player) {
        // VAR
        int rnd = 0;

        if (CheckBullet(Weapon.BALL)) {
            rnd = trigo.RND() % 8;
            if (rnd < 3)
                chaosSounds.SoundEffect(player, ballFireEffect1);
            else if (rnd < 6)
                chaosSounds.SoundEffect(player, ballFireEffect3);
            else
                chaosSounds.SoundEffect(player, ballFireEffect2);
            StdFire(player, Weapon.BALL, 1024, false, false);
        }
    }

    private final ChaosBase.FireProc FireBall_ref = this::FireBall;

    private void FireStar(ChaosBase.Obj player) {
        // VAR
        ChaosBase.ObjAttr attr = null;
        int max = 0;

        if (CheckBullet(Weapon.STAR)) {
            if (GetPower(Weapon.STAR) == 4)
                max = 3;
            else
                max = 1;
            attr = chaosBase.GetAnimAttr(Anims.WEAPON, Weapon.STAR.ordinal());
            if (attr.nbObj < max) {
                chaosSounds.SoundEffect(player, starFireEffect);
                StdFire(player, Weapon.STAR, 3000, true, true);
            }
        }
    }

    private final ChaosBase.FireProc FireStar_ref = this::FireStar;

    private void FireGrenade(ChaosBase.Obj player) {
        if (CheckBullet(Weapon.GRENADE)) {
            chaosSounds.SoundEffect(player, grenadeFireEffect);
            StdFire(player, Weapon.GRENADE, 2000, true, false);
        }
    }

    private final ChaosBase.FireProc FireGrenade_ref = this::FireGrenade;

    private void GunBomb(ChaosBase.Obj player) {
        if (CheckBomb(Weapon.GUN)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Bomb"), String.class), ChaosActions.statPos, 1);
            chaosSounds.StereoEffect();
            chaosSounds.SoundEffect(player, gunBombEffectL);
            chaosSounds.SoundEffect(player, gunBombEffectR);
            StdBomb(player, Weapon.GUN, 3250, 1, 20, false);
        }
    }

    private final ChaosBase.FireProc GunBomb_ref = this::GunBomb;

    private void FBBomb(ChaosBase.Obj player) {
        if (CheckBomb(Weapon.FB)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Cold Fire"), String.class), ChaosActions.statPos, 2);
            StdBomb(player, Weapon.FB, 2090, 1, 30, false);
        }
    }

    private final ChaosBase.FireProc FBBomb_ref = this::FBBomb;

    private void LaserBomb(ChaosBase.Obj player) {
        if (CheckBomb(Weapon.LASER)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Supernovae"), String.class), ChaosActions.statPos, 2);
            chaosSounds.StereoEffect();
            chaosSounds.SoundEffect(player, laserBombEffectL);
            chaosSounds.SoundEffect(player, laserBombEffectR);
            StdBomb(player, Weapon.LASER, 4090, 1, 30, false);
        }
    }

    private final ChaosBase.FireProc LaserBomb_ref = this::LaserBomb;

    private void BubbleBomb(ChaosBase.Obj player) {
        // VAR
        int q = 0;

        if (CheckBomb(Weapon.BUBBLE)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Fragmentation Bomb"), String.class), ChaosActions.statPos, 2);
            q = GetPower(Weapon.BUBBLE) + 2;
            chaosSounds.SoundEffect(player, bubbleBombEffect);
            StdBomb(player, Weapon.BUBBLE, 2000, q, q, true);
        }
    }

    private final ChaosBase.FireProc BubbleBomb_ref = this::BubbleBomb;

    private void FireBomb(ChaosBase.Obj player) {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        Anims a = Anims.PLAYER;

        if (CheckBomb(Weapon.FIRE)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Pyrotechnics"), String.class), ChaosActions.statPos, 2);
            for (int _a = 0; _a < Anims.values().length; _a++) {
                a = Anims.values()[_a];
                if (Runtime.withRange(EnumSet.of(Anims.MACHINE), Anims.ALIEN3, Anims.ALIEN1).contains(a)) {
                    obj = (ChaosBase.Obj) memory.First(chaosBase.animList[a.ordinal()]);
                    tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[a.ordinal()]);
                    while (obj != tail) {
                        if (!chaosActions.OutOfScreen(obj))
                            obj.temperature = ChaosBase.MaxHot;
                        obj = (ChaosBase.Obj) memory.Next(obj.animNode);
                    }
                }
            }
        }
    }

    private final ChaosBase.FireProc FireBomb_ref = this::FireBomb;

    private void BallBomb(ChaosBase.Obj player) {
        if (CheckBomb(Weapon.BALL)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Hunters"), String.class), ChaosActions.statPos, 2);
            chaosSounds.StereoEffect();
            chaosSounds.SoundEffect(player, ballBombEffectR);
            chaosSounds.SoundEffect(player, ballBombEffectL);
            StdBomb(player, Weapon.BALL, 1024, 0, 24, false);
        }
    }

    private final ChaosBase.FireProc BallBomb_ref = this::BallBomb;

    private void StarBomb(ChaosBase.Obj player) {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj next = null;
        ChaosBase.Obj tail = null;
        Runtime.Ref<Integer> hit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);
        Anims a = Anims.PLAYER;

        if (CheckBomb(Weapon.STAR)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Big Bang"), String.class), ChaosActions.statPos, 2);
            chaosSounds.SoundEffect(player, gunFireEffect);
            for (int _a = Anims.ALIEN3.ordinal(); _a <= Anims.MACHINE.ordinal(); _a++) {
                a = Anims.values()[_a];
                if (EnumSet.of(Anims.ALIEN3, Anims.ALIEN2, Anims.ALIEN1, Anims.MACHINE).contains(a)) {
                    obj = (ChaosBase.Obj) memory.First(chaosBase.animList[a.ordinal()]);
                    tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[a.ordinal()]);
                    while (obj != tail) {
                        next = (ChaosBase.Obj) memory.Next(obj.animNode);
                        if (!chaosActions.OutOfScreen(obj)) {
                            hit.set(100);
                            fire.set(100);
                            chaosActions.Aie(obj, player, hit, fire);
                        }
                        obj = next;
                    }
                }
            }
        }
    }

    private final ChaosBase.FireProc StarBomb_ref = this::StarBomb;

    private void GrenadeBomb(ChaosBase.Obj player) {
        if (CheckBomb(Weapon.GRENADE)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Black Hole"), String.class), ChaosActions.statPos, 2);
            StdBomb(player, Weapon.GRENADE, 0, 1, 1, true);
        }
    }

    private final ChaosBase.FireProc GrenadeBomb_ref = this::GrenadeBomb;

    private void InitParams_AddAttrs(ChaosBase.ObjAttr attrs) {
        // VAR
        ChaosBase.ObjAttr attr = null;

        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.copyFrom(attrs);
        memory.AddTail(chaosBase.attrList[Anims.WEAPON.ordinal()], attr.node);
    }

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attrs = new ChaosBase.ObjAttr();
        Runtime.Ref<ChaosBase.ObjAttr> attr = new Runtime.Ref<>(null);
        int c = 0;
        int d = 0;
        int e = 0;

        for (c = 1; c <= MAXF; c++) {
            follow[c - 1].used = false;
        }
        attr.set((ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class)));
        checks.CheckMem(attr.get());
        attrs.copyFrom(attr.get());
        memory.FreeMem(attr.asAdrRef());
        chaosSounds.SetEffect(gunFireEffect[0], chaosSounds.soundList[SoundList.sGun.ordinal()], 0, 4181, 50, 2);
        chaosSounds.SetEffect(gunBombEffectL[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 0, 8363, 160, 5);
        chaosSounds.SetEffect(gunBombEffectR[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 167, 8363, 0, 5);
        chaosSounds.SetEffect(gunBombEffectR[1], chaosSounds.soundList[SoundList.sPouf.ordinal()], 0, 8363, 160, 5);
        chaosSounds.SetEffect(gunKillEffect[0], chaosSounds.soundList[SoundList.wWhite.ordinal()], 1673, 16726, 8, 0);
        chaosBase.Fire[Weapon.GUN.ordinal()] = FireGun_ref;
        chaosBase.Bomb[Weapon.GUN.ordinal()] = GunBomb_ref;
        attrs.Reset = ResetGun_ref;
        attrs.Make = MakeGun_ref;
        attrs.Move = MoveGun_ref;
        attrs.weight = 24;
        attrs.charge = 90;
        attrs.basicType = BasicTypes.NotBase;
        attrs.priority = 50;
        attrs.toKill = true;
        InitParams_AddAttrs(attrs);
        chaosSounds.SetEffect(fbFireEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 418, 4181, 120, 4);
        chaosSounds.SetEffect(fbFireEffect[1], chaosSounds.soundList[SoundList.wNoise.ordinal()], 627, 6265, 120, 4);
        chaosSounds.SetEffect(fbFireEffect[2], chaosSounds.soundList[SoundList.wNoise.ordinal()], 527, 5268, 120, 4);
        chaosSounds.SetEffect(fbKillEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 1673, 16726, 16, 0);
        chaosBase.Fire[Weapon.FB.ordinal()] = FireFB_ref;
        chaosBase.Bomb[Weapon.FB.ordinal()] = FBBomb_ref;
        attrs.Reset = ResetFB_ref;
        attrs.Make = MakeFB_ref;
        attrs.Move = MoveFB_ref;
        attrs.weight = 60;
        attrs.charge = 6;
        attrs.basicType = BasicTypes.NotBase;
        attrs.priority = 51;
        attrs.toKill = true;
        InitParams_AddAttrs(attrs);
        chaosSounds.SetEffect(laserFireEffect[0], chaosSounds.soundList[SoundList.sLaser.ordinal()], 0, 8363, 100, 3);
        for (c = 0; c <= 3; c++) {
            d = (c + 1) * 48;
            chaosSounds.SetEffect(laserBombEffectL[c], chaosSounds.soundList[SoundList.wNoise.ordinal()], 523, 4181, d, 5);
            chaosSounds.SetEffect(laserBombEffectL[8 - c], chaosSounds.nulSound, 1045, 4181, d, 5);
        }
        chaosSounds.SetEffect(laserBombEffectL[4], chaosSounds.nulSound, 1045, 4181, 192, 5);
        chaosSounds.SetEffect(laserBombEffectR[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 4181, 8363, 96, 5);
        for (c = 1; c <= 8; c++) {
            d = (9 - c) * 1045 + 8366;
            e = (9 - c) * 30;
            chaosSounds.SetEffect(laserBombEffectR[c], chaosSounds.nulSound, 1673, d, e, 5);
        }
        chaosBase.Fire[Weapon.LASER.ordinal()] = FireLaser_ref;
        chaosBase.Bomb[Weapon.LASER.ordinal()] = LaserBomb_ref;
        attrs.Reset = ResetLaser_ref;
        attrs.Make = MakeLaser_ref;
        attrs.Move = MoveLaser_ref;
        attrs.weight = 40;
        attrs.charge = 120;
        attrs.basicType = BasicTypes.NotBase;
        attrs.priority = 51;
        attrs.toKill = true;
        InitParams_AddAttrs(attrs);
        chaosSounds.SetEffect(bubbleBombEffect[0], chaosSounds.soundList[SoundList.sCannon.ordinal()], 0, 0, 192, 5);
        chaosSounds.SetEffect(bubbleFireEffectL[0], chaosSounds.soundList[SoundList.wWhite.ordinal()], 1097, 16726, 120, 3);
        chaosSounds.SetEffect(bubbleFireEffectR[0], chaosSounds.soundList[SoundList.wWhite.ordinal()], 2090, 16726, 120, 3);
        for (c = 1; c <= 7; c++) {
            chaosSounds.SetEffect(bubbleFireEffectL[c], chaosSounds.nulSound, 1097, 16726, (8 - c) * 15, 3);
        }
        for (c = 0; c <= 3; c++) {
            d = 4 - c;
            chaosSounds.SetEffect(bubbleFireEffectR[c * 3 + 1], chaosSounds.nulSound, 279, 8363, d * 25, 3);
            chaosSounds.SetEffect(bubbleFireEffectR[c * 3 + 2], chaosSounds.nulSound, 318, 12544, d * 25, 3);
            chaosSounds.SetEffect(bubbleFireEffectR[c * 3 + 3], chaosSounds.nulSound, 558, 16726, d * 25, 3);
        }
        chaosBase.Fire[Weapon.BUBBLE.ordinal()] = FireBubble_ref;
        chaosBase.Bomb[Weapon.BUBBLE.ordinal()] = BubbleBomb_ref;
        attrs.Reset = ResetBubble_ref;
        attrs.Make = MakeBubble_ref;
        attrs.Move = MoveBubble_ref;
        attrs.weight = 10;
        attrs.charge = 80;
        attrs.basicType = BasicTypes.NotBase;
        attrs.priority = 51;
        attrs.toKill = true;
        InitParams_AddAttrs(attrs);
        for (c = 0; c <= 3; c++) {
            d = c + 1;
            chaosSounds.SetEffect(fireFireEffect[c], chaosSounds.nulSound, 300, 4181, d * 20, 3);
            chaosSounds.SetEffect(fireFireEffect[7 - c], chaosSounds.nulSound, 400, 4181, d * 20, 3);
        }
        chaosSounds.SetEffect(fireFireEffect[0], chaosSounds.soundList[SoundList.wNoise.ordinal()], 300, 4181, 20, 3);
        chaosBase.Fire[Weapon.FIRE.ordinal()] = FireFire_ref;
        chaosBase.Bomb[Weapon.FIRE.ordinal()] = FireBomb_ref;
        attrs.Reset = ResetFire_ref;
        attrs.Make = MakeFire_ref;
        attrs.Move = MoveFire_ref;
        attrs.weight = 3;
        attrs.charge = 30;
        attrs.dieStone = 1;
        attrs.dieStStyle = ChaosBase.slowStyle;
        attrs.dieStKinds = EnumSet.of(Stones.stFOG1);
        attrs.dieSKCount = 1;
        attrs.basicType = BasicTypes.NotBase;
        attrs.priority = 55;
        attrs.toKill = true;
        InitParams_AddAttrs(attrs);
        attrs.dieStone = 0;
        chaosSounds.SetEffect(ballFireEffect1[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 8363, 110, 3);
        chaosSounds.SetEffect(ballFireEffect2[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 9387, 110, 3);
        chaosSounds.SetEffect(ballFireEffect3[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 10536, 110, 3);
        chaosSounds.SetEffect(ballBombEffectR[0], chaosSounds.soundList[SoundList.aPanflute.ordinal()], 0, 4694, 250, 5);
        for (c = 1; c <= 6; c++) {
            chaosSounds.SetEffect(ballBombEffectR[c], chaosSounds.soundList[SoundList.wWhite.ordinal()], 0, 18774, c * c * 4, 5);
        }
        chaosSounds.SetEffect(ballBombEffectR[7], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1671, 12530, 4, 5);
        chaosSounds.SetEffect(ballBombEffectR[8], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1671, 21073, 9, 5);
        chaosSounds.SetEffect(ballBombEffectR[9], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2810, 14065, 14, 5);
        chaosSounds.SetEffect(ballBombEffectR[10], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1875, 18774, 21, 5);
        chaosSounds.SetEffect(ballBombEffectR[11], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2503, 22327, 32, 5);
        chaosSounds.SetEffect(ballBombEffectR[12], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2977, 15787, 50, 5);
        chaosSounds.SetEffect(ballBombEffectR[13], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2105, 12000, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[14], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1600, 16726, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[15], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2230, 14000, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[16], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1867, 18800, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[17], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2507, 15000, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[18], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2000, 12500, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[19], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1667, 11000, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[20], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1467, 16700, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[21], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2227, 21000, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[22], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2800, 25000, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[23], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 3333, 15900, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[24], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2120, 18700, 70, 5);
        chaosSounds.SetEffect(ballBombEffectR[25], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2493, 22300, 50, 5);
        chaosSounds.SetEffect(ballBombEffectR[26], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2973, 28130, 32, 5);
        chaosSounds.SetEffect(ballBombEffectR[27], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 3751, 16726, 21, 5);
        chaosSounds.SetEffect(ballBombEffectR[28], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2230, 22100, 14, 5);
        chaosSounds.SetEffect(ballBombEffectR[29], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 2947, 14800, 9, 5);
        chaosSounds.SetEffect(ballBombEffectR[30], chaosSounds.soundList[SoundList.wPanflute.ordinal()], 1973, 13000, 4, 5);
        chaosSounds.SetEffect(ballBombEffectL[0], chaosSounds.soundList[SoundList.wShakuhachi.ordinal()], 17838, 4694, 80, 5);
        for (c = 1; c <= 15; c++) {
            chaosSounds.SetEffect(ballBombEffectL[c], chaosSounds.nulSound, 588, 4694, (16 - c) * 5, 5);
        }
        chaosBase.Fire[Weapon.BALL.ordinal()] = FireBall_ref;
        chaosBase.Bomb[Weapon.BALL.ordinal()] = BallBomb_ref;
        attrs.Reset = ResetBall_ref;
        attrs.Make = MakeBall_ref;
        attrs.Move = MoveBall_ref;
        attrs.weight = 80;
        attrs.charge = 20;
        attrs.basicType = BasicTypes.NotBase;
        attrs.priority = 49;
        attrs.toKill = true;
        InitParams_AddAttrs(attrs);
        chaosSounds.SetEffect(starFireEffect[0], chaosSounds.soundList[SoundList.sCasserole.ordinal()], 0, 16726, 100, 3);
        chaosBase.Fire[Weapon.STAR.ordinal()] = FireStar_ref;
        chaosBase.Bomb[Weapon.STAR.ordinal()] = StarBomb_ref;
        attrs.Reset = ResetStar_ref;
        attrs.Make = MakeStar_ref;
        attrs.Move = MoveStar_ref;
        attrs.weight = 10;
        attrs.charge = 30;
        attrs.inerty = 96;
        attrs.priority = 51;
        attrs.basicType = BasicTypes.NotBase;
        attrs.toKill = true;
        InitParams_AddAttrs(attrs);
        chaosSounds.SetEffect(grenadeBombEffect[0], chaosSounds.soundList[SoundList.sPouf.ordinal()], 0, 16726, 160, 5);
        chaosSounds.SetEffect(grenadeFireEffect[0], chaosSounds.soundList[SoundList.sHHat.ordinal()], 697, 8363, 120, 3);
        chaosSounds.SetEffect(grenadeFireEffect[1], chaosSounds.soundList[SoundList.sCymbale.ordinal()], 697, 8363, 120, 3);
        chaosSounds.SetEffect(grenadeFireEffect[2], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1045, 12544, 120, 3);
        chaosSounds.SetEffect(grenadeFireEffect[3], chaosSounds.soundList[SoundList.aCrash.ordinal()], 0, 0, 120, 3);
        chaosBase.Fire[Weapon.GRENADE.ordinal()] = FireGrenade_ref;
        chaosBase.Bomb[Weapon.GRENADE.ordinal()] = GrenadeBomb_ref;
        attrs.Reset = ResetGrenade_ref;
        attrs.Make = MakeGrenade_ref;
        attrs.Move = MoveGrenade_ref;
        attrs.weight = 50;
        attrs.charge = -120;
        attrs.inerty = 32;
        attrs.priority = 47;
        attrs.basicType = BasicTypes.NotBase;
        attrs.toKill = true;
        attrs.dieStone = 4;
        attrs.dieStStyle = ChaosBase.slowStyle;
        attrs.dieStKinds = EnumSet.of(Stones.stFOG2);
        attrs.dieSKCount = 1;
        InitParams_AddAttrs(attrs);
    }


    // Support

    private static ChaosWeapon instance;

    public static ChaosWeapon instance() {
        if (instance == null)
            new ChaosWeapon(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        InitParams();
    }

    public void close() {
    }

}
