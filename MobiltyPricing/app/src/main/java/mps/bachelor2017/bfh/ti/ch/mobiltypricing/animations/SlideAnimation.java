package mps.bachelor2017.bfh.ti.ch.mobiltypricing.animations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.R;

/**
 * Created by Pascal on 18.11.2017.
 */

public class SlideAnimation extends View {

    public interface SlideAnimationEvents {
        void onSlideAnimationSubmit();
    }

    float xPos = -1;

    int width;
    int height;
    int cornerRadius;

    Paint pForeground;
    Paint pBackground;
    Paint pSubmitted;

    boolean mSubmitted = false;

    private SlideAnimationEvents mStartAnimationEvents;

    public void registerCallbacks(SlideAnimationEvents slideAnimationEvents) {
        this.mStartAnimationEvents = slideAnimationEvents;
    }

    boolean started = false;
    public SlideAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);

        pForeground = new Paint();
        pForeground.setColor(getContext().getColor(R.color.colorPrimary));

        pBackground = new Paint();
        pBackground.setColor(getContext().getColor(R.color.colorAccent));

        pSubmitted = new Paint();
        pSubmitted.setColor(getContext().getColor(R.color.colorAccent3));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void reset() {
        xPos = -1;
        this.mSubmitted = false;
        this.postInvalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if(mSubmitted || mStartAnimationEvents == null)
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                started = false;
                xPos = height;
                invalidate();
                return true;
            }

            case MotionEvent.ACTION_DOWN: {
                if(event.getX() < height) {
                    started = true;
                    xPos = event.getX();
                    invalidate();
                }
            }
            case MotionEvent.ACTION_MOVE: {
                if(started) {
                    xPos = event.getX() > height ? event.getX() : height;
                    invalidate();
                }
                return true;
            }
            default: return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(xPos == -1) {
            width = canvas.getWidth();
            height = canvas.getHeight();
            cornerRadius = Math.round(height / 2);
            xPos = height;
        }
        RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawRoundRect(rect, cornerRadius,cornerRadius, pBackground);

        if(started) {
            if(xPos > width -cornerRadius) {
                mSubmitted = true;
                rect = new RectF(0, 0, width, height);
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, pSubmitted);
                if(mStartAnimationEvents != null) {
                    mStartAnimationEvents.onSlideAnimationSubmit();
                }
                return;
            }
            else {
                rect = new RectF(0, 0, xPos, height);
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, pForeground);
            }
        }
        canvas.drawCircle( (xPos-cornerRadius < cornerRadius ? cornerRadius :xPos-cornerRadius), height / 2, cornerRadius, pForeground);
    }
}