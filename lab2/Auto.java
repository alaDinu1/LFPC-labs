package lab2;
import java.util.*;

class Auto {
    private final ArrayList<String>
            Q = new ArrayList<>();
    private String
            startState = "q0";
    private final Set<String>
            F = new HashSet<>();
    private final ArrayList<String>
            Alphabet = new ArrayList<>();
    private final HashMap<String, ArrayList<HashMap<String, Set<String>>>>
            Transactions = new HashMap<>();
    private final ArrayList<Set<String>>
            executedList = new ArrayList<>();

    Scanner scanner = new Scanner(System.in);

    // Functions for Automato forming
    private String[] scanAndSplit() {
        String scannedStr = scanner.nextLine();
        return scannedStr.split(" ");
    }

    private void addQ() {
        System.out.print("Q = ");

        String[] splitScannedStr = scanAndSplit();
        Collections.addAll(Q, splitScannedStr);

    }

    private void addF() {
        System.out.println("Insert Final states like rhis: F = q1 q2");
        System.out.print("F = ");

        String[] splitScannedStr = scanAndSplit();

        for (String element : splitScannedStr)
            if (Q.contains(element)) {
                F.add(element);
            } else {
                System.out.println("Error");
                addF();
            }
    }

    private void addAlphabet() {
        System.out.println("Insert values of alphabes in this format: Alphabet = a b ");
        System.out.print("Alphabet = ");

        String[] splitScannedStr = scanAndSplit();
        Collections.addAll(Alphabet, splitScannedStr);

    }

    private boolean verifyInput(String Node1, String Node2, String transitionVariable) {
        return Q.contains(Node1)
                && Q.contains(Node2)
                && Alphabet.contains(transitionVariable);
    }

    private void addTransactions() {
        String scannedStr;
        System.out.println("Insert trasactions like this: \n\tq1,a=q2 \n\tq2,b=q2 ");
        System.out.println("Transactions = ");

        while (!(scannedStr = scanner.nextLine()).isEmpty()) {
            String[] splitByEqual = scannedStr.split("=");
            String[] splitByComma = splitByEqual[0].split(",");

            String Node1 = splitByComma[0];
            String transitionVariable = splitByComma[splitByComma.length - 1];
            String Node2 = splitByEqual[splitByEqual.length - 1];

            if (verifyInput(Node1, Node2, transitionVariable)) {
                ArrayList<HashMap<String, Set<String>>> value = new ArrayList<>();
                HashMap<String, Set<String>> newHashMap = new HashMap<>();
                Set<String> newSet = new HashSet<>();

                if (!Transactions.containsKey(Node1)) {
                    newSet.add(Node2);
                    newHashMap.put(transitionVariable, newSet);
                    value.add(newHashMap);
                    Transactions.put(Node1, value);
                } else {
                    value = Transactions.get(Node1);
                    for (HashMap<String, Set<String>> element : value)
                        if (element.containsKey(transitionVariable)) {
                            newSet = element.get(transitionVariable);
                            newSet.add(Node2);
                            element.replace(transitionVariable, newSet);
                        } else {
                            newSet.add(Node2);
                            element.put(transitionVariable, newSet);
                        }
                }
            } else {

                System.out.println("Error");
                return;
            }
        }
    }

    public void inputAuto() {
        addQ();

        System.out.print("State the start q: \nq = ");
        String state = scanner.nextLine();

        while(!Q.contains(state)){
            System.out.println("Error");
            System.out.print("State the start q: \nq = ");
            state = scanner.nextLine();
        }
        startState = state;

        addF();
        addAlphabet();
        addTransactions();
    }

    public void printAuto() {
        if (!Q.isEmpty()) {
            for (Map.Entry<String, ArrayList<HashMap<String, Set<String>>>> entry : Transactions.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                Set<String> keySet = new HashSet<>();
                keySet.add(key);
                if (key.equals(startState))
                    System.out.print("->" + key + "-> ");
                else if (!Collections.disjoint(F, keySet))
                    System.out.print(" *" + key + "-> ");
                else
                    System.out.print("  " + key + "-> ");
                System.out.println(value);
            }
        }
    }

    // Transform NFA to DFA
    public void toDFA() {
        HashMap<Set<String>, ArrayList<HashMap<String, Set<String>>>> DFA;
        DFA = initiateDFA();

        Set<String> initialState = new HashSet<>();
        initialState.add(startState);

        ArrayList<Set<String>> nextStates;
        nextStates = findNextStates(Transactions.get(startState));

        while (!nextStates.isEmpty()) {
            Set<String> state = nextStates.get(0);
            DFA.put(state, findNewStatesValues(state));
            executedList.add(state);
            nextStates.addAll(findNextStates(DFA.get(state)));
            nextStates = removeExecutedStates(nextStates);
        }
        printDFA(DFA);
        executedList.clear();
    }

    // All functions for DFA construction
    private HashMap<Set<String>, ArrayList<HashMap<String, Set<String>>>> initiateDFA() {
        HashMap<Set<String>, ArrayList<HashMap<String, Set<String>>>> DFA = new HashMap<>();

        ArrayList<HashMap<String, Set<String>>> initialValue = Transactions.get(startState);
        Set<String> initialKey = new HashSet<>();

        initialKey.add(startState);
        DFA.put(initialKey, initialValue);
        executedList.add(initialKey);

        return DFA;
    }

    private ArrayList<Set<String>> findNextStates(ArrayList<HashMap<String, Set<String>>> currentStateValue) {
        ArrayList<Set<String>> newStatesArray = new ArrayList<>();

        for (HashMap<String, Set<String>> StringSetHashMap : currentStateValue)
            for (String letter : Alphabet)
                if (StringSetHashMap.containsKey(letter)) {
                    Set<String> newState = StringSetHashMap.get(letter);
                    newStatesArray.add(newState);
                }
        return newStatesArray;
    }

    private ArrayList<HashMap<String, Set<String>>> findNewStatesValues(Set<String> newState) {
        ArrayList<HashMap<String, Set<String>>> NewStateValues = new ArrayList<>();

        for (String letter : Alphabet) {
            Set<String> newSetString = new HashSet<>();
            for (String state : newState) {
                ArrayList<HashMap<String, Set<String>>> values = Transactions.get(state);
                for (HashMap<String, Set<String>> stringSetHashMap : values)
                    if (stringSetHashMap.containsKey(letter))
                        newSetString.addAll(stringSetHashMap.get(letter));
            }
            HashMap<String, Set<String>> map = new HashMap<>();
            map.put(letter, newSetString);
            NewStateValues.add(map);
        }
        return NewStateValues;
    }

    private ArrayList<Set<String>> removeExecutedStates(ArrayList<Set<String>> statesList) {
        ArrayList<Set<String>> newList = new ArrayList<>();
        for (Set<String> element : statesList)
            if (!executedList.contains(element) &&
                    !element.isEmpty() &&  !newList.contains(element)) {
                newList.add(element);
            }
        return newList;
    }

    private void printDFA(HashMap<Set<String>, ArrayList<HashMap<String, Set<String>>>> DFA) {
        System.out.println(" ");

        Set<String> startStateSet = new HashSet<>();
        startStateSet.add(startState);

        for (Map.Entry<Set<String>, ArrayList<HashMap<String, Set<String>>>> entry : DFA.entrySet()) {

            Set<String> key = entry.getKey();
            Object value = entry.getValue();

            if (key.equals(startStateSet))
                System.out.print("->" + key + "-> ");
            else if (!Collections.disjoint(F, key))
                System.out.print(" *" + key + "-> ");
            else
                System.out.print("  " + key + "-> ");


            System.out.println(value);
        }
    }
}