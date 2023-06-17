# LaBoulangerieMmo

This plugin supports minecraft 1.19 and might partially support other versions.
You can download the plugin [here.](https://github.com/LaBoulangerie/LaBoulangerieMmo/releases/latest)

## Concept

LaBoulangerieMmo is a plugin in the spirit of McMmo, it provides you with "talents" (professions) that give you access to "abilities" (skills) when you have acquired enough experience in the talent.

Players don't need to choose only one talent, they can grind them all!

On top of that, LaBoulangerieMmo lets other plugins create new talents and abilities via its [API](#api) and server owners can also create and delete talents through the plugin's configuration file.

How do you gain experience you might ask? There are lot of ways! Depending on the talent, you can get xp by breaking blocks, killing entities, crafting or discovering a recipe. All of that can be configured!

## BetonQuest integration

LaBoulangerieMmo provides some new conditions and events for BetonQuest.

All LaBoulangerieMmo related keywords are prefixed with `lbmmo_`.

-   Check if a player has more than a required level: `lbmmo_level <talent> <level>`

    > `lbmmo_level miner 50` verifies if the player's miner level is greater or equal to 50.

-   Give or take xp: `lbmmo_xp <talent> <level>`
    > `lbmmo_xp miner +50` will give to the player 50 xp in the miner talent

> `lbmmo_xp miner -50` will take from the player 50 xp in the miner talent

## MythicMobs integration

You can set MythicMobs-specific xp values by prefixing `MYTHICMOBS_` to the mob's uppercased name.

Example: if you want to set 100xp for a MythicMobs mob with identifier `capitaine_zombie`, it will be:

```yml
MYTHICMOBS_CAPITAINE_ZOMBIE: 100
```

## Building from sources

This project uses [Gradle](https://gradle.org/) as its build system, once you have Gradle installed, follow the steps below:
Clone the project then, go into the project'sfolderby typing `> cd LaBoulangerieMmo` next, type `> gradlew assemble` if your are on Windows.
If you are using Linux or macOS, type the following in your terminal the first time you try to build the project `$ chmod +x ./gradlew` then, type `$ ./gradlew assemble`.

The generated JARs are located in `build/libs/`, the one you should use is `LaBoulangerie-X.X.X-jar`.

## API

Our API is distributed as a maven package, you can find out how to add it to your maven project [here.](https://github.com/LaBoulangerie/LaBoulangerieMmo/packages/1356101)

### Leaderboards

> **/!\ This is still in a very early stage**

LaBoulangerieMmo provides you with an API to create leaderboards that will be displayed on minecraft maps.
This API takes care of all the headaches related to managing the maps, it creates and deletes them stores them between restarts and provides you with a way to update them.

The API supports combining multiple maps together to create a single leaderboard.

You can access the API from anywhere in your plugin by calling `LeaderBoardManager.getInstance()`.

Creating a leaderboard:

```java
List<Integer> mapsId = LeaderBoardManager.getInstance().createLeaderBoard(pretenders, title, unit, width, height)
```

-   `HashMap<String, Double> pretenders` is a map where the key is a "pretender" or an element of your leaderboard and the value is the amount that will be used to sort it. (example: key: "Bob", value: "10.9")
    All the pretenders will be sorted and displayed in descending order.

-   `String title` will be displayed at the top of the map.
-   `String unit` is the unit of the value used to sort the pretenders, it will displayed after value of each pretender.
-   `int width` defines how many maps are combined vertically in order to display the leaderboard. (must be between 0 and 10)
-   `int height` defines how many maps are combined horizontally in order to display the leaderboard. (must be between 0 and 10)

`List<Integer> mapsId` is contains the id of all the maps used in this leaderboard, you will need them if you want to update or delete the leaderboard or if you want to give them to a player.

You can do so with:

```java
mapsId.stream().forEach(id -> player.getInventory().addItem(LeaderBoardManager.getInstance().getMapItem(id)));
```

If you want to update your leaderboard with an updated list of pretenders, you can do:

```java
mapsId.stream().forEach(id -> player.getInventory().addItem(LeaderBoardManager.getInstance().updateMap(id, newPretenders)));
```

-   `HashMap<String, Double> newPretenders` is the new map of pretenders.
-   `Integer id` is the id of one map of the leaderboard, each will remember which part of the pretenders it has to display.

Finally you can delete your leaderboard by marking all its maps as "free":

```java
mapsId.stream().forEach(id -> LeaderBoardManager.getInstance().freeMap(id));
```

**/!\\** The method LeaderBoardManager#freeAllMaps() is intended for **debugging** only and shouldn't be called, if you really need it, use the command `/mmo leaderboards freeAllMaps`.
