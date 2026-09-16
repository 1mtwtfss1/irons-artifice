### Additions
- Added `irons_artifice:guns` tag
- Added `irons_artifice:ammo` tag
  - Any item in the `irons_artifice:ammo` item tag can now be loaded into guns and counts toward the reserve ammo HUD. Bullets are in this tag by default.
  - Dev Note: Currently no support for delineating which stacks where chosen when loaded into a gun

### Changes
- Reworked fire delay handling to reduce network jitter that could cause stuttering, especially on laggy connections.
- Fire Rate is now displayed in the tooltip at the effective firerate instead of the raw numerical rate
- Clockwork Rifle damage 6 -> 7

### API
#### Addon support
- All `GunItems`s, including addons, are now automatically registered with a Gun renderers and arm pose
- Added item tags `irons_artifice:guns` and `irons_artifice:ammo` (`IronsArtificeTags.GUNS`, `IronsArtificeTags.AMMO`)
- Added `api.GunBones`: named constants for every bone the client helpers look up. Use these in your own models if you weren't already.
- Added `api.GunAnimations`: named constants for the animation names (`idle`, `fire`, `reload`, `equip`) and controller names the gunplay system triggers. Use these in your own animation files if you weren't already.
- `GunInHandRenderer` render hooks (`handleAttachmentRendering`, `handleMuzzleFlashEmission`, `handleFirstPersonHandRendering`, `handlePerspectiveAdjustments`, `handleGunAdjustments`, `silenceAnimations`, and friends) are now `protected` so the renderer can be more easily subclassed.
- `IronsArtificeClient.armPoseExtension` is now public.
- Datagen helpers are now reusable: `ItemModelDataGenerator.gunModel(...)` and `generateTemplatedItem(...)` are public static, `GECKOLIB_GUN_DISPLAY` / `REVOLVER_GUN_DISPLAY` are public, and `RecipeDataGenerator` has a public constructor.
- The published artifact now includes a sources jar.

#### Gun definitions
- **Breaking:** `GunProfile` is now a final class instead of a record; the positional constructor is gone. Construct via `GunProfile.builder(magazineCapacity, modifierSlots, reloadTimeTicks, fireMode, armPoseKind, components)` and the optional setters `topLoadConfig`, `reloadCues`, `equipSound`, `fireCycleCues`, `animationAdjusters`, `occupancy`. Accessor names are unchanged, so readers of a profile do not need to change. Future fields will be added to the builder without breaking existing callers.
- **Breaking:** occupancy overrides are keyed by the new `GunState` enum (`RELOAD`, `FIRE`) instead of raw strings. `GunProfile#occupancyFor` now takes a `GunState`.
- Added `ShotComponentTemplate`, a `Supplier<ShotComponentMap>` that builds a fresh, identical map on every call. Build one with `ShotComponentTemplate.builder(damage, spread, characterBlowback, fireDelayTicks, cameraRecoil)`; projectile count, bullet speed, gravity, and knockback have defaults and can be overridden. Convenience methods cover gunshot sounds and muzzle flash; `set(type, factory)`, `value(type, base)`, and `modify(step)` cover everything else. All built-in guns now use it, and `GunProfile.defaultComponents()` has been removed.

#### Shot profiles
- **Breaking:** `ShotProfile#get` has been renamed to `ShotProfile#peek`. This is to clarify that returned value is not stored when the component is absent, so mutating it was a silent no-op
- Added `ShotProfile#modify(type, consumer)` to modify components in one line
- Added `ShotProfile#modifyValue(type, ValueModifier)` and `ShotComponentMap#modifyValue(type, ValueModifier)`, a helper to `modify` components typed as values with a `ValueModifier`
