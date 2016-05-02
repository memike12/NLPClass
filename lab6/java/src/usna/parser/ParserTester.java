package usna.parser;

import usna.io.BioIETreebankReader;
import usna.io.GENIATreebankReader;
import usna.io.PennTreebankReader;
import usna.ling.Tree;
import usna.ling.Trees;
import usna.parser.EnglishPennTreebankParseEvaluator;
import usna.util.*;

import java.io.File;
import java.util.*;


/**
 * Main class to run Lab 6 for SI485i.
 *
 * @author Dan Klein
 * @author Nate Chambers
 */
public class ParserTester {
  // Longest sentence length that will be tested on.
  private static int MAX_LENGTH = 20;

  /**
   * This function takes a parser (your CKY parser), and runs it on the given list of parse trees.
   * It takes each parse tree, strips it down to just the raw sentence, and asks your parser what
   * it thinks the true parse tree is. This function then comparse the parser's guessed tree to
   * the true tree.
   * 
   * The function prints out the overall precision/recall/F1 scores.
   * 
   * @param parser A parser.
   * @param testTrees A list of gold trees.
   */
  private static void testParser(Parser parser, List<Tree<String>> testTrees) {
    EnglishPennTreebankParseEvaluator.LabeledConstituentEval<String> eval = 
      new EnglishPennTreebankParseEvaluator.LabeledConstituentEval<String>(Collections.singleton("ROOT"), new HashSet<String>(Arrays.asList(new String[] {"''", "``", ".", ":", ","})));

    for (Tree<String> testTree : testTrees) {
      List<String> testSentence = testTree.getYield();

      if (testSentence.size() > MAX_LENGTH)
        continue;
      
      Tree<String> guessedTree = parser.getBestParse(testSentence);
      System.out.println("Guess:\n" + Trees.PennTreeRenderer.render(guessedTree));
      System.out.println("Gold:\n"  + Trees.PennTreeRenderer.render(testTree));
      eval.evaluate(guessedTree, testTree);
    }
    eval.display(true);
  }
  
  private static List<Tree<String>> readTrees(String basePath, int low, int high) {
		Collection<Tree<String>> trees = PennTreebankReader.readTrees(basePath, low, high);
		// normalize trees
		Trees.TreeTransformer<String> treeTransformer = new Trees.StandardTreeNormalizer();
		List<Tree<String>> normalizedTreeList = new ArrayList<Tree<String>>();
		for (Tree<String> tree : trees) {
			Tree<String> normalizedTree = treeTransformer.transformTree(tree);
			// System.out.println(Trees.PennTreeRenderer.render(normalizedTree));
			normalizedTreeList.add(normalizedTree);
		}
		return normalizedTreeList;
	}

	private static List<Tree<String>> readTrees(String basePath) {
		Collection<Tree<String>> trees = PennTreebankReader.readTrees(basePath);
		// normalize trees
		Trees.TreeTransformer<String> treeTransformer = new Trees.StandardTreeNormalizer();
	  List<Tree<String>> normalizedTreeList = new ArrayList<Tree<String>>();
    for (Tree<String> tree : trees) {
      //      System.err.println(tree);
      Tree<String> normalizedTree = treeTransformer.transformTree(tree);
      // System.out.println(Trees.PennTreeRenderer.render(normalizedTree));
      normalizedTreeList.add(normalizedTree);
    }
    return normalizedTreeList;
  }

  
  private static List<Tree<String>> readGENIATrees(String basePath, int low, int high) {
    Collection<Tree<String>> trees = GENIATreebankReader.readTrees(basePath, low, high);
    // normalize trees
    Trees.TreeTransformer<String> treeTransformer = new Trees.StandardTreeNormalizer();
    List<Tree<String>> normalizedTreeList = new ArrayList<Tree<String>>();
    for (Tree<String> tree : trees) {
      Tree<String> normalizedTree = treeTransformer.transformTree(tree);
      // System.out.println(Trees.PennTreeRenderer.render(normalizedTree));
      normalizedTreeList.add(normalizedTree);
    }
    return normalizedTreeList;
  }
  
  private static List<Tree<String>> readBioIETrees(String basePath, int low, int high) {
    Collection<Tree<String>> trees = BioIETreebankReader.readTrees(basePath, low, high);
    // normalize trees
    Trees.TreeTransformer<String> treeTransformer = new Trees.StandardTreeNormalizer();
    List<Tree<String>> normalizedTreeList = new ArrayList<Tree<String>>();
    for (Tree<String> tree : trees) {
      Tree<String> normalizedTree = treeTransformer.transformTree(tree);
      // System.out.println(Trees.PennTreeRenderer.render(normalizedTree));
      normalizedTreeList.add(normalizedTree);
    }
    return normalizedTreeList;
  }

  
  
  /**
   * The main controller for your lives on this lab.
   */
  public static void main(String[] args) {

    // set up default options ..............................................
    Map<String, String> options = new HashMap<String, String>();
    options.put("-path",      "/courses/nlp/lab6/data");
    options.put("-data",      "ptb");
    options.put("-parser",    "usna.parser.BaselineParser");
    options.put("-maxLength", "20");

    // let command-line options supersede defaults .........................
    options.putAll(CommandLineUtils.simpleCommandLineParser(args));
    System.out.println("ParserTester options:");
    for (Map.Entry<String, String> entry: options.entrySet()) {
      System.out.printf("  %-12s: %s%n", entry.getKey(), entry.getValue());
    }
    System.out.println();

    MAX_LENGTH = Integer.parseInt(options.get("-maxLength"));

    Parser parser;
    try {
      Class parserClass = Class.forName(options.get("-parser"));
      parser = (Parser) parserClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    System.out.println("Using parser: " + parser);

    String basePath = options.get("-path");
    String dataSet = options.get("-data");
    if( !basePath.endsWith("/") ) basePath += File.separator;
    String preBasePath = basePath;
    System.out.println("Data will be loaded from: " + basePath + "\n");

    File file = new File(basePath);
    if( !file.isDirectory() && !file.isFile() ) {
    	System.out.println("Bad file path (not found): " + basePath);
    	System.exit(1);
    }
    
    List<Tree<String>> trainTrees = new ArrayList<Tree<String>>(),
    				   validationTrees = new ArrayList<Tree<String>>(),
    				   testTrees = new ArrayList<Tree<String>>();

    if (dataSet.equals("miniTest")) {
      // training data: first 3 of 4 datums
      System.out.println("Loading training trees...");
      trainTrees = readTrees(basePath+dataSet, 1, 3);
      System.out.println("...read " + (trainTrees == null ? 0 : trainTrees.size()) + " trees.");
      System.out.println("done.");

      // test data: last of 4 datums
      System.out.println("Loading test trees...");
      testTrees = readTrees(basePath+dataSet, 4, 4);
      System.out.println("...read " + (testTrees == null ? 0 : testTrees.size()) + " trees.");
      System.out.println("done.");

    }
    
    if (dataSet.equals("genia") || dataSet.equals("combo")) {
      // training data: GENIA Treebank 0-90%
      System.out.println("Loading GENIA training trees... from: "+basePath+"genia");
      trainTrees.addAll(readGENIATrees(basePath+"genia", 0, 440));
      System.out.println("done.");
      System.out.println("Train trees size: "+trainTrees.size());

      System.out.println("First train tree: "+Trees.PennTreeRenderer.render(trainTrees.get(0)));
      System.out.println("Last train tree: "+Trees.PennTreeRenderer.render(trainTrees.get(trainTrees.size()-1)));
      
      // validation data: GENIA Treebank 90-95%
      // System.out.println("Loading validation trees...");
      // validationTrees.add(readGENIATrees(basePath+"genia", 441, 480));
      // System.out.println("Test trees size: "+testTrees.size());
      // System.out.println("done.");

      // test data: GENIA Treebank 95-100%
      System.out.println("Loading GENIA test trees...");
      testTrees.addAll(readGENIATrees(basePath+"genia", 481, 500));
      System.out.println("Test trees size: "+testTrees.size());
      System.out.println("done.");
      
      System.out.println("First train tree: "+Trees.PennTreeRenderer.render(testTrees.get(0)));
      System.out.println("Last train tree: "+Trees.PennTreeRenderer.render(testTrees.get(testTrees.size()-1)));
    }
    
    if (dataSet.equals("bioie") || dataSet.equals("combo")) {
      // training data: BioIE Treebank 0-90%
      System.out.println("Loading BioIE training trees...");
      trainTrees.addAll(readBioIETrees(basePath+"bioie", 0, 580));
      System.out.println("done.");
      System.out.println("Train trees size: "+trainTrees.size());
      
      System.out.println("First train tree: "+Trees.PennTreeRenderer.render(trainTrees.get(0)));
      System.out.println("Last train tree: "+Trees.PennTreeRenderer.render(trainTrees.get(trainTrees.size()-1)));

      // validation data: BioIE Treebank 90-95%
      // System.out.println("Loading validation trees...");
      // validationTrees.addAll(readBioIETrees(basePath+"bioie", 581, 613));
      // System.out.println("Test trees size: "+testTrees.size());
      // System.out.println("done.");

      // test data: BioIE Treebank 95-100%
      System.out.println("Loading BioIE test trees...");
      testTrees.addAll(readBioIETrees(basePath+"bioie", 613, 645));
      System.out.println("Test trees size: "+testTrees.size());
      System.out.println("done.");

      System.out.println("First train tree: "+Trees.PennTreeRenderer.render(testTrees.get(0)));
      System.out.println("Last train tree: "+Trees.PennTreeRenderer.render(testTrees.get(testTrees.size()-1)));
      
    }
    
    // BAD USER INPUT
    if(!dataSet.equals("miniTest") && !dataSet.equals("genia") && !dataSet.equals("bioie") && !dataSet.equals("combo")){
    //  throw new RuntimeException("Bad data set: " + dataSet + ": use miniTest, genia, bioie, or combo (genia and bioie)."); 
    }
    
    if( options.containsKey("-testData") ) {
      System.out.println("Loading " + options.get("-testData") + " test trees ...");
      testTrees.clear();
      testTrees = readTrees(preBasePath + options.get("-testData"));
      System.out.println("Test trees size: "+testTrees.size());
    }

    System.out.println("\nTraining parser...");
    parser.train(trainTrees);
 
    System.out.println("\nTesting parser...");
    testParser(parser, testTrees);
  }
}
