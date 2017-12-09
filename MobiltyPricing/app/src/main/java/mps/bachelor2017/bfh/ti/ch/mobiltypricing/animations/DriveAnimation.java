package mps.bachelor2017.bfh.ti.ch.mobiltypricing.animations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Pascal on 18.11.2017.
 */

public class DriveAnimation extends View {

    AnimatedVectorDrawable[] drawables;

    int ctr = 0;

    public DriveAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void simulate() {
        if(drawables == null) {
            drawables = new AnimatedVectorDrawable[18];
            for(int i = 0; i < 18; i++) {
                drawables[i] = (AnimatedVectorDrawable) getContext().getDrawable(getResources().getIdentifier("ic_track_animation_"+i, "drawable", getContext().getPackageName()));
            }
        }
        this.postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (drawables != null) {
            drawables[ctr].setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawables[ctr].draw(canvas);

            drawables[ctr].start();

            ctr = ctr == 17 ? 0 : ctr + 1;
            if(ctr > 0) {
                postInvalidateDelayed(400);
            }
        }

    }
}