package ch.chaos.castle.alien;

import java.util.Map;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosCreator;

/**
 * @param 'statOrLife', copied to 'stat' if 'type' is in {@link ChaosBase#AnimAlienSet},
 * and in 'stat' else. However, the <tt>MakeXxx</tt> method that initializes the alien
 * may move 'life' back to 'stat' and choose a standard number of lives (for instance
 * cartoons)
 */
public record SpriteInfo(Anims type, int subKind, int statOrLife) {
    
    /**
     * Special value recognized by
     * {@link SpriteFiller#placeRandom(java.util.List, ch.chaos.castle.utils.Rect, java.util.function.Predicate, java.util.function.Function, ch.chaos.castle.utils.MinMax, ch.chaos.castle.utils.MinMax, int)}
     * meaning not to place anything. Usually used when supplying a list, some list item can be this value, especially when
     * {@link SimplexRandomizer} is used,.
     */
    public final static SpriteInfo NONE = new SpriteInfo(null, 0, 0);
    
    
    private static final Map<Integer, Integer> ALIENS_KIND_STAT = Map.of(
            ChaosAlien.aCartoon, 2,
            ChaosAlien.aSmallDrawer, 0,
            ChaosAlien.aBigDrawer, 0,
            ChaosAlien.aKamikaze, 3,
            ChaosAlien.aPic, 1
            );
    
    private static final Map<Integer, Integer> CREATORS_KIND_STAT = Map.of(
            ChaosCreator.cNest, 0,
            ChaosCreator.cAlienBox, 1
            );
    
    public SpriteInfo(Anims type, int subKind, int statOrLife) {
        this.type = type;
        this.subKind = subKind;
        this.statOrLife = statOrLife;
        validate();
    }

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
    
    private void validate() {
        if (type == Anims.ALIEN1) {
            Integer maxStat = ALIENS_KIND_STAT.get(subKind);
            if (maxStat == null) {
                // A life is expected
                if (statOrLife <= 0) {
                    throw new IllegalArgumentException("'statOrLife' must be a positive life");
                }
            } else {
                // A stat is expected
                if (statOrLife < 0 || statOrLife > maxStat) {
                    throw new IllegalArgumentException("'statOrLife' must be a status between 0 and " + maxStat);
                }
            }
        } else if (type == Anims.ALIEN2) {
            Integer maxStat = CREATORS_KIND_STAT.get(subKind);
            if (maxStat == null) {
                // A life is expected
                if (statOrLife <= 0) {
                    throw new IllegalArgumentException("'statOrLife' must be a positive life");
                }
            } else {
                // A stat is expected
                if (statOrLife < 0 || statOrLife > maxStat) {
                    throw new IllegalArgumentException("'statOrLife' must be a status between 0 and " + maxStat);
                }
            }
        }
    }
}
