import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class lab6 {
    /* Creates the Cache objects and places them in a list to iterate through each with ease */
    private static List<Cache> initCaches(){
        List<Cache> caches = new ArrayList<>();
        /* Direct Mapped 1 word*/
        Cache one = new Cache(2048, 1, 1);
        caches.add(one);
        Cache two = new Cache(2048, 2, 1);
        caches.add(two);
        Cache three = new Cache(2048, 4, 1);
        caches.add(three);
        Cache four = new Cache(2048, 1, 2);
        caches.add(four);
        Cache five = new Cache(2048, 1, 4);
        caches.add(five);
        Cache six = new Cache(2048, 4, 4);
        caches.add(six);
        /* Direct Mapped 1 word*/
        Cache seven = new Cache(4096, 1, 1);
        caches.add(seven);

        return caches;
    }

    public static void main(String[] args){
        /* List of caches */
        List<Cache> caches = initCaches();

        /* Initialize the array for each cache */
        for (int i = 0; i < caches.size(); i++){
            caches.get(i).initializeCache();
        }

        /* Try opening the file */
        try{
            /* Access our Mem Stream */
            Scanner file = new Scanner(new File(args[0]));

            /* Iterate through Mem Stream*/
            while(file.hasNextInt()){
                /* Skip integer at each line */
                file.nextInt();
                /* Radix is set to 16 to convert the number from hexadecimal */
                int address = file.nextInt(16)/4; /* Divide by 4. 4 bytes in 1 word */

                /* Update each cache */
                for (int i = 0; i < caches.size(); i++){
                    caches.get(i).compute(address);
                }
            }

            /* Print out all our information */
            for (int i = 0; i < caches.size(); i++){
                int cacheNum = i+1;
                System.out.println("Cache #" + cacheNum);
                caches.get(i).printStatistics();
                System.out.println("---------------------------");
            }

        }
        /* File Not Found */
        catch(FileNotFoundException e){
            System.err.println("File Not Found");
        }

    }
}
