package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.library.Checks;
import ch.chaos.library.Dialogs;
import ch.chaos.library.Files;
import ch.chaos.library.Files.AccessFlags;
import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.Modes;
import ch.chaos.library.Input;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosImages {

    // Imports
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final Checks checks;
    private final Dialogs dialogs;
    private final Files files;
    private final Graphics graphics;
    private final Input input;
    private final Languages languages;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosImages() {
        instance = this; // Set early to handle circular dependencies
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        checks = Checks.instance();
        dialogs = Dialogs.instance();
        files = Files.instance();
        graphics = Graphics.instance();
        input = Input.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // CONST

    private static final String ImagesFile = "Images";
    private static final String ObjectsFile = "Objects";
    private static final String OpenIErrMsg = "Cannot open data file: Images";
    private static final String OpenOErrMsg = "Cannot open data file: Objects";
    private static final String ReadIErrMsg = "Error reading file: Images";
    private static final String ReadOErrMsg = "Error reading file: Objects";


    // VAR

    private short fadeMn1;
    private short fadeMn2;
    private short fadeMx1;
    private short fadeMx2;
    private short lastCastleLevel;


    public short getFadeMn1() {
        return this.fadeMn1;
    }

    public void setFadeMn1(short fadeMn1) {
        this.fadeMn1 = fadeMn1;
    }

    public short getFadeMn2() {
        return this.fadeMn2;
    }

    public void setFadeMn2(short fadeMn2) {
        this.fadeMn2 = fadeMn2;
    }

    public short getFadeMx1() {
        return this.fadeMx1;
    }

    public void setFadeMx1(short fadeMx1) {
        this.fadeMx1 = fadeMx1;
    }

    public short getFadeMx2() {
        return this.fadeMx2;
    }

    public void setFadeMx2(short fadeMx2) {
        this.fadeMx2 = fadeMx2;
    }

    public short getLastCastleLevel() {
        return this.lastCastleLevel;
    }

    public void setLastCastleLevel(short lastCastleLevel) {
        this.lastCastleLevel = lastCastleLevel;
    }


    // PROCEDURE

    private void SetMetalPalette() {
        chaosGraphics.SetRGB((short) 8, (short) 238, (short) 87, (short) 0);
        chaosGraphics.SetRGB((short) 9, (short) 35, (short) 35, (short) 35);
        chaosGraphics.SetRGB((short) 10, (short) 92, (short) 92, (short) 92);
        chaosGraphics.SetRGB((short) 11, (short) 153, (short) 153, (short) 153);
        chaosGraphics.SetRGB((short) 12, (short) 68, (short) 102, (short) 204);
        chaosGraphics.SetRGB((short) 13, (short) 221, (short) 85, (short) 85);
        chaosGraphics.SetRGB((short) 14, (short) 51, (short) 221, (short) 68);
        chaosGraphics.SetRGB((short) 15, (short) 238, (short) 238, (short) 238);
    }

    private void SetGhostPalette() {
        SetMetalPalette();
        chaosGraphics.CycleRGB((short) 9, (short) 3, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 10, (short) 3, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 11, (short) 3, (short) 0, (short) 0, (short) 0);
    }

    private void SetOrPalette() {
        chaosGraphics.SetRGB((short) 8, (short) 255, (short) 68, (short) 0);
        chaosGraphics.SetRGB((short) 9, (short) 68, (short) 21, (short) 0);
        chaosGraphics.SetRGB((short) 10, (short) 153, (short) 40, (short) 0);
        chaosGraphics.SetRGB((short) 11, (short) 205, (short) 80, (short) 0);
        chaosGraphics.SetRGB((short) 12, (short) 238, (short) 187, (short) 0);
        chaosGraphics.SetRGB((short) 13, (short) 255, (short) 153, (short) 0);
        chaosGraphics.SetRGB((short) 14, (short) 255, (short) 102, (short) 0);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetBluePalette() {
        chaosGraphics.SetRGB((short) 8, (short) 0, (short) 68, (short) 255);
        chaosGraphics.SetRGB((short) 9, (short) 0, (short) 34, (short) 85);
        chaosGraphics.SetRGB((short) 10, (short) 0, (short) 51, (short) 170);
        chaosGraphics.SetRGB((short) 11, (short) 0, (short) 85, (short) 221);
        chaosGraphics.SetRGB((short) 12, (short) 0, (short) 187, (short) 238);
        chaosGraphics.SetRGB((short) 13, (short) 0, (short) 153, (short) 255);
        chaosGraphics.SetRGB((short) 14, (short) 0, (short) 102, (short) 255);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetForestPalette() {
        chaosGraphics.SetRGB((short) 8, (short) 6, (short) 15, (short) 0);
        chaosGraphics.SetRGB((short) 9, (short) 70, (short) 34, (short) 0);
        chaosGraphics.SetRGB((short) 10, (short) 137, (short) 68, (short) 0);
        chaosGraphics.SetRGB((short) 11, (short) 187, (short) 136, (short) 15);
        chaosGraphics.SetRGB((short) 12, (short) 0, (short) 55, (short) 0);
        chaosGraphics.SetRGB((short) 13, (short) 0, (short) 110, (short) 0);
        chaosGraphics.SetRGB((short) 14, (short) 75, (short) 180, (short) 0);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetBagleyPalette() {
        chaosGraphics.SetRGB((short) 8, (short) 15, (short) 6, (short) 0);
        chaosGraphics.SetRGB((short) 9, (short) 80, (short) 34, (short) 34);
        chaosGraphics.SetRGB((short) 10, (short) 140, (short) 68, (short) 35);
        chaosGraphics.SetRGB((short) 11, (short) 187, (short) 136, (short) 50);
        chaosGraphics.SetRGB((short) 12, (short) 85, (short) 51, (short) 0);
        chaosGraphics.SetRGB((short) 13, (short) 119, (short) 85, (short) 0);
        chaosGraphics.SetRGB((short) 14, (short) 187, (short) 119, (short) 0);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetGraveyardPalette() {
        chaosGraphics.SetRGB((short) 8, (short) 3, (short) 3, (short) 3);
        chaosGraphics.SetRGB((short) 9, (short) 68, (short) 68, (short) 68);
        chaosGraphics.SetRGB((short) 10, (short) 102, (short) 102, (short) 102);
        chaosGraphics.SetRGB((short) 11, (short) 136, (short) 136, (short) 136);
        chaosGraphics.SetRGB((short) 12, (short) 51, (short) 51, (short) 51);
        chaosGraphics.SetRGB((short) 13, (short) 119, (short) 119, (short) 119);
        chaosGraphics.SetRGB((short) 14, (short) 187, (short) 187, (short) 187);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetWinterPalette() {
        chaosGraphics.SetRGB((short) 8, (short) 68, (short) 85, (short) 102);
        chaosGraphics.SetRGB((short) 9, (short) 119, (short) 68, (short) 34);
        chaosGraphics.SetRGB((short) 10, (short) 204, (short) 221, (short) 238);
        chaosGraphics.SetRGB((short) 11, (short) 230, (short) 238, (short) 255);
        chaosGraphics.SetRGB((short) 12, (short) 35, (short) 35, (short) 35);
        chaosGraphics.SetRGB((short) 13, (short) 193, (short) 210, (short) 252);
        chaosGraphics.SetRGB((short) 14, (short) 170, (short) 221, (short) 255);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetJunglePalette() {
        SetForestPalette();
        chaosGraphics.SetRGB((short) 14, (short) 0, (short) 68, (short) 204);
    }

    private void DoFade(short fadeMin, short fadeMax, boolean cycle) {
        // VAR
        int sr = 0;
        int sg = 0;
        int sb = 0;
        int dr = 0;
        int dg = 0;
        int db = 0;
        int c = 0;
        int r = 0;
        int g = 0;
        int b = 0;

        sr = 0;
        sg = 0;
        sb = 0;
        if (fadeMin == 0)
            sr = 255;
        else if (fadeMin == 1)
            sg = 255;
        else if (fadeMin == 2)
            sb = 255;
        dr = 255;
        dg = 255;
        db = 255;
        if (fadeMax == 0)
            dr = 0;
        else if (fadeMax == 1)
            dg = 0;
        else if (fadeMax == 2)
            db = 0;
        for (c = 0; c <= 5; c++) {
            r = (sr * (5 - c) + dr * c) / 7;
            g = (sg * (5 - c) + dg * c) / 7;
            b = (sb * (5 - c) + db * c) / 7;
            if (cycle)
                chaosGraphics.CycleRGB((short) (c + 9), (short) 2, (short) r, (short) g, (short) b);
            else
                chaosGraphics.SetRGB((short) (c + 9), (short) r, (short) g, (short) b);
        }
    }

    private void SetFadePalette() {
        if (chaosBase.level[Zone.Castle.ordinal()] != lastCastleLevel) {
            fadeMn1 = (short) (trigo.RND() % 4);
            fadeMn2 = (short) (trigo.RND() % 4);
            fadeMx1 = (short) (trigo.RND() % 4);
            fadeMx2 = (short) (trigo.RND() % 4);
        }
        DoFade(fadeMn1, fadeMx1, false);
        if (chaosBase.difficulty >= 2)
            DoFade(fadeMn2, fadeMx2, true);
        chaosGraphics.SetRGB((short) 8, (short) 68, (short) 68, (short) 68);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetRGBIcePalette() {
        chaosGraphics.SetRGB((short) 8, (short) 51, (short) 51, (short) 85);
        chaosGraphics.SetRGB((short) 9, (short) 102, (short) 102, (short) 119);
        chaosGraphics.SetRGB((short) 10, (short) 153, (short) 153, (short) 187);
        chaosGraphics.SetRGB((short) 11, (short) 204, (short) 204, (short) 255);
        chaosGraphics.SetRGB((short) 12, (short) 0, (short) 70, (short) 105);
        chaosGraphics.SetRGB((short) 13, (short) 0, (short) 90, (short) 90);
        chaosGraphics.SetRGB((short) 14, (short) 0, (short) 100, (short) 115);
        chaosGraphics.SetRGB((short) 15, (short) 204, (short) 255, (short) 255);
    }

    private void SetAnimPalette() {
        chaosGraphics.SetRGB((short) 8, (short) 0, (short) 255, (short) 153);
        chaosGraphics.SetRGB((short) 9, (short) 51, (short) 51, (short) 51);
        chaosGraphics.SetRGB((short) 10, (short) 102, (short) 102, (short) 102);
        chaosGraphics.SetRGB((short) 11, (short) 153, (short) 153, (short) 153);
        chaosGraphics.SetRGB((short) 12, (short) 0, (short) 255, (short) 0);
        chaosGraphics.SetRGB((short) 13, (short) 204, (short) 0, (short) 0);
        chaosGraphics.SetRGB((short) 14, (short) 0, (short) 0, (short) 153);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetAnimatedPalette() {
        chaosGraphics.SetRGB((short) 8, (short) 0, (short) 0, (short) 255);
        chaosGraphics.CycleRGB((short) 8, (short) 4, (short) 0, (short) 255, (short) 0);
        chaosGraphics.SetRGB((short) 9, (short) 51, (short) 51, (short) 51);
        chaosGraphics.SetRGB((short) 10, (short) 102, (short) 102, (short) 102);
        chaosGraphics.SetRGB((short) 11, (short) 153, (short) 153, (short) 153);
        chaosGraphics.SetRGB((short) 12, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 12, (short) 4, (short) 0, (short) 255, (short) 0);
        chaosGraphics.SetRGB((short) 13, (short) 255, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 13, (short) 17, (short) 0, (short) 68, (short) 0);
        chaosGraphics.SetRGB((short) 14, (short) 0, (short) 0, (short) 255);
        chaosGraphics.CycleRGB((short) 14, (short) 4, (short) 0, (short) 0, (short) 0);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetAnimatedPalette2() {
        chaosGraphics.SetRGB((short) 8, (short) 255, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 8, (short) 6, (short) 0, (short) 255, (short) 0);
        chaosGraphics.SetRGB((short) 9, (short) 51, (short) 51, (short) 51);
        chaosGraphics.SetRGB((short) 10, (short) 102, (short) 102, (short) 102);
        chaosGraphics.SetRGB((short) 11, (short) 153, (short) 153, (short) 153);
        chaosGraphics.SetRGB((short) 12, (short) 255, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 12, (short) 6, (short) 0, (short) 0, (short) 0);
        chaosGraphics.SetRGB((short) 13, (short) 70, (short) 70, (short) 0);
        chaosGraphics.CycleRGB((short) 13, (short) 13, (short) 0, (short) 180, (short) 180);
        chaosGraphics.SetRGB((short) 14, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 14, (short) 6, (short) 0, (short) 255, (short) 0);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetAnimatedPalette3() {
        chaosGraphics.SetRGB((short) 8, (short) 180, (short) 180, (short) 180);
        chaosGraphics.CycleRGB((short) 8, (short) 12, (short) 90, (short) 90, (short) 90);
        chaosGraphics.SetRGB((short) 9, (short) 51, (short) 51, (short) 51);
        chaosGraphics.SetRGB((short) 10, (short) 102, (short) 102, (short) 102);
        chaosGraphics.SetRGB((short) 11, (short) 153, (short) 153, (short) 153);
        chaosGraphics.SetRGB((short) 12, (short) 180, (short) 180, (short) 180);
        chaosGraphics.CycleRGB((short) 12, (short) 6, (short) 0, (short) 0, (short) 0);
        chaosGraphics.SetRGB((short) 13, (short) 240, (short) 240, (short) 240);
        chaosGraphics.CycleRGB((short) 13, (short) 13, (short) 0, (short) 0, (short) 0);
        chaosGraphics.SetRGB((short) 14, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 14, (short) 6, (short) 180, (short) 180, (short) 180);
        chaosGraphics.SetRGB((short) 15, (short) 255, (short) 255, (short) 255);
    }

    private void SetFactoryPalette() {
        SetMetalPalette();
        chaosGraphics.CycleRGB((short) 8, (short) 13, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 12, (short) 7, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 13, (short) 24, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 14, (short) 19, (short) 0, (short) 0, (short) 0);
    }

    private void SetDarkPalette() {
        chaosGraphics.SetRGB((short) 8, (short) 119, (short) 44, (short) 0);
        chaosGraphics.SetRGB((short) 9, (short) 18, (short) 18, (short) 18);
        chaosGraphics.SetRGB((short) 10, (short) 46, (short) 46, (short) 46);
        chaosGraphics.SetRGB((short) 11, (short) 77, (short) 77, (short) 77);
        chaosGraphics.SetRGB((short) 12, (short) 34, (short) 51, (short) 102);
        chaosGraphics.SetRGB((short) 13, (short) 111, (short) 43, (short) 43);
        chaosGraphics.SetRGB((short) 14, (short) 26, (short) 111, (short) 34);
        chaosGraphics.SetRGB((short) 15, (short) 119, (short) 119, (short) 119);
    }

    private void SetDarkFactoryPalette() {
        SetDarkPalette();
        chaosGraphics.CycleRGB((short) 8, (short) 13, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 12, (short) 7, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 13, (short) 24, (short) 0, (short) 0, (short) 0);
        chaosGraphics.CycleRGB((short) 14, (short) 19, (short) 0, (short) 0, (short) 0);
    }

    private void InitImages_DrawNextBlock(int BlockSize, Dialogs.GadgetPtr progress, short[] block, short[] dest, /* VAR */ Runtime.IRef<Short> bRead, /* VAR */ Runtime.IRef<Integer> pc, /* VAR */ Runtime.IRef<Short> px, /* VAR */ Runtime.IRef<Short> py, /* VAR */ Runtime.IRef<Integer> fill, int boost) {
        // VAR
        Graphics.Image image = new Graphics.Image();
        Input.Event e = new Input.Event(); /* WRT */
        int p = 0;
        short x = 0;
        short y = 0;
        short z = 0;
        short dx = 0;
        short dy = 0;
        short v = 0;
        int patCnt = 0;
        int val = 0;
        int pix = 0;
        int add = 0;

        if (chaosGraphics.color) {
            bRead.set((short) files.ReadFileBytes(chaosBase.file, block, BlockSize));
            if (bRead.get() != BlockSize)
                checks.Check(true, Runtime.castToRef(languages.ADL(ReadIErrMsg), String.class), files.FileErrorMsg());
            if (chaosGraphics.dualpf) {
                for (z = (short) (BlockSize - 1); z >= 0; z -= 1) {
                    v = block[z];
                    block[z * 2 + 1] = (short) (v % 16);
                    block[z * 2] = (short) (v / 16);
                }
                y = 8;
            } else {
                y = 4;
            }
            image.data = block;
            image.bitPerPix = y;
            image.bytePerRow = y * 4;
            image.width = ChaosGraphics.BW;
            image.height = ChaosGraphics.BH;
            image.zw = 1;
            image.zh = 1;
            if (chaosGraphics.mulS > 1) {
                graphics.SetArea(chaosGraphics.shapeArea);
                graphics.DrawImage(image, (short) 0, (short) 0, (short) 0, (short) 0, (short) ChaosGraphics.BW, (short) ChaosGraphics.BH);
                graphics.SetArea(chaosGraphics.imageArea);
                graphics.ScaleRect(chaosGraphics.shapeArea, (short) 0, (short) 0, (short) ChaosGraphics.BW, (short) ChaosGraphics.BH, chaosGraphics.W.invoke(px.get()), chaosGraphics.H.invoke(py.get()), chaosGraphics.W.invoke((short) (px.get() + ChaosGraphics.BW)), chaosGraphics.H.invoke((short) (py.get() + ChaosGraphics.BH)));
            } else {
                graphics.SetArea(chaosGraphics.imageArea);
                graphics.DrawImage(image, (short) 0, (short) 0, px.get(), py.get(), (short) 32, (short) 32);
            }
            if (chaosGraphics.dualpf) {
                for (z = 0; z < BlockSize * 2; z++) {
                    block[z] = (short) (block[z] * 16);
                }
                if (chaosGraphics.mulS > 1) {
                    graphics.SetArea(chaosGraphics.shapeArea);
                    graphics.DrawImage(image, (short) 0, (short) 0, (short) 0, (short) 0, (short) ChaosGraphics.BW, (short) ChaosGraphics.BH);
                    graphics.SetArea(chaosGraphics.image2Area);
                    graphics.ScaleRect(chaosGraphics.shapeArea, (short) 0, (short) 0, (short) ChaosGraphics.BW, (short) ChaosGraphics.BH, chaosGraphics.W.invoke(px.get()), chaosGraphics.H.invoke(py.get()), chaosGraphics.W.invoke((short) (px.get() + ChaosGraphics.BW)), chaosGraphics.H.invoke((short) (py.get() + ChaosGraphics.BH)));
                } else {
                    graphics.SetArea(chaosGraphics.image2Area);
                    graphics.DrawImage(image, (short) 0, (short) 0, px.get(), py.get(), (short) 32, (short) 32);
                }
            }
        } else {
            graphics.SetPen(1);
            patCnt = 256;
            p = 0;
            bRead.set((short) files.ReadFileBytes(chaosBase.file, block, BlockSize));
            if (bRead.get() != BlockSize)
                checks.Check(true, Runtime.castToRef(languages.ADL(ReadIErrMsg), String.class), files.FileErrorMsg());
            for (y = 0; y < ChaosGraphics.BH; y++) {
                for (dy = 0; dy < chaosGraphics.mulS; dy++) {
                    z = (short) (y * ChaosGraphics.BW);
                    for (x = 0; x < ChaosGraphics.BW; x++) {
                        pix = memory.GetBitField(new Runtime.ArrayElementRef<>(block, z / 2), (z % 2) * 4, (short) 4);
                        for (dx = 0; dx < chaosGraphics.mulS; dx++) {
                            { // WITH
                                ChaosGraphics.Palette _palette = chaosGraphics.palette[pix];
                                add = _palette.red;
                                add += _palette.green;
                                add += _palette.blue;
                            }
                            patCnt += add * boost / 8;
                            if (patCnt >= 512) {
                                if (patCnt >= 1024)
                                    patCnt = 511;
                                else
                                    patCnt -= 512;
                                val = 1;
                            } else {
                                val = 0;
                            }
                            pc.set(p / 8);
                            memory.SetBitField(new Runtime.ArrayElementRef<>(dest, pc.get()), (p % 8), (short) 1, (short) val);
                            p++;
                        }
                        z++;
                    }
                }
            }
            graphics.SetArea(chaosGraphics.imageArea);
            image.data = dest;
            image.bitPerPix = 1;
            image.bytePerRow = chaosGraphics.mulS * 4;
            image.width = (short) (ChaosGraphics.BW * chaosGraphics.mulS);
            image.height = (short) (ChaosGraphics.BH * chaosGraphics.mulS);
            image.zw = 1;
            image.zh = 1;
            graphics.DrawImage(image, (short) 0, (short) 0, (short) (px.get() * chaosGraphics.mulS), (short) (py.get() * chaosGraphics.mulS), (short) (ChaosGraphics.BW * chaosGraphics.mulS), (short) (ChaosGraphics.BH * chaosGraphics.mulS));
        }
        fill.inc(1023);
        if (progress != dialogs.noGadget)
            dialogs.ModifyGadget(progress, (Memory.TagItem) memory.TAG1(Dialogs.dFILL, fill.get()));
        input.GetEvent(e);
        px.inc(ChaosGraphics.BW);
        if (px.get() >= 256) {
            px.set((short) 0);
            py.inc(ChaosGraphics.BH);
        }
    }

    public boolean InitImages() {
        // CONST
        final int BlockSize = 512;
        final int DstSize = 10368;
        final String DTitle = "Loading backgrounds";

        // VAR
        Dialogs.GadgetPtr progress = null;
        Runtime.Ref<short[]> block = new Runtime.Ref<>(null);
        Runtime.Ref<short[]> dest = new Runtime.Ref<>(null);
        Runtime.Ref<Short> bRead = new Runtime.Ref<>((short) 0);
        int nbcolors = 0;
        int c = 0;
        Runtime.Ref<Integer> pc = new Runtime.Ref<>(0);
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Integer> fill = new Runtime.Ref<>(0);
        int boost = 0;
        boolean oldWater = false;
        Object dTitle = null;

        oldWater = chaosBase.water;
        chaosBase.water = false;
        graphics.DeleteArea(new Runtime.FieldRef<>(chaosGraphics::getImage2Area, chaosGraphics::setImage2Area));
        graphics.DeleteArea(new Runtime.FieldRef<>(chaosGraphics::getImageArea, chaosGraphics::setImageArea));
        if (chaosGraphics.color) {
            if (chaosGraphics.dualpf)
                nbcolors = 256;
            else
                nbcolors = 16;
        } else {
            nbcolors = 2;
        }
        chaosGraphics.imageArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, chaosGraphics.W.invoke((short) 256), Graphics.aSIZEY, chaosGraphics.H.invoke((short) 256), Graphics.aCOLOR, nbcolors));
        if (chaosGraphics.imageArea == graphics.noArea)
            return false;
        if (chaosGraphics.dualpf) {
            chaosGraphics.image2Area = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, chaosGraphics.W.invoke((short) 256), Graphics.aSIZEY, chaosGraphics.H.invoke((short) 256), Graphics.aCOLOR, nbcolors));
            if (chaosGraphics.image2Area == graphics.noArea) {
                graphics.DeleteArea(new Runtime.FieldRef<>(chaosGraphics::getImageArea, chaosGraphics::setImageArea));
                return false;
            }
        }
        if (chaosGraphics.dualpf)
            block.set((short[]) memory.AllocMem(BlockSize * 2 * Runtime.sizeOf(2, short.class)));
        else
            block.set((short[]) memory.AllocMem(BlockSize * Runtime.sizeOf(2, short.class)));
        if (block.get() == null)
            return false;
        if (chaosGraphics.mulS > 1)
            dest.set((short[]) memory.AllocMem(chaosGraphics.mulS * chaosGraphics.mulS * 128 * Runtime.sizeOf(2, short.class)));
        else
            dest.set(block.get());
        if (dest.get() == null) {
            memory.FreeMem(block.asAdrRef());
            return false;
        }
        px.set((short) 0);
        py.set((short) 0);
        fill.set(63);
        chaosBase.file = files.OpenFile(Runtime.castToRef(memory.ADS(ImagesFile), String.class), EnumSet.of(AccessFlags.accessRead));
        checks.Check(chaosBase.file == files.noFile, Runtime.castToRef(languages.ADL(OpenIErrMsg), String.class), files.FileErrorMsg());
        dTitle = languages.ADL(DTitle);
        chaosBase.d = dialogs.CreateGadget((short) Dialogs.dDialog, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, dTitle, Dialogs.dRFLAGS, Dialogs.dfCLOSE));
        progress = dialogs.noGadget;
        if (chaosBase.d != dialogs.noGadget) {
            progress = dialogs.AddNewGadget(chaosBase.d, (short) Dialogs.dProgress, (Memory.TagItem) memory.TAG3(Dialogs.dRFLAGS, Dialogs.dfBORDER, Dialogs.dTEXT, memory.ADS(""), Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
            if ((progress == dialogs.noGadget) || (dialogs.RefreshGadget(chaosBase.d) != Dialogs.DialogOk))
                dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
        }
        for (c = 1; c <= ChaosGraphics.NbBackground; c++) {
            if ((c == 1) || (c == 22))
                boost = 8;
            else if (c == 16)
                boost = 5;
            if ((c == 1) || (c == 22) || (c == 24))
                SetMetalPalette();
            else if (c == 15)
                SetForestPalette();
            else if ((c == 18) || (c == 20))
                SetRGBIcePalette();
            else if ((c == 19) || (c == 21))
                SetAnimPalette();
            else if (c == 23)
                SetOrPalette();
            graphics.SetArea(chaosGraphics.imageArea);
            InitImages_DrawNextBlock(BlockSize, progress, block.get(), dest.get(), bRead, pc, px, py, fill, boost);
        }
        for (c = 1; c <= ChaosGraphics.NbWall; c++) {
            if ((c == 1) || (c == 32))
                boost = 8;
            else if (c == 25)
                boost = 12;
            if (c == 1)
                SetMetalPalette();
            else if (c == 14)
                SetFadePalette();
            else if (c == 17)
                SetOrPalette();
            else if (c == 25)
                SetForestPalette();
            else if (c == 36)
                SetAnimPalette();
            else if (c == 39)
                SetRGBIcePalette();
            graphics.SetArea(chaosGraphics.imageArea);
            InitImages_DrawNextBlock(BlockSize, progress, block.get(), dest.get(), bRead, pc, px, py, fill, boost);
        }
        SetMetalPalette();
        graphics.SetArea(chaosGraphics.shapeArea);
        graphics.SetPen(0);
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) 256), chaosGraphics.H.invoke((short) 256));
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
        if (dest.get() != block.get())
            memory.FreeMem(dest.asAdrRef());
        memory.FreeMem(block.asAdrRef());
        files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
        chaosBase.water = oldWater;
        InitPalette();
        return true;
    }

    private void RenderObjects_GetChar(/* var */ Runtime.IRef<Character> ch, Dialogs.GadgetPtr progress, /* VAR */ Runtime.IRef<Long> size, long total, /* VAR */ Runtime.IRef<Integer> bRead, /* VAR */ Runtime.IRef<Integer> tofill) {
        // VAR
        long fill = 0L;

        bRead.set(files.ReadFileBytes(chaosBase.file, ch, 1));
        if (bRead.get() != 1)
            checks.Check(true, Runtime.castToRef(languages.ADL(ReadOErrMsg), String.class), files.FileErrorMsg());
        size.dec();
        tofill.dec();
        if (tofill.get() == 0) {
            fill = ((total - size.get()) * 257 / total) * 255;
            if (progress != dialogs.noGadget)
                dialogs.ModifyGadget(progress, (Memory.TagItem) memory.TAG1(Dialogs.dFILL, fill));
            tofill.set(200);
        }
    }

    private void RenderObjects_GetVal(/* VAR */ Runtime.IRef<Short> val, Dialogs.GadgetPtr progress, /* VAR */ Runtime.IRef<Long> size, long total, /* VAR */ Runtime.IRef<Integer> bRead, /* VAR */ Runtime.IRef<Integer> tofill) {
        // VAR
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        RenderObjects_GetChar(ch, progress, size, total, bRead, tofill);
        val.set((short) (char) ch.get());
    }

    private void RenderObjects_GetCoords(/* VAR+WRT */ Runtime.IRef<Short> x, /* VAR+WRT */ Runtime.IRef<Short> y, Dialogs.GadgetPtr progress, /* VAR */ Runtime.IRef<Long> size, long total, /* VAR */ Runtime.IRef<Integer> bRead, /* VAR */ Runtime.IRef<Integer> tofill, boolean highx, boolean highy) {
        RenderObjects_GetVal(x, progress, size, total, bRead, tofill);
        RenderObjects_GetVal(y, progress, size, total, bRead, tofill);
        if (highx && (x.get() < 64))
            x.inc(256);
        if (highy && (y.get() < 64))
            y.inc(256);
    }

    private void RenderObjects_XFillRect(short sx, short sy, short ex, short ey, short lightColor, short darkColor, short bpen, short pat, EnumSet<Modes> cm, /* VAR */ Runtime.IRef<Boolean> triColor) {
        // VAR
        short i = 0;

        graphics.SetCopyMode(Graphics.cmCopy);
        for (i = 0; i < chaosGraphics.mulS; i++) {
            graphics.SetPat((short) ((i * 4 + chaosGraphics.mulS / 2) / chaosGraphics.mulS));
            graphics.SetBPen(lightColor);
            graphics.FillRect(sx, sy, ex, ey);
            sx++;
            sy++;
            graphics.SetBPen(darkColor);
            graphics.FillRect(sx, sy, ex, ey);
            ex--;
            ey--;
        }
        graphics.SetPat(pat);
        graphics.SetBPen(bpen);
        graphics.FillRect(sx, sy, ex, ey);
        triColor.set(false);
        graphics.SetCopyMode(cm);
    }

    private void RenderObjects_XFillEllipse(short sx, short sy, short ex, short ey, short lightColor, short darkColor, short bpen, short pat, EnumSet<Modes> cm, /* VAR */ Runtime.IRef<Boolean> triColor) {
        // VAR
        short i = 0;

        graphics.SetCopyMode(Graphics.cmCopy);
        for (i = 0; i < chaosGraphics.mulS; i++) {
            graphics.SetPat((short) ((i * 4 + chaosGraphics.mulS / 2) / chaosGraphics.mulS));
            graphics.SetBPen(lightColor);
            graphics.FillEllipse(sx, sy, ex, ey);
            sx++;
            sy++;
            graphics.SetBPen(darkColor);
            graphics.FillEllipse(sx, sy, ex, ey);
            ex--;
            ey--;
        }
        graphics.SetPat(pat);
        graphics.SetBPen(bpen);
        graphics.FillEllipse(sx, sy, ex, ey);
        triColor.set(false);
        graphics.SetCopyMode(cm);
    }

    private void RenderObjects_XFillPoly(short[] xvect, short[] yvect, boolean[] xdec, boolean[] ydec, /* VAR */ Runtime.IRef<Integer> cnt, short lightColor, short darkColor, short bpen, short pat, EnumSet<Modes> cm, /* VAR */ Runtime.IRef<Boolean> triColor) {
        // VAR
        short i = 0;
        int c = 0;

        graphics.SetArea(chaosGraphics.shapeArea);
        graphics.SetCopyMode(Graphics.cmCopy);
        for (i = 0; i < chaosGraphics.mulS; i++) {
            graphics.SetPat((short) ((i * 4 + chaosGraphics.mulS / 2) / chaosGraphics.mulS));
            graphics.SetBPen(lightColor);
            for (c = 0; c < cnt.get(); c++) {
                if (c > 0)
                    graphics.AddLine(xvect[c], yvect[c]);
                else
                    graphics.OpenPoly(xvect[0], yvect[0]);
                if (!xdec[c])
                    xvect[c]++;
                if (!ydec[c])
                    yvect[c]++;
            }
            graphics.FillPoly();
            graphics.SetBPen(darkColor);
            for (c = 0; c < cnt.get(); c++) {
                if (c > 0)
                    graphics.AddLine(xvect[c], yvect[c]);
                else
                    graphics.OpenPoly(xvect[0], yvect[0]);
                if (xdec[c])
                    xvect[c]--;
                if (ydec[c])
                    yvect[c]--;
            }
            graphics.FillPoly();
        }
        graphics.SetPat(pat);
        graphics.SetBPen(bpen);
        for (c = 0; c < cnt.get(); c++) {
            if (c > 0)
                graphics.AddLine(xvect[c], yvect[c]);
            else
                graphics.OpenPoly(xvect[0], yvect[0]);
        }
        graphics.FillPoly();
        cnt.set(0);
        triColor.set(false);
        graphics.SetCopyMode(cm);
    }

    public void RenderObjects() {
        // CONST
        final String DTitle = "Loading objects";

        // VAR
        Dialogs.GadgetPtr progress = null;
        Runtime.Ref<Long> size = new Runtime.Ref<>(0L);
        long total = 0L;
        Runtime.Ref<Integer> bRead = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> tofill = new Runtime.Ref<>(0);
        boolean highx = false;
        boolean highy = false;
        Object dTitle = null;
        short[] xvect = new short[20];
        short[] yvect = new short[20];
        boolean[] xdec = new boolean[20];
        boolean[] ydec = new boolean[20];
        Runtime.Ref<Integer> cnt = new Runtime.Ref<>(0);
        int c = 0;
        Runtime.Ref<Short> lightColor = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> darkColor = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> bpen = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> x1 = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y1 = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> x2 = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y2 = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> sz = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> w = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> col = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> p = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> pat = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> msk = new Runtime.Ref<>((short) 0);
        short sx = 0;
        short sy = 0;
        short ex = 0;
        short ey = 0;
        Runtime.Ref<String> str = new Runtime.Ref<>("");
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        EnumSet<Modes> cm = EnumSet.noneOf(Modes.class);
        Runtime.Ref<Boolean> triColor = new Runtime.Ref<>(false);

        cnt.set(0);
        triColor.set(false);
        pat.set((short) 4);
        bpen.set((short) 0);
        chaosGraphics.SetOrigin((short) 0, (short) 0);
        graphics.SetArea(chaosGraphics.maskArea);
        graphics.SetPen(1);
        graphics.SetArea(chaosGraphics.shapeArea);
        graphics.SetPen(1);
        chaosBase.file = files.OpenFile(Runtime.castToRef(memory.ADS(ObjectsFile), String.class), EnumSet.of(AccessFlags.accessRead));
        checks.Check(chaosBase.file == files.noFile, Runtime.castToRef(languages.ADL(OpenOErrMsg), String.class), files.FileErrorMsg());
        dTitle = languages.ADL(DTitle);
        chaosBase.d = dialogs.CreateGadget((short) Dialogs.dDialog, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, dTitle, Dialogs.dRFLAGS, Dialogs.dfCLOSE));
        progress = dialogs.noGadget;
        if (chaosBase.d != dialogs.noGadget) {
            progress = dialogs.AddNewGadget(chaosBase.d, (short) Dialogs.dProgress, (Memory.TagItem) memory.TAG3(Dialogs.dRFLAGS, Dialogs.dfBORDER, Dialogs.dTEXT, memory.ADS(""), Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
            if ((progress == dialogs.noGadget) || (dialogs.RefreshGadget(chaosBase.d) != Dialogs.DialogOk))
                dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
        }
        size.set((long) files.FileLength(chaosBase.file));
        tofill.set(200);
        total = size.get();
        while (size.get() > 0) {
            RenderObjects_GetChar(ch, progress, size, total, bRead, tofill);
            if (ch.get() >= ((char) 0200)) {
                highy = true;
                ch.dec(128);
            } else {
                highy = false;
            }
            if (ch.get() >= ((char) 0140)) {
                highx = true;
                ch.dec(32);
            } else {
                highx = false;
            }
            switch (ch.get()) {
                case 'C' -> {
                    RenderObjects_GetVal(col, progress, size, total, bRead, tofill);
                    RenderObjects_GetVal(p, progress, size, total, bRead, tofill);
                    RenderObjects_GetVal(msk, progress, size, total, bRead, tofill);
                    graphics.SetArea(chaosGraphics.maskArea);
                    graphics.SetPat(msk.get());
                    graphics.SetArea(chaosGraphics.shapeArea);
                    if (chaosGraphics.color)
                        graphics.SetPen(col.get());
                    else
                        graphics.SetPat(p.get());
                }
                case 'P' -> {
                    RenderObjects_GetVal(pat, progress, size, total, bRead, tofill);
                    RenderObjects_GetVal(bpen, progress, size, total, bRead, tofill);
                    graphics.SetArea(chaosGraphics.shapeArea);
                    if (chaosGraphics.color) {
                        graphics.SetPat(pat.get());
                        graphics.SetBPen(bpen.get());
                    }
                }
                case 'X' -> {
                    RenderObjects_GetVal(lightColor, progress, size, total, bRead, tofill);
                    RenderObjects_GetVal(darkColor, progress, size, total, bRead, tofill);
                    triColor.set(chaosGraphics.color);
                }
                case 'M' -> {
                    RenderObjects_GetVal(msk, progress, size, total, bRead, tofill);
                    if (msk.get() == 0)
                        cm = Graphics.cmCopy;
                    else if (msk.get() == 1)
                        cm = Graphics.cmTrans;
                    else
                        cm = Graphics.cmXor;
                    graphics.SetArea(chaosGraphics.maskArea);
                    graphics.SetCopyMode(cm);
                    graphics.SetArea(chaosGraphics.shapeArea);
                    graphics.SetCopyMode(cm);
                }
                case 'R' -> {
                    RenderObjects_GetCoords(x1, y1, progress, size, total, bRead, tofill, highx, highy);
                    RenderObjects_GetCoords(x2, y2, progress, size, total, bRead, tofill, highx, highy);
                    sx = chaosGraphics.X.invoke(x1.get());
                    sy = chaosGraphics.Y.invoke(y1.get());
                    ex = chaosGraphics.X.invoke(x2.get());
                    ey = chaosGraphics.Y.invoke(y2.get());
                    graphics.SetArea(chaosGraphics.maskArea);
                    graphics.FillRect(sx, sy, ex, ey);
                    graphics.SetArea(chaosGraphics.shapeArea);
                    if (triColor.get())
                        RenderObjects_XFillRect(sx, sy, ex, ey, lightColor.get(), darkColor.get(), bpen.get(), pat.get(), cm, triColor);
                    else
                        graphics.FillRect(sx, sy, ex, ey);
                }
                case 'E' -> {
                    RenderObjects_GetCoords(x1, y1, progress, size, total, bRead, tofill, highx, highy);
                    RenderObjects_GetCoords(x2, y2, progress, size, total, bRead, tofill, highx, highy);
                    sx = chaosGraphics.X.invoke(x1.get());
                    sy = chaosGraphics.Y.invoke(y1.get());
                    ex = chaosGraphics.X.invoke(x2.get());
                    ey = chaosGraphics.Y.invoke(y2.get());
                    graphics.SetArea(chaosGraphics.maskArea);
                    graphics.FillEllipse(sx, sy, ex, ey);
                    graphics.SetArea(chaosGraphics.shapeArea);
                    if (triColor.get())
                        RenderObjects_XFillEllipse(sx, sy, ex, ey, lightColor.get(), darkColor.get(), bpen.get(), pat.get(), cm, triColor);
                    else
                        graphics.FillEllipse(sx, sy, ex, ey);
                }
                case 'T' -> {
                    RenderObjects_GetCoords(x1, y1, progress, size, total, bRead, tofill, highx, highy);
                    RenderObjects_GetVal(w, progress, size, total, bRead, tofill);
                    RenderObjects_GetVal(sz, progress, size, total, bRead, tofill);
                    RenderObjects_GetVal(col, progress, size, total, bRead, tofill);
                    c = 0;
                    do {
                        RenderObjects_GetChar(ch, progress, size, total, bRead, tofill);
                        Runtime.setChar(str, c, ch.get());
                        c++;
                    } while (!((c > 4) || (ch.get() == '"')));
                    c--;
                    Runtime.setChar(str, c, ((char) 0));
                    graphics.SetArea(chaosGraphics.shapeArea);
                    graphics.SetTextSize(chaosGraphics.H.invoke(sz.get()));
                    x1.set((short) (chaosGraphics.X.invoke(x1.get()) + (chaosGraphics.W.invoke((short) (w.get() - 1)) - graphics.TextWidth(str)) / 2));
                    y1.set(chaosGraphics.Y.invoke(y1.get()));
                    graphics.SetTextPos(x1.get(), y1.get());
                    if (!chaosGraphics.color)
                        graphics.SetPen(col.get());
                    graphics.DrawText(str);
                    if (!chaosGraphics.color)
                        graphics.SetPen(1);
                    graphics.SetArea(chaosGraphics.maskArea);
                    graphics.SetTextSize(chaosGraphics.H.invoke(sz.get()));
                    graphics.SetTextPos(x1.get(), y1.get());
                    graphics.DrawText(str);
                }
                case 'L' -> {
                    RenderObjects_GetCoords(x1, y1, progress, size, total, bRead, tofill, highx, highy);
                    RenderObjects_GetVal(msk, progress, size, total, bRead, tofill);
                    x1.set(chaosGraphics.X.invoke(x1.get()));
                    if (((msk.get() % 2) != 0))
                        x1.dec();
                    y1.set(chaosGraphics.Y.invoke(y1.get()));
                    if (msk.get() >= 2)
                        y1.dec();
                    graphics.SetArea(chaosGraphics.maskArea);
                    if (cnt.get() > 0)
                        graphics.AddLine(x1.get(), y1.get());
                    else
                        graphics.OpenPoly(x1.get(), y1.get());
                    xdec[cnt.get()] = ((msk.get() % 2) != 0);
                    ydec[cnt.get()] = (msk.get() >= 2);
                    xvect[cnt.get()] = x1.get();
                    yvect[cnt.get()] = y1.get();
                    cnt.inc();
                }
                case 'F' -> {
                    graphics.SetArea(chaosGraphics.maskArea);
                    graphics.FillPoly();
                    if (triColor.get()) {
                        RenderObjects_XFillPoly(xvect, yvect, xdec, ydec, cnt, lightColor.get(), darkColor.get(), bpen.get(), pat.get(), cm, triColor);
                    } else {
                        graphics.SetArea(chaosGraphics.shapeArea);
                        for (c = 0; c < cnt.get(); c++) {
                            if (c > 0)
                                graphics.AddLine(xvect[c], yvect[c]);
                            else
                                graphics.OpenPoly(xvect[0], yvect[0]);
                        }
                        graphics.FillPoly();
                        cnt.set(0);
                    }
                }
                default -> {
                }
            }
        }
        files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
        graphics.SetArea(chaosGraphics.maskArea);
        graphics.SetPat((short) 4);
        graphics.SetBPen(0);
        graphics.SetPen(1);
        graphics.SetCopyMode(Graphics.cmXor);
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) 256), chaosGraphics.H.invoke((short) 256));
        graphics.SetArea(chaosGraphics.shapeArea);
        graphics.SetPat((short) 4);
        graphics.SetBPen(0);
        graphics.SetPen(0);
        graphics.SetCopyMode(Graphics.cmTrans);
        graphics.DrawShadow(chaosGraphics.maskArea, (short) 0, (short) 0, (short) 0, (short) 0, chaosGraphics.W.invoke((short) 256), chaosGraphics.H.invoke((short) 256));
        graphics.SetArea(chaosGraphics.maskArea);
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) 256), chaosGraphics.H.invoke((short) 256));
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
    }

    public void InitPalette() {
        // VAR
        int rnd = 0;

        if (chaosGraphics.color) {
            chaosGraphics.SetRGB((short) 0, (short) 0, (short) 0, (short) 0);
            chaosGraphics.SetRGB((short) 1, (short) 127, (short) 127, (short) 127);
        } else {
            chaosGraphics.SetRGB((short) 0, (short) 0, (short) 0, (short) 0);
            chaosGraphics.SetRGB((short) 1, (short) 255, (short) 255, (short) 255);
        }
        chaosGraphics.SetRGB((short) 2, (short) 255, (short) 255, (short) 255);
        chaosGraphics.CycleRGB((short) 2, (short) 40, (short) 127, (short) 255, (short) 255);
        chaosGraphics.SetRGB((short) 3, (short) 255, (short) 255, (short) 0);
        chaosGraphics.CycleRGB((short) 3, (short) 200, (short) 255, (short) 200, (short) 0);
        chaosGraphics.SetRGB((short) 4, (short) 255, (short) 0, (short) 0);
        chaosGraphics.SetRGB((short) 5, (short) 0, (short) 255, (short) 0);
        chaosGraphics.SetRGB((short) 6, (short) 0, (short) 0, (short) 255);
        chaosGraphics.SetRGB((short) 7, (short) 216, (short) 152, (short) 0);
        if (chaosBase.zone == Zone.Chaos) {
            SetMetalPalette();
        } else if (chaosBase.zone == Zone.Castle) {
            switch (chaosBase.level[Zone.Castle.ordinal()]) {
                case 1, 8, 13 -> SetRGBIcePalette();
                case 2 -> SetFadePalette();
                case 3, 4, 11, 14, 17, 18 -> SetForestPalette();
                case 5 -> SetAnimPalette();
                case 6, 10, 15, 19 -> SetMetalPalette();
                case 7 -> SetAnimatedPalette();
                case 9 -> SetFactoryPalette();
                case 12, 16 -> SetAnimatedPalette2();
                case 20 -> SetJunglePalette();
                default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.level[Zone.Castle.ordinal()]);
            }
            lastCastleLevel = chaosBase.level[Zone.Castle.ordinal()];
        } else if (chaosBase.zone == Zone.Family) {
            switch (chaosBase.level[Zone.Family.ordinal()]) {
                case 1, 2, 4, 6, 8, 9, 10 -> SetOrPalette();
                case 3 -> SetMetalPalette();
                case 5 -> SetBluePalette();
                case 7 -> SetFactoryPalette();
                default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.level[Zone.Family.ordinal()]);
            }
        } else {
            if (chaosBase.level[Zone.Special.ordinal()] == 24) {
                SetMetalPalette();
            } else if (chaosBase.level[Zone.Special.ordinal()] % 8 == 0) {
                SetWinterPalette();
            } else if (chaosBase.level[Zone.Special.ordinal()] % 4 == 0) {
                SetGraveyardPalette();
            } else if (chaosBase.level[Zone.Special.ordinal()] % 2 == 0) {
                SetBagleyPalette();
            } else {
                rnd = trigo.RND() % 3;
                if (rnd == 0)
                    SetAnimatedPalette();
                else if (rnd == 1)
                    SetAnimatedPalette2();
                else
                    SetAnimatedPalette3();
            }
        }
        chaosGraphics.UpdatePalette();
    }

    public void InitDualPalette() {
        chaosGraphics.SetRGB((short) 0, (short) 0, (short) 0, (short) 0);
        chaosGraphics.SetRGB((short) 1, (short) 127, (short) 127, (short) 127);
        chaosGraphics.SetRGB((short) 2, (short) 255, (short) 255, (short) 255);
        chaosGraphics.CycleRGB((short) 2, (short) 40, (short) 127, (short) 255, (short) 255);
        chaosGraphics.SetRGB((short) 3, (short) 255, (short) 255, (short) 0);
        chaosGraphics.CycleRGB((short) 3, (short) 200, (short) 255, (short) 200, (short) 0);
        chaosGraphics.SetRGB((short) 4, (short) 255, (short) 0, (short) 0);
        chaosGraphics.SetRGB((short) 5, (short) 0, (short) 255, (short) 0);
        chaosGraphics.SetRGB((short) 6, (short) 0, (short) 0, (short) 255);
        chaosGraphics.SetRGB((short) 7, (short) 216, (short) 152, (short) 0);
        if (chaosBase.zone == Zone.Chaos) {
            SetMetalPalette();
        } else if (chaosBase.zone == Zone.Castle) {
            switch (chaosBase.level[Zone.Castle.ordinal()]) {
                case 1, 4 -> {
                    SetForestPalette();
                }
                case 2, 3, 5, 7, 10, 14, 16, 17, 19 -> {
                    SetMetalPalette();
                }
                case 6 -> {
                    SetGhostPalette();
                }
                case 8 -> {
                    if (chaosBase.difficulty >= 7)
                        SetAnimatedPalette();
                    else
                        SetMetalPalette();
                }
                case 9 -> {
                    if (((chaosBase.difficulty % 2) != 0))
                        SetDarkFactoryPalette();
                    else
                        SetDarkPalette();
                }
                case 11 -> {
                    SetDarkPalette();
                }
                case 12 -> {
                    SetAnimatedPalette();
                }
                case 13 -> {
                    if (chaosBase.difficulty == 7)
                        SetAnimatedPalette2();
                    else
                        SetForestPalette();
                }
                case 15 -> {
                    if (((chaosBase.difficulty % 2) != 0))
                        SetForestPalette();
                    else
                        SetJunglePalette();
                }
                case 18 -> {
                    SetRGBIcePalette();
                }
                case 20 -> {
                    if (((chaosBase.difficulty % 2) != 0))
                        SetDarkPalette();
                    else
                        SetDarkFactoryPalette();
                }
                default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.level[Zone.Castle.ordinal()]);
            }
        } else if (chaosBase.zone == Zone.Family) {
            switch (chaosBase.level[Zone.Family.ordinal()]) {
                case 3, 7, 8, 10 -> SetMetalPalette();
                case 1, 2, 9 -> SetForestPalette();
                case 4 -> SetGraveyardPalette();
                case 5 -> SetBagleyPalette();
                case 6 -> SetDarkFactoryPalette();
                default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.level[Zone.Family.ordinal()]);
            }
        } else {
            if (chaosBase.level[Zone.Special.ordinal()] == 24)
                SetMetalPalette();
            else if (chaosBase.level[Zone.Special.ordinal()] % 8 == 0)
                SetFadePalette();
            else if (chaosBase.level[Zone.Special.ordinal()] % 4 == 0)
                SetMetalPalette();
            else if (chaosBase.level[Zone.Special.ordinal()] % 2 == 0)
                SetGraveyardPalette();
            else if (chaosBase.difficulty > 6)
                SetJunglePalette();
            else if (chaosBase.difficulty > 3)
                SetBagleyPalette();
            else
                SetForestPalette();
        }
    }


    // Support

    private static ChaosImages instance;

    public static ChaosImages instance() {
        if (instance == null)
            new ChaosImages(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        lastCastleLevel = 0;
    }

    public void close() {
    }

}
