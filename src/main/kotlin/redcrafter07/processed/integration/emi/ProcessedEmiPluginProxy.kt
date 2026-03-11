package redcrafter07.processed.integration.emi

import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiInitRegistry
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry

@EmiEntrypoint()
class ProcessedEmiPluginProxy: EmiPlugin {
    override fun register(registry: EmiRegistry) {
        ProcessedEmiPlugin.register(registry)
    }

    override fun initialize(registry: EmiInitRegistry) {
        super.initialize(registry)
        ProcessedEmiPlugin.initialize(registry)
    }
}