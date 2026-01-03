# Alex's Mobs 1.21.1 Port - Changelog

## Bug Fixes

- Fixed armor items (Roadrunner Boots, Crocodile Chestplate, etc.) stacking to 64 instead of 1
- Fixed rattlesnake model segments spinning wildly during animations (getAllParts() was only returning body, not child parts)
- Fixed Komodo Dragons attacking their own children
- Fixed Rocky Chestplate roll ability not working (Citadel entity data not persisting on NeoForge 1.21.1)
- Fixed item tags: `alexsmobs:insect_items`, `alexsmobs:bananas`, `alexsmobs:tiger_breedables`, `alexsmobs:animal_dictionary_ingredient`
- Added convention tags: `c:foods`, `c:raw_meat`, `c:cooked_meat`, `c:raw_fish`, `c:cooked_fish`
- Added fish mobs to `minecraft:not_scary_for_pufferfish` entity tag (lobster, blobfish, terrapin, comb_jelly, cosmic_cod, catfish, flying_fish, mudskipper, triops, devils_hole_pupfish)
- Fixed Froststalker, Tusklin, and Snow Leopard taking freeze damage in powder snow (added canFreeze override)
- Fixed pufferfish puffing up around Alex's Mobs fish (event-based workaround since entity tags weren't loading)
- Added mobs to appropriate entity type tags: `minecraft:undead` (bone_serpent, soul_vulture, skelewag, murmur), `minecraft:arthropod` (fly, crimson_mosquito, lobster, centipede, cockroach, mantis_shrimp, leafcutter_ant, tarantula_hawk, triops), `minecraft:aquatic` (orca, sharks, lobster, blobfish, seal, cachalot_whale, mimic_octopus, giant_squid, sea_bear, fish, terrapin, comb_jelly, mudskipper, stradpole)
- Added proper mining tool tags for blocks: pickaxe (straddlite_block, void_worm_beak/effigy, transmutation_table, rainbow_glass, crystalized_mucus, gustmaker), axe (leafcutter_anthill/chamber, capsid), shovel (sand_circle, red_sand_circle, ender_residue), hoe (sculk_boomer, bison_fur_block/carpet)
- Fixed multi-segment mobs (Anaconda, Bone Serpent, Cave Centipede, Void Worm, Giant Squid) not showing hurt/death animations (MessageHurtMultipart was failing to sync when damage type was empty)
- Fixed beam/rope rendering for Squid Grapple, Vine Lasso, Tiger leash, and Laviathan reins (updated vertex format for 1.21)
- Fixed Transmutation Table dropping itself instead of Nether Star (updated loot table enchantment predicate format for 1.21)
- Fixed armor and tools not being enchantable (added data-driven enchantable item tags for 1.21)
- Fixed potion crash when drinking clinging potion (potions were using unregistered Holder.direct() instead of DeferredHolder)
- Fixed ALL potion effects not working (renamed isDurationEffectTick to shouldApplyEffectTickThisTick for 1.21)
- Fixed Clinging potion effect not pushing player to ceiling (improved ceiling detection logic)
- Fixed Froststalker frost walking not working (reimplemented for 1.21 enchantment system changes)