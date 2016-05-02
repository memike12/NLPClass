package usna.sentiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import usna.util.CommandLineUtils;
import usna.util.Counter;
import usna.sentiment.LabeledTweet.SENTIMENT;

/**
 * Code to label tweets with sentiment in two different ways.
 * The first simply uses a sentiment lexicon of fixed words. 
 * The second learns a lexicon from raw tweets.
 * 
 * TrainTest -data <data-dir> [-learnmylex] [-usemylex]
 *  
 * @author Nate Chambers, US Naval Academy
 */
public class TrainTest {
  Datasets datasets;
  boolean learnLexicon = false; // if true, runs your lexicon learning code.
  boolean testWithLearned = false;  // if true, loads your already learned lexicon.
  Segment seg = new Segment("dic.txt");
  
  String mypositive = "mypositive.txt";
  String mynegative = "mynegative.txt";
  List<String> englishWords = new ArrayList<String>();
  
  /**
   * YOU MUST WRITE THIS METHOD.
   * This method will run your algorithm that uses a Sentiment Lexicon to label
   * tweets with their sentiment.
   * The method receives a list of tweets, and should return a list of predicted labels.
   * @param passages A list of tweets without labels.
   * @return A list of Sentiment labels, one for each given tweet.
   */
  public List<SENTIMENT> labelWithLexicon(List<LabeledTweet> tweets, List<String> positiveWords, List<String> negativeWords) {
    //System.out.println("labelWithLexicon...");
    List<SENTIMENT> labels = new ArrayList<SENTIMENT>();
    int positive;
    int negative;
    for( LabeledTweet tweet : tweets){
    	List<String> cleanedTweet = scrubTweet(tweet);
    	positive = 0;
    	negative = 0;
    	//System.out.println(tweet);
    	//System.out.println(cleanedTweet);
    	//System.out.print("testing: ");
    	for( String word: cleanedTweet){
    		//System.out.print( word + "... ");
    		if(positiveWords.contains(word.toLowerCase())){
    			//System.out.print( " Positive contains " + word);
    			positive++;
    		}
    		else if(negativeWords.contains(word.toLowerCase())){
    			negative++;
    			//System.out.print( " Negative contains " + word);
    		}
    	}
    	if(positive > negative){
    		labels.add(SENTIMENT.POSITIVE);
			//System.out.println("Actual: " + tweet.getSentiment() + " Guess: " + SENTIMENT.POSITIVE + " tweet: " + tweet);
    	}
    	else if(negative > positive){
    		labels.add(SENTIMENT.NEGATIVE);
			
			System.out.println("Actual: " + tweet.getSentiment() + " Guess: " + SENTIMENT.NEGATIVE + " tweet: " + tweet );
    	}
    	else{
			labels.add(SENTIMENT.OBJECTIVE);
			//System.out.println("Actual: " + tweet.getSentiment() + " Guess: " + SENTIMENT.OBJECTIVE);
		}
    	//System.out.println("");
    }
    return labels;
  }
  
  private List<String> scrubTweet(LabeledTweet origional){
	  List<String> cleanTweet = new ArrayList<String>();
	  List<String> parsedWordsFromHashtag = new ArrayList<String>();
	  String tweet = origional.getText();
	  String[] wordsInTweet = tweet.split("[\\p{Blank}\\s]");
	  for(String word : wordsInTweet){
			  word = word.replaceAll("[:)]", "Love");
		  	  word = word.replaceAll("[:(]", "Hated");
		  	  word = word.replaceAll("[!]", "");
			  word = word.replaceAll("[^\\p{Print}\\s]", "");
			  
			  if(word.startsWith("http")){
				  word = "--website--";
			  }
			  
			  if(word.startsWith("#") | word.startsWith("@")){
				  //word = word.replaceAll("[\\p{Punct}\\s]", "");
				  word = word.replaceAll("[_]", "");
				  parsedWordsFromHashtag = seg.improvedMaxMatch(word);
				  //System.out.println("found: " + word + " and made: " + parsedWordsFromHashtag);
				  for(String words : parsedWordsFromHashtag){
					  cleanTweet.add(words);
				  }
				  
			  }else 
				  if(!word.isEmpty()){
				  word = word.replaceAll("[\\p{Punct}\\s]", "");
				  cleanTweet.add(word);
			  }
	  }
	  return cleanTweet;
  }
  
  private List<String> scrubTweet(List<String> wordsInTweet){
	  List<String> cleanTweet = new ArrayList<String>();
	  List<String> parsedWordsFromHashtag = new ArrayList<String>();
	  for(String word : wordsInTweet){
		  	  word = word.replaceAll("[!]", "");
			  word = word.replaceAll("[^\\p{Print}\\s]", "");
			  
			  if(word.startsWith("http")){
				  word = "--website--";
			  }
			  
			  if(word.startsWith("#") | word.startsWith("@")){
				  //word = word.replaceAll("[\\p{Punct}\\s]", "");
				  word = word.replaceAll("[_]", "");
				  parsedWordsFromHashtag = seg.improvedMaxMatch(word);
				  //System.out.println("found: " + word + " and made: " + parsedWordsFromHashtag);
				  for(String words : parsedWordsFromHashtag){
					  cleanTweet.add(words);
				  }
				  
			  }else 
				  if(!word.isEmpty()){
				  word = word.replaceAll("[\\p{Punct}\\s]", "");
				  cleanTweet.add(word);
			  }
	  }
	  return cleanTweet;
  	}

  public List<String> makeEnglishWordList(){
	  List<String> wordList= new ArrayList<String>();
	  wordList.add("the"); wordList.add("and"); wordList.add("to");
	  wordList.add("of"); wordList.add("for"); wordList.add("is");
	  wordList.add("on"); wordList.add("that"); wordList.add("by");
	  wordList.add("this"); wordList.add("with"); wordList.add("you");
	  wordList.add("i"); wordList.add("it"); wordList.add("not");
	  wordList.add("or"); wordList.add("be"); wordList.add("are");
	  wordList.add("from"); wordList.add("at"); wordList.add("as");
	  wordList.add("your"); wordList.add("all"); wordList.add("have");
	  wordList.add("more"); wordList.add("an"); wordList.add("free");
	  wordList.add("was"); wordList.add("we"); wordList.add("will");
	  wordList.add("home"); wordList.add("can"); wordList.add("us");
	  wordList.add("about"); wordList.add("if"); wordList.add("page");
	  wordList.add("my"); wordList.add("has"); wordList.add("search"); 
	  return wordList;
  }
  
  
  
  
  /**
   * YOU MUST WRITE THIS METHOD.
   */
  public void learnTheLexicon() {
	  englishWords = makeEnglishWordList();
	  int numTweets = 0;
	  List<LabeledTweet> labeledList = new ArrayList<LabeledTweet>();
	  int posTweet;
	  int negTweet;
	  
	  // Read 100 tweets. You'll want to increase this!
	  String tweet = datasets.getNextRawTweet();
	  while( tweet != null && numTweets++ < 800000) {
    	posTweet = 0;
    	negTweet = 0;
    	//get rid of the unprintable characters
    	tweet = tweet.replaceAll("[^\\p{Print}\\s]", "");
    	//System.out.println();
    	//System.out.println("---Tweet " + count + ":" + tweet);
    	
    	//Check to see if the tweet is english
    	//if(isEnglish(tweet)){
    		String[] ArrayWordsInTweet = tweet.split("[\\p{Blank}\\s]");
    		for(int x = 0; x < ArrayWordsInTweet.length -3; x++){
	  			  if(  (ArrayWordsInTweet[x].contains(":)")) || 
	  				   (ArrayWordsInTweet[x].contains("^.^")) || 
	  				   (ArrayWordsInTweet[x].contains("lol")) || 
	  				   (ArrayWordsInTweet[x].contains(":D")) || 
	  				   (ArrayWordsInTweet[x].contains("B)")) || 
	  				   (ArrayWordsInTweet[x].contains("8D")) || 
	  				   (ArrayWordsInTweet[x].contains(";)")) || 
	  				   (ArrayWordsInTweet[x].contains(";-)")) || 
	  				   (ArrayWordsInTweet[x].contains(":-D")) || 
	  				   (ArrayWordsInTweet[x].contains(":-)"))  ){ //|word.matches(".*[:D].*")){
	  				  //System.out.println("Found pos:" + ArrayWordsInTweet[x]);
	  				  posTweet++;
	  			  }
	  			  if(  (ArrayWordsInTweet[x].contains(":(")) || 
	  				   (ArrayWordsInTweet[x].contains(":,("))  || 
	  				   (ArrayWordsInTweet[x].contains(":'(")) || 
	  				   (ArrayWordsInTweet[x].contains(":-(")) || 
	  				   (ArrayWordsInTweet[x].contains(">:(")) || 
	  				   (ArrayWordsInTweet[x].contains(">:-(")) || 
	  				   (ArrayWordsInTweet[x].contains("D:")) ){
	  				  //System.out.println("Found neg:" + ArrayWordsInTweet[x]);
	  				  negTweet++;
	  			  }
    		//}
    		if(posTweet > negTweet){
    			labeledList.add(new LabeledTweet(SENTIMENT.POSITIVE, tweet));
    			//System.out.println("positive: " + tweet);
    		}
    		if(posTweet < negTweet){
    			labeledList.add(new LabeledTweet(SENTIMENT.NEGATIVE, tweet));
    			//System.out.println("------------negative: " + tweet);
    		}
    		else{
    			labeledList.add(new LabeledTweet(SENTIMENT.OBJECTIVE, tweet));
    		}
    	}
    	
    	tweet = datasets.getNextRawTweet();
    }
    
    //prints # of sucessfully labled tweets
    //printLabels(labeledList);
    
    //Go through the labels and make a counter for positive words
    Counter<String> positive = new Counter<String>();
    Counter<String> negative = new Counter<String>();
    for(int x = 0; x < labeledList.size(); x++){
    	if(labeledList.get(x).getSentiment() == SENTIMENT.POSITIVE){
			  for(String word : labeledList.get(x).getText().split("[\\p{Blank}\\s]")){
				  if(seg.dictionary.contains(word.toLowerCase()))
					  positive.incrementCount(word.toLowerCase(), 1);
			  }
    	}
    	else if(labeledList.get(x).getSentiment() == SENTIMENT.NEGATIVE){
    			for(String word : labeledList.get(x).getText().split("[\\p{Blank}\\s]")){
    				if(seg.dictionary.contains(word.toLowerCase()))
    					negative.incrementCount(word.toLowerCase(), 1);
    			}
    	}
    }
    
    
    //assign each word in the positive words list a value based on its count
    //positive.totalCount();
    
    //make a priority queue
    usna.util.PriorityQueue<String> positivePriority = positive.asPriorityQueue();

    //normalizes the common words
    String word;
    while(positivePriority.hasNext()){
    	word = positivePriority.next();
    	if(positive.getCount(word) > 0 && negative.getCount(word) > 0){
    		if(positive.getCount(word) > negative.getCount(word)){
    			positive.setCount(word, positive.getCount(word)*(positive.getCount(word)/(positive.getCount(word)+negative.getCount(word))));
    			negative.setCount(word, 0);
    		}
    		else{
    		negative.setCount(word, negative.getCount(word)*(negative.getCount(word)/(positive.getCount(word)+negative.getCount(word))));
    		positive.setCount(word, 0);
    		}
    	}
    }

    //remake the priority queues with new word counts
    positivePriority = positive.asPriorityQueue();
    usna.util.PriorityQueue<String> negativePriority = negative.asPriorityQueue();
   
    
    System.out.println(positive.toString(100));
    System.out.println("------------------------");
    System.out.println(negative.toString(100));
    
    System.out.println("Saving our new lexicon to disk: " + mypositive + " and " + mynegative);
    // Output your positive words to file.
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(new File(mypositive)));
      // YOUR CODE HERE!
      while(positivePriority.hasNext() && positivePriority.getPriority() >5){
    	  word = positivePriority.next();
    	  writer.write(word+ "\n");
      }
      writer.close();
    } catch( IOException ex ) { ex.printStackTrace(); }
    
    // Output your negative words to file.
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(new File(mynegative)));
      // YOUR CODE HERE!
      while(negativePriority.hasNext() && negativePriority.getPriority() >5){
    	  word = negativePriority.next();
    	  writer.write(word+ "\n");
      }
      writer.close();
  	 } catch( IOException ex ) { ex.printStackTrace(); }
  }
  
  public void printLabels(List<LabeledTweet> labeledList){
	  int pos = 0;
	  int neg = 0;
	  int obj = 0;
	  for(int x = 0; x < labeledList.size(); x++){
		  if(labeledList.get(x).getSentiment() == SENTIMENT.OBJECTIVE)
			  obj++;
		  else if(labeledList.get(x).getSentiment() == SENTIMENT.POSITIVE)
			  pos++;
		  else if(labeledList.get(x).getSentiment() == SENTIMENT.NEGATIVE)
			  neg++;
	  }
	  //System.out.println("Positive sentences: " + pos + " and negative: " + neg + " and objective :" + obj);
  }
  
  public boolean isEnglish(String tweet){
	  int wordFound = 0;
	  List<String> concatedWords = new ArrayList<String>();
	  List<String> wordsInTweet = new ArrayList<String>();
	  String[] ArrayWordsInTweet = tweet.split("[\\p{Blank}\\s]");
	  double englishCutoff = .7;
	  
	  //make a list of strings out of the tweet
	  for(String word: ArrayWordsInTweet){
		  wordsInTweet.add(word);
	  }
	
	  //Delete the meta data off the end
	  for(int x = 0; x < 3; x++){
		  if(wordsInTweet.size() > 1){
			  wordsInTweet.remove(wordsInTweet.size()-1);
		  }
	  }
		  	
	  //scrub out the nasty stuff
	  wordsInTweet = scrubTweet(wordsInTweet);
	  //System.out.println( "\t" +"---Scrubbed Tweet:" + wordsInTweet.toString());
	  //System.out.print( "\t" );
					  	
	  //test each tweet against the dictionary
	  for(int x = 0; x < wordsInTweet.size(); x++){
		  if(wordsInTweet.get(x).length() > 0){
			  //try to split up the word into smaller words
		    		concatedWords = seg.improvedMaxMatch(wordsInTweet.get(x));
		    		for(String words : concatedWords){
		    			//This was giving me a lot of spanish sentences. 
		    			//if(seg.dictionary.contains(words.toLowerCase())){
		    			//	wordFound++;
		    			//}
		    			
		    			if(englishWords.contains(words.toLowerCase()))
		    				return true;
		    		}
	  		}
	  	}
	  	
	  	//Compare the amount of english words found against the length of the tweet.
	  	//System.out.println("\t Fraction is " + (double)((double)wordFound/(double)wordsInTweet.size()));
	  	//if((double)((double)wordFound/(double)wordsInTweet.size()) > englishCutoff){
  		//System.out.println("------Identified as English");
	  		//return true;
	  	//}
	  	return false;
  	}
  
  /**
   * ***************************************************
   * 
   * The following methods shouldn't need to be changed.
   * 
   * ***************************************************
   */
  
  /**
   * DO NOT CHANGE THIS METHOD.
   * This runs the three-step process of train/test/evaluate.
   */
  public void execute() {
    // Learn a lexicon from raw tweets!
    if( learnLexicon ) {
      System.out.println("Learn...");
      learnTheLexicon();
    }
    // No lexicon learning. Use a lexicon on disk and label tweets!
    else {
      System.out.println("Test...");
      List<SENTIMENT> guesses = null;
      // Label tweets.
      if( testWithLearned )
        guesses = labelWithLexicon(datasets.getLabeledTweets(), datasets.getWordsFromFile(mypositive), datasets.getWordsFromFile(mynegative));
      else
        guesses = labelWithLexicon(datasets.getLabeledTweets(), datasets.getLexiconPositiveWords(), datasets.getLexiconNegativeWords());

      // Evaluate the model.
      double accuracy = Evaluator.evaluate(guesses, datasets.getLabeledTweets());
      System.out.printf("Final Accuracy = %.3f%%\n", accuracy);
    }
  }

  
  /**
   * YOU PROBABLY DON'T WANT TO CHANGE THIS UNLESS YOU ARE ADDING COMMAND-LINE FLAGS.
   * (make sure the current flags still work)
   */
  public TrainTest(String[] args) {
    Map<String,String> flags = CommandLineUtils.simpleCommandLineParser(args);
    
    if( !flags.containsKey("-data") ) {
      System.out.println("TrainTest -data <data-dir> [-learnmylex] [-usemylex]");
      System.exit(1);
    }
    
    if( flags.containsKey("-learnmylex") && flags.containsKey("-usemylex") ) {
      System.out.println("Hmm, can't learn a lexicon and use the lexicon at the same time.");
      System.exit(1);
    }
    else if( flags.containsKey("-learnmylex") )
      learnLexicon = true;
    else if( flags.containsKey("-usemylex") )
      testWithLearned = true;
    
    datasets = new Datasets(flags.get("-data"));
  }


  /**
   * DO NOT CHANGE THIS MAIN FUNCTION.
   */
  public static void main(String[] args) {
    TrainTest program = new TrainTest(args);
    program.execute();
  }
}
