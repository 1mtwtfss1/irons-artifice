### Changes
- Reworked fire delay handling to reduce network jitter that could cause stuttering, especially on laggy connections.
- Fire Rate is now displayed in the tooltip at the effective firerate instead of the raw numerical rate

### API
- `GunplayManager#tryFire` now returns an enum `FireOutcome` instead of a boolean
  - Contains additional context for why the shot failed, or fired.
- Fire Delay is now an entity attachment, not an item component
- `GunItem#currentOccupancy` now must take an entity