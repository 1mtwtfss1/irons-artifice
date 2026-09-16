### Additions
- Added Suppressor Attachment Modifier
- Added Hook Shot Modifier
- Added Bloodletting Tip Modifier
- Added `irons_artifice:guns` tag
- Added `irons_artifice:ammo` tag

### Changes
- Reworked fire delay handling to reduce network jitter that could cause stuttering, especially on laggy connections.
- Fire Rate is now displayed in the tooltip at the effective firerate instead of the raw numerical rate
- Clockwork Rifle damage 6 -> 7

### Fixes
- Fixed Bullets not firing projectile impact event

### API
#### BREAKING CHANGES
- `GunplayManager#tryFire` now returns an enum `FireOutcome` instead of a boolean
  - Contains additional context for why the shot failed, or fired.
- Fire Delay is now an entity attachment, not an item component
- Hand Occupancy is now keyed by the new `GunState` enum (`RELOAD`, `FIRE`) instead of raw strings. `GunProfile#occupancyFor` now takes a `GunState`.
- `GunItem#currentOccupancy` now must take an entity
- Reworked `AnimationAdjuster`
  - No longer an enum, but an interface. Can now be implemented by addons.
  - `GunProfile` now takes a list of animation adjusters, instead of only one
  - Removed `NONE` animation adjuster
- `GunProfile` is now a final class instead of a record. Constructor is now private, and uses `GunProfile.Builder`
- `ShotProfile#get` has been renamed to `ShotProfile#peek`. This is to clarify that returned value is not stored (so mutating it was a silent failure)
#### Additions
- All `GunItems`s, including addons, are now automatically registered with a Gun renderers and arm pose
- Added item tags `irons_artifice:guns` and `irons_artifice:ammo` (`IronsArtificeTags.GUNS`, `IronsArtificeTags.AMMO`)
  - Currently, no support for delineating which stacks where chosen when loaded into a gun
- Added `api.GunBones`: named constants for every bone the client helpers look up. Use these in your own models if you weren't already.
- Added `api.GunAnimations`: named constants for the animation names (`idle`, `fire`, `reload`, `equip`) and controller names the gunplay system triggers. Use these in your own animation files if you weren't already.
- `GunInHandRenderer` render hooks (`handleAttachmentRendering`, `handleMuzzleFlashEmission`, `handleFirstPersonHandRendering`, `handlePerspectiveAdjustments`, `handleGunAdjustments`, `silenceAnimations`, and friends) are now `protected` so the renderer can be more easily subclassed.
- `IronsArtificeClient.armPoseExtension` is now public.
- Datagen helpers are now reusable: `ItemModelDataGenerator.gunModel(...)` and `generateTemplatedItem(...)` are public static, `GECKOLIB_GUN_DISPLAY` / `REVOLVER_GUN_DISPLAY` are public, and `RecipeDataGenerator` has a public constructor.
- The published artifact now includes a sources jar.
- Added `ShotComponentTemplate`, a `Supplier<ShotComponentMap>` for building shot components (instead of the in-line supplier in the old GunProfile constructor). Build one with `ShotComponentTemplate.builder(damage, spread, characterBlowback, fireDelayTicks, cameraRecoil)`
- Added `ShotProfile#modify(type, consumer)` to modify components in one line
- Added `ShotProfile#modifyValue(type, ValueModifier)` and `ShotComponentMap#modifyValue(type, ValueModifier)`, a helper to `modify` components typed as values with a `ValueModifier`
