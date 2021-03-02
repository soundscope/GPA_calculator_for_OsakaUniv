package score_calculator_for_SIKcsv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {
	private File targetFile;
	File getFile(String path){
		File[] files = new File(path).listFiles();
		if(files == null) return null;
		Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
		for (int i = 0 ; i < files.length ; i++){
			if((files[i]).getName().indexOf("SIKS") != -1 ) 
				if((files[i]).getName().lastIndexOf(".csv") != -1)
					return targetFile = files[i];
		}
		return null;
	}
	FileInputStream fis;
	byte[] ret = new byte[100000];
	byte[] readFile(File f) throws IOException {
		fis = new FileInputStream(f);
		fis.read(ret);
		return ret;				
	}

	byte[] readFile() throws IOException {
		fis = new FileInputStream(targetFile);
		fis.read(ret);
		return ret;
	}

	boolean isUTF8orSJIS(byte[] src, String encoding)
	{
		try {
			byte[] tmp = new String(src, encoding).getBytes(encoding);
			return Arrays.equals(tmp, src);
		} catch(UnsupportedEncodingException e) {
			return false;
		}
	}
}
