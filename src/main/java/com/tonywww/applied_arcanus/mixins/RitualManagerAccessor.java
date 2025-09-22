package com.tonywww.applied_arcanus.mixins;

import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.ActiveRitualData;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(RitualManager.class)
public interface RitualManagerAccessor {
    @Accessor("activeRitualData")
    @Nullable
    ActiveRitualData getActiveRitualDataField();

    @Accessor("activeRitualData")
    void setActiveRitualDataField(@Nullable ActiveRitualData activeRitualData);
}