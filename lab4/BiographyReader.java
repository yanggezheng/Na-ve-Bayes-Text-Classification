package lab4;

import java.io.*;
import java.util.*;

public class BiographyReader {
    static HashMap<String, Integer> map = new HashMap<>();
    static HashMap<Integer, String> map1 = new HashMap<>();
    static List<Biography> biographies;
    static List<Biography> biographies1 = new ArrayList<>();
    static List<Biography> biographies2 = new ArrayList<>();
    static ArrayList<String> trainingWords = new ArrayList<>();
    static double[] res;
    static double[][] p;
    static int[] index;

    public List<Biography> readFile(String filePath) {
        List<Biography> biographies = new ArrayList<>();

        try {
            // Open the file using the FileReader class
            FileReader fr = new FileReader(filePath);

            // Wrap the FileReader object with a BufferedReader object
            BufferedReader br = new BufferedReader(fr);

            // Read the file line by line
            String line;
            String name = "";
            String category = "";
            StringBuilder bio = new StringBuilder();
            while ((line = br.readLine()) != null) {
                // Check if the line is a blank line
                if (line.trim().isEmpty()) {
                    // Create a Biography object with the previous lines and add it to the list
                    if (!name.equals("")) {
                        Biography b = new Biography(name, category, bio.toString());
                        biographies.add(b);
                    }

                    // Reset the variables
                    name = "";
                    category = "";
                    bio = new StringBuilder();
                } else {
                    // Store the line in a variable
                    if (name.isEmpty()) {
                        name = line;
                    } else if (category.isEmpty()) {
                        category = line;
                    } else {
                        bio.append(line).append("\n");
                    }
                }
            }

            // Add the last Biography to the list
            Biography b = new Biography(name, category, bio.toString());
            biographies.add(b);


            // Close the BufferedReader object
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return biographies;
    }

    public static double pCategory(String s) {
        if (!map.containsKey(s)) {
            return 0.1 / (1 + map.size() * 0.1);
        } else {
            Collection<Integer> values = map.values();

            // Use the stream() method to sum the values
            int total = values.stream().mapToInt(Integer::intValue).sum();
            // Get the value associated with the given category
            int value = map.get(s);
            // Calculate the probability of the category
            double p = (double) value / total;
            // Return the probability
            return Math.log((p + 0.1) / (1 + map.size() * 0.1)) / Math.log(2) * -1;

        }
    }

    public static double pwc(String s1, String s2) {
        int count = 0;
        for (Biography b : biographies1) {
            if (b.getCategory().equals(s2)) {
                if (b.getWords().contains(s1)) {
                    count++;
                }
            }
        }
        double p = (double) count / map.get(s2);
        return Math.log((p + 0.1) / (1.2)) / Math.log(2) * -1;
    }

    public static void main(String[] args) {
        // Create an instance of the BiographyReader class
        BiographyReader reader = new BiographyReader();
        // Read the file
        biographies = reader.readFile("file.txt");
        // Print each Biography in the list

        for (int i = 0; i < biographies.size(); i++) {
            if (i < Integer.parseInt(args[1])) {
                biographies1.add(biographies.get(i));
            } else biographies2.add(biographies.get(i));
        }//divide the input into 2 part, one for training, one for prediction
        for (Biography b : biographies1) {
            if (!map.containsKey(b.getCategory())) {
                // If not, add the string to the list
                map.put(b.getCategory(), 1);
            } else {
                int temp = map.get(b.getCategory());
                map.put(b.getCategory(), ++temp);
            }
        }//get the occurrence of each category

        for (Biography b : biographies1) {
            trainingWords.addAll(b.getWords());
        }//add all words to the training set

        for (Biography b : biographies2) {
            ArrayList<String> temp = b.getWords();
            temp.retainAll(trainingWords);
            b.setWords(temp);
        }//remove the words did not appear in the training set

        int number = 0;
        for (String key : map.keySet()) {//create a map so that I can access through index (the map is <Int, String>)

            map1.put(number++, key);
        }
        res = new double[biographies2.size() * map1.size()];
        double[][] total = new double[biographies2.size()][map.size()];// get the value for each person and their category
        for (int i = 0; i < biographies2.size(); i++) {
            ArrayList<String> arr = biographies2.get(i).getWords();
            int count = 0;
            for (String key : map.keySet()) {
                for (String s : arr) {
                    total[i][count] += pwc(s, key);
                }
                total[i][count++] += pCategory(key);
                res[i * map.size() + count - 1] = total[i][count - 1];
            }
        }

        int count1 = 0;
        index = new int[biographies2.size()];//get the least from a, b, c
        for (int i = 0; i < biographies2.size(); i++) {
            index[count1] = 0;
            for (int j = 0; j < map.size() - 1; j++) {
                if (res[i * map.size() + index[count1]] > res[i * map.size() + j + 1]) {
                    index[count1] = j + 1;
                }
            }
            count1++;
        }

        p = new double[biographies2.size()][map.size()];//2 d array for prob

        for (int i = 0; i < biographies2.size(); i++) {
            double de = res[i * map.size() + index[i]];
            double val = 0;
            for (int j = 0; j < map.size(); j++) {
                val += Math.pow(2, de - res[i * map.size() + j]);
            }
            for (int j = 0; j < map.size(); j++) {
                p[i][j] = Math.pow(2, de - res[i * map.size() + j]) / val;
                double temp = Math.round(p[i][j] * 100);
                p[i][j] = temp / 100;
            }
        }
        for (Biography b : biographies2) {
            System.out.println(b.getWords());
        }
        int correct = 0;
        for (int i = 0; i < biographies2.size(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(biographies2.get(i).getName()).append(".    Prediction: ").append(map1.get(index[i])).append(".    ").append(Objects.equals(biographies2.get(i).getCategory(), map1.get(index[i])) ? "Right.\n" : "Wrong.\n");
            if (Objects.equals(biographies2.get(i).getCategory(), map1.get(index[i])))
                correct++;//count the number of correct prediction
            int count2 = 0;
            for (String key : map.keySet()) {
                sb.append(key).append(": ").append(p[i][count2++]).append("    ");//append the prob
            }
            System.out.println(sb);
        }
        System.out.println("Overall accuracy: " + correct + " out of " + biographies2.size() + " = " + (double) correct / biographies2.size() + ".");
    }
}