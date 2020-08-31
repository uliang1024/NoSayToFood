package info.androidhive.firebaseauthapp.ImageEdit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import info.androidhive.firebaseauthapp.ImageEdit.Adapter.ThumbnailAdapter;
import info.androidhive.firebaseauthapp.ImageEdit.Fragments.BrushFragment;
import info.androidhive.firebaseauthapp.ImageEdit.Fragments.EditImageFragment;
import info.androidhive.firebaseauthapp.ImageEdit.Fragments.EmojiFragment;
import info.androidhive.firebaseauthapp.ImageEdit.Fragments.StickerFragment;
import info.androidhive.firebaseauthapp.ImageEdit.Fragments.TextFragment;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.AddTextFragmentListener;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.BrushFragmentListener;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.EditImageFragmentListener;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.EmojiFragmentListener;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.FilterListFragmentListener;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.StickerFragmentListener;
import info.androidhive.firebaseauthapp.ImageEdit.Utils.BitmapUtils;
import info.androidhive.firebaseauthapp.ImageEdit.Utils.SpacesItemDecoration;
import info.androidhive.firebaseauthapp.PostingActivity;
import info.androidhive.firebaseauthapp.R;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class ImageEditActivity extends AppCompatActivity implements FilterListFragmentListener, EditImageFragmentListener, BrushFragmentListener, EmojiFragmentListener, AddTextFragmentListener, StickerFragmentListener {

    public static String pictureName = "flash.jpg";
    public static final int PERMISSION_PICK_IMAGE =1000;
    public static final int PERMISSION_INSERT_IMAGE =1001;

    private int image_position;

    PhotoEditorView photoEditorView;
    PhotoEditor photoEditor;
    CoordinatorLayout coordinatorLayout;
    CardView btn_filters_list,btn_edit,btn_brush,btn_emoji,btn_text,btn_sticker,btn_crop;
    ImageView btn_undo,btn_redo;

    Bitmap originalBitmap,filteredBitmap,finalBitmap;

    Context context;
    Uri image_selected_uri;
    Uri postingPictureUri;
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float constraintFinal = 1.0f;

    EmojiFragment emojiFragment;
    BrushFragment brushFragment;
    EditImageFragment editImageFragment;
    TextFragment textFragment;
    StickerFragment stickerFragment;

    //===========================================//
    RecyclerView recyclerView;
    ThumbnailAdapter adapter;
    List<ThumbnailItem> thumbnailItems;

    //==========================================//
    static {
        System.loadLibrary("NativeImageProcessor");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        image_position = getIntent().getIntExtra("position",-1);

    //=====================init views and set actionbar=========================================//

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("MY FILTER");

        photoEditorView = findViewById(R.id.image_preview);
        photoEditor = new PhotoEditor.Builder(this,photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(),"emojione-android.ttf"))
                .build();

        coordinatorLayout = findViewById(R.id.coordinator);
//        btn_filters_list = findViewById(R.id.btn_filters_list);
        btn_edit = findViewById(R.id.btn_edit);
        btn_brush = findViewById(R.id.btn_brush);
        btn_emoji = findViewById(R.id.btn_emoji);
        btn_text = findViewById(R.id.btn_text);
        btn_sticker = findViewById(R.id.btn_sticker);
        btn_crop = findViewById(R.id.btn_crop);

        btn_undo = findViewById(R.id.btn_undo);
        btn_redo = findViewById(R.id.btn_redo);

        context = getApplicationContext();
        //=======================filter recyclerview================================================//

        recyclerView = findViewById(R.id.recycler_view);


        thumbnailItems = new ArrayList<>();
        adapter = new ThumbnailAdapter(thumbnailItems,this,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(adapter);
        loadImage();
        displayThumbnail(originalBitmap);

        //=======================bottom cardview onclick============================================//

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImageFragment = EditImageFragment.getInstance();//取得singleton實體
                editImageFragment.setListener(ImageEditActivity.this);//簽訂契約並實作方法，得到契約的參數
                editImageFragment.show(getSupportFragmentManager(),editImageFragment.getTag());
            }
        });

        btn_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                photoEditor.setBrushDrawingMode(true);
                //獲得該fragment的單一實體
                brushFragment = BrushFragment.getInstance();
                //MainActivity.this 指的是最上面我們 implement過的BrushFragmentListener
                //並且導入他的4個listener(onBrushSizeChanged,onBrushOpacityChanged.....)
                brushFragment.setListener(ImageEditActivity.this);
                brushFragment.show(getSupportFragmentManager(),brushFragment.getTag());
            }
        });
        btn_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiFragment = EmojiFragment.getInstance();
                emojiFragment.setListener(ImageEditActivity.this);
                emojiFragment.show(getSupportFragmentManager(),emojiFragment.getTag());
            }
        });
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textFragment = TextFragment.getInstance();
                textFragment.setListener(ImageEditActivity.this);
                textFragment.show(getSupportFragmentManager(),textFragment.getTag());
            }
        });
        btn_sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickerFragment = StickerFragment.getInstance();
                stickerFragment.setListener(ImageEditActivity.this);
                stickerFragment.show(getSupportFragmentManager(),stickerFragment.getTag());
            }
        });
        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                image_selected_uri = getIntent().getData();
                start_crop(image_selected_uri);
            }
        });

    }

    private void start_crop(Uri image_selected_uri) {
        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        //來源路徑uri 目標路徑uri
        UCrop uCrop = UCrop.of(image_selected_uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));
        uCrop.start(ImageEditActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ){
            if (requestCode == UCrop.REQUEST_CROP) {
                handelRequest(data);
            }
        } else if(resultCode ==UCrop.RESULT_ERROR){
            handelCropError(data);
        }
    }

    private void handelCropError(Intent data) {
        final Throwable cropError = UCrop.getError(data);
        if (cropError!= null){
            Toast.makeText(context, ""+cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "i dont know wtf", Toast.LENGTH_SHORT).show();
        }
    }

    private void handelRequest(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        if (resultUri!= null){
            photoEditorView.getSource().setImageURI(resultUri);
            Bitmap bitmap = ((BitmapDrawable)photoEditorView.getSource().getDrawable()).getBitmap();
            //剪裁完的bitmap，要重新賦予給originalBitmap (重置 bitmap)
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            filteredBitmap = originalBitmap;
            finalBitmap = originalBitmap;
            Log.e("croped",""+resultUri);

        }else{
            Toast.makeText(context, "cannot retrieve image", Toast.LENGTH_SHORT).show();
        }
    }

    //======================create options menu===============================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_edit,menu);
        return true;
    }
    //======================options menu onItemSelected===============================//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=  item.getItemId();
        if(id==R.id.action_open){
            Toast.makeText(context, "u clicked action_open", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.action_save){
            Toast.makeText(context, "u clicked action_save", Toast.LENGTH_SHORT).show();
            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(Bitmap saveBitmap) {

//                    ByteArrayOutputStream bytes = new ByteArrayOutputStream(); //also works
//                    saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), saveBitmap, "Title", null);
//                    Log.e("saveBitmap",path+"");
                    try {
                        //取得 content://media/external/images/media/891062
                        final String path = BitmapUtils.insertImage(getContentResolver(),saveBitmap,System.currentTimeMillis()+"_profile.jpg",null);
                        Log.e("saved path is",path);
                        String filePath;
                        Uri _uri = Uri.parse(path);
                        if (_uri != null && "content".equals(_uri.getScheme())) {
                            Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                            cursor.moveToFirst();
                            filePath = cursor.getString(0);//取得實體位址
                            cursor.close();
                        } else {
                            filePath = _uri.getPath();
                        }
                        Log.e("Chosen path = ","Chosen path = "+ filePath);//實體位址
                        Log.e("Chosen path = ","image position = "+ image_position);

                        //將照片的位置及uri 放入
                        Intent intent = new Intent();
                        intent.putExtra("position",image_position);
                        intent.putExtra("uri","file://"+filePath);

                        setResult(RESULT_OK, intent);
                        finish();
//                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Exception e) {

                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadImage() {
        Intent intent = getIntent();
        postingPictureUri = intent.getData();

        File file= new File(intent.getData().getPath());
        String path = file.getAbsolutePath();
        Log.e("file",path+",");

        //將圖片壓縮
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds= true;//拿到大圖後，先進行縮放
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize = BitmapUtils.calculateInSampleSize(options,500,500);//恰當的inSampleSize可以使BitmapFactory分配更少的空間
        options.inJustDecodeBounds = false;
        //fix image orientation
        int degree = BitmapUtils.readDegree(path);
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        //create fixed bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        Bitmap fixedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
        originalBitmap = fixedBitmap;
        Log.e("uri",""+postingPictureUri);

        //copy originalBitmap to other bitmap
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);

        photoEditorView.getSource().setImageBitmap(originalBitmap);

    }

    public void displayThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Bitmap thumbImg;
                if(bitmap==null){
                    thumbImg = BitmapUtils.getBitmapFromAssets(getApplicationContext(), ImageEditActivity.pictureName , 100,100);
                }else{
                    thumbImg = Bitmap.createScaledBitmap(bitmap,100,100,false);
                }

                if (thumbImg ==null) {
                    return;
                }


                ThumbnailsManager.clearThumbs();
                thumbnailItems.clear();
                //add normal bitmap first
                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImg;
                thumbnailItem.filterName = "Normal";

                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

                for(Filter filter: filters){
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbImg;
                    tI.filter = filter;
                    tI.filterName = filter.getName();

                    ThumbnailsManager.addThumb(tI);
                }
                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        new Thread( r).start();
    }



    //======================= INTERFACE METHODS===========================================//
    //=======================method in FilterListFragmentListener=========================//
    @Override
    public void onFilterSelected(Filter filter) {
        //fix crash
        //resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap =filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
    }
    //=======================method in EditImageFragmentListener=========================//
    //=======================在此獲得由EditImageFragment設定的參數=========================//
    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        Log.e("brightness",""+brightness);
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        constraintFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        Log.e("contrast",""+contrast);
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        Log.e("saturation",""+saturation);
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter(constraintFinal));
        Log.e("onEditCompleted","onEditCompleted");
        finalBitmap = myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterCleared() {
        photoEditorView.getSource().setImageBitmap(filteredBitmap.copy(Bitmap.Config.ARGB_8888,true));
    }
    //=======================method in BrushFragmentListener=========================//
    @Override
    public void onBrushSizeChanged(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChanged(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangeListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangeListener(boolean isEraser) {
        if (isEraser){
            photoEditor.brushEraser();
        }else{
            photoEditor.setBrushDrawingMode(true);
        }
    }

    //=======================method in EmojiFragmentListener=========================//
    //=======================在此獲得由EmojiFragment設定的參數=========================//
    @Override
    public void onEmojiSelected(String emoji) {
        photoEditor.addEmoji(emoji);
        emojiFragment.dismiss();
    }
    //=======================method in TextFragmentListener=========================//
    //=======================在此獲得由 TextFragment 設定的參數=========================//
    @Override
    public void onTextAdded(Typeface typeface, String text, int color) {
        photoEditor.addText(typeface,text,color);
        textFragment.dismiss();
    }
    //=======================method in StickerFragmentListener=========================//
    //=======================在此獲得由StickerFragment設定的參數=========================//
    @Override
    public void onStickerSelected(int sticker) {
        Drawable d = context.getResources().getDrawable(sticker);
        Bitmap bitmap = BitmapUtils.drawableToBitmap(d);
        photoEditor.addImage(bitmap);

        stickerFragment.dismiss();
    }


}