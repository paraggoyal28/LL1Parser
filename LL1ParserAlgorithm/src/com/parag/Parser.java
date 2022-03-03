package com.parag;

import java.io.*;
import java.util.ArrayList;
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
        while(ch!='\r' && ch!= '\n' && ch!='\0') {
            temp = "";
            while (ch == ' ') {
                curr++;
                ch = line.charAt(curr);
            }
            while (ch != ' ' && ch != '\r' && ch != '\n' && ch != '\0') {
                temp += ch;
                curr++;
                ch = line.charAt(curr);
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
        String inputString = parsedString.get(0);
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
            for(String inStr: parsedString) {
                System.out.print(inStr + "\t\t\t\t\t --->");
            }
            
        }
    }

}

