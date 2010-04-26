import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;


public class ExtractUserNamesFromLog {
	
	private static DataInputStream in;
	private static BufferedReader br;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//files are expected to start with 'server.log'
		String fileNameToken = "server.log";
		String searchToken = "[eu.planets_project.tb.gui.UserBean] Looking up user details for";
		
		//logRoot dir containing the testbed log files
		String sLogRoot = args[0];
		File logRoot = new File (sLogRoot);
		if(logRoot.canRead()==false || logRoot.isDirectory()==false){
			throw new Exception ("need to specify the data log-root dir");
		}
		String[] sLogFiles = logRoot.list();
		System.out.println("listed "+sLogFiles.length +" file-names under root");
		
		//iterate over all files that match the fileNameToken
		//users with their times associated
		HashMap<String,List<String>> ret = new HashMap<String,List<String>>();
		for(String fileName : sLogFiles){
			if(fileName.startsWith(fileNameToken)){
				System.out.println("parsing information from: "+fileName);
				//String fileContent = getFileContentAsString(logRoot.getAbsolutePath()+"/"+fileName);
				
				br = initLogReader(logRoot.getAbsolutePath()+"/"+fileName);
				String strLine="";
				while(strLine!=null){
					//parse another line
					strLine = br.readLine();
					
					if(strLine!=null){
						int iFound = strLine.indexOf(searchToken, 0);
						//check if search-token was found on this line
						if(iFound!=-1){
							String user = strLine.substring(iFound+searchToken.length()+1,strLine.length()).trim();
							String date = strLine.substring(0,10);
							if(user.startsWith("null")){
								//exclude this item
							}else{
								if(!ret.containsKey(user)){
									ret.put(user, new ArrayList<String>());
								}
								if(!ret.get(user).contains(date)){
									ret.get(user).add(date);
								}
							}
						}
					}
				}
				//close inputstream on log file
				in.close();	
			}
		}
		
		//now iterate over all users and write them to disk with their dates
		File fOut = new File(logRoot.getCanonicalPath()+"/user_stats.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(fOut));
		for(String usr : ret.keySet()){
			System.out.println("writing: "+usr+";"+ret.get(usr).size()+";"+ret.get(usr));
			writer.write(usr+";"+ret.get(usr).size()+";"+ret.get(usr)+"\n");
		}
		writer.close();
	}

	/*public static String getFileContentAsString(String fileRef) throws IOException{
		StringBuilder fileData = new StringBuilder(1024);
		File xmlf = new File(fileRef);
		BufferedReader reader = new BufferedReader(new FileReader(xmlf));
	    char[] buf = new char[1024];
	    int numRead = 0;
	    while((numRead=reader.read(buf)) != -1) {
	        fileData.append(buf, 0, numRead);
	    }
	    reader.close();
	    //System.out.println(fileData.toString());
	    return fileData.toString();
	}*/
	
	private static BufferedReader initLogReader(String fileRef) throws IOException{
  	  FileInputStream fstream = new FileInputStream(fileRef);
	  // Get the object of DataInputStream
	  in = new DataInputStream(fstream);
	  br = new BufferedReader(new InputStreamReader(in));
	  return br;
	}
	

}
