import java.util.Iterator;
/**
 * Clase para almacenar conjuntos de palabras, y contar la cantidad
 * de veces que cada palabra fue observada.
*/
public class WordCounter
{
    private SortedMap<String, Integer> wordMap;

    /**
     * @post Crea un WordCounter
     */
    public WordCounter() {
        this.wordMap = new AVLMap<>();
    }
    
    /**
     * @post Almacena el conjunto de palabras input, incrementando 
     *   por cada palabra su cuenta en 1.
     */
    public void addWords(SortedSet<String> input) {
        for (String word : input) {
            if (wordMap.containsKey(word)) {
                // Si la palabra ya existe, incrementamos su valor en 1
                int currentCount = wordMap.get(word);
                wordMap.put(word, currentCount + 1);
            } else {
                // Si es la primera vez que la vemos, la agregamos con valor 1
                wordMap.put(word, 1);
            }
        }
    }
    
    /**
     * @post Retorna todas las palabras almacenadas como un 
     *   conjunto
     */
    public SortedSet<String> getAllWords() {
        return wordMap.keySet();
    }
    
    /**
     * @post Retorna la cantidad de veces que una palabra fue
     *   ingresada
     */
    public int getRepetitions(String word) {
    Integer count = wordMap.get(word);
        if (count == null) {
            return 0; 
        }
        return count;
    }
    
    /**
     * @post Retorna verdadero si y solo si word pertenece al
     *   conjunto de palabras que fueron observadas
     */
    public boolean belongsToSet(String word) {
        return wordMap.containsKey(word);
    }
}
