import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProgramPredicateIterator implements Iterator<Predicate> {
    private List<Predicate> predicatesToExamine = new ArrayList<>();

    // constructor
    public ProgramPredicateIterator(List<Predicate> predicateList) {
        this.predicatesToExamine.addAll(predicateList);
    }

    // Checks if the next element exists
    public boolean hasNext() {
        return !this.predicatesToExamine.isEmpty();
    }

    // moves the cursor/iterator to next element
    public Predicate next() {
        int lowestScore = Integer.MAX_VALUE;
        Predicate p = null;
        for(Predicate pred : predicatesToExamine) {
            int score = pred.getNbOutsideCalls(predicatesToExamine);
            if(score < lowestScore) {
                lowestScore = score;
                p = pred;
            }
        }
        if(lowestScore < Integer.MAX_VALUE) {
            this.predicatesToExamine.remove(p);
        }
        return p;
    }
}
