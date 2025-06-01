package ch.chaos.castle.level;

import java.util.List;
import java.util.Random;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.ChaosDObj;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BinaryLevel;
import ch.chaos.castle.utils.generator.BrickMask;

public class BigCavern extends LevelBase {
    
    public void build() {
        Random rnd = new Random();
        BrickMask mask = BrickMask.buildBrickMask(rnd.nextLong(), 100, 100); // TOSO size based on difficulty, maxed at 8 or 9
        BinaryLevel cavern = mask.toExtendedBinaryLevel();
        
        LevelBuilder builder = new LevelBuilder(cavern.getWidth(), cavern.getHeight(), rnd);
        SpriteFiller filler = new SpriteFiller(rnd);
        
        cavern.forWalls((Coord coord) -> {
            builder.put(coord, SimpleBlock);
        });
        cavern.forHoles((Coord coord) -> {
            builder.put(coord, Ground);
        });
        Coord center = new Coord(cavern.getWidth() / 2, cavern.getHeight() / 2);
        filler.putPlayer(center.x(), center.y());
        center = center.add(1, 0);
        builder.put(center, Ground);
        filler.putExit(center.x(), center.y());
        
        // Review, add color aliens too, real cartoons, drawers, etc
        // Create clusters by choosing a random point and adding all points within a given distance
        // Walls: Simplex noise on 7 forest blocks + 7 times brick. Then random leafs on bricks
        Rect anywhere = new Rect(0, 0, cavern.getWidth(), cavern.getHeight());
        filler.placeRandom(List.of(
                new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doCartoon, 0),
                new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doCartoon, 1),
                new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doCartoon, 2),
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienBox, 1),
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aBubble, filler.pLife(2)),
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aDiese, filler.pLife(2))
                ), anywhere, filler.background(), MinMax.value(300));
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), anywhere, filler.background(), 15);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), anywhere, filler.background(), 30);
        filler.placeRandom(new SpriteInfo(Anims.BONUS, ChaosBonus.Money, ChaosBonus.Moneys.m5.ordinal()),
                anywhere, filler.background(), MinMax.value(16)); // remove
        
        filler.addOptions(anywhere, filler.background(), 3, 3, 1, 20, 10, 40, 5);
    }

}
