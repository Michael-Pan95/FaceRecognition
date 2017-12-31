package Database;

import java.util.ArrayList;

/**
 *
 * @author Michael Pan (zpan1@andrew.cmu.edu);
 */
public final class Helper {
    //create a array of arraylist. Just to simplify my coding work

    public static ArrayList[] ArrayListCreator(int num) {
        ArrayList[] result = new ArrayList[num];
        for (int i = 0; i < num; i++) {
            result[i] = new ArrayList();
        }
        return result;
    }

    //display any ArrayList Array
    public static void display(ArrayList[] result) {
        //show the data
        if(result.length ==5){
            System.out.printf("%-5s%-15s%-15s%-15s%-15s%-15s%n","No.","Last Name","First Name","Position","Salary","Age");
        }
        for (int i = 0; i < result[0].size(); i++) {
            for (int j = 0; j < result.length; j++) {
                if (j == 0) {
                    System.out.printf("%-5d", i + 1);
                }
                System.out.printf("%-15s", result[j].get(i));
            }
            System.out.print("\n");
        }
        System.out.println();
    }
}
