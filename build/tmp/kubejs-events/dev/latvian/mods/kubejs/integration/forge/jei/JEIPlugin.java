/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.fluid.FluidStack
 *  mezz.jei.api.IModPlugin
 *  mezz.jei.api.JeiPlugin
 *  mezz.jei.api.constants.VanillaTypes
 *  mezz.jei.api.forge.ForgeTypes
 *  mezz.jei.api.ingredients.IIngredientType
 *  mezz.jei.api.registration.IRecipeRegistration
 *  mezz.jei.api.registration.ISubtypeRegistration
 *  mezz.jei.api.runtime.IJeiRuntime
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.fluids.FluidStack
 */
package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.architectury.fluid.FluidStack;
import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.integration.forge.jei.AddJEIEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.HideCustomJEIEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.HideJEIEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.InformationJEIEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.JEIEvents;
import dev.latvian.mods.kubejs.integration.forge.jei.JEISubtypesEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.RemoveJEICategoriesEvent;
import dev.latvian.mods.kubejs.integration.forge.jei.RemoveJEIRecipesEvent;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.Objects;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEIPlugin
implements IModPlugin {
    public static final ResourceLocation ID = new ResourceLocation("kubejs", "jei");
    public IJeiRuntime runtime;

    public ResourceLocation getPluginUid() {
        return ID;
    }

    public void onRuntimeAvailable(IJeiRuntime r) {
        this.runtime = r;
        BuiltinKubeJSPlugin.GLOBAL.put("jeiRuntime", this.runtime);
        if (JEIEvents.HIDE_ITEMS.hasListeners()) {
            JEIEvents.HIDE_ITEMS.post(ScriptType.CLIENT, new HideJEIEventJS<ItemStack>(this.runtime, (IIngredientType<ItemStack>)VanillaTypes.ITEM_STACK, IngredientJS::of, stack -> !stack.m_41619_()));
        }
        if (JEIEvents.HIDE_FLUIDS.hasListeners()) {
            JEIEvents.HIDE_FLUIDS.post(ScriptType.CLIENT, new HideJEIEventJS<net.minecraftforge.fluids.FluidStack>(this.runtime, (IIngredientType<net.minecraftforge.fluids.FluidStack>)ForgeTypes.FLUID_STACK, object -> {
                FluidStackJS fs = FluidStackJS.of(object);
                return fluidStack -> fluidStack.getFluid().m_6212_(fs.getFluid()) && Objects.equals(fluidStack.getTag(), fs.getNbt());
            }, stack -> !stack.isEmpty()));
        }
        if (JEIEvents.HIDE_CUSTOM.hasListeners()) {
            JEIEvents.HIDE_CUSTOM.post(ScriptType.CLIENT, new HideCustomJEIEventJS(this.runtime));
        }
        if (JEIEvents.REMOVE_CATEGORIES.hasListeners()) {
            JEIEvents.REMOVE_CATEGORIES.post(ScriptType.CLIENT, new RemoveJEICategoriesEvent(this.runtime));
        }
        if (JEIEvents.REMOVE_RECIPES.hasListeners()) {
            JEIEvents.REMOVE_RECIPES.post(ScriptType.CLIENT, new RemoveJEIRecipesEvent(this.runtime));
        }
        if (JEIEvents.ADD_ITEMS.hasListeners()) {
            JEIEvents.ADD_ITEMS.post(ScriptType.CLIENT, new AddJEIEventJS<ItemStack>(this.runtime, (IIngredientType<ItemStack>)VanillaTypes.ITEM_STACK, ItemStackJS::of, stack -> !stack.m_41619_()));
        }
        if (JEIEvents.ADD_FLUIDS.hasListeners()) {
            JEIEvents.ADD_FLUIDS.post(ScriptType.CLIENT, new AddJEIEventJS<net.minecraftforge.fluids.FluidStack>(this.runtime, (IIngredientType<net.minecraftforge.fluids.FluidStack>)ForgeTypes.FLUID_STACK, object -> JEIPlugin.fromArchitectury(FluidStackJS.of(object).getFluidStack()), stack -> !stack.isEmpty()));
        }
    }

    public static net.minecraftforge.fluids.FluidStack fromArchitectury(FluidStack stack) {
        return new net.minecraftforge.fluids.FluidStack(stack.getFluid(), (int)stack.getAmount(), stack.getTag());
    }

    public void registerItemSubtypes(ISubtypeRegistration registration) {
        if (JEIEvents.SUBTYPES.hasListeners()) {
            JEIEvents.SUBTYPES.post(ScriptType.CLIENT, new JEISubtypesEventJS(registration));
        }
    }

    public void registerRecipes(IRecipeRegistration registration) {
        if (JEIEvents.INFORMATION.hasListeners()) {
            JEIEvents.INFORMATION.post(ScriptType.CLIENT, new InformationJEIEventJS(registration));
        }
    }
}

