import java.util.ArrayList;
import java.util.List;

public class PredicateProfile {
    private List<ArgumentProfile> argumentProfiles = new ArrayList<>();;
    private Predicate predicate;

    public PredicateProfile(List<String> inputArguments, List<String> outputArguments) {
        int i = 0;
        for(String s : inputArguments) {
            ArgumentProfile ap = new ArgumentProfile(i, s, false);
            ap.setPredicate(this.predicate);
            argumentProfiles.add(ap);
            i++;
        }
        for(String s : outputArguments) {
            ArgumentProfile ap = new ArgumentProfile(i, s, true);
            ap.setPredicate(this.predicate);
            argumentProfiles.add(ap);
            i++;
        }
    }

    public List<ArgumentProfile> getArgumentProfiles() {
        return argumentProfiles;
    }

    public ArgumentProfile retrieveArgumentProfile(String varName) {
        for(ArgumentProfile ap : this.argumentProfiles) {
            if(ap.getName().equals(varName)) {
                return ap;
            }
        }
        return null;
    }

    public String toString() {
        String str = "[";
        int i = 1;
        for(ArgumentProfile ap : this.argumentProfiles) {
            str += ap;
            if(i < this.argumentProfiles.size() - 1) {
                str += ", ";
            }
            i++;
        }
        str += "]";

        return str;
    }

    public boolean equals(PredicateProfile pp) {
        return this == pp; // todo
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }
}
