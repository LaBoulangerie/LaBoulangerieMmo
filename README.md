# LaBoulangerieMmo

## BetonQuest integration

LaBoulangerieMmo provides some new conditions and events for BetonQuest.

All LaBoulangerieMmo related keywords have the suffix `lbmmo_`.

- Check if a player has a more than a required level: `lbmmo_level <talent> <level>`
> `lbmmo_level mining 50` verifies if the player's mining level is greater or equal to 50.

- Give or take xp: `lbmmo_xp <talent> <level>`
> `lbmmo_xp mining +50` will give 50 xp in the mining talent to the player

> `lbmmo_xp mining -50` will take 50 xp in the mining talent from the player
