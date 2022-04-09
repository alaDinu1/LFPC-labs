package lab3;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        File file = new File("D:\\LFPC\\codeEx.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        lab3.Lexer lexer = new lab3.Lexer();
        String st;
        while ((st = br.readLine()) != null){
            // Print the string
            lexer.tokenizer(st);
            System.out.println(st);
        }
        System.out.println();
        lexer.printTokens();

    }
}