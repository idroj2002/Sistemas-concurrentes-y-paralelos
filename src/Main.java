import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    public static File getNextFile() {
        return filesList.poll();
    }

    public static void main(String[] args) {
        totalFiles = 0;
        filesList = new ConcurrentLinkedDeque<File>();
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
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < N; i++) {
            ProcesarFicherosCon task = new ProcesarFicherosCon();
            Thread t = Thread.startVirtualThread(task);
            threads.add(t);
        }
        for (int i = 0; i < N; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                System.out.println(e.getStackTrace());
            }
        }
    }

    public static void MostrarResultados() {

    }
}