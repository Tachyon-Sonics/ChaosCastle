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
import ch.chaos.castle.utils.generator.DoubleCyclo;
import ch.chaos.castle.utils.generator.SimpleBrickMask;

public class SnowTrack extends LevelBase {
    
    private DoubleCyclo dc;
    private Coord start;
    private Coord exit;
    
    
    public void build() {
        chaosBase.snow = true;
        
        buildBinary(); // Creates 'dc', 'start', 'exit

        Random rnd = new Random();
        LevelBuilder builder = new LevelBuilder(dc.getWidth(), dc.getHeight(), rnd);
        SpriteFiller filler = new SpriteFiller(rnd);
        
        dc.forWalls((Coord coord) -> {
            builder.put(coord, IceBlock);
        });
        dc.forHoles((Coord coord) -> {
            builder.put(coord, Ice);
        });
        filler.putPlayer(start.x(), start.y());
        filler.putExit(exit.x(), exit.y());
        
        // Review
        Rect anywhere = new Rect(0, 0, dc.getWidth(), dc.getHeight());
        filler.placeRandom(List.of(
                new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doCartoon, 0),
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aCartoon, 0),
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aCartoon, 1),
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aCartoon, 2)
                ), anywhere, filler.background(), MinMax.value(200));
        filler.placeRandom(List.of(
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCircle, 60)
                ), anywhere, filler.background(), MinMax.value(10));
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), anywhere, filler.background(), 15);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), anywhere, filler.background(), 15);
        
        filler.addOptions(anywhere, filler.background(), 3, 40, 1, 5, 10, 10, 10);
    }

    private void buildBinary() {
        // Double cycloide
        dc = new DoubleCyclo(84, 2, 6, (period1, period2) -> {
            return (period1 != period2 && period1 + period2 > 7 && period1 > 1 && period2 > 1);
        });
        dc.build();
        
        // Simple brick mask
        SimpleBrickMask mask = new SimpleBrickMask(30, 30);
        mask.build(0.4);
        mask.fillInterior(3);
        
        // Add brick mask to interior of double cycloid
        for (int x = 0; x < mask.getWidth(); x++) {
            for (int y = 0; y < mask.getHeight(); y++) {
                if (!mask.isWall(x, y)) {
                    int px = (dc.getWidth() - mask.getWidth()) / 2 + x;
                    int py = (dc.getHeight() - mask.getHeight()) / 2 + y;
                    dc.setWall(px, py, false);
                }
            }
        }
        dc.removeDiagonalsMakeHole();
        
        // Put road from one to the other
        int cx = dc.getWidth() / 2;
        int y = 0;
        while (dc.isWall(cx, y)) {
            y++;
        }
        while (!dc.isWall(cx, y)) {
            y++; // In cycloid
        }
        while (dc.isWall(cx, y)) {
            dc.setWall(cx, y, false);
            y++;
        }
        
        // Initial wall
        cx++;
        y = 0;
        while (dc.isWall(cx, y)) {
            y++;
        }
        while (!dc.isWall(cx, y)) {
            dc.setWall(cx, y, true);
            y++;
        }
        
        // Start
        cx++;
        y = 0;
        while (dc.isWall(cx, y)) {
            y++;
        }
        start = new Coord(cx, y);
        
        // Exit
        cx = (dc.getWidth() + 1) / 2;
        y = dc.getHeight() - 1;
        while (dc.isWall(cx, y)) {
            y--;
        }
        while (!dc.isWall(cx, y)) {
            y--; // In cycloid
        }
        while (dc.isWall(cx, y)) {
            y--;
        }
        exit = new Coord(cx, y);
    }

}
