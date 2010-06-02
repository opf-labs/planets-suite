package eu.planets_project.ifr.core.wee.impl.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUtil {
	
	private FileUtil(){}
	/**
	  * Recursively returns a List of all Files found under a root dir; 
	  * the List is sorted using File.compareTo().
	  *
	  * @param rootDir
	  */
	  static public List<File> getFileListing(File rootDir) throws FileNotFoundException {
		    validateDirectory(rootDir);
		    List<File> result = getFileListingNoSort(rootDir);
		    Collections.sort(result);
		    return result;
	  }

	  static private List<File> getFileListingNoSort(File rootDir) throws FileNotFoundException {
		    List<File> result = new ArrayList<File>();
		    File[] filesAndDirs = rootDir.listFiles();
		    List<File> filesDirs = Arrays.asList(filesAndDirs);
		    for(File file : filesDirs) {
		      result.add(file); //always add, even if directory
		      if ( ! file.isFile() ) {
		        //must be a directory
		        //recursive call!
		        List<File> deeperList = getFileListingNoSort(file);
		        result.addAll(deeperList);
		      }
		    }
		    return result;
	  }

	  /**
	  * Directory is valid if it exists, does not represent a file, and can be read.
	  */
	  static private void validateDirectory(File dir) throws FileNotFoundException {
		  if (dir == null) 
			  throw new IllegalArgumentException("Directory should not be null.");
		  if (!dir.exists()) 
			  throw new FileNotFoundException("Directory does not exist: " + dir);
		  if (!dir.isDirectory()) 
			  throw new IllegalArgumentException("Is not a directory: " + dir);
		  if (!dir.canRead()) {
			  throw new IllegalArgumentException("Directory cannot be read: " + dir);
		  }
	  }
	  
	/**
	 * Writes a byte[] into File and creates all required directories.
	 * @param data
	 * @param dir
	 * @param fileName
	 * @throws IOException
	 */
	  public static void writeFile(byte[] data, String dir, String fileName) throws IOException{
		  
		  String dirPackages = new File(fileName).getParent();
		  new File(dir+"/"+dirPackages).mkdirs();
		  OutputStream out = new FileOutputStream(dir+"/"+fileName);
		  try {
			  out.write(data);
		  } finally {
			  out.close();
		  }
	  }
	  
	 /**
	  * Used to retrieve a file - fetched from the data registry and transferred into the wee's local /temp directory
	  * Information in the temp directory is used to load templates.class after javac with classloader.
	  * @param QName
	  * @param ext accepted "java" or "class"
	  * @throws IOException when requested file is found in the /wee/temp dir
	  */
	  public static File getTempFile(String QName, String ext) throws IOException{
		  if((ext!=null)&&(QName!=null)&&((ext.equalsIgnoreCase("java"))||(ext.equalsIgnoreCase("class")))){
			  int p = QName.lastIndexOf(".");
			  String name = QName.substring(p+1,QName.length())+"."+ext;
			  String javaPath = RegistryUtils.getWeeDirBase()+RegistryUtils.getWeeTmpDir()+"/"+QName.substring(0,p).replace(".","/");
			  File f = new File(javaPath+"/"+name);
			  if((f.exists())&&(f.canRead())){
				  return f;
			  }
			  throw new IOException("FileUtil.getTempFile file source not accessible");
		  }
		  else
			  throw new IOException("FileUtil.getTempFile accepted .java and .class");
	  }


}
