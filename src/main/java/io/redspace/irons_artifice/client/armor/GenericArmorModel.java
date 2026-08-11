package io.redspace.irons_artifice.client.armor;

import com.geckolib.animatable.GeoItem;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import io.redspace.irons_artifice.IronsArtifice;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

public class GenericArmorModel<T extends Item & GeoItem> extends DefaultedItemGeoModel<T> {

    private final Identifier model;

    private final Identifier texture;

    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(IronsArtifice.MODID, "empty");

    public GenericArmorModel(String modid, String name) {
        this(
                Identifier.fromNamespaceAndPath(modid, String.format("armor/%s", name)),
                Identifier.fromNamespaceAndPath(modid, String.format("textures/models/armor/%s.png", name))
        );
    }

    public GenericArmorModel(Identifier model, Identifier texture) {
        super(Identifier.fromNamespaceAndPath(model.getNamespace(), ""));
        this.model = model;
        this.texture = texture;
    }

    public GenericArmorModel(String name) {
        this(IronsArtifice.MODID, name);
    }

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return model;
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return texture;
    }

    @Override
    public @NonNull Identifier getAnimationResource(T animatable) {
        return ANIMATION;
    }
}