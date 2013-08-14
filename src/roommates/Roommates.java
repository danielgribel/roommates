package roommates;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Stable roommates problem
 * 
 * @author Andre Oliveira
 * @author Daniel Gribel
 * @author Fabricio Kolk
 * 
 */

public class Roommates {
    
    enum AVAILABILITY {
        FREE,
        SEMIENGAGED
    }

    private List<List<Integer>> preferences;
    private List<AVAILABILITY> availability;
    
    public static void main(String[] args) {
        try {
            Roommates roommatesApp = new Roommates();
            roommatesApp.load();
            roommatesApp.phase1();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    // load input data
    private void load() {
        preferences = new ArrayList<List<Integer>>();
        availability = new ArrayList<AVAILABILITY>();
        try {
            FileInputStream fstream = new FileInputStream("data/input3.tsv");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while((strLine = br.readLine()) != null) {
                String[] split = strLine.split("\t");
                int personId = Integer.valueOf(split[0]);
                String[] personPreferencesString = split[1].split(" ");
                List<Integer> personPreferencesInt;
                personPreferencesInt = new ArrayList<Integer>();
                for(String id : personPreferencesString) {
                    personPreferencesInt.add(Integer.valueOf(id)-1);
                }
                preferences.add(personId-1, personPreferencesInt);
                availability.add(AVAILABILITY.FREE);
            }
            in.close();
        } catch(Exception e) {
            System.err.println("Error loading input file: " + e.getMessage());
        }
    }
    
    // phase 1 of roommates algorithm
    private void phase1() {
        int x = nextPerson();
        while(x != -1) {
            int y = getHead(x);
            int z = getSemiEngaged(y);
            if(z != -1) {
                assignFree(z); // y rejects z
            }
            assignSemiEngaged(x); // assign x to be semiengaged to y
            int xIndex = preferences.get(y).indexOf(x);
            System.out.println("# proposal: " + (x+1) + " --> " + (y+1));
            for(int i = xIndex+1; i < preferences.get(y).size(); i++) {
                int deletedId = preferences.get(y).get(i);
                if(deletedId != -1) {
                    delete(deletedId, y); // delete pair
                    System.out.println("# deletion: {" + (y+1) + "," + (deletedId+1) + "}");
                }
            }
            x = nextPerson(); // get next free person
        }
        resultPhase1();
    }
    
    // get the first person in preference list of someone
    private int getHead(int id) {
        List<Integer> personPreferences = preferences.get(id);
        for(int i = 0; i < personPreferences.size(); i++) {
            int head = personPreferences.get(i);
            if(head != -1) {
                return head;
            }
        }
        return -1;
    }
    
    // get the second person in preference list of someone
    private int getSecond(int id) {
        List<Integer> personPreferences = preferences.get(id);
        for(int i = 0; i < personPreferences.size(); i++) {
            int n = personPreferences.get(i);
            if(n != -1 && n != getHead(id)) {
                return n;
            }
        }
        return -1;
    }
    
    // delete a pair
    private void delete(int a, int b) {
        List<Integer> aArray = preferences.get(a);
        List<Integer> bArray = preferences.get(b);
        int aPosition = bArray.indexOf(a);
        int bPosition = aArray.indexOf(b);
        aArray.set(bPosition, -1);
        bArray.set(aPosition, -1);
    }
    
    // get the next free person
    private int nextPerson() {
        for(int i = 0; i < preferences.size(); i++) {
            if(availability.get(i) == AVAILABILITY.FREE && !isListEmpty(i)) {
                return i;
            }
        }
        return -1;
    }
    
    // check if someone list is empty
    private boolean isListEmpty(int id) {
        List<Integer> personPreferences = preferences.get(id);
        for(int i = 0; i < personPreferences.size(); i++) {
            if(personPreferences.get(i) != -1) {
                return false;
            }
        }
        return true;
    }
    
    // check if someone list has only one entry
    private boolean isListSingle(int id) {
        int numEmpty = 0;
        List<Integer> personPreferences = preferences.get(id);
        for(int i = 0; i < personPreferences.size(); i++) {
            if(personPreferences.get(i) == -1) {
                numEmpty++;
            }
        }
        if(numEmpty == personPreferences.size()-1) {
            return true;    
        }
        else {
            return false;
        }
    }
    
    // check if all lists have only one entry
    private boolean isAllListsSingle() {
        for(int i = 0; i < preferences.size(); i++) {
            if(!isListSingle(i)) {
                return false;
            }
        }
        return true;
    }
    
    // check if an empty list exists
    private boolean existEmptyList() {
        for(int i = 0; i < preferences.size(); i++) {
            if(isListEmpty(i)) {
                return true;
            }
        }
        return false;
    }
    
    // get the next list that have many entries
    private int nextListWithMultipleEntries() {
        for(int i = 0; i < preferences.size(); i++) {
            if(!isListSingle(i) && !isListEmpty(i)) {
                return i;
            }
        }
        return -1;
    }
    
    // assign a person to be semiengaged
    private void assignSemiEngaged(int id) {
        availability.set(id, AVAILABILITY.SEMIENGAGED);
    }
    
    // assign a person to be free
    private void assignFree(int id) {
        availability.set(id, AVAILABILITY.FREE);
    }
    
    // get the next free person which have in his/her head the person passed in parameter
    // we use this to get a person current partner and then to break the marriage
    private int getSemiEngaged(int id) {
        for(int i = 0; i < preferences.size(); i++) {
            if(availability.get(i) == AVAILABILITY.SEMIENGAGED && getHead(i) == id) {
                return i;
            }
        }    
        return -1;
    }
    
    // print the result for phase 1. if necessary, phase 2 is called
    private void resultPhase1() {
        int numEmpty = 0;
        int numSingle = 0;
        for(int i = 0; i < preferences.size(); i++) {
            if(isListEmpty(i)) {
                numEmpty++;
            }
            else if(isListSingle(i)) {
                numSingle++;
            }
        }
        if(numEmpty > 0) {
            System.out.println("- result: no stable matching.");
        }
        else if(numSingle == preferences.size()) {
            System.out.println("- result: stable matching found:");
            printFinalResult();
        }
        else {
            System.out.println("- please embark to phase 2.");
            phase2();
        }
    }
    
    // get the list by passing the head value
    private int getIdFromHead(int head) {
        for(int i = 0; i < preferences.size(); i++) {
            if(getHead(i) == head) {
                return i;
            }
        }
        return -1;
    }
    
    // phase1 phase 2 -- find rotations and reduce lists
    private void phase2() {
        int counterRotations = 1;
        while(!isAllListsSingle() && !existEmptyList()) {
            int id = nextListWithMultipleEntries();
            int head = getHead(id);
            List<Integer> xSet = new ArrayList<Integer>();
            List<Integer> ySet = new ArrayList<Integer>();
            xSet.add(id);
            ySet.add(head);
            int second = getSecond(id);
            while(!ySet.contains(second)) {
                int nextX = getIdFromHead(second);
                xSet.add(nextX);
                ySet.add(second);
                second = getSecond(nextX);
            }
            System.out.print("#rotation(" + counterRotations + "): ");
            for(int i = 0; i < ySet.size(); i++) {
                System.out.print("{" + (xSet.get(i)+1) + "," + (ySet.get(i)+1) + "} ");
                if(i == 0) {
                    handleRotations(ySet.get(i), xSet.get(xSet.size()-1));
                }
                else {
                    handleRotations(ySet.get(i), xSet.get(i-1));
                }
            }
            counterRotations++;
            System.out.println();
        }
        if(existEmptyList()) {
            System.out.println("- result: no stable matching.");
        }
        else {
            System.out.println("- result: stable matching found:");
            printFinalResult();
        }
    }
    
    // handle rotations, removing required people from required lists
    private void handleRotations(int id, int removedFrom) {
        List<Integer> list = preferences.get(id);
        int removeFromIndex = list.indexOf(removedFrom);
        for(int i = removeFromIndex+1; i < list.size(); i++) {
            int n = list.get(i);
            if(n != -1) {
                removeFromList(n, id);
                list.set(i, -1);
            }
        }
    }
    
    // remove an entry from a list
    private void removeFromList(int id, int removed) {
        List<Integer> list = preferences.get(id);
        int pos = list.indexOf(removed);
        list.set(pos, -1);
    }
    
    // print the final result
    private void printFinalResult() {
        for(int i = 0; i < preferences.size()/2; i++) {
            System.out.println("{" + (i+1) + "," + (getHead(i)+1) + "}");
        }
    }
    
    // print the full table
    private void printFullTable() {
        for(int i = 0; i < preferences.size(); i++) {
            System.out.print((i+1) + "\t" + availability.get(i) + "\t");
            for(Integer id : preferences.get(i)) {
                System.out.print((id+1) + " ");
            }
            System.out.println();
        }
    }
    
}
