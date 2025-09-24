package ch.chaos.tools;

import ch.chaos.castle.level.Levels;
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
            case Levels.SPACE_STATION -> {
                return Runtime.castToRef(languages.ADL("Space Station"), String.class);
            }
            case Levels.PIPELINE -> {
                return Runtime.castToRef(languages.ADL("Pipeline"), String.class);
            }
            case Levels.SILENT_VOID -> {
                return Runtime.castToRef(languages.ADL("Silent Void"), String.class);
            }
            case Levels.SNOW_TRACK -> {
                return Runtime.castToRef(languages.ADL("Snow Track"), String.class);
            }
            case Levels.CITY_PARK -> {
                return Runtime.castToRef(languages.ADL("City Park"), String.class);
            }
            case Levels.BIG_CAVERN -> {
                return Runtime.castToRef(languages.ADL("Big Cavern"), String.class);
            }
            case Levels.TWILIGHT -> {
                return Runtime.castToRef(languages.ADL("Twilight"), String.class);
            }
            case Levels.POND -> {
                return Runtime.castToRef(languages.ADL("Pond"), String.class);
            }
            case Levels.ELECTRICAL_PLANT -> {
                return Runtime.castToRef(languages.ADL("Electrical Plant"), String.class);
            }
            case Levels.JUNKYARD -> {
                return Runtime.castToRef(languages.ADL("Junkyard"), String.class);
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
        if (GameSimulator.NEW_MODE) {
            String name = switch (level) {
                case 1 -> "Brother Alien";
                case 2 -> "Sister Alien";
                case 3 -> "Mother Alien";
                case 4 -> "Father Alien";
                case 5 -> "Brother Alien";
                case 6 -> "Sister Alien";
                case 7 -> "Mother Alien";
                case 8 -> "Father Alien";
                case 9 -> "Kids";
                case 10 -> "Parents";
                case 11 -> "Brother Alien";
                case 12 -> "Sister Alien";
                case 13 -> "Mother Alien";
                case 14 -> "Father Alien";
                case 15 -> "Kids";
                case 16 -> "Parents";
                case 17 -> "Master Alien";
                case 18 -> "Master Alien 2";
                case 19 -> "Brother Alien";
                case 20 -> "Sister Alien";
                case 21 -> "Mother Alien";
                case 22 -> "Father Alien";
                case 23 -> "Kids";
                case 24 -> "Parents";
                case 25 -> "Master Alien";
                case 26 -> "Master Alien 2";
                case 27 -> "Masters";
                case 28 -> "* FINAL *";
                default -> throw new RuntimeException("Unhandled CASE value " + level);
            };
            return Runtime.castToRef(languages.ADL(name), String.class);
        } else {
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
                    return Runtime.castToRef(languages.ADL("KIDS"), String.class);
                }
                case 6 -> {
                    return Runtime.castToRef(languages.ADL("PARENTS"), String.class);
                }
                case 7 -> {
                    return Runtime.castToRef(languages.ADL("MASTER ALIEN"), String.class);
                }
                case 8 -> {
                    return Runtime.castToRef(languages.ADL("MASTER ALIEN 2"), String.class);
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

}
