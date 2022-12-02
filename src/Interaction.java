import java.util.ArrayList;

public class Interaction {

    private String inVar;
    private String outVar;
    private ArrayList<Operation> operations;

    public ArrayList<Operation> getOperations() {
        return operations;
    }

    public String getInVar() {
        return inVar;
    }

    public String getOutVar() {
        return outVar;
    }

    public Interaction(String inVar, String outVar) {
        this.inVar = inVar;
        this.outVar = outVar;
        this.operations = new ArrayList<>();
    }

    public Interaction setInVar(String inVar) {
        this.inVar = inVar;
        return this;
    }

    public Interaction setOutVar(String outVar) {
        this.outVar = outVar;
        return this;
    }

    public Interaction addOperation(Operation operation) {
        if(!this.operations.contains(operation))
            this.operations.add(operation);
        return this;
    }

    public boolean canMerge(Interaction i) {
        return i.inVar.equals(this.inVar) && i.outVar.equals(this.outVar);
    }

    public boolean mergeInto(ArrayList<Interaction> interactions) {
        for(Interaction i : interactions) {
            if(i.canMerge(this)) {
                return i.merge(this);
            }
        }
        interactions.add(this);
        return true;
    }

    public boolean merge(Interaction i) {
        boolean changes = false;
        for(Operation o : i.operations) {
            boolean noSuch = true;
            for(Operation o2 : this.operations) {
                if(o.equals(o2)) {
                    noSuch = false;
                }
            }
            if(noSuch) {
                this.addOperation(o);
                changes = true;
            }
        }
        return changes;
    }

    public String toString() {
        String str = this.inVar +  " ----- ";

        for(int i = 0; i < this.operations.size(); i++) {
            str += this.operations.get(i).toString();
            if(i < this.operations.size() - 1)
                str += ", ";
        }

        str += " ----> " + this.outVar;

        return str;
    }
}
