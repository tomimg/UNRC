import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * AVLSet is an implementation of unbounded sets of 
 * objects of type T, based on AVLs. 
 * A typical AVLSet is {o1, . . . , on}.
 * 
 * AVLSet requires that the key type T implements the 
 * Comparable interface. AVLSet calls the compareTo method 
 * to compare two keys in many of the operations.
 * 
 * The methods use compareTo to determine equality of elements.
 */
public class AVLSet<T extends Comparable<? super T>> implements SortedSet<T>
{
    private TreeNode root;
    private int size;

    /**
     * @post Creates an empty set.
     *   More formally, it satisfies: this = {}.
     */
    public AVLSet() {
        this.root = null;
        this.size = 0;
    }

    // Inner class: TreeNode
    private class TreeNode {
        private T key; // the key
        private int height; // height of the subtree
        private TreeNode left; // left subtree
        private TreeNode right; // right subtree

        public TreeNode(T key, int height) {
            this.key = key;
            this.height = height;
        }

        public T getKey(){
            return this.key;
        }
    }
    
    /**
     * @post Returns true if 'this' is the empty set.
     *   More formally, it satisfies: return = (this = {}).
     */
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    /**
     * @post Adds 'key' to the elements of 'this'. 
     *   Returns true iff 'key' was added. The
     *   tree is rebalanced after the insertion.
     *   More formally, it satisfies: 
     *      result = !(key in old(this)) && 
     *        this = old(this) U {key}.
     */
    public boolean add(T key) {
        // if the set already contains the key, return false
        if (contains(key)) {
            return false;
        }
        // otherwise, add the key to the set and increment size
        root = add(root, key);
        size++;
        return true;
    }

    
    private TreeNode add(TreeNode x, T key) {
        // if the TreeNode is null, create a new TreeNode with the key
        if (x == null)
            return new TreeNode(key, 0);
        int cmp = key.compareTo(x.key);
        // compare the key with the TreeNode's key
        if (cmp < 0)
            x.left = add(x.left, key);
        else if (cmp > 0)
            x.right = add(x.right, key);
        else
            assert false; // Should not happen if the key is not in the set
        updateHeight(x);
        return balance(x);
    }


    
    /**
     * @post Returns the height of the AVL tree. 
     * It is assumed that the height of an empty tree is -1 
     * and the height of a tree with just one node is 0.
     */
    public int height() {
        return height(this.root);
    }

    private int height(TreeNode x){
        if (x == null)
            return 0;
        return x.height;
    }

    
    /**
     * @post Removes 'x' from 'this'. Returns
     *   true iff 'x' was removed.  The
     *   tree is rebalanced after the removal.
     *   More formally, it satisfies: 
     *      result = (e in old(this)) && this = old(this) \ {e}.
     */
    public boolean remove(T key) {
        if (!contains(key)) {
            return false;
        }
        root = remove(root, key);
        size--;
        return true;
    }

    private TreeNode remove(TreeNode x, T key){
        int cmp = key.compareTo(x.key);
        if (cmp < 0)
            x.left = remove(x.left, key);
        else if (cmp > 0)
            x.right = remove(x.right, key);
        else {
            if (x.left == null)
                return x.right;
            if (x.right == null)
                return x.left;
            T tempKey = min(x.right);
            x.key = tempKey;
            x.right = removeMin(x.right);
        }
        updateHeight(x);
        return balance(x);
    }
   
    /**
     * @pre !isEmpty()
     * @post Returns the smallest element of 'this'.
     */   
    public T min() {
        if (isEmpty())
            throw new NoSuchElementException("There is no minimum in an empty set.");
        return min(this.root);
    }

    private T min (TreeNode x){
        while (x.left != null)
            x = x.left;
        return x.getKey();
    }
    
    /**
     * @pre !isEmpty()
     * @post Returns the largest element of 'this'.
     */   
    public T max() {
        if (isEmpty())
            throw new NoSuchElementException("There is no maximum in an empty set.");
        return max(this.root);
    }

    private T max (TreeNode x){
        while (x.right != null)
            x = x.right;
        return x.getKey();
    }
    
    /**
     * @post Returns true iff 'key' is in 'this'.
     */  
    public boolean contains(T key) {
        return get(root, key) != null;
    }

    private TreeNode get(TreeNode x, T key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0)
            return get(x.left, key);
        else if (cmp > 0)
            return get(x.right, key);
        else
            return x;
    }

    /**
     * @post Returns the cardinality of 'this'.
     *   More formally, it satisfies: #this.
     */   
    public int size() {
        return this.size;
    }

    /**
     * @pre !isEmpty()
     * @post Deletes the smallest element of 'this'. The
     *   tree is rebalanced after the removal.
     */   
    public void removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Set is empty");
        }
        root = removeMin(root);
        size--;
    }

    private TreeNode removeMin(TreeNode x){
        if (x.left == null) {
            return x.right;
        }
        x.left = removeMin(x.left);
        updateHeight(x);
        return balance(x);
    }
    
    /**
     * @pre !isEmpty()
     * @post Deletes the largest element of 'this'. The
     *   tree is rebalanced after the removal.
     */
    public void removeMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Set is empty");
        }
        root = removeMax(root);
        size--;
    }

    private TreeNode removeMax(TreeNode x){
        if (x.right == null) {
            return x.left;
        }
        x.right = removeMax(x.right);
        updateHeight(x);
        return balance(x);
    }        
    
    /**
     * @post Returns a list containing all the keys of the tree, where
     *   nodes are visited in preorder.
     */    
    public List<T> preorder() {
        if (this.root == null)
            return new LinkedList<>();
        return preOrder(this.root);
    }

    private List<T> preOrder(TreeNode root) {
        List<T> result = new LinkedList<T>();

        result.add(root.key);
        if (root.left != null)
            result.addAll(preOrder(root.left));
        if (root.right != null)
            result.addAll(preOrder(root.right));

        return result;
    }
    
    /**
     * @post Returns a list containing all the keys of the tree, where
     *   nodes are visited in inorder.
     */    
    public List<T> inorder() {
        if (this.root == null)
            return new LinkedList<>();
        return inOrder(this.root);
    }
    
    private List<T> inOrder(TreeNode root) {
        List<T> result = new LinkedList<T>();

        if (root.left != null)
            result.addAll(inOrder(root.left));
        result.add(root.key);
        if (root.right != null)
            result.addAll(inOrder(root.right));

        return result;
    }

    /**
     * @post Returns a string representation of the set. Implements
     *   the abstraction function. It represents the set by showing
     *   its elements in increasing order "{o1, o2,..., on}".
     */
    @Override
    public String toString() {
        String res = "{";
        boolean first = true;
        List<T> elems = inorder();
        for (T item : elems) {
            if (!first) {
                res += ", ";
            }
            res += item.toString();
            first = false;
        }
        res += "}";
        return res;
    }



    /**
     * @post Returns true if and only if the structure is a 
     *   valid AVL.
    */
    public boolean repOK() {
        return isBST(root, null, null) && isAVL();
    }

    private boolean isBST(TreeNode x, T min, T max){
        if (x == null)
            return true;
        if (min != null && x.key.compareTo(min) <= 0)
            return false;
        if (max != null && x.key.compareTo(max) >= 0)
            return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    }

    public boolean isAVL(){
        return isAVL(root);
    }

    private boolean isAVL(TreeNode x){
        if (x == null)
            return true;
        int bf = balanceFactor(x);
        if (bf > 1 || bf < -1)
            return false;
        return isAVL(x.left) && isAVL(x.right);
    }

    /**
     * @post Returns an iterator that iterates through 
     *   the elements in the set in sorted order.
     */
    public Iterator<T> iterator() {
        return inorder().iterator();
    }

    @Override
    public boolean equals(Object other) {

    if (this == other) {
        return true;
    }
    if (other == null || !(other instanceof AVLSet)) {
        return false;
    }

    // SOLUCIÓN: Casteamos explícitamente a AVLSet<T>
    // y silenciamos la advertencia del compilador.
    @SuppressWarnings("unchecked")
    AVLSet<T> that = (AVLSet<T>) other;

    if (this.size() != that.size()) {
        return false;
    }

    for (T elem : this) {
        // Ahora el compilador sabe que 'that' recibe elementos de tipo T
        if (!that.contains(elem)) {
            return false;
        }
    }

    return true;
    }


    private void updateHeight(TreeNode x) {
        x.height = 1 + Math.max(height(x.left), height(x.right));
    }

    // Balancer and Rotations
    private int balanceFactor(TreeNode x) { 
        return height(x.left) - height(x.right);
    }

    private TreeNode balance(TreeNode x) {
        int balance = balanceFactor(x);

        // LL
        if (balance > 1 && balanceFactor(x.left) >= 0) 
            return rotateRight(x);
        // RR
        if (balance < -1 && balanceFactor(x.right) <= 0) 
            return rotateLeft(x);
        // LR
        if (balance > 1 && balanceFactor(x.left) < 0) {
            x.left = rotateLeft(x.left);
            return rotateRight(x);
        }
        // RL
        if (balance < -1 && balanceFactor(x.right) > 0) {
            x.right = rotateRight(x.right);
            return rotateLeft(x);
        }

        return x;
    }

    private TreeNode rotateLeft(TreeNode x) {
        TreeNode y = x.right;
        x.right = y.left;
        y.left = x;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    private TreeNode rotateRight(TreeNode x) {
        TreeNode y = x.left;
        x.left = y.right;
        y.right = x;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

}
