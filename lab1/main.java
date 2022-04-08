package lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("D:\\LFPC\\input.txt");
        Scanner grammar_file = new Scanner(file);

        String input;

        HashMap<Character, HashMap<Character, Character>> fa = new HashMap<>();

        while (grammar_file.hasNextLine()) {
            input = grammar_file.nextLine();
            String[] contexts = input.split(" -> ");

            if (fa.containsKey(contexts[0].charAt(0))) {
                if (contexts[1].length() == 2) {
                    fa.get(contexts[0].charAt(0)).put(contexts[1].charAt(0), contexts[1].charAt(1));
                } else {
                    fa.get(contexts[0].charAt(0)).put(contexts[1].charAt(0), '!');
                }
            } else {
                HashMap<Character, Character> rules = new HashMap<>();
                if (contexts[1].length() == 2) {
                    rules.put(contexts[1].charAt(0), contexts[1].charAt(1));
                } else {
                    rules.put(contexts[1].charAt(0), '!');
                }
                fa.put(contexts[0].charAt(0), rules);
            }
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the word: ");
        input = scanner.nextLine();

        char nextNode = 'S';
        StringBuilder nodes = new StringBuilder();
        nodes.append(" ->S");

        for (int i = 0; i < input.length(); i++) {
            if (fa.get(nextNode).containsKey(input.charAt(i))) {
                nextNode = fa.get(nextNode).get(input.charAt(i));
                if (nextNode != '!')
                    nodes.append("->").append(nextNode);
            }

            if (i == input.length() - 1) {
                if (nextNode == '!') {
                    System.out.println(input + "<- accepted");
                    System.out.println("The traversed nodes are " + nodes);
                } else {
                    System.out.println(input + "<- not accepted");
                }
            }
        }
    }
}
