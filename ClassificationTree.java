//Name : Rishabh Singh
//Class : CSE 123
//Date : 11/22/24
//Project : P3
//TA : Kavya Nair
import java.util.*;
import java.io.*;

public class ClassificationTree extends Classifier {

    private ClassificationNode overallRoot;

    // Behavior: Constructs a ClassificationTree by loading from the provided Scanner.
    // Exceptions: None.
    // Returns: None.
    // Parameters: sc - the Scanner connected to the input file containing the 
    //             serialized tree data; should not be null. Format of sc should match
    //             the save method within Classifier Class. 
    public ClassificationTree(Scanner sc) {

        overallRoot = treeBuilder(sc);
        
    }

    // Behavior: Constructs a ClassificationTree from the provided data and corresponding labels. Uses data
    //           to train the tree and build a model. 
    // Exceptions: Throws IllegalArgumentException if the provided lists are null, empty, or of different sizes.
    // Returns: None.
    // Parameters:
    //   data - the list of Classifiable objects; should not be null or empty.
    //   results - the list of corresponding labels; should not be null or empty.
    
    public ClassificationTree(List<Classifiable> data, List<String> results) {

        if(data.size() != results.size()) {
            throw new IllegalArgumentException();
        }
        else if(data.isEmpty() || results.isEmpty()) {
            throw new IllegalArgumentException();
        }
        overallRoot = new ClassificationNode(results.get(0), data.get(0));
        for(int i = 1; i < data.size(); i++) {
            overallRoot = treeBuilderComplex(overallRoot, data, results, i);
        }
    }

    // Behavior: Recursively builds the classification tree from the provided Scanner input.
    // Exceptions: None.
    // Returns: The root of the constructed ClassificationNode subtree.
    // Parameters: sc - the Scanner to read the tree data from; 
    //             should not be null.
    
    private ClassificationNode treeBuilder(Scanner sc) {
        if(sc.hasNextLine()) {
            String line = sc.nextLine();

            if(line.startsWith("Feature")) {
                String feature = line.substring("Feature: ".length(), line.length());
                line = sc.nextLine();
                if(line.startsWith("Threshold: ")) { 
                    double threshold = Double.parseDouble(line.substring("Threshold: ".length(), line.length()));
                    ClassificationNode root = new ClassificationNode(new Split(feature, threshold));
                
                    root.left = treeBuilder(sc);
                    root.right = treeBuilder(sc);
                    return root;
                }

            }
            else {
                return new ClassificationNode(line.trim());
            }

        }
        return null;
    }

    // Behavior: Recursively builds or updates the classification tree by adding the data point at the given index.
    // Exceptions: None.
    // Returns: The updated ClassificationNode representing the root of the subtree.
    // Parameters:
    //   root - the current root of the subtree;
    //   data - the list of Classifiable objects; should not be null.
    //   results - the list of corresponding labels; should not be null.
    //   index - the index of the data point to add; should be valid within the lists.
    
    private ClassificationNode treeBuilderComplex(ClassificationNode root, List<Classifiable> data,
        List<String> results, int index) {

        if (root == null) {
            return new ClassificationNode(results.get(index), data.get(index));
        }

        if (root.left == null && root.right == null) {
            if (root.label.equals(results.get(index))) {
                return root;
            } else {
                Split newSplit = root.oldInput.partition(data.get(index));
                ClassificationNode newRoot = new ClassificationNode(newSplit);

                if (newSplit.evaluate(root.oldInput)) {
                    newRoot.left = new ClassificationNode(root.label, root.oldInput);
                    newRoot.right = new ClassificationNode(results.get(index), data.get(index));
                } else {
                    newRoot.right = new ClassificationNode(root.label, root.oldInput);
                    newRoot.left = new ClassificationNode(results.get(index), data.get(index));
                }

                return newRoot; 
            }
        } else {
            if (root.split.evaluate(data.get(index))) {
                root.left = treeBuilderComplex(root.left, data, results, index);
            } else {
                root.right = treeBuilderComplex(root.right, data, results, index);
            }
            return root; 
    }
}


    // Behavior: Returns whether or not the classifier is able to classify 
    //           datapoints that match that of the provided 'input'
    // Exceptions: None
    // Returns: a boolean representing if the classifier can classify the input
    // Parameters: input - the classifiable object, which should be non-null
    public boolean canClassify(Classifiable input){
        return canClassifyHelper(overallRoot, input);
        }
    
    private boolean canClassifyHelper(ClassificationNode root, Classifiable input) {
        if(root == null) {
            return true;
        }
        if(root.split!=null) {
            if(!input.getFeatures().contains(root.split.getFeature())) {
                return false;
            }
        }
        else {
            return true;
        }
        return canClassifyHelper(root.left, input) && canClassifyHelper(root.right, input);
    }

    // Behavior: Classifies the provided 'input', returning the associated 
    //           learned label
    // Exceptions: IllegalArgumentException if the provided input can't be classified
    // Returns: a String representing the learned label
    // Parameters: input - the classifiable object, which should be non-null
    public String classify(Classifiable input){
        if(!canClassify(input)) {
            throw new IllegalArgumentException();
        }
        return classifyHelper(overallRoot, input);
        }

    // Behavior : Helper method for classify: recursively traverse the tree to classify the input
    // Exceptions : None.
    // Returns : the predicted label for the input.
    // Parameters : input - the classifiable object, which should not be null, 
    //              root - current root of the tree while traversing.
    private String classifyHelper(ClassificationNode root, Classifiable input) {
        if(root != null) {
            if(root.split == null) {
                return root.label;
            }
            String ans = "";
            if(root.split.evaluate(input)) {
                ans = classifyHelper(root.left, input);
            }
            else {
                ans = classifyHelper(root.right, input);
            }
            return ans;
        }
        return null;
    }

    // Behavior: Saves this classifier to the provided PrintStream 'ps'
    // Exceptions: None
    // Returns: None
    // Parameters: ps - the PrintStream to save the classifier to, which should be non-null
    public void save(PrintStream ps) {
        saveHelper(ps, overallRoot);
    }

    // Behavior: Helper method for save: recursively writes the tree to the Print Stream provided.
    //           Using a pre-order traversal.
    // Exceptions: None.
    // Returns: None.
    // Parameters: ps - the PrintStream to save the classifier to, should not be null
    //             root - current root of tree used to traverse and print. 

    private void saveHelper(PrintStream ps, ClassificationNode root) {
        if(root != null) {
            if(root.left == null && root.right == null) {
                ps.println(root.label);
            }
            else {
                ps.println(root.split.toString());
                saveHelper(ps, root.left);
                saveHelper(ps, root.right);
            }
        }
    }
    
    //This class represnets a node in the classification tree, which can either be an internal
    //an internal node(split) or a leaf node(label, oldInput).

    private static class ClassificationNode {
        public String label;
        public Split split;
        public Classifiable oldInput;
        public ClassificationNode left;
        public ClassificationNode right;

        // Behavior: Constructs an internal node with the given split.
        // Exceptions: None.
        // Returns: None.
        // Parameters:
        //   s - the Split object representing the feature and threshold.
        public ClassificationNode(Split s) {
            this.split = s;
        }
    
        // Behavior: Constructs a leaf node with the given label and old input.
        // Exceptions: None.
        // Returns: None.
        // Parameters:
        //   label - the label for this leaf node.
        //   c - the Classifiable object associated with this label.
        
        public ClassificationNode(String label, Classifiable c) {
            this.oldInput = c;
            this.label = label;
        }

        // Behavior: Constructs a leaf node with the given label.
        // Exceptions: None.
        // Returns: None.
        // Parameters:
        //   label - the label for this leaf node.

        public ClassificationNode(String label) {
            this(label, null);
        }
    }
}