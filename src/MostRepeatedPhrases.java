import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MostRepeatedPhrases {
    final static int MINIMUM_LENGTH = 3;
    final static int MAXIMUM_LENGTH = 10;
    static String[] document;
    static HashMap<Integer, Boolean> phraseEnd = new HashMap<>();

    public static void main(String[] args) {
        // store most repeated phrases
        List<String> result = new ArrayList<>();

        tokenize("data/simpleTest");
        PriorityQueue<Phrase> maxHeap = buildHeap(document, phraseEnd);
        int numberToPop = Math.min(MAXIMUM_LENGTH, maxHeap.size());

        for(int i = 0; i < numberToPop; i++) {
            result.add(maxHeap.poll().phrase);
        }
        System.out.println(result);
    }

    private static void tokenize(String path) {
        try {
            String contents = new String(Files.readAllBytes(Paths.get(path)));
            document = contents.trim().split("\\s+");
            phraseEnd = new HashMap<>();
            for(int i = 0; i < document.length; i++) {
                if(i == document.length - 1) {
                    phraseEnd.put(i, endWithSymbol(document[i]));
                } else {
                    phraseEnd.put(i, endWithSymbol(document[i]) && startWithCapitalLetter(document[i+1]));
                }
                document[i] = document[i].toLowerCase().replaceAll("[^a-z0-9]$", "");
            }
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    private static boolean endWithSymbol(String word) {
        return word.matches(".*[.!?]$");
    }

    private static boolean startWithCapitalLetter(String word) {
        return word.substring(0, 1).matches("^[A-Z]$");
    }

    private static PriorityQueue<Phrase> buildHeap(String[] document, HashMap<Integer, Boolean> phraseEnd) {

        PriorityQueue<Phrase> queue = new PriorityQueue<>(10, new Comparator<Phrase>() {
            @Override
            public int compare(Phrase a, Phrase b){
                return b.repeatTimes - a.repeatTimes; // top of heap is the most repeated phrase
            }
        });

        HashMap<String, Phrase> set = new HashMap<>();

        for(int index = 0; index < document.length - MINIMUM_LENGTH + 1; index++) {
            String phrase = getMinimumPhrase(index, document);
            if(!set.containsKey(phrase)) {
                set.put(phrase, new Phrase(0, new ArrayList<Integer>(), phrase));
            }

            if(validatePhrase(index, set.get(phrase).indexes)) {
                set.get(phrase).repeatTimes++;
                set.get(phrase).indexes.add(index);
            }
        }

        for(Map.Entry<String, Phrase> entry : set.entrySet()) {
            if(entry.getValue().repeatTimes > 1) {
                queue.add(entry.getValue());
            }
        }

        return queue;
    }

    private static String getMinimumPhrase(int start, String[] document) {
        StringBuffer sb = new StringBuffer();
        for(int i = start; i < MINIMUM_LENGTH + start; i++) {
            sb.append(document[i]).append(" ");
        }
        return sb.toString();
    }

    private static boolean validatePhrase(int index, ArrayList<Integer> indexes) {
        boolean isPhraseOverlap = (indexes.size() != 0 && (indexes.get(indexes.size() - 1) + MINIMUM_LENGTH >= index));
        boolean isSpanningSentences = false;
        for(int i = index; i < index + MINIMUM_LENGTH - 1; i++) {
            if(phraseEnd.get(i)) {
                isSpanningSentences = true;
            }
        }
        return !isPhraseOverlap && !isSpanningSentences;
    }
}
