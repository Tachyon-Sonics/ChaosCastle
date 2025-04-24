package ch.chaos.castle.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DfsLabyrinth extends BinaryLevelBuilderBase {
    
    private final int width;
    private final int height;
    private final int cellWidth;
    private final int cellHeight;

    // Coord (0,0 -- width,height) -> openings (from Coord.n4())
    private final Map<Coord, List<Coord>> openings = new HashMap<>();
    private final boolean[][] reached;
    
    private final Random rnd = new Random();
    
    
    public DfsLabyrinth(int width, int height, int cellWidth, int cellHeight) {
        super(width * cellWidth + 1, height * cellHeight + 1);
        this.width = width;
        this.height = height;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Coord coord = new Coord(x, y);
                openings.put(coord, new ArrayList<>());
            }
        }
        reached = new boolean[width][height];
    }
    
    public void build() {
        List<Coord> addedCells = new ArrayList<>();
        addedCells.add(new Coord(0, 0));
        reached[0][0] = true;
        while (!isFinished()) {
            // Pick last added cell so far with at least one unreached neighbour
            int cellIndex = addedCells.size() - 1;
            while (!hasUnreachedNeighbourCell(addedCells.get(cellIndex))) {
                cellIndex--;
            }
            
            // Pick a random unreached neighbour
            Coord cell = addedCells.get(cellIndex);
            Coord delta = pickRandomUnreachedNeighbourg(cell);
            
//            System.out.println(cell.toShortString() + " -> " + cell.add(delta).toShortString());
            
            // Open a path from current cell to target cell
            openings.get(cell).add(delta);
            Coord newCell = cell.add(delta);
            reached[newCell.x()][newCell.y()] = true;
            
            // Add target cell
            addedCells.add(newCell);
        }
        
        // Now paint the cells
        for (int cx = 0; cx < width; cx++) {
            for (int cy = 0; cy < height; cy++) {
                // Exterior walls + interior empty
                for (int x = 0; x <= cellWidth; x++) {
                    for (int y = 0; y <= cellHeight; y++) {
                        boolean border = (x == 0 || y == 0 || x == cellWidth || y == cellHeight);
                        setWall(cx * cellWidth + x, cy * cellHeight + y, border);
                    }
                }
            }
        }
        
        // And the openings
        for (int cx = 0; cx < width; cx++) {
            for (int cy = 0; cy < height; cy++) {
                Coord cell = new Coord(cx, cy);
                for (Coord delta : openings.get(cell)) {
                    int x = (delta.x() + 1) * cellWidth / 2;
                    int y = (delta.y() + 1) * cellHeight / 2;
                    int sx = (delta.x() == 0 ? 1 : x);
                    int ex = (delta.x() == 0 ? cellWidth - 1 : x);
                    int sy = (delta.y() == 0 ? 1 : y);
                    int ey = (delta.y() == 0 ? cellHeight - 1 : y);
                    
//                    setWall(cx * cellWidth + x, cy * cellHeight + y, false);
                    
                    for (int px = sx; px <= ex; px++) {
                        for (int py = sy; py <= ey; py++) {
                            setWall(cx * cellWidth + px, cy * cellHeight + py, false);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * The space station is finished when all cells are reachable
     */
    private boolean isFinished() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!reached[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isReached(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return true;
        return reached[x][y];
    }
    
    private boolean hasUnreachedNeighbourCell(Coord cell) {
        for (Coord delta : Coord.n4()) {
            if (!isReachedNeighbour(cell, delta)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isReachedNeighbour(Coord cell, Coord delta) {
        Coord neighbour = cell.add(delta);
        return isReached(neighbour.x(), neighbour.y());
    }
    
    /**
     * From the given cell, pick a random 4-delta that yields to a unreached neighbouring cell.
     * Return the chosen delta.
     */
    private Coord pickRandomUnreachedNeighbourg(Coord cell) {
        Coord delta;
        do {
            delta = Coord.n4().get(rnd.nextInt(4));
        } while (isReachedNeighbour(cell, delta));
        return delta;
    }
    
    public Coord getFarthestCell(Coord fromCell) {
        List<Coord> visited = new ArrayList<>();
        bfsVisit(fromCell, null, visited::add);
        return visited.get(visited.size() - 1);
    }
    
    public Map<Coord, Integer> getAllDistances(Coord fromCell) {
        Map<Coord, Integer> result = new LinkedHashMap<>();
        AtomicInteger distance = new AtomicInteger();
        bfsVisit(fromCell, distance::incrementAndGet, (Coord visited) -> {
            if (!result.containsKey(visited)) {
                result.put(visited, distance.get());
            }
        });
        return result;
    }
    
    public int getDistanceBetween(Coord cell1, Coord cell2) {
        Map<Coord, Integer> allDistances = getAllDistances(cell1);
        return allDistances.get(cell2);
    }
    
    private void bfsVisit(Coord fromCell, Runnable onRound, Consumer<Coord> onCellVisited) {
        // BFS
        Set<Coord> current = new LinkedHashSet<>();
        Set<Coord> visited = new HashSet<>();
        current.add(fromCell);
        while (!current.isEmpty()) {
            Set<Coord> next = new LinkedHashSet<>();
            for (Coord cell : current) {
                // Visit current cell
                visited.add(cell);
                if (onCellVisited != null) {
                    onCellVisited.accept(cell);
                }
                
                // Add all its reachable neighbours that have not been visited yet to the next round
                for (Coord delta : getOpenings(cell)) {
                    Coord nextCell = cell.add(delta);
                    if (!current.contains(nextCell) && !visited.contains(nextCell)) {
                        next.add(nextCell);
                    }
                }
            }
            
            // Proceed with next round
            current = next;
            if (onRound != null) {
                onRound.run();
            }
        }
    }
    
    private List<Coord> getOpenings(Coord cell) {
        List<Coord> result = new ArrayList<>(openings.get(cell));
        for (Coord delta : Coord.n4()) {
            if (!result.contains(delta)) { // Check if there is an opening declared in the target cell
                Coord neighbour = cell.add(delta);
                Coord revDelta = new Coord(-delta.x(), -delta.y());
                if (openings.get(neighbour) != null && openings.get(neighbour).contains(revDelta)) {
                    result.add(delta);
                }
            }
        }
        return result;
    }
    
    public static void main(String[] args) {
        final int Width = 10;
        final int Height = 10;
        final int CellWidth = 10;
        final int CellHeight = 6;
        DfsLabyrinth spaceStation = new DfsLabyrinth(Width, Height, CellWidth, CellHeight);
        spaceStation.build();
        Coord exitCell = spaceStation.getFarthestCell(new Coord(0, 0));
        spaceStation.setWall(exitCell.x() * CellWidth + CellWidth / 2, exitCell.y() * CellHeight + CellHeight / 2, true); // Just to highlight the exit
        System.out.println(spaceStation.toString());
        
        int distanceToExit = spaceStation.getDistanceBetween(new Coord(0, 0), exitCell);
        System.out.println("Distance to EXIT: " + distanceToExit);
        
//        // Distances from exit can be used to choose the background, and to hint the player. Must be weighted using 'distanceToExit'
//        int prevDistance = -1;
//        Map<Coord, Integer> distancesFromExit = spaceStation.getAllDistances(exitCell);
//        for (Map.Entry<Coord, Integer> entry : distancesFromExit.entrySet()) {
//            Coord cell = entry.getKey();
//            int distance = entry.getValue();
//            if (distance != prevDistance) {
//                prevDistance = distance;
//                System.out.println();
//                System.out.print(distance + ": ");
//            }
//            System.out.print(cell.toShortString() + " ");
//        }
//        System.out.println();
    }

}
