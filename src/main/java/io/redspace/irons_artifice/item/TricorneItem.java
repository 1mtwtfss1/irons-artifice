package io.redspace.irons_artifice.item;

import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.renderer.GeoArmorRenderer;
import com.google.common.base.Suppliers;
import io.redspace.irons_artifice.IronsArtifice;
import io.redspace.irons_artifice.client.armor.GenericArmorModel;
import io.redspace.irons_artifice.client.armor.TransmogArmorRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;
import java.util.function.Supplier;

public class TricorneItem extends BaseGeoItem {
    public static final ArmorMaterial TRICORNE_MATERIAL = new ArmorMaterial(37, Map.of(ArmorType.HELMET, 3),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0,
            0,
            ItemTags.REPAIRS_LEATHER_ARMOR, ResourceKey.create(EquipmentAssets.ROOT_ID, IronsArtifice.id("empty")));

    public TricorneItem(Properties properties) {
        super(properties.humanoidArmor(TRICORNE_MATERIAL, ArmorType.HELMET));
        geoRenderProvider.setValue(new GeoRenderProvider() {
            private final Supplier<TransmogArmorRenderer<?, ?>> renderer =
                    Suppliers.memoize(() -> new TransmogArmorRenderer<>(new GenericArmorModel<>("tricorne")));

            @Override
            public @org.jspecify.annotations.Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
                return renderer.get();
            }
        });
    }
}
