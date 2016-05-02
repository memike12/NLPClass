package usna.parser;

import java.util.ArrayList;
import java.util.List;

import usna.ling.Tree;
import usna.ling.TreeAnnotations;
import usna.util.CounterMap;
import usna.util.Pair;
import usna.util.Triplet;


/**
 * The CKY Parser you must implement.
 */
public class CKYParser implements Parser {
	private class Cell{
		public List<Pair<Rule, Double>> tags;
		public List<Pair<Cell, Cell>> backs;
		public Cell(){
			tags=new ArrayList<Pair<Rule, Double>>();
			backs=new ArrayList<Pair<Cell,Cell>>();
		}
		public void add(Rule r1, Double d1, Cell left, Cell right){
			tags.add(new Pair(r1, d1));
			backs.add(new Pair(left, right));
		}
    }
	
  private Grammar grammar; // holds your binary and unary non-terminal rules
  private Lexicon lexicon; // holds your unary POS->word lexicon rules.
  CounterMap<List<String>,Tree<String>> knownParses;

  
  /** 
   * This function takes a list of Trees and builds a lexicon and grammar.
   */
  public void train(List<Tree<String>> trainTrees) {

    // NOTE: The training trees are not CNF. You must first binarize them
    //       before you create the Lexicon and Grammar.
    
    // YOUR CODE HERE
	  List<Tree<String>> newTreeList = new ArrayList<Tree<String>>();
	  for( Tree<String> tree : trainTrees){
		  newTreeList.add(TreeAnnotations.binarizeTree(tree));
	  }

    
    // This automatically creates your "lexicon" of words and their POS tags.
    lexicon = new Lexicon(trainTrees);
    
    // This automatically creates your grammar and counts all of the seen rules.
    // You don't have to count yourself...just call the function!
    grammar = new Grammar(newTreeList);
    
    //lexicon.getRuleProbability(tag, word)
    //grammar.getBinaryRulesByLeftChild(leftChild)
    //grammar.getUnaryRulesByOnlyChild(child)
  }

  /**
   * This function runs the CKY algorithm.
   * It assumes the global grammar and lexicon variables have been setup.
   * 
   * Given one sentence as the parameter (a list of words), return a parse tree.
   */
  public Tree<String> getBestParse(List<String> sentence) {
    // YOUR CODE HERE
    // HINT: this will be somewhat long, and require supporting functions

	  //Need to make a cell class which can hold multiple pairs from pair.java and also what the cell came from.
	  //Baseline parser is the stupid way of doing things. 
	  //the CKY algorythim 
	  List<ArrayList<Cell>> probs=new ArrayList<ArrayList<Cell>>(); 
		//Initializing
		for (int i=0; i<sentence.size(); i++){
		    probs.add(new ArrayList<Cell>());
		    for (int j=0; j<sentence.size(); j++){
		    	Cell c=new Cell();
		    	probs.get(i).add(c);
		    }
		}
		int i=0;
		//Does the diagonals
		for (String word : sentence){
		    Cell c=probs.get(i).get(i);
		    for (String pos :  lexicon.getAllTags()){
		    	
		    	//may want to change this to add into the cell the right way inside this function. 
		    	//would make it 
		    	//
		    	//
		    	
		    	//adding this if to make sure I'm actuall adding only real rules
		    	if(lexicon.getRuleProbability(pos, word) > 0)
		    		addUnary(c, lexicon.getRuleProbability(pos, word), pos);
		    }
		    System.out.println(word);
		    for (Pair p : c.tags){
		    	System.out.println(p);
		    }
		    System.out.println();
		    i++;
		}
		
		//Does the next level
		int end;
		double prob;
		for(int span=2; span<sentence.size(); span++){
			for(int begin = 0; begin<(sentence.size()-span); begin++){
				end = begin-span;
				for(int split = begin+1; split < end-1; split++){
					Cell c1= probs.get(begin).get(split);
					Cell c2= probs.get(split).get(end);
					//got the cells (maybe) now i need to assign the left and right words to Pair<left, right, probability>
					List<Pair<Rule,Double>> leftChildTags = c1.tags;
					List<Pair<Rule,Double>> rightChildTags = c2.tags;
					List<Pair<Rule,Double>> parentTags;
					List<BinaryRule> possibleRules = new ArrayList<BinaryRule>();
					List<BinaryRule> rules = new ArrayList<BinaryRule>();
					for(Pair leftChild : leftChildTags){
						possibleRules.addAll(grammar.getBinaryRulesByLeftChild((String)leftChild.getFirst()));
					}
					for(Pair rightChild : rightChildTags){
						//not that I have all of the possible rules from the left child, I need to check the possibilities against my right rules and narrow down to actual rules that apply
						
						if()
					}
					for(BinaryRule rule : rules){
							//prob = leftChild.getThird() * rightChild.getThird() * probability of grammar rule happening
						prob = leftChild.getThird() * rightChild.getThird() * lexicon.getRuleProbability(tag, word)
					}
				}		
			}
		}
	return null;
}
  	
  	public void addUnary(Cell c, double probs, String child){
		List<UnaryRule> unes=grammar.getUnaryRulesByOnlyChild(child);
		c.add(child, null, probs, null);
		for (UnaryRule u : unes){
			addUnary(c, probs*u.score, u.parent);
		}
  	}
  
}
