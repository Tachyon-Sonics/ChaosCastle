package ch.chaos.castle.level;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.ChaosGraphics;
import ch.chaos.castle.ChaosMachine;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.DfsLabyrinth;

public class SpaceStation extends LevelBase {
    
    public void build() {
        Random rnd = new Random();
        chaos1Zone.rotate = false;
        chaos1Zone.flipVert = false;

        final int Width = 10;
        final int Height = 10;
        final int CellWidth = 10;
        final int CellHeight = 6;
        final int PadWidth = 10;
        final int YOffset = 19;
        final int FullWidth = Width * CellWidth + 1 + PadWidth;
        final int FullHeight = Height * CellHeight + 1 + YOffset;
        
        // 101x61 for Station
        LevelBuilder builder = new LevelBuilder(FullWidth, FullHeight, rnd);
        SpriteFiller filler = new SpriteFiller(rnd);
        // Stars as background
        builder.fillRandom(0, 0, FullWidth, FullHeight, 0, 7, builder::isBackground, builder::expRandom);

        // Station:
        DfsLabyrinth spaceStation = new DfsLabyrinth(Width, Height, CellWidth, CellHeight);
        spaceStation.build();
        Coord exitCell = spaceStation.getFarthestCell(new Coord(0, 0));
        
        // Walls
        spaceStation.forWalls((Coord coord) -> {
            builder.put(coord.add(0, YOffset), BarDark);
        });
        // Default floor between cells
        spaceStation.forHoles((Coord coord) -> {
            builder.put(coord.add(0, YOffset), Round4);
        });
        // Entry
        builder.put(1, YOffset, Tar);
        filler.putObj(new SpriteInfo(Anims.MACHINE, ChaosMachine.mDoor, 0), 
                (short) (1 * ChaosGraphics.BW + ChaosGraphics.BW / 2), 
                (short) (YOffset * ChaosGraphics.BH + ChaosGraphics.BH / 8));
        filler.putBlockBonus(ChaosBonus.tbSGSpeed, new Coord(1, YOffset));

        // Exit
        filler.putExit(exitCell.x() * CellWidth + CellWidth / 2, exitCell.y() * CellHeight + CellHeight / 2 + YOffset);
        // Player
        filler.putPlayer(FullWidth - 1, FullHeight - 1);
        filler.putBlockBonus(ChaosBonus.tbDBSpeed, new Coord(FullWidth - PadWidth, FullHeight - 1));
        
        // Cells Background
        Map<Coord, Integer> distances = spaceStation.getAllDistances(exitCell);
        int[] backgrounds = { Back2x2, Back4x4, Back8x8 };
        for (int cx = 0; cx < Width; cx++) {
            for (int cy = 0; cy < Height; cy++) {
                int distance = distances.get(new Coord(cx, cy));
                int background = (distance == 0 ? BackSmall : backgrounds[distance % 3]);
                for (int x = 1; x < CellWidth; x++) {
                    for (int y = 1; y < CellHeight; y++) {
                        builder.put(cx * CellWidth + x, cy * CellHeight + y + YOffset, background);
                    }
                }
            }
        }
        
        // Cells Content
        for (int cx = 0; cx < Width; cx++) {
            for (int cy = 0; cy < Height; cy++) {
                Rect cellRect = new Rect(cx * CellWidth + 1, cy * CellHeight + 1 + YOffset, CellWidth - 2, CellHeight - 2);
                int type = rnd.nextInt(5);
                // TODO (0) review, iterate on types and pick cell at random
                if (type == 0) {
                    // Bonus
                    filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), cellRect, filler.background(), new MinMax(1, 2));
                    filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), cellRect, filler.background(), new MinMax(0, 1));
                } else if (type == 1 || type == 2 || type == 3) {
                    // Aliens
                    List<SpriteInfo> aliens = List.of(
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienBox, 0),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienBox, 1),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aCartoon, 0),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorC, chaos1Zone.pLife3),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cNest, 0),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aKamikaze, 0),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aTri, chaos1Zone.pLife2),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aColor, chaos1Zone.pLife3),
                            new SpriteInfo(Anims.MACHINE, ChaosMachine.mTurret, 0)
                            );
                    int index = rnd.nextInt(aliens.size());
                    SpriteInfo alien = aliens.get(index);
                    Predicate<Coord> isAllowed = filler.background();
                    if (index < 3)
                        isAllowed = filler.nearWall4();
                    int amount = 4 + rnd.nextInt(12);
                    filler.placeRandom(alien, cellRect, isAllowed, amount);
                }
            }
        }
        
        Rect stationRect = new Rect(0, YOffset, Width * CellWidth, Height * CellHeight);
        filler.addOptions(stationRect, filler.background(), 4, 8, 1, 5, 0, 0, 2);
    }

}
