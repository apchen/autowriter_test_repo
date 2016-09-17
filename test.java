// test.java
import java.io.*;
import java.util.*;
import java.util.Random;


// Code to implement a first-order markov chain for generating new words
// Possibly useful for writing fiction, though it would be better to use 
// a second or third order markov chain for that, and there's probably
// a python package for this sort of thing
public class test{
	// int[][] freq;
	// lower case letters only
	static int valueAt(char a){
		int retval = ((int)a) - 96;
		// System.out.println(retval);
		return retval;
	}
	static char intToChar(int a){
		char retval = (char)(a + 96);
		return retval;
	}

    // assumes that string n is at least length 1
    // also stores frequencies for character being the start of a word
    // and frequencies for character being the end of a word
	static void store_data(String n, int[][] freq){
		int i;
		freq[0][valueAt(n.charAt(0))]++;
		for(i = 0; i < n.length()-1; i++){
			freq[valueAt(n.charAt(i))][valueAt(n.charAt(i+1))]++;
		}
		freq[valueAt(n.charAt(i))][27]++;
	}
	static void generate_word(int[][] freq){
		int temp;
		int i;
		List<Integer> newWord = new ArrayList<Integer>();
		String nw;
		char[] temp2;
		Random r = new Random();
		temp = find_random_char(freq, 0, r);
		i = 0;
		while(temp < 27){
			newWord.add(new Integer(temp));
			temp = find_random_char(freq, temp, r);
		}
//		temp2 = new char[newWord.size()];
//		for(i = 0; i < newWord.size(); i++){
//			temp2[i] = newWord.get(i);
//		}
		temp2 = new char[(newWord.size())];
		for(i = 0; i < newWord.size(); i++){
			temp2[i] = intToChar(newWord.get(i));
		}
		nw = new String(temp2);
		System.out.println(nw);
	}
	static int find_random_char(int [][] freq, int startchar, Random r){
		int temp;
		int i;

        temp = 0;
		for(i = 0; i < 28; i++){
			temp = temp + freq[startchar][i];
		}
		if(temp == 0){
			System.out.println("No entries for " + intToChar(startchar));
			return r.nextInt(28);
		}
		temp = r.nextInt(temp + 1);
		i = 0;
		while(freq[startchar][i] == 0){
			i++;
		}
		while(temp - freq[startchar][i] > 0){
			temp = temp - freq[startchar][i];
			i++;
		}
		System.out.println(intToChar(startchar) + " " + intToChar(i) + ": " + freq[startchar][i]);
		return i;
	}
	static void write_freq_to_file(int[][] freq, String fname) throws IOException{
		int i, j;
		File f = new File(fname);
		FileWriter fw = new FileWriter(f);
		PrintWriter pw = new PrintWriter(fw);

		for(i = 0; i < 28; i++){
			for(j = 0; j < 28; j++){
				pw.print(freq[i][j] + " ");
			}
			pw.println("");
		}
		pw.close();
	}

    // 
	static void read_freq_from_file(int[][] freq, String fname) throws FileNotFoundException{
		int i, j;
		Scanner sc = new Scanner(new File(fname));
		i = 0;
		j = 0;
		// System.out.println("loading freq");
		while(sc.hasNext()){
			// uncomment to switch on method
			freq[i][j] = sc.nextInt();
			// comment out to turn off testing
//			if(freq[i][j] != sc.nextInt()){
//				System.out.println("Error?");
//			}
			j++;
			if(j > 27){
				j = 0;
				i++;
				// System.out.println("loading freq: " + intToChar(i));
			}
		}
	}
	public static void main(String a[]) throws FileNotFoundException, IOException {
		int[][] freq;
		int i,j;
		Scanner sc;
		String temp;
		if(a.length >= 2){
			System.out.println("valid input");
			if (a[0].equals("-f")){
				System.out.println("file read mode -f");
				freq = new int[28][28];
				// calculate frequency patterns
				for(i = 0; i < 28; i++){
					for(j = 0; j < 28; j++){
						freq[i][j] = 0;
					}
				}
				sc = new Scanner(new File(a[1]));
				while(sc.hasNext()){
					store_data(sc.next().toLowerCase(), freq);
				}
				sc.close();
				generate_word(freq);
				write_freq_to_file(freq, "example_freq.txt");
				// read_freq_from_file(freq, "example_freq.txt");

			} else if (a[0].equals("-l")){
				// load frequency from csv
				freq = new int[28][28];
				System.out.println("frequency load mode");
				read_freq_from_file(freq, a[1]);
				generate_word(freq);
			}
		}
	}
}
