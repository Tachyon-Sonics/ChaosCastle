package ch.chaos.castle.alien;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import ch.chaos.castle.ChaosObjects;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BinaryLevel;

public class SpriteFiller {
    
    private final Random rnd;
    private final ChaosObjects chaosObjects;
    private final Set<Coord> usedCoords = new HashSet<>();

    
    public SpriteFiller(Random rnd) {
        this.rnd = rnd;
        this.chaosObjects = ChaosObjects.instance();
    }
    
    /**
     * Clear the set of used coordinates, meaning that next placement can be put on top of those
     * previous to this call.
     */
    public void reset() {
        usedCoords.clear();
    }

    /**
     * Place aliens / sprites randomly.
     * <p>
     * For each attempt to place a sprite, at most 'nbTries' location will be tried, until 'isAllowed' returns true.
     * @param types the sprite types. Each placed sprite is chosen among this list
     * @param where the rectangle in which to place sprites
     * @param isAllowed predicate to check if placement is allowed on a given coordinate
     * @param amount the number of sprites to place (a random amount is chosen between min and max)
     * @param statOrLife number of lives / stat of the placed sprites (a random amount is chosen between min and max). If
     * <tt>null</tt>, {@link SpriteInfo#statOrLife()} is used instead.
     */
    public int tryPlaceRandom(List<SpriteInfo> types, Rect where, Predicate<Coord> isAllowed, 
            MinMax amount, MinMax statOrLife, int nbTries) {
        int pickAmount = amount.pick(rnd);
        int result = 0;
        for (int k = 0; k < pickAmount; k++) {
            // Choose a random coordinate
            Coord position;
            int tries = nbTries;
            do {
                tries--;
                int x = where.x() + rnd.nextInt(where.w());
                int y = where.y() + rnd.nextInt(where.h());
                position = new Coord(x, y);
            } while (tries >= 0 && (usedCoords.contains(position) || !isAllowed.test(position)));
            
            if (isAllowed.test(position)) {
                usedCoords.add(position);
                result++;
                
                // Choose a random sprite type and lives
                int typeIndex = types.size() == 1 ? 0 : rnd.nextInt(types.size());
                SpriteInfo info = types.get(typeIndex);
                int pickStat = info.statOrLife();
                if (statOrLife != null)
                    pickStat = statOrLife.pick(rnd);
                
                // Place it
                chaosObjects.PutBlockObj(info.type(), (short) info.subKind(), pickStat, 
                        (short) position.x(), (short) position.y());
            }
        }
        return result;
    }
    
    /**
     * Place aliens / sprites randomly.
     * <p>
     * The exact specified amount of sprites will be placed, unless there is not enough available coordinates
     * @param types the sprite types. Each placed sprite is chosen among this list
     * @param where the rectangle in which to place sprites (tile-index-coordinates)
     * @param isAllowed predicate to check if placement is allowed on a given coordinate
     * @param amount the number of sprites to place (a random amount is chosen between min and max)
     * @param statOrLife number of lives / stat of the placed sprites (a random amount is chosen between min and max). If
     * <tt>null</tt>, {@link SpriteInfo#statOrLife()} is used instead.
     */
    public int placeRandom(List<SpriteInfo> types, Rect where, Predicate<Coord> isAllowed, 
            MinMax amount, MinMax statOrLife) {
        // Build list of available coordinates
        List<Coord> availableCoords = new ArrayList<>();
        for (int x = 0; x < where.w(); x++) {
            for (int y = 0; y < where.h(); y++) {
                Coord coord = new Coord(where.x() + x, where.y() + y);
                if (!usedCoords.contains(coord) && isAllowed.test(coord)) {
                    availableCoords.add(coord);
                }
            }
        }
        
        // Place
        int pickAmount = amount.pick(rnd);
        int result = 0;
        for (int k = 0; k < pickAmount; k++) {
            // Choose a random coordinate
            if (availableCoords.isEmpty()) {
                return result;
            }
            
            int coordIndex = availableCoords.size() == 1 ? 0 : rnd.nextInt(availableCoords.size());
            Coord position = availableCoords.remove(coordIndex);
            result++;
            
            // Choose a random sprite type and lives
            int typeIndex = types.size() == 1 ? 0 : rnd.nextInt(types.size());
            SpriteInfo info = types.get(typeIndex);
            int pickStat = info.statOrLife();
            if (statOrLife != null)
                pickStat = statOrLife.pick(rnd);
            
            // Place it
            chaosObjects.PutBlockObj(info.type(), (short) info.subKind(), pickStat, 
                    (short) position.x(), (short) position.y());
        }
        return result;
    }
    
    public int placeRandom(List<SpriteInfo> types, Rect where, Predicate<Coord> isAllowed, 
            MinMax amount) {
        return placeRandom(types, where, isAllowed, amount, null);
    }
    
    public int placeRandom(SpriteInfo type, Rect where, Predicate<Coord> isAllowed, 
            MinMax amount) {
        return placeRandom(List.of(type), where, isAllowed, amount, null);
    }
    
    public int placeRandom(SpriteInfo type, Rect where, Predicate<Coord> isAllowed, int amount) {
        return placeRandom(List.of(type), where, isAllowed, nb(amount));
    }
    
    /**
     * Create a {@link MinMax} corresponding to a fixed number
     */
    public MinMax nb(int value) {
        return MinMax.value(value);
    }
    
    public Predicate<Coord> background() {
        return (Coord coord) -> {
            return chaosObjects.OnlyBackground((short) coord.x(), (short) coord.y());
        };
    }
    
    public Predicate<Coord> background8() {
        return (Coord coord) -> {
            if (!chaosObjects.OnlyBackground((short) coord.x(), (short) coord.y()))
                return false;
            for (Coord delta : Coord.n8()) {
                Coord n = coord.add(delta);
                if (!chaosObjects.OnlyBackground((short) n.x(), (short) n.y()))
                    return false;
            }
            return true;
        };
    }
    
    /**
     * Create a predicate for coordinates, that check the target coordinate is a background,
     * and is a wall/not wall in the given mask, assuming the mask is at the given offset
     */
    public Predicate<Coord> mask(Coord offset, BinaryLevel mask, boolean wall) {
        return (Coord coord) -> {
            Coord maskCoord = coord.add(-offset.x(), -offset.y());
            return chaosObjects.OnlyBackground((short) coord.x(), (short) coord.y())
                    && (mask.isWall(maskCoord) == wall);
        };
    }
    
}
