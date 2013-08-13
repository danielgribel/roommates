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
            roommatesApp.run();
        } catch (Exception e) {
            
        }
    }
    
    private void load() {
        preferences = new ArrayList<List<Integer>>();
        availability = new ArrayList<AVAILABILITY>();
        try {
            FileInputStream fstream = new FileInputStream("data/input1.tsv");
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
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    private void run() {
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
                    delete(deletedId, y);
                    System.out.println("# deletion: {" + (y+1) + "," + (deletedId+1) + "}");
                }
            }
            x = nextPerson();
        }
        print();
    }
    
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
    
    private void delete(int a, int b) {
        List<Integer> aArray = preferences.get(a);
        List<Integer> bArray = preferences.get(b);
        int aPosition = bArray.indexOf(a);
        int bPosition = aArray.indexOf(b);
        aArray.set(bPosition, -1);
        bArray.set(aPosition, -1);
    }
    
    private int nextPerson() {
        for(int i = 0; i < preferences.size(); i++) {
            if(availability.get(i) == AVAILABILITY.FREE && !isListEmpty(i)) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean isListEmpty(int id) {
        List<Integer> personPreferences = preferences.get(id);
        for(int i = 0; i < personPreferences.size(); i++) {
            if(personPreferences.get(i) != -1) {
                return false;
            }
        }
        return true;
    }
    
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
    
    private void assignSemiEngaged(int id) {
        availability.set(id, AVAILABILITY.SEMIENGAGED);
    }
    
    private void assignFree(int id) {
        availability.set(id, AVAILABILITY.FREE);
    }
    
    private int getSemiEngaged(int id) {
        for(int i = 0; i < preferences.size(); i++) {
            if(availability.get(i) == AVAILABILITY.SEMIENGAGED && preferences.get(i).get(0) == id) {
                return i;
            }
        }    
        return -1;
    }
    
    private void print() {
        /*for(int i = 0; i < preferences.size(); i++) {
            System.out.print((i+1) + "\t" + availability.get(i) + "\t");
            for(Integer id : preferences.get(i)) {
                System.out.print((id+1) + " ");
            }
            System.out.println();
        }*/
        resultPhase1();
    }
    
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
            System.out.println("# result: no stable matching.");
        }
        else if(numSingle == preferences.size()) {
            System.out.println("# result: stable matching found:");
            for(int i = 0; i < preferences.size()/2; i++) {
                System.out.println("{" + (i+1) + "," + (getHead(i)+1) + "}");
            }
        }
        else {
            System.out.println("# result: please embark to phase 2.");
        }
    }
}
