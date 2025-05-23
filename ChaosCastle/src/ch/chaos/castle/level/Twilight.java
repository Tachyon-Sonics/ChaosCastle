package ch.chaos.castle.level;

import java.util.List;
import java.util.Random;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;

public class Twilight extends LevelBase {

    public void build() {
        Random rnd = new Random();
        LevelBuilder builder = new LevelBuilder(12, 12, rnd);
        SpriteFiller filler = new SpriteFiller(rnd);
        
        builder.fillRandom(0, 0, 12, 12, 0, 7, builder::anywhere, builder::randomly);
        filler.putPlayer(0, 0);
        filler.putExit(11, 11);
        
        // Review
        Rect anywhere = new Rect(0, 0, 12, 12);
        filler.placeRandom(List.of(
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienBox, 0),
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorC, filler.life(20, 2, 2)),
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorR, filler.life(20, 2, 2)),
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aStar, filler.pLife(2))
                ), anywhere, filler.background(), MinMax.value(10));
        
        filler.addOptions(anywhere, filler.background(), 0, 1, 0, 0, 0, 0, 1);
    }
}
