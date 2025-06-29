package ch.chaos.castle.alien;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.GameStat;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.ChaosGraphics;
import ch.chaos.castle.ChaosObjects;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BinaryLevel;

public class SpriteFiller {
    
    private final Random rnd;
    private final ChaosObjects chaosObjects;
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final Set<Coord> usedCoords = new HashSet<>();
    
    private final Stack<Set<Coord>> usedStack = new Stack<>();
    private boolean markUsed = true;
    private boolean preventUsed = true;

    
    public SpriteFiller(Random rnd) {
        this.rnd = rnd;
        this.chaosObjects = ChaosObjects.instance();
        this.chaosBase = ChaosBase.instance();
        this.chaosGraphics = ChaosGraphics.instance();
    }
    
    /**
     * Clear the set of used coordinates, meaning that next placement can be put on top of those
     * previous to this call.
     */
    public void reset() {
        usedCoords.clear();
    }
    
    public boolean isMarkUsed() {
        return markUsed;
    }
    
    public void setMarkUsed(boolean markUsed) {
        this.markUsed = markUsed;
    }
    
    public boolean isPreventUsed() {
        return preventUsed;
    }
    
    public void setPreventUsed(boolean preventUsed) {
        this.preventUsed = preventUsed;
    }
    
    public void pushUsed() {
        usedStack.push(new HashSet<>(usedCoords));
    }
    
    public void popUsed() {
        Set<Coord> popped = usedStack.pop();
        usedCoords.clear();
        usedCoords.addAll(popped);
    }
    
    public void resetUsed() {
        usedCoords.clear();
    }
    
    public boolean isAlreadyUsed(Coord coord) {
        return preventUsed && usedCoords.contains(coord);
    }
    
    private void markUsed(Coord coord) {
        if (markUsed) {
            usedCoords.add(coord);
        }
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
            } while (tries >= 0 && (isAlreadyUsed(position) || !isAllowed.test(position)));
            
            if (isAllowed.test(position)) {
                markUsed(position);
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
    
    public Coord getRandomPlace(Rect where, Predicate<Coord> isAllowed) {
        // Build list of available coordinates
        List<Coord> availableCoords = new ArrayList<>();
        for (int x = 0; x < where.w(); x++) {
            for (int y = 0; y < where.h(); y++) {
                Coord coord = new Coord(where.x() + x, where.y() + y);
                if (!isAlreadyUsed(coord) && isAllowed.test(coord)) {
                    availableCoords.add(coord);
                }
            }
        }
        
        // Pick one
        if (availableCoords.isEmpty())
            return null;
        int index = rnd.nextInt(availableCoords.size());
        return availableCoords.get(index);
    }
    
    /**
     * Place aliens / sprites randomly.
     * <p>
     * The exact specified amount of sprites will be placed, unless there is not enough available coordinates
     * @param types the sprite types. Each placed sprite is chosen among this list
     * @param where the rectangle in which to place sprites (tile-index-coordinates)
     * @param isAllowed predicate to check if placement is allowed on a given coordinate
     * @param selector how to randomly pick from the given types, possibly based on coordinate
     * @param amount the number of sprites to place (a random amount is chosen between min and max)
     * @param statOrLife number of lives / stat of the placed sprites (a random amount is chosen between min and max). If
     * <tt>null</tt>, {@link SpriteInfo#statOrLife()} is used instead.
     * @param maxFineShift max random fine shift of coordinates
     */
    public int placeRandom(List<SpriteInfo> types, Rect where, Predicate<Coord> isAllowed, Function<Coord, Integer> selector,
            MinMax amount, MinMax statOrLife, int maxFineShift) {
        // Build list of available coordinates
        List<Coord> availableCoords = new ArrayList<>();
        for (int x = 0; x < where.w(); x++) {
            for (int y = 0; y < where.h(); y++) {
                Coord coord = new Coord(where.x() + x, where.y() + y);
                if (!isAlreadyUsed(coord) && isAllowed.test(coord)) {
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
            Coord position = availableCoords.get(coordIndex);
            if (markUsed && preventUsed)
                availableCoords.remove(coordIndex);
            result++;
            
            // Choose a random sprite type and lives
            int typeIndex = selector.apply(position);
            SpriteInfo info = types.get(typeIndex);
            if (!info.equals(SpriteInfo.NONE)) {
                int pickStat = info.statOrLife();
                if (statOrLife != null)
                    pickStat = statOrLife.pick(rnd);
                
                SpriteInfo toPlace = new SpriteInfo(info.type(), info.subKind(), pickStat);
                
                // Place it
                if (maxFineShift == 0) {
                    putBlockObj(toPlace, position);
                } else {
                    putBlockObjRandomShift(toPlace, position, maxFineShift);
                }
                markUsed(position);
            }
        }
        return result;
    }
    
    public int placeRandom(List<SpriteInfo> types, Rect where, Predicate<Coord> isAllowed, 
            MinMax amount, MinMax statOrLife, int maxFineShift) {
        return placeRandom(types, where, isAllowed,
                (coord) -> types.size() == 1 ? 0 : rnd.nextInt(types.size()),
                amount, statOrLife, maxFineShift);
    }
    
    public int placeRandom(List<SpriteInfo> types, Rect where, Predicate<Coord> isAllowed, 
            MinMax amount, MinMax statOrLife) {
        return placeRandom(types, where, isAllowed, amount, statOrLife, 0);
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
    
    public int placeRandomS(SpriteInfo type, Rect where, Predicate<Coord> isAllowed, int amount, int maxShift) {
        return placeRandom(List.of(type), where, isAllowed, nb(amount), null, maxShift);
    }
    
    public int placeRandomS(SpriteInfo type, Rect where, Predicate<Coord> isAllowed, int amount, MinMax stat, int maxShift) {
        return placeRandom(List.of(type), where, isAllowed, nb(amount), stat, maxShift);
    }
    
    public int placeRandom(SpriteInfo type, Rect where, Predicate<Coord> isAllowed, int amount, int stat) {
        return placeRandom(List.of(type), where, isAllowed, nb(amount), nb(stat));
    }
    
    /**
     * Builder-pattern version of {@link #placeRandom(List, Rect, Predicate, Function, MinMax, MinMax, int)}.
     * <p>
     * Set the different arguments using the different methods of the returned object, and then invoke
     * {@link RandomPlacer#place()}.
     */
    public RandomPlacer randomPlacer() {
        return new RandomPlacer(rnd, this);
    }
    
    /**
     * Put a sprite at the exact given coordinates
     */
    public void putObj(SpriteInfo info, Coord position) {
        chaosObjects.PutObj(info.type(), (short) info.subKind(), (short) info.statOrLife(), 
                (short) position.x(), (short) position.y());
    }
    
    public void putObj(SpriteInfo info, int px, int py) {
        putObj(info, new Coord(px, py));
    }
    
    /**
     * Put a sprite at the given tile coordinate. It will be centered on the tile
     */
    public void putBlockObj(SpriteInfo info, Coord position) {
        chaosObjects.PutBlockObj(info.type(), (short) info.subKind(), info.statOrLife(), 
                (short) position.x(), (short) position.y());
        markUsed(position);
    }
    
    public void putBlockBonus(int tbType, Coord position) {
        chaosObjects.PutBlockBonus(tbType, (short) position.x(), (short) position.y());
        markUsed(position);
    }
    
    /**
     * Put a sprite at the given quarter-tile coordinate. It will be centered on the tile's quarter
     * @param position tile coordinate
     * @param quarter tile's quarter. Must be between (0, 0) and (1, 1) (inclusives)
     */
    public void putQuarterObj(SpriteInfo info, Coord position, Coord quarter) {
        if (quarter.x() < 0 || quarter.y() < 0 || quarter.x() > 1 || quarter.y() > 1)
            throw new IllegalArgumentException();
        chaosObjects.PutFineObj(info.type(), (short) info.subKind(), info.statOrLife(), 
                (short) position.x(), (short) position.y(), (short) quarter.x(), (short) quarter.y());
        markUsed(position);
    }
    
    public void putPlayer(int x, int y) {
        chaosObjects.PutPlayer((short) x, (short) y);
        markUsed(new Coord(x, y));
    }
    
    public void putExit(int x, int y) {
        chaosObjects.PutExit((short) x, (short) y);
        markUsed(new Coord(x, y));
    }
    
    public void putBubbleMaker(int stat, int x, int y) {
        chaosObjects.PutBubbleMaker(stat, (short) x, (short) y);
        markUsed(new Coord(x, y));
    }
    
    /**
     * Put a sprite at approximately at the center of the given tile coordinate.
     * <p>
     * The exact location is randomly shifted from the tile's center
     * @param position the tile coordinate
     * @param maxShift the maximum random displacement in both x and y. It will be between <tt>-maxShift</tt>
     * and <tt>maxShift</tt> (inclusives)
     */
    public void putBlockObjRandomShift(SpriteInfo info, Coord position, int maxShift) {
        int dx = rnd.nextInt(maxShift * 2 + 1) - maxShift;
        int dy = rnd.nextInt(maxShift * 2 + 1) - maxShift;
        Coord location = new Coord(
                position.x() * ChaosGraphics.BW + ChaosGraphics.BW / 2 + dx,
                position.y() * ChaosGraphics.BH + ChaosGraphics.BH / 2 + dy);
        putObj(info, location);
        markUsed(position);
    }
    
    public void addOptions(Rect where, Predicate<Coord> isAllowed, int nbGrid, int nbBumper, int nbChief, int nbGhost, int nbPopup, int nbBig, int nbSquare) {
        int stat = chaosBase.pLife * 3 + chaosBase.difficulty * 2;
        addIfDiff(where, new SpriteInfo(Anims.ALIEN2, ChaosCreator.cGrid, stat), isAllowed, 2, nbGrid);
        addIfDiff(where, new SpriteInfo(Anims.ALIEN1, ChaosAlien.aBumper, stat), isAllowed, 3, nbBumper);
        addIfDiff(where, new SpriteInfo(Anims.ALIEN2, ChaosCreator.cChief, stat), isAllowed, 4, nbChief);
        addIfDiff(where, new SpriteInfo(Anims.ALIEN2, ChaosCreator.cGhost, stat), isAllowed, 5, nbGhost);
        addIfDiff(where, new SpriteInfo(Anims.ALIEN2, ChaosCreator.cPopUp, stat), isAllowed, 6, nbPopup);
        addIfDiff(where, new SpriteInfo(Anims.ALIEN1, ChaosAlien.aBig, stat), isAllowed, 7, nbBig);
        addIfDiff(where, new SpriteInfo(Anims.ALIEN1, ChaosAlien.aSquare, stat), isAllowed, 8, nbSquare);
    }
    
    public void asNested(Runnable task) {
        GameStat stat = chaosBase.gameStat;
        chaosBase.gameStat = GameStat.Playing;
        try {
            task.run();
        } finally {
            chaosBase.gameStat = stat;
        }
    }
    
    private void addIfDiff(Rect where, SpriteInfo sprite, Predicate<Coord> isAllowed, int minDiff, int amount) {
        if (chaosBase.difficulty >= minDiff) {
            placeRandom(sprite, where, isAllowed, amount);
        }
    }
    
    public int pLife(int multiplier) {
        return chaosBase.pLife * multiplier;
    }
    
    public int life(int base, int playerLifeMultiplier, int difficultyMultiplier) {
        return base + chaosBase.pLife * playerLifeMultiplier + chaosBase.difficulty * difficultyMultiplier;
    }
    
    /**
     * Create a {@link MinMax} corresponding to a fixed number
     */
    public MinMax nb(int value) {
        return MinMax.value(value);
    }
    
    /**
     * Predicate for choosing a background tile
     */
    public Predicate<Coord> background() {
        return this::isBackground;
    }
    
    public Predicate<Coord> backgroundOrFalse() {
        return this::isBackgroundOrFalse;
    }
    
    /**
     * Predicate for choosing a background tile whose 4 neighbours are also background
     */
    public Predicate<Coord> background4() {
        return (Coord coord) -> {
            if (!isBackground(coord))
                return false;
            for (Coord delta : Coord.n4()) {
                Coord n = coord.add(delta);
                if (!isBackground(n))
                    return false;
            }
            return true;
        };
    }
    
    /**
     * Predicate for choosing a background tile whose 8 neighbours are also background
     */
    public Predicate<Coord> background8() {
        return (Coord coord) -> {
            if (!isBackground(coord))
                return false;
            for (Coord delta : Coord.n8()) {
                Coord n = coord.add(delta);
                if (!isBackground(n))
                    return false;
            }
            return true;
        };
    }
    
    public Predicate<Coord> backgroundOrFalse8() {
        return (Coord coord) -> {
            if (!isBackgroundOrFalse(coord))
                return false;
            for (Coord delta : Coord.n8()) {
                Coord n = coord.add(delta);
                if (!isBackgroundOrFalse(n))
                    return false;
            }
            return true;
        };
    }
    
    public Predicate<Coord> nearWall4() {
        return (Coord coord) -> {
            if (!isBackgroundOrFalse(coord))
                return false;
            for (Coord delta : Coord.n4()) {
                Coord n = coord.add(delta);
                if (!isBackgroundOrFalse(n))
                    return true;
            }
            return false;
        };
    }
    
    /**
     * Predicate for choosing a background tile whose neighbors with the given distance or less
     * are also background.
     */
    public Predicate<Coord> backgroundDistance(int distance) {
        return (Coord coord) -> {
            for (int dx = -distance; dx <= distance; dx++) {
                for (int dy = -distance; dy <= distance; dy++) {
                    double curDist = Math.sqrt(dx * dx + dy * dy);
                    if (curDist <= distance) {
                        if (!isBackground(coord.add(dx, dy)))
                            return false;
                    }
                }
            }
            return true;
        };
    }
    
    private Predicate<Coord> line(Coord delta, int minLength, int maxLength) {
        return (Coord coord) -> {
            if (isWall(coord))
                return false;
            while (isBackground(coord)) {
                coord = coord.add(delta.neg());
            }
            coord = coord.add(delta);
            int length = 0;
            while (isBackground(coord)) {
                length++;
                coord = coord.add(delta);
            }
            return length >= minLength && length <= maxLength;
        };
    }
    
    public Predicate<Coord> horizontalLine(int minLength, int maxLength) {
        return line(new Coord(1, 0), minLength, maxLength);
    }
    
    public Predicate<Coord> verticalLine(int minLength, int maxLength) {
        return line(new Coord(0, 1), minLength, maxLength);
    }
    
    public Predicate<Coord> diagonalLine(Coord delta, int minLength, int maxLength) {
        return (Coord coord) -> {
            if (isWall(coord))
                return false;
            while (isBackground(coord)) {
                coord = coord.add(delta.neg());
            }
            coord = coord.add(delta);
            Coord deltaX = new Coord(delta.x(), 0);
            Coord deltaY = new Coord(0, delta.y());
            int length = 0;
            while (isBackground(coord) && isBackground(coord.add(deltaX)) && isBackground(coord.add(deltaY))) {
                length++;
                coord = coord.add(delta);
            }
            return length >= minLength && length <= maxLength;
        };
    }
    
    /**
     * Must be a wall in -direction, then background in direction for nb tiles
     */
    public Predicate<Coord> wallToBackground(Coord direction, int nb) {
        return (Coord coord) -> {
            Coord back = coord.add(direction.neg());
            if (isBackground(back) || !isBackground(coord))
                return false;
            Coord front = coord;
            for (int k = 0; k < nb; k++) {
                front = front.add(direction);
                if (!isBackground(front))
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
            return isBackground(coord) && (mask.isWall(maskCoord) == wall);
        };
    }
    
    public boolean isBackground(Coord coord) {
        if (coord.x() <= 0 || coord.y() <= 0 
                || coord.x() >= chaosGraphics.castleWidth
                || coord.y() >= chaosGraphics.castleHeight) {
            return false;
        }
        return chaosObjects.OnlyBackground((short) coord.x(), (short) coord.y());
    }
    
    /**
     * Includes false backgrounds
     */
    private boolean isBackgroundOrFalse(Coord coord) {
        if (coord.x() <= 0 || coord.y() <= 0 
                || coord.x() >= chaosGraphics.castleWidth
                || coord.y() >= chaosGraphics.castleHeight) {
            return false;
        }
        return !chaosObjects.OnlyWall((short) coord.x(), (short) coord.y());
    }
    
    private boolean isWall(Coord coord) {
        return !isBackgroundOrFalse(coord);
    }
    
}
