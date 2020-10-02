package info.androidhive.firebaseauthapp.htmlTextViewUtil;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.ViewPager;

public class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private boolean mOnce = false;

    private float mInitScale;

    private float mMidScale;

    private float mMaxScale;

    private Matrix mScaleMatrix = null;


    private ScaleGestureDetector mScaleGestureDetector = null;


    private int mLastPointerCount;


    private float mLastX;
    private float mLastY;

    private int mTouchSlop;

    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;


    private GestureDetector mGestureDetector = null;
    private boolean isAutoScale;



    public ZoomImageView(Context context) {
        this(context , null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mScaleMatrix = new Matrix();
        setScaleType(ImageView.ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(
                context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.e("onDoubleTap","onDoubleTap");//當雙擊時觸發
                        if(isAutoScale == true) {
                            return true;
                        }

                        float x = e.getX();
                        float y = e.getY();


                        if(getCurrentScale() < mMidScale) {
                            //目前為縮小，點擊放大
                            Log.e("onDoubleTap","目前為縮小，點擊放大");
                            postDelayed(new AutoScaleRunnable(mMidScale, x, y) , 16);
                            isAutoScale = true;
                        }
                        else {
                            //目前為放大，點擊縮小
                            Log.e("onDoubleTap","else");
                            postDelayed(new AutoScaleRunnable(mInitScale, x, y) , 16);
                            isAutoScale = true;
                        }

                        return true;
                    }
                });
    }



    @Override
    public void onGlobalLayout() {

        Log.e("onGlobalLayout","test");
        //ȫ�ֵĲ�������Ժ󣬻�����������
        if(mOnce == false) {
            //只有第一次點開圖片才會觸發功能
            Log.e("mOnce == false","triggered");
            //�ؼ��Ŀ�͸�
            int width = getWidth();
            int height = getHeight();

            //�õ����ǵ�ͼƬ���Լ���͸�
            Drawable drawable = getDrawable();
            if(drawable == null) {
                return;
            }
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();

            //�����������
            float scale = 1.0f;
            if(drawableWidth > width && drawableHeight < height) { //���ͼƬ�ܿ����߶ȵ�
                Log.e("case 01","drawableWidth > width && drawableHeight < height");
                scale = width * 1.0f / drawableWidth;
            }
            if(drawableWidth < width && drawableHeight > height) { //���ͼƬ��խ�����Ǹ߶Ⱥܸ�
                Log.e("case 02","drawableWidth < width && drawableHeight > height");
                scale = height * 1.0f / drawableHeight;
            }
            if(drawableWidth > width && drawableHeight > height) { //���ͼƬ�Ŀ�͸߶��ܴ�
                Log.e("case 03","drawableWidth > width && drawableHeight > height");
                scale = Math.min(width * 1.0f / drawableWidth , height * 1.0f / drawableHeight);
            }
            if(drawableWidth < width && drawableHeight < height) { //���ͼƬ�Ŀ�͸߶���С
                Log.e("case 04","drawableWidth < width && drawableHeight < height ,觸發");
                Log.e("width",""+width);
                Log.e("drawableWidth",""+drawableWidth);
                Log.e("height",""+height);
                Log.e("drawableHeight",""+drawableHeight);

                scale = Math.min(width / drawableWidth , height / drawableHeight);
                //original : 2.278481 //
                Log.e("scale",""+scale);
            }

            //�õ��˳�ʼ��ʱ���ŵı���
            mInitScale = scale;
            mMidScale = mInitScale * 2;
            mMaxScale = mInitScale * 4;

            //��ͼƬ�ƶ����ؼ�������
            int dx = Math.round((width  - drawableWidth)/2 );
            int dy = Math.round((height  - drawableHeight) / 2);
            Log.e("dx",""+dx);
            Log.e("dy",""+dy);
//			Matrix提供了四种操作：
//			translate(平移)
//			rotate(旋转)
//			scale(缩放)
//			skew(错切)
//
//			pre是在队列最前面插入，post是在队列最后面追加，而set先清空队列在添加。

            //todo:初次載入圖片大小改這
            //����ƽ��
            Log.e("Math.round(dx/4)",""+Math.round(dx/8));
            //mScaleMatrix.postTranslate(Math.round(dx/2), Math.round(dy/2));
            //��������
            mScaleMatrix.postScale(mInitScale/2 , mInitScale/2 );
            //�����������
            setImageMatrix(mScaleMatrix);

            mOnce = true;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }



    /**
     * �õ���ǰ�����ű���
     */
    public float getCurrentScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }




    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Log.e("onScale","triggered");
        float scale = getCurrentScale();
        //�õ���ָ���غ�õ������ŵ�ֵ��������1�㼸��Ҳ������0�㼸
        float scaleFactor = detector.getScaleFactor();

        if(getDrawable() == null) {
            Log.e("onScale : ","getDrawable() == null");
            return true;
        }

        //ǰ���ǡ���Ŵ󡱣������ǡ�����С����������е������ŷ�Χ�Ŀ���
        if( (scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f) ) {
            if(scale * scaleFactor < mInitScale) {
                Log.e("onScale : ","scale*scaleFactor=mInitScale");
                scaleFactor = mInitScale / scale; //Ҳ������scale*scaleFactor=mInitScale
            }
            if(scale * scaleFactor > mMaxScale) {
                Log.e("onScale : ","scale*scaleFactor=mMaxScale");
                scaleFactor = mMaxScale / scale; //Ҳ������scale*scaleFactor=mMaxScale
            }
        }

        //��������detector.getFocusX()��detector.getFocusY()������ָ���ص����ĵ�
        mScaleMatrix.postScale(scaleFactor , scaleFactor , detector.getFocusX() , detector.getFocusY());

        checkBorderAndCenterWhenScale();

        setImageMatrix(mScaleMatrix);

        return true;
    }


    /**
     * ���ͼƬ�Ŵ����С�Ժ�Ŀ�͸ߣ��Լ�left��right��top��bottom
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if(drawable != null) {
            rectF.set(0 , 0 , drawable.getIntrinsicWidth() , drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }



    /**
     * �����ŵ�ʱ�򣬽��б߽�Ŀ��ƣ��Լ����ǵ�λ�õĿ���
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rect = getMatrixRectF();

        //��ֵ
        float deltaX = 0.0f;
        float deltaY = 0.0f;

        //�ؼ��Ŀ�Ⱥ͸߶�
        int width = getWidth();
        int height = getHeight();

        //�аױ߳��־���ƽ�Ʋ��ױ�
        if(rect.width() >= width) {
            if(rect.left > 0) { //�������п�϶����������Ҫ�ֲ�
                deltaX = -rect.left; //��ֵ����ʾӦ�������ƶ�
            }
            if(rect.right < width) { //����ұ��п�϶����������Ҫ�ֲ�
                deltaX = width - rect.right; //��ֵ����ʾӦ�������ƶ�
            }
        }
        if(rect.height() >= height) {
            if(rect.top > 0) {
                deltaY = -rect.top;
            }
            if(rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }

        //�����Ȼ�߶�С�ڿؼ��Ŀ�Ȼ�߶ȣ��;���
        if(rect.width() < width) {
            deltaX = width / 2f - rect.right + rect.width() / 2f;
        }
        if(rect.height() < height) {
            deltaY = height / 2f - rect.bottom + rect.height() / 2f;
        }

        //��֮ǰ�õ���ƽ�����ݸ��µ�mScaleMatrix��
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {

        //������һ��Ҫ��Ϊ����true
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        mScaleGestureDetector.onTouchEvent(event);

        //��¼���ĵ��λ��
        float x = 0;
        float y = 0;

        //�õ���㴥�ص�����
        int pointerCount = event.getPointerCount();

        for(int i = 0; i < pointerCount ; i++) {
            //����֮����Ҫ�ۼ���Ϊ���������ƽ��ֵ
            x += event.getX(i);
            y += event.getY(i);
        }

        //�������ĵ��λ����ͨ������ƽ��ֵԼ���ڳ�����
        x /= pointerCount;
        y /= pointerCount;

        if(mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    if(getParent() instanceof ViewPager) {
                        //���󸸿ؼ������������ص�ǰ�Ŀؼ�
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    if(getParent() instanceof ViewPager) {
                        //���󸸿ؼ������������ص�ǰ�Ŀؼ�
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                float deltaX = x - mLastX;
                float deltaY = y - mLastY;
                if(isCanDrag == false) {
                    isCanDrag = isMoveAction(deltaX , deltaY);
                }
                if(isCanDrag == true) {
                    //�����������ͼƬ���ƶ�
                    if(getDrawable() != null) {
                        isCheckLeftAndRight = true;
                        isCheckTopAndBottom = true;
                        if(rectF.width() < getWidth()) { //���ͼƬ�Ŀ��<�ؼ��Ŀ��
                            isCheckLeftAndRight = false;
                            deltaX = 0; //�Ͳ���������ƶ�
                        }
                        if(rectF.height() < getHeight()) { //���ͼƬ�ĸ߶�<�ؼ��ĸ߶�
                            isCheckTopAndBottom = false;
                            deltaY = 0; //�Ͳ����������ƶ�
                        }
                        //֮����Ҫ�������ţ�����Ϊ�ƶ���Ŀ����Ϊ����ʾ��û���ڿؼ�����ʾ�Ķ���
                        mScaleMatrix.postTranslate(deltaX, deltaY);
                        //�����ƶ��ı߽�
                        checkBorderWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                mLastPointerCount = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }

        return true;
    }



    /**
     * ����ƽ��ͼƬ�ı߽�
     */
    private void checkBorderWhenTranslate() {

        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        //�ؼ��Ŀ�͸�
        int width = getWidth();
        int height = getHeight();

        if(rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }
        if(rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }
        if(rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }
        if(rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }



    /**
     * �ж��Ƿ��ƶ���
     */
    private boolean isMoveAction(float deltaX, float deltaY) {

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) > mTouchSlop;

    }


    /**
     * �Զ��Ŵ�����С
     */
    //圖片霜及放大的方法
    private class AutoScaleRunnable implements Runnable {

        //���ŵ�Ŀ��ֵ
        private float mTargetScale;
        //���ŵ����ĵ�
        private float x;
        private float y;

        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        //��ʱ�ı���
        private float tmpScale;



        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;

            if(getCurrentScale() < mTargetScale) {
                tmpScale = BIGGER; //Ŀ������Ŵ�
            }
            else if(getCurrentScale() > mTargetScale) {
                tmpScale = SMALL; //Ŀ��������С
            }
        }



        @Override
        public void run() {

            //��������
            mScaleMatrix.postScale(tmpScale , tmpScale , x , y);
            checkBorderAndCenterWhenScale();
            //將圖片放大
            setImageMatrix(mScaleMatrix);

            float currentScale = getCurrentScale();
            //if�е������ǣ����û�дﵽĿ��ֵ����һֱͨ��postDelayed()ִ��run()������ֱ������elseΪֹ
            if( (tmpScale > 1.0f && currentScale < mTargetScale) || (tmpScale < 1.0f && currentScale > mTargetScale) ) {
                postDelayed(this , 16); //��this���Ǵ��Լ�
            }
            else {
                //������Ϊ���ǵ�Ŀ��ֵ
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }

    }
}
