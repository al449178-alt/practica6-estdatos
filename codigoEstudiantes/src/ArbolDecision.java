import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ArbolDecision {
    private BinaryNode<String> raiz;

    // Crea un árbol que solo conoce un animal
    public ArbolDecision(String animal) {
        raiz = new BinaryNode<>(animal);
    }

    // Crea un árbol a partir de una raíz ya construida (lo usan cargar y los tests)
    protected ArbolDecision(BinaryNode<String> raiz) {
        this.raiz = raiz;
    }

    public BinaryNode<String> getRaiz() {
        return raiz;
    }

    // Métodos a implementar

    //Ejercicio1
    public boolean esExtendido() {
        return esExtendidoRecursivo(raiz);
    }

    private boolean esExtendidoRecursivo(BinaryNode<String> r){
        if (r==null){
            return true;
        }
        if(r.isLeaf()){
            return true;
        }
        if(!r.hasLeft()||!r.hasRight()){
            return false;
        }
        return esExtendidoRecursivo(r.left()) && esExtendidoRecursivo(r.right());
    }


    //Ejercicio2
    public int numAnimales() {
        return numAnimalesRecursivo(raiz);
    }

    private int numAnimalesRecursivo(BinaryNode<String> r){
        if (r==null){
            return 0;
        }
        if (r.isLeaf()){
            return 1;
        }
        return numAnimalesRecursivo(r.right())+numAnimalesRecursivo(r.left());
    }

    //Ejercicio3
    public List<String> animales() {
        List<String> orden = new ArrayList<>();
        animalesRecursivo(orden, raiz);
        return orden;
    }

    private void animalesRecursivo(List<String> orden, BinaryNode<String> r){
        if (r==null) return;
        if (r.isLeaf()){
            orden.add(r.getData());
            return;
        }
        animalesRecursivo(orden, r.left());
        animalesRecursivo(orden, r.right());
    }


    //Ejercicio 4
    public List<String> camino (String elem) {
        if (elem==null) throw new IllegalArgumentException();
        List<String> recorrido = new ArrayList<>();
        caminoRecursivo(raiz, elem, recorrido);
        return recorrido;
    }

    private boolean caminoRecursivo(BinaryNode<String> r, String elem, List<String> recorrido){
        if (r==null) return false;
        recorrido.add(r.getData());

        if (elem.equals(r.getData())){
            return true;
        }

        if (caminoRecursivo(r.left(), elem, recorrido)) return true;
        if(caminoRecursivo(r.right(), elem, recorrido)) return true;

        recorrido.remove(recorrido.size()-1);
        return false;
    }


    //Ejercicio 5

    public void aprender (BinaryNode<String> hoja, String animalNew, String pregunta, boolean respuestaNuevo) {
        if (hoja == null || !hoja.isLeaf()) {
            throw new IllegalArgumentException();
        }
        if (animalNew == null || animalNew.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (pregunta == null || pregunta.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (animales().contains(animalNew)) {
            throw new IllegalArgumentException();
        }
        String animalAnt = hoja.getData();
        hoja.setData(pregunta);

        if (respuestaNuevo) {
            hoja.setLeft(new BinaryNode<>(animalNew));
            hoja.setRight(new BinaryNode<>(animalAnt));
        } else {
            hoja.setLeft(new BinaryNode<>(animalAnt));
            hoja.setRight(new BinaryNode<>(animalNew));
        }
    }

    //Ejercicio 6
    public List<String> describir(String animal) {
        List<String> recorrido = new ArrayList<>();
        if (animal!=null){
            describirRecursivo(raiz, animal, recorrido);
        }
        return recorrido;
    }

    private boolean describirRecursivo(BinaryNode<String> r, String animal, List<String> recorrido){
        if (r == null) {
            return false;
        }
        if (r.isLeaf()) {
            return animal.equals(r.getData());
        }

        recorrido.add(r.getData() + " Sí");
        if (describirRecursivo(r.left(), animal, recorrido)) {
            return true;
        }
        recorrido.remove(recorrido.size() - 1);

        recorrido.add(r.getData() + " No");
        if (describirRecursivo(r.right(), animal, recorrido)) {
            return true;
        }
        recorrido.remove(recorrido.size() - 1);
        return false;
    }

    //Ejercicio 7
    public String preguntaDiferenciadora(String animal1, String animal2) {
        if (animal1 == null || animal2 == null || animal1.equals(animal2)) {
            return null;
        }
        List<String> listaAnimales = animales();
        if (!listaAnimales.contains(animal1) || !listaAnimales.contains(animal2)) {
            return null;
        }

        List<String> camino1 = camino(animal1);
        List<String> camino2 = camino(animal2);
        String pregunta = null;
        int i = 0;
        while (i < camino1.size() && i < camino2.size() && camino1.get(i).equals(camino2.get(i))) {
            pregunta = camino1.get(i);
            i++;
        }
        return pregunta;
    }


    public void guardar(String nombreFichero) throws IOException {
        if (raiz == null) {
            throw new IllegalStateException("No se puede guardar un árbol vacío");
        }
        try (PrintWriter out = new PrintWriter(nombreFichero, StandardCharsets.UTF_8.name())) {
            guardar(raiz, out);
            if (out.checkError()) {
                throw new IOException("Error al escribir en " + nombreFichero);
            }
        }
    }

    //Ejercicio 8
    private void guardar(BinaryNode<String> r, PrintWriter out) {
        if (r == null) {
            return;
        }
        if (r.isLeaf()) {
            out.println("A:" + r.getData());
        } else {
            out.println("P:" + r.getData());
            guardar(r.left(), out);
            guardar(r.right(), out);
        }
    }
    // ---------- cargar ----------

    public static ArbolDecision cargar(String nombreFichero) throws IOException {
        try (Scanner sc = new Scanner(new File(nombreFichero), StandardCharsets.UTF_8.name())) {
            if (!sc.hasNextLine()) {
                throw new IOException("El fichero está vacío");
            }
            BinaryNode<String> raiz = leerNodo(sc);
            while (sc.hasNextLine()) {
                if (!sc.nextLine().trim().isEmpty()) {
                    throw new IOException("Sobran líneas después de completar el árbol");
                }
            }
            return new ArbolDecision(raiz);
        }
    }

    // Lee un subárbol completo: una hoja, o una pregunta seguida de sus dos subárboles
    private static BinaryNode<String> leerNodo(Scanner sc) throws IOException {
        if (!sc.hasNextLine()) {
            throw new IOException("El fichero termina antes de completar el árbol");
        }
        String linea = sc.nextLine();
        if (linea.startsWith("A:")) {
            return new BinaryNode<>(linea.substring(2));
        }
        if (linea.startsWith("P:")) {
            BinaryNode<String> nodo = new BinaryNode<>(linea.substring(2));
            nodo.setLeft(leerNodo(sc));
            nodo.setRight(leerNodo(sc));
            return nodo;
        }
        throw new IOException("Línea con formato incorrecto: " + linea);
    }
}
