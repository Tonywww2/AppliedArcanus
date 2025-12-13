package com.tonywww.applied_arcanus.data;

import appeng.core.definitions.AEBlocks;
import com.stal111.forbidden_arcanus.core.init.ModItems;
import com.tonywww.applied_arcanus.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.HEPHAESTUS_FORGE_SUPPLIER.get())
                .pattern("ABA")
                .pattern("CDC")
                .pattern("AEA")
                .define('A', Items.IRON_INGOT)
                .define('B', ModItems.MUNDABITUR_DUST)
                .define('C', AEBlocks.PATTERN_PROVIDER)
                .define('D', ModItems.NETHERITE_BLACKSMITH_GAVEL)
                .define('E', ModItems.ETERNAL_STELLA)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(recipeOutput);

//        ItemStack bloodTestTube = new ItemStack(ModItems.BLOOD_TEST_TUBE.get());
//        bloodTestTube.set(ModDataComponents.ESSENCE_VALUE.get(), EssenceValue.of(EssenceType.BLOOD, 3000));
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, com.tonywww.applied_arcanus.init.ModItems.BLOOD_SOURCE)
//                .requires(Ingredient.of(bloodTestTube))
//                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
//                .save(recipeOutput);
    }
}
