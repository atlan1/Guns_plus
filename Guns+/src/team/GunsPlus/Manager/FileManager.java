package team.GunsPlus.Manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import team.GunsPlus.Util.Util;


public class FileManager {

	
	public static boolean copy(InputStream in, File file){
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean create(String path){
		File f = new File(path);
		return create(f);
	}
	
	public static boolean create(File f){
		if(!f.exists()){
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				Util.debug(e);
				return false;
			}
		}else{
			return false;
		}
		return true;
	}
	
	public static boolean delete(File f){
		return f.delete();
	}
}
