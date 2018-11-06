package com.eros.framework.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Carry on 2017/8/21.
 */

public class ImageUtil {

	/**
	 * get a bitmap by path
	 */
	public static Bitmap getBitmap(String path, Context context) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}


	//计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * zoom image by targetWidth and targetHeight
	 */
	public static Bitmap zoomImage(Bitmap bitmap, float targetWidth, float targetHeight) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (width > 0 && height > 0) {
			float widthScale = targetWidth / width;
			float heightScale = targetHeight / height;
			Matrix matrix = new Matrix();
			matrix.postScale(widthScale, heightScale);
			Bitmap getBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
					matrix,
					true);
			return getBitmap;

		}
		return null;
	}


	/**
	 * 上传图片前，对图片进行压缩。
	 *
	 * @param bitmap   原始图片
	 * @param newWidth 前端指定的压缩宽,传递0,按照原图压缩，如果原图大于最大上线828,按照828比例压缩。
	 */
	public static String zoomImage(Context context, Bitmap bitmap, int newWidth, int
			biggestWidth, float degree, String filename) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (newWidth <= 0) {//Js 返回0 上传原始图片、
			if (width > biggestWidth) {
				newWidth = biggestWidth;
			} else {
				newWidth = width;
			}
		}
		float scaleWidth = ((float) newWidth) / width;
		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		matrix.postRotate(degree);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return saveBitmap(newBitmap, filename, context);
	}


	public static Bitmap zooImage(Context context, Bitmap bitmap, float scale) {
		if (bitmap == null || bitmap.isRecycled()) return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width > 0 && height > 0) {
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			Bitmap getBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
					matrix,
					true);
			return getBitmap;

		}
		return null;
	}

	/**
	 * 保存bitmap
	 *
	 * @param bmp     图片资源
	 * @param path    保存路径
	 * @param context 上下文
	 * @return 图片保存的路径
	 */
	public static String saveBitmap(Bitmap bmp, String path, Context context) {
		File dest = new File(path);
		try {
			dest.createNewFile();
		} catch (IOException e) {
			Log.e("BaseImageManager", "保存压缩图片Bitmap转Sd卡本地出错！！！");
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(dest);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bmp.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dest.getAbsolutePath();
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {


		Bitmap bitmap = Bitmap.createBitmap(

				drawable.getIntrinsicWidth(),

				drawable.getIntrinsicHeight(),

				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;

	}


	/**
	 * 读取照片exif信息中的旋转角度
	 *
	 * @param path 照片路径
	 * @return 角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
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

	public static Bitmap toturn(Bitmap img, String path) {
		Matrix matrix = new Matrix();
		matrix.postRotate(readPictureDegree(path));
		int width = img.getWidth();
		int height = img.getHeight();
		img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
		return img;
	}

}
