package com.medfd.bitdayrlivewallpaper;

import java.util.Calendar;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

public class BitDayR_Service extends WallpaperService  {
	
	private static final String DEBUG = "Wallpaper";
	
	@Override public Engine onCreateEngine() {
		return new BitDayR_Engine();
	}
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(DEBUG, "onCreate");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG, "onDestroy");
    }
    //---------------- ENGINE
    private class BitDayR_Engine extends Engine {
    	
    	//--------- Var
    	private static final String PREF = "Service_preference";
    	private boolean isVisible = true;
        private final Handler handler = new Handler();
		private float xOffset = 0.5f, yOffset = 0.5f;
		
		private Bitmap lastBackground = null, lastBackgroundScaled = null;

		private int lastLevel = -1, lastWidth = -1, lastHeight = -1;
        
        private final Runnable drawRunner = new Runnable() {
          @Override
          public void run() {
            draw();
          }

        };
        
        // Override methods
        @Override
        public void onVisibilityChanged(boolean visible) {
          this.isVisible = visible;
          if (visible) {
            handler.post(drawRunner);
          } else {
            handler.removeCallbacks(drawRunner);
          }
        }
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
          super.onSurfaceDestroyed(holder);
          this.isVisible = false;
          handler.removeCallbacks(drawRunner);
        }
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
            int width, int height) {
//          this.width = width;
//          this.height = height;
          super.onSurfaceChanged(holder, format, width, height);
        }
		@Override public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
			this.xOffset = xOffset; 
			this.yOffset = yOffset;
			draw();
		}
		@Override
		public void onDestroy() {
			super.onDestroy();
			handler.removeCallbacks(drawRunner);
		}
        //--------- Methods        
		public void draw() {
			
			if(isPreview()) {
				xOffset = yOffset = 0.5f;
			}
			
			final SurfaceHolder holder = getSurfaceHolder();
			
			Canvas canvas = null;

			try {
				canvas = holder.lockCanvas();

				canvas.drawColor(Color.BLACK);
				
				Resources resources = getResources();
				DisplayMetrics metrics = resources.getDisplayMetrics();
				Bitmap background = getBackground(resources);
				
				float 
					x = (metrics.widthPixels  - background.getWidth() ) * xOffset,
					y = (metrics.heightPixels - background.getHeight()) * yOffset;
				
				canvas.drawBitmap(background, x, y, null);
			} finally {
				if (canvas != null) holder.unlockCanvasAndPost(canvas);
			}
			
			handler.removeCallbacks(drawRunner);
		}
		private Bitmap createBitmapFillDisplay(Bitmap bitmap, float displayWidth, float displayHeight) {
			
			float 
				bitmapWidth  = bitmap.getWidth(), 
				bitmapHeight = bitmap.getHeight(),
				xScale 		 = displayWidth  / bitmapWidth,
				yScale 		 = displayHeight / bitmapHeight,
				scale 		 = Math.max(xScale, yScale),
				scaledWidth  = scale * bitmapWidth,
				scaledHeight = scale * bitmapHeight;
			
			Bitmap scaledImage = Bitmap.createBitmap((int) scaledWidth, (int) scaledHeight, Config.ARGB_8888);
			
			Canvas canvas = new Canvas(scaledImage);
			
			Matrix transformation = new Matrix();
			transformation.preScale(scale, scale);
			
			Paint paint = new Paint();
			paint.setFilterBitmap(true);
			
			canvas.drawBitmap(bitmap, transformation, paint);
			
			return scaledImage;
		}
		public Bitmap getBackground(Resources resources) {
			DisplayMetrics metrics = resources.getDisplayMetrics();
			
			int 
				currentWidth  = metrics.widthPixels,
				currentHeight = metrics.heightPixels,
				currentHour   = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
				currentLevel  = getLevel(currentHour);
			
			if(lastLevel != currentLevel) {
				int id = getBackgroundIdByLevel(currentLevel);
				lastBackground = BitmapFactory.decodeResource(resources, id);
			}
			
			if(lastLevel  != currentLevel
			|| lastWidth  != currentWidth
			|| lastHeight != currentHeight) {
				
				lastBackgroundScaled = createBitmapFillDisplay(
					lastBackground,
					currentWidth,
					currentHeight
				);
				
				lastLevel  = currentLevel;
				lastWidth  = currentWidth;
				lastHeight = currentHeight;
			}
			
			return lastBackgroundScaled;
		}
		private int getBackgroundIdByLevel(int level) {
			switch (level) {
			case 1 : return R.drawable.early_morning;
			case 2 : return R.drawable.mid_morning;
			case 3 : return R.drawable.late_morning;
			case 4 : return R.drawable.early_afternoon;
			case 5 : return R.drawable.mid_afternoon;
			case 6 : return R.drawable.late_afternoon;
			case 7 : return R.drawable.early_evening;
			case 8 : return R.drawable.mid_evening;
			case 9 : return R.drawable.late_evening;
			case 10 : return R.drawable.early_night;
			case 11 : return R.drawable.mid_night;
			default: return R.drawable.late_night;

			}
		}
		private int getLevel(int hour){
			int level = -1;
			
			if(hour >= 6 && hour < 9){
				level = 1;
			}
			if(hour >= 9 && hour < 12){
				level = 2;
			}
			if(hour >= 12 && hour < 15){
				level = 3;
			}
			if(hour >= 15 && hour < 16){
				level = 4;
			}
			if(hour >= 16 && hour < 17){
				level = 5;
			}
			if(hour >= 17 && hour < 18){
				level = 6;
			}
			if(hour >= 18 && hour < 19){
				level = 7;
			}
			if(hour >= 19 && hour < 20){
				level = 8;
			}
			if(hour >= 20 && hour < 21){
				level = 9;
			}
			if(hour >= 21 && hour < 24){
				level = 10;
			}
			if(hour >= 0 && hour < 2){
				level = 2;
			}
			
//			switch (hour) {
//			case 6 :  level = 1;
//			case 9 :  level = 2;
//			case 12 :  level = 3;
//			case 15 :  level = 4;
//			case 16 :  level = 5;
//			case 17 :  level = 6;
//			case 18 :  level = 7;
//			case 19 :  level = 8;
//			case 20 :  level = 9;
//			case 21 :  level = 10;
//			case 0 :  level = 11;
//			case 2 :   level = 12;
//			}
			return level;
		}
        
        
    } // -----------> END ENGINE        
}
