package ch.chaos.castle.level;

import java.util.List;
import java.util.Random;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BinaryLevel;
import ch.chaos.castle.utils.generator.TwilightGenerator;

public class Twilight extends LevelBase {
    
    private final static int WIDTH = 100;
    private final static int HEIGHT = 100;
    

    public void build() {
        chaos1Zone.rotate = false;
        Random rnd = new Random();
        
        TwilightGenerator generator = new TwilightGenerator(WIDTH, HEIGHT);
        generator.build(rnd);
        BinaryLevel reachable = generator.getReachable();
        
        LevelBuilder builder = new LevelBuilder(WIDTH, HEIGHT, rnd);
        SpriteFiller filler = new SpriteFiller(rnd);
        
        // Stars as background
        builder.fillRandom(0, 0, WIDTH, HEIGHT, 0, 7, builder::anywhere, builder::expRandom);
        
        // BarLight as walls
        generator.forWalls((Coord coord) -> {
            builder.put(coord, BarLight);
        });
        
        // Light where reachable
        reachable.forHoles((Coord coord) -> {
            builder.put(coord, Light);
        });
        
        filler.putPlayer(1, 1);
        Coord exit = generator.getExit();
        filler.putExit(exit.x(), exit.y());
        Coord indicator = generator.getIndicator();
        filler.putBlockBonus(ChaosBonus.tbSGSpeed, indicator);
        
        // Review
        Rect anywhere = new Rect(0, 0, WIDTH, HEIGHT);
        
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), anywhere, reachable::isHole, 15);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), anywhere, reachable::isHole, 40);
        
        filler.placeRandom(List.of(
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienBox, 0),
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorC, filler.life(20, 2, 2)),
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorR, filler.life(20, 2, 2)),
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aStar, filler.pLife(2))
                ), anywhere, reachable::isHole, MinMax.value(300));
        
        filler.addOptions(anywhere, reachable::isHole, 10, 20, 2, 0, 10, 5, 20);
    }
}
