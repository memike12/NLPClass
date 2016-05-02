package usna.ling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import usna.util.Filter;


/**
 * Class to hold static functions.
 * This code can binarize and debinarize trees for a parser's use.
 */
public class TreeAnnotations {

	/**
	 * Given a tree, returns a new tree that is a lossless binarization version of it.
	 * @param tree A valid tree.
	 * @return An equivalent tree that has been binarized.
	 */
  public static Tree<String> binarizeTree(Tree<String> tree) {
    String label = tree.getLabel();
    if (tree.isLeaf())
      return new Tree<String>(label);
    if (tree.getChildren().size() == 1) {
      return new Tree<String>
        (label, 
         Collections.singletonList(binarizeTree(tree.getChildren().get(0))));
    }
    // otherwise, it's a binary-or-more local tree, 
    // so decompose it into a sequence of binary and unary trees.
    String intermediateLabel = "@"+label+"->";
    Tree<String> intermediateTree =
      binarizeTreeHelper(tree, 0, intermediateLabel);
    return new Tree<String>(label, intermediateTree.getChildren());
  }

  private static Tree<String> binarizeTreeHelper(Tree<String> tree,
                                                 int numChildrenGenerated, 
                                                 String intermediateLabel) {
    Tree<String> leftTree = tree.getChildren().get(numChildrenGenerated);
    List<Tree<String>> children = new ArrayList<Tree<String>>();
    children.add(binarizeTree(leftTree));
    if (numChildrenGenerated < tree.getChildren().size() - 1) {
      Tree<String> rightTree = 
        binarizeTreeHelper(tree, numChildrenGenerated + 1, 
                           intermediateLabel + "_" + leftTree.getLabel());
      children.add(rightTree);
    }
    return new Tree<String>(intermediateLabel, children);
  } 
 
  public static Tree<String> unBinarizeTree(Tree<String> annotatedTree) {

    // Remove intermediate nodes (labels beginning with "@"
    // Remove all material on node labels which follow their base symbol 
    // (cuts at the leftmost -, ^, or : character)
    // Examples: a node with label @NP->DT_JJ will be spliced out, 
    // and a node with label NP^S will be reduced to NP

    Tree<String> debinarizedTree =
      Trees.spliceNodes(annotatedTree, new Filter<String>() {
          public boolean accept(String s) {
            return s.startsWith("@");
          }
        });
    Tree<String> unAnnotatedTree = 
      (new Trees.FunctionNodeStripper()).transformTree(debinarizedTree);
    return unAnnotatedTree;
  }
}