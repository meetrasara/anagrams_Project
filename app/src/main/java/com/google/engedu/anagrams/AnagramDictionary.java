package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static int wordLength = DEFAULT_WORD_LENGTH;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    //sequence
    private ArrayList<String> dictionary = new ArrayList<String>();
    //unique items
    private HashSet<String> wordSet = new HashSet<String>();
    //search by key
    private HashMap<String, ArrayList<String>> wordMap = new HashMap<String, ArrayList<String>>();
    //maps word length to an arraylist of all words that length
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<Integer, ArrayList<String>>();

    /**
     * Constructor
     * @param reader
     * @throws IOException
     */
    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        //while line can read in, get word, add it to the dictionary(arraylist), add it to the hashSET
        while((line = in.readLine()) != null) {
            String word = line.trim();
            dictionary.add(word);
            wordSet.add(word);
        }

        //hashmap - key is a letter number, value is array list of words with that many letters
        for (int count = 0; count < dictionary.size(); count ++){
            Integer key = dictionary.get(count).length();
            ArrayList<String> sizeList = new ArrayList<String>();
            if(!sizeToWords.containsKey(key)){
                sizeList.add(dictionary.get(count));
                sizeToWords.put(key, sizeList);
            }
            else {
                sizeToWords.get(key).add(dictionary.get(count));

            }
        }

        //walk through the dictionary & add keys and values to hashmap
        for (int count = 0; count < dictionary.size(); count ++){
            //use sortLetter method and save a new string obj
            String key = sortLetters(dictionary.get(count));
            //if the hashmap has the key already
            if(!wordMap.containsKey(key)){
                ArrayList<String> valueList = new ArrayList<String>();  //make new arraylist to hold values
                valueList.add(dictionary.get(count));   //add the word in question to the arraylist
                wordMap.put(key, valueList);    //make a new entry with the key and the value
            }
            else{
                ArrayList<String> oldList = wordMap.get(key);   //new arraylist with values at preexisting key
                oldList.add(dictionary.get(count)); //save new word to values list
                wordMap.remove(key);
                wordMap.put(key, oldList);  //replace key with same key(sorted) but new array list
            }
        }
        //getAnagrams("sun");
        // Log.i("test", "opst vals: " + wordMap.get("opst"));
        // Log.i("test", "Is the word dog in the hashSet: " + wordSet.contains("dog"));
        // Log.i("test " , ""+ getAnagramsWithTwoLetter("sun"));
    }


    // Returns a String with the letters of 'word' in alphabetical order (e.g. given "post", returns "opst").
    private String sortLetters(String word) {
        // Log.i("word", word);
        char[] lettersArray = new char[word.length()];
        for (int i = 0; i < word.length(); i++){
            lettersArray[i] = word.charAt(i);
            //Log.i("test", "" + lettersArray[i]);
        }
        Arrays.sort(lettersArray);
        String sorted = new String(lettersArray);
        return sorted;
        //Log.i("test", "" + sorted);
    }


    public boolean isGoodWord(String word, String base) {
        return (wordSet.contains(word) && !word.toLowerCase().contains(base));

    }

    //uses array list to get all the anagrams
    public List<String> getAnagrams(String targetWord) {
        //sort the user input
        String target = targetWord;
        String sortedWord = sortLetters(targetWord);
        List<String> anagramList = new ArrayList<String>();
        //go through dictionary and if the passed in word matches other words when sorted, it's an anagram!
        for (int count = 0; count < dictionary.size(); count++){
            if(sortedWord.equals(sortLetters(dictionary.get(count)))){
                if (isGoodWord(dictionary.get(count), target)) {
                    anagramList.add(dictionary.get(count));
                }
            }
        }
        //Log.i("test", "" + anagramList.toString());
        return anagramList;
    }

    //takes a string and finds all anagrams that can be formed by adding one letter to that word
    public List<String> getAnagramsWithOneMoreLetter(String word) {
        List<String> result = getAnagrams(word); //holds all possible anagrams when one letter added

        //check given word+each letter of alphabet against the entries in the hashMap (wordMap)
        for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            String word2 = word + alphabet;
            if (wordMap.containsKey(sortLetters(word2))) {
                //iterate through list values at the key
                for (String index2 : wordMap.get(sortLetters(word2))) {
                    if (isGoodWord(index2, word)) {
                        result.add(index2);
                        // Log.i("test", "resultafteradd1" + result.toString());
                    }
                }
            }
        }

        return result;
    }

    public List<String> getAnagramsWithTwoLetter(String word) {
        HashSet<String> resultSet = new HashSet<>();
        resultSet.addAll(getAnagramsWithOneMoreLetter(word));
        ArrayList<String> result = new ArrayList<String>(); // = getAnagramsWithOneMoreLetter(word); //holds all possible anagrams when one letter added
        //check given word+each letter of alphabet against the entries in the hashMap (wordMap)
        for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            for (char alphabet2 = 'a'; alphabet2 <= 'z'; alphabet2++) {
                String word2 = word + alphabet+alphabet2;
                if (wordMap.containsKey(sortLetters(word2))) {
                    //iterate through list values at the key
                    for (String index2 : wordMap.get(sortLetters(word2))) {
                        if (isGoodWord(index2, word)) {
                            resultSet.add(index2);
                        }
                    }
                }
            }
        }
        result.addAll(resultSet);
        return result;
    }

    //hard coded start word
    public String pickGoodStarterWord() {
        String startWord;
        //make a list of words the correct length
        ArrayList<String> wordsOfSize = sizeToWords.get(wordLength);

        //size of a list of words that are the word length instead of dictionary size
        int index = random.nextInt(wordsOfSize.size()-1) +1;
        //pick random start point in wordList and find one with min numbers of anagrams
        startWord = wordsOfSize.get(index);
        while (getAnagramsWithOneMoreLetter(startWord).size() < MIN_NUM_ANAGRAMS){
            index = random.nextInt(wordsOfSize.size()-1) +1;
            startWord = wordsOfSize.get(index);
        }

        if (wordLength<MAX_WORD_LENGTH) {
            wordLength++;
        }
        //startWord.length() == wordLength, wordLength++
        return startWord;

    }
}