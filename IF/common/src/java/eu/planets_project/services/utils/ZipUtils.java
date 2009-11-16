package eu.planets_project.services.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.EntryOutputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;
import eu.planets_project.services.datatypes.Checksum;



public class ZipUtils {
	
	private static Log log = LogFactory.getLog(ZipUtils.class);
	
	/**
	 * Creates a Zip64File zip file.
	 * @param srcFolder the folder containing the files to be written to the zip
	 * @param destFolder the folder to write the Zip file to
	 * @param zipName the name the zip file should have. If no zipName is passed, the name of the folder will be used.
	 * @param compress true if the file shoukld be compressed, false if not.
	 * @return a zip(64) File
	 */
	public static File createZip(File srcFolder, File destFolder, String zipName, boolean compress) {
		Zip64File zipFile = null;
		
		if(!srcFolder.isDirectory()) {
			log.error("[createZip] The File object you have passed is NOT a folder! Nothing has been done, sorry.");
			return null;
		}
		
		if(zipName==null) {
			zipName = srcFolder.getName();
			log.info("[createZip] No zipName specified, using folder name instead: " + zipName);
		}
		
		try {
			File newZip = new File(destFolder, zipName);
			zipFile = new Zip64File(newZip);
			List<String> listOfFiles = listAllFilesAndFolders(srcFolder, new ArrayList<String> ());
			if(listOfFiles.size()==0) {
				log.info("[createZip] Found no files to put in the zip. Created empty Zip file anyway...");
				zipFile.close();
				return new File(zipFile.getDiskFile().getFileName());
			}
			log.info("[createZip] Working on zipFile: " + zipFile.getDiskFile().getFileName());
			log.info("[createZip] Normalizing paths...");
			List<String> normalizedPaths = normalizePaths(srcFolder);
			
			for(int i=0;i<normalizedPaths.size();i++) {
				String currentZipEntryPath = normalizedPaths.get(i);
				FileEntry entry = new FileEntry(currentZipEntryPath);
				File currentFile = new File(listOfFiles.get(i));
				
				FileEntry currentEntry = writeEntry(zipFile, entry, currentFile, compress);
			}
			log.info("[createZip] All Files written to zip file: " + zipFile.getDiskFile().getFileName());
			zipFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new File(zipFile.getDiskFile().getFileName());
	}
	
	
	/**
	 * Creates a Zip64File containing all the files in srcFolder and write it to destFolder using the passed zipName.
	 * After creating the zip file, a checksum is calculated.  
	 * @param srcFolder the folder containing the files to be written to the zip
	 * @param destFolder the folder to write the Zip file to
	 * @param zipName the name the zip file should have. If no zipName is passed, the name of the folder will be used.
	 * @param compress TODO
	 * @return a ZipResult, containing the zip as a file and the created checksum (MD5)
	 */
	public static ZipResult createZipAndCheck(File srcFolder, File destFolder, String zipName, boolean compress) {
		if(zipName==null) {
			zipName = srcFolder.getName();
		}
		File newZipFile = createZip(srcFolder, destFolder, zipName, compress);
		log.info("[createZipAndCheck] Zip file created: " + zipName);
		ZipResult zipResult = new ZipResult();
		try {
			byte[] digest = Checksums.md5(newZipFile);
			zipResult.setZipFile(newZipFile);
			zipResult.setChecksum(new Checksum("MD5", Arrays.toString(digest)));
			log.info("[createZipAndCheck] Checksum (MD5) created: " + zipResult.getChecksum()); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return zipResult;
	}
	
	
	/**
	 * Unzips a zip file and writes the contained files to destFolder.
	 * 
	 * @param zipFile the zip file to unpack.
	 * @param destFolder the folder to write the extracted files to.
	 * @return a List with all extracted files.
	 */
	public static List<File> unzipTo(File zipFile, File destFolder) {
		List<File> extractedFiles = null;
		try {
			Zip64File zip64File = new Zip64File(zipFile);
			
			List<FileEntry> entries = zip64File.getListFileEntries();
			extractedFiles = new ArrayList<File>();
			for (FileEntry fileEntry : entries) {
				
				File currentFile = readEntry(zip64File, fileEntry, destFolder);
				extractedFiles.add(currentFile);
				
			}
			zip64File.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return extractedFiles;
	}
	
	private static void readFileEntry(Zip64File zip64File, FileEntry fileEntry, File destFolder) {
		FileOutputStream fileOut;
		File target = new File(destFolder, fileEntry.getName());
		File targetsParent = target.getParentFile();
		if(targetsParent!=null) {
			targetsParent.mkdirs();
		}
		try {
//			boolean madeFolder = target.createNewFile();
			fileOut = new FileOutputStream(target);
			log.info("[readFileEntry] writing entry: " + fileEntry.getName() + " to file: " + target.getAbsolutePath());
			EntryInputStream entryReader = zip64File.openEntryInputStream(fileEntry.getName());
			FileUtils.writeInputStreamToOutputStream(entryReader, fileOut);
			entryReader.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (ZipException e) {
			log.warn("ATTENTION PLEASE: Some strange, but obviously not serious ZipException occured! Extracted file '" + target.getName() + "' anyway! So don't Panic!" + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void readFolderEntry(Zip64File zip64File, FileEntry folderEntry, File dest) {
		log.info("[readFolderEntry] The target you have specified is a folder: " + folderEntry.getName());
		File currentFolder = new File(dest, folderEntry.getName());
		boolean dirsCreated = currentFolder.mkdirs();
		List<String> containedFiles = getFileEntryChildren(zip64File, folderEntry);
		
		if(containedFiles.size()>0) {
			for (String currentFilePath : containedFiles) {
				FileEntry currentEntry = zip64File.getFileEntry(currentFilePath);
				File destination = new File(dest, currentEntry.getName());
				if(!currentEntry.isDirectory()) {
					readFileEntry(zip64File, currentEntry, dest);
				}
				else {
					dirsCreated = destination.mkdirs();
					log.info("[readFolderEntry] Created folder in file system: " + destination.getAbsolutePath());
//					readFolderEntry(zip64File, currentEntry, destination);
				}
			}
		}
	}

	
	/**
	 * Unzips a zip file and writes the content to destFolder, checking if the checksum is correct.
	 * 
	 * @param zipFile the file to unpack
	 * @param destFolder the folder to write the content of the zip to
	 * @param checksum the checksum to check
	 * @return a list containing all extracted files.
	 */
	public static List<File> checkAndUnzipTo(File zipFile, File destFolder, Checksum checksum) {
		try {
			byte[] fileDigest = Checksums.md5(zipFile);
			String fileDigestString = Arrays.toString(fileDigest);
			if(!fileDigestString.equals(checksum.getValue())) {
				log.warn("[checkAndUnzipTo] WARNING: The calculated checksum of the zip file is NOT equal to the passed checksum. File might be corrupted!");
				log.warn("[checkAndUnzipTo] Checksum Algorithm: " + checksum.getAlgorithm());
				log.warn("[checkAndUnzipTo] Passed checksum: " + checksum.getValue());
				log.warn("[checkAndUnzipTo] Calculated checksum: " + fileDigestString);
			}
			else {
				log.info("[checkAndUnzipTo] Success!! Checksum correct!");
			}
			return unzipTo(zipFile, destFolder); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * A convenience method. It creates an array of fragments from all entries in a given zip file.
	 * Fragments can be used to adress entries in a zip file.
	 * 
	 * @param zip the zip file to scan
	 * @return an String[] containing all file entries in this zip. 
	 * 
	 */
	public static String[] getAllFragments(File zip) {
		String[] fragments = null;
		try {
			Zip64File zip64File = new Zip64File(zip);
			List<FileEntry> entries = zip64File.getListFileEntries();
			if(entries.size() > 0) {
				int i = 0;
				fragments = new String[entries.size()];
				for (FileEntry fileEntry : entries) {
					fragments[i] = new String(fileEntry.getName());
					i++;
				}
			}
			else {
				return new String[]{};
			}
			zip64File.close();
		} catch (ZipException e) {
			log.error("No file entries found! This file is not a valid ZIP file!");
//			e.printStackTrace();
			return new String[] {};
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fragments;
	}
	
	
	/**
	 * Extracts a file specified by targetPathInZipfile from the passed zip and writes it to destFolder.
	 * If the targetPathInZipfile points to a folder, all files in the folder will be extracted as well
	 * and the parent folder, containing all these files, is returned.
	 * 
	 * @param zip the zip to extract targetPathInZipfile from
	 * @param targetPathInZipfile the file to extract
	 * @param destFolder the folder to write the extrcated file to
	 * @return the extracted File.
	 */
	public static File getFileFrom(File zip, String targetPathInZipfile, File destFolder) {
		File target = null;
		try {
			Zip64File zip64File = new Zip64File(zip);
			FileEntry targetEntry = getFileEntry(zip64File, targetPathInZipfile);
			
			if(targetEntry!=null) {
				target = readEntry(zip64File, targetEntry, destFolder);
			}
			else {
				log.error("[getFileFrom] Sorry, the file/folder you ask for could not be found in this zip: " + targetPathInZipfile);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return target;
	}
	
	
	private static List<String> checkFilesForDeletion(Zip64File zip64File, FileEntry parentFolderEntry, File folderToInsert) {
		List<String> entryChildren = getFileEntryChildren(zip64File, parentFolderEntry);
		List<String> filesToAdd = normalizePaths(folderToInsert);
		
		List<String> filesToDelete = new ArrayList<String>();
		
		for (String currentChild : entryChildren) {
			for (String currentToAdd : filesToAdd) {
				if(currentChild.endsWith(currentToAdd)) {
					filesToDelete.add(currentChild);
				}
			}
		}
		
		return filesToDelete;
	}
	
	
	/**
	 * Inserts a file into a given zip at a location specified by targetPath.
	 * If toInsert points to a folder, all files in this folder will be added to the zip.
	 * If the zip already contains a file specified by targetPath, the existing entry (and all files contained in it) 
	 * will be deleted and toInsert (and all contained files) is inserted.
	 * 
	 * @param zipFile the zip file where the file toInsert will be added.
	 * @param toInsert the file to add to the zip 
	 * @param targetPath the location the file should have in this zip
	 * @return the modified zip, containing the file toInsert.
	 */
	public static File insertFileInto(File zipFile, File toInsert, String targetPath) {
		Zip64File zip64File = null;
		try {
			boolean compress = false;
			
			zip64File = new Zip64File(zipFile);
			
			FileEntry testEntry = getFileEntry(zip64File, targetPath);
			
			if(testEntry!= null && testEntry.getMethod() == FileEntry.iMETHOD_DEFLATED) {
				compress = true;
			}
			
			processAndCreateFolderEntries(zip64File, parseTargetPath(targetPath, toInsert), compress);
			
			if(testEntry!=null) {
				log.info("[insertFileInto] Entry exists: " + testEntry.getName());
				log.info("[insertFileInto] Will delete this entry before inserting: " + toInsert.getName());
				if(!testEntry.isDirectory()) {
					zip64File.delete(testEntry.getName());
				}
				else {
					log.info("[insertFileInto] Entry is a directory. " +
							"Will delete all files contained in this entry and insert " + toInsert.getName() +  
							"and all nested files.");
					
					if(!targetPath.contains("/")) {
						targetPath = targetPath + "/";
					}
					deleteFileEntry(zip64File, testEntry);
					log.info("[insertFileInto] Entry successfully deleted.");
				}
				
				log.info("[insertFileInto] Writing new Entry: " + targetPath);
				EntryOutputStream out = null;
				if(!compress) {
					out = zip64File.openEntryOutputStream(targetPath, FileEntry.iMETHOD_STORED, new Date(toInsert.lastModified()));
				}
				else {
					out = zip64File.openEntryOutputStream(targetPath, FileEntry.iMETHOD_DEFLATED, new Date(toInsert.lastModified()));
				}
				
				if(toInsert.isDirectory()) {
					out.flush();
					out.close();
					log.info("[insertFileInto] Finished writing entry: " + targetPath);
					
					List<String> containedPaths = normalizePaths(toInsert);
					List<File> containedFiles = FileUtils.listAllFilesAndFolders(toInsert, new ArrayList<File>());
					
					log.info("[insertFileInto] Added entry is a folder.");
					log.info("[insertFileInto] Adding all nested files: ");
					for(int i=0;i<containedPaths.size();i++) {
						File currentFile = containedFiles.get(i);
						String currentPath = targetPath.replace("/", "") + File.separator + containedPaths.get(i);
						EntryOutputStream loop_out = null;
						if(!compress) {
							loop_out = zip64File.openEntryOutputStream(currentPath, FileEntry.iMETHOD_STORED, new Date(currentFile.lastModified()));
						}
						else {
							loop_out = zip64File.openEntryOutputStream(currentPath, FileEntry.iMETHOD_DEFLATED, new Date(currentFile.lastModified()));
						}
						if(currentFile.isFile()) {
							InputStream loop_in = new FileInputStream(currentFile);
							FileUtils.writeInputStreamToOutputStream(loop_in, loop_out);
							loop_in.close();
						}
						log.info("[insertFileInto] Added: " + currentPath);
						loop_out.flush();
						loop_out.close();
					}
				}
				else {
					InputStream in = new FileInputStream(toInsert);
					FileUtils.writeInputStreamToOutputStream(in, out);
					in.close();
					out.flush();
					out.close();
				}
			}
			else {
				EntryOutputStream out = null;
				if(!compress) {
					out = zip64File.openEntryOutputStream(targetPath, FileEntry.iMETHOD_STORED, new Date(toInsert.lastModified()));
				}
				else {
					out = zip64File.openEntryOutputStream(targetPath, FileEntry.iMETHOD_DEFLATED, new Date(toInsert.lastModified()));
				}
				
				if(toInsert.isDirectory()) {
					out.flush();
					out.close();
					log.info("[insertFileInto] Finished writing entry: " + targetPath);
					
					List<String> containedPaths = normalizePaths(toInsert);
					List<File> containedFiles = FileUtils.listAllFilesAndFolders(toInsert, new ArrayList<File>());
					
					log.info("[insertFileInto] Added entry is a folder.");
					log.info("[insertFileInto] Adding all nested files: ");
					
					for(int i=0;i<containedPaths.size();i++) {
						File currentFile = containedFiles.get(i);
						String currentPath = targetPath.replace("/", "") + File.separator + containedPaths.get(i);
						EntryOutputStream loop_out = null;
						if(!compress) {
							loop_out = zip64File.openEntryOutputStream(currentPath, FileEntry.iMETHOD_STORED, new Date(currentFile.lastModified()));
						}
						else {
							loop_out = zip64File.openEntryOutputStream(currentPath, FileEntry.iMETHOD_DEFLATED, new Date(currentFile.lastModified()));
						}
						
						if(currentFile.isFile()) {
							InputStream loop_in = new FileInputStream(currentFile);
							FileUtils.writeInputStreamToOutputStream(loop_in, loop_out);
							loop_in.close();
						}
						log.info("[insertFileInto] Added: " + currentPath);
						loop_out.flush();
						loop_out.close();
					}
				}
				else {
					InputStream in = new FileInputStream(toInsert);
					FileUtils.writeInputStreamToOutputStream(in, out);
					in.close();
					out.flush();
					out.close();
				}
			}
			log.info("[insertFileInto] Done! Added " + toInsert.getName() + " to zip.");
			zip64File.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new File(zip64File.getDiskFile().getFileName());
	}
	
	private static List<String> parseTargetPath(String targetPath, File toInsert) {
		List<String> cleanedParts = new ArrayList<String>();
		
		if(targetPath.contains("\\")) {
			targetPath = targetPath.replace("\\", "#");
			String[] parts = targetPath.split("#");
			if(parts.length > 1) {
				String accumulatedPath = "";
				for (int i = 0; i < parts.length-1; i++) {
					String thisPath = accumulatedPath + "\\" + parts[i] + "/";
					cleanedParts.add(thisPath.substring(1));
					accumulatedPath = accumulatedPath + "\\" + parts[i];
				}
			}
			else {
				return null;
			}
			return cleanedParts;
		}
		else {
			return null;
		}
	}
	
	private static void processAndCreateFolderEntries(Zip64File zip64File, List<String> pathParts, boolean compress) {
		if(pathParts==null) {
			return;
		}
		for (String string : pathParts) {
			FileEntry currentEntry = zip64File.getFileEntry(string);
			if(currentEntry!=null) {
				continue;
			}
			else {
				currentEntry = zip64File.getFileEntry(string + "/");
				if(currentEntry!=null) {
					continue;
				}
				else {
					try {
						EntryOutputStream entryWriter = null;
						
						if(!compress) {
							entryWriter = zip64File.openEntryOutputStream(string, FileEntry.iMETHOD_STORED, null);
						}
						else {
							entryWriter = zip64File.openEntryOutputStream(string, FileEntry.iMETHOD_DEFLATED, null);
						}
						
						entryWriter.flush();
						entryWriter.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (ZipException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	

	
	/**
	 * Removes the file 'fileToRemove' from zipFile. 
	 * 'fileToRemove' points to the location of the file to delete inside the zip file.
	 * 
	 * @param zipFile the zip file to remove fileToRemove from.
	 * @param fileToRemove the path of the file to remove from zipFile.
	 * @return the modified zipFile.
	 */
	public static File removeFileFrom(File zipFile, String fileToRemove) {
		Zip64File zip64File = null;
		try {
			zip64File = new Zip64File(zipFile);
			
			FileEntry testEntry = getFileEntry(zip64File, fileToRemove);
			
			if(testEntry==null) {
				log.info("File not found: " + fileToRemove);
				log.info("Nothing has been deleted...");
			}
			else {
				if(testEntry.isDirectory()) {
					deleteFileEntry(zip64File, testEntry);
				}
				else {
					FileEntry deletedEntry = zip64File.delete(fileToRemove);
					log.info("Deleted entry from zip: " + deletedEntry.getName());
					zip64File.close();
				}
			}
			
			zip64File.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new File(zip64File.getDiskFile().getFileName());
	}
	
	public static boolean isZipFile(File file) {
		try {
			Zip64File zip = new Zip64File(file);
			List<FileEntry> entries = zip.getListFileEntries();
			if(entries==null) {
				return false;
			}
			else {
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ZipException e) {
			log.info("File '" + file.getName() + "' is NOT a ZIP.");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * Lists all entries in this zip file. Each entry is a file/folder in this zip.
	 * @param zip the zip file to scan
	 * @return a list with all entry paths
	 */
	public static List<String> listZipEntries(File zip) {
		List<String> entryList = new ArrayList<String>();
		try {
			Zip64File zip64File = new Zip64File(zip);
			List<FileEntry> entries = zip64File.getListFileEntries();
			
			if(entries.size() > 0) {
				for (FileEntry fileEntry : entries) {
					entryList.add(fileEntry.getName());
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entryList;
	}
	

	private static File readEntry(Zip64File zip64File, FileEntry toRead, File destFolder) {
		InputStream in = null;
		File target = new File(destFolder, toRead.getName());
		if(!toRead.isDirectory()) {
			readFileEntry(zip64File, toRead, destFolder);
		}
		else {
			readFolderEntry(zip64File, toRead, destFolder);
		}
		return target;
	}
	
	
	private static FileEntry writeEntry(Zip64File zip64File, FileEntry targetPath, File toWrite, boolean compress) {
		InputStream in = null;
		EntryOutputStream out = null;
		
		processAndCreateFolderEntries(zip64File, parseTargetPath(targetPath.getName(), toWrite), compress);
		
		try {
			
			if(!compress) {
				out = zip64File.openEntryOutputStream(targetPath.getName(), FileEntry.iMETHOD_STORED, getFileDate(toWrite));
			}
			else {
				out = zip64File.openEntryOutputStream(targetPath.getName(), FileEntry.iMETHOD_DEFLATED, getFileDate(toWrite));
			}
			
			
			if(!targetPath.isDirectory()) {
				in = new FileInputStream(toWrite);
				FileUtils.writeInputStreamToOutputStream(in, out);
				in.close();
			}
			out.flush();
			out.close();
			if(targetPath.isDirectory()) {
				log.info("[createZip] Written folder entry to zip: " + targetPath.getName());
			}
			else {
				log.info("[createZip] Written file entry to zip: " + targetPath.getName());
			}
				
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (ZipException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return targetPath;
	}

	
	private static Date getFileDate(File file) {
		Date date = new Date(file.lastModified());
		return date;
	}
	
	
	private static FileEntry testFileEntry(Zip64File zip64File, String targetPath) {
		FileEntry fileEntry = zip64File.getFileEntry(targetPath);
		return fileEntry;
	}
	
	private static FileEntry testFolderEntry(Zip64File zip64File, String targetPath) {
		FileEntry folderEntry = zip64File.getFileEntry(targetPath + "/");
		return folderEntry;
	}
	
	
	private static boolean containsEntry(Zip64File zip64File, String targetPath) {
		FileEntry result = testFileEntry(zip64File, targetPath);
		if(result!=null) {
			return true; 
		}
		else {
			result = testFolderEntry(zip64File, targetPath);
			if(result!=null) {
				return true;
			}
			else {
				return false;
			}
		}
	}


	/**
	 * Private method to delete a FileEntry including all nested entries "below" the FileEntry toDelete.
	 * @param zip the zip64File to delete the FileEntry toDelete from
	 * @param toDelete the FileEntry to delete
	 * @return a list with all deleted file entries.
	 */
	private static List<FileEntry> deleteFileEntry(Zip64File zip, FileEntry toDelete) {
		List<FileEntry> deletedEntries = deleteEntriesRecursively(zip, toDelete, new ArrayList<FileEntry>());
		return deletedEntries;
	}
	

	/**
	 * Helper method to delete a FileEntry (and all nested entries) recursively.
	 * If toDelete is a folder, all nested entries will be deleted.
	 * @param zip the zip to delete the entry from
	 * @param toDelete the entry to delete
	 * @param deletedEntries a list where all deleted entries will be stored.
	 * @return the list of deletedEntries
	 */
	private static List<FileEntry> deleteEntriesRecursively(Zip64File zip, FileEntry toDelete, List<FileEntry> deletedEntries) {
		try {
			if(toDelete.isDirectory()) {
				log.info("[deleteEntriesRecursively] The FileEntry to delete is a folder. Deleting all nested entries: ");
				List<String> containedFiles = getFileEntryChildren(zip, toDelete);
				
				if(containedFiles.size()>0) {
					for (String currentEntryPath : containedFiles) {
						FileEntry current = zip.getFileEntry(currentEntryPath);
						if(current!=null) {
							deleteEntriesRecursively(zip, current, deletedEntries);
						}
					}
					log.info("[deleteEntriesRecursively] Deleted entry: " + toDelete.getName());
					deletedEntries.add(zip.delete(toDelete.getName()));
				}
				else {
					log.info("[deleteEntriesRecursively] Deleted entry: " + toDelete.getName());
					deletedEntries.add(zip.delete(toDelete.getName()));
				}
			}
			else {
				FileEntry current = zip.delete(toDelete.getName());
				log.info("[deleteEntriesRecursively] Deleted entry: " + toDelete.getName());
				deletedEntries.add(current);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return deletedEntries;
	}
	
	
	/**
	 * Checks if a FileEntry named by entryPath is contained in the zip.
	 * 
	 * @param zip the zip file to search.
	 * @param entryPath the FileEntry to find
	 * @return the found FileEntry or null, if entryPath is not a valid location.
	 */
	private static FileEntry getFileEntry(Zip64File zip64File, String entryPath) {
		FileEntry testEntry = null;
		testEntry = zip64File.getFileEntry(entryPath);
		if(testEntry==null) {
			testEntry = zip64File.getFileEntry(entryPath + "/");
		}
		if(testEntry!=null) {
			log.info("[getFileEntry] Found entry: " + testEntry.getName());
		}
		else {
			log.error("[getFileEntry] Entry NOT found: " + entryPath);
		}
		return testEntry;
	}
	
	
	/**
	 * Returns all nested FileEntries for a given folder-FileEntry
	 * @param zip the zip file
	 * @param folderEntry the folder FileEntry to scan.
	 * @return all entries nested in folderEntry.
	 */
	private static List<String> getFileEntryChildren(Zip64File zip, FileEntry folderEntry) {
		List<String> list = listZipEntries(zip);
		List<String> resultList = new ArrayList<String>();
		String folderEntryName = folderEntry.getName();
		String testName = folderEntryName.substring(0, folderEntryName.length()-1);
		for (String currentPath : list) {
			if(currentPath.contains(testName)) {
				if(!currentPath.equalsIgnoreCase(folderEntry.getName())) {
					log.info("[getFileEntryChildren] Found child: " + currentPath);
					resultList.add(currentPath);
				}
			}
		}
		return resultList;
	}

	
	/**
	 * Convenience method to get the file name as String from a given FileEntry.
	 * This method returns the last part of the FileEntry.getName() String, starting with
	 * the last index of "\". 
	 * @param entry the FileEntry to get the file name for.
	 * @return the file name as String
	 */
	private static String getEntryFileName(FileEntry entry) {
		String entryPath = entry.getName();
		String name = null;
		if(entryPath.contains(File.separator)) {
			name = entryPath.substring(entryPath.lastIndexOf(File.separator)+1);
			return name;
		}
		else {
			return entryPath;
		}
	}

	
	/**
	 * Utility method to list all entries in a zip file as a List<String> 
	 * @param zip64File the zip64 file to list the entries 
	 * @return all entries in this zip64File
	 */
	private static List<String> listZipEntries(Zip64File zip64File) {
		List<String> entryList = new ArrayList<String>();
		
		List<FileEntry> entries = zip64File.getListFileEntries();
		
		if(entries.size() > 0) {
			for (FileEntry fileEntry : entries) {
				entryList.add(fileEntry.getName());
			}
		}
		return entryList;
	}

	/**
     * @param dir The dir to list
     * @param list The list to add the contents of dir to
     * @return The given list, with the contents of dir added
     */
    private static ArrayList<String> listAllFilesAndFolders(final File dir,
            final ArrayList<String> list) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File currentFile = files[i];
                boolean currentFileIsDir = currentFile.isDirectory();
                if (currentFileIsDir) {
                    // Ignore hidden folders
                    if (currentFile.isHidden()) {
                        continue;
                    }
                    if (currentFile.getName().equalsIgnoreCase("CVS")) {
                        continue;
                    }
                    if (currentFile.getName().equalsIgnoreCase(".svn")) {
                        continue;
                    }
                    list.add(currentFile.getPath() + "/");
                    /*
                     * the closing "/" has to be there to tell the
                     * ZipOutputStream that this is a folder...
                     */
                    listAllFilesAndFolders(currentFile, list);
                } else {
                    list.add(currentFile.getPath());
                }
            }
        }
        return list;
    }
    
    
    /**
     * Utility method to strip the absoulte part of a files path, starting with the index of folder.getName().
     * 
     * @param folder the folder
     * @return
     */
    private static List<String> normalizePaths(File folder) {
		if(!folder.isDirectory()) {
			return null;
		}
		
		List<String> resultFileList = listAllFilesAndFolders(folder, new ArrayList<String>());
		
		if (resultFileList.size() == 0) {
	        return null;
	    } 
		ArrayList<String> normalizedPaths = new ArrayList<String>();
	    for (int i = 0; i < resultFileList.size(); i++) {
	        String currentPath = resultFileList.get(i);
	        // Strip the beginning of the String, except the "[FOLDER-NAME]
	        // itself\"....
	        int index = currentPath.indexOf(folder.getName());
	        currentPath = currentPath.substring(index);
	        // Delete the [FOLDER-NAME] part of the paths
	        currentPath = currentPath.replace(folder.getName()
	                + File.separator, "");
	        // add the normalized path to the list
	        normalizedPaths.add(currentPath);
	    }
	    return normalizedPaths;
	}

}
