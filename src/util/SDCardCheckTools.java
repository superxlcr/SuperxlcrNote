package util;

import android.os.Environment;
/**
 * 
 * @author XuZhiwei (xuzw13@gmail.com)
 * Create at 2012-8-17 ����10:14:40
 * ����Ƿ����SD���Ĺ�����
 */
public class SDCardCheckTools {
	/**
	 * ����Ƿ����SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
}
