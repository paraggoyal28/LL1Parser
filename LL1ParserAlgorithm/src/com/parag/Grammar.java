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
    ArrayList<String> variables = new ArrayList<>();
    ArrayList<String> terminals = new ArrayList<>();
    ArrayList<ArrayList<String>> first = new ArrayList<>();
    ArrayList<ArrayList<String>> follow = new ArrayList<>();
    ArrayList<Integer> pTable[][] = new ArrayList[100][100];
    ArrayList<String> ans = new ArrayList<>();
    ArrayList<String> dp = new ArrayList<>();
    ArrayList<String> follSol = new ArrayList<>();
    ArrayList<String> follPro = new ArrayList<>();
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

    public void findFirst(String alpha, int locnull) {
        if(checkTerminal(alpha) && !alpha.equals("null")){
            if(terminals.contains(alpha) && !ans.contains(alpha)){
                ans.add(alpha);
            }
            locnull = 0;
            return;
        }
        if(alpha.equals("null")) {
            ans.add(alpha);
            locnull = 1;
            return;
        }
        for(int i = 0; i < size; ++i) {
            if(lhsProduction.get(i).equals(alpha)) {
                for(int j = 0, n = rhsProduction.size(); j < n; ++j) {
                    String rhsString = rhsProduction.get(i).get(j);
                    if(checkTerminal(rhsString) && !rhsString.equals("null")) {
                        if(!ans.contains(rhsString) && terminals.contains(rhsString)) {
                            ans.add(rhsString);
                        }
                        locnull = 0;
                        break;
                    } else if(checkVariable(rhsString)) {
                        if(!dp.contains(rhsString)) {
                            dp.add(rhsString);
                            findFirst(rhsString, locnull);
                            dp.remove(rhsString);
                        }else {
                            break;
                        }
                        if(locnull == 0){
                            break;
                        }
                        locnull = 0;
                        if(j + 1 != rhsProduction.size()) {
                            if(ans.contains("null")) {
                                ans.remove("null");
                            }
                        } else {
                            int it = i + 1, flag = 0;
                            for(; it < size; ++it) {
                                if(lhsProduction.get(it).get(0).equals(lhsProduction.get(i).get(0))) {
                                    flag = 1;
                                    break;
                                }
                                if(flag == 1) {
                                    if(ans.contains("null")){
                                        ans.remove("null");
                                    }
                                } else {
                                    return;
                                }
                            }
                        }
                    } else {
                        locnull = 1;
                        ans.add("null");
                        break;
                    }
                }
            }
        }
        return;
    }

    public  void findFirstUtil(ArrayList<String> s) {
        ans.clear();
        int loc_null = 0;
        for(int i = 0, n = s.size(); i < n; ++i) {
            loc_null = 0;
            dp.clear();
            dp.add(s.get(i));
            findFirst(s.get(i), loc_null);
            if(ans.contains("null") && i+1 != n) {
                ans.remove("null");
            } else {
                break;
            }
        }
    }

    public void populateFirst() {
        for(int i = 0, n  = variables.size(); i < n; ++i){
            String variable = variables.get(i);
            ans.clear();
            int loc_null = 0;
            dp.clear();
            dp.add(variable);
            findFirst(variable, loc_null);
            ArrayList<String> firstArr = new ArrayList<>();
            for(int j = 0, m = ans.size(); j < m; ++j){
                firstArr.add(ans.get(j));
            }
            first.add(firstArr);
        }
        ans.clear();
    }

    public void findFollow(String s) {
        if(startSymbol.equals(s)) {
            if(!follSol.contains("$")) {
                follSol.add("$");
            }
        }
        for(int i = 0; i < size; ++i) {
            for(int j = 0, n = rhsProduction.get(i).size(); j < n; ++j){
                if(s.equals(rhsProduction.get(i).get(j)) && j+1!=n){
                    ArrayList<String> temp = new ArrayList<>();
                    j++;
                    while(j!=n) {
                        temp.add(rhsProduction.get(i).get(j));
                        j++;
                    }
                    ans.clear();
                    findFirstUtil(temp);
                    int flag = 0;
                    for(String str: ans) {
                        if(str.equals("null")){
                            flag = 1;
                        }
                        else {
                            if(!follSol.contains(str)){
                                follSol.add(str);
                            }
                        }
                    }
                    ans.clear();
                    if(flag == 0){
                        break;
                    } else {
                        String lhsStr = lhsProduction.get(i).get(0);
                        if(lhsStr.equals(s)) {
                            if(!follPro.contains(lhsStr)) {
                                follPro.add(lhsStr);
                                findFollow(lhsStr);
                                if (follPro.contains(lhsStr)) {
                                    follPro.remove(lhsStr);
                                }
                            } else {
                                break;
                            }
                        }
                    }
                } else if(s.equals(rhsProduction.get(i).get(j)) && j+1==n){
                    if(lhsProduction.get(i).get(0).equals(s)){
                        if(!follPro.contains(lhsProduction.get(i).get(0))) {
                            follPro.add(lhsProduction.get(i).get(0));
                            findFollow(lhsProduction.get(i).get(0));
                            if(follPro.contains(lhsProduction.get(i).get(0))) {
                                follPro.remove(lhsProduction.get(i).get(0));
                            }
                        } else {
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return;
    }

    public void populateFollow() {
        for(int i = 0, n = variables.size(); i < n; ++i){
            String variable = variables.get(i);
            follSol.clear();
            follPro.clear();
            follPro.add(variable);
            findFollow(variable);
            ArrayList<String> followStrArr = new ArrayList<>();
            for(String follSolStr: follSol) {
                followStrArr.add(follSolStr);
            }
            follow.set(i,followStrArr);
            follPro.clear();
            follSol.clear();
        }
    }

    public void computeTable() {
        for(int i = 0; i < size; ++i) {
            ArrayList<String> temp = new ArrayList<>();
            temp.clear();
            for(int j = 0; j < rhsProduction.get(i).size(); ++j) {
                temp.add(rhsProduction.get(i).get(j));
            }
            findFirstUtil(temp);
            int fflag = 0, i_ind;
            if(ans.contains("null")){
                fflag = 1;
                ans.remove("null");
            }

            i_ind = variables.indexOf(lhsProduction.get(i).get(0));
            for(int j = 0, n = ans.size(); j < n; ++j) {
                int j_ind = terminals.indexOf(ans.get(j));
                pTable[i_ind][j_ind].add(i);
            }
            if(fflag == 1) {
                for(int j = 0; j < follow.get(i_ind).size(); ++j) {
                    int j_ind = terminals.indexOf(follow.get(i_ind).get(j));
                    if(j_ind == -1) {
                        pTable[i_ind][terminals.size()].add(i);
                    } else {
                        pTable[i_ind][j_ind].add(i);
                    }
                }
            }
            ans.clear();
        }
    }
}
