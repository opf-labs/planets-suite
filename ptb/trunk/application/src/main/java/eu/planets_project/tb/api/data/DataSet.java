package eu.planets_project.tb.api.data;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author alindley
 * The interface description for a logical representation of data
 * within the Testbed. 1..n input files from different locations 
 * (local file, URL, etc.) may be added into a DataSet container 
 * object. All items included in a DataSet object get added by the 
 * same semantic representation within the data registry. 
 * 
 * Note: All addDataSource methods get resolved as File objects at the time
 * they are added to the DataSet object.
 *
 */
public interface DataSet{
	
	/**
	 * When no FileName is choosen, the original FileName is taken.
	 * All addDataSource methods get resolved as File object
	 * @param sInputFile gets resolved as local File Object
	 * @return -1 when error; unique ID for the added data source (unique in this DataSet object)
	 */
	public int addDataSource(String sInputFile);
	/**
	 * @param sInputFile gets resolved as local File object
	 * @param sFileName
	 * @return -1 when error; unique ID for the added data source(unique in this DataSet object)
	 */
	public int addDataSource(String sInputFile, String sFileName);
	
	public void addDataSource(File fFile);
	public void addDataSource(File fFile, String sFileName);
	
	/**
	 * The source of the URL gets fetched at the time when this method is 
	 * executed.
	 * @param url
	 */
	public void addDataSource(URL url);
	public void addDataSource(URL url, String sFileName);
	
	public void addDirectory(String sPath);
	
	public List<String> getAddedDataSourceNames();
	/**
	 * @return HashMap<Integer id, String sName>
	 */
	public Map<Integer,String> getAddedDataSourceNamesWithIDs();
	
	/**
	 * @return HashMap<Integer id, String sName>
	 */
	public Map<Integer,String> getAddedDataSourceIDswithNames();
	
	/**
	 * @return HashMap<Integer id, File fFile>
	 */
	public Map<Integer,File> getAddedDataSources();
	
	/**
	 * The sourceID is unique within a DataSet object
	 * @param iSourceID
	 */
	public void removeDataSource(int iSourceID);
	

	public void persistDataSet(DataRegistryBinding registry, String sNode);
	
	//KLasse noch ausbauen!

}
