# BlockSurvival
https://www.spigotmc.org/resources/minigame-blocksurvival.52667/

Here is a fork with the addition of maven in the project for downloading the necessary resources.

![Title](https://www.spigotmc.org/attachments/header-png.315324/)

BlockSurvival is quick little mini-game where blocks inside of an Arena will start to disappear one by one. The goal of the players is to be the last one standing!

![Features](https://www.spigotmc.org/attachments/features-png.315327/)

* Very easy to setup arenas with detailed feedback on command use.
* Multiple arenas (and supports Multi-world plugins such as Multiverse).
* Automatically restores arenas.
* Arenas can have any number of players (except below 2!)
* No dependencies required and is very lightweight.
* Multiple "JOIN" signs supported for arenas.
* **Free**

![Commands](https://www.spigotmc.org/attachments/commands-png.323721/)

```
=======================================
Arena Setup Commands
=======================================
/fallingblock setmainlobby - set current player position as the return point when a game finishes or player exits an arena.
/fallingblock status <arena> - check the setup status of an arena.

/fallingblock create <arena> - creates an arena.
/fallingblock delete <arena> - deletes an arena.

/fallingblock enable <arena> - enables an arena.
/fallingblock disable <arena> - disables an arena.

/fallingblock pos1 <arena> - set position #1 of arena.
/fallingblock pos2 <arena> - set position #2 of arena.

/fallingblock setminplayers <arena> <amount> - set the minimum number of players for the arena.
/fallingblock setmaxplayers <arena> <amount> - set the maximum number of players for the arena.
```

![Tutorial](https://www.spigotmc.org/attachments/tutorial-png.315326/)

1. Set main lobby using "/blocksurvival setmainlobby"
2. Create an arena using "/blocksurvival create <arena>"
3. OPTIONAL: Create lobby for the arena using "/blocksurvival lobby <arena>"
4. Set position #1 of arena using "/blocksurvival pos1 <arena>"
5. Set position #2 of arena using "/blocksurvival pos2 <arena>"
6. Set minimum number of players using "/blocksurvival setminplayers <arena> <amount>"
7. Set maximum number of players using "/blocksurvival setmaxplayers <arena> <amount>"
8. Set the difficulty / speed of the arena using "/blocksurvival difficulty <arena> <0~10>"
9. If you want a player to be eliminated when they drop below a certain height use "/blocksurvival floor <arena>" to set your current height as the elimination height.
10. OPTIONAL: If you aren't going to use a elimination height, you can alternatively make the player be eliminated when taking VOID or LAVA damage. This can be done by doing "/blocksurvival death <arena> <type:NONE/VOID/LAVA>"
11. Join signs can be created using the format:
```
Line 1: [bs] OR [BlockSurvival]
Line 2: JOIN
Line 3: <Arena>
```
12. Leave signs can be created using the format:
```
Line 1: [bs] or [BlockSurvival]
Line 2: LEAVE
```
