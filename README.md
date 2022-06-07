# LaBoulangerieMmo

This plugin supports minecraft 1.18 and might partially support other versions.
You can download the plugin [here.](https://github.com/LaBoulangerie/LaBoulangerieMmo/releases/latest)

## BetonQuest integration

LaBoulangerieMmo provides some new conditions and events for BetonQuest.

All LaBoulangerieMmo related keywords are prefixed with `lbmmo_`.

- Check if a player has more than a required level: `lbmmo_level <talent> <level>`
> `lbmmo_level mining 50` verifies if the player's mining level is greater or equal to 50.

- Give or take xp: `lbmmo_xp <talent> <level>`
> `lbmmo_xp mining +50` will give to the player 50 xp in the mining talent

> `lbmmo_xp mining -50` will take from the player 50 xp in the mining talent

## Building from sources

THis project uses [Gradle](https://gradle.org/) as its build system, once you have Gradle installed you can build the project in cloning the project and then, inside the project's folder, open your terminal and type `> gradlew assemble` if your are on Windows and `$ chmod +x ./gradlew` the first time you build it on Unix-like systems and then `$ ./gradlew assemble`.

The generated JARs are located in `build/libs/`, the one you should use is `LaBoulangerie-X.X.X-jar`.

## API

### Leaderboards

LaBoulangerieMmo provides you with an API to create leaderboards that will be displayed on minecraft maps.
This API takes care of all the headaches related to managing the maps, it creates the maps, deletes them stores them between restarts and provides you with a way to update them.

The API supports combining multiple maps together to create a single leaderboard.

You can access the API from anywhere in your plugin by calling `LeaderBoardManager.getInstance()`.

Creating a leaderboard:

```java
List<Integer> mapsId = LeaderBoardManager.getInstance().createLeaderBoard(pretenders, title, unit, width, height)
```

- `HashMap<String, Double> pretenders` is a map where the key is a "pretender" or an element of your leaderboard and the value is the amount that will be used to sort it. (example: key: "Bob", value: "10.9")
All the pretenders will be sorted and displayed in descending order.

- `String title` will be displayed at the top of the map.
- `String unit` is the unit of the value used to sort the pretenders, it will displayed after value of each pretender.
- `int width` defines how many maps are combined vertically in order to display the leaderboard. (must be between 0 and 10)
- `int height` defines how many maps are combined horizontally in order to display the leaderboard. (must be between 0 and 10)

`List<Integer> mapsId` is containing the id of all the maps used in this leaderboard you will need them if you want to update or delete the leaderboard and if you want to give them to a player.

You can do so with:
```java
mapsId.stream().forEach(id -> player.getInventory().addItem(LeaderBoardManager.getInstance().getMapItem(id)));
```

If you want to update your leaderboard with an updated list of pretenders you do:

```java
mapsId.stream().forEach(id -> player.getInventory().addItem(LeaderBoardManager.getInstance().updateMap(id, newPretenders)));
```
- `HashMap<String, Double> newPretenders` is the new map of pretenders.
- `Integer id` is the id of one map of the leaderboard, each will remember which part of the pretenders it has to display.

Finally you can delete your leaderboard by marking all its maps as "free":
```java
mapsId.stream().forEach(id -> player.getInventory().addItem(LeaderBoardManager.getInstance().freeMap(id)));
```

**/!\\** The method LeaderBoardManager#freeAllMaps() is intended for **debugging** only and shouldn't be called, if you really need it, use the command `/mmo leaderboards freeAllMaps`.
