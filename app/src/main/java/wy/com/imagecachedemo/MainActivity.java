package wy.com.imagecachedemo;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private Button mButton;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.button);
        mImageView = (ImageView) findViewById(R.id.image);


        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = "http://img.blog.csdn.net/20130702124537953?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdDEyeDM0NTY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast";
                ImageCacheManager.loadImage(url, mImageView, getBitmapFromRes(R.drawable.ic_launcher), getBitmapFromRes(R.drawable.ic_launcher));

            }
        });
    }

    public Bitmap getBitmapFromRes(int resId) {
        Resources res = this.getResources();
        return BitmapFactory.decodeResource(res, resId);

    }

}