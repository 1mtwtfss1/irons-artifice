### Changes
- Reworked fire delay handling to reduce network jitter that could cause stuttering, especially on laggy connections.
- Fire Rate is now displayed in the tooltip at the effective firerate instead of the raw numerical rate

### Fixes
- Fixed Bullets not firing projectile impact event

### API
- `GunplayManager#tryFire` now returns an enum `FireOutcome` instead of a boolean
  - Contains additional context for why the shot failed, or fired.
- Fire Delay is now an entity attachment, not an item component
- `GunItem#currentOccupancy` now must take an entity
- Reworked `AnimationAdjuster`
  - No longer an enum, but an interface. Can now be implemented by addons.
  - `GunProfile` now takes a list of animation adjusters, instead of only one
  - Removed `NONE` animation adjuster