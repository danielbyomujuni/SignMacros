package net.signMacros.mixin;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;


@Mixin(AbstractSignBlock.class)
public class SignBlockMixins extends BlockWithEntity implements Waterloggable {

    private final SignType type;
    private long activation;

    protected SignBlockMixins(Settings settings, SignType type) {
        super(settings);
        this.type = type;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new SignBlockEntity();
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "onUse", at = @At("HEAD"))
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable co) {
        ItemStack itemStack = player.getStackInHand(hand);
        if(itemStack.getItem()==Items.BLAZE_ROD)
        {
            System.out.println("Running Command");
            List<String> lines = Lists.newArrayList();
                CompoundTag tag = world.getBlockEntity(pos).toTag(new CompoundTag());
                System.out.println(tag);
                for(int i = 0; i < 4; ++i) {

                    String string = tag.getString("Text" + (i + 1));
                    String RefactoredString;
                    if (string.startsWith("{\"extra\"")) {

                        RefactoredString = string.substring(9);
                    } else {
                        RefactoredString = string;
                    }
                    System.out.println("Important: " + RefactoredString);
                    Text text = Text.Serializer.fromJson(RefactoredString.isEmpty() ? "\"\"" : RefactoredString);
                    if (text != null) {
                        lines.add(text.asString());
                    }
                }
            for(int i = 0; i < 4; ++i) {
                System.out.println(lines.get(i));
            }

            MinecraftClient myclient = MinecraftClient.getInstance();
            if (lines.get(0).equals("Command") && (activation + 5) <= world.getTime()) {
                myclient.player.sendChatMessage(lines.get(1));
                activation = world.getTime();
            } else {

            }

        }

}


}
