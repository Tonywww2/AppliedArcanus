package com.tonywww.applied_arcanus.mixins;

import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceManager;
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssencesDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EssenceManager.class)
public interface EssenceManagerAccessor
{
    @Accessor("maxEssences")
    EssencesDefinition getMaxEssences();
}
