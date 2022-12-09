import java.util.*;

public class ArgumentProfile {
    private int num;
    private String name;

    private boolean isOutput;

    private ArgumentDescription description = null;

    private Map<ArgumentProfile, List<Operation>> osets;

    private List<ArgumentProfile> inputProfiles = new ArrayList<>();

    private Predicate predicate;

    public ArgumentProfile(int num, String name, boolean isOutput) {
        this.num = num;
        this.name = name;
        this.osets = new HashMap<>();
        this.isOutput = isOutput;
    }

    public ArgumentProfile addOperation(Operation o, ArgumentProfile ap) {
        List<Operation> oplist;
        if(this.osets.get(ap) != null) {
            oplist = this.osets.get(ap);
        }
        else {
            oplist = new ArrayList<>();
            this.osets.put(ap, oplist);
        }
        if(!oplist.contains(o)) {
            oplist.add(o);
        }
        ap.addInputProfile(this);
        return this;
    }

    public String toString() {
        String str = "";// this.name + " (argument " + this.num +"): \n";
        Iterator<Map.Entry<ArgumentProfile, List<Operation>>> it = this.osets.entrySet().iterator();
        Map.Entry<ArgumentProfile, List<Operation>> oset;
        for(int k = 0; k < this.osets.entrySet().size(); k++) {
            oset = it.next();
            str += "({";
            for(int i = 0; i < oset.getValue().size(); i++) {
                str += oset.getValue().get(i).toString();
                if(i < oset.getValue().size() - 1) {
                    str += ", ";
                }
            }
            str += "}, " + oset.getKey().num + ")";
            if(k < this.osets.entrySet().size() - 1) {
                str += ", ";
            }
        }

        return str;
    }

    public void addInputProfile(ArgumentProfile ap) {
        if(!this.inputProfiles.contains(ap))
            this.inputProfiles.add(ap);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<ArgumentProfile, List<Operation>> getOsets() {
        return osets;
    }

    public void setDescription(ArgumentDescription description) {
        this.description = description;
    }

    public boolean isOutput() {
        return isOutput;
    }
    public boolean isInput() {
        return !isOutput;
    }

    public ArgumentDescription getDescription() {
        return this.description;
    }

    public int getNbInteractions() {
        return this.osets.size();
    }

    public List<Operation> allOps() {
        List<Operation> allOps = new ArrayList<>();
        for(List<Operation> operationList : osets.values()) {
            allOps.addAll(operationList);
        }
        return allOps;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public List<ArgumentProfile> getInputProfiles() {
        return inputProfiles;
    }

    public void setInputProfiles(List<ArgumentProfile> inputProfiles) {
        this.inputProfiles = inputProfiles;
    }
}