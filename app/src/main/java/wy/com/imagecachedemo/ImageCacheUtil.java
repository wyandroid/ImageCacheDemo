package wy.com.imagecachedemo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 21  * 图片缓存帮助类
 * 22  *
 * 23  * 包含内存缓存LruCache和磁盘缓存DiskLruCache
 * 24  *
 * 25  * @author Rabbit_Lee
 * 26  *
 * 27
 */

public class ImageCacheUtil implements ImageLoader.ImageCache {

    private String TAG = ImageCacheUtil.this.getClass().getSimpleName();

    //缓存类

    private static LruCache<String, Bitmap> mLruCache;

    private static DiskLruCache mDiskLruCache;

    //磁盘缓存大小

    private static final int DISKMAXSIZE = 10 * 1024 * 1024;


    public ImageCacheUtil() {
        // 获取应用可占内存的1/8作为缓存
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        // 实例化LruCaceh对象
        mLruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override


            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();

            }


        };
        try {
            // 获取DiskLruCahce对象
//            第二个参数是应用程序的版本号，要传入版本号是因为如果应用升级缓存会被清除掉。
            mDiskLruCache = DiskLruCache.open(getDiskCacheDir(MyApplication.newInstance(), "Rabbit"), getAppVersion(MyApplication.newInstance()), 1, DISKMAXSIZE);

        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    /**
     * 58      * 从缓存（内存缓存，磁盘缓存）中获取Bitmap
     * 59
     */

    @Override


    public Bitmap getBitmap(String url) {
        if (mLruCache.get(url) != null) {
            // 从LruCache缓存中取
            Log.i(TAG, "从LruCahce获取");
            return mLruCache.get(url);

        } else {
            String key = MD5Utils.md5(url);
            try {
                if (mDiskLruCache.get(key) != null) {
                    // 从DiskLruCahce取
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    Bitmap bitmap = null;
                    if (snapshot != null) {
                        bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                        // 存入LruCache缓存
                        mLruCache.put(url, bitmap);
                        Log.i(TAG, "从DiskLruCahce获取");

                    }
                    return bitmap;

                }

            } catch (IOException e) {
                e.printStackTrace();

            }

        }
        return null;

    }


    /**
     * 89      * 存入缓存（内存缓存，磁盘缓存）
     * 90
     */

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        // 存入LruCache缓存
        mLruCache.put(url, bitmap);
        // 判断是否存在DiskLruCache缓存，若没有存入
        String key = MD5Utils.md5(url);
        try {
            if (mDiskLruCache.get(key) == null) {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                        editor.commit();

                    } else {
                        editor.abort();
                    }
                }
                mDiskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 117      * 该方法会判断当前sd卡是否存在，然后选择缓存地址
     * 118      *
     * 119      * @param context
     * 120      * @param uniqueName
     * 121      * @return
     * 122
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
//        DiskLruCache所有的数据都存储在/storage/emulated/0/Android/data/应用包名/cache/XXX文件夹中
//       (你也可以修改，但不建议这样做，原因请继续往下看)，这个是android系统默认的应用缓存位置，
//        如果应用被删除，这个文件也会一起被删除，避免应用删除后有残留数据的问题。同时，由于数据没有存储在硬盘里，
//        所以不会影响系统性能，在sd卡里，你可以存储任意多数据。
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 134      * 获取应用版本号
     * 135      *
     * 136      * @param context
     * 137      * @return
     * 138
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}