//package io.redspace.irons_artifice.client.gun;
//
//import com.geckolib.animatable.GeoAnimatable;
//import com.geckolib.animatable.manager.AnimatableManager;
//import com.geckolib.animation.AnimationController;
//import com.geckolib.constant.DataTickets;
//import net.minecraft.world.item.ItemDisplayContext;
//import org.jspecify.annotations.NonNull;
//
//import java.util.EnumSet;
//import java.util.HashMap;
//import java.util.Map;
//
//public class SimplePerspectiveAnimatableManager<T extends GeoAnimatable> extends AnimatableManager<T> {
//    private static final EnumSet<ItemDisplayContext> ALLOWED_PERSPECTIVES = EnumSet.of(
//            ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
//            ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
//            ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
//            ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
//    );
//
//    Map<String, AnimationController<T>> noopControllers = new HashMap<>();
//
//    public SimplePerspectiveAnimatableManager(GeoAnimatable animatable) {
//        super(animatable);
//    }
//
//    @Override
//    public void addController(AnimationController<T> controller) {
//        super.addController(controller);
//        noopControllers.put(controller.getName(),con)
//    }
//
//    public boolean isManagerLive() {
//        var perspective = getAnimatableData(DataTickets.ITEM_RENDER_PERSPECTIVE);
//        return perspective == null || ALLOWED_PERSPECTIVES.contains(perspective);
//    }
//
//    public @NonNull Map<String, AnimationController<T>> getAnimationControllers() {
//        return isManagerLive() ? super.getAnimationControllers() : noopControllers;
//    }
//}
