package usna.author;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import usna.author.Passage.AUTHOR;
import usna.util.CommandLineUtils;
import usna.util.LinearInterpolation;
import usna.util.SmoothedBigram;

/**
 * Code to train an author identification model, and to test the same model.
 * When run properly, this code will output a final accuracy score.
 * 
 * TrainTest -data <data-dir> [-test]
 *  
 * @author Nate Chambers, US Naval Academy
 */
public class TrainTest {
/*  private static final String AUSTEN = null;
private static final String CONRAD = null;
private static final String DICKENS = null;
private static final String ELIOT = null;
private static final String HARDY = null;
private static final String HAWTHORNE = null;
private static final String JAMES = null;
private static final String SHAKESPEARE = null;
private static final String SHAW = null;
private static final String TWAIN = null;*/
Datasets datasets;
  boolean runontest = false;
  LinearInterpolation authors[] = new LinearInterpolation[10];
  AUTHOR[] arthurName = new AUTHOR[10];
  int nameIndex = 0;
  //public static enum AUTHOR { AUSTEN, CONRAD, DICKENS, ELIOT, HARDY, HAWTHORNE, JAMES, SHAKESPEARE, SHAW, TWAIN };
  
  /**
   * YOU MUST WRITE THIS METHOD.
   * This method will train your Naive Bayes classifier.
   * The method receives a list of Passage objects which are short passages from
   * novels, ~500 words each. Each object also contains the author who wrote the
   * passage. This method should compute features and their probabilities for
   * a naive bayes classifier.
   * @param passages A list of passages with author labels.
   */
  @SuppressWarnings("null")
public void train(List<Passage> passages) {
	  AUTHOR Author;
	  AUTHOR prevAuthor = passages.get(0).getAuthor();
	  int index = 0;
	  List<Passage> sumPassages = new ArrayList<Passage>();
	  
	  Collection<List<String>> sentences = new ArrayList();

	  Author = passages.get(0).getAuthor();
	  
	  int first = 0;
	  int last = 0;
	  for(int ii= 0; ii <passages.size(); ii++){
		  //System.out.println(Author);
		  //System.out.println(passages.size());
		  Author = passages.get(ii).getAuthor();
		  if(Author == prevAuthor)
		  {
			  sumPassages.add(passages.get(ii));
		  }
		  if(Author != prevAuthor){
			  last = ii-1;
			  sentences = new ArrayList();
			  sentences.add(this.parseSentences(sumPassages));
			  //System.out.println(sentences);System.out.println();System.out.println();System.out.println();

			 System.out.println("Author " + prevAuthor + " got passages "+ first + "-" + last);
			 first = ii;
			  authors[index] = new LinearInterpolation();
			  authors[index].train(sentences);
			  arthurName[index] = prevAuthor;
			  index = index +1;
			  prevAuthor = passages.get(ii).getAuthor();
			  sumPassages.clear(); 
			  sumPassages.add(passages.get(ii));
			  
		  }	  		 		  
	  }
	  sentences = new ArrayList();
	  sentences.add(this.parseSentences(sumPassages));
	  //System.out.println(sentences);System.out.println();System.out.println();System.out.println();
	  authors[index] = new LinearInterpolation();
	  authors[index].train(sentences);
	  arthurName[9] = Author;
	  //System.out.println(sumPassages);
  }
  public List<String> parseSentences(List<Passage> passages){
	  List<String> sentences = new ArrayList<String>();
	  for(int ii = 0; ii < passages.size(); ii++){
	  String passage = passages.get(ii).getText().toLowerCase();
	  //My passage is a long string that is all lower case. 
	  //I need to figure out how to split it into sentences.
	  //System.out.println(ii);
	  //System.out.println( "------------ Parsed Sentences -------------");
	 String[] wordsInPassage = passage.split("[\\p{Blank}\\s]");
	 String sentence = "";
	  for(int i = 0; i < wordsInPassage.length; i++){
		  //System.out.print( wordsInPassage[i] + " ");
		  
		  if(wordsInPassage[i].length() > 0){
			  sentence += wordsInPassage[i].replace(", " , " ");
			  sentence += " ";
			  if(wordsInPassage[i].substring(wordsInPassage[i].length() - 1).equals(".")){  // || wordsInPassage[i].substring(wordsInPassage[i].length() - 1).equals(";") ){
				  sentences.add(sentence.substring(0, sentence.length()-2));
				  //System.out.println(wordsInPassage[i]);
				  //System.out.println();
				  sentence = "";
			  }
		  }
	  }}
	 //System.out.println(sentences);
	  return sentences;
  }

  /**
   * YOU MUST WRITE THIS METHOD.
   * This method will run your learned Naive Bayes classifier on a new set of
   * passages. Given a list of passages, you must predict the most probable
   * author (of course, don't look at the Passage object's labeled author for
   * this decision). The method should return a list of authors where the ith
   * index in the list corresponds to the ith given passage.
   * @param passages The list of passages to label.
   * @return A list of author labels for each of the given passages. This list
   *         must be the same length of the given list of passages, and the labels
   *         must be aligned with the passages.
   */
  public List<Passage.AUTHOR> test(List<Passage> passages) {
	  //System.out.println("entering test)");
    List<Passage.AUTHOR> guesses = new ArrayList<Passage.AUTHOR>();
    //Passage.enum AUTHOR = new enum();
    List<String> sentences;
    for(Passage testPassage :  passages ){
    	List<Passage> testPassegeConvertedToList = new ArrayList<Passage>();
    	testPassegeConvertedToList.clear();
    	testPassegeConvertedToList.add(testPassage);
    	int index = 0;
    	double passageGuess[] = new double[10];
    	double guess = 0;
    	for(int i = 0; i < 10; i++){
    			sentences = parseSentences(testPassegeConvertedToList);
    			guess = authors[index].getSentenceProbability(sentences);
    			passageGuess[index] = guess;
    			index = index + 1;
    	}
    	double top = 0;
    	int highestIndex = 0;
    	for(int i = 0; i < 10; i++)
    	{
    		if((passageGuess[i]*-1)  > top ){
    			top = passageGuess[i]*-1;
    			highestIndex = i;
    			//System.out.println("highest is now :" + top + " and highest index is: " + highestIndex);
    		}
    		System.out.println(arthurName[i] + "'s guess is " + passageGuess[i]*-1 + " ");
    	}
    	//System.out.println(passageGuess[0] + " " + highestIndex);
    	
    	//System.out.println(arthurEnum[0] + " " + arthurEnum[1] + " " +  arthurEnum[2]);
    	guesses.add(arthurName[highestIndex]);    //  (int) (Math.random()*10)]);
    	System.out.println(arthurName[highestIndex] + " is the winner for this passage");
    	System.out.println();
    	
   
    	
    	
    }
    
    
    
    return guesses;
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
    // Train the model.
    train(datasets.getTrainingSet());
    
    // Test the model.
    List<Passage> testset = null;
    if( runontest ) testset = datasets.getTestSet();
    else testset = datasets.getDevelopmentSet();
    List<Passage.AUTHOR> predictedLabels = test(testset);
    
    // Evaluate the model.
    double accuracy = Evaluator.evaluate(predictedLabels, testset);
    System.out.printf("Final Accuracy = %.3f%%\n", accuracy);
  }

  
  /**
   * YOU PROBABLY DON'T WANT TO CHANGE THIS UNLESS YOU ARE ADDING COMMAND-LINE FLAGS.
   * (make sure the current flags still work)
   */
  public TrainTest(String[] args) {
    Map<String,String> flags = CommandLineUtils.simpleCommandLineParser(args);
    
    if( !flags.containsKey("-data") ) {
      System.out.println("TrainTest -data <data-dir>");
      System.exit(1);
    }
    if( flags.containsKey("-test") )
      runontest = true;
    
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
