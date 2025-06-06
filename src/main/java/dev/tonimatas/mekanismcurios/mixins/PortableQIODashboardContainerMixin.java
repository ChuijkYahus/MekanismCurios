package dev.tonimatas.mekanismcurios.mixins;

import dev.tonimatas.mekanismcurios.MekanismCurios;
import mekanism.common.inventory.container.item.PortableQIODashboardContainer;
import mekanism.common.inventory.container.sync.SyncableItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortableQIODashboardContainer.class)
public abstract class PortableQIODashboardContainerMixin {
    @Shadow @Final protected InteractionHand hand;

    @Shadow protected ItemStack stack;

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    private void mci$stillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (this.hand == null) {
            ItemStack curiosStack = MekanismCurios.getSlot(player);
            boolean validCurios = !curiosStack.isEmpty() && (curiosStack.is(this.stack.getItem()));
            cir.setReturnValue(validCurios);
        }
    }
    
    @Inject(method = "addInventorySlots", at = @At("RETURN"))
    private void mci$addInventorySlots(Inventory inv, CallbackInfo ci) {
        if (this.hand == null) {
            ((PortableQIODashboardContainer) (Object) this).track(SyncableItemStack.create(() -> MekanismCurios.getSlot(inv.player), item -> {
                MekanismCurios.setSlot(inv.player, item);
                if (stack.is(item.getItem())) {
                    stack = item;
                }
            }));
        }
    }
}
