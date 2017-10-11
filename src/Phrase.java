import java.util.ArrayList;
import java.util.List;

public class Phrase {
    int repeatTimes;
    ArrayList<Integer> indexes;
    String phrase;

    public Phrase(int repeatTimes, ArrayList<Integer> indexes, String phrase) {
        this.repeatTimes = repeatTimes;
        this.indexes = indexes;
        this.phrase = phrase;
    }
}
