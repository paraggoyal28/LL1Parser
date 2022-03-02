package com.parag;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Grammar {
    int size;
    String startSymbol;
    ArrayList<ArrayList<String>> lhsProduction = new ArrayList<>();
    ArrayList<ArrayList<String>> rhsProduction = new ArrayList<>();
    Set<String> variables = new HashSet<>();
    Set<String> terminals = new HashSet<>();
    ArrayList<ArrayList<String>> first = new ArrayList<>();
    ArrayList<ArrayList<String>> follow = new ArrayList<>();
    String fileName;

    Grammar(String fileName) {
            this.fileName = fileName;
            this.startSymbol = null;
    }
    boolean isUpper(char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    boolean checkTerminal(String str) {
        for(int i = 0, n = str.length(); i < n; ++i) {
            char ch = str.charAt(i);
            if(isUpper(ch)) {
                return false;
            }
        }
        return true;
    }

    boolean checkVariable(String str) {
        for(int i = 0, n = str.length(); i < n; ++i){
            char ch = str.charAt(i);
            if(!isUpper(ch)){
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> handleLeftProduction(String leftVar) throws IOError{
        ArrayList<String> temp = new ArrayList<>();
        if(checkVariable(leftVar)) {
            temp.add(leftVar);
            if(startSymbol==null){
                startSymbol = leftVar;
            }
            variables.add(leftVar);
        } else {
            System.out.println("ERROR: Grammar not according to the READ_ME File norms1\\n Aborting...");
            throw new IOError(new Error("Wrong input"));
        }
        return temp;
    }

    public String handleRightProduction(String rightVar) throws IOError {
        if(!checkTerminal(rightVar) && !checkVariable(rightVar)) {
            System.out.println("ERROR: Grammar not according to the READ_ME File norms2\nAborting...");
            throw new IOError(new Error("Wrong Input"));
        }

        if(!rightVar.equals("null") && checkTerminal(rightVar)) {
            terminals.add(rightVar);
        }
        return rightVar;
    }

    public void readLine(String line) throws IOError{
        int currPos = 0;
        char c = line.charAt(0);
        StringBuilder leftVar = new StringBuilder();
        StringBuilder rightVar;
        ArrayList<String> rhsTemp = new ArrayList<>();
        while(c!=' ') {
            leftVar.append(c);
            currPos++;
            c = line.charAt(currPos);
        }
        lhsProduction.add(handleLeftProduction(leftVar.toString()));
        currPos += 3;
        c = line.charAt(currPos);
        // handle extra spaces
        while(c == ' '){
            currPos++;
            c = line.charAt(currPos);
        }

        // handle right production
        while(c!='\n' && c!='\r' && c!='\0') {

            rightVar = new StringBuilder();
            while (c != ' ' && c != '\0' && c != '\n' && c != '\r') {
                rightVar.append(c);
                currPos++;
                c = line.charAt(currPos);
            }

            rhsTemp.add(handleRightProduction(rightVar.toString()));
        }
        rhsProduction.add(rhsTemp);
    }

    public void readGrammar() throws FileNotFoundException {
        File file = new File(this.fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        try {
            while((st=br.readLine())!=null) {
                readLine(st);
            }
        } catch (IOException | IOError e) {
            System.out.println("IOException " + e.getMessage());
            return;
        }
        this.size = rhsProduction.size();
    }


}
