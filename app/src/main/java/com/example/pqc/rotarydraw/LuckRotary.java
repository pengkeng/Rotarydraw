package com.example.pqc.rotarydraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LuckRotary extends SurfaceView implements SurfaceHolder.Callback ,Runnable{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private  Thread thread;
    private  boolean isRunning;

    private Bitmap mBgBitmap = BitmapFactory.decodeResource( getResources(),R.drawable.bg2 );
    //绘制图片的画笔
    private Paint mArcPaint;
    //绘制文字的画笔
    private Paint mTextPaint;

    private  float mTextSize = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics() );

    private Bitmap[] mImgsBitmap;
    //圆盘范围
    private RectF mRange = new RectF( );
    //圆盘直径
    private  int mRadius;


    //转动速度
    private  double mSpeed = 0;
    //角度
    private volatile  float mStartAngle = 0;
    //转盘中心
    private  int mCenter;
    //取padding最小值
    private  int mPadding;
    //奖项名称
    private  String[] mStrs= new String[]{"单反相机","IPAD","恭喜发财","IPHONE","服装","恭喜发财"};
    //奖项图片
    private  int[] mImage = new int[]{R.drawable.danfan,R.drawable.ipad,R.drawable.f015,R.drawable.iphone,R.drawable.meizi,R.drawable.f040};
    //奖项颜色
    private  int[] mColor = new int[]{0xFFFFC300,0XFFF17e01,0xFFFFC300,0XFFF17e01,0xFFFFC300,0XFFF17e01};
    //奖项数量
    private  int mItemCount = 6;
    //停止按钮
    private  boolean isShouldEnd;


    public LuckRotary(Context context) {
        this( context,null );
    }




    public LuckRotary(Context context, AttributeSet attrs) {
        super( context, attrs );
        mHolder = getHolder();
        mHolder.addCallback( this );
        //可获去焦点
        setFocusable( true );
        setFocusableInTouchMode( true );
        //设置常量
        setKeepScreenOn( true );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        int width = Math.min( getMeasuredWidth(),getMeasuredHeight() );
        mPadding = getPaddingLeft();
        //半径
        mRadius = width -mPadding*2;
        //中心点
        mCenter = width/2;
        setMeasuredDimension( width,width );
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化绘制图片画笔
        mArcPaint = new Paint(  );
        mArcPaint.setAntiAlias( true );
        mArcPaint.setDither( true );
        //初始化绘制文字画笔
        mTextPaint = new Paint(  );
        mTextPaint.setColor( 0xffffffff );
        mTextPaint.setTextSize( mTextSize );
        //设置圆盘范围
        mRange = new RectF( mPadding,mPadding,mPadding+mRadius,mPadding+mRadius );

        //初始化图片
        mImgsBitmap = new Bitmap[mItemCount];
        for(int i = 0;i<mItemCount;i++){
            mImgsBitmap[i] = BitmapFactory.decodeResource( getResources() ,mImage[i]);
        }
        isRunning =true;
        thread = new Thread( this );
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning =false;
    }

    @Override
    public  void  run(){
        //不断绘制
        while (isRunning){
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if(end-start<50) {
                try {
                    Thread.sleep( 50-(end-start) );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void draw() {

        try {
            mCanvas = mHolder.lockCanvas();
            if(mCanvas!=null){
                //绘制背景
                drawBg();
                //绘制盘块
                float tmpAngle = mStartAngle;
                float sweepAngle = 360/mItemCount;
                for(int i = 0;i<mItemCount;i++){
                    mArcPaint.setColor( mColor[i]);
                    mCanvas.drawArc( mRange,tmpAngle,sweepAngle,true,mArcPaint );

                    //绘制文本
                    drawText(tmpAngle,sweepAngle,mStrs[i]);
                    //绘制图片
                    drawIcon(tmpAngle,mImgsBitmap[i]);
                    tmpAngle+=sweepAngle;

                }
                mStartAngle+=mSpeed;
                if(isShouldEnd){
                    mSpeed -= 1;
                }
                if(mSpeed<=0){
                    mSpeed = 0;
                    isShouldEnd =false;
                }


            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if(mCanvas!=null){
                mHolder.unlockCanvasAndPost( mCanvas );
            }
        }
    }

    public  void luckyStart(int index){
        float angle = 360/mItemCount;
        //1->150-210
        //0->210-270

        float from = 270-(index+1)*angle;
        float end = from + angle;

        //设置停下来旋转的距离
        float targetFrom = 4*360+from;
        float targetEnd = 4*360+end;

        float v1 = (float) ((-1 + Math.sqrt(1+8*targetFrom ))/2);
        float v2 = (float) ((-1 + Math.sqrt(1+8*targetEnd ))/2);

        mSpeed = v1 +Math.random()*(v2-v1);

        //mSpeed = 30;
        isShouldEnd = false;
    }
    public void luckyEnd(){
        mStartAngle = 0;
        isShouldEnd = true;
    }
    public boolean isStart(){
        return  mSpeed != 0;
    }
    public boolean isShouldEnd(){
        return  isShouldEnd;
    }

    //绘制图片
    private void drawIcon(float tmpAngle, Bitmap bitmap) {
        int imgWidth = mRadius/8;
        float angle = (float) ((tmpAngle + 360/mItemCount/2)*Math.PI/180);
        int x = (int) (mCenter + mRadius/2/2*Math.cos(angle));
        int y = (int) (mCenter + mRadius/2/2*Math.sin(angle));
        //确定图片位置
        Rect rect = new Rect( x - imgWidth/2,y - imgWidth/2, x + imgWidth/2,y+ imgWidth/2 );
        mCanvas.drawBitmap( bitmap,null,rect,null );

    }

    //绘制盘块文本
    private void drawText(float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();
        path.addArc( mRange,tmpAngle,sweepAngle );

        float textWidth = mTextPaint.measureText( mStr );
        int hOffset = (int)(mRadius*Math.PI/mItemCount/2-textWidth/2);
        int vOffset = mRadius/2/6;
        mCanvas.drawTextOnPath( mStr,path,hOffset,vOffset,mTextPaint );


    }

    //绘制背景
    private void drawBg() {
        mCanvas.drawColor( 0xffffffff );
        mCanvas.drawBitmap( mBgBitmap,null,new Rect( mPadding/2,
                mPadding/2,getMeasuredHeight()-mPadding/2,
                getMeasuredHeight()-mPadding/2 ),null);

    }
}
