package fr.vergne.livingwallpaper;

import java.util.Map;
import java.util.WeakHashMap;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import fr.vergne.livingwallpaper.bot.Bot;
import fr.vergne.livingwallpaper.bot.BotEmotion;
import fr.vergne.livingwallpaper.bot.action.ActionFactory;
import fr.vergne.livingwallpaper.environment.Environment;

// TODO Move random targets to emotion (but with no delay) as "bored"
// TODO Remove touch targets but add "interrupted" emotion (touch droid = droid stop during 4s + display "?")
// TODO use listeners? services?
public class LivingWallpaper extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new LiveWallpaperEngine();
	}

	private class LiveWallpaperEngine extends Engine {

		private int updateFrequencyInMs = 10;
		private boolean visible = true;
		private final Bot bot = new Bot();
		private final Environment botEnvironment = bot.getEnvironment();
		private final ActionFactory actionFactory = new ActionFactory();
		private final BotEmotion botEmotion = new BotEmotion();
		private final Handler handler = new Handler();
		private final String PREF_SPEED = getResources().getString(
				R.string.pref_speed_key);
		private final String PREF_ZOOM = getResources().getString(
				R.string.pref_zoom_key);
		private final Runnable botRunner = new Runnable() {
			@Override
			public void run() {
				bot.executeAction();
				if (visible) {
					draw();
				} else {
					// save resources
				}
				long remainingTime = updateFrequencyInMs
						- bot.getLastExecutionDelayInMs();
				handler.postDelayed(botRunner, Math.max(0, remainingTime));
			}
		};

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
					} else if (key.equals(PREF_ZOOM)) {
						updateZoom();
						updateSpeed(); // depends on zoom
					} else {
						throw new IllegalArgumentException(key
								+ " is not managed.");
					}
				}

			});
		}

		private void updateZoom() {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(LivingWallpaper.this);
			float zoom = Float.parseFloat(prefs.getString(PREF_ZOOM,
					getResources().getString(R.string.pref_zoom_default)));

			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = holder.lockCanvas();
			canvas.setDensity(Math.round(zoom * getDroidPicture().getDensity()));
			holder.unlockCanvasAndPost(canvas);
		}

		private void updateSpeed() {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(LivingWallpaper.this);
			int speed = Integer.parseInt(prefs.getString(PREF_SPEED,
					getResources().getString(R.string.pref_speed_default)));

			bot.setPixelsPerSecond(Math.round(getCanvasZoom() * speed
					* getDroidPicture().getWidth()));
		}

		private float getCanvasZoom() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			float zoom = 1;
			try {
				canvas = holder.lockCanvas(new Rect(0, 0, 0, 0));
				if (canvas != null) {
					int droidDensity = getDroidPicture().getDensity();
					int canvasDensity = canvas.getDensity();
					canvasDensity = canvasDensity == 0 ? droidDensity
							: canvasDensity;
					zoom = (float) canvasDensity / droidDensity;
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
			return zoom;
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
			super.onSurfaceChanged(holder, format, width, height);

			bot.setX(width / 2);
			bot.setY(height / 2);
			botEnvironment.setMaxX(width);
			botEnvironment.setMaxY(height);
			updateZoom();
			updateSpeed();
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
			bot.addAction(actionFactory.createWalkingAction(event.getX(),
					event.getY()));
			botEmotion.interrupt();
			super.onTouchEvent(event);
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				float zoom = getCanvasZoom();
				canvas = holder.lockCanvas();
				if (canvas != null) {
					canvas.drawColor(Color.WHITE);
					float left = bot.getX()
							- (zoom * getDroidPicture().getWidth() / 2);
					float top = bot.getY()
							- (zoom * getDroidPicture().getHeight() / 2);
					canvas.drawBitmap(getDroidPicture(), left, top, null);
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

		Map<Integer, Bitmap> bitmaps = new WeakHashMap<Integer, Bitmap>();

		private Bitmap getDroidPicture() {
			int id;
			if (botEmotion.isQuestioning()) {
				id = R.drawable.question_droid;
			} else {
				id = R.drawable.droid;
			}
			if (bitmaps.containsKey(id)) {
				// use the one already loaded
			} else {
				bitmaps.put(id,
						BitmapFactory.decodeResource(getResources(), id));
			}
			return bitmaps.get(id);
		}
	}

}
