package usna.langmodel;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import usna.util.Counter;
import usna.langmodel.EmpiricalUnigramLanguageModel;

public class SmoothedBigram extends LanguageModel {
	public Counter<String> wordCounterBigram;
	private Counter<String> wordCounterUnigram;
	private double bigramTotal;
	private double unigramTotal;
	private double bestSmooth;
	
	public SmoothedBigram(){
		wordCounterBigram = new Counter<String>();
		wordCounterUnigram = new Counter<String>();
	    bigramTotal = Double.NaN;
	}
	
	public SmoothedBigram(Collection<List<String>> trainingSentences){
		this();
	    train(trainingSentences);
	}
	@Override
	public void train(Collection<List<String>> trainingSentences) {
		wordCounterBigram = new Counter<String>();
		String word;
		String nextWord;
		String bigram;
		
		EmpiricalUnigramLanguageModel unigrams = new EmpiricalUnigramLanguageModel();
		unigrams.train(trainingSentences);
		unigramTotal = unigrams.getTotal();
		wordCounterUnigram = unigrams.getCounter();
		//wordCounterUnigram.incrementCount("", 1.0);
	    for (List<String> sentence : trainingSentences) {
		      List<String> stoppedSentence = new ArrayList<String>(sentence);
		      stoppedSentence.add(STOP);
	    	  wordCounterBigram.incrementCount(START + " " + stoppedSentence.get(0), 1.0);
    		  wordCounterUnigram.incrementCount(START, 1.0);
		      for (int i = 0; i < stoppedSentence.size()-1; i++) {
		       	  word = stoppedSentence.get(i);
		    	  nextWord = stoppedSentence.get(i+1);
		    	  bigram = word + " " + nextWord;
		    	  //System.out.println("/" + bigram + "/");
		    	  wordCounterBigram.incrementCount(bigram, 1.0);
		    	  //System.out.println(bigram + "/t  --" + wordCounterBigram.getCount(bigram) );
		      }
	    }
	    bigramTotal = wordCounterBigram.totalCount();
	    /*try{
	    	printLibrary();
	    }
	    catch(Exception e){
	    	System.out.println("error" + e);
	    }*/
    }
	
	@Override
	public double getWordProbability(List<String> sentence, int index) {
		// TODO Auto-generated method stub
		String first = "";
		String second = "";
		if (index == sentence.size()) {
			if(index-1 < 0){
				return getBigramProbability(START, STOP);
			}
			else{
				first = sentence.get(index-1);
				return getBigramProbability(first.trim(), STOP);
			}
	    } 
		else {
			if(index-1 < 0){
				first = START;
				second = sentence.get(index).trim();
				return getBigramProbability(first, second);
			}
			else{
				first = sentence.get(index-1);
				return getBigramProbability(first.trim(), sentence.get(index));
			}
		}
	}

	@Override
	public Collection<String> getVocabulary() {
	    Set<String> vocabulary = new HashSet(wordCounterUnigram.keySet());
	    return vocabulary;
	}

	@Override
	public List<String> generateSentence() {
		List<String> sentence = new ArrayList<String>();
		//int index = 0;
	    String word = generateWord(START);
	    //System.out.println(word);
	    while (!word.equals(STOP)) {
	      sentence.add(word);
	      word = generateWord(word);
	    }
	    //System.out.println(sentence + "-------------------");
	    return sentence;
	}

	public double getBigramProbability(String firstWord, String secondWord) {
		//System.out.println("/" + firstWord + " " + secondWord + "/");
	    double count = wordCounterBigram.getCount(firstWord + " " + secondWord);
	    double unicount = wordCounterUnigram.getCount(firstWord);
	    double smoother = .0001;
	    //System.out.println("/" + firstWord + " " + secondWord + "/");
	    //if (unicount == 0) {                   // unknown word
	      //System.out.println("UNKNOWN WORD:/" + firstWord + "/" + secondWord);
	    //  return 0;
	    //}
	   // System.out.println("KNOWN WORD: " + firstWord + " / " + secondWord + "//" + count);
	    double probability = ((count + smoother) / (unicount+wordCounterUnigram.size()*smoother));
	    return probability;
	 }
	
	private String generateWord(String prevWord) {
	    double sample = Math.random();
	    double sum = 0.0;
	    //System.out.println("first" + prevWord);
	    for (String word : wordCounterUnigram.keySet()) {
	    	//System.out.println("second" + "/" + prevWord + "/");
	      sum += getBigramProbability(prevWord, word);
	      //System.out.print(sum);
	      if (sum >= sample) {
	        return word;
	      }
	    }
	    System.out.println("ERROR: shouldn't be here");
	    //wordCounterBigram.incrementCount(prevWord + " " + word, .5);
	    return UNK;   // a little probability mass was reserved for unknowns
	}
	
	public double getTotal(){
		  return bigramTotal;
	}
	  
	public Counter<String> getCounter(){
		  return wordCounterBigram;
	}
	
	private void printLibrary() throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("MyBigramLibrary.txt", "UTF-8");
        writer.println(wordCounterBigram.toString());
		writer.close();	
		
		PrintWriter writer2 = new PrintWriter("MyUnigramLibrary.txt", "UTF-8");
		writer2.println(wordCounterUnigram.toString());
		writer2.close();
	
	}
		
	
}
