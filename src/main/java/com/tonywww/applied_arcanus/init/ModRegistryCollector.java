package com.tonywww.applied_arcanus.init;

import net.valhelsia.valhelsia_core.api.common.registry.RegistryCollector;

public class ModRegistryCollector extends RegistryCollector {

    public ModRegistryCollector(String modId) {
        super(modId);
    }

    @Override
    protected void collectHelpers() {
        this.addItemHelper(ModItems.class);
    }
}
