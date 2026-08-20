### Additions
- Added "Attachment" Modifiers
  - The difference between Attachments Modifiers and Modifier Modifiers is that attachments render on the gun, and generally create a right-click use
  - Added Scope Attachment Modifier
    - Grants spyglass-style zoom on use, and -1 Spread
  - Added Bayonet Attachment Modifier
    - Grants Spear Charging on use
    - Stats similar to Iron Spear
- Added Underwater Drag

### Changes
- Nerfed Frozen Shrapnel count from +5 to +3
- 

### Fixes
- Fixed ricochet ordering to prevent Frozen Jacket from shooting the ground with other Ricochet Modifiers
- Fixed Blackpowder Charge and Breaching Shell only displaying block break progress for one block per explosion
- Fixed Blackpowder Charge not doing terrain damage if the bullet completely destroyed the block it hit
- Fixed movement penalty not being tracked correctly on the server
- Fixed spectators being able to shoot and reload
- Fixed "Hand Occupancy", the system deciding when to show/hide offhand based on whether your gun is using two hands
  - Now also prevents offhand item usage when the gun is two-handed

### API
- 

