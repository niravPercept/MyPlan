package percept.myplan.customviews;


import android.content.Context;
import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * Created by percept on 13/7/16.
 */

public class SimpleViewAnimator extends LinearLayout {
    private Animation inAnimation;
    private Animation outAnimation;

    public SimpleViewAnimator(Context context) {
        super(context);
    }

    public void setInAnimation(Animation inAnimation) {
        this.inAnimation = inAnimation;
    }

    public void setOutAnimation(Animation outAnimation) {
        this.outAnimation = outAnimation;
    }

    @Override
    public void setVisibility(int visibility) {
        if (getVisibility() != visibility) {
            if (visibility == VISIBLE) {
                if (inAnimation != null) startAnimation(inAnimation);
            } else if ((visibility == INVISIBLE) || (visibility == GONE)) {
                if (outAnimation != null) startAnimation(outAnimation);
            }
        }

        super.setVisibility(visibility);
    }
}
