package ch.chaos.castle.level;

import java.util.List;
import java.util.Map;
import java.util.Random;

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
        chaosObjects.Clear((short) FullWidth, (short) FullHeight); // TODO (1) helper for all stuff with walls and backgrounds
        // Stars as background
        chaosObjects.FillRandom((short) 0, (short) 0, (short) (FullWidth - 1), (short) (FullHeight - 1),
                (short) 0, (short) 7, chaosObjects.OnlyBackground_ref, chaosObjects.ExpRandom_ref);

        // Station:
        DfsLabyrinth spaceStation = new DfsLabyrinth(Width, Height, CellWidth, CellHeight);
        spaceStation.build();
        Coord exitCell = spaceStation.getFarthestCell(new Coord(0, 0));
        
        // Walls
        spaceStation.forWalls((Coord coord) -> {
            chaosObjects.Put((short) coord.x(), (short) (coord.y() + YOffset), (short) BarDark);
        });
        // Default floor between cells
        spaceStation.forHoles((Coord coord) -> {
            chaosObjects.Put((short) coord.x(), (short) (coord.y() + YOffset), (short) Round4);
        });
        // Entry
        chaosObjects.Put((short) 1, (short) YOffset, (short) Tar);
        chaosObjects.PutObj(Anims.MACHINE, (short) ChaosMachine.mDoor, 0, 
                (short) (1 * ChaosGraphics.BW + ChaosGraphics.BW / 2), 
                (short) (YOffset * ChaosGraphics.BH + ChaosGraphics.BH / 8));
        chaosObjects.PutBlockBonus(ChaosBonus.tbSGSpeed, (short) 1, (short) YOffset);

        // Exit
        chaosObjects.PutExit((short) (exitCell.x() * CellWidth + CellWidth / 2), (short) (exitCell.y() * CellHeight + CellHeight / 2 + YOffset));
        // Player
        chaosObjects.PutPlayer((short) (FullWidth - 1), (short) (FullHeight - 1));
        chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, (short) (FullWidth - PadWidth), (short) (FullHeight - 1));
        
        // Cells Background
        Map<Coord, Integer> distances = spaceStation.getAllDistances(exitCell);
        int[] backgrounds = { Back2x2, Back4x4, Back8x8 };
        for (int cx = 0; cx < Width; cx++) {
            for (int cy = 0; cy < Height; cy++) {
                int distance = distances.get(new Coord(cx, cy));
                int background = (distance == 0 ? BackSmall : backgrounds[distance % 3]);
                for (int x = 1; x < CellWidth; x++) {
                    for (int y = 1; y < CellHeight; y++) {
                        chaosObjects.Put((short) (cx * CellWidth + x), (short) (cy * CellHeight + y + YOffset), (short) background);
                    }
                }
            }
        }
        
        // Cells Content
        SpriteFiller filler = new SpriteFiller(rnd);
        for (int cx = 0; cx < Width; cx++) {
            for (int cy = 0; cy < Height; cy++) {
                Rect cellRect = new Rect(cx * CellWidth + 1, cy * CellHeight + 1 + YOffset, CellWidth - 2, CellHeight - 2);
                int type = rnd.nextInt(3);
                // TODO (0) review, iterate on types and pick cell at random
                if (type == 0) {
                    // Bonus
                    filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), cellRect, filler.background(), new MinMax(0, 1));
                    filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), cellRect, filler.background(), 1);
                } else if (type == 1) {
                    // Aliens
                    List<SpriteInfo> aliens = List.of(
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienBox, 0),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienBox, 1),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorC, chaos1Zone.pLife3),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cNest, 0),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aCartoon, 0),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aKamikaze, 0),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aTri, chaos1Zone.pLife2),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aColor, chaos1Zone.pLife3),
                            new SpriteInfo(Anims.MACHINE, ChaosMachine.mTurret, 0)
                            );
                    SpriteInfo alien = aliens.get(rnd.nextInt(aliens.size()));
                    int amount = 2 + rnd.nextInt(6);
                    filler.placeRandom(alien, cellRect, filler.nearWall4(), amount);
                }
            }
        }
        
        Rect stationRect = new Rect(0, YOffset, Width * CellWidth, Height * CellHeight);
        filler.addOptions(stationRect, filler.background(), 4, 8, 1, 5, 0, 0, 2);
    }

}
