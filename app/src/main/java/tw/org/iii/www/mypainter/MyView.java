package tw.org.iii.www.mypainter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016/9/13.
 */
public class MyView extends View {//宣告變數
    private LinkedList<LinkedList<HashMap<String, Float>>> lines;
    private Resources res;
    private boolean isInit;
    private int viewW, viewH;
    private Bitmap bmpBall, bmpBg;
    private Matrix matrix;
    private Timer timer;
    private float ballX, ballY, ballW, ballH, dx, dy;//物體移動要具備的變數

    public MyView(Context context, AttributeSet attrs) {//產生物件
        super(context, attrs);
        lines = new LinkedList<>();
        res = context.getResources();
        matrix = new Matrix();
        timer = new Timer();
    }

    Timer getTimer() {return timer; }//待查

    private void init() {//物件比例縮放
        viewW = getWidth();
        viewH = getHeight();
        float ballW = viewW / 8f, ballH = ballW;//物件縮為視窗的比例縮放

        bmpBg = BitmapFactory.decodeResource(res, R.drawable.bg);//背景圖
        bmpBg = resizeBitmap(bmpBg, viewW, viewH);//背景徒長寬

        bmpBall = BitmapFactory.decodeResource(res, R.drawable.ball);//物件圖
        bmpBall = resizeBitmap(bmpBall, ballW, ballH);//物件圖長寬

        dx = dy = 10;

        timer.schedule(new RefreshView(), 0, 60);//待查
        timer.schedule(new BallTask(), 1000 ,100);

        isInit = true;
    }

    private Bitmap resizeBitmap(Bitmap src, float newW, float newH) {
        matrix.reset();//用來圖片縮放 旋轉 移動的操作
        matrix.postScale(newW / src.getWidth(), newH / src.getHeight());
        bmpBall = Bitmap.createBitmap(src, 0, 0, src.getWidth()
                , src.getHeight(), matrix, true);//ture 跟false差別
        return bmpBall;
    }

    @Override
    protected void onDraw(Canvas canvas) {//繪畫
        super.onDraw(canvas);
        if (!isInit) init();


        canvas.drawBitmap(bmpBg, 0,0,null);//球的起始點

        canvas.drawBitmap(bmpBall, ballX, ballY,null);//球開始移動(座標)


        Paint p = new Paint();//小畫家的使用
        p.setColor(Color.BLUE);
        p.setStrokeWidth(4);
        for (LinkedList<HashMap<String,Float>> line:lines) {//複數線
            for (int i = 1; i < line.size(); i++) {//使用FOR迴圈來達成可以畫多條線的功能
                canvas.drawLine(line.get(i - 1).get("x"),
                        line.get(i - 1).get("y"),
                        line.get(i).get("x"), line.get(i).get("y"), p);
            }
        }
    }

    private class RefreshView extends TimerTask {//物件移動的功能
        @Override
        public void run() {
            //invalidate();
            postInvalidate();//球觸發移動的條件
        }
    }

    private class BallTask extends TimerTask {//讓物體在程式視窗內運轉的公式
        @Override
        public void run() {
            if (ballX < 0 || ballX + ballW > viewW) dx *= -1;
            if (ballY < 0 || ballY + ballH > viewH) dy *= -1;
            ballX += dx;
            ballY += dy;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//待查
        float ex = event.getX(), ey = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            doTouchDown(ex, ey);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            doTouchMove(ex, ey);
        }
        return true;
    }

    private void doTouchDown(float x, float y) {//讓畫筆沒點擊時移動不會有繪畫功能
        LinkedList<HashMap<String, Float>> line =
                new LinkedList<>();
        lines.add(line);
        addPoint(x, y);
    }

    private void doTouchMove(float x, float y) {//畫筆移動時
        addPoint(x, y);
    }

    private void addPoint(float x, float y) {//讓畫筆每次點擊下去繪畫會出現線條的功能
        HashMap<String, Float> point =
                new HashMap<>();
        point.put("x", x);
        point.put("y", y);
        lines.getLast().add(point);
        invalidate();
    }
}
