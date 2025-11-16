Materials are specified in [`Materials.kt`](./Material.kt). A material consists out of a few things:
- An identifier, used to name the blocks, items, and fluids (e.g. `block_of_$identifier`)
- A color, used to tint blocks, items, and fluids
- A list of material types, see below
- A nugget and a raw block variant, if the ingotlike and orelike material types are set, respectively (Note: This should be migrated to extra data)
- A list of extra data associated with this material, that'll cause things like cables or pipes to be of that material.
The list of datas should be located in [`data/`](./data).

Material Types:

- OreLike: Adds an ore block, a raw item, and a raw block
- Dust: Adds a dust variant
- IngotLike: Adds an ingot and a nugget item
- MetalBlock: Adds a block of that material