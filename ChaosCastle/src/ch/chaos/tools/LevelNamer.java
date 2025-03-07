package ch.chaos.tools;

import ch.chaos.library.Languages;
import ch.pitchtech.modula.runtime.Runtime;

public class LevelNamer {
    
    private final static Languages languages = Languages.instance();
    

    public static String getChaosName(short level) {
        // VAR
        short d = 0;
        String levelName = "";

        d = (short) (level / 10);
        if (d == 0) {
            levelName = Runtime.setChar(levelName, 0, (char) (48 + level));
            levelName = Runtime.setChar(levelName, 1, ((char) 0));
        } else {
            levelName = Runtime.setChar(levelName, 0, (char) (48 + d % 10));
            levelName = Runtime.setChar(levelName, 1, (char) (48 + level % 10));
            levelName = Runtime.setChar(levelName, 2, ((char) 0));
        }
        return levelName;
    }
    
    public static String getCastleName(short level) {
        return getCastleName0(level).get();
    }

    private static Runtime.IRef<String> getCastleName0(short level) {
        switch (level) {
            case 1 -> {
                return Runtime.castToRef(languages.ADL("Entry"), String.class);
            }
            case 2 -> {
                return Runtime.castToRef(languages.ADL("Groove"), String.class);
            }
            case 3 -> {
                return Runtime.castToRef(languages.ADL("Garden"), String.class);
            }
            case 4 -> {
                return Runtime.castToRef(languages.ADL("Lake"), String.class);
            }
            case 5 -> {
                return Runtime.castToRef(languages.ADL("Site"), String.class);
            }
            case 6 -> {
                return Runtime.castToRef(languages.ADL("GhostCastle"), String.class);
            }
            case 7 -> {
                return Runtime.castToRef(languages.ADL("Machinery"), String.class);
            }
            case 8 -> {
                return Runtime.castToRef(languages.ADL("Ice Rink"), String.class);
            }
            case 9 -> {
                return Runtime.castToRef(languages.ADL("Factory"), String.class);
            }
            case 10 -> {
                return Runtime.castToRef(languages.ADL("Labyrinth"), String.class);
            }
            case 11 -> {
                return Runtime.castToRef(languages.ADL("Rooms"), String.class);
            }
            case 12 -> {
                return Runtime.castToRef(languages.ADL("Yard"), String.class);
            }
            case 13 -> {
                return Runtime.castToRef(languages.ADL("Antarctica"), String.class);
            }
            case 14 -> {
                return Runtime.castToRef(languages.ADL("Forest"), String.class);
            }
            case 15 -> {
                return Runtime.castToRef(languages.ADL(" Castle "), String.class);
            }
            case 16 -> {
                return Runtime.castToRef(languages.ADL("Lights"), String.class);
            }
            case 17 -> {
                return Runtime.castToRef(languages.ADL("Plain"), String.class);
            }
            case 18 -> {
                return Runtime.castToRef(languages.ADL("Underwater"), String.class);
            }
            case 19 -> {
                return Runtime.castToRef(languages.ADL("Assembly"), String.class);
            }
            case 20 -> {
                return Runtime.castToRef(languages.ADL("Jungle"), String.class);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + level);
        }
    }
    
    public static String getBonusLevelName(short level) {
        return getSpecialName0(level).get();
    }

    private static Runtime.IRef<String> getSpecialName0(short level) {
        if (level == 24)
            return Runtime.castToRef(languages.ADL("ChaosCastle"), String.class);
        else if (level % 8 == 0)
            return Runtime.castToRef(languages.ADL("Winter"), String.class);
        else if (level % 4 == 0)
            return Runtime.castToRef(languages.ADL("Graveyard"), String.class);
        else if (level % 2 == 0)
            return Runtime.castToRef(languages.ADL("Autumn"), String.class);
        else
            return Runtime.castToRef(languages.ADL("Baby Aliens"), String.class);
    }

    public static String getFamilyName(short level) {
        return getFamilyName0(level).get();
    }
    
    private static Runtime.IRef<String> getFamilyName0(short level) {
        switch (level) {
            case 1 -> {
                return Runtime.castToRef(languages.ADL("Brother Alien"), String.class);
            }
            case 2 -> {
                return Runtime.castToRef(languages.ADL("Sister Alien"), String.class);
            }
            case 3 -> {
                return Runtime.castToRef(languages.ADL("Mother Alien"), String.class);
            }
            case 4 -> {
                return Runtime.castToRef(languages.ADL("FATHER ALIEN"), String.class);
            }
            case 5 -> {
                return Runtime.castToRef(languages.ADL("MASTER ALIEN"), String.class);
            }
            case 6 -> {
                return Runtime.castToRef(languages.ADL("MASTER ALIEN 2"), String.class);
            }
            case 7 -> {
                return Runtime.castToRef(languages.ADL("KIDS"), String.class);
            }
            case 8 -> {
                return Runtime.castToRef(languages.ADL("PARENTS"), String.class);
            }
            case 9 -> {
                return Runtime.castToRef(languages.ADL("MASTERS"), String.class);
            }
            case 10 -> {
                return Runtime.castToRef(languages.ADL("* FINAL *"), String.class);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + level);
        }
    }

}
