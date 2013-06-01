package fr.vergne.livingwallpaper;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class LivingWallpaper extends WallpaperService {

	private final Logger logger = Logger.getLogger(LivingWallpaper.class
			.getName());

	@Override
	public Engine onCreateEngine() {
		logger.setLevel(Level.ALL);
		return new LiveWallpaperEngine();
	}

	private class LiveWallpaperEngine extends Engine {

		private BotLocation botLocation = new BotLocation();
		private int updateFrequencyInMs = 10;
		private final Handler handler = new Handler();
		private final String PREF_SPEED = getResources().getString(
				R.string.pref_speed_key);
		private final Runnable botRunner = new Runnable() {
			@Override
			public void run() {
				botLocation.moveToTarget();
				if (visible) {
					draw();
				} else {
					// save resources
				}
				handler.postDelayed(botRunner, updateFrequencyInMs);
			}
		};

		private boolean visible = true;

		public LiveWallpaperEngine() {
			handler.post(botRunner);
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(LivingWallpaper.this);
			prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

				@Override
				public void onSharedPreferenceChanged(SharedPreferences prefs,
						String key) {
					if (key.equals(PREF_SPEED)) {
						updateSpeed();
					} else {
						throw new IllegalArgumentException(key
								+ " is not managed.");
					}
				}

			});
			updateSpeed();
		}

		private void updateSpeed() {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(LivingWallpaper.this);
			Integer speed = Integer.valueOf(prefs.getString(PREF_SPEED,
					getResources().getString(R.string.pref_speed_default)));
			botLocation.setPixelsPerSecond(Math.round((float) speed
					* androidPic.getWidth()));
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			handler.removeCallbacks(botRunner);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			botLocation.setMaxX(width);
			botLocation.setMaxY(height);
			botLocation.setX(width / 2);
			botLocation.setY(height / 2);
			botLocation.resetTarget();

			super.onSurfaceChanged(holder, format, width, height);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			botLocation.setTarget((int) event.getX(), (int) event.getY());
			super.onTouchEvent(event);
		}

		Bitmap androidPic = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					canvas.drawColor(Color.WHITE);
					float left = botLocation.getX()
							- (androidPic.getWidth() / 2);
					float top = botLocation.getY()
							- (androidPic.getHeight() / 2);
					canvas.setDensity(androidPic.getDensity());
					canvas.drawBitmap(androidPic, left, top, null);
				} else {
					throw new RuntimeException("Impossible to lock the canvas.");
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				} else {
					// nothing to unlock
				}
			}
		}
	}

}
