package es.yetaap.wine.catalog;

import android.content.Context;
import android.os.Vibrator;

 public class Vibrate 
 {
  public static Vibrator v;
  public static final int CORTO = 100;
  public static final int LARGO = 500;
    
  static public void vibrar(Context ctx, int t)
  {
    v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
	if(v != null)
		v.vibrate(t);
  }
}
