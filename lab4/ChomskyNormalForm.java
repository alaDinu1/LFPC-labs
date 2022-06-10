package lab4;

// The process of Context Free Grammar to Chomsky Normal Form
// Realised using the following steps
// Step 1 - Removing e-production (epsilon is writeen in grammar with symbol 0)
// Step 2 - Remove Unit Productions (only one non-terminal in the right side)
// Step 3 - Remove Non-Productive Symbols
// Step 4 - Remove Inaccesible Symbols
// Step 5.1 - Make all the rules of length 2 or 1
// Step 5.2 - Add some terminal rules for the rules when the right side has one terminal and one non-terminal( ex.aB,cD,Ae)

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ChomskyNormalForm {
    private final ContextFreeGrammar grammar;

    ChomskyNormalForm(File grammar_in) throws FileNotFoundException {
        this.grammar = new ContextFreeGrammar(grammar_in);
    }

    void to_chomsky(){
        this.grammar.print();
        //cnf transformation
        this.removeEpsilon();
        this.removeUnitProductions();
        this.removeNonProductive();
        this.removeInaccessible();
        this.removeMoreThanTwo();
        this.addTerminalRules();
        this.grammar.merge();
        System.out.println("\nThe Chomsky Normal Form is: ");
        this.grammar.print();
    }

    // STEP 1
    // Removing epsilon productions
    //
    private void removeEpsilon(){
        if (!this.grammar.contains_epsilon){
            System.out.println("No epsilon non-terminals.");
            return;
        }
        List<String> epsilon = this.grammar.epsilon_non_terminals();
        System.out.print("\nEpsilon non_terminals: ");
        for (String s: epsilon){
            System.out.print(s + " ");
        }
        System.out.println();
        for (int i = 0; i < this.grammar.getLeft().size(); i++){
            for (int j = 0; j < this.grammar.getRight().get(i).size(); j++){
                String word = this.grammar.getRight().get(i).get(j);
                for (char c: word.toCharArray()){
                    if (epsilon.contains(String.valueOf(c))){
                        if (word.length() == 1){
                            this.grammar.getRight().get(i).add("0");
                        }
                        else{
                            this.grammar.getRight().get(i).add(word.replaceFirst(String.valueOf(c), ""));
                        }
                        break;
                    }
                }
            }
        }
        List<String> remove = new ArrayList<>();
        for (String left: this.grammar.getLeft()){
            if (left.equals("S")) continue;
            this.grammar.rules(left).remove("0");
            if (this.grammar.rules(left).size() == 0){
                remove.add(left);
            }
        }
        for (String left: remove){
            this.grammar.getNonterminals().remove(left);
            this.grammar.getRight().remove(this.grammar.getLeft().indexOf(left));
            this.grammar.getLeft().remove(left);
        }
        this.grammar.print();
    }

    // Step 2
    // Removing (Unit Productions) productions with one non-terminal in the right side
    //
    private void removeUnitProductions(){
        for (String left: this.grammar.getLeft()){
            List<String> new_r = new ArrayList<>();
            for (String rule: this.grammar.rules(left)){
                if (rule.length() < 2 && rule.toUpperCase().equals(rule)){
                    List<String> r = this.grammar.rules(rule);
                    for (String r_: r){
                        if (r_.length() == 1 && r_.toLowerCase().equals(r_)){
                            new_r.add(r_);
                        }
                        if (r_.length() == 2){
                            new_r.add(r_);
                        }
                        if (r_.length() == 3){
                            new_r.add(r_);
                        }
                        if (r_.length() == 4){
                            new_r.add(r_);
                        }

                    }
                }
                else{
                    new_r.add(rule);
                }
            }
            this.grammar.change_rules(left, new_r);
        }
        System.out.println("\nRemoving productions with one non-terminal in the right side");
        this.grammar.print();
    }



    // STEP 3
    //Removing Non-Productive Symbols
    private void removeNonProductive(){
        List<String> non_productive = new ArrayList<>();
        for (String non_terminal: this.grammar.getNonterminals()){
            if (!this.grammar.getLeft().contains(non_terminal)){
                non_productive.add(non_terminal);
            }
        }
        int i = 0;
        for (List<String> words: this.grammar.getRight()){
            i += 1;
            List<String> remove = new ArrayList<>(); // word with non_productive non_terminal of some terminal
            for (String word: words){
                for (char c: word.toCharArray()){
                    if (non_productive.contains(String.valueOf(c))){
                        remove.add(word);
                        break;
                    }
                }
            }
            this.grammar.getRight().get(i - 1).removeAll(remove);
        }
        List<Integer> remove = new ArrayList<>();
        int k = 0;
        for (String left: this.grammar.getLeft()){ // we might remove all word for some non_terminal
            if (this.grammar.rules(left).size() == 0){ //and now can remove this non_terminal too
                remove.add(k);
            }
            k += 1;
        }
        k = 0;
        for (int j: remove){
            this.grammar.getNonterminals().remove(this.grammar.getLeft().get(j - k));
            this.grammar.getRight().remove(j - k);
            this.grammar.getLeft().remove(j - k);
            k += 1;
        }
        if (non_productive.size() != 0){
            this.grammar.getNonterminals().removeAll(non_productive);
            System.out.print("\nNon-productive non-terminals are: ");
            for (String s: non_productive){
                System.out.print(s + " ");
            }
            System.out.println();
            this.grammar.print();
        }
        else{
            System.out.println("\nNo non-productive non-terminals.");
        }
    }

    // STEP 4
    // Removing inaccessible symbols
    private void removeInaccessible(){
        List<String> inaccessible = new ArrayList<>();
        for (String non_terminal: this.grammar.getLeft()){
            if (non_terminal.equals("S")) continue;
            boolean finded = false;
            for (List<String> words: this.grammar.getRight()){
                for (String word: words){
                    if (word.contains(non_terminal)){
                        finded = true;
                        break;
                    }
                }
                if (finded) break;
            }
            if (!finded && !inaccessible.contains(non_terminal)){
                inaccessible.add(non_terminal);
            }
        }
        if (inaccessible.size() != 0){
            this.grammar.getNonterminals().removeAll(inaccessible);
            System.out.print("\nInaccessible non-terminals are: ");
            for (String s: inaccessible){
                System.out.print(s + " ");
            }
            System.out.println();
            int i = 0, removed = 0;
            for (String left: this.grammar.getLeft()){
                if (inaccessible.contains(left)){
                    this.grammar.getRight().remove(i - removed);
                    removed += 1;
                }
                i += 1;
            }
            this.grammar.getLeft().removeAll(inaccessible);
            this.grammar.print();
        }
        else {
            System.out.println("\nNo inaccessible non-terminals.");
        }
    }


    // STEP 5.1
    // Removing productions with length more than 2. Make all the rules of length 1 or 2.
    //
    private void removeMoreThanTwo(){
        List<String> r = new ArrayList<>(this.grammar.getLeft());
        for (String left: r){
            List<String> new_r = new ArrayList<>();
            for (String rule: this.grammar.rules(left)){
                if (rule.length() > 2){
                    while (rule.length() > 2){
                        String w = rule.substring(0, 2);
                        rule = rule.substring(2);
                        List<String> l = new ArrayList<>();
                        l.add(w);
                        String w_ = this.grammar.get_free();
                        this.grammar.getNonterminals().add(w_);
                        this.grammar.add_rule(w_, l);
                        rule = w_ + rule;
                    }
                    new_r.add(rule);
                }
                else {
                    new_r.add(rule);
                }
            }
            this.grammar.change_rules(left, new_r);
        }
        System.out.println("\nRemoving productions with length more than 2.");
        this.grammar.print();
    }

    // STEP 5.2
    // Adding terminal rules and replacing these terminals in the rules with 1 terminal and one non-terminal in the right side
    private void addTerminalRules(){
        for (String left: this.grammar.getLeft()) {
            List<String> l = new ArrayList<>();
            for (String rule : this.grammar.rules(left)) {
                StringBuilder new_word = new StringBuilder();
                if (rule.length() > 1) {
                    for (char c : rule.toCharArray()) {
                        if (!String.valueOf(c).toUpperCase().equals(String.valueOf(c))) {
                            new_word.append(String.valueOf(c).toUpperCase()).append("1");
                        } else {
                            new_word.append(c);
                        }
                    }

                    l.add(String.valueOf(new_word));
                }
                else l.add(rule);
            }
            this.grammar.change_rules(left, l);
        }
        for (String terminal: this.grammar.getTerminals()){
            String non_terminal = terminal.toUpperCase()+"1";
            if (!this.grammar.getNonterminals().contains(non_terminal)){
                this.grammar.getNonterminals().add(non_terminal);
            }
            List<String> l = new ArrayList<>();
            l.add(terminal);
            this.grammar.add_rule(non_terminal, l);
            this.grammar.getNonterminals().add(non_terminal);
        }
        System.out.println("\nAdded new rules for terminals:");
        this.grammar.print();
    }


}