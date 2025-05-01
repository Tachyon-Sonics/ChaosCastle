package ch.chaos.castle.alien;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosCreator;

public class Sprites {
    
    /**
     * Create a basic (not firing, not creating) alien
     * @param subKind a value from {@link ChaosAlien}, such as {@link ChaosAlien#aDbOval}
     */
    public static SpriteInfo basicAlien(int subKind, int stat) {
        return new SpriteInfo(Anims.ALIEN1, subKind, stat);
    }
    
    public static SpriteInfo basicAlien(int subKind) {
        return new SpriteInfo(Anims.ALIEN1, subKind, 0);
    }
    
    /**
     * Create an alien that is firing or creating other aliens
     * @param subKind a value for {@link ChaosCreator}, such as {@link ChaosCreator#cAlienV} (the butterfly alien from chaos)
     */
    public static SpriteInfo creatorAlien(int subKind, int stat) {
        return new SpriteInfo(Anims.ALIEN2, subKind, stat);
    }

    public static SpriteInfo creatorAlien(int subKind) {
        return new SpriteInfo(Anims.ALIEN2, subKind, 0);
    }
    
}
