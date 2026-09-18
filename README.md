[<img alt="Curse Forge" src="https://cf.way2muchnoise.eu/1650193.svg?badge_style=flat"/>](https://www.curseforge.com/minecraft/mc-mods/irons-artifice)
<a href="https://discord.gg/TRzEdrndM2"><img src="https://img.shields.io/discord/1104430139275743293.svg?label=&amp;logo=discord&amp;logoColor=ffffff&amp;color=7389D8&amp;labelColor=6A7EC2&amp;style=for-the-badge" alt="" width="129" height="28" /></a>

![alt text](https://media.forgecdn.net/attachments/1867/330/iaa_title-png.png)

# Iron's Arms 'n Artifice

[Curseforge Page](https://www.curseforge.com/minecraft/mc-mods/irons-artifice)

## General

If you love the mod and would like to help support its ongoing development consider becoming a patron

<a href="https://www.patreon.com/Iron431"><img src="https://shields.io/badge/-Patreon-f86754?style=for-the-badge&amp;logo=patreon&amp;logoColor=white" alt="" width="106" height="28" /></a>
<a href="https://bmc.link/iron431"><img src="https://shields.io/badge/-Buy%20Me%20a%20Coffee-FFDD00?style=for-the-badge&amp;logo=buymeacoffee&amp;logoColor=white" alt="" width="162" height="28" /></a>

## Basic Documentation
### Guns
Guns are items, and should be registered from the `GunItem` class. `GunItem`s hold a `GunProfile`, which is built from the `GunProfile.Builder`, and defines the properties of the gun: magazine capacity, reload time, reload or fire cycle sound effects, and the "shot components".

Shot Components, held in the `ShotComponentMap` (builder via `ShotComponentTemplate`) hold the information about what the gun shoots -- think attributes, but for bullets. Damage, Spread, Fire Rate, Recoil, Muzzle Flash, etc. Shot components are keyed via by `ComponentType<T>`.

Default shot components types are in the `ShotComponents` class. New shot components can be created by simply creating a new key, and wiring its functionality. Keys must give a default value.

Gun Items are automatically registered with a Geckolib renderer and model, and use the item's registered name for resource lookups (i.e. `<namespace>/geckolib/animations/item/<item_name>.animation.json`).

### Modifiers
Modifiers affect the shot components of a gunshot. Modifiers have two halves: their item part, and their modifier functionality. The item can be registered from the `ModifierItem` class. Their functionality, passed into the constructor, is an implementation of the `GunModifier` interface.

`ValueStackModifier` is a preset implementation of `GunModifier` for simple value-based modifications -- modifiers that just change numbers.

Modifiers exhibit their functionality by modifying a bullet's shot components via `apply(ShotComponentMap components);`, called any time the `ShotComponentMap` of a gun item is resolved.
Modifiers can also affect the item components of the gun item they get installed into (See the bayonet for example).

Developer's note: Modifiers are designed to be stackable without limits: no limits gun type, stack count, or interaction effects. 
For balance, a single modifier should affect how the gun feels. That being said, modifiers should not be balanced expecting to be stacked -- certainly not up to 5-7 -- lest it be *required that they stack* in order to be effective. 
When well-balanced, modifiers tend to have diminishing returns, or too high of an opportunity cost to make stacking 5-7 of a single one viable.

### Mobs
The attack goal `RangedGunAttackGoal` can be applied to any `Mob`, and will enable if they are holding a gun. Modifiers in their held gun work. The goal has basic navigation, shooting, and bayonet-charging funtionality: Mobs try to keep their distance, strafe, and then stand still for a volley of shots. 
They automatically reload, and attack with a bayonet if equipped and their target gets too close.

All hooks in `GunplayManager`, such as `attemptStartReload`, `compose`, or `tryFire` work for both players and mobs. Implement your own goals if you need!

`IGunslingerMob` is an interface that gives additional hooks that `RangedGunAttackGoal` automatically triggers on various gun-slinging events.

BY DEFAULT, MOB GUN STATS ARE NERFED. This can be controlled via `IGunslingerMob` hooks. As of 1.0.0, mobs get -25% damage, -25% bullet speed, and +3, +2, or +1 spread based on the game difficulty.
See `IGunSlingingMob#applyDefaultMobNerfs`.

### Events
Events are in the `api` package. I will not list them out because I will not maintain README. As of 1.0.0, events give basic hooks for Ammo Consumption, building a Shot's components, and actually firing a gun. 
Several default modifiers (and both the hats) actually use the events (instead of hard-coding their buffs into the mod). Look at them for more info!

All vanilla events still fire for bullets -- entity spawn, projectile impact, living damage, etc. If an Artifice event doesn't support what you need, think outside box, or bring it up in Discord!
