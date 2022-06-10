package lab4;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            ChomskyNormalForm chomsky = new ChomskyNormalForm(new File("D:\\LFPC\\lab4.txt"));
            chomsky.to_chomsky();
        }
        catch (FileNotFoundException e){
            System.out.print(e.toString());
        }
    }
}