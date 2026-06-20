import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * AVLMap es una implementación de maps de tipo K->V con
 * árboles binarios de búsqueda balanceados.
 * Un AVLMap típico es {(k1, v1),..., (kn, vn)}.
 *
 * AVLMap requiere que el tipo de la clave K implemente la
 * interfaz Comparable.
 *
 * Los métodos usan compareTo para determinar la igualdad de
 * las claves.
 */
public class AVLMap<K extends Comparable<? super K>, V> implements SortedMap<K, V> {
    private TreeNode root;
    private int size;

    public AVLMap(){
        this.root = null;
        this.size = 0;
    }

    //Inner class: TreeNode
    private class TreeNode {
        private K key;
        private V value;
        private int height;
        private TreeNode left;
        private TreeNode right;

        public TreeNode(K key, V value, int height) {
            this.key = key;
            this.value = value;
            this.height = height;
        }
    }

    /**
     * @post Asocia la clave k con el valor v en el map. Sobreescribe el
     *   valor asociado con k si k está presente en el map.
     */
    public void put(K k, V v) {
        this.root = put(this.root, k, v);
    }

    private TreeNode put (TreeNode x, K key, V value){
        if (x == null) {
            this.size++;
            return new TreeNode(key, value, 0);
        }

        int cmp = key.compareTo(x.key);
        if (cmp < 0) 
            x.left = put(x.left, key, value);
        else if (cmp > 0) 
            x.right = put(x.right, key, value);
        else 
            x.value = value;

        return balance(x);
    }



    /**
     * @post Returns the cardinality of 'this'.
     *   More formally, it satisfies: #this.
     */   
    public int size() {
        return this.size;
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
     * @post Elimina la clave k y su valor asociado del map.
     *   Retorna el valor previo de k en el map, o null si k
     *   no está en el map.
     */
    public V removeKey(K k) {
        if (!containsKey(k)) {
            return null;
        }
        V value = get(k);
        this.root = removeKey(this.root, k);
        return value;
    }

    private TreeNode removeKey(TreeNode x, K key) {
        if (x == null) 
            return null;
            
        int cmp = key.compareTo(x.key);
        
        if (cmp < 0) 
            x.left = removeKey(x.left, key);
        else if (cmp > 0) 
            x.right = removeKey(x.right, key);
        else {
            if (x.right == null) {
                this.size--; 
                return x.left; 
            }
            if (x.left == null) {
                this.size--; 
                return x.right; 
            }

            TreeNode min = min(x.right);
            x.key = min.key;
            x.value = min.value;
            x.right = removeMin(x.right);
        }
        updateHeight(x);
        return balance(x);
    }

    /**
     * @post Retorna si el map contiene una entrada para key.
     */
    public boolean containsKey(K key){
        return get(root, key) != null;
    }

    /**
     * @post Retorna el valor asociado a la clave k en el map.
     *   Retorna null si k no está en el map.
     */
    public V get(K k) {
        return get(this.root, k);
    }

    private V get(TreeNode x, K key){
        if (x == null) 
            return null;

        int cmp = key.compareTo(x.key);
        if (cmp < 0) 
            return get(x.left, key);
        else if (cmp > 0) 
            return get(x.right, key);
        else 
            return x.value;
    }

    /**
     * @post Retorna las claves del map en un conjunto.
     */
    public SortedSet<K> keySet(){
        AVLSet<K> keys = new AVLSet<>();
        java.util.Iterator<Entry> it = inOrder().iterator();
        while (it.hasNext()) {
            keys.add(it.next().key);
        }
        return keys;
    }


    /**
     * @post Retorna true si y sólo si el map está vacío.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * @pre !isEmpty()
     * @post Retorna el elemento más pequeño del map.
     */
    public K min() {
        if (isEmpty())
            throw new NoSuchElementException("There is no minimum key in an empty map.");
        TreeNode x = min(this.root);
        return x.key;
    }

    private TreeNode min(TreeNode x) {
        while (x.left != null)
            x = x.left;
        return x;
    }

    /**
     * @pre !isEmpty()
     * @post Elimina el par con la clave más pequeña del map.
     */
    public void removeMin() {
        if (isEmpty()) 
            throw new NoSuchElementException("Can't remove minimum element from an empty map.");
        this.root = removeMin(this.root);
        this.size--;
    }

    private TreeNode removeMin(TreeNode x) {
        if (x.left == null) 
            return x.right;
        x.left = removeMin(x.left);
        updateHeight(x);
        return balance(x);
    }


    /**
     * @post Returns a string representation of the set. Implements
     *   the abstraction function. It represents the set by showing
     *   its elements in increasing order "{o1, o2,..., on}".
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("{");
        boolean first = true;
        for (Entry e : inOrder()) {
            if (!first) res.append(", ");
            res.append(e.key).append("->").append(e.value);
            first = false;
        }
        res.append("}");
        return res.toString();
    }


    public List<Entry> inOrder() {
        if (root == null) 
            return new LinkedList<>();
        return inOrder(root);
    }

    private List<Entry> inOrder(TreeNode node) {
        List<Entry> result = new LinkedList<>();
        
        if (node.left != null) 
            result.addAll(inOrder(node.left));

        result.add(new Entry(node.key, node.value));

        if (node.right != null) 
            result.addAll(inOrder(node.right));

        return result;
    }

    //Inner class: Entry
    private class Entry {
        private K key;
        private V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
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
