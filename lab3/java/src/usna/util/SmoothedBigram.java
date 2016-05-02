package usna.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import usna.util.Counter;

/**
 * THIS CODE IS FOR THE 2015 USNA NLP CLASS ONLY.
 * DO NOT DISTRIBUTE.
 * @author Chambers
 */
public class SmoothedBigram extends LanguageModel {
  public Counter<String> unigramCounter;
  public Counter<String> bigramCounter;
  double lambda = 0.0001;
  double sizeOfVocabulary = 0.0;

  private double bigramTotal;
  private double total;
  
  public Counter<String> getCounter(){
	  return this.bigramCounter;
  }
  
  public double getTotal(){
	  return this.total;
  }

  @Override
  public List<String> generateSentence() {
    List<String> sentence = new ArrayList<String>();
    String word = generateWord(START);
    while (!word.equals(STOP)) {
      sentence.add(word);
      word = generateWord(word);
    }
    return sentence;
  }

  @Override
  public Collection<String> getVocabulary() {
    Set<String> vocabulary = new HashSet<String>();
    for( String token : unigramCounter.keySet() )
      vocabulary.add(token);		
    return vocabulary;
  }

  @Override
  public double getWordProbability(List<String> sentence, int index) {
    if (index == sentence.size()) {
      return getBigramProbability(sentence.get(sentence.size()-1), LanguageModel.STOP);
    } else if( index == 0 ) {
      return getBigramProbability(START, sentence.get(0));
    } else {
      return getBigramProbability(sentence.get(index-1), sentence.get(index));
    }
  }

  @Override
  public void train(Collection<List<String>> trainingSentences) {
    unigramCounter = new Counter<String>();
    bigramCounter = new Counter<String>();
    for (List<String> sentence : trainingSentences) {
      List<String> stoppedSentence = new ArrayList<String>(sentence);
      stoppedSentence.add(STOP);
      String previousWord = START;
      unigramCounter.incrementCount(START, 1.0);
      for (String word : stoppedSentence) {
	unigramCounter.incrementCount(word, 1.0);
	bigramCounter.incrementCount(previousWord + " " + word, 1.0);
	previousWord = word;
      }
    }
    sizeOfVocabulary = getVocabulary().size();
    bigramTotal = bigramCounter.totalCount();
  }

  public double getBigramProbability(String one, String two) {
    double oneCount = unigramCounter.getCount(one) + sizeOfVocabulary*lambda;
    double bigramCount = bigramCounter.getCount(one + " " + two) + lambda;
    return bigramCount / oneCount;
  }
	
  /**
   * Returns a random word sampled according to the model.  A simple
   * "roulette-wheel" approach is used: first we generate a sample uniform
   * on [0, 1]; then we step through the vocabulary eating up probability
   * mass until we reach our sample.
   */
  private String generateWord(String previousWord) {
    double sample = Math.random();
    double sum = 0.0;
    for( String word : unigramCounter.keySet() ) {
      double prob = getBigramProbability(previousWord, word); 
      sum += prob;
      if (sum > sample)
	return word;
    }
    return LanguageModel.UNK;   // ERROR: SHOULDN'T GET HERE
  }
}