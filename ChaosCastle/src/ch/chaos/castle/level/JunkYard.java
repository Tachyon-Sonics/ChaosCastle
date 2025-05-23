package ch.chaos.castle.level;

import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.CoordPairDistance;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.JunkYardGenerator;

public class JunkYard extends LevelBase {
    
    public void build() {
        chaos1Zone.rotate = false;
        
        Random rnd = new Random();
        JunkYardGenerator yard = new JunkYardGenerator(80, 80);
        boolean success;
        do {
            double persistence = 0.4 + rnd.nextDouble() * 0.4;
//            System.out.println("Persistence: " + persistence);
            yard.build(rnd, persistence);
            yard.drawRect(0, 0, yard.getWidth(), yard.getHeight(), true);
            Coord center = new Coord(yard.getWidth() / 2, yard.getHeight() / 2);
            boolean fromCenter = yard.retainReachableFrom(center);
            List<List<Coord>> distances = yard.getDistancesFrom(center);
            Map<Coord, Integer> distMap = yard.remapDistances(distances);
//            if (fromCenter)
//                System.out.println("Points: " + distMap.size() + ": " + (distMap.size() * 100 / (yard.getWidth() * yard.getHeight())) + "%");
            boolean enougPoints = distMap.size() * 2.5 > yard.getWidth() * yard.getHeight();
            success = fromCenter && enougPoints;
        } while (!success);
        yard.removeDiagonalsMakeHole();
        
        CoordPairDistance cpd = yard.guessFarthest8Coords(rnd, 10);
        
        LevelBuilder builder = new LevelBuilder(yard.getWidth(), yard.getHeight(), rnd);
        SpriteFiller filler = new SpriteFiller(rnd);
        
        builder.fillRandom(0, 0, yard.getWidth(), yard.getHeight(), Forest1, Forest7, builder::anywhere, builder::expRandom);
        yard.forHoles((Coord coord) -> {
            builder.put(coord, Ground2);
        });
        filler.putPlayer(cpd.coord1().x(), cpd.coord1().y());
        filler.putExit(cpd.coord2().x(), cpd.coord2().y());
        
        // Review
        Rect anywhere = new Rect(0, 0, yard.getWidth(), yard.getHeight());
        filler.placeRandom(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cNest), anywhere, filler.background(), 50);
        filler.placeRandom(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienA, filler.pLife(2)), anywhere, filler.background(), 50);
        filler.placeRandom(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienV, filler.pLife(2)), anywhere, filler.background(), 50);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), anywhere, filler.background(), 30);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), anywhere, filler.background(), 30);
    }

}
