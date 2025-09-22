package com.tonywww.applied_arcanus.mixins;

import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.ActiveRitualData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ActiveRitualData.class)
public interface ActiveRitualDataAccessor {

    @Accessor("counter")
    void setCounter(int counter);
}
