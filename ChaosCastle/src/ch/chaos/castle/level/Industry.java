package ch.chaos.castle.level;

import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.CoordPairDistance;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.IndustryGenerator;

public class Industry extends LevelBase {
    
    public void build() {
        chaos1Zone.rotate = false;
        
        Random rnd = new Random();
        IndustryGenerator industry = new IndustryGenerator(120, 120, 60);
        industry.build();
        
        Coord start = new Coord(industry.getWidth() / 2, industry.getHeight() / 2);
        CoordPairDistance cpd = industry.guessFarthest8Coords(rnd, 10);
        
        LevelBuilder builder = new LevelBuilder(industry.getWidth(), industry.getHeight(), rnd);
        SpriteFiller filler = new SpriteFiller(rnd);
        
        builder.fillRandom(0, 0, industry.getWidth(), industry.getHeight(), EmptyBlock, Fact3Block, builder::anywhere, 
                (range) -> expRandom(rnd, range));
        industry.forHoles((Coord coord) -> {
            builder.put(coord, BackBig);
        });
        filler.putPlayer(start.x(), start.y());
        filler.putExit(start.x() + 1, start.y());
        
        filler.putBlockBonus(ChaosBonus.tbBomb, cpd.coord1());
        filler.putBlockBonus(ChaosBonus.tbBonusLevel, cpd.coord2());
        
        List<List<Coord>> distances = industry.getDistancesFrom(start);
        Map<Coord, Integer> distMap = industry.remapDistances(distances);
        
        Rect anywhere = new Rect(0, 0, industry.getWidth(), industry.getHeight());
        filler.placeRandom(List.of(
                new SpriteInfo(Anims.BONUS, ChaosBonus.Money, ChaosBonus.Moneys.m1.ordinal()),
                new SpriteInfo(Anims.BONUS, ChaosBonus.Money, ChaosBonus.Moneys.m2.ordinal()),
                new SpriteInfo(Anims.BONUS, ChaosBonus.Money, ChaosBonus.Moneys.m5.ordinal())
                ), anywhere, (coord) -> isLocalMaxDist(coord, builder, distMap), MinMax.value(20));
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), anywhere, filler.background(), 15);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), anywhere, filler.background(), 15);
        
        /*
         * Add side chambers, including hidden ones
         * Darker palette than Factory, violet color should be present
         */
    }

    private int expRandom(Random rnd, int range) {
        int result = 0;
        while (result < range - 1 && rnd.nextBoolean()) {
            result++;
        }
        return result;
    }
    
    private boolean isLocalMaxDist(Coord coord, LevelBuilder builder, Map<Coord, Integer> distMap) {
        if (builder.isWall(coord.x(), coord.y()))
            return false;
        int distance = distMap.get(coord);
        for (Coord delta : Coord.n4()) {
            Coord other = coord.add(delta);
            if (!builder.isWall(other.x(), other.y())) {
                Integer otherDistance = distMap.get(other);
                if (otherDistance != null && otherDistance > distance)
                    return false;
            }
        }
        return true;
    }

}
