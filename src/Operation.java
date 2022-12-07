import java.util.ArrayList;
import java.util.List;

public class Operation {
    public static final String PSI_BOT = "psi_bottom";
    public static final String PSI = "psi";

    private String op;
    private int programPoint = -1;

    private Predicate calledPredicate = null;
    private int position = -1;

    private PredicateProfile predicateProfile = null;

    public boolean equals(Operation o) {
        boolean eq = this.op.equals(o.op) && this.programPoint == o.programPoint;
        if(this.hasProfile()) {
            // System.out.println("t2");
            if(o.hasProfile()) {
               return o.predicateProfile.equals(this.predicateProfile);
            }
            else
                return false;
        }
        return eq;
    }

    public boolean samePsis(List<Interaction> l1, List<Interaction> l2) {
        boolean same = true;
        if(l1.size() != l2.size())
            return false;
        for(int i = 0; i < l1.size(); i++) {
            same = same && l1.get(i).equals(l2.get(i));
        }
        return same;
    }

    public Operation(String op) {
        this.op = op;
    }
    public Operation(String op, int programPoint) {
        this.programPoint = programPoint;
        this.op = op;
    }

    public Operation(String op, int programPoint, Predicate predicate, int position) {
        this.programPoint = programPoint;
        this.op = op;
        this.calledPredicate = predicate;
        this.position = position;
    }


    public int getProgramPoint() {
        return programPoint;
    }

    public void setProgramPoint(int programPoint) {
        this.programPoint = programPoint;
    }

    public String toString() {
        String str = this.op;

        if(this.hasProfile()) {
            str += this.predicateProfile;
        }

        return str;
    }

    public boolean hasProfile() {
        return this.predicateProfile != null; 
    }

    public PredicateProfile getPredicateProfile() {
        return predicateProfile;
    }

    public void setPredicateProfile(PredicateProfile predicateProfile) {
        this.predicateProfile = predicateProfile;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Predicate getCalledPredicate() {
        return calledPredicate;
    }

    public void setCalledPredicate(Predicate calledPredicate) {
        this.calledPredicate = calledPredicate;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
