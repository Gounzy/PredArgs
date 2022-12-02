import java.util.ArrayList;
import java.util.List;

public class PredicateProfile {
    private List<ArgumentProfile> argumentProfiles = new ArrayList<>();;

    public PredicateProfile(List<String> arguments) {
        int i = 0;
        for(String s : arguments) {
            ArgumentProfile ap = new ArgumentProfile(i, s);
            i++;
            argumentProfiles.add(ap);
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
}
