package quang.project2.view.boommenu;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import quang.project2.R;


/**
 * Created by Weiping on 2016/3/19.
 */
public class Dot extends View {
    public Dot(Context context) {
        this(context, null);
    }

    public Dot(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.dot);
    }

    public void setColor(int color) {
        ((GradientDrawable)getBackground()).setColor(color);
    }

}
