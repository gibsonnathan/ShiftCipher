/*******************************************************************************
 *   Nathan Gibson
 *   January 22, 2017
 *   Program will encrypt, decrypt, and break a shift cipher
 *******************************************************************************/
import java.util.*;
import java.io.*;

public class ShiftCipher{
    /*
        shifts a lower case letter c by shift amount s, ignores 
        non lowercase letters, returns shifted char
    */
    public static char encryptChar(char c, int s){
        if((int) c > 64 && (int) c < 91) { c = Character.toLowerCase(c); }
        if ((int) c > 122 || (int) c < 97) { return c; } 
        if((int) c + s > 122){
            return (char) (97 + ((int) c + s) % 123);
        }
        else{
            return (char) ((int) c + s);
        }
    }
    /*
        shifts a lower case letter c by shift amount s, ignores 
        non lowercase letters, returns shifted char
    */
    public static char decryptChar(char c, int shift){
        if((int) c > 64 && (int) c < 91) { c = Character.toLowerCase(c); }
        if ((int) c > 122 || (int) c < 97) { return c; }
        if((int) c - shift < 97){
            return (char) (123 - (97 - ((int) c - (shift > 25 ? 97 + shift % 26 : shift))));
        }else{
            return (char) ((int) c - shift);
        }
    }
    /*
        takes an encrypted string and shifts it, returns the
        shifted string
    */
    public static String decryptString(String word, int shift){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < word.length(); i++){
            result.append(decryptChar(word.charAt(i), shift));
        }
        return new String(result);
    }
    /*
        takes a plaintext string and shifts it, returns the 
        shifted string
    */
    public static String encryptString(String word, int shift){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < word.length(); i++){
            result.append(encryptChar(word.charAt(i), shift));
        }
        return new String(result);
    }
    /*
        prints out a menu of options
    */
    public static void printMenu(){
        System.out.println("1. Encrypt the file message.txt");
        System.out.println("2. Decrypt the file cipher.txt");
        System.out.println("3. Break the cipher");
        System.out.println("4. Quit");
    }
    /*
        takes a shift amount and encrypts the contents of source, 
        storing the result in destination
    */
    public static void encryptFile(int shift, String sourcePath, String destinationPath){
        ArrayList<String> plainWords = getWordsFromFile(sourcePath);
        ArrayList<String> encryptedWords = new ArrayList<String>();
        for(String plainWord : plainWords){
            encryptedWords.add(encryptString(plainWord, shift));
        }
        writeWordsToFile(destinationPath, encryptedWords);
    }
    /*
        takes a shift amount and decrypts the contents of cipher.txt, 
        storing the result in plain.txt
    */
    public static void decryptFile(int shift, String sourcePath, String destinationPath){
        ArrayList<String> encryptedWords = getWordsFromFile(sourcePath);
        ArrayList<String> plainWords = new ArrayList<String>();
        for(String encryptedWord : encryptedWords){
            plainWords.add(decryptString(encryptedWord, shift));
        }
        writeWordsToFile(destinationPath, plainWords);
    }
    /*
        takes a file path, reads all the words in the file and accumulates them,
        returns a list of the words
    */
    public static ArrayList<String> getWordsFromFile(String path){
        Scanner sc = null;
        try{
            sc = new Scanner(new File(path));
            ArrayList<String> wordList = new ArrayList<String>();
            while(sc.hasNext()){
                wordList.add(sc.next());
            }
            return wordList;
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }
    /*
        Given a list of words, writes the words to a file
    */
    public static void writeWordsToFile(String path, ArrayList<String> words){
        try{
            PrintWriter writer = new PrintWriter(path);
            for(int i = 0; i < words.size(); i++){
                writer.print(i != words.size() - 1 ? words.get(i) + " " : words.get(i));
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
        given a list of words, print them to stdout
    */
    public static void printWordsToScreen(ArrayList<String> words){
        for(int i = 0; i < words.size(); i++){
            System.out.print(i != words.size() - 1 ? words.get(i) + " " : words.get(i) + "\n");
        }
    }
    /*
        method is too long, needs refactoring

        given a path the words from the file are loaded in as well as a dictionary
        different shift amounts are tried and if a shift creates words more that 79%
        of the time the result is printed to the screen
    */
    public static void bruteForce(String sourcePath){
        try{
            ArrayList<String> encryptedWords = getWordsFromFile(sourcePath);
            HashSet<String> dictionary = new HashSet<String>();
            Scanner sc = new Scanner(new File("dictionary.txt"));
            while(sc.hasNext()){
                dictionary.add(sc.next());
            }
            for(int i = 0; i < 26; i++){
                ArrayList<String> current = new ArrayList<String>();
                int count = 0;
                for(String encryptedWord : encryptedWords){
                    current.add(decryptString(encryptedWord, i));
                }
                for(String word : current){
                    if(dictionary.contains(word)){
                        count++;
                    }
                }
                if((double) count / current.size() >= .8){
                    System.out.println("##############KEY:" + i + "###############");
                    printWordsToScreen(current);
                    System.out.println("###################################");
                }
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }
    /*
        method to check if input is a valid int
    */
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }
    /*
        provides a menu to the user and lets them choose different options
    */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while(running){
            printMenu();
            int shift;
            String response = sc.next();
            int input = 0;
            if(isInteger(response)){
                input = Integer.parseInt(response); 
            }
            switch(input){
                case 1:
                    System.out.println("Enter a shift Amount: ");
                    shift = sc.nextInt();
                    encryptFile(shift, "message.txt", "cipher.txt");
                    printWordsToScreen(getWordsFromFile("cipher.txt"));
                break;
                case 2:
                    System.out.println("Enter a shift Amount: ");
                    shift = sc.nextInt();
                    decryptFile(shift, "cipher.txt", "plain.txt");
                    printWordsToScreen(getWordsFromFile("plain.txt"));
                break;
                case 3:
                    bruteForce("cipher.txt");
                break;
                case 4:
                    running = false;
                break;
                default:
                    System.out.println("Invalid choice");
                break;
            }
        }
    }
}