package wy.com.imagecachedemo;


import android.app.Application;

/**
 * 5  * Application类，提供全局上下文对象
 * 6  * @author Rabbit_Lee
 * 7  *
 * 8
 */
public class MyApplication extends Application {

    public static String TAG;
    public static MyApplication myApplication;

    public static MyApplication newInstance() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = this.getClass().getSimpleName();
        myApplication = this;

    }
}
