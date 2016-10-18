
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.myapplication.R;

/**
 * Created by Hy on 2016/8/8.
 */
public class RatingView extends View {
    private Context mContext;
    private int border_size = -1;// 边框厚度
    private int borderColor = -1;// 边框颜色
    private int fillColor = -1;  // 填充颜色
    private int imgRes;//图片资源

    private int maxStars = 5;//默认星星为5颗
    private int defColor = Color.RED;// 默认颜色
    private int defSize = 2;//默认边框宽度2px
    private String score = "0";//评分

    private int width = 0;// 控件的宽度
    private int height = 0;// 控件的高度

    public RatingView(Context context) {
        super(context);
        this.mContext = context;
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setAttributes(attrs);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        setAttributes(attrs);
    }

    /**
     * 获得自定义属性
     *
     * @param attrs
     */
    private void setAttributes(AttributeSet attrs) {
        TypedArray mArray = mContext.obtainStyledAttributes(attrs,
                R.styleable.RatingView);
        // 得到边框厚度，否则返回 defSize
        border_size = mArray.getDimensionPixelSize(R.styleable.RatingView_border_size, defSize);
        // 得到星星填充颜色，否则返回默认颜色
        fillColor = mArray.getColor(R.styleable.RatingView_fill_color, defColor);
        // 得到边框颜色，否则返回默认颜色
        borderColor = mArray.getColor(R.styleable.RatingView_border_color, defColor);
        //得到资源图片
        imgRes = mArray.getResourceId(R.styleable.RatingView_imgSrc, -1);
        //得到星星数量
        maxStars = mArray.getInt(R.styleable.RatingView_max_stars, maxStars);
        //得到评分
        score = mArray.getString(R.styleable.RatingView_score)==null?score:mArray.getString(R.styleable.RatingView_score).trim();
        mArray.recycle();// 回收mArray
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas); 必须去掉该行或注释掉，否则会出现两张图片
        // 得到传入的图片

        // 得到画布宽高
        width = getWidth();
        height = getHeight();
        int radius = (width < height ? width : height) / 2;

        // 将图片转为位图
        Bitmap mBitmap = getBitmap(1);
        Bitmap shapeBitmap = drawShapeBitmap(mBitmap, radius);
        double mScore=Double.parseDouble(score);
        mScore=mScore>maxStars?maxStars:mScore;

        int fullStars= (int) mScore;
        double halfStar=mScore-fullStars;
        int emptyStars=(maxStars-fullStars-halfStar==0)?0:maxStars-fullStars-(halfStar!=0?1:0);
        //画实心
        for (int i = 0; i < fullStars; i++) {
            if (width < height) {
                canvas.drawBitmap(shapeBitmap, 0, height / 2 - radius, null);
            } else {
                canvas.drawBitmap(shapeBitmap, (width - radius * 10) / 2 - radius, 0, null);
            }
            canvas.translate(radius * 2 + 5, 0);
        }
        //画半实心的
        if (halfStar!=0){
            mBitmap = getBitmap(halfStar);
            shapeBitmap = drawShapeBitmap(mBitmap, radius);
            if (width < height) {
                canvas.drawBitmap(shapeBitmap, 0, height / 2 - radius, null);
            } else {
                canvas.drawBitmap(shapeBitmap, (width - radius * 10) / 2 - radius, 0, null);
            }
            if (emptyStars!=0)
                canvas.translate(radius * 2 + 10, 0);
        }

        if (emptyStars !=0) {
            //画空心
            mBitmap = getBitmap(0f);
            shapeBitmap = drawShapeBitmap(mBitmap, radius);
            for (int i =0; i < emptyStars; i++) {
                if (width < height) {
                    canvas.drawBitmap(shapeBitmap, 0, height / 2 - radius, null);
                } else {
                    canvas.drawBitmap(shapeBitmap, (width - radius * 10) / 2 - radius, 0, null);
                }
                canvas.translate(radius * 2 + 10, 0);
            }
        }

    }

    /**
     *
     * @param bmp
     * @param radius 
     * @return
     */
    private Bitmap drawShapeBitmap(Bitmap bmp, int radius) {

        Bitmap outputbmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(outputbmp);// 创建一个相同大小的画布
        Paint paint = new Paint();// 定义画笔
        paint.setAntiAlias(true);// 设置抗锯齿
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(borderColor);
        paint.setStrokeWidth(3);
        canvas.drawARGB(0, 0, 0, 0);

        // 如果绘制的形状为五角星形
        Path path = new Path();
        float radian = degree2Radian(36);// 36 五角星外点与相邻内点的夹角角度
        float radius_in = (float) (radius * Math.sin(radian / 2) / Math.cos(radian)); // 中间五边形的半径
        path.moveTo((float) (radius * Math.cos(radian / 2)), 0);// 此点为多边形的起点
        path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.sin(radian)), (float) (radius - radius * Math.sin(radian / 2)));
        path.lineTo((float) (radius * Math.cos(radian / 2) * 2), (float) (radius - radius * Math.sin(radian / 2)));
        path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.cos(radian / 2)), (float) (radius + radius_in * Math.sin(radian / 2)));
        path.lineTo((float) (radius * Math.cos(radian / 2) + radius * Math.sin(radian)), (float) (radius + radius * Math.cos(radian)));
        path.lineTo((float) (radius * Math.cos(radian / 2)), (radius + radius_in));
        path.lineTo((float) (radius * Math.cos(radian / 2) - radius * Math.sin(radian)), (float) (radius + radius * Math.cos(radian)));
        path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.cos(radian / 2)), (float) (radius + radius_in * Math.sin(radian / 2)));
        path.lineTo(0, (float) (radius - radius * Math.sin(radian / 2)));
        path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.sin(radian)), (float) (radius - radius * Math.sin(radian / 2)));
        path.close();// 使这些点构成封闭的多边形
        canvas.drawPath(path, paint);

        // 设置Xfermode的Mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bmp, 0, 0, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawPath(path, paint);
        bmp = null;
        return outputbmp;
    }

    /**
     * 角度转弧度公式
     *
     * @param degree
     * @return
     */
    private float degree2Radian(int degree) {
        return (float) (Math.PI * degree / 180);
    }

    /**
     * 如果图片为圆形，这该方法为画出圆形图片的有色边框
     *
     * @param canvas
     * @param radius 边框半径
     * @param color  边框颜色
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        // TODO Auto-generated method stub
        Paint paint = new Paint();

        paint.setAntiAlias(true);// 抗锯齿
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);// 设置画笔颜色
        paint.setStyle(Paint.Style.STROKE);// 设置画笔的style为STROKE：空心
        paint.setStrokeWidth(border_size);// 设置画笔的宽度
        // 画出空心圆，也就是边框
        canvas.drawCircle(width / 2, height / 2, radius, paint);
    }


    //没有图片资源画星星底色
    public Bitmap getBitmap(double steps) {
        int rule = width < height ? width : height;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getRight(), getBottom(), paint);
        paint.setColor(fillColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawRect(0, 0, (float) ((rule - (6 * steps)) * steps), getBottom(), paint);
        canvas.save();
        return bitmap;
    }

    public void setScore(String score) {
        this.score = score; 
        invalidate();
    }

}
