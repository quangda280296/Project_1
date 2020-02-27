package quang.project2.view.loading;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import quang.project2.R;
import quang.project2.view.loading.shimmer.Shimmer;
import quang.project2.view.loading.shimmer.ShimmerTextView;


public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        TitanicTextView tv = (TitanicTextView) findViewById(R.id.my_text_view);

        // set fancy typeface
        tv.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));

        // start animation
        new Titanic().start(tv);

        ShimmerTextView shimmer_tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);;
        shimmer_tv.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));

        Shimmer shimmer ;

        shimmer = new Shimmer();
        shimmer.start(shimmer_tv);

        Thread bamgio = new Thread(){
        public void run()
        {
            try {
                sleep(5000);
            } catch (Exception e) {

            }
            finally
            {
                Intent activitymoi = new Intent("quang.project2.view.homebackground.MainActivity");
                startActivity(activitymoi);
            }
        }
    };
        bamgio.start();


    }

    protected void onPause(){
        super.onPause();
        finish();
    }

}
