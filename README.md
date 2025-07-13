# ChaosCastle

ChaosCastle is a simple, retro-like, 2D shoot-them-up game with a slow pace.

It starts with very simple levels. But as the game progress, the difficulty increases. Ultimately, you get boss fights.

The game was initially released for the Amiga and Macintosh computers in 1999. This is a Java-port that runs on any modern hardware, and is very close to the original version (see **History** below).

Because this is an almost unmodified port of an old game, please read the instructions carefully (especially **Controls** and **Weapons**), because the game is far from having the conviviality and ease-of-use of your typical Smartphone game.

Oh, and there is no download yet. You have to compile the game from the sources (see ** Compiling**).


## Screenshots

The game has simple graphics. For instance, this is the main character:

[<img src="./ChaosCastle/src/ch/chaos/castle/app/App64.png">]

And this is a screenshot of the "Garden" level of the "Castle" zone:

TODO

Do not like the graphics? then sorry this game is not for you. If on the other hand you are interested by the gameplay rather than by the graphics (or if you like retro games), you might be interested.


## Compiling

To compile and run ChaosCastle, you must be familiar with the Command Prompt (Windows) or Terminal (Linux / macOS).

First you need to install:

- A Java JDK version 17 or greater
    - You need a JDK and not just a JRE!
    - The `JAVA_HOME` environment variable should properly locate the JDK's installation directory. If this environment variable is not properly created during the installation, you may need to create it manually.
    - On Debian-based Linux, this can be done using `sudo apt install openjdk-17-jdk`
    - On Windows and macOS, the JDK can be downloaded from www.adoptium.net
- Git
    - On Debian-based Linux, `sudo apt install git`
    - On other systems, https://git-scm.com/downloads
    
Then open a Command Prompt or Terminal.

- Change the current directory to some new empty directory
- Get the sources of the Modula-2 to Java translator, using `git checkout https://github.com/Tachyon-Sonics/Modula2Java17`
  - While the game is already translated to Java, you will still need the small Modula-2 runtime that comes with the compiler
- Checkout the latest stable release: TODO
- Get the sources of the ChaosCastle game: `git checkout https://github.com/Tachyon-Sonics/ChaosCastle`
- Checkout the latest stable release: TODO
- Compile it and create an executable .jar file:
    - On Windows: `gradlew.bat jar`
    - On Linux or macOS: `./gradle jar`, or `sh ./gradle jar` if the former does not work.
    - This takes quite a time, and this will also download a few libraries used by the game. This is where most of the magics happens, and also where problems are most likely to occur...
- If everything went well and without errors, the file `ChaosCastle.jar` can be found in the `ChaosCastle\build\libs` directory (relative to the current one). This is the only file you need to run the game. You can now place it somewhere else where it is easier to find. It still needs a Java JRE version 17 or more to run (which you already have as you installed a Java JDK (that includes a JRE) to compile the game). Also note that there is currently no way to do a "installation"; the game is just a portable, single file. Note that the `ChaosCastle.jar` file is 100% cross-platform: it can be used on other operating systems (Windows, Linux, macOS) as long as they have a Java JRE version 17 or greater installed.



## Starting the game

TODO: cmd, launcher, in-game menu

## Goals

The game has three "zones", each with several levels. The zones are "Chaos", "Castle" and "Family".

You start in the "Chaos" zone at level 1. The goal of any level in this zone is to destroy everything and to collect $. Between every level, you enter the **shop**. Each time you have collected 100$, you can play a level of the "Castle" zone (by buying it in the shop); else you have to continue with the next level of the "Chaos" zone.

In the "Castle" zone, the goal of any level is to collect both $ and £, and to find the "EXIT" panel. Levels in the "Castle" zone are big levels with scrolling. Each time you have collected 150£, you can play a level of the "Family" zone (by buying it in the shop).

In the "Family" zone, each level is basically a fight against a boss.

Note that levels are cycling in every zones. Once the last level of a zone has been done, it restarts with the first one. As such the game never finishes. However, it takes several hours of playing until you can reach the last ("\*\*\* Final \*\*\*") boss in the "Family" zone. Note that there is an in-game menu (use the right-mouse button to show it) that allows you to load and save games, when you are in the shop between levels.

During the game, you may eventually find "Skull" bonuses (and some of them cannot be avoided). Each time you get a skull bonus, the overal difficulty of the game increases (displayed in blue on the top of the right-panel).

When the difficulty increases, the levels do not fundamentally change, but subtle difficulties are introduced:
- New aliens
- New stuff
- New passages in some "Castle" levels
- Aliens are globally harder to kill
- Stuff that was intially friendly become hostile
- etc.

In addition to the "Chaos", "Castle" and "Family" levels, there are:

- Bonus levels. To enable a bonus level you must find a diamond bonus.
- A mysterious "PMM". Just read the help texts in the "[?]" panels that are hidden in various levels, and you may eventually find out how to enable it. Note that the "PMM" is not particularly impressive, but it will help you a lot in understanding some "Castle" levels.
- Weapons that are more efficient that the standard "Gun", and bonuses to add power and bullets to them. See the **Weapons** section below.

There are 100 levels in the "Chaos" zone, 20 levels in the "Castle" zone and 10 levels (boss) in the "Family" zone.

Levels in the "Castle" zone are procedurally generated. This means that you never play exactly the same level twice, although each level typically has a distinct "feel", based on the procedural algorithm used to generate it. Many "Castle" levels may also appear with a different random orientation / rotation each time you play them.


## Controls

## Weapons


## History

### The initial Amiga version - 1998

ChaosCastle was initially written in 1998 - 2000. It was written in the Modula-2 programming language for the Amiga range of computers. Several choices were made, that were not common at that time:

- The operating system's libraries were always used instead of hitting the hardware directly.
- No hardware-specific feature was used. For instance, hardware scrolling and hardware sprites (features of the Amiga computers of that time) were not used, although it was possible to use them through the operating system's libraries.
- The operating system dependent parts of the code (graphics, input, sounds) were all abstracted in Modula-2  _definition modules_  (similar to .h header files in C, or interfaces in Java). The idea is that porting the game to another platform could be done by rewriting the implementation of these modules only. These modules were what I called the "Library" (think of it as a very simplified, cross-platform 2D game engine). Apart from the implementation of the "Library", the remainder of the code is entirely portable.
- Backgrounds used bitmap images, and sprites used vector graphics.
- The game used a base resolution of 320x240, and featured an integer "scaling" factor. With a scaling factor of 2, it could run in 640x480 resolution (if supported), with a scaling factor of 3 in 960x720, etc.
- Both the background images and the sprite images were pre-rendered at the target scaling factor during startup. Hence no scaling or vector graphics (which were both quite slow on hardware of that time) occured during the game.
- The game did not rely on a fixed FPS such as 50 FPS. Instead it used a clock to measure how much time passed between two frames, and ajusted the speed accordingly. Hence, even if the FPS dropped down to 4 FPS, the overal speed of the game did not slow down.
    - Note however that the game did not use a separate game loop and rendering loop. Hence at low FPS, some collisions were less accurate.
- Sound effects supported 1 to 8 channels, and either mono or stereo. They used either the built-in audio device, or the third-party AHI library. Sounds could also be disabled altogether.

The main result of these choices is that, on an Amiga 500 (one of the earliest model in the Amiga range of computers, featuring a Motorola 68000 processor), the game could not achieve 50 FPS (the typical screen refresh rate at that time) at all. It was rather running at 16 - 25 FPS, with drops down to 4 FPS under heavy animations. At that time, commercial games with similar animations all ran at 50 FPS on the same hardware, thank to the use of hardware scrolling, and more generally by hitting the hardware directly.

The game also could only use 4 mono channels or 2 stereo channels for sounds, which was what the Amiga hardware provided.

There was, however, interesting results (and that was the goal of the choices above):
- On more recent hardware, such as the Amiga 1200, 50 FPS could be achieved at 320x240
- With dedicated graphic cards, 50 FPS could be achieved even at 640x480 (scaling x2) or higher resolutions. No additional code was necessary to use dedicated graphic card as they were properly accessed through the operating system libraries.
- With fast enough processors, such as the 68030 or 68060, sounds could use 8 stereo channels with the AHI library
- The game could be ported without difficulties to the Macintosh range of computers


### The Macintosh and other ports - 2000

By rewriting the system-dependent modules (the "Library"), a Macintosh port was successfully completed. Because of the lack of hardware dedicated to graphics, models with at least a 68020 processor were recommanded. Interestingly, the game had a "black&white" option in addition to the "16 colors" mode, and in this mode the game could even run on very old, black&white Macintosh computers. While the Amiga version ran in fullscreen mode, the Macintosh port was windowed.

An Atari ST port was almost completed, but was never released. It was playable, but still had unresolved bugs, making it unstable. It ran only in 16 colors at 320x240 (scaling x1), and with no sounds.

A Linux port was started, with graphics based on X11.

At that time I also bought a licence to the MHC Modula-2 to Java compiler in order to start a Java port.

However, at that time I lost interest in the project, and this was the end of the "old" ChaosCastle years. Only the Amiga and Macintosh versions were ever released.


### The void years - 2000 - 2024

During many years, I did absolutely nothing on the ChaosCastle game because I was working on other projects, in the Java language. I also stopped using Modula-2, which never got a mainstream programming language.

I still very ocasionnaly played ChaosCastle, mostly using the UAE Amiga emulator.

Recently, playing a complete game with the UAE emulator, I got frustrated by different things:

- While the game featured a scaling factor, I was not able to use it properly (without interlacing) with UAE. It involved installing an UAE-specific gfx driver, but documentation is near zero and my knowledge of the Amiga computer vanished after all those years. I was never able to do it although it should definitely be possible; after all, UAE even emulates the exact Amiga graphic card I had on my Amiga 1200.
- Altough it was possible to achieve 50 FPS (at 320x240 resolution) by tweaking some UAE settings, for some reason scrolling in the game was always jaggy. I blamed the UAE emulation.


### The Java version - 2024 - 2025

#### Modula-2 to Java translation

I recently remembered that I bought an MHC licence (a Modula-2 to Java translator) at that time, and that I could use it to create a proper Java port. In theory, I could then solve the two problems above that got me frustrated. Unfortunately, by digging for several hours in all my backups, I could not find the MHC licence. It was probably lost when I trashed my old Amiga and Macintosh computers. I still found an unlicenced MHC version, but it was way too limited. That was another frustration. I also searched for other existing Modula-2 to Java converter, but found none that was suitable. As a Java guy I also did not want to go the C/C++ route (although I found the excellent and fully working XDS Modula-2 to C converter).

Then I had this weird idea: what if I wrote my own Modula-2 to Java converter?

The idea was not so weird after all: I had compiler courses. I also worked on Eclipse plugins involving complex Java to Java code transformations (refactoring); and I had a basic knowledge of the antlr parser library.

Don't get me wrong, it still took the equivalent of *several weeks* at full time to finalize the Modula-2 to Java translator. You can find it at ([www.github.com/Modula2Java17/Modula2Java17](https://github.com/Tachyon-Sonics/Modula2Java17)) if you are interested. And even if it could successfully convert the whole game:

- The translator probably still has many bugs when used on other Modula-2 programs, as my own game was the only "big" Modula-2 program on which I tested it
- The resulting game did not run because it used pointer arithetic at some places, which could not be converted in Java. I had to manually modify the resulting Java code.
    - I still plan to improve the Modula-2 to Java translator to handle pointer arithmetic in the future
- I had to "fix" a few non portable stuff in the original Modula-2 code, but I could keep it to a minimum.
- I still had to rewrite the operating system dependent parts in Java (the "Library"): graphics, sounds, etc.

The last point tooks a few additional weeks until the Java port of the game was finally playable.

Believe it or not:

- At that time I finally found my MHC licence hidden deep in some of my old external disks (!). But I decided to continue with my own compiler...
- Even at 60 FPS, scrolling was still jaggy on the Java version. I finally found that the problem was in the original code (and hence UAE was not responsible for it)
    - More precisely, the game tried to adjust to the actual frame rate in case it dropped below 60 FPS
    - However, instead of counting the number of missed frames during vertical sync, it was done while moving the player. Unefortunately this could happend at any time during the game loop, because there was no guarantee that the player is the first or last sprite.
    - Furthermore, it queried and deduced the value from an independent 300 Hz clock, potentially resulting in fractional missed frames.
- Hopefully, I could fix the jaggy scrolling issue without touching the initial code, by locking the 300 Hz clock to the vertical sync in their Java implementation.
- Another problem is that Java's `Thread.nanoSleep` method is not as accurate as it may seem: it may sleep up to a few milliseconds more than requested (which is significant, as 60 FPS requires 16.6 milliseconds of sleeping). This also introduces jaggy scrolling, but from the Java side this time. To fix the problem I had to create an "accurate sleeper" class that combines sleeping (to prevent 100% CPU uage and battery drain) and active waiting...
    - Note that scrolling is still not as good as it can be. The reason is that it is still limited to unscaled pixels. So when the game is scaled by a factor 4 for instance, it will only scroll by multiples of 4 pixels. In a future version I plan to fix this as well.    

    
#### The Java "Library" implementation

As I said earlier, the graphics, sounds and other operating system dependent modules (the "Library") had to be rewritten in Java. Thank to the original design of the game, I could:

- Scale the bitmap images (for the backgrounds) using the high quality xBRZ algorithm. The Java implementation of that algorithm probably does not run in real-time, but this is not important because the scaling occurs only once at game startup. This means that only graphics that are already scaled are used during the game.
- Scale the sprites with high quality, as they are vector-based. Like the bitmap images this is only done once at application startup.
- Implement an audio mixer that allows for 8 stereo channels. Note however that the sounds are still the original 8 bit and low resolution ones (between 5 - 20 kHz only), so do not expect miracles here.

Note that while the game had a "Graphics/Settings" menu which allowed you to choose the scaling factor (among other), in the Java version this setting is completely disabled. In fact the results are better if the original code thinks it is running at x1 scaling, and that scaling is done behind the scene in the Java code. The reason is that the scaling code in the original code did not properly scale the lines. With a x2 scaling this was ok. But with higher scalings, some sprites juste begin to have too thin outlines.

Another reason why I disabled the "Graphics/Settings" is that it provided a black&white mode and a "Color x2" mode (actually a mode with two independent 16 color playfields that scroll at different speeds) in addition to the default 16-color mode. I have not implemented them in Java yet, and I may not implement them ever (black&white really does not have any value today, and the "Color x2" was impressive in the old times, but it doesn't really bring anything to the game).

The original game design also has a few drawbacks:

- The game uses indexed colors (palette graphics). While supported by Java, they are in general *not* hardware optimised. 
- For the same reason I could not use anti-aliasing.
- No floating point was used (it was slow at that time). All arithmetic was integer-based. That means they are a few visible round-off errors, for instance when some bosses (Mother Alien, Master Alien) fire at you or explode.


#### Other changes

While the game was converted from Modula-2 to Java, and the "Library" was rewritten in Java, the "portable" part of the code is still 99% the same as the old Amiga and Macintosh versions.

I just changed a few things that went on my nerves while playing and testing, and all these small changes were done directly in the Modula-2 code. If you know about the original Amiga or Macintosh version you may hence notice a few differences. Here's some of the most important ones:

- The levels of "Family" zone now cost 150£ instead of 100£. I found that "Family" levels were otherwise too frequent.
- Some aliens release less "£" for the same reason
- Less meteorites in the "Chaos" zone
- The order of "Family" levels has slightly changed

BTW if you saved games with the original Amiga or Macintosh version, you should be able to load them in the Java version. The format is compatible!


## Future works

Here's my plans for the future of ChaosCastle (but any of these things may or may not happen - this is a hobbyist project):

- 10 new "Castle" levels. This has already started. Do not expect too much, these levels do not use new background images or new enemies / sprites. However:
    - They have much more animation (sprites). The original game was limited to 256 sprites, I raised this limitation to 1024 in the Java version and took advantage of it in the new levels. Believe it or not, this changes the gameplay in a significant way.
    - They feature much better procedural generation algorithms. At the old times I wrote the original version for the Amiga computers, I remember being quite frustrated by the results, most levels did not feel "random" enough.
- A real ending
- Replace all the sounds with high quality versions, for instance from the freesounds site.
- Create a Java rendering engine that uses RGB colors so it can be hardware accelerated. This will probably involve changes in the original portable part of the game code to preserve the color animations.
- Improve the Modula-2 to Java translator that was used to create this Java port.
- Better graphics, but probably not from me. I still plan to at least convert them to standard formats like PNG (bitmaps) and SVG (vector graphics) so it can be easier for other interested people to contribute.
- Add background musics. There are various sites with authors of "free" musics that can be used for that purpose.
- Further improve scrolling, which is still not as smooth as it can be because of the way scaling is implemented.
- Replace all integer-based arithmetic by floating point arithmetic.
- Replace in-game menu and dialogs (combining swing components with double-buffered, full-screen graphics does not work well in Java; it's surprising it could even be done).
- Fixes (The "Factory" level for instance rarely, but sometimes, has an unreachable exit)

Here's my **NOT** plans, basically the things I do *not* want to change:

- The game is 100% free and I want to keep it that way. It is in fact now also open source.
- The game does not require any internet connection
- The game has absolutely no ad, and no in-app purchase
