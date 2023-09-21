import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Main {
    // Número de hilos
    private static int N = 10;

    // Path al directorio de entrada que contiene los ficheros que se van a prcoesar.
    private static String path;

    // Extensión de los ficheros a procesar
    private static String extension;

    // Lista de donde se guardan los ficheros a procesar
    private static ConcurrentLinkedDeque<File> filesList;

    private static int totalFiles;
    private static Map<Character,Integer> resultsMap = new TreeMap<>();
    private static ArrayList<ProcesarFicherosCon> runnables;
    private static ArrayList<Thread> threads;

    public static File getNextFile() {
        return filesList.poll();
    }

    public static void main(String[] args) {
        totalFiles = 0;
        filesList = new ConcurrentLinkedDeque<File>();
        runnables = new ArrayList<ProcesarFicherosCon>();
        threads = new ArrayList<Thread>();
        path = "./Input";
        extension = ".txt";
        if (args.length>0)
            path = args[0];

        ProcesarDirectorioRecursivo(path);

        ProcesarFicheros();

        MostrarResultados();
    }

    // Procesamiento recursivo del directorio para buscar los ficheros de texto, almacenandolo en la lista fileList
    public static void ProcesarDirectorioRecursivo(String dirpath) {
        File file=new File(dirpath);
        File content[] = file.listFiles();
        if (content != null) {
            for (int i = 0; i < content.length; i++) {
                if (content[i].isDirectory()) {
                    // Si es un directorio, procesarlo recursivamente.
                    ProcesarDirectorioRecursivo(content[i].getAbsolutePath());
                }
                else {
                    // Si es un fichero de texto, añadirlo a la lista para su posterior procesamiento.
                    if (checkFile(content[i].getName())){
                        filesList.add(content[i]);
                        System.out.printf("%3dth Fichero de texto encontrdo: %s\n", ++totalFiles, content[i].getAbsolutePath());
                    }
                }
            }
        }
        else
            System.err.printf("Directorio %s no existe.\n",file.getAbsolutePath());
    }

    public static boolean checkFile(String name) {
        return name.endsWith(extension);
    }

    public static void ProcesarFicheros() {
        for (int i = 0; i < N; i++) {
            ProcesarFicherosCon task = new ProcesarFicherosCon();
            Thread t = Thread.startVirtualThread(task);
            runnables.add(task);
            threads.add(t);
        }
    }

    public static void MostrarResultados() {
        for (int i = 0; i < N; i++) {
            try {
                ProcesarFicherosCon t = runnables.get(i);
                threads.get(i).join();
                Map<Character,Integer> threadMap = t.getResultsMap();
                for (Map.Entry<Character,Integer> e : threadMap.entrySet()) {
                    Integer value = resultsMap.putIfAbsent(e.getKey(), e.getValue());
                    if (value != null) {
                        // Si existe, incrementar el número de ocurrencias para este carácter.
                        resultsMap.put(e.getKey(), value + e.getValue());
                    }
                }

                for (Map.Entry<Character,Integer> e : resultsMap.entrySet()) {
                    System.out.println("Carácter " + e.getKey() + " -> número de ocurrencias: " + e.getValue());
                }
            } catch (InterruptedException e) {
                System.out.println(e.getStackTrace());
            }
        }


    }
}