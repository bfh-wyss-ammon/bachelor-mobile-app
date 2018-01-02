/**
 *   Copyright 2018 Pascal Ammon, Gabriel Wyss
 *
 * 	 Implementation eines anonymen Mobility Pricing Systems auf Basis eines Gruppensignaturschemas
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package mps.bachelor2017.bfh.ti.ch.mobiltypricing.animations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.R;

/**
 * Created by Pascal on 18.11.2017.
 */

public class SlideAnimation extends View {

    private enum Direction {
        LeftToRight,
        RightToLeft
    }


    public interface SlideAnimationEvents {
        void onRightSubmit();
        void onLeftSubmit();
    }

    float xPos = -1;
    int width;
    int height;
    int cornerRadius;

    int leftMax = -1;
    int rightMax = -1;

    Paint pFingerCircle;
    Paint pBackground;
    Paint pLeftToRight;
    Paint pRightToLeft;
    Paint pWhite;
    TextPaint pText;

    Direction direction;

    boolean started = false;

    int yLine1;
    int yLine2;
    int xLine1;
    int xLine2;

    private SlideAnimationEvents mStartAnimationEvents;
    public void registerCallbacks(SlideAnimationEvents slideAnimationEvents) {
        this.mStartAnimationEvents = slideAnimationEvents;
    }

    public SlideAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);

        pFingerCircle = new Paint();
        pFingerCircle.setColor(getContext().getColor(R.color.colorPrimary));
        pFingerCircle.setAntiAlias(true);
        pFingerCircle.setShadowLayer(5.5f, 6.0f, 6.0f, 0x80000000);

        pBackground = new Paint();
        pBackground.setColor(getContext().getColor(R.color.colorAccent));

        pLeftToRight = new Paint();
        pLeftToRight.setColor(getContext().getColor(R.color.colorBorder));

        pRightToLeft = new Paint();
        pRightToLeft.setColor(getContext().getColor(R.color.colorAccent3));

        pWhite = new Paint();
        pWhite.setColor(getContext().getColor(R.color.text));
        pText = new TextPaint(pWhite);

        direction = Direction.LeftToRight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if(mStartAnimationEvents == null)
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                started = false;
                xPos = 0;
                invalidate();
                return true;
            }

            case MotionEvent.ACTION_DOWN: {
                started = direction == Direction.LeftToRight ? event.getX() <  + height : event.getX() >  - height;

                if(started) {
                    xPos = event.getX();
                    invalidate();
                }
            }
            case MotionEvent.ACTION_MOVE: {
                if(started) {
                    xPos = event.getX();
                    invalidate();
                }
                return true;
            }
            default: return false;
        }
    }

    private  RectF backgroundRect;
    private RectF getBackgroundRect() {
        if(backgroundRect == null) {
            backgroundRect = new RectF(0, 0, width, height);
        }
        return backgroundRect;
    }

    private RectF getBackgroundOverlayRect() {
        if(direction == Direction.LeftToRight) {
            return new RectF(0, 0, xPos < leftMax ?leftMax + cornerRadius : xPos + cornerRadius, height);
        }
        else {
            return new RectF(xPos > rightMax ? rightMax -cornerRadius : xPos - cornerRadius, 0, width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(xPos == -1) {
            width = canvas.getWidth();
            height = canvas.getHeight();
            cornerRadius = Math.round(height / 2);
            leftMax = cornerRadius;
            rightMax = width - cornerRadius;
            pText.setTextSize( height / 6);
            yLine1 = Math.round(height/2 - (pText.getTextSize() / 2 + 2));
            yLine2 = Math.round(height/2 + (pText.getTextSize() / 2 + 2));
            xLine1 = Math.round(xPos + cornerRadius * 2 + 5);
            xLine2 = Math.round(xPos + cornerRadius * 2 + 5);
        }
        // background
        canvas.drawRoundRect(getBackgroundRect(), cornerRadius,cornerRadius, pBackground);

        if(direction == Direction.LeftToRight) {
            canvas.drawText(getContext().getString(R.string.MoveToStart), xLine1, yLine1, pText);
            canvas.drawText(getContext().getString(R.string.ToRight), xLine2, yLine2, pText);
        }
        else {
            canvas.drawText(getContext().getString(R.string.MoveToStop),  xLine1, yLine1, pText);
            canvas.drawText(getContext().getString(R.string.ToLeft),  xLine2, yLine2, pText);
        }

        if(started) {
            boolean submitted = direction == Direction.LeftToRight ? xPos >= rightMax : xPos <= leftMax;

            if(submitted) {

                if(direction == Direction.LeftToRight) {
                    xPos = rightMax;
                    direction = Direction.RightToLeft;
                    mStartAnimationEvents.onRightSubmit();
                }
                else {
                    xPos = leftMax;
                    direction = Direction.LeftToRight;
                    mStartAnimationEvents.onLeftSubmit();
                }
                started = false;
            }
            else {
                // background overlay
                canvas.drawRoundRect(getBackgroundOverlayRect(), cornerRadius, cornerRadius, direction == Direction.LeftToRight ? pLeftToRight : pRightToLeft);
            }
        }

        // finger pointer
        float x = ((xPos > rightMax) || (xPos < leftMax)) ? ((direction == Direction.LeftToRight) ? leftMax : rightMax) : xPos;
        canvas.drawCircle(x, cornerRadius, cornerRadius-5, pWhite);
        canvas.drawCircle(x, cornerRadius, cornerRadius-25, pFingerCircle);

        if(started) {
            String text = direction == Direction.LeftToRight ? getContext().getString(R.string.Start) : getContext().getString(R.string.Stop);
            canvas.drawText(text,  x - pText.measureText(text) / 2, height / 2, pText);
        }
    }
}