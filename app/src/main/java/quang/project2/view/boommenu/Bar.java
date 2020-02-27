package quang.project2.view.boommenu;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import quang.project2.R;

/**
 * Created by Weiping on 2016/3/19.
 */
public class Bar extends View {
    public Bar(Context context) {
        this(context, null);
    }

    public Bar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.bar);
    }

    public void setColor(int color) {
        ((GradientDrawable)getBackground()).setColor(color);
    }

}
