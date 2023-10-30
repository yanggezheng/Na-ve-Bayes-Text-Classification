package lab4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Biography {
    private String name;
    private String category;
    private String bio;
    private String[] words;
    private List<String> wordList;
    private  ArrayList<String> newWordList;
    private String[] wordsToRemove = """
            about all along also although among and any anyone anything are around because\s
            been before being both but came come coming could did each else every for from\s
            get getting going got gotten had has have having her here hers him his how\s
            however into its like may most next now only our out particular same she\s
            should some take taken taking than that the then there these they this those\s
            throughout too took very was went what when which while who why will with\s
            without would yes yet you your
                        
            com doc edu encyclopedia fact facts free home htm html http information\s
            internet net new news official page pages resource resources pdf site\s
            sites usa web wikipedia www
                        
            one ones two three four five six seven eight nine ten tens eleven twelve\s
            dozen dozens thirteen fourteen fifteen sixteen seventeen eighteen nineteen\s
            twenty thirty forty fifty sixty seventy eighty ninety hundred hundreds\s
            thousand thousands million millions
                        
            """.split(" ");


    public Biography(String name, String category, String bio) {
        this.name = name.toLowerCase();
        this.category = category.toLowerCase();
        this.bio = bio.toLowerCase();
        words = this.bio.split(" ");

// Convert the array of words to a List
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll(",", "");
            words[i] = words[i].replaceAll("\\.", "");
        }
        wordList = Arrays.asList(words);

         newWordList = new ArrayList<>(wordList);

// Remove all the words contained in the wordsToRemove array
        newWordList.removeIf(word -> Arrays.asList(wordsToRemove).contains(word));

// Remove all the words with 1 or 2 letters
        newWordList.removeIf(word -> word.length() <= 2);
// Remove all the words with 1 or 2 letters
        newWordList.replaceAll(s -> s.replaceAll("\n", ""));

    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
    public void setWords(ArrayList<String> s){
        this.newWordList = s;
    }

    public String getBio() {
        return bio;
    }
    public ArrayList<String> getWords(){
        return newWordList;
    }
}
