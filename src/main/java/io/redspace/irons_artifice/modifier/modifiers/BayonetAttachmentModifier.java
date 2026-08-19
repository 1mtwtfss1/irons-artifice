package io.redspace.irons_artifice.modifier.modifiers;

import io.redspace.irons_artifice.IronsArtifice;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.item.AttachmentMap;
import io.redspace.irons_artifice.modifier.GunModifier;
import io.redspace.irons_artifice.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class BayonetAttachmentModifier implements GunModifier {
    // TODO: gun geos need an `attachment_bayonet` marker bone to show the mesh
    public static final String ATTACHMENT_BONE = "attachment_bayonet";
    public static final String MESH_ID = "bayonet";

    private static final List<DataComponentType<?>> IRON_SPEAR_USE_COMPONENTS = List.of(
            DataComponents.KINETIC_WEAPON,
            DataComponents.ATTACK_RANGE,
            DataComponents.ATTRIBUTE_MODIFIERS,
            DataComponents.USE_EFFECTS,
            DataComponents.DAMAGE_TYPE
    );

    @Override
    public void apply(ShotComponentMap components) {
    }

    @Override
    public void getDescriptionText(Consumer<Component> builder) {
        builder.accept(Component.translatable("irons_artifice.modifier.bayonet").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public Optional<DataComponentPatch> getPatch() {
        ItemStack ironSpear = new ItemStack(Items.IRON_SPEAR);
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        for (DataComponentType<?> type : IRON_SPEAR_USE_COMPONENTS) {
            copy(builder, ironSpear, type);
        }
        builder.set(DataComponentRegistry.ATTACHMENT.get(), new AttachmentMap(Map.of(
                ATTACHMENT_BONE, IronsArtifice.id(MESH_ID)
        )));
        return Optional.of(builder.build());
    }

    private static <T> void copy(DataComponentPatch.Builder builder, ItemStack source, DataComponentType<T> type) {
        T value = source.get(type);
        if (value != null) {
            builder.set(type, value);
        }
    }
}
