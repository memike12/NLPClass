package usna.sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import usna.sentiment.LabeledTweet.SENTIMENT;


/**
 * YOU PROBABLY DON'T NEED TO CHANGE OR ADD CODE TO THIS CLASS.
 * 
 * Reads the tweets and the Opinion Lexicon from disk for you.
 * Use the lexicon to label tweets with sentiment, and use the tweets to learn a new lexicon.
 * 
 * @author Nate Chambers, US Naval Academy
 */
public class Datasets {
  private String rootDirectory = "/courses/nchamber/nlp/lab4/data";
  BufferedReader rawReader = null;
  String currentRawFile = null;
	  
  /**
   * Creates the object, and requires the path to the base data directory "lab4/data/"
   * @param rootPath
   */
  public Datasets(String rootPath) {
    rootDirectory = rootPath;
  }

  /**
   * This function gives you a single tweet. It will read the next tweet in the currently
   * opened file. If the file reaches the end, it opens the next unread file and gives you
   * its first tweet. This function is amazing. Almost no memory is used.
   * @return A String which is the text of one tweet.
   */
  public String getNextRawTweet() {
	  if( rawReader != null ) {
		  try {
			  String line = rawReader.readLine();
			  if( line != null )
				  return line;
		  } catch( IOException ex ) { ex.printStackTrace(); }
	  }
	  if( advanceRawFile() )
		  return getNextRawTweet();
	  else return null;
  }
  
  private boolean advanceRawFile() {
	  String dir = rootDirectory + "/tweets";
	  
	  // Make sure it is a valid directory.
	  File filedir = new File(dir);
	  if( !filedir.isDirectory() ) {
		  System.err.println("ERROR: path is not a directory: " + dir);
		  System.exit(1);
	  }

	  // Read the author files from this directory.
	  String[] files = getZipFilesSorted(dir);
	  String nextFile = null;
	  if( currentRawFile == null )
		  nextFile = files[0];
	  else {
		  for( int ii = 0; ii < files.length; ii++ ) {
			  if( files[ii].equalsIgnoreCase(currentRawFile) && ii+1 < files.length )
				  nextFile = files[ii+1];
		  }
	  }

	  // No more files to read.
	  if( nextFile == null ) return false;
	  currentRawFile = nextFile;

	  // Open the file to be read later.	  
	  System.out.println("opening file " + currentRawFile);
	  try {
		  if( rawReader != null ) rawReader.close();
		  InputStream in = new GZIPInputStream(new FileInputStream(new File(dir + "/" + currentRawFile)));
		  rawReader = new BufferedReader(new InputStreamReader(in));
	  } catch( IOException ex ) { 
		  System.err.println("Error opening file: " + currentRawFile);
		  ex.printStackTrace();
	  }
	  return true;
  }
  
  /**
   * Reads tweets from the given directory.
   * @param numTweets The number of tweets to read in and return.
   * @return A list of Strings, each string is a tweet.
  public List<String> getRawTweets(int numTweets) {
    List<String> tweets = new ArrayList<String>();
    String dir = rootDirectory + "/tweets";
    
    // Make sure it is a valid directory.
    File filedir = new File(dir);
    if( !filedir.isDirectory() ) {
      System.err.println("ERROR: path is not a directory: " + dir);
      System.exit(1);
    }

    // Read the author files from this directory.
    String[] files = getZipFilesSorted(dir);
    for( String file : files ) {
    	System.out.println("opening file " + file);
    	try {
    		InputStream in = new GZIPInputStream(new FileInputStream(new File(dir + "/" + file)));
    		rawReader = new BufferedReader(new InputStreamReader(in));

    		String line = rawReader.readLine();
    		while( line != null && tweets.size() < numTweets ) {
    		  int tabindex = line.indexOf('\t');
    		  if( tabindex > 0 ) {
    		    tweets.add(line.substring(0, tabindex));
    		  }
    		  line = rawReader.readLine();
    		}
    		in.close();
    	} catch( IOException ex ) { 
    		System.err.println("Error opening file: " + file);
    		ex.printStackTrace();
    	}
    }

    if( tweets.size() == 0 )
      System.out.println("WARNING: no tweets were read from " + dir);
    else
      System.out.println("Read " + tweets.size() + " tweets.");

	return tweets;
  }
   */

  
  /**
   * Read the labeled tweets from disk, put them in a list of tweet objects.
   * @return A list of tweet objects with sentiment labels.
   */
  public List<LabeledTweet> getLabeledTweets() {
    List<LabeledTweet> tweets = new ArrayList<LabeledTweet>();
    String path = rootDirectory + File.separator + "combined-labeled-tweets.txt";
    
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
      String line = in.readLine();
      while( line != null ) {
        String parts[] = line.split("\t");
        tweets.add(new LabeledTweet(SENTIMENT.valueOf(parts[0].toUpperCase()), parts[1]));
        line = in.readLine();
      }
      in.close();
    } catch( IOException ex ) { 
      System.err.println("Error opening file: " + path);
      ex.printStackTrace();
    }
    
    return tweets;
  }
  
  /**
   * Grab all the positive words from the Opinion Lexicon.
   * @return A list of words.
   */
  public List<String> getLexiconPositiveWords() {
	  return getWordsFromFile(rootDirectory + File.separator + "lexicon/positive-words.txt");
  }

  /**
   * Grab all the negative words from the Opinion Lexicon.
   * @return A list of words.
   */
  public List<String> getLexiconNegativeWords() {
	  return getWordsFromFile(rootDirectory + File.separator + "lexicon/negative-words.txt");
  }

  
  /**
   * Opens the given file path and assumes one word per line. Returns those words as a List.
   * @param path The path to the word file.
   * @return A list of strings, the words in the file.
   */
  public List<String> getWordsFromFile(String path) {
	  List<String> words = new ArrayList<String>();

	  try {
		  BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
		  String line = in.readLine();
		  while( line != null ) {
			  if( line.indexOf('\t') > -1 )
				  words.add(line.substring(0, line.indexOf('\t')));
			  else
				  words.add(line);
			  line = in.readLine();
		  }
		  in.close();
	  } catch( IOException ex ) { 
		  System.err.println("Error opening file: " + path);
		  ex.printStackTrace();
		  return null;
	  }
	  return words;	
  }
  
  /**
   * Read a directory and return all files in sorted order.
   */
  private static String[] getZipFilesSorted(String dirPath) {
    List<String> unsorted = getZipFiles(new File(dirPath));

    if( unsorted == null )
      System.out.println("ERROR: Directory.getFilesSorted() path is not known: " + dirPath);

    String[] sorted = new String[unsorted.size()];
    sorted = unsorted.toArray(sorted);
    Arrays.sort(sorted);

    return sorted;
  }

  /**
   * Get all regular files in a directory.
   */
  private static List<String> getZipFiles(File dir) {
    if( dir.isDirectory() ) {
      List<String> files = new ArrayList<String>();
      for( String file : dir.list() ) {
        if( !file.startsWith(".") && file.endsWith(".gz") )
          files.add(file);
      }
      return files;
    }

    return null;
  }
    
  // Debugging only.
  public static void main(String[] args) {
    Datasets data = new Datasets(args[0]);
//    for( LabeledTweet tweet : data.getLabeledTweets() )
//      System.out.println(tweet);
    for( int xx = 0; xx < 4000000; xx++ ) {
    	String tweet = data.getNextRawTweet();
    	if( tweet == null ) {
    		System.out.println("Done at " + xx);
    		break;
    	}
    }
//    System.out.println("tweet: " + data.getRawTweet());
//    System.out.println("tweet: " + data.getRawTweet());
//    System.out.println("tweet: " + data.getRawTweet());
  }
}
