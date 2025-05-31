package ch.chaos.castle.alien;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;

/**
 * Builder-pattern version of {@link SpriteFiller#placeRandom(List, Rect, Predicate, Function, MinMax, MinMax, int)}
 * <p>
 * Normally used through {@link SpriteFiller#randomPlacer()}
 */
public class RandomPlacer {

    private final Random rnd;
    private final SpriteFiller spriteFiller;
    private List<SpriteInfo> types;
    private Rect where;
    private Predicate<Coord> isAllowed;
    private Function<Coord, Integer> selector;
    private MinMax amount;
    private MinMax statOrLife;
    private int maxFineShift = 0;
    
    
    public RandomPlacer(Random rnd, SpriteFiller spriteFiller) {
        this.rnd = rnd;
        this.spriteFiller = spriteFiller;
    }
    
    public RandomPlacer where(Rect where) {
        this.where = where;
        return this;
    }
    
    public RandomPlacer types(List<SpriteInfo> types) {
        this.types = types;
        if (selector == null)
            selector = (coord) -> types.size() == 1 ? 0 : rnd.nextInt(types.size());
        return this;
    }
    
    public RandomPlacer type(SpriteInfo type) {
        return types(List.of(type));
    }
    
    public RandomPlacer isAllowed(Predicate<Coord> isAllowed) {
        this.isAllowed = isAllowed;
        return this;
    }
    
    /**
     * Combined any existing 'isAllowed' predicate by AND'ing it with the given one
     */
    public RandomPlacer andAllowed(Predicate<Coord> isAllowed) {
        if (this.isAllowed == null) {
            return isAllowed(isAllowed);
        } else {
            Predicate<Coord> previous = this.isAllowed;
            Predicate<Coord> combined = (Coord coord) -> previous.test(coord) && isAllowed.test(coord);
            this.isAllowed = combined;
        }
        return this;
    }
    
    public RandomPlacer selector(Function<Coord, Integer> selector) {
        this.selector = selector;
        return this;
    }
    
    public RandomPlacer amount(MinMax amount) {
        this.amount = amount;
        return this;
    }
    
    public RandomPlacer amount(int amount) {
        return amount(MinMax.value(amount));
    }
    
    public RandomPlacer statOrLife(MinMax statOrLife) {
        this.statOrLife = statOrLife;
        return this;
    }
    
    public RandomPlacer statOrLife(int statOrLife) {
        return statOrLife(MinMax.value(statOrLife));
    }
    
    public RandomPlacer maxFineShift(int maxFineShift) {
        this.maxFineShift = maxFineShift;
        return this;
    }
    
    public int place() {
        Objects.requireNonNull(types, "types");
        Objects.requireNonNull(where, "where");
        Objects.requireNonNull(isAllowed, "isAllowed");
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(amount, "amount");
        return spriteFiller.placeRandom(types, where, isAllowed, selector, amount, statOrLife, maxFineShift);
    }
}
