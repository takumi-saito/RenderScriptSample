package com.example.saito.renderscriptsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

/**
 * Created by saito on 2016/09/14.
 */
public class ImageUtil {

    // ぼかし値
    private static float MAX_RADIUS = 25f;

    /**
     * ビットマップ複製処理
     * @return
     */
    public static Bitmap copyBitmap(Context context, Bitmap bitmap) {
        int defaultDownSampling = 1;
        return copyBitmap(context, bitmap, defaultDownSampling);
    }
    public static Bitmap copyBitmap(Context context, Drawable drawable) {
        int defaultDownSampling = 1;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        return copyBitmap(context, bitmap, defaultDownSampling);
    }
    public static Bitmap copyBitmap(Context context, Bitmap bitmap, int sampling) {
        if (null == bitmap) return null;
        // 元のbitmapは不変で加工できないため、いったん可変で複製する
        Bitmap copyBitmap =
                bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(copyBitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(copyBitmap, 0, 0, paint);
        return copyBitmap;
    }

    public static Drawable blurDrawable(Context context, Drawable image) {
        int maxRadius = 25;
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
        Bitmap blurBitmap = blurBitmap(context, bitmap, maxRadius);
        return new BitmapDrawable(blurBitmap);
    }
    public static Drawable blurGetDrawableFromBitmap(Context context, Bitmap image) {
        int maxRadius = 25;
        Bitmap blurBitmap = blurBitmap(context, image, maxRadius);
        return new BitmapDrawable(blurBitmap);
    }
    public static Bitmap blurBitmap(Context context, Bitmap image, int mRadius) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(mRadius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    /**
     * blurその1
     * @param context
     */
    public static Bitmap process1(Context context, Bitmap bmpOri) {
        Bitmap bmp = RGB565toARGB888(bmpOri);

        final RenderScript rs = RenderScript.create( context );
        Allocation alloc = Allocation.createFromBitmap(rs, bmp);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, alloc.getElement());
        blur.setRadius(MAX_RADIUS); // ブラーする度合い
        blur.setInput(alloc);
        blur.forEach(alloc);
        alloc.copyTo(bmp); // 加工した画像をbmpに移す
        return bmp;
    }

    /**
     * blurその2
     * @param context
     * @param bmpOri
     * @return
     */
    public static Bitmap process2(Context context, Bitmap bmpOri) {
        Bitmap bmp = RGB565toARGB888(bmpOri);

        final RenderScript rs = RenderScript.create( context );
        final Allocation input = Allocation.createFromBitmap( rs, bmp, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
        final Allocation output = Allocation.createTyped( rs, input.getType() );
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );
        script.setRadius(MAX_RADIUS);
        script.setInput( input );
        script.forEach( output );
        output.copyTo( bmp );
        return bmp;
    }

    /**
     * ブラーその3
     * @param ctx
     * @param bmpOri
     * @return
     */
    public static Bitmap process3(Context ctx, Bitmap bmpOri) {
        Bitmap input = RGB565toARGB888(bmpOri);

        RenderScript rsScript = RenderScript.create(ctx);
        Allocation alloc = Allocation.createFromBitmap(rsScript, input);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, Element.U8_4(rsScript));
        blur.setRadius(MAX_RADIUS);
        blur.setInput(alloc);
        Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);
        blur.forEach(outAlloc);
        outAlloc.copyTo(result);
        rsScript.destroy();
        return result;
    }

    /**
     * ブラーその4
     * @param context
     * @param bmpOri
     * @return
     */
    public static Bitmap process4(Context context, Bitmap bmpOri) {
        Bitmap bmp = RGB565toARGB888(bmpOri);

        final RenderScript rs = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(rs, bmp, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(MAX_RADIUS);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bmp);
        return bmp;
    }

    /**
     * RGB565をARGB888に変換する
     * @param img
     * @return
     */
    public static Bitmap RGB565toARGB888(Bitmap img) {
        int numPixels = img.getWidth()* img.getHeight();
        int[] pixels = new int[numPixels];
        // JPEGのピクセル取得
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        // オプション指定し、Bitmap作成
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        // ピクセルセット
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}
