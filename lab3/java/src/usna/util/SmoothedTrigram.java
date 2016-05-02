package usna.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import usna.util.Counter;
import usna.util.EmpiricalUnigramLanguageModel;

public class SmoothedTrigram extends LanguageModel{
	private Counter<String> wordCounterTrigram;
	private Counter<String> wordCounterBigram;
	private Counter<String> wordCounterUnigram;
	private double bigramTotal;
	private double trigramTotal;
	private double unigramTotal;
	EmpiricalUnigramLanguageModel unigrams = new EmpiricalUnigramLanguageModel();
	BigramModel bigrams = new BigramModel();
	
	public SmoothedTrigram(){
		wordCounterTrigram = new Counter<String>();
		wordCounterBigram = new Counter<String>();
	    trigramTotal = 0;//Double.NaN;
	}
	
	public SmoothedTrigram(Collection<List<String>> trainingSentences){
		this();
	    train(trainingSentences);
	}
	
	@Override
	public void train(Collection<List<String>> trainingSentences) {
		wordCounterTrigram = new Counter<String>();
		String word;
		String nextWord;
		String wordAfterNext;
		String trigram;
		
		
		unigrams.train(trainingSentences);
		unigramTotal = unigrams.getTotal();
		wordCounterUnigram = unigrams.getCounter();
		
		
		bigrams.train(trainingSentences);
		bigramTotal = bigrams.getTotal();
		wordCounterBigram = bigrams.getCounter();
		
	    for (List<String> sentence : trainingSentences) {
		      List<String> stoppedSentence = new ArrayList<String>(sentence);
		      stoppedSentence.add(STOP);
		      wordCounterTrigram.incrementCount(START + " " + START + " " + stoppedSentence.get(0), 1.0);
		      wordCounterTrigram.incrementCount(START + " " + stoppedSentence.get(0) + " " + stoppedSentence.get(1), 1.0);
		      wordCounterBigram.incrementCount(START + " " + START, 1.0);
		      
		      for (int i = 0; i < stoppedSentence.size()-2; i++) {
		    	  word = stoppedSentence.get(i);
		    	  nextWord = stoppedSentence.get(i+1);
		    	  wordAfterNext = stoppedSentence.get(i+2);
		    	  trigram = word+ " " + nextWord + " " + wordAfterNext;
		    	  wordCounterTrigram.incrementCount(trigram, 1.0);
		      }
	    }
	    trigramTotal = wordCounterTrigram.totalCount();
	    //System.out.println("Bigram = " + wordCounterBigram.size() + " and Trigram count = " + wordCounterTrigram.size() + " and unigram count = " + wordCounterUnigram.size());
	    /*try{
	    	printLibrary();
	    }
	    catch(Exception e){
	    	System.out.println("error" + e);
	    }*/
	}

	@Override
	public double getWordProbability(List<String> sentence, int index) {
		String first = "";
		String second = "";
		String third = "";
		if (index == sentence.size()) {
			if(index == 1){
				return getTrigramProbability(START, sentence.get(index-1), STOP);
			}
			else{
				first = sentence.get(index-2);
				second = sentence.get(index-1);
				return getTrigramProbability(first, second, STOP);
			}
	    } 
		else {
			if(index == 0){
				first = START;
				second = START;
				third = sentence.get(index);
			}
			else if(index-1 == 0){
				first = START;
				second = sentence.get(index-1);
				third = sentence.get(index);
			}
			else{
				first = sentence.get(index-2);
				second = sentence.get(index-1);
				third = sentence.get(index);
			}
			return getTrigramProbability(first, second, third);
		}
	}

	@Override
	public Collection<String> getVocabulary() {
		// TODO Auto-generated method stub
		Set<String> vocabulary = new HashSet(wordCounterUnigram.keySet());
	    //vocabulary.add(UNK);
	    return vocabulary;
	}

	@Override
	public List<String> generateSentence() {
		List<String> sentence = new ArrayList<String>();
	    String word = generateWord(START, START);
	    //System.out.println(" - - - - - - - - " + word);
	    while (!word.equals(STOP)) {
	      sentence.add(word);
	      //System.out.println("Sentence is: " + sentence);
	      if(sentence.size()-2 < 0){
	    	  word = generateWord( START , word);
	      }
	      else
	      word = generateWord( sentence.get(sentence.size()-2), word);
	    }
	    return sentence;
	}
	
	public double getTrigramProbability(String firstWord, String secondWord, String thirdWord) {
	    double count = wordCounterTrigram.getCount(firstWord + " " + secondWord + " " + thirdWord);
	    double biCount = bigrams.wordCounterBigram.getCount( firstWord + " " + secondWord);
	    double smoother = .0001;
	    
	    //System.out.println("Trigram: " + firstWord + " " + secondWord + " " + thirdWord);
	    //System.out.println("count ="+ count + "and biCount ="+ biCount);
	    double probability = ((count + smoother) / (biCount + (wordCounterUnigram.size()*smoother)));
	    //System.out.println(probability + " numerator :" +(count + smoother) +  " denominator :" + (biCount + wordCounterUnigram.size()*smoother));
	    //probability = count/biCount;
	    return probability;
	 }
	 
	 private String generateWord(String firstWord, String secondWord) {
		    double sample = Math.random();
		    double sum = 0.0;
		    //System.out.println( firstWord + " " + secondWord);
		    for (String word : wordCounterUnigram.keySet()) {
		      sum += getTrigramProbability(firstWord, secondWord, word);
		      if (sum >= sample) {
		    	  //System.out.println("Sum is: " + sum + " and word is:" + word);
		    	  return word;
		      }
		    }
		    //System.out.println("Sum was :" + sum + " and random was :" + sample);
		    System.out.println("ERROR: THIS SHOULDN'T BE HERE /" + firstWord + "/ /" + secondWord + "/");
		    return LanguageModel.UNK;   // a little probability mass was reserved for unknowns
	}
	 
	 private void printLibrary() throws FileNotFoundException, UnsupportedEncodingException{
		 PrintWriter writer3 = new PrintWriter("MyTrigramLibrary.txt", "UTF-8");
	        writer3.println(wordCounterTrigram.toString());
			writer3.close();	
		 
		 PrintWriter writer = new PrintWriter("MyBigramLibrary.txt", "UTF-8");
	        writer.println(wordCounterBigram.toString());
			writer.close();	
			
			PrintWriter writer2 = new PrintWriter("MyUnigramLibrary.txt", "UTF-8");
			writer2.println(wordCounterUnigram.toString());
			writer2.close();
		
		}
	 
	 public double getTotal(){
		  return bigramTotal;
	}
	  
	public Counter<String> getCounter(){
		  return wordCounterBigram;
	}

}
