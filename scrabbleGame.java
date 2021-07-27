import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

/** Stevie **/
public class ScrabbleGame 
{
    public static void main(String [] args) 
        throws FileNotFoundException
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("\f");
        ArrayList<Tile> letterBag = new ArrayList<Tile>();
        fillBag(letterBag);

        System.out.println("Would you like to play 1) against the computer, or 2) with other players? (Type \"1\" or \"2\")");
        int methodOfPlay = keyboard.nextInt();
        while(methodOfPlay != 1 && methodOfPlay != 2) {
            System.out.println("Please type again. You must type either \"1\", or \"2\".");
            methodOfPlay = keyboard.nextInt();
        }
        
        if(methodOfPlay == 1){
            System.out.println("The game will end once a player reaches a score of 50, or there are not enough tiles left in the tile bag.");
            ArrayList<Tile> playerRack = makeRack(letterBag);
            ArrayList<Tile> computerRack = makeRack(letterBag);
            
            int totalPScore = 0;
            int totalCScore = 0;
            while((playerRack.size() == 7 && computerRack.size() ==7)&&(totalPScore < 50 && totalCScore < 50)) { // while there are enough tiles in the bag to keep the racks full or until a player reaches score of 75
                System.out.print("Player rack = "); 
                printAllTiles(playerRack);
                System.out.print("Computer rack = ");
                printAllTiles(computerRack);
            
            
                System.out.println("Player, please form and type a word using the letters in the rack");
                System.out.println("If you cannot make a word, please type \"N1\", to refresh your rack.");
                String word = keyboard.next();
                if(word.equals("N1")){
                    refreshRack(playerRack, letterBag);
                    System.out.println("You have refreshed your rack, please form a word now.");
                    printAllTiles(playerRack);
                    word = keyboard.next();
                }
                
            
                while(!validLegalWord(word, playerRack)){
                    System.out.println("That word is not valid. Please try again.");
                    word = keyboard.next();
                }
            
                int score = scoreWord(word, playerRack);
                System.out.println("Your word, " + word + ", had a score of " + score);
                totalPScore += score;
                System.out.println("Player total score: " + totalPScore);
                
                System.out.println("The computer will now make a word using it's rack");
                String computerWord = findBestWord(computerRack);
                if(computerWord.equals(""))
                    refreshRack(computerRack, letterBag);
                computerWord = findBestWord(computerRack);
                int compScore = scoreWord(computerWord, computerRack);
                totalCScore += compScore;
                System.out.println("The computer has chosen the word: " + computerWord + ", which has a score of " + compScore);
                System.out.println("Computer total score: " + totalCScore);
                
                replenishRack(word, playerRack, letterBag);
                replenishRack(computerWord, computerRack, letterBag);
            }
            
            System.out.println("The game has ended.");
            if(totalPScore > totalCScore){
                System.out.println("With a total score of " + totalPScore + ", the player has beat the computer, which had a score of " + totalCScore);
                
            }
            else if (totalPScore < totalCScore){
                System.out.println("With a total score of " + totalCScore + ", the computer has beat the player, which had a score of " + totalPScore);
            }
            else{
                System.out.println("There was a tie. Both the computer and the player achieved a score of " + totalPScore);
            }
        }
        
        if(methodOfPlay == 2){
            System.out.println("How many players are there? There may be up to four—and there must be at least two.");
            int numPlayers = keyboard.nextInt();
            System.out.println("The game will end once a player reaches a score of 50, or there are not enough tiles left in the tile bag.");
            ArrayList<ArrayList<Tile>> players = new ArrayList<ArrayList<Tile>>(); 
            while (numPlayers<2 && numPlayers>4){
                System.out.println("There cannot be more than four players or less than two players. Type a number betweeen 2 and 4.");
                numPlayers = keyboard.nextInt();
            }
            
            for(int i= 0; i<numPlayers; i++){
                players.add(makeRack(letterBag)); 
            }
            
            int[] Pscores = new int[numPlayers]; 
            int maxScore = 0;
            int maxScoreIndex = 0;
            boolean allHave7Tiles = true;
            
            while(allHave7Tiles && maxScore < 50){
                for(int i =0; i<numPlayers; i++){
                    int player = i+1;
                    System.out.println("Player " + player + ", please form and type a word using the letters in the rack");
                    printAllTiles(players.get(i));
                    System.out.println("If you cannot make a word, please type \"N1\", to refresh your rack.");
                    String word = keyboard.next();
                    
                    if(word.equals("N1")){
                        refreshRack(players.get(i), letterBag);
                        System.out.println("You have refreshed your rack, please form a word now.");
                        printAllTiles(players.get(i));
                        word = keyboard.next();
                    }
                    
                
                    while(!validLegalWord(word, players.get(i))){
                        System.out.println("That word is not valid. Please try again.");
                        word = keyboard.next();
                    }
                
                    int score = scoreWord(word, players.get(i));
                    System.out.println("Your word, " + word + ", had a score of " + score);
                    Pscores[i] += score;
                    System.out.println("Player " + player + " total score: " + Pscores[i]);
                    replenishRack(word, players.get(i), letterBag);
                }
                
                for(ArrayList<Tile> rack:players){
                    if(rack.size() <7){
                        allHave7Tiles = false;
                    }
                }
                
                for(int k=0; k< numPlayers; k++){
                    if (Pscores[k] > maxScore){
                        maxScore = Pscores[k];
                        maxScoreIndex = k;
                    }
                }
            }
            System.out.println("The game has ended.");
            int winner = maxScoreIndex+1;
            System.out.println("Player " + winner + " has won, with a total score of " + Pscores[maxScoreIndex]);
        }
    }
    
    public static void refreshRack(ArrayList<Tile> rack, ArrayList<Tile> letterBag){
        if(letterBag.size() == 0){
            return;
        }
        for(int k=0; k<7; k++){
            int random = randomInt(0,letterBag.size());
            Tile randomTile = letterBag.get(random);
            rack.set(k, randomTile);
            letterBag.remove(random);
        }
    }
    
    public static String findBestWord(ArrayList<Tile> rack) throws FileNotFoundException
    {
        String bestWord = ""; 
        int scoreBestWord;
        String currentWord = "";
        Scanner inputFile = new Scanner(new File("scrabbleWords.txt"));
        int firstWord = 0;
        
        while(inputFile.hasNext()){
            String nextWord = inputFile.next(); 
            if(validLegalWord(nextWord, rack)){
                currentWord = nextWord;
                firstWord++;
                if(firstWord == 1){
                    bestWord = currentWord;
                }
                else if(scoreWord(currentWord, rack) > scoreWord(bestWord, rack)){
                    bestWord = currentWord;
                }
            }
        }
        
        if(firstWord == 1){
            return currentWord;
        }
        return bestWord;
    }
    
    public static void fillBag(ArrayList<Tile> bag)
            throws FileNotFoundException
    {  
        Scanner inputFile = new Scanner(new File("tiles.txt"));
        
        int k = 0;
        while(k<97 && inputFile.hasNext())
        {
            char letter = inputFile.next().charAt(0);
            int value = inputFile.nextInt();
            bag.add(new Tile(letter,value));
            k++;
        }
    }
    
    public static void replenishRack(String word, ArrayList<Tile> rack, ArrayList<Tile> tileBag)
    {
        for(int i = 0; i<word.length(); i++){
            char TileToRemove = word.charAt(i);
            int tileRackIndex = findCharInRack(TileToRemove, rack);
            rack.remove(tileRackIndex);
        }
        
        int k = 0;
        while(0<tileBag.size() && rack.size() < 7)
        {
            int randomTile = randomInt(0,tileBag.size());
            rack.add(tileBag.get(randomTile));
            tileBag.remove(randomTile);
        }
    }
    
    public static int findCharInRack(char c, ArrayList<Tile> rack)
    {
        int index = -1;
        for(int i = 0; i<rack.size(); i++){
            if(rack.get(i).getLetter() == c){
                index = i;
            }
        }
        return index;
    }
    
    public static ArrayList<Tile> makeRack(ArrayList<Tile> bag)
            throws FileNotFoundException
    {
        // create a Tile array called rack that can hold 7 tiles
        ArrayList<Tile> rack = new ArrayList<Tile>();
        // make a for loop that starts with int k=0 and repeats 7 times
        for(int i=0; i<7; i++)
        {
            int random = randomInt(0,bag.size());
            rack.add(bag.get(random));
            bag.remove(random);
        }
        
        return rack; // delete after completing the above
    }    

    public static int randomInt(int low, int high) {
        // Determine the length of the range.
                int range = high - low;
      
        // Give a random number from the range.
            return (int)(range*Math.random()) + low;
        
    }   
    
    public static void printAllTiles(ArrayList<Tile> tiles)
    {
        for (int k = 0; k < tiles.size(); k++)
          System.out.print(tiles.get(k) + " "); 
        System.out.println();// go to new line
    }    
    
    public static boolean validLegalWord(String word, ArrayList<Tile> rack) throws FileNotFoundException
    {
        boolean valid = true; 
        boolean legal = false;
        Scanner inputFile = new Scanner(new File("scrabbleWords.txt"));
        String rackString = "";
        
        for(Tile t: rack)
            rackString = rackString + t.getLetter();
        // then, make a String, rackString, which has all the letters 
        //   in the rack
        for(int i = 0; i < word.length();i++)
        {
            if(countLetters(word.charAt(i), word) > countLetters(word.charAt(i), rackString)){
                valid = false; return false;
            }
        }
        
        while(inputFile.hasNext()){
            if(inputFile.next().equalsIgnoreCase(word)){
                legal = true;
            }
        }
        
        return legal&&valid;   // remove after completing above
    }   
    
    public static int countLetters(char c, String s)
    {
        int count = 0;
        for(int i=0; i<s.length(); i++){
            if(s.charAt(i) == c){
                count++;
            }
        }
        return count;
    }

    public static int scoreWord(String word, ArrayList<Tile> rack)
    {
        int score = 0;
        // a for loop that steps k to access every letter of word
        for (int k = 0; k < word.length(); k++)        
            // add to score the scoreOfLetter of word.charAt(k) in the rack
            score += scoreOfLetter(word.charAt(k), rack);  // fix this so it does the above
        return score;
    }       
    
    public static int scoreOfLetter(char letter, ArrayList<Tile> rack)
    {
        // write a for loop that steps k to access every Tile of rack
        for(Tile tile: rack){
          if(letter == tile.getLetter())
          {
              return tile.getValue();
          }
        }
            
           // if the letter matches the letter in the Tile at location k in the rack

                // return the value of the Tile in the rack at location k
                
        return -1000; // after the loop, if we didn't find the letter, 
                      // something is wrong -- invalid word, so return -1000
    }
}
