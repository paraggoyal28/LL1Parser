package com.parag;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Parser {
    String grammarFileName;
    String stringFileName;
    Grammar grammar;
    Stack<String> st;
    ArrayList<String> parsedString;
    Parser(String grammarFileName,String stringFileName) {
        this.grammarFileName = grammarFileName;
        this.stringFileName = stringFileName;
        this.parsedString = new ArrayList<>();
        this.st = new Stack<>();
        grammar = new Grammar(grammarFileName);
    }
    public void readLine(String line) {
        String temp = "";
        int curr = 0;
        char ch = line.charAt(curr);
        while(curr < line.length() ) {
            temp = "";
            while (curr < line.length() && line.charAt(curr)!= ' ' && line.charAt(curr)!= '\r' && line.charAt(curr) != '\n'
                    && line.charAt(curr) != '\0') {
                temp += line.charAt(curr);
                curr++;
            }
            while (curr < line.length() && line.charAt(curr) == ' ') {
                curr++;
            }
            parsedString.add(temp);
        }
    }

    public void inputParsingString() throws FileNotFoundException {
        File file = new File(this.stringFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st, temp;
        int curr = 0;
        try {
            while((st=br.readLine())!=null) {
                readLine(st);
            }
        } catch (IOException | IOError e) {
            System.out.println("IOException " + e.getMessage());
            return;
        }
    }

    public void parse() {
        st.push("$");
        st.push(grammar.startSymbol);
        boolean fail = false;
        for(int i = 0, n = parsedString.size(); i < n; ++i) {
            String str = parsedString.get(i);
            if(!grammar.terminals.contains(str)) {
                System.out.println("Input String Contains Invalid Token " + str);
                fail = true;
            }
        }
        if(fail) {
            System.out.println("Hence Parsing Failed..Terminating..");
            return;
        }
        parsedString.add("$");
        int parsed = 0;
        int idx = 0;
        String inputString = parsedString.get(idx);
        System.out.println("Stack \t\t\t\t INPUT \t\t\t\t ACTION");
        while(true){
            Stack<String> tSt = new Stack<String>();
            while(!st.empty()){
                tSt.push(st.peek());
                st.pop();
            }
            while(!tSt.empty()){
                st.push(tSt.peek());
                System.out.print(tSt.peek() + '\t');
                tSt.pop();
            }
            System.out.print("\t\t\t\t --->");
            for(int i = idx; i < parsedString.size(); ++i) {
                System.out.print(parsedString.get(i) + " ");
            }
            System.out.print("\t\t\t\t---->");

            if(st.peek().equals(inputString) && st.peek().equals("$")) {
                System.out.println("Successful Parsing");
                parsed = 1;
                break;
            }

            else if(st.peek().equals(inputString)) {
                st.pop();
                inputString = parsedString.get(++idx);
                System.out.println("Match");
            }else if(grammar.checkVariable(st.peek())) {
                int v_ind = grammar.variables.indexOf(st.peek());
                int t_ind = grammar.terminals.indexOf(inputString);
                if(t_ind == -1) t_ind = grammar.terminals.size();
                if(grammar.pTable[v_ind][t_ind].size()!=1) {
                    if(grammar.pTable[v_ind][t_ind].size() == 0){
                        System.out.println("Error: The input String is not valid");
                    } else {
                        System.out.println("Error: The parsing table is ambiguous");
                    }
                    break;
                }
                int production =  grammar.pTable[v_ind][t_ind].get(0);
                st.pop();
                System.out.print(grammar.lhsProduction.get(production).get(0) + " -> ");
                for(int j = 0; j < grammar.rhsProduction.get(production).size(); ++j){
                    System.out.print(grammar.rhsProduction.get(production).get(j) + " ");
                }
                System.out.println();
                for(int j =  grammar.rhsProduction.get(production).size() - 1; j >= 0; --j){
                    if(!grammar.rhsProduction.get(production).get(j).equals("null")){
                        st.push(grammar.rhsProduction.get(production).get(j));
                    }
                }
            }
        }
        if(parsed == 1){
            System.out.println("Result: String Successfully Parsed");
        } else {
            System.out.println("Result: Error");
        }
    }
    public static void main(String args[]) throws IOException {
        String fileName = "output.txt";
        BufferedWriter f_writer = new BufferedWriter(new FileWriter(fileName));
        System.out.println("Enter grammar file name");
        Scanner scanner = new Scanner(System.in);
        String grammarFilename = scanner.nextLine();
        System.out.println("Enter Input String file name");
        String inputStringFileName = scanner.nextLine();
        Parser parser = new Parser(grammarFilename, inputStringFileName);
        parser.grammar.readGrammar();
        System.out.println("No. of productions in grammar " + parser.grammar.size);
        System.out.println("\n\n");
        System.out.println("Start Symbol ---> " + parser.grammar.startSymbol);
        System.out.println("\n\n");
        System.out.println("_________________________________________________________");
        System.out.println("_________________________________________________________");
        System.out.println("Grammar: ");
        System.out.println("\n\n");
        for(int i = 0;i < parser.grammar.size; ++i){
            System.out.print("\t\t" + parser.grammar.lhsProduction.get(i).get(0) + " ---->");
            for(int j = 0; j < parser.grammar.rhsProduction.get(i).size(); ++j){
                System.out.print(parser.grammar.rhsProduction.get(i).get(j) + " ");
            }
            System.out.println("\n\n");
        }
        System.out.print("_____________________________________________________________");
//        System.out.println("List of variables\n");
//        for(int i = 0; i < parser.grammar.variables.size(); ++i){
//            System.out.print(parser.grammar.variables.get(i) + " ");
//        }
//        System.out.println("\n\n");
//        System.out.println("List of terminals\n");
//        for(int i = 0; i < parser.grammar.terminals.size(); ++i){
//            System.out.print(parser.grammar.terminals.get(i) + " ");
//        }
//        System.out.println("\n\n");
        parser.grammar.populateFirst();
        System.out.println("FIRST() set of non-terminals: \n\n");
        for(int i = 0, n = parser.grammar.variables.size(); i < n; ++i){
            System.out.print("\t First of " + parser.grammar.variables.get(i) + " --->\t\t");
            for(int j = 0; j < parser.grammar.first.get(i).size(); j++){
                System.out.print(parser.grammar.first.get(i).get(j) + " ");
            }
            System.out.println("\n\n");
        }

        System.out.println();
        System.out.print("______________________________________________________________");
        parser.grammar.populateFollow();
        System.out.println("\n\t FOLLOW() set of non-termianls: \n\n");
        for(int i = 0, n = parser.grammar.variables.size(); i < n; ++i){
            System.out.print("\t Follow of " + parser.grammar.variables.get(i) + " -->\t\t");
            for(int j = 0; j < parser.grammar.follow.get(i).size(); ++j) {
                System.out.print(parser.grammar.follow.get(i).get(j) + " ");
            }
            System.out.println("\n\n");
        }
        System.out.println();
        System.out.print("_________________________________________________________________");
        parser.grammar.computeTable();
        System.out.println("\n\n\t\t\t PARSING TABLE\n\n");
        boolean mult_entry = false;
        System.out.print(" \t");
        for(int i = 0; i < parser.grammar.terminals.size(); ++i){
            System.out.print(parser.grammar.terminals.get(i) + " \t\t\t");
        }
        System.out.print("$\n\n");
        for(int i = 0; i < parser.grammar.variables.size(); ++i) {
            System.out.print(parser.grammar.variables.get(i) + "\t");
            for(int j = 0; j <= parser.grammar.terminals.size(); ++j) {
                if(parser.grammar.pTable[i][j].size() == 0){
                    System.out.print(" ------- ");
                    continue;
                }
                if(parser.grammar.pTable[i][j].size() > 1) {
                    mult_entry = true;
                    System.out.print("*");
                }
                int index = parser.grammar.pTable[i][j].get(0);
                System.out.print(parser.grammar.lhsProduction.get(index).get(0) + "->");
                for(int k = 0, n = parser.grammar.rhsProduction.get(index).size(); k < n; ++k) {
                    System.out.print(parser.grammar.rhsProduction.get(index).get(k));
                }
                System.out.print(" ");
            }
            System.out.println("\n");
        }
        if(mult_entry) {
            System.out.print("\nNOTE: Parsing table has multiple entries in the same blocks which are marked by asterisk(*)\n\n");
            System.out.print("This multiple entries is due to either ambiguous grammar or left recursion or Common prefixes\n\n");
            System.out.print("Thus parsing of string is not possible due to ambiguity\n\nTerminating...\n\n");
        }

        System.out.print("______________________________________________________________________");
        parser.inputParsingString();
        System.out.print("\n\t String to be Parsed: \n\n\t");
        for(int i = 0; i < parser.parsedString.size(); ++i){
            System.out.print(parser.parsedString.get(i));
        }
        System.out.println();
        System.out.print("_______________________________________________________________________");
        System.out.print("\n\t String involved in Parsing: \n\n");
        parser.parse();
        System.out.print("_______________________________________________________________________");
    }
}

