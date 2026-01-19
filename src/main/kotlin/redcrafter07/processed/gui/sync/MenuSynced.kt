package redcrafter07.processed.gui.sync

/**
 * Synchronizes a field from the server to the client. fields marked with this should never be set on the client, instead `@RPCMethod`s should be used.
 *
 * Example:
 * ```kt
 * class ExampleMenu(containerId: Int, player: Player) : AbstractProcessedContainerMenu(myMenuType, containerId, player) {
 *     @MenuSynced
 *     var number: Int = 0;
 *
 *     override fun setCarried(stack: ItemStack) {
 *         number++;
 *         super.setCarried(stack);
 *     }
 * }
 * ```
 * */
annotation class MenuSynced(
    val type: SyncType = SyncType.OnChange,
    /**
     * A custom function that's called on the client with the previous value when a synchronisation occurred.
     *
     * Signature: `(T) -> Unit`
     * */
    val onSynchronised: String = "",
    /**
     * A custom method that's called with the value of this field to check if it's dirty.
     *
     * Signature: `(T) -> Boolean`
     * */
    val dirtyChecker: String = "",
    /**
     * A custom method to obtain this field's codec.
     * If not present, the CodecRegistry will be used.
     * If that fails, the following methods are tried:
     * - `<field name>$codec`
     * - `<field name>Codec`
     * - `get<field name capitalised>$codec`
     * - `get<field name capitalised>Codec`.
     *
     * If all of that still yields no codec, initialising this menu will throw an error.
     *
     * Signature: `() -> StreamCodec<RegistryFriendlyByteBuf, T>`
     * */
    val codecGetter: String = "",
    /**
     * method for copying this field.
     *
     * Signature: `(T) -> T`.
     * */
    val copyMethod: String = "",
    /**
     * Method for conditional synchronisation.
     *
     * If this field is marked as dirty, and this returns false, it's marked as clean and will *not* be synced.
     *
     * Signature: (T) -> Boolean
     */
    val condition: String = "",
)
