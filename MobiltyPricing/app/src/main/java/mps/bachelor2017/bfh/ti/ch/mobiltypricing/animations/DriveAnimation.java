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
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.view.View;

public class DriveAnimation extends View {

    AnimatedVectorDrawable[] drawables;

    int ctr = 0;

    boolean simulate = false;

    public DriveAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);

        drawables = new AnimatedVectorDrawable[18];
        for(int i = 0; i < 18; i++) {
            drawables[i] = (AnimatedVectorDrawable) getContext().getDrawable(getResources().getIdentifier("ic_track_animation_"+i, "drawable", getContext().getPackageName()));
        }
    }

    public void simulate() {
        simulate = true;
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
        drawables[ctr].setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        if (simulate) {
            drawables[ctr].draw(canvas);
            drawables[ctr].start();

            ctr = ctr == 17 ? 0 : ctr + 1;
            if(ctr > 0) {
                postInvalidateDelayed(200);
            }
        }
        else {
            drawables[0].draw(canvas);
        }

    }
}