# ChaosCastle

ChaosCastle is a simple, retro-like, 2D shoot-them-up game with a slow pace.

It starts with very simple levels. But as the game progress, the difficulty increases. Ultimately, you get boss fights.

The game was initially released for the Amiga and Macintosh computers in 1999. This is a Java-port that runs on any modern hardware, and is very close to the original version (see **History** below).

Because this is an almost unmodified port of an old game, please read the instruction carefully (especially **Controls** and **Weapons**), because the game is far from having the conviviality and ease-of-use of your typical Smartphone game.

Oh, and there is no download yet. You have to compile the game from the sources (see ** Compiling**).


## Screenshots

## Compiling

## Starting the game

TODO: launcher, in-game menu

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
