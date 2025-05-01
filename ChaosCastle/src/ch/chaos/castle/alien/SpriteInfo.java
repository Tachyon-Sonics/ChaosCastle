package ch.chaos.castle.alien;

import ch.chaos.castle.ChaosBase;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;

/**
 * @param 'statOrLife', copied to 'stat' if 'life' is in {@link ChaosBase#AnimAlienSet},
 * and in 'stat' else. However, the <tt>MakeXxx</tt> method that initializes the alien
 * may move 'life' back to 'stat' and choose a standard number of lives (for instance
 * cartoons)
 */
public record SpriteInfo(Anims type, int subKind, int statOrLife) {

    public SpriteInfo(Anims type, int subKind) {
        this(type, subKind, 0);
    }
    
    /**
     * Create a bonus (bullets, hospital, magnet, etc)
     * @param stat ChaosBonus.tbXxx
     */
    public static SpriteInfo tbBonus(int stat) {
        return new SpriteInfo(Anims.BONUS, ChaosBonus.TimedBonus, stat);
    }
}
