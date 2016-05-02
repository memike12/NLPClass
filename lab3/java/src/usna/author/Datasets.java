package usna.author;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * YOU PROBABLY DON'T NEED TO CHANGE OR ADD CODE TO THIS CLASS.
 * 
 * Reads the training and test sets from disk for you. Returns Passage objects.
 * Use these Passages to train your Naive Bayes model, as well as to evaluate it.
 * 
 * @author Nate Chambers, US Naval Academy
 */
public class Datasets {
  private String rootDirectory = "/courses/nchamber/nlp/lab3/data";
	  
  /**
   * Creates the object, and requires the path to the base data directory "lab3/data/"
   * @param rootPath
   */
  public Datasets(String rootPath) {
    rootDirectory = rootPath;
  }
  
  public List<Passage> getTrainingSet() {
    return readDatums(rootDirectory + File.separator + "train");
  }

  public List<Passage> getDevelopmentSet() {
    return readDatums(rootDirectory + File.separator + "dev");
  }

  public List<Passage> getTestSet() {
    return readDatums(rootDirectory + File.separator + "test");
  }
    
  /**
   * Expects a directory that contains only files like train-<authorname> or dev-<authorname>.
   * Reads the text and returns Passage objects for each author.
   * @param dir A directory of files.
   * @return A List of Passage objects, labeled with the authors of each passage.
   */
  private List<Passage> readDatums(String dir) {
    List<Passage> passages = new ArrayList<Passage>();
    	
    // Make sure it is a valid directory.
    File filedir = new File(dir);
    if( !filedir.isDirectory() ) {
      System.err.println("ERROR: path is not a directory: " + dir);
      System.exit(1);
    }
    	
    // Read the author files from this directory.
    List<String> files = getFiles(filedir);
    for( String file : files ) {
      System.out.println("opening file " + file);
      String author = file.substring(file.lastIndexOf('-')+1).toUpperCase();
      Passage.AUTHOR theauthor = Passage.AUTHOR.valueOf(author); 
      try {
        BufferedReader in = new BufferedReader(new FileReader(dir + File.separator + file));
        passages.addAll(readDatums(in, theauthor));
        in.close();
      } catch( IOException ex ) { 
        System.err.println("Error opening file: " + file);
        ex.printStackTrace();
      }
    }
    	
    if( passages.size() == 0 )
      System.out.println("WARNING: no passages were read from " + dir);
    	
    return passages;
  }

  /**
   * Takes an open file stream and reads text passages from it, returning Passage objects, one
   * for each text segment. The objects are labeled with the given author.
   * @param dir An open file stream.
   * @return A List of Passage objects, labeled with the given author.
   */
  private List<Passage> readDatums(BufferedReader in, Passage.AUTHOR author) throws IOException {
    List<Passage> passages = new ArrayList<Passage>();
    StringBuffer passageText = new StringBuffer();
    String line;
    	
    while( (line = in.readLine()) != null) {
      // New passage starting.
      if( line.matches("--\\*\\*--\\*\\*--") ) {
        if( passageText.length() > 10 ) {
          Passage passage = new Passage(author, passageText.toString().trim());
          passages.add(passage);
        }
        passageText = new StringBuffer();
      }
      // Normal text line.
      else passageText.append(line);
    }

    return passages;
  }
    
  /**
   * Get all regular files in a directory.
   */
  private static List<String> getFiles(File dir) {
    if( dir.isDirectory() ) {
      List<String> files = new ArrayList<String>();
      for( String file : dir.list() ) {
        if( !file.startsWith(".") )
          files.add(file);
      }
      return files;
    }

    return null;
  }
    
  // Debugging only.
  public static void main(String[] args) {
    Datasets data = new Datasets(args[0]);
    for( Passage passage : data.getTestSet() ) {
      System.out.println(passage);
    }
  }
}
