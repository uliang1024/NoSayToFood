package info.androidhive.firebaseauthapp.ImageEdit.Utils;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//https://gist.github.com/agustinsivoplas/44f01672d698394dc28edda2c7a05cb6
public class BitmapUtils {


    //added===========
    public static Bitmap getBitmapFromAssets(Context context , String filename, int width , int height){
        //通過資產管理員(AssetManager)讀取資產(assets)
        //在 /assets/ 的文件沒有一個資源 ID，所以只可以通過資產管理員(AssetManager)存取在 /assets/ 文件夾下的文件。
        AssetManager assetManager = context.getAssets();
//        通常做位元組傳輸，會叫做Stream
//        以常見的字串傳輸，會叫做Reader/Writer
//        Stream就如同字面上的意思，像水流一樣，有開有關
//        當Read()或write()方法被呼叫時，串流就會開啟，直到close()被呼叫為止
        InputStream inputStream ;
        Bitmap bitmap = null;

        try{
//            BitmapFactory.Options 类指定解码选项。在解码时将 inJustDecodeBounds 属性设置为 true 可避免内存分配
            BitmapFactory.Options options = new BitmapFactory.Options();
//            这里要设置Options.inJustDecodeBounds=true,这时候decode的bitmap为null,只是把图片的宽高放在Options里
//            避免直接decode大圖
            options.inJustDecodeBounds= true;//拿到大圖後，先進行縮放
            inputStream = assetManager.open(filename);//輸入流設定為指定路徑的asset
            options.inSampleSize = calculateInSampleSize(options,width,height);//设置合适的压缩比例inSampleSize
            options.inJustDecodeBounds=false;
            return  BitmapFactory.decodeStream(inputStream,null,options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
    //=================

    //added===========
    public static Bitmap getBitMapFromGallery(Context context , Uri uri, int width , int height){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        //Query the given URI, returning a Cursor over the result set.
        Cursor cursor = context.getContentResolver().query(uri,filePathColumn,null,null,null);
        cursor.moveToFirst();
        int columnIndex =cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);//得到圖片路徑
        cursor.close();

        //寫一個方法，傳入圖片的路徑
        int degree =readDegree(picturePath);
        Log.e("rotation ",degree+"");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds= true;//拿到大圖後，先進行縮放
        BitmapFactory.decodeFile(picturePath,options);
        options.inSampleSize = calculateInSampleSize(options,width,height);//恰當的inSampleSize可以使BitmapFactory分配更少的空間
        options.inJustDecodeBounds = false;

        //設置圖片旋轉matrix
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath,options);
        //創造bitmap
        Bitmap fixedBitmap = Bitmap.createBitmap(imageBitmap,0,0,imageBitmap.getWidth(),imageBitmap.getHeight(),matrix,false);

        return fixedBitmap;
    }

    //解決有些照片會自甕旋轉90度
    public static int readDegree(String path) {

        int degree  = 0;
        try {
            //取的路徑圖片的資訊(旋轉角度)
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    //=================

    public static Bitmap applyOverlay(Context context, Bitmap sourceImage, int overlayDrawableResourceId){
        Bitmap bitmap = null;
        try{
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();
            Resources r = context.getResources();

            Drawable imageAsDrawable =  new BitmapDrawable(r, sourceImage);
            Drawable[] layers = new Drawable[2];

            layers[0] = imageAsDrawable;
            layers[1] = new BitmapDrawable(r, BitmapUtils.decodeSampledBitmapFromResource(r, overlayDrawableResourceId, width, height));
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            bitmap = BitmapUtils.drawableToBitmap(layerDrawable);
        }catch (Exception ex){}
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static String insertImage(ContentResolver cr, Bitmap source, String title , String description) throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME,title);
        values.put(MediaStore.Images.Media.DESCRIPTION,description);
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED,System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());

        Uri uri = null;
        String stringUrl = null;

        try{
            uri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
            if (source!= null){
                OutputStream outputStream = cr.openOutputStream(uri);
                try{
                    source.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
                }finally{
                    {
                        outputStream.close();
                    }
                    long id = ContentUris.parseId(uri);
                    Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr,id,MediaStore.Images.Thumbnails.MINI_KIND,null);
                    storeThumbnail(cr,miniThumb,id,50f,50f,MediaStore.Images.Thumbnails.MICRO_KIND);
                }
            }
            else {
                cr.delete(uri,null,null);
                uri = null;
            }
        } catch (FileNotFoundException e) {

            if(uri!= null){
                cr.delete(uri,null,null);
                uri = null;
            }

        }
        if (uri!= null){
            stringUrl = uri.toString();
        }
        return  stringUrl;
    }

    private static final Bitmap storeThumbnail(ContentResolver cr, Bitmap resource, long id, float width, float height, int kind) {

        Matrix matrix = new Matrix();
        float scaleX = width/resource.getWidth();
        float scaleY = height/resource.getHeight();

        matrix.setScale(scaleX,scaleY);

        Bitmap thumb = Bitmap.createBitmap(resource,0,0,resource.getWidth(),resource.getHeight(),matrix,true);
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(MediaStore.Images.Thumbnails.KIND,kind);
        contentValues.put(MediaStore.Images.Thumbnails.IMAGE_ID,id);
        contentValues.put(MediaStore.Images.Thumbnails.WIDTH,width);
        contentValues.put(MediaStore.Images.Thumbnails.HEIGHT,height);


        Uri uri = cr.insert( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,contentValues);
        try{
            OutputStream outputStream = cr.openOutputStream(uri);
            thumb.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.close();
            return thumb;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return  null;
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }
}
