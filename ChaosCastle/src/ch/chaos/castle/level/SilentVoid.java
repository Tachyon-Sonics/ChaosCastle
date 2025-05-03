package ch.chaos.castle.level;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BinaryLevel;
import ch.chaos.castle.utils.generator.VoidGenerator;

public class SilentVoid extends LevelBase {

    public void build() {
        chaos1Zone.rotate = false;
        Random rnd = new Random();
        
        // Build structure
        BinaryLevel reachable;
        Coord entry;
        VoidGenerator silentVoid;
        do {
            int nbLines = 15 + rnd.nextInt(30);
            int nbEllipses = 10 + rnd.nextInt(15);
            silentVoid = new VoidGenerator(120, 60, nbLines, nbEllipses, 3, 9);
            do {
                silentVoid.build();
                entry = silentVoid.getEntry();
            } while (entry == null);
            
            // Create reachability mask:
            reachable = new BinaryLevel(silentVoid.getWidth(), silentVoid.getHeight());
            reachable.fillRect(0, 0, silentVoid.getWidth(), silentVoid.getHeight(), true);
            BinaryLevel copy = silentVoid.copy();
            BinaryLevel reachable0 = reachable;
            copy.fillFlood(entry, true, (coord) -> reachable0.setWall(coord, false));
        } while (reachable.countHoles() < 1000); // Reject if interior is too small
        
        // Add entry passage
        Coord cur = new Coord(entry.x(), entry.y() - 1);
        while (cur.y() >= 0) {
            silentVoid.setWall(cur, false);
            silentVoid.setWall(cur.add(-1, 0), true);
            silentVoid.setWall(cur.add(1, 0), true);
            silentVoid.setWall(cur, false);
            cur = cur.add(0, -1);
        };
        
        final int IAH = 40;
        chaosObjects.Clear((short) 120, (short) (60 + IAH));
        // Stars as background
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 119, (short) (60 + IAH), (short) 0, (short) 7, chaosObjects.OnlyBackground_ref, chaosObjects.ExpRandom_ref);

        // Initial area
        chaosObjects.Fill((short) 40, (short) 0, (short) 80, (short) (IAH - 1), (short) EmptyBlock);
        chaosObjects.Fill((short) 57, (short) 20, (short) 63, (short) 20, (short) BackBig);
        chaosObjects.PutPlayer((short) 57, (short) 20);
        chaosObjects.PutExit((short) 63, (short) 20);
        chaosObjects.Fill((short) 40, (short) (IAH - 1), (short) 80, (short) (IAH - 1), (short) Bricks);
        chaosObjects.Fill((short) 59, (short) (IAH - 1), (short) 61, (short) (IAH - 1), (short) BarLight);
        chaosObjects.Fill((short) 60, (short) (IAH - 1), (short) 60, (short) (IAH - 1), (short) Light);
        chaosObjects.Fill((short) 60, (short) 21, (short) 60, (short) (IAH - 2), (short) FalseEmpty);
        
        // Hidden area
        chaosObjects.Put((short) 64, (short) 20, (short) FalseEmpty);
        chaosObjects.Fill((short) 57, (short) 1, (short) 63, (short) 1, (short) Ground);
        chaosObjects.Fill((short) 63, (short) 1, (short) 63, (short) 19, (short) FalseEmpty);
        chaosObjects.PutExtraLife((short) 57, (short) 1);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 60, (short) 38);
        if (chaosBase.difficulty >= 6) {
            chaosObjects.PutBlockObj(Anims.ALIEN1, (short) ChaosAlien.aPic, 0, (short) 58, (short) 1);
        }
        
        // Silent Void
        silentVoid.forWalls((Coord coord) -> {
            chaosObjects.Put((short) coord.x(), (short) (coord.y() + IAH), (short) EmptyBlock);
        });
        List<Integer> fillTypes = List.of(Sq1Block, Sq4Block);
        List<Integer> sparseTypes = List.of(Fact1Block, Fact2Block, Fact3Block);
        for (BinaryLevel ellipse : silentVoid.getEllipses()) {
            int fillType = fillTypes.get(rnd.nextInt(fillTypes.size()));
            ellipse.forWalls((Coord coord) -> {
                int ft = fillType;
                if (rnd.nextInt(10) == 0) {
                    ft = sparseTypes.get(rnd.nextInt(sparseTypes.size()));
                }
                chaosObjects.Put((short) coord.x(), (short) (coord.y() + IAH), (short) ft);
            });
        }
        
        // Objects
        Coord bonusLevel = silentVoid.pickFarthestFrom(entry, rnd);
        bonusLevel = bonusLevel.add(0, IAH);
        for (Coord delta : Coord.n9()) {
            Coord coord = bonusLevel.add(delta);
            if (chaosObjects.OnlyBackground((short) coord.x(), (short) coord.y())) {
                chaosObjects.Put((short) coord.x(), (short) coord.y(), (short) Ice);
            }
        }

        SpriteFiller filler = new SpriteFiller(rnd);
        filler.putBlockObj(SpriteInfo.tbBonus(ChaosBonus.tbBonusLevel), bonusLevel);
        
        Rect whole = new Rect(0, IAH, 120, 60);
        Predicate<Coord> whereReachable = filler.mask(new Coord(0, IAH), reachable, false);
        
        if (chaosBase.difficulty >= 6) {
            filler.addOptions(whole, whereReachable, 7, 7, 2, 15, 30, 15, 7);
            filler.placeRandom(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCircle, 120), whole, whereReachable, 10);
            filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), whole, whereReachable, 10);
        }
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), whole, whereReachable, 10);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBomb), whole, whereReachable, 6 - chaosBase.difficulty / 2);
        filler.placeRandom(new SpriteInfo(Anims.BONUS, ChaosBonus.Money, Moneys.st.ordinal()),
                whole, whereReachable, 10 + (20 - chaosBase.difficulty * 2));
    }


}
