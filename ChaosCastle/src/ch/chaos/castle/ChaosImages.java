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

    private int fadeMn1;
    private int fadeMn2;
    private int fadeMx1;
    private int fadeMx2;
    private int lastCastleLevel;


    public int getFadeMn1() {
        return this.fadeMn1;
    }

    public void setFadeMn1(int fadeMn1) {
        this.fadeMn1 = fadeMn1;
    }

    public int getFadeMn2() {
        return this.fadeMn2;
    }

    public void setFadeMn2(int fadeMn2) {
        this.fadeMn2 = fadeMn2;
    }

    public int getFadeMx1() {
        return this.fadeMx1;
    }

    public void setFadeMx1(int fadeMx1) {
        this.fadeMx1 = fadeMx1;
    }

    public int getFadeMx2() {
        return this.fadeMx2;
    }

    public void setFadeMx2(int fadeMx2) {
        this.fadeMx2 = fadeMx2;
    }

    public int getLastCastleLevel() {
        return this.lastCastleLevel;
    }

    public void setLastCastleLevel(int lastCastleLevel) {
        this.lastCastleLevel = lastCastleLevel;
    }


    // PROCEDURE

    private void SetMetalPalette() {
        chaosGraphics.SetRGB(8, 238, 87, 0);
        chaosGraphics.SetRGB(9, 35, 35, 35);
        chaosGraphics.SetRGB(10, 92, 92, 92);
        chaosGraphics.SetRGB(11, 153, 153, 153);
        chaosGraphics.SetRGB(12, 68, 102, 204);
        chaosGraphics.SetRGB(13, 221, 85, 85);
        chaosGraphics.SetRGB(14, 51, 221, 68);
        chaosGraphics.SetRGB(15, 238, 238, 238);
    }

    private void SetGhostPalette() {
        SetMetalPalette();
        chaosGraphics.CycleRGB(9, 3, 0, 0, 0);
        chaosGraphics.CycleRGB(10, 3, 0, 0, 0);
        chaosGraphics.CycleRGB(11, 3, 0, 0, 0);
    }

    private void SetOrPalette() {
        chaosGraphics.SetRGB(8, 255, 68, 0);
        chaosGraphics.SetRGB(9, 68, 21, 0);
        chaosGraphics.SetRGB(10, 153, 40, 0);
        chaosGraphics.SetRGB(11, 205, 80, 0);
        chaosGraphics.SetRGB(12, 238, 187, 0);
        chaosGraphics.SetRGB(13, 255, 153, 0);
        chaosGraphics.SetRGB(14, 255, 102, 0);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetBluePalette() {
        chaosGraphics.SetRGB(8, 0, 68, 255);
        chaosGraphics.SetRGB(9, 0, 34, 85);
        chaosGraphics.SetRGB(10, 0, 51, 170);
        chaosGraphics.SetRGB(11, 0, 85, 221);
        chaosGraphics.SetRGB(12, 0, 187, 238);
        chaosGraphics.SetRGB(13, 0, 153, 255);
        chaosGraphics.SetRGB(14, 0, 102, 255);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetForestPalette() {
        chaosGraphics.SetRGB(8, 6, 15, 0);
        chaosGraphics.SetRGB(9, 70, 34, 0);
        chaosGraphics.SetRGB(10, 137, 68, 0);
        chaosGraphics.SetRGB(11, 187, 136, 15);
        chaosGraphics.SetRGB(12, 0, 55, 0);
        chaosGraphics.SetRGB(13, 0, 110, 0);
        chaosGraphics.SetRGB(14, 75, 180, 0);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetBagleyPalette() {
        chaosGraphics.SetRGB(8, 15, 6, 0);
        chaosGraphics.SetRGB(9, 80, 34, 34);
        chaosGraphics.SetRGB(10, 140, 68, 35);
        chaosGraphics.SetRGB(11, 187, 136, 50);
        chaosGraphics.SetRGB(12, 85, 51, 0);
        chaosGraphics.SetRGB(13, 119, 85, 0);
        chaosGraphics.SetRGB(14, 187, 119, 0);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetGraveyardPalette() {
        chaosGraphics.SetRGB(8, 3, 3, 3);
        chaosGraphics.SetRGB(9, 68, 68, 68);
        chaosGraphics.SetRGB(10, 102, 102, 102);
        chaosGraphics.SetRGB(11, 136, 136, 136);
        chaosGraphics.SetRGB(12, 51, 51, 51);
        chaosGraphics.SetRGB(13, 119, 119, 119);
        chaosGraphics.SetRGB(14, 187, 187, 187);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetWinterPalette() {
        chaosGraphics.SetRGB(8, 68, 85, 102);
        chaosGraphics.SetRGB(9, 119, 68, 34);
        chaosGraphics.SetRGB(10, 204, 221, 238);
        chaosGraphics.SetRGB(11, 230, 238, 255);
        chaosGraphics.SetRGB(12, 35, 35, 35);
        chaosGraphics.SetRGB(13, 193, 210, 252);
        chaosGraphics.SetRGB(14, 170, 221, 255);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetJunglePalette() {
        SetForestPalette();
        chaosGraphics.SetRGB(14, 0, 68, 204);
    }

    private void DoFade(int fadeMin, int fadeMax, boolean cycle) {
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
                chaosGraphics.CycleRGB(c + 9, 2, r, g, b);
            else
                chaosGraphics.SetRGB(c + 9, r, g, b);
        }
    }

    private void SetFadePalette() {
        if (chaosBase.level[Zone.Castle.ordinal()] != lastCastleLevel) {
            fadeMn1 = trigo.RND() % 4;
            fadeMn2 = trigo.RND() % 4;
            fadeMx1 = trigo.RND() % 4;
            fadeMx2 = trigo.RND() % 4;
        }
        DoFade(fadeMn1, fadeMx1, false);
        if (chaosBase.difficulty >= 2)
            DoFade(fadeMn2, fadeMx2, true);
        chaosGraphics.SetRGB(8, 68, 68, 68);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetRGBIcePalette() {
        chaosGraphics.SetRGB(8, 51, 51, 85);
        chaosGraphics.SetRGB(9, 102, 102, 119);
        chaosGraphics.SetRGB(10, 153, 153, 187);
        chaosGraphics.SetRGB(11, 204, 204, 255);
        chaosGraphics.SetRGB(12, 0, 70, 105);
        chaosGraphics.SetRGB(13, 0, 90, 90);
        chaosGraphics.SetRGB(14, 0, 100, 115);
        chaosGraphics.SetRGB(15, 204, 255, 255);
    }

    private void SetAnimPalette() {
        chaosGraphics.SetRGB(8, 0, 255, 153);
        chaosGraphics.SetRGB(9, 51, 51, 51);
        chaosGraphics.SetRGB(10, 102, 102, 102);
        chaosGraphics.SetRGB(11, 153, 153, 153);
        chaosGraphics.SetRGB(12, 0, 255, 0);
        chaosGraphics.SetRGB(13, 204, 0, 0);
        chaosGraphics.SetRGB(14, 0, 0, 153);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetAnimatedPalette() {
        chaosGraphics.SetRGB(8, 0, 0, 255);
        chaosGraphics.CycleRGB(8, 4, 0, 255, 0);
        chaosGraphics.SetRGB(9, 51, 51, 51);
        chaosGraphics.SetRGB(10, 102, 102, 102);
        chaosGraphics.SetRGB(11, 153, 153, 153);
        chaosGraphics.SetRGB(12, 0, 0, 0);
        chaosGraphics.CycleRGB(12, 4, 0, 255, 0);
        chaosGraphics.SetRGB(13, 255, 0, 0);
        chaosGraphics.CycleRGB(13, 17, 0, 68, 0);
        chaosGraphics.SetRGB(14, 0, 0, 255);
        chaosGraphics.CycleRGB(14, 4, 0, 0, 0);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetAnimatedPalette2() {
        chaosGraphics.SetRGB(8, 255, 0, 0);
        chaosGraphics.CycleRGB(8, 6, 0, 255, 0);
        chaosGraphics.SetRGB(9, 51, 51, 51);
        chaosGraphics.SetRGB(10, 102, 102, 102);
        chaosGraphics.SetRGB(11, 153, 153, 153);
        chaosGraphics.SetRGB(12, 255, 0, 0);
        chaosGraphics.CycleRGB(12, 6, 0, 0, 0);
        chaosGraphics.SetRGB(13, 70, 70, 0);
        chaosGraphics.CycleRGB(13, 13, 0, 180, 180);
        chaosGraphics.SetRGB(14, 0, 0, 0);
        chaosGraphics.CycleRGB(14, 6, 0, 255, 0);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetAnimatedPalette3() {
        chaosGraphics.SetRGB(8, 180, 180, 180);
        chaosGraphics.CycleRGB(8, 12, 90, 90, 90);
        chaosGraphics.SetRGB(9, 51, 51, 51);
        chaosGraphics.SetRGB(10, 102, 102, 102);
        chaosGraphics.SetRGB(11, 153, 153, 153);
        chaosGraphics.SetRGB(12, 180, 180, 180);
        chaosGraphics.CycleRGB(12, 6, 0, 0, 0);
        chaosGraphics.SetRGB(13, 240, 240, 240);
        chaosGraphics.CycleRGB(13, 13, 0, 0, 0);
        chaosGraphics.SetRGB(14, 0, 0, 0);
        chaosGraphics.CycleRGB(14, 6, 180, 180, 180);
        chaosGraphics.SetRGB(15, 255, 255, 255);
    }

    private void SetFactoryPalette() {
        SetMetalPalette();
        chaosGraphics.CycleRGB(8, 13, 0, 0, 0);
        chaosGraphics.CycleRGB(12, 7, 0, 0, 0);
        chaosGraphics.CycleRGB(13, 24, 0, 0, 0);
        chaosGraphics.CycleRGB(14, 19, 0, 0, 0);
    }

    private void SetDarkPalette() {
        chaosGraphics.SetRGB(8, 119, 44, 0);
        chaosGraphics.SetRGB(9, 18, 18, 18);
        chaosGraphics.SetRGB(10, 46, 46, 46);
        chaosGraphics.SetRGB(11, 77, 77, 77);
        chaosGraphics.SetRGB(12, 34, 51, 102);
        chaosGraphics.SetRGB(13, 111, 43, 43);
        chaosGraphics.SetRGB(14, 26, 111, 34);
        chaosGraphics.SetRGB(15, 119, 119, 119);
    }

    private void SetDarkFactoryPalette() {
        SetDarkPalette();
        chaosGraphics.CycleRGB(8, 13, 0, 0, 0);
        chaosGraphics.CycleRGB(12, 7, 0, 0, 0);
        chaosGraphics.CycleRGB(13, 24, 0, 0, 0);
        chaosGraphics.CycleRGB(14, 19, 0, 0, 0);
    }

    private void InitImages_DrawNextBlock(int BlockSize, Dialogs.GadgetPtr progress, int[] block, int[] dest, /* VAR */ Runtime.IRef<Integer> bRead, /* VAR */ Runtime.IRef<Integer> pc, /* VAR */ Runtime.IRef<Integer> px, /* VAR */ Runtime.IRef<Integer> py, /* VAR */ Runtime.IRef<Integer> fill, int boost) {
        // VAR
        Graphics.Image image = new Graphics.Image();
        Input.Event e = new Input.Event(); /* WRT */
        long p = 0L;
        int x = 0;
        int y = 0;
        int z = 0;
        int dx = 0;
        int dy = 0;
        int v = 0;
        int patCnt = 0;
        int val = 0;
        int pix = 0;
        int add = 0;

        /* BW * BH DIV 2 */
        if (chaosGraphics.color) {
            bRead.set((int) files.ReadFileBytes(chaosBase.file, block, BlockSize));
            if (bRead.get() != BlockSize)
                checks.Check(true, Runtime.castToRef(languages.ADL(ReadIErrMsg), String.class), files.FileErrorMsg());
            if (chaosGraphics.dualpf) {
                /* 4bit -> 8 bit */
                for (z = BlockSize - 1; z >= 0; z -= 1) {
                    v = block[z];
                    block[z * 2 + 1] = v % 16;
                    block[z * 2] = v / 16;
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
                graphics.DrawImage(image, 0, 0, 0, 0, ChaosGraphics.BW, ChaosGraphics.BH);
                graphics.SetArea(chaosGraphics.imageArea);
                graphics.ScaleRect(chaosGraphics.shapeArea, 0, 0, ChaosGraphics.BW, ChaosGraphics.BH, chaosGraphics.W.invoke(px.get()), chaosGraphics.H.invoke(py.get()), chaosGraphics.W.invoke(px.get() + ChaosGraphics.BW), chaosGraphics.H.invoke(py.get() + ChaosGraphics.BH));
            } else {
                graphics.SetArea(chaosGraphics.imageArea);
                graphics.DrawImage(image, 0, 0, px.get(), py.get(), 32, 32);
            }
            if (chaosGraphics.dualpf) {
                for (z = 0; z < BlockSize * 2; z++) {
                    block[z] = block[z] * 16;
                }
                if (chaosGraphics.mulS > 1) {
                    graphics.SetArea(chaosGraphics.shapeArea);
                    graphics.DrawImage(image, 0, 0, 0, 0, ChaosGraphics.BW, ChaosGraphics.BH);
                    graphics.SetArea(chaosGraphics.image2Area);
                    graphics.ScaleRect(chaosGraphics.shapeArea, 0, 0, ChaosGraphics.BW, ChaosGraphics.BH, chaosGraphics.W.invoke(px.get()), chaosGraphics.H.invoke(py.get()), chaosGraphics.W.invoke(px.get() + ChaosGraphics.BW), chaosGraphics.H.invoke(py.get() + ChaosGraphics.BH));
                } else {
                    graphics.SetArea(chaosGraphics.image2Area);
                    graphics.DrawImage(image, 0, 0, px.get(), py.get(), 32, 32);
                }
            }
        } else {
            graphics.SetPen(1);
            patCnt = 256;
            p = 0;
            bRead.set((int) files.ReadFileBytes(chaosBase.file, block, BlockSize));
            if (bRead.get() != BlockSize)
                checks.Check(true, Runtime.castToRef(languages.ADL(ReadIErrMsg), String.class), files.FileErrorMsg());
            for (y = 0; y < ChaosGraphics.BH; y++) {
                for (dy = 0; dy < chaosGraphics.mulS; dy++) {
                    z = y * ChaosGraphics.BW;
                    for (x = 0; x < ChaosGraphics.BW; x++) {
                        pix = memory.GetBitField(new Runtime.ArrayElementRef<>(block, z / 2), (z % 2) * 4, 4);
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
                            pc.set((int) (p / 8));
                            memory.SetBitField(new Runtime.ArrayElementRef<>(dest, pc.get()), (p % 8), 1, val);
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
            image.width = ChaosGraphics.BW * chaosGraphics.mulS;
            image.height = ChaosGraphics.BH * chaosGraphics.mulS;
            image.zw = 1;
            image.zh = 1;
            graphics.DrawImage(image, 0, 0, px.get() * chaosGraphics.mulS, py.get() * chaosGraphics.mulS, ChaosGraphics.BW * chaosGraphics.mulS, ChaosGraphics.BH * chaosGraphics.mulS);
        }
        fill.inc(1023);
        if (progress != dialogs.noGadget)
            dialogs.ModifyGadget(progress, (Memory.TagItem) memory.TAG1(Dialogs.dFILL, fill.get()));
        input.GetEvent(e);
        px.inc(ChaosGraphics.BW);
        if (px.get() >= 256) {
            px.set(0);
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
        Runtime.Ref<int[]> block = new Runtime.Ref<>(null);
        Runtime.Ref<int[]> dest = new Runtime.Ref<>(null);
        Runtime.Ref<Integer> bRead = new Runtime.Ref<>(0);
        int nbcolors = 0;
        int c = 0;
        Runtime.Ref<Integer> pc = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
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
        chaosGraphics.imageArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, chaosGraphics.W.invoke(256), Graphics.aSIZEY, chaosGraphics.H.invoke(256), Graphics.aCOLOR, nbcolors));
        if (chaosGraphics.imageArea == graphics.noArea)
            return false;
        if (chaosGraphics.dualpf) {
            chaosGraphics.image2Area = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, chaosGraphics.W.invoke(256), Graphics.aSIZEY, chaosGraphics.H.invoke(256), Graphics.aCOLOR, nbcolors));
            if (chaosGraphics.image2Area == graphics.noArea) {
                graphics.DeleteArea(new Runtime.FieldRef<>(chaosGraphics::getImageArea, chaosGraphics::setImageArea));
                return false;
            }
        }
        if (chaosGraphics.dualpf)
            block.set((int[]) memory.AllocMem(BlockSize * 2 * Runtime.sizeOf(4, int.class)));
        else
            block.set((int[]) memory.AllocMem(BlockSize * Runtime.sizeOf(4, int.class)));
        if (block.get() == null)
            return false;
        if (chaosGraphics.mulS > 1)
            dest.set((int[]) memory.AllocMem(chaosGraphics.mulS * chaosGraphics.mulS * 128 * Runtime.sizeOf(4, int.class)));
        else
            dest.set(block.get());
        if (dest.get() == null) {
            memory.FreeMem(block.asAdrRef());
            return false;
        }
        px.set(0);
        py.set(0);
        fill.set(63);
        chaosBase.file = files.OpenFile(Runtime.castToRef(memory.ADS(ImagesFile), String.class), EnumSet.of(AccessFlags.accessRead));
        checks.Check(chaosBase.file == files.noFile, Runtime.castToRef(languages.ADL(OpenIErrMsg), String.class), files.FileErrorMsg());
        dTitle = languages.ADL(DTitle);
        chaosBase.d = dialogs.CreateGadget(Dialogs.dDialog, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, dTitle, Dialogs.dRFLAGS, Dialogs.dfCLOSE));
        progress = dialogs.noGadget;
        if (chaosBase.d != dialogs.noGadget) {
            progress = dialogs.AddNewGadget(chaosBase.d, Dialogs.dProgress, (Memory.TagItem) memory.TAG3(Dialogs.dRFLAGS, Dialogs.dfBORDER, Dialogs.dTEXT, memory.ADS(""), Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
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
        graphics.FillRect(0, 0, chaosGraphics.W.invoke(256), chaosGraphics.H.invoke(256));
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
        if (dest.get() != block.get())
            memory.FreeMem(dest.asAdrRef());
        memory.FreeMem(block.asAdrRef());
        files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
        /*  SetArea(mainArea); CopyRect(imageArea, 0, 0, 0, H(-16), W(256), H(256)); */
        chaosBase.water = oldWater;
        InitPalette();
        return true;
    }

    private void RenderObjects_GetChar(/* var */ Runtime.IRef<Character> ch, Dialogs.GadgetPtr progress, /* VAR */ Runtime.IRef<Long> size, long total, /* VAR */ Runtime.IRef<Long> bRead, /* VAR */ Runtime.IRef<Integer> tofill) {
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

    private void RenderObjects_GetVal(/* VAR */ Runtime.IRef<Integer> val, Dialogs.GadgetPtr progress, /* VAR */ Runtime.IRef<Long> size, long total, /* VAR */ Runtime.IRef<Long> bRead, /* VAR */ Runtime.IRef<Integer> tofill) {
        // VAR
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        RenderObjects_GetChar(ch, progress, size, total, bRead, tofill);
        val.set((int) ch.get());
    }

    private void RenderObjects_GetCoords(/* VAR+WRT */ Runtime.IRef<Integer> x, /* VAR+WRT */ Runtime.IRef<Integer> y, Dialogs.GadgetPtr progress, /* VAR */ Runtime.IRef<Long> size, long total, /* VAR */ Runtime.IRef<Long> bRead, /* VAR */ Runtime.IRef<Integer> tofill, boolean highx, boolean highy) {
        RenderObjects_GetVal(x, progress, size, total, bRead, tofill);
        RenderObjects_GetVal(y, progress, size, total, bRead, tofill);
        if (highx && (x.get() < 64))
            x.inc(256);
        if (highy && (y.get() < 64))
            y.inc(256);
    }

    private void RenderObjects_XFillRect(int sx, int sy, int ex, int ey, int lightColor, int darkColor, int bpen, int pat, EnumSet<Modes> cm, /* VAR */ Runtime.IRef<Boolean> triColor) {
        // VAR
        int i = 0;

        graphics.SetCopyMode(Graphics.cmCopy);
        for (i = 0; i < chaosGraphics.mulS; i++) {
            graphics.SetPat((i * 4 + chaosGraphics.mulS / 2) / chaosGraphics.mulS);
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

    private void RenderObjects_XFillEllipse(int sx, int sy, int ex, int ey, int lightColor, int darkColor, int bpen, int pat, EnumSet<Modes> cm, /* VAR */ Runtime.IRef<Boolean> triColor) {
        // VAR
        int i = 0;

        graphics.SetCopyMode(Graphics.cmCopy);
        for (i = 0; i < chaosGraphics.mulS; i++) {
            graphics.SetPat((i * 4 + chaosGraphics.mulS / 2) / chaosGraphics.mulS);
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

    private void RenderObjects_XFillPoly(int[] xvect, int[] yvect, boolean[] xdec, boolean[] ydec, /* VAR */ Runtime.IRef<Integer> cnt, int lightColor, int darkColor, int bpen, int pat, EnumSet<Modes> cm, /* VAR */ Runtime.IRef<Boolean> triColor) {
        // VAR
        int i = 0;
        int c = 0;

        graphics.SetArea(chaosGraphics.shapeArea);
        graphics.SetCopyMode(Graphics.cmCopy);
        for (i = 0; i < chaosGraphics.mulS; i++) {
            graphics.SetPat((i * 4 + chaosGraphics.mulS / 2) / chaosGraphics.mulS);
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
        Runtime.Ref<Long> bRead = new Runtime.Ref<>(0L);
        Runtime.Ref<Integer> tofill = new Runtime.Ref<>(0);
        boolean highx = false;
        boolean highy = false;
        Object dTitle = null;
        int[] xvect = new int[20];
        int[] yvect = new int[20];
        boolean[] xdec = new boolean[20];
        boolean[] ydec = new boolean[20];
        Runtime.Ref<Integer> cnt = new Runtime.Ref<>(0);
        int c = 0;
        Runtime.Ref<Integer> lightColor = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> darkColor = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> bpen = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> x1 = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y1 = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> x2 = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y2 = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> sz = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> w = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> col = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> p = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> pat = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> msk = new Runtime.Ref<>(0);
        int sx = 0;
        int sy = 0;
        int ex = 0;
        int ey = 0;
        Runtime.Ref<String> str = new Runtime.Ref<>("");
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        EnumSet<Modes> cm = EnumSet.noneOf(Modes.class);
        Runtime.Ref<Boolean> triColor = new Runtime.Ref<>(false);

        cnt.set(0);
        triColor.set(false);
        pat.set(4);
        bpen.set(0);
        chaosGraphics.SetOrigin(0, 0);
        graphics.SetArea(chaosGraphics.maskArea);
        graphics.SetPen(1);
        graphics.SetArea(chaosGraphics.shapeArea);
        graphics.SetPen(1);
        chaosBase.file = files.OpenFile(Runtime.castToRef(memory.ADS(ObjectsFile), String.class), EnumSet.of(AccessFlags.accessRead));
        checks.Check(chaosBase.file == files.noFile, Runtime.castToRef(languages.ADL(OpenOErrMsg), String.class), files.FileErrorMsg());
        dTitle = languages.ADL(DTitle);
        chaosBase.d = dialogs.CreateGadget(Dialogs.dDialog, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, dTitle, Dialogs.dRFLAGS, Dialogs.dfCLOSE));
        progress = dialogs.noGadget;
        if (chaosBase.d != dialogs.noGadget) {
            progress = dialogs.AddNewGadget(chaosBase.d, Dialogs.dProgress, (Memory.TagItem) memory.TAG3(Dialogs.dRFLAGS, Dialogs.dfBORDER, Dialogs.dTEXT, memory.ADS(""), Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
            if ((progress == dialogs.noGadget) || (dialogs.RefreshGadget(chaosBase.d) != Dialogs.DialogOk))
                dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
        }
        size.set(files.FileLength(chaosBase.file));
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
                    x1.set(chaosGraphics.X.invoke(x1.get()) + (chaosGraphics.W.invoke(w.get() - 1) - graphics.TextWidth(str)) / 2);
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
        graphics.SetPat(4);
        graphics.SetBPen(0);
        graphics.SetPen(1);
        graphics.SetCopyMode(Graphics.cmXor);
        graphics.FillRect(0, 0, chaosGraphics.W.invoke(256), chaosGraphics.H.invoke(256));
        graphics.SetArea(chaosGraphics.shapeArea);
        graphics.SetPat(4);
        graphics.SetBPen(0);
        graphics.SetPen(0);
        graphics.SetCopyMode(Graphics.cmTrans);
        graphics.DrawShadow(chaosGraphics.maskArea, 0, 0, 0, 0, chaosGraphics.W.invoke(256), chaosGraphics.H.invoke(256));
        graphics.SetArea(chaosGraphics.maskArea);
        graphics.FillRect(0, 0, chaosGraphics.W.invoke(256), chaosGraphics.H.invoke(256));
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
    }

    public void InitPalette() {
        // VAR
        int rnd = 0;

        if (chaosGraphics.color) {
            chaosGraphics.SetRGB(0, 0, 0, 0);
            chaosGraphics.SetRGB(1, 127, 127, 127);
        } else {
            chaosGraphics.SetRGB(0, 0, 0, 0);
            chaosGraphics.SetRGB(1, 255, 255, 255);
        }
        chaosGraphics.SetRGB(2, 255, 255, 255);
        chaosGraphics.CycleRGB(2, 40, 127, 255, 255);
        chaosGraphics.SetRGB(3, 255, 255, 0);
        chaosGraphics.CycleRGB(3, 200, 255, 200, 0);
        chaosGraphics.SetRGB(4, 255, 0, 0);
        chaosGraphics.SetRGB(5, 0, 255, 0);
        chaosGraphics.SetRGB(6, 0, 0, 255);
        chaosGraphics.SetRGB(7, 216, 152, 0);
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
        chaosGraphics.SetRGB(0, 0, 0, 0);
        chaosGraphics.SetRGB(1, 127, 127, 127);
        chaosGraphics.SetRGB(2, 255, 255, 255);
        chaosGraphics.CycleRGB(2, 40, 127, 255, 255);
        chaosGraphics.SetRGB(3, 255, 255, 0);
        chaosGraphics.CycleRGB(3, 200, 255, 200, 0);
        chaosGraphics.SetRGB(4, 255, 0, 0);
        chaosGraphics.SetRGB(5, 0, 255, 0);
        chaosGraphics.SetRGB(6, 0, 0, 255);
        chaosGraphics.SetRGB(7, 216, 152, 0);
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
