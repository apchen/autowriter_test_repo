// markovChainTest.java
import java.io.*;
import java.util.*;
import java.util.Random;

// more complete markov chain employing higher-order mechanics
// still only operates on the character level
// also a chain can only generate a word with a minimum size of whatever order it is
public class markovChainTest{
	Map<String, int[]> freq;
	int order;
	markovChainTest seeder;

    // add frequency to map
    // @param String k - key string before character
    // @param char v - char whose frequency should be increased
    public void addFreq(String k, char v){
    	int temp = (int) v;
    	int[] t2;
        // first seed is special - line cannot start with a space
        if(k.equals("") && (v == ' ')){
            return;
        }
    	if(!freq.containsKey(k)){
    		t2 = new int[128];
    		freq.put(k, t2);
    	} 
    	t2 = freq.get(k);
    	t2[temp]++;
    	freq.put(k,t2);
    }

    // generates frequencies for letters appearing after 
    // @param File f - file to be read
    // NOTE: assumes that the file is an ordinary text document with no non-ascii characters
    // reader will be completely confused by strange formatting of any kind
    // such as the line indentation in this code (which creates massive spaces)
    public void readSeederFileWords(File f) throws FileNotFoundException{
    	int i = 0;
    	String temp;
    	freq = new HashMap<String, int[]>();
    	Scanner sc = new Scanner(f);
    	while(sc.hasNextLine()){
    		temp = sc.nextLine();
    		// start of line is considered special
    		if(temp.length() >= order){
    			if(order > 1){
    				addFreq(temp.substring(0,order-1),temp.charAt(order-1));
    			} else {
    				addFreq("",temp.charAt(0));
    			}
    		}
    		for (i = order; i < temp.length(); i++){
    			addFreq(temp.substring(i-order,i),temp.charAt(i));
    		}
    	}
    }

    // set up markov chain
	// higher-order chains require lower-order chains to "seed" their output
	// @param int o - order
	// @param File f - file to be read (please open file for reading before inputting it)
	// @param int mode - 0 for characters, 1 for words (TODO: implement 1)
    // TODO: figure out why having more than order 1 seems to cause problems
	public markovChainTest(int o, String fname, int mode, markovChainTest m){
		// 1st order markov chains do not need a seeder
		// I assume a 0th or negative order chain is a joke, and should be treated as being first order
		if(o < 2){
			order = 1;
			seeder = null;
		// store seeder chain
		// TODO: implement some handler if no seeder is present (e.g. create own seeder)
		} else if (m != null){
			order = o;
			seeder = m;
		} else {
			order = o;
			// temporary workaround, won't work if the file holds frequencies rather than plain text
			seeder = new markovChainTest(o-1, fname, 0, null);
		}
		try{
		    readSeederFileWords(new File(fname));
	    } catch (FileNotFoundException e){
	    	// TODO: figure out how to handle a nonexistant file
            // right now I guess all it does is create a blank file with nothing in it
            System.out.print("File not found\n");
	    	freq = new HashMap<String, int[]>();
	    }
	}

    // generate a single char to follow a word
    public char generateChar(List<Character> seed, Random r){
        char[] temp;
        int size;
        int i;
        int temp2;
        int[] temp3;
        if(seed.size() < order - 1){
            return seeder.generateChar(seed, r);
        }
        size = (seed.size() >= order)?order:(order-1);
        temp = new char[size];
        for(i = 0; i < size; i++){
            temp[i] = (char)seed.get(seed.size() - size + i);
        }
        // System.out.print((new String(temp)) + " + ");
        // if no frequency found for that particular substring, 
        // just choose a random lowercase letter
        if(!freq.containsKey(new String(temp))){
            temp2 = r.nextInt(27);
            if(temp2 == 26){
                return ' ';
            } else {
                return (char)(temp2 + (int)'a');
            }
        } else {
            temp3 = freq.get(new String(temp));
            temp2 = 0;
            for(i = 0; i < 128; i++){
                temp2 += temp3[i];
//                if(temp3[i] > 0){
//                    System.out.print(((char) i) + "-" + temp3[i] + " ");
//                }
            }
            // System.out.println("");
            temp2 = r.nextInt(temp2 + 1);
            // System.out.println(temp2);
            i = 0;
            while((temp2 > temp3[i]) && (i < 128)){
                temp2 -= temp3[i];
                i++;
            }
///            while((i < 128) && (temp3[i] == 0)){
///                i++;
///            }
            // System.out.println((char)i);
            return (char)i;
        }
    }

    // generates a single word (e.g. generates letters until the generator produces a space)
	public String generateWord(){
		List<Character> partial_word = new ArrayList<Character>();
        Random r = new Random();
        char[] temp;
        int i;
        do{
            partial_word.add(generateChar(partial_word,r));
        }while((partial_word.get(partial_word.size()-1) > ' ') && (partial_word.get(partial_word.size()-1) < 128));
        temp = new char[partial_word.size()-1];
        for(i = 0; i < partial_word.size()-1; i++){
            temp[i] = partial_word.get(i);
        }
        return new String(temp);
	}

    // generates characters until a non-numeric character is generated
    public String generateLine(){
        List<Character> partial_word = new ArrayList<Character>();
        Random r = new Random();
        char[] temp;
        int i;
        do{
            partial_word.add(generateChar(partial_word,r));
        }while((partial_word.get(partial_word.size()-1) >= ' ') && (partial_word.get(partial_word.size()-1) < 128));
        temp = new char[partial_word.size()-1];
        for(i = 0; i < partial_word.size()-1; i++){
            temp[i] = partial_word.get(i);
        }
        return new String(temp);
    }

    public static void main(String[] a){
        markovChainTest m;
        if(a.length >= 2){
            if(a[0].equals("-f")){
                // need to figure out how to set order
                m = new markovChainTest(2,a[1],0,null);
                System.out.println(m.generateWord());
                System.out.println(m.generateLine());
            }
        } else {
            System.out.print("Usage: markovChainTest [option] [filename]\n -f read file for input\n");
        }
    }
}