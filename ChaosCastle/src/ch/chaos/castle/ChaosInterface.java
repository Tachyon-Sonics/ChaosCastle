package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.GameStat;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.castle.ChaosGraphics.Explosions;
import ch.chaos.library.Checks;
import ch.chaos.library.Dialogs;
import ch.chaos.library.Files;
import ch.chaos.library.Files.AccessFlags;
import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.GraphicsErr;
import ch.chaos.library.Input;
import ch.chaos.library.Input.SysMsg;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Menus;
import ch.chaos.library.Registration;
import ch.chaos.library.Sounds;
import ch.pitchtech.modula.runtime.Runtime;
import java.util.EnumSet;


public class ChaosInterface {

    // Imports
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final ChaosImages chaosImages;
    private final ChaosSounds chaosSounds;
    private final Checks checks;
    private final Dialogs dialogs;
    private final Files files;
    private final Graphics graphics;
    private final Input input;
    private final Languages languages;
    private final Memory memory;
    private final Menus menus;
    private final Registration registration;
    private final Sounds sounds;


    private ChaosInterface() {
        instance = this; // Set early to handle circular dependencies
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosImages = ChaosImages.instance();
        chaosSounds = ChaosSounds.instance();
        checks = Checks.instance();
        dialogs = Dialogs.instance();
        files = Files.instance();
        graphics = Graphics.instance();
        input = Input.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        menus = Menus.instance();
        registration = Registration.instance();
        sounds = Sounds.instance();
    }


    // TYPE

    public static class TopScore { // RECORD

        public String name = "";
        public long score;
        public long seed;
        public short[] endLevel = new short[Zone.Family.ordinal() - Zone.Chaos.ordinal() + 1];
        public Zone endZone;
        public short endDifficulty;


        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getScore() {
            return this.score;
        }

        public void setScore(long score) {
            this.score = score;
        }

        public long getSeed() {
            return this.seed;
        }

        public void setSeed(long seed) {
            this.seed = seed;
        }

        public short[] getEndLevel() {
            return this.endLevel;
        }

        public void setEndLevel(short[] endLevel) {
            this.endLevel = endLevel;
        }

        public Zone getEndZone() {
            return this.endZone;
        }

        public void setEndZone(Zone endZone) {
            this.endZone = endZone;
        }

        public short getEndDifficulty() {
            return this.endDifficulty;
        }

        public void setEndDifficulty(short endDifficulty) {
            this.endDifficulty = endDifficulty;
        }


        public void copyFrom(TopScore other) {
            this.name = other.name;
            this.score = other.score;
            this.seed = other.seed;
            this.endLevel = Runtime.copyOf(true, other.endLevel);
            this.endZone = other.endZone;
            this.endDifficulty = other.endDifficulty;
        }

        public TopScore newCopy() {
            TopScore copy = new TopScore();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    public Runnable Refresh;


    public Runnable getRefresh() {
        return this.Refresh;
    }

    public void setRefresh(Runnable Refresh) {
        this.Refresh = Refresh;
    }


    // CONST

    private static final String ConfigFileName = "Config";
    private static final String TopScoreFileName = "TopScores";
    private static final int ID = 1128482669;


    // VAR

    private short dfltLanguage;
    private boolean dfltLang;
    private Menus.MenuPtr fileMenu;
    private Menus.MenuPtr settingsMenu;
    private Menus.MenuPtr newMenu;
    private Menus.MenuPtr loadMenu;
    private Menus.MenuPtr saveMenu;
    private Menus.MenuPtr bar1Menu;
    private Menus.MenuPtr hideMenu;
    private Menus.MenuPtr bar2Menu;
    private Menus.MenuPtr quitMenu;
    private Menus.MenuPtr graphicsMenu;
    private Menus.MenuPtr soundsMenu;
    private Menus.MenuPtr languageMenu;
    private Menus.MenuPtr miscMenu;
    private boolean fmEnabled;
    private String lsFile = "";


    public short getDfltLanguage() {
        return this.dfltLanguage;
    }

    public void setDfltLanguage(short dfltLanguage) {
        this.dfltLanguage = dfltLanguage;
    }

    public boolean isDfltLang() {
        return this.dfltLang;
    }

    public void setDfltLang(boolean dfltLang) {
        this.dfltLang = dfltLang;
    }

    public Menus.MenuPtr getFileMenu() {
        return this.fileMenu;
    }

    public void setFileMenu(Menus.MenuPtr fileMenu) {
        this.fileMenu = fileMenu;
    }

    public Menus.MenuPtr getSettingsMenu() {
        return this.settingsMenu;
    }

    public void setSettingsMenu(Menus.MenuPtr settingsMenu) {
        this.settingsMenu = settingsMenu;
    }

    public Menus.MenuPtr getNewMenu() {
        return this.newMenu;
    }

    public void setNewMenu(Menus.MenuPtr newMenu) {
        this.newMenu = newMenu;
    }

    public Menus.MenuPtr getLoadMenu() {
        return this.loadMenu;
    }

    public void setLoadMenu(Menus.MenuPtr loadMenu) {
        this.loadMenu = loadMenu;
    }

    public Menus.MenuPtr getSaveMenu() {
        return this.saveMenu;
    }

    public void setSaveMenu(Menus.MenuPtr saveMenu) {
        this.saveMenu = saveMenu;
    }

    public Menus.MenuPtr getBar1Menu() {
        return this.bar1Menu;
    }

    public void setBar1Menu(Menus.MenuPtr bar1Menu) {
        this.bar1Menu = bar1Menu;
    }

    public Menus.MenuPtr getHideMenu() {
        return this.hideMenu;
    }

    public void setHideMenu(Menus.MenuPtr hideMenu) {
        this.hideMenu = hideMenu;
    }

    public Menus.MenuPtr getBar2Menu() {
        return this.bar2Menu;
    }

    public void setBar2Menu(Menus.MenuPtr bar2Menu) {
        this.bar2Menu = bar2Menu;
    }

    public Menus.MenuPtr getQuitMenu() {
        return this.quitMenu;
    }

    public void setQuitMenu(Menus.MenuPtr quitMenu) {
        this.quitMenu = quitMenu;
    }

    public Menus.MenuPtr getGraphicsMenu() {
        return this.graphicsMenu;
    }

    public void setGraphicsMenu(Menus.MenuPtr graphicsMenu) {
        this.graphicsMenu = graphicsMenu;
    }

    public Menus.MenuPtr getSoundsMenu() {
        return this.soundsMenu;
    }

    public void setSoundsMenu(Menus.MenuPtr soundsMenu) {
        this.soundsMenu = soundsMenu;
    }

    public Menus.MenuPtr getLanguageMenu() {
        return this.languageMenu;
    }

    public void setLanguageMenu(Menus.MenuPtr languageMenu) {
        this.languageMenu = languageMenu;
    }

    public Menus.MenuPtr getMiscMenu() {
        return this.miscMenu;
    }

    public void setMiscMenu(Menus.MenuPtr miscMenu) {
        this.miscMenu = miscMenu;
    }

    public boolean isFmEnabled() {
        return this.fmEnabled;
    }

    public void setFmEnabled(boolean fmEnabled) {
        this.fmEnabled = fmEnabled;
    }

    public String getLsFile() {
        return this.lsFile;
    }

    public void setLsFile(String lsFile) {
        this.lsFile = lsFile;
    }


    // PROCEDURE

    public void ReadTopScoreList(/* var */ TopScore[] topScores) {
        // VAR
        int res = 0;
        int c = 0;
        int d = 0;
        Zone z = Zone.Chaos;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        for (c = 1; c <= 10; c++) {
            { // WITH
                TopScore _topScore = topScores[c - 1];
                for (d = 0; d <= 19; d++) {
                    _topScore.name = Runtime.setChar(_topScore.name, d, '.');
                }
                _topScore.score = 0;
                _topScore.seed = 0;
                for (int _z = Zone.Chaos.ordinal(); _z <= Zone.Family.ordinal(); _z++) {
                    z = Zone.values()[_z];
                    _topScore.endLevel[z.ordinal()] = 1;
                }
                _topScore.endZone = Zone.Chaos;
                _topScore.endDifficulty = 1;
            }
        }
        chaosBase.file = files.OpenFile(Runtime.castToRef(memory.ADS(TopScoreFileName), String.class), EnumSet.of(AccessFlags.accessRead));
        if (chaosBase.file != files.noFile) {
            for (c = 1; c <= 10; c++) {
                { // WITH
                    TopScore _topScore = topScores[c - 1];
                    res = files.ReadFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getName, _topScore::setName), Runtime.sizeOf(20, String.class));
                    res = files.ReadFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getScore, _topScore::setScore), Runtime.sizeOf(8, long.class));
                    res = files.ReadFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getSeed, _topScore::setSeed), Runtime.sizeOf(8, long.class));
                    for (int _z = Zone.Chaos.ordinal(); _z <= Zone.Family.ordinal(); _z++) {
                        z = Zone.values()[_z];
                        res = files.ReadFileBytes(chaosBase.file, ch, 1);
                        _topScore.endLevel[z.ordinal()] = (short) (char) ch.get();
                    }
                    res = files.ReadFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getEndZone, _topScore::setEndZone), Runtime.sizeOf(1, Zone.class));
                    res = files.ReadFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getEndDifficulty, _topScore::setEndDifficulty), Runtime.sizeOf(2, short.class));
                    if (res <= 0) {
                        files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
                        return;
                    }
                }
            }
            files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
        }
    }

    public void WriteTopScoreList(TopScore[] topScores) {
        // VAR
        int res = 0;
        int c = 0;
        Zone z = Zone.Chaos;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        chaosBase.file = files.OpenFile(Runtime.castToRef(memory.ADS(TopScoreFileName), String.class), EnumSet.of(AccessFlags.accessWrite));
        if (chaosBase.file != files.noFile) {
            for (c = 1; c <= 10; c++) {
                { // WITH
                    TopScore _topScore = topScores[c - 1];
                    res = files.WriteFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getName, _topScore::setName), Runtime.sizeOf(20, String.class));
                    res = files.WriteFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getScore, _topScore::setScore), Runtime.sizeOf(8, long.class));
                    res = files.WriteFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getSeed, _topScore::setSeed), Runtime.sizeOf(8, long.class));
                    for (int _z = Zone.Chaos.ordinal(); _z <= Zone.Family.ordinal(); _z++) {
                        z = Zone.values()[_z];
                        ch.set((char) _topScore.endLevel[z.ordinal()]);
                        res = files.WriteFileBytes(chaosBase.file, ch, 1);
                    }
                    res = files.WriteFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getEndZone, _topScore::setEndZone), Runtime.sizeOf(1, Zone.class));
                    res = files.WriteFileBytes(chaosBase.file, new Runtime.FieldRef<>(_topScore::getEndDifficulty, _topScore::setEndDifficulty), Runtime.sizeOf(2, short.class));
                    if (res <= 0) {
                        checks.Warn(true, Runtime.castToRef(languages.ADL("Error writing file 'TopScores':"), String.class), files.FileErrorMsg());
                        files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
                        return;
                    }
                }
            }
            files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
        } else {
            checks.Warn(true, Runtime.castToRef(languages.ADL("Error creating file 'TopScores':"), String.class), files.FileErrorMsg());
        }
    }

    private void DefaultSound() {
        // VAR
        Memory.TagItem what = new Memory.TagItem(); /* WRT */

        chaosSounds.dfltSound = true;
        chaosSounds.music = false;
        chaosSounds.musicPri = 0;
        what.tag = Sounds.sSTEREO;
        sounds.GetSoundsSysAttr(what);
        chaosSounds.stereo = (what.data != 0);
        what.tag = Sounds.sNUMCHANS;
        sounds.GetSoundsSysAttr(what);
        if (what.data < 16)
            chaosSounds.nbChannel = (int) what.data;
        else
            chaosSounds.nbChannel = 16;
        chaosSounds.sound = (chaosSounds.nbChannel >= 1);
    }

    private void DefaultGraphic() {
        // VAR
        Memory.TagItem what = new Memory.TagItem(); /* WRT */
        short sizex = 0;
        short sizey = 0;

        chaosGraphics.dfltGraphic = true;
        what.tag = Graphics.aSIZEX;
        graphics.GetGraphicsSysAttr(what);
        sizex = (short) what.data;
        what.tag = Graphics.aSIZEY;
        graphics.GetGraphicsSysAttr(what);
        sizey = (short) what.data;
        if ((sizex >= 640) && (sizey >= 480))
            chaosGraphics.mulS = 2;
        else
            chaosGraphics.mulS = 1;
        what.tag = Graphics.aCOLOR;
        graphics.GetGraphicsSysAttr(what);
        chaosGraphics.color = what.data >= 16;
        chaosGraphics.dualpf = what.data >= 256;
        if (chaosGraphics.color || (chaosGraphics.mulS > 1))
            chaosGraphics.explosions = Explosions.Medium;
        else
            chaosGraphics.explosions = Explosions.Low;
        if (chaosGraphics.color && (chaosGraphics.mulS > 1))
            chaosGraphics.explosions = Explosions.High;
    }

    private void ResetConfig() {
        chaosSounds.dfltSound = false;
        chaosGraphics.dfltGraphic = false;
        chaosSounds.sound = false;
        chaosSounds.music = false;
        chaosGraphics.mulS = 1;
        chaosGraphics.color = false;
        chaosGraphics.dualpf = false;
        languages.SetLanguage((short) 0);
    }

    private void LoadConfig() {
        // VAR
        Runtime.RangeSet data = new Runtime.RangeSet(Memory.SET16_r);
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        chaosBase.file = files.OpenFile(Runtime.castToRef(memory.ADS(ConfigFileName), String.class), EnumSet.of(AccessFlags.accessRead));
        if (chaosBase.file != files.noFile) {
            if ((files.ReadFileBytes(chaosBase.file, data, 2) < 2) || (files.ReadFileBytes(chaosBase.file, new Runtime.FieldRef<>(chaosSounds::getMusicPri, chaosSounds::setMusicPri), 2) < 2) || (files.ReadFileBytes(chaosBase.file, new Runtime.FieldRef<>(chaosSounds::getNbChannel, chaosSounds::setNbChannel), 2) < 2) || (files.ReadFileBytes(chaosBase.file, new Runtime.FieldRef<>(chaosGraphics::getMulS, chaosGraphics::setMulS), 2) < 2) || (files.ReadFileBytes(chaosBase.file, new Runtime.FieldRef<>(chaosGraphics::getExplosions, chaosGraphics::setExplosions), 1) < 1) || (files.ReadFileBytes(chaosBase.file, ch, 1) < 1)) {
                checks.Warn(true, Runtime.castToRef(languages.ADL("Error reading file 'Config':"), String.class), files.FileErrorMsg());
                DefaultGraphic();
                DefaultSound();
            } else {
                dfltLang = (data.contains(5));
                if (dfltLang)
                    languages.SetLanguage(dfltLanguage);
                else
                    languages.SetLanguage((short) (char) ch.get());
                chaosGraphics.dfltGraphic = false;
                chaosSounds.dfltSound = false;
                chaosSounds.sound = (data.contains(0));
                chaosSounds.music = (data.contains(1));
                chaosSounds.stereo = (data.contains(2));
                chaosGraphics.color = (data.contains(6));
                chaosGraphics.dualpf = chaosGraphics.color && (data.contains(7));
                if (data.contains(3))
                    DefaultGraphic();
                if (data.contains(4))
                    DefaultSound();
            }
            files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
        } else {
            DefaultGraphic();
            DefaultSound();
        }
    }

    private void SaveConfig() {
        // VAR
        Runtime.RangeSet data = new Runtime.RangeSet(Memory.SET16_r);
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        ch.set((char) languages.language);
        data = new Runtime.RangeSet(Memory.SET16_r);
        if (chaosSounds.sound)
            data.incl(0);
        if (chaosSounds.music)
            data.incl(1);
        if (chaosSounds.stereo)
            data.incl(2);
        if (chaosGraphics.dfltGraphic)
            data.incl(3);
        if (chaosSounds.dfltSound)
            data.incl(4);
        if (dfltLang)
            data.incl(5);
        if (chaosGraphics.color)
            data.incl(6);
        if (chaosGraphics.color && chaosGraphics.dualpf)
            data.incl(7);
        while (true) {
            chaosBase.file = files.OpenFile(Runtime.castToRef(memory.ADS(ConfigFileName), String.class), EnumSet.of(AccessFlags.accessWrite));
            if ((chaosBase.file != files.noFile) && (files.WriteFileBytes(chaosBase.file, data, 2) == 2) && (files.WriteFileBytes(chaosBase.file, new Runtime.FieldRef<>(chaosSounds::getMusicPri, chaosSounds::setMusicPri), 2) == 2) && (files.WriteFileBytes(chaosBase.file, new Runtime.FieldRef<>(chaosSounds::getNbChannel, chaosSounds::setNbChannel), 2) == 2) && (files.WriteFileBytes(chaosBase.file, new Runtime.FieldRef<>(chaosGraphics::getMulS, chaosGraphics::setMulS), 2) == 2) && (files.WriteFileBytes(chaosBase.file, new Runtime.FieldRef<>(chaosGraphics::getExplosions, chaosGraphics::setExplosions), 1) == 1) && (files.WriteFileBytes(chaosBase.file, ch, 1) == 1)) {
                files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
                break;
            } else {
                if (!checks.Ask(Runtime.castToRef(languages.ADL("Error writing file 'Config':"), String.class), files.FileErrorMsg(), Runtime.castToRef(languages.ADL("Retry"), String.class), Runtime.castToRef(languages.ADL("Cancel"), String.class)))
                    break;
            }
        }
        files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
    }

    private void FlushMenus() {
        menus.FreeMenu(new Runtime.FieldRef<>(this::getMiscMenu, this::setMiscMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getLanguageMenu, this::setLanguageMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getSoundsMenu, this::setSoundsMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getGraphicsMenu, this::setGraphicsMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getQuitMenu, this::setQuitMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getBar2Menu, this::setBar2Menu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getHideMenu, this::setHideMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getBar1Menu, this::setBar1Menu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getSaveMenu, this::setSaveMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getLoadMenu, this::setLoadMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getNewMenu, this::setNewMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getSettingsMenu, this::setSettingsMenu));
        menus.FreeMenu(new Runtime.FieldRef<>(this::getFileMenu, this::setFileMenu));
    }

    private void InitMenus() {
        // VAR
        Object fm = null;
        Object sm = null;

        fileMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG1(Menus.mNAME, languages.ADL("File")));
        fm = menus.MenuToAddress(fileMenu);
        newMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("New Game "), Menus.mPARENT, fm, Menus.mCOMM, 'N'));
        loadMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("Load Game"), Menus.mPARENT, fm, Menus.mCOMM, 'O'));
        saveMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("Save Game"), Menus.mPARENT, fm, Menus.mCOMM, 'S'));
        bar1Menu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("---------"), Menus.mPARENT, fm, Menus.mENABLE, Memory.NO));
        hideMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("Hide     "), Menus.mPARENT, fm, Menus.mCOMM, 'H'));
        bar2Menu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("---------"), Menus.mPARENT, fm, Menus.mENABLE, Memory.NO));
        quitMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("Quit"), Menus.mPARENT, fm, Menus.mCOMM, 'Q'));
        settingsMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG1(Menus.mNAME, languages.ADL("Settings")));
        sm = menus.MenuToAddress(settingsMenu);
        graphicsMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("Graphics..."), Menus.mPARENT, sm, Menus.mCOMM, 'G'));
        soundsMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("Sounds...  "), Menus.mPARENT, sm, Menus.mCOMM, 'M'));
        languageMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("Language..."), Menus.mPARENT, sm, Menus.mCOMM, 'L'));
        miscMenu = menus.AddNewMenu((Memory.TagItem) memory.TAG3(Menus.mNAME, languages.ADL("Misc...    "), Menus.mPARENT, sm, Menus.mCOMM, 'C'));
    }

    public void EnableFileMenus() {
        fmEnabled = true;
        menus.ModifyMenu(graphicsMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.YES));
        menus.ModifyMenu(soundsMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.YES));
        menus.ModifyMenu(languageMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.YES));
        menus.ModifyMenu(miscMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.YES));
        menus.ModifyMenu(newMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.YES));
        menus.ModifyMenu(loadMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, (chaosBase.gameStat != GameStat.Playing ? 1 : 0)));
        menus.ModifyMenu(saveMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, (chaosBase.gameStat != GameStat.Playing ? 1 : 0)));
        menus.ModifyMenu(hideMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.YES));
    }

    public void DisableFileMenus() {
        fmEnabled = false;
        menus.ModifyMenu(graphicsMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.NO));
        menus.ModifyMenu(soundsMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.NO));
        menus.ModifyMenu(languageMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.NO));
        menus.ModifyMenu(miscMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.NO));
        menus.ModifyMenu(newMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.NO));
        menus.ModifyMenu(loadMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.NO));
        menus.ModifyMenu(saveMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.NO));
        menus.ModifyMenu(hideMenu, (Memory.TagItem) memory.TAG1(Menus.mENABLE, Memory.NO));
    }

    private void ColdInit() {
        input.SetBusyStat((short) Input.statBusy);
        dfltLanguage = languages.language;
        LoadConfig();
        InitMenus();
        input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eKEYBOARD, Input.eMENU, Input.eGADGET, Input.eREFRESH, Input.eTIMER, Input.eSYS));
    }

    private void WarnMem() {
        checks.Warn(true, Runtime.castToRef(languages.ADL("Not enough memory"), String.class), Runtime.castToRef(languages.ADL("for requested settings."), String.class));
    }

    private void InitSounds() {
        if (chaosSounds.sound) {
            input.RemEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU));
            if (!chaosSounds.SwitchSoundOn()) {
                chaosSounds.sound = false;
                chaosSounds.stereo = false;
                chaosSounds.SwitchSoundOff();
            }
            input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU));
        }
    }

    private void InitGraphics() {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj last = null;
        GraphicsErr gerr = GraphicsErr.gOk;

        input.RemEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU, Input.eREFRESH));
        input.SetBusyStat((short) Input.statBusy);
        if (!chaosGraphics.OpenScreen() || !chaosImages.InitImages()) {
            gerr = graphics.GetGraphicsErr();
            chaosGraphics.CloseScreen();
            ResetConfig();
            chaosSounds.SwitchSoundOff();
            if (gerr == GraphicsErr.gNotSupported)
                checks.Warn(true, Runtime.castToRef(languages.ADL("Unable to display the"), String.class), Runtime.castToRef(languages.ADL("requested number of colors"), String.class));
            else if (gerr == GraphicsErr.gTooComplex)
                checks.Warn(true, Runtime.castToRef(languages.ADL("Graphics size too big"), String.class), null);
            else
                WarnMem();
            checks.CheckMemBool(!chaosGraphics.OpenScreen() || !chaosImages.InitImages());
        }
        chaosImages.RenderObjects();
        obj = (ChaosBase.Obj) chaosBase.FirstObj(chaosBase.objList);
        last = (ChaosBase.Obj) chaosBase.TailObj(chaosBase.objList);
        while (obj != last) {
            { // WITH
                ChaosBase.ObjAttr _objAttr = obj.attr;
                if (_objAttr.Make != null)
                    _objAttr.Make.invoke(obj);
            }
            obj = (ChaosBase.Obj) chaosBase.NextObj(obj.objNode);
        }
        obj = (ChaosBase.Obj) chaosBase.FirstObj(chaosBase.leftObjList);
        last = (ChaosBase.Obj) chaosBase.TailObj(chaosBase.leftObjList);
        while (obj != last) {
            { // WITH
                ChaosBase.ObjAttr _objAttr = obj.attr;
                if (_objAttr.Make != null)
                    _objAttr.Make.invoke(obj);
            }
            obj = (ChaosBase.Obj) chaosBase.NextObj(obj.objNode);
        }
        do {
            input.BeginRefresh();
        } while (!input.EndRefresh());
        if (Refresh != null)
            Refresh.run();
        input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU, Input.eREFRESH));
    }

    public void WarmInit() {
        WarmFlush();
        InitGraphics();
        InitSounds();
        EnableFileMenus();
    }

    private void FlushSounds() {
        chaosSounds.SwitchSoundOff();
    }

    private void FlushGraphics() {
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
        graphics.DeleteArea(new Runtime.FieldRef<>(chaosGraphics::getImage2Area, chaosGraphics::setImage2Area));
        graphics.DeleteArea(new Runtime.FieldRef<>(chaosGraphics::getImageArea, chaosGraphics::setImageArea));
        chaosGraphics.CloseScreen();
    }

    public void WarmFlush() {
        DisableFileMenus();
        FlushSounds();
        FlushGraphics();
    }

    private void Hide() {
        // VAR
        Input.Event e = new Input.Event(); /* WRT */
        Menus.MenuPtr menu = null;

        if (chaosBase.d != dialogs.noGadget)
            return;
        WarmFlush();
        menus.ModifyMenu(hideMenu, (Memory.TagItem) memory.TAG2(Menus.mNAME, languages.ADL("Continue"), Menus.mENABLE, Memory.YES));
        while (true) {
            input.WaitEvent();
            input.GetEvent(e);
            if (e.type == Input.eMENU) {
                menu = menus.AddressToMenu(e.menu);
                if (menu == hideMenu)
                    break;
                else if (menu == quitMenu)
                    checks.Terminate();
            } else if (e.type == Input.eSYS) {
                if (e.msg == SysMsg.pActivate)
                    break;
                else if (e.msg == SysMsg.pQuit)
                    checks.Terminate();
            }
        }
        menus.ModifyMenu(hideMenu, (Memory.TagItem) memory.TAG1(Menus.mNAME, languages.ADL("Hide     ")));
        WarmInit();
        chaosImages.InitPalette();
        graphics.SetArea(chaosGraphics.mainArea);
        graphics.AreaToFront();
    }

    private void Quit() {
        if ((chaosBase.gameStat == GameStat.Start) || checks.Ask(Runtime.castToRef(languages.ADL("Quit program ?"), String.class), null, Runtime.castToRef(languages.ADL("Yes"), String.class), Runtime.castToRef(languages.ADL("No"), String.class)))
            chaosBase.gameStat = GameStat.Break;
    }

    private void NumToString(long val, /* VAR */ Runtime.IRef<String> str) {
        // VAR
        short c = 0;
        short d = 0;
        char ch = (char) 0;

        Runtime.setChar(str, (str.get().length() - 1), ((char) 0));
        c = (short) (str.get().length() - 1);
        do {
            c--;
            Runtime.setChar(str, c, (char) (48 + val % 10));
            val = val / 10;
        } while (!((c == 0) || (val == 0)));
        d = 0;
        do {
            ch = Runtime.getChar(str, c);
            Runtime.setChar(str, d, ch);
            c++;
            d++;
        } while (ch != ((char) 0));
    }

    private void LoadGame_Get(/* var */ Runtime.IRef<byte[]> data, /* VAR */ Runtime.IRef<Boolean> ok) {
        // VAR
        int size = 0;

        if (ok.get()) {
            size = (data.get().length - 1) + 1;
            ok.set((files.ReadFileBytes(chaosBase.file, data, size) == size));
        }
    }

    private void LoadGame_RangeChk(short val, short min, short max, /* VAR */ Runtime.IRef<Boolean> ok) {
        if ((val < min) || (val > max))
            ok.set(false);
    }

    private void LoadGame() {
        // VAR
        Runtime.Ref<String> dflt = new Runtime.Ref<>("");
        Runtime.IRef<String> fileName = null;
        Runtime.Ref<Long> id = new Runtime.Ref<>(0L);
        Runtime.Ref<Integer> v = new Runtime.Ref<>(0);
        Zone z = Zone.Chaos;
        Weapon w = Weapon.GUN;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        Runtime.Ref<Boolean> ok = new Runtime.Ref<>(false);

        if (!registration.registered) {
            checks.Warn(true, Runtime.castToRef(languages.ADL("Game loading is not possible"), String.class), Runtime.castToRef(languages.ADL("with the demo version."), String.class));
            if (checks.Ask(Runtime.castToRef(languages.ADL("Would you like to start"), String.class), Runtime.castToRef(languages.ADL("directly at level 5 ?"), String.class), Runtime.castToRef(languages.ADL("Yes"), String.class), Runtime.castToRef(languages.ADL("No"), String.class))) {
                chaosBase.level[Zone.Chaos.ordinal()] = 10;
                chaosBase.level[Zone.Castle.ordinal()] = 5;
                chaosBase.level[Zone.Family.ordinal()] = 3;
                chaosBase.level[Zone.Special.ordinal()] = 1;
                chaosBase.nbDollar = 150;
                chaosBase.nbSterling = 10;
                chaosBase.powerCountDown = 6;
                chaosBase.specialStage = 0;
                for (int _w = 0; _w < Weapon.values().length; _w++) {
                    w = Weapon.values()[_w];
                    { // WITH
                        ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
                        _weaponAttr.nbBullet = 0;
                        _weaponAttr.nbBomb = 0;
                        _weaponAttr.power = (short) (w == Weapon.GUN ? 1 : 0);
                    }
                }
                chaosBase.weaponAttr[Weapon.GUN.ordinal()].nbBullet = 99;
                chaosBase.weaponAttr[Weapon.LASER.ordinal()].power = 2;
                chaosBase.weaponAttr[Weapon.LASER.ordinal()].nbBullet = 10;
                chaosBase.weaponAttr[Weapon.BALL.ordinal()].power = 2;
                chaosBase.weaponAttr[Weapon.BALL.ordinal()].nbBullet = 10;
                chaosBase.weaponAttr[Weapon.FIRE.ordinal()].power = 4;
                chaosBase.weaponAttr[Weapon.LASER.ordinal()].nbBomb = 1;
                if ((chaosBase.gameStat != GameStat.Start) && (Refresh != null))
                    Refresh.run();
                chaosBase.gameStat = GameStat.Finish;
            }
            return;
        }
        ok.set(true);
        memory.CopyStr(new Runtime.FieldRef<>(this::getLsFile, this::setLsFile), dflt, Runtime.sizeOf(96, String.class));
        fileName = files.AskFile((Memory.TagItem) memory.TAG2(Files.fNAME, dflt, Files.fTEXT, languages.ADL("Load game:")));
        if (fileName != null) {
            memory.CopyStr(fileName, new Runtime.FieldRef<>(this::getLsFile, this::setLsFile), Runtime.sizeOf(96, String.class));
            chaosBase.file = files.OpenFile(fileName, EnumSet.of(AccessFlags.accessRead));
            checks.Warn(chaosBase.file == files.noFile, fileName, files.FileErrorMsg());
            if (chaosBase.file != files.noFile) {
                LoadGame_Get(Runtime.asByteArray(id), ok);
                if (id.get() != ID) {
                    checks.Warn(true, Runtime.castToRef(languages.ADL("Invalid format"), String.class), fileName);
                } else {
                    LoadGame_Get(new Runtime.FieldRef<>(chaosBase::getScore, chaosBase::setScore).asByteArray(), ok);
                    LoadGame_Get(Runtime.asByteArray(ch), ok);
                    chaosBase.pLife = (short) (char) ch.get();
                    LoadGame_RangeChk(chaosBase.pLife, (short) 1, (short) 239, ok);
                    chaosBase.pLife = (short) (chaosBase.pLife % 30);
                    LoadGame_Get(Runtime.asByteArray(ch), ok);
                    chaosBase.nbDollar = (short) (char) ch.get();
                    LoadGame_RangeChk(chaosBase.nbDollar, (short) 0, (short) 200, ok);
                    LoadGame_Get(Runtime.asByteArray(ch), ok);
                    chaosBase.nbSterling = (short) (char) ch.get();
                    LoadGame_RangeChk(chaosBase.nbSterling, (short) 0, (short) 200, ok);
                    LoadGame_Get(Runtime.asByteArray(ch), ok);
                    chaosBase.powerCountDown = (short) ((char) ch.get() / 10);
                    chaosBase.difficulty = (char) ch.get() % 10;
                    if (chaosBase.difficulty == 0)
                        chaosBase.difficulty = 10;
                    LoadGame_Get(Runtime.asByteArray(ch), ok);
                    chaosBase.specialStage = (short) (char) ch.get();
                    chaosBase.stages = (short) (chaosBase.specialStage % 6);
                    chaosBase.specialStage = (short) (chaosBase.specialStage / 6);
                    chaosBase.zone = Zone.Chaos;
                    for (int _z = 0; _z < Zone.values().length; _z++) {
                        z = Zone.values()[_z];
                        LoadGame_Get(Runtime.asByteArray(ch), ok);
                        chaosBase.level[z.ordinal()] = (short) (char) ch.get();
                    }
                    if (chaosBase.level[Zone.Family.ordinal()] > 10) {
                        chaosBase.level[Zone.Family.ordinal()] -= 10;
                        chaosBase.password = true;
                    } else {
                        chaosBase.password = false;
                    }
                    LoadGame_RangeChk(chaosBase.level[Zone.Chaos.ordinal()], (short) 1, (short) 100, ok);
                    LoadGame_RangeChk(chaosBase.level[Zone.Castle.ordinal()], (short) 1, (short) 20, ok);
                    LoadGame_RangeChk(chaosBase.level[Zone.Family.ordinal()], (short) 1, (short) 10, ok);
                    LoadGame_RangeChk(chaosBase.level[Zone.Special.ordinal()], (short) 1, (short) 24, ok);
                    for (int _w = 0; _w < Weapon.values().length; _w++) {
                        w = Weapon.values()[_w];
                        { // WITH
                            ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
                            LoadGame_Get(Runtime.asByteArray(v), ok);
                            _weaponAttr.nbBullet = (short) (v.get() % 100);
                            v.set(v.get() / 100);
                            _weaponAttr.nbBomb = (short) (v.get() % 10);
                            _weaponAttr.power = (short) (v.get() / 10);
                            LoadGame_RangeChk(_weaponAttr.power, (short) 0, (short) 4, ok);
                        }
                    }
                    LoadGame_Get(new Runtime.FieldRef<>(chaosBase::getGameSeed, chaosBase::setGameSeed).asByteArray(), ok);
                }
                files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
                if ((chaosBase.gameStat != GameStat.Start) && (Refresh != null))
                    Refresh.run();
                checks.Warn(!ok.get(), new Runtime.FieldRef<>(this::getLsFile, this::setLsFile), Runtime.castToRef(languages.ADL("Unable to load game's infos"), String.class));
                if (ok.get())
                    chaosBase.gameStat = GameStat.Finish;
            }
        }
    }

    private void SaveGame_Put(byte[] data, /* VAR */ Runtime.IRef<Boolean> ok) {
        // VAR
        int size = 0;

        if (ok.get()) {
            size = (data.length - 1) + 1;
            ok.set((files.WriteFileBytes(chaosBase.file, data, size) == size));
        }
    }

    private void SaveGame() {
        // VAR
        Runtime.Ref<String> dflt = new Runtime.Ref<>("");
        Runtime.IRef<String> fileName = null;
        long id = 0L;
        int v = 0;
        Zone z = Zone.Chaos;
        Weapon w = Weapon.GUN;
        char ch = (char) 0;
        Runtime.Ref<Boolean> ok = new Runtime.Ref<>(false);

        if (!registration.registered) {
            checks.Warn(true, Runtime.castToRef(languages.ADL("Game saving is not possible"), String.class), Runtime.castToRef(languages.ADL("with the demo version."), String.class));
            return;
        }
        ok.set(true);
        id = ID;
        memory.CopyStr(new Runtime.FieldRef<>(this::getLsFile, this::setLsFile), dflt, Runtime.sizeOf(96, String.class));
        fileName = files.AskFile((Memory.TagItem) memory.TAG3(Files.fNAME, dflt, Files.fTEXT, languages.ADL("Save game:"), Files.fFLAGS, Files.afNEWFILE));
        if (fileName != null) {
            memory.CopyStr(fileName, new Runtime.FieldRef<>(this::getLsFile, this::setLsFile), Runtime.sizeOf(96, String.class));
            chaosBase.file = files.OpenFile(fileName, EnumSet.of(AccessFlags.accessWrite));
            checks.Warn(chaosBase.file == files.noFile, fileName, files.FileErrorMsg());
            if (chaosBase.file != files.noFile) {
                SaveGame_Put(Runtime.toByteArray(id), ok);
                SaveGame_Put(Runtime.toByteArray(chaosBase.score), ok);
                ch = (char) chaosBase.pLife;
                SaveGame_Put(Runtime.toByteArray(ch), ok);
                ch = (char) chaosBase.nbDollar;
                SaveGame_Put(Runtime.toByteArray(ch), ok);
                ch = (char) chaosBase.nbSterling;
                SaveGame_Put(Runtime.toByteArray(ch), ok);
                ch = (char) (chaosBase.powerCountDown * 10 + chaosBase.difficulty % 10);
                SaveGame_Put(Runtime.toByteArray(ch), ok);
                ch = (char) (chaosBase.specialStage * 6 + chaosBase.stages);
                SaveGame_Put(Runtime.toByteArray(ch), ok);
                for (int _z = 0; _z < Zone.values().length; _z++) {
                    z = Zone.values()[_z];
                    if ((z == Zone.Family) && chaosBase.password)
                        ch = (char) (chaosBase.level[z.ordinal()] + 10);
                    else
                        ch = (char) chaosBase.level[z.ordinal()];
                    SaveGame_Put(Runtime.toByteArray(ch), ok);
                }
                for (int _w = 0; _w < Weapon.values().length; _w++) {
                    w = Weapon.values()[_w];
                    { // WITH
                        ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
                        v = _weaponAttr.power;
                        v = v * 10;
                        v += _weaponAttr.nbBomb;
                        v = v * 100;
                        v += _weaponAttr.nbBullet;
                        SaveGame_Put(Runtime.toByteArray(v), ok);
                    }
                }
                SaveGame_Put(Runtime.toByteArray(chaosBase.gameSeed), ok);
                files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
                if ((chaosBase.gameStat != GameStat.Start) && (Refresh != null))
                    Refresh.run();
                checks.Warn(!ok.get(), fileName, files.FileErrorMsg());
            }
        }
    }

    private void ColdFlush() {
        input.SetBusyStat((short) Input.statBusy);
        WarmFlush();
        chaosBase.FlushAllObjs();
        input.RemEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eKEYBOARD, Input.eMENU, Input.eGADGET, Input.eREFRESH, Input.eSYS));
        FlushMenus();
    }

    private void AskGraphicSettings() {
        // CONST
        final String Busy = "---------------";

        // VAR
        Dialogs.GadgetPtr gadget = null;
        Dialogs.GadgetPtr autoconf = null;
        Dialogs.GadgetPtr expl = null;
        Dialogs.GadgetPtr render = null;
        Dialogs.GadgetPtr size = null;
        Dialogs.GadgetPtr save = null;
        Dialogs.GadgetPtr use = null;
        Dialogs.GadgetPtr cancel = null;
        Dialogs.GadgetPtr group1 = null;
        Dialogs.GadgetPtr group2 = null;
        Dialogs.GadgetPtr group3 = null;
        Input.Event event = new Input.Event(); /* WRT */
        Memory.TagItem tagItem = new Memory.TagItem(); /* WRT */
        Object expTxt = null;
        Object renderTxt = null;
        Object busy = null;
        Runnable oldRefresh = null;
        boolean oldcolor = false;
        boolean olddflt = false;
        boolean olddual = false;
        Explosions oldexplosions = Explosions.Low;
        short oldmulS = 0;
        short c = 0;
        boolean change = false;

        DisableFileMenus();
        input.SetBusyStat((short) Input.statBusy);
        input.FlushEvents();
        oldmulS = chaosGraphics.mulS;
        olddflt = chaosGraphics.dfltGraphic;
        olddual = chaosGraphics.dualpf;
        oldcolor = chaosGraphics.color;
        oldexplosions = chaosGraphics.explosions;
        oldRefresh = Refresh;
        Refresh = null;
        busy = languages.ADL(Busy);
        chaosBase.d = dialogs.CreateGadget((short) Dialogs.dDialog, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, languages.ADL("Graphics settings:"), Dialogs.dFLAGS, Dialogs.dfCLOSE + Dialogs.dfSELECT));
        group1 = dialogs.AddNewGadget(chaosBase.d, (short) Dialogs.dGroup, (Memory.TagItem) memory.TAG1(Dialogs.dFLAGS, Dialogs.dfVDIR));
        autoconf = dialogs.AddNewGadget(group1, (short) Dialogs.dBool, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, languages.ADL("Auto configure"), Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
        group2 = dialogs.AddNewGadget(group1, (short) Dialogs.dGroup, (Memory.TagItem) memory.TAG2(Dialogs.dSPAN, 3, Dialogs.dFLAGS, Dialogs.dfBORDER + Dialogs.dfVDIR));
        gadget = dialogs.AddNewGadget(group2, (short) Dialogs.dLabel, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, languages.ADL("Explosions:"), Dialogs.dRFLAGS, Dialogs.dfAUTOLEFT));
        gadget = dialogs.AddNewGadget(group2, (short) Dialogs.dLabel, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, languages.ADL("Rendering:"), Dialogs.dRFLAGS, Dialogs.dfAUTOLEFT));
        gadget = dialogs.AddNewGadget(group2, (short) Dialogs.dLabel, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, languages.ADL("Size:"), Dialogs.dRFLAGS, Dialogs.dfAUTOLEFT));
        expl = dialogs.AddNewGadget(group2, (short) Dialogs.dCycle, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, busy, Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
        render = dialogs.AddNewGadget(group2, (short) Dialogs.dCycle, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, busy, Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
        size = dialogs.AddNewGadget(group2, (short) Dialogs.dIntEdit, (Memory.TagItem) memory.TAG3(Dialogs.dINTVAL, chaosGraphics.mulS, Dialogs.dTXTLEN, 2, Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
        group3 = dialogs.AddNewGadget(group1, (short) Dialogs.dGroup, null);
        save = dialogs.AddNewGadget(group3, (short) Dialogs.dButton, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Save")));
        use = dialogs.AddNewGadget(group3, (short) Dialogs.dButton, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Use")));
        cancel = dialogs.AddNewGadget(group3, (short) Dialogs.dButton, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Cancel")));
        checks.CheckMemBool(dialogs.RefreshGadget(chaosBase.d) != Dialogs.DialogOk);
        do {
            if (chaosGraphics.explosions == Explosions.Low)
                expTxt = languages.ADL("Low");
            else if (chaosGraphics.explosions == Explosions.Medium)
                expTxt = languages.ADL("Medium");
            else
                expTxt = languages.ADL("High");
            dialogs.ModifyGadget(expl, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, expTxt));
            if (chaosGraphics.color) {
                if (chaosGraphics.dualpf)
                    renderTxt = languages.ADL("Colors x2");
                else
                    renderTxt = languages.ADL("Colors");
            } else {
                renderTxt = languages.ADL("Black&White");
            }
            dialogs.ModifyGadget(render, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, renderTxt));
            dialogs.ModifyGadget(expl, (Memory.TagItem) memory.TAG2(Dialogs.dFLAGS, (!chaosGraphics.dfltGraphic ? 1 : 0) * Dialogs.dfACTIVE, Dialogs.dMASK, Dialogs.dfACTIVE));
            dialogs.ModifyGadget(render, (Memory.TagItem) memory.TAG2(Dialogs.dFLAGS, (!chaosGraphics.dfltGraphic ? 1 : 0) * Dialogs.dfACTIVE, Dialogs.dMASK, Dialogs.dfACTIVE));
            dialogs.ModifyGadget(size, (Memory.TagItem) memory.TAG2(Dialogs.dFLAGS, (!chaosGraphics.dfltGraphic ? 1 : 0) * Dialogs.dfACTIVE, Dialogs.dMASK, Dialogs.dfACTIVE));
            dialogs.ModifyGadget(autoconf, (Memory.TagItem) memory.TAG2(Dialogs.dFLAGS, (chaosGraphics.dfltGraphic ? 1 : 0) * Dialogs.dfSELECT, Dialogs.dMASK, Dialogs.dfSELECT));
            do {
                if (chaosBase.gameStat == GameStat.Break)
                    return;
                input.SetBusyStat((short) Input.statWaiting);
                input.WaitEvent();
                input.GetEvent(event);
                if (event.type == Input.eGADGET) {
                    if (event.gadget == expl) {
                        if (chaosGraphics.explosions == Explosions.High)
                            chaosGraphics.explosions = Explosions.Low;
                        else
                            chaosGraphics.explosions = Runtime.next(chaosGraphics.explosions);
                    } else if (event.gadget == render) {
                        if (chaosGraphics.color) {
                            if (chaosGraphics.dualpf) {
                                chaosGraphics.color = false;
                                chaosGraphics.dualpf = false;
                            } else {
                                chaosGraphics.dualpf = true;
                            }
                        } else {
                            chaosGraphics.color = true;
                            chaosGraphics.dualpf = false;
                        }
                    } else if (event.gadget == autoconf) {
                        chaosGraphics.dfltGraphic = !chaosGraphics.dfltGraphic;
                        if (chaosGraphics.dfltGraphic) {
                            DefaultGraphic();
                            dialogs.ModifyGadget(size, (Memory.TagItem) memory.TAG1(Dialogs.dINTVAL, chaosGraphics.mulS));
                        }
                    }
                } else {
                    CommonEvent(event);
                }
            } while (event.type != Input.eGADGET);
            tagItem.tag = Dialogs.dINTVAL;
            dialogs.GetGadgetAttr(size, tagItem);
            chaosGraphics.mulS = (short) tagItem.data;
            if ((chaosGraphics.mulS < 1) || (chaosGraphics.mulS > 9))
                chaosGraphics.mulS = 1;
        } while (!((event.gadget == save) || (event.gadget == use) || (event.gadget == cancel) || (event.gadget == chaosBase.d)));
        input.SetBusyStat((short) Input.statBusy);
        change = (chaosGraphics.mulS != oldmulS) || (chaosGraphics.color != oldcolor) || (chaosGraphics.dualpf != olddual);
        if ((event.gadget != cancel) && (event.gadget != chaosBase.d)) {
            input.FlushEvents();
            if (change) {
                FlushGraphics();
                Refresh = oldRefresh;
                InitGraphics();
            } else if (oldRefresh != null) {
                Refresh = oldRefresh;
                Refresh.run();
            }
        } else {
            chaosGraphics.mulS = oldmulS;
            chaosGraphics.dfltGraphic = olddflt;
            chaosGraphics.dualpf = olddual;
            chaosGraphics.color = oldcolor;
            chaosGraphics.explosions = oldexplosions;
            Refresh = oldRefresh;
            Refresh.run();
        }
        EnableFileMenus();
        if (event.gadget == save)
            SaveConfig();
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
    }

    private void AskSoundSettings() {
        // VAR
        Dialogs.GadgetPtr gadget = null;
        Dialogs.GadgetPtr autoconf = null;
        Dialogs.GadgetPtr cbsound = null;
        Dialogs.GadgetPtr cbstereo = null;
        Dialogs.GadgetPtr tchan = null;
        Dialogs.GadgetPtr save = null;
        Dialogs.GadgetPtr use = null;
        Dialogs.GadgetPtr cancel = null;
        Dialogs.GadgetPtr group1 = null;
        Dialogs.GadgetPtr group2 = null;
        Dialogs.GadgetPtr group3 = null;
        Input.Event event = new Input.Event(); /* WRT */
        Memory.TagItem tagItem = new Memory.TagItem(); /* WRT */
        boolean oldsound = false;
        boolean oldmusic = false;
        boolean oldstereo = false;
        boolean olddflt = false;
        short oldmusicPri = 0;
        int oldnbChannel = 0;
        boolean change = false;

        DisableFileMenus();
        input.SetBusyStat((short) Input.statBusy);
        input.FlushEvents();
        oldsound = chaosSounds.sound;
        oldmusic = chaosSounds.music;
        oldstereo = chaosSounds.stereo;
        oldmusicPri = chaosSounds.musicPri;
        oldnbChannel = chaosSounds.nbChannel;
        olddflt = chaosSounds.dfltSound;
        chaosBase.d = dialogs.CreateGadget((short) Dialogs.dDialog, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, languages.ADL("Sounds settings"), Dialogs.dFLAGS, Dialogs.dfCLOSE + Dialogs.dfSELECT));
        group1 = dialogs.AddNewGadget(chaosBase.d, (short) Dialogs.dGroup, (Memory.TagItem) memory.TAG1(Dialogs.dFLAGS, Dialogs.dfVDIR));
        autoconf = dialogs.AddNewGadget(group1, (short) Dialogs.dBool, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, languages.ADL("Auto configure"), Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
        group3 = dialogs.AddNewGadget(group1, (short) Dialogs.dGroup, (Memory.TagItem) memory.TAG1(Dialogs.dFLAGS, Dialogs.dfBORDER + Dialogs.dfVDIR));
        group2 = dialogs.AddNewGadget(group3, (short) Dialogs.dGroup, null);
        cbsound = dialogs.AddNewGadget(group2, (short) Dialogs.dCheckbox, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Sounds")));
        cbstereo = dialogs.AddNewGadget(group2, (short) Dialogs.dCheckbox, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Stereo")));
        group2 = dialogs.AddNewGadget(group3, (short) Dialogs.dGroup, null);
        gadget = dialogs.AddNewGadget(group2, (short) Dialogs.dLabel, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Number of channels:")));
        tchan = dialogs.AddNewGadget(group2, (short) Dialogs.dIntEdit, (Memory.TagItem) memory.TAG2(Dialogs.dINTVAL, chaosSounds.nbChannel, Dialogs.dTXTLEN, 3));
        group3 = dialogs.AddNewGadget(group1, (short) Dialogs.dGroup, null);
        save = dialogs.AddNewGadget(group3, (short) Dialogs.dButton, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Save")));
        use = dialogs.AddNewGadget(group3, (short) Dialogs.dButton, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Use")));
        cancel = dialogs.AddNewGadget(group3, (short) Dialogs.dButton, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Cancel")));
        checks.CheckMemBool(dialogs.RefreshGadget(chaosBase.d) != Dialogs.DialogOk);
        do {
            dialogs.ModifyGadget(cbsound, (Memory.TagItem) memory.TAG2(Dialogs.dFLAGS, Dialogs.dfACTIVE * (!chaosSounds.dfltSound ? 1 : 0) + Dialogs.dfSELECT * (chaosSounds.sound ? 1 : 0), Dialogs.dMASK, Dialogs.dfSELECT + Dialogs.dfACTIVE));
            dialogs.ModifyGadget(cbstereo, (Memory.TagItem) memory.TAG2(Dialogs.dFLAGS, Dialogs.dfACTIVE * (chaosSounds.sound && !chaosSounds.dfltSound ? 1 : 0) + Dialogs.dfSELECT * (chaosSounds.stereo ? 1 : 0), Dialogs.dMASK, Dialogs.dfACTIVE + Dialogs.dfSELECT));
            dialogs.ModifyGadget(tchan, (Memory.TagItem) memory.TAG2(Dialogs.dFLAGS, Dialogs.dfACTIVE * (!chaosSounds.dfltSound ? 1 : 0), Dialogs.dMASK, Dialogs.dfACTIVE));
            dialogs.ModifyGadget(autoconf, (Memory.TagItem) memory.TAG2(Dialogs.dFLAGS, Dialogs.dfSELECT * (chaosSounds.dfltSound ? 1 : 0), Dialogs.dMASK, Dialogs.dfSELECT));
            do {
                if (chaosBase.gameStat == GameStat.Break)
                    return;
                input.SetBusyStat((short) Input.statWaiting);
                input.WaitEvent();
                input.GetEvent(event);
                if (event.type == Input.eGADGET) {
                    if (event.gadget == cbsound) {
                        chaosSounds.sound = !chaosSounds.sound;
                        chaosSounds.stereo = chaosSounds.stereo && chaosSounds.sound;
                    } else if (event.gadget == cbstereo) {
                        chaosSounds.stereo = !chaosSounds.stereo;
                    } else if (event.gadget == autoconf) {
                        chaosSounds.dfltSound = !chaosSounds.dfltSound;
                        if (chaosSounds.dfltSound) {
                            DefaultSound();
                            dialogs.ModifyGadget(tchan, (Memory.TagItem) memory.TAG1(Dialogs.dINTVAL, chaosSounds.nbChannel));
                        }
                    }
                } else {
                    CommonEvent(event);
                }
            } while (event.type != Input.eGADGET);
            tagItem.tag = Dialogs.dINTVAL;
            dialogs.GetGadgetAttr(tchan, tagItem);
            chaosSounds.nbChannel = (int) tagItem.data;
            if (chaosSounds.nbChannel < 1)
                chaosSounds.sound = false;
        } while (!((event.gadget == save) || (event.gadget == use) || (event.gadget == cancel) || (event.gadget == chaosBase.d)));
        input.SetBusyStat((short) Input.statBusy);
        change = (chaosSounds.sound != oldsound) || (chaosSounds.stereo != oldstereo) || (chaosSounds.nbChannel != oldnbChannel) || (chaosSounds.music != oldmusic) || (chaosSounds.musicPri != oldmusicPri);
        if ((event.gadget != cancel) && (event.gadget != chaosBase.d)) {
            if (chaosSounds.nbChannel > 16)
                chaosSounds.nbChannel = 16;
            if (change) {
                dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
                FlushSounds();
                InitSounds();
            }
        } else {
            chaosSounds.sound = oldsound;
            chaosSounds.music = oldmusic;
            chaosSounds.stereo = oldstereo;
            chaosSounds.musicPri = oldmusicPri;
            chaosSounds.nbChannel = oldnbChannel;
            chaosSounds.dfltSound = olddflt;
        }
        EnableFileMenus();
        if (event.gadget == save)
            SaveConfig();
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
    }

    private void AskLanguage() {
        // CONST
        final String Busy = "----------------------";

        // VAR
        Dialogs.GadgetPtr group1 = null;
        Dialogs.GadgetPtr group2 = null;
        Dialogs.GadgetPtr cycle = null;
        Dialogs.GadgetPtr save = null;
        Dialogs.GadgetPtr use = null;
        Dialogs.GadgetPtr cancel = null;
        Input.Event event = new Input.Event(); /* WRT */
        Object name = null;
        short newlanguage = 0;
        boolean change = false;

        DisableFileMenus();
        input.SetBusyStat((short) Input.statBusy);
        input.FlushEvents();
        newlanguage = languages.language;
        chaosBase.d = dialogs.CreateGadget((short) Dialogs.dDialog, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Language:")));
        group1 = dialogs.AddNewGadget(chaosBase.d, (short) Dialogs.dGroup, (Memory.TagItem) memory.TAG1(Dialogs.dFLAGS, Dialogs.dfVDIR));
        cycle = dialogs.AddNewGadget(group1, (short) Dialogs.dCycle, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL(Busy)));
        group2 = dialogs.AddNewGadget(group1, (short) Dialogs.dGroup, null);
        save = dialogs.AddNewGadget(group2, (short) Dialogs.dButton, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Save")));
        use = dialogs.AddNewGadget(group2, (short) Dialogs.dButton, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Use")));
        cancel = dialogs.AddNewGadget(group2, (short) Dialogs.dButton, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, languages.ADL("Cancel")));
        checks.CheckMemBool(dialogs.RefreshGadget(chaosBase.d) != Dialogs.DialogOk);
        do {
            while (true) {
                input.SetBusyStat((short) Input.statBusy);
                if (dfltLang)
                    name = languages.ADL("<Default>");
                else
                    name = languages.GetLanguageName(newlanguage);
                if (name != null)
                    break;
                dfltLang = true;
                newlanguage = 0;
            }
            dialogs.ModifyGadget(cycle, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, name));
            do {
                if (chaosBase.gameStat == GameStat.Break)
                    return;
                input.SetBusyStat((short) Input.statWaiting);
                input.WaitEvent();
                input.GetEvent(event);
                if (event.type == Input.eGADGET) {
                    if (event.gadget == cycle) {
                        if (dfltLang)
                            dfltLang = false;
                        else
                            newlanguage++;
                    }
                } else {
                    CommonEvent(event);
                }
            } while (event.type != Input.eGADGET);
        } while (event.gadget == cycle);
        input.SetBusyStat((short) Input.statBusy);
        if (dfltLang)
            newlanguage = dfltLanguage;
        change = (newlanguage != languages.language);
        if ((event.gadget != cancel) && (event.gadget != chaosBase.d)) {
            if (change) {
                languages.SetLanguage(newlanguage);
                InitMenus();
                if (Refresh != null)
                    Refresh.run();
            }
        }
        EnableFileMenus();
        if (event.gadget == save)
            SaveConfig();
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
    }

    public void CommonEvent(Input.Event event) {
        // VAR
        Menus.MenuPtr menu = null;
        Runtime.RangeSet miscChanges = new Runtime.RangeSet(Memory.SET16_r);

        if (event.type == Input.eMENU) {
            menu = menus.AddressToMenu(event.menu);
            if (menu == newMenu) {
                if ((chaosBase.gameStat == GameStat.Playing) || (chaosBase.gameStat == GameStat.Finish)) {
                    chaosBase.gameStat = GameStat.Gameover;
                    chaosBase.water = false;
                }
            } else if (menu == loadMenu) {
                if ((chaosBase.d == dialogs.noGadget) && fmEnabled)
                    LoadGame();
            } else if (menu == saveMenu) {
                if ((chaosBase.d == dialogs.noGadget) && fmEnabled)
                    SaveGame();
            } else if (menu == hideMenu) {
                if (chaosBase.d == dialogs.noGadget)
                    Hide();
            } else if (menu == quitMenu) {
                Quit();
            } else if (menu == graphicsMenu) {
                if ((chaosBase.d == dialogs.noGadget) && fmEnabled)
                    AskGraphicSettings();
            } else if (menu == soundsMenu) {
                if ((chaosBase.d == dialogs.noGadget) && fmEnabled)
                    AskSoundSettings();
            } else if (menu == languageMenu) {
                if ((chaosBase.d == dialogs.noGadget) && fmEnabled)
                    AskLanguage();
            } else if (menu == miscMenu) {
                if ((chaosBase.d == dialogs.noGadget) && fmEnabled) {
                    input.SetBusyStat((short) Input.statBusy);
                    DisableFileMenus();
                    FlushSounds();
                    miscChanges.copyFrom(files.AskMiscSettings(new Runtime.RangeSet(Memory.SET16_r).with(Files.msGraphic, Files.msSound, Files.msInput, Files.msClock, Files.msDialogs, Files.msMenus)));
                    if (miscChanges.contains(Files.msGraphic))
                        FlushGraphics();
                    if (miscChanges.contains(Files.msMenus)) {
                        FlushMenus();
                        InitMenus();
                    }
                    if (miscChanges.contains(Files.msGraphic))
                        InitGraphics();
                    InitSounds();
                    EnableFileMenus();
                }
            }
        } else if (event.type == Input.eKEYBOARD) {
            if ((event.ch == 'Q') || (event.ch == 'q'))
                Quit();
        } else if (event.type == Input.eREFRESH) {
            do {
                input.BeginRefresh();
                if (Refresh != null)
                    Refresh.run();
            } while (!input.EndRefresh());
        } else if (event.type == Input.eSYS) {
            if ((event.msg == SysMsg.pQuit) || (event.msg == SysMsg.pKill))
                Quit();
        }
    }


    // Support

    private static ChaosInterface instance;

    public static ChaosInterface instance() {
        if (instance == null)
            new ChaosInterface(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        lsFile = "Games/";
        Refresh = null;
        checks.AddTermProc(Runtime.proc(this::ColdFlush, "ChaosInterface.ColdFlush"));
        ColdInit();
    }

    public void close() {
    }

}
