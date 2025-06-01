package ch.chaos.castle.level;

import java.util.List;
import java.util.Random;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.ChaosDObj;
import ch.chaos.castle.ChaosMachine;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BinaryLevel;
import ch.chaos.castle.utils.generator.TwilightGenerator;
import ch.chaos.castle.utils.simplex.SimplexRandomizer;

public class Twilight extends LevelBase {
    
    private int width;
    private int height;
    

    public void build() {
        chaos1Zone.rotate = false;
        Random rnd = new Random();
        this.width = (chaosBase.difficulty + 2) * 10; // 50x50 -> 120x120
        this.height = this.width;
        
        TwilightGenerator generator = new TwilightGenerator(width, height);
        generator.build(rnd);
        BinaryLevel reachable = generator.getReachable();
        
        LevelBuilder builder = new LevelBuilder(width, height, rnd);
        SpriteFiller filler = new SpriteFiller(rnd);
        
        // Stars as background
        builder.fillRandom(0, 0, width, height, 0, 7, builder::anywhere, builder::expRandom);
        
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
        
        int nbCells = reachable.getNbHoles(); // avg 4200 for 100x100
        
        // Review
        Rect anywhere = new Rect(0, 0, width, height);
        
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), anywhere, reachable::isHole, nbCells / 280);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbFreeFire), anywhere, reachable::isHole, nbCells / 800);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbMagnet), anywhere, reachable::isHole, nbCells / 800);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), anywhere, reachable::isHole, nbCells / 280);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), anywhere, reachable::isHole, nbCells / 100);
        filler.placeRandom(
                List.of(new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doWindMaker)), 
                anywhere, (Coord coord) -> reachable.isHole(coord) && filler.background8().test(coord),
                filler.nb(nbCells / 300), filler.nb(0));
        filler.placeRandom(
                List.of(new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doFireWall)), 
                anywhere, (Coord coord) -> reachable.isHole(coord) && filler.background8().test(coord),
                filler.nb(nbCells / 400), filler.nb(0));
        filler.placeRandom(
                List.of(new SpriteInfo(Anims.MACHINE, ChaosMachine.mTraverse, 0)), 
                anywhere, (Coord coord) -> reachable.isHole(coord) && filler.horizontalLine(2, 8).test(coord),
                filler.nb(nbCells / 200), filler.nb(0));
        filler.placeRandom(
                List.of(new SpriteInfo(Anims.MACHINE, ChaosMachine.mTraverse, 1)), 
                anywhere, (Coord coord) -> reachable.isHole(coord) && filler.verticalLine(2, 8).test(coord),
                filler.nb(nbCells / 200), filler.nb(1));
        filler.placeRandom(
                List.of(new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doMirror)), 
                anywhere, (Coord coord) -> reachable.isHole(coord) && filler.background8().test(coord),
                filler.nb(nbCells / 300), filler.nb(0));
        
        List<SpriteInfo> spriteTypes = List.of(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aStar, filler.pLife(2)),
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cFour, filler.pLife(3)),
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienBox, 0),
                SpriteInfo.NONE,
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorR, filler.life(40, 3, 4)),
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorC, filler.life(40, 3, 4))
                );
        SimplexRandomizer simplexRandom = new SimplexRandomizer(width, height, 0.6, spriteTypes.size(), rnd);
        filler.randomPlacer()
            .types(spriteTypes)
            .where(anywhere)
            .isAllowed(reachable::isHole)
            .andAllowed(new Coord(1, 1).atAtLeast(4))
            .selector(simplexRandom::valueAt)
            .amount(nbCells / 12)
            .place();
        
        SimplexRandomizer oneFourth = new SimplexRandomizer(width, height, 0.5, 4, rnd);
        filler.randomPlacer()
            .type(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCircle, 120))
            .where(anywhere)
            .isAllowed(reachable::isHole)
            .andAllowed((coord) -> oneFourth.valueAt(coord) == 0)
            .andAllowed(new Coord(1, 1).atAtLeast(4))
            .amount(10 + chaosBase.difficulty)
            .place();
        
        // TODO use Groove blocks instead of lights (instead if difficulty = 7?)
        // TODO 2 hidden passage to stars zones, one with sleeper, other with bomb
        // TODO in clusters: ?? (some money releasing aliens)
        // TODO here, or somewhere else: Double Oval with huge number of lives
        
        filler.addOptions(anywhere, reachable::isHole, 8, 10, 2, 0, 10, 5, 10);
    }
}
