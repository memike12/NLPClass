package usna.sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Complete the functions below. Output your segmentations one per line.
 *
 * java Segment max <dictionarypath> <tagspath>
 *   - This calls the maxMatch function.
 * 
 * java Segment better <dictionarypath> <tagspath>
 *   - This calls the improvedMaxMatch function.
 * 
 */
public class Segment {
	Set<String> dictionary;
	Map<String, Long> map = new HashMap<String, Long>();
	public Segment(String dictionaryPath) {
		loadDictionary(dictionaryPath);
	}

	/**
	 * Reads the dictionary from file and puts the words in a global Set.
	 * @param path Path to a file of dictionary words, one word and 
	 *             its frequency count per line.
	 */
	private void loadDictionary(String path) {
		try {
			dictionary = new HashSet<String>();
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line, subLine;
			long num;
			while( (line = reader.readLine()) != null ) {
				//subLine = line.substring(0, line.indexOf('\t'));
				dictionary.add(line);
				//line = line.substring(line.indexOf('\t')).trim();
				//num = Long.valueOf(line);
				//System.out.println(subLine + " " + num);
				//map.put(subLine, num);
				
			}
			reader.close();
		} catch( Exception ex ) { ex.printStackTrace(); }
	}

	/**
	 * YOU COMPLETE THIS FUNCTION: implement MaxMatch.
	 * @param inputFile Path to a file of hashtags, one tag per line.
	 */
	public void maxMatch(String inputFile) {
		try{
			BufferedReader reader = new BufferedReader(new FileReader(new File(inputFile)));
			String line;
			//set to one because if there is no word found then it prints out the length -1 and strips
			// off that many characters from the line
			int lengthOfLongestWordFound = 1;
			//reads the file line by line
			while( (line = reader.readLine()) != null ) {
				//Parses off the hashtag
				line = line.substring(1);
				int length = line.length();
				while( length >= 1){
					lengthOfLongestWordFound = 1;
					for(int x = 1; x <= length; x++){
						String subLine = line.substring(0, x);
						if(dictionary.contains(subLine.toLowerCase())){
							lengthOfLongestWordFound = subLine.length();						
							//System.out.print("Found " + subLine + " and length is " + lengthOfLongestWordFound + " result is: " );
						}
					}
					//System.out.print(line.substring(0, lengthOfLongestWordFound  ) + " ");
					line = line.substring(lengthOfLongestWordFound);
					length = line.length();
					//System.out.print( " " + length + " ");
				}
				//System.out.println(line);
			}
			reader.close();
		} catch( Exception ex ) { ex.printStackTrace(); }
	}

	/**
	 * YOU COMPLETE THIS FUNCTION: implement your improved MaxMatch.
	 * @param inputFile Path to a file of hashtags, one tag per line.
	 */
	public List<String> improvedMaxMatch(String line) {
		//BufferedReader reader = new BufferedReader(new FileReader(new File(inputFile)));
		line.replaceAll("[\\p{Punct}\\s]", "");
		long greatestLength = 0;
		int bestIndex = 0;
		int backBestIndex= 0;
		boolean changed = false;
		List<String> sentence = new ArrayList<String>();
		//set to one because if there is no word found then it prints out the length -1 and strips
		// off that many characters from the line
		int lengthOfLongestWordFound = 1;
		
		//reads the file line by line
		//while( (line = reader.readLine()) != null ) {
			
		//Parses off the hashtag
		if(line.startsWith("#") | line.startsWith("@")){
			line = line.substring(1);
		}
		int length = line.length();
		int parse = 0;
		String subLine = line;
			
		while( length >= 1){
			//if there are numbers, handle them
			changed = false;
			if(line.matches("[0-9].*")){
				subLine = line;
				for(int x = 0; x <= length; x++){
					if(subLine.matches("[0-9].*")&& !subLine.isEmpty()){
						parse = x;
						subLine = subLine.substring(1);
					}
				}
				//System.out.println(line.substring(0, parse + 1) + " ");
				line = line.substring(parse +1);
				length = line.length();
			}
			lengthOfLongestWordFound = 1;
			for(int x = 1; x <= length; x++){
				subLine = line.substring(0, x);
				if(x < length -1 && !Character.isUpperCase(line.charAt(x)) && Character.isUpperCase(line.charAt(x+1))){
					lengthOfLongestWordFound = subLine.length() + 1;
					break;
				}
				//System.out.println("testing " + subLine + "... Result is " + map.containsKey(subLine.toLowerCase().trim()));
				if(subLine.length() > lengthOfLongestWordFound){
					lengthOfLongestWordFound = subLine.length();
				}
			    //if(map.containsKey(subLine.toLowerCase())){
					//lengthOfLongestWordFound = subLine.length();
					//System.out.println("identified " + subLine);
					//System.out.println("the num of " + subLine + " is " + map.get(subLine.toLowerCase()) +" vs greatest num which is" + greatestNum);
					//if(map.get(subLine.toLowerCase()) < greatestNum){
						//System.out.println( subLine + " is greater than greatestNum");
						//greatestNum = map.get(subLine.toLowerCase());
						//bestIndex = x;
						//changed = true;
					//}
				//}
			}
			if(changed){
				//System.out.print(line.substring(0, bestIndex  ) + " ");
				line = line.substring(bestIndex);
				//System.out.print("$$$$$$$$$$$$$$$$" + line + " ");
				length = line.length();
			}else
			 
			if(!subLine.isEmpty()){
			//System.out.println(line.substring(0, lengthOfLongestWordFound  ) + " ");
			sentence.add(line.substring(0, lengthOfLongestWordFound));
			line = line.substring(lengthOfLongestWordFound);
			length = line.length();
			}
		}
		//System.out.println("Sentence is: " +sentence.toString());
		return sentence;
	}

	public static void main(String[] args) {
		if (args.length == 3) {
			Segment seg = new Segment(args[1]);
			if( args[0].equals("max") )
				seg.maxMatch(args[2]);
			else if( args[0].equals("better") )
				seg.improvedMaxMatch(args[2]);
		} else {
			System.err.println("java usna.segment.Segment max|better <dictionary> <path>");
		}
	}
}
