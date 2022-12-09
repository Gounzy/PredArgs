import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgumentDescription {

    private ArgumentProfile profile;
    private Predicate predicate;
    private int oldOrder, newOrder = -1;
    private int nbInteractions, nbOperations, nbIndirectOperations, nbPositions;
    private int nbDec, nbAssign, nbGets, nbPsiBots, nbPsis;

    private float meanPosition;


    public ArgumentDescription(ArgumentProfile ap, List<Predicate> predicates) {
        profile = ap;
        predicate = ap.getPredicate();

        nbInteractions = ap.getNbInteractions();
        ap.setDescription(this);
        oldOrder = ap.getNum();

        List<Integer> positions = new ArrayList<>();
        positions.add(oldOrder);

        nbDec = nbAssign = nbGets = nbPsiBots = nbPsis = nbIndirectOperations = nbOperations = 0;

        for(Operation op : ap.allOps()) {
            switch(op.getOp()) {
                case Predicate.ASSIGN:
                    nbAssign++;
                    break;
                case Predicate.DEC:
                    nbDec++;
                    break;
                case Predicate.GETS:
                    nbGets++;
                    break;
                case Operation.PSI:
                    nbPsis++;
                    Predicate called = op.getCalledPredicate();
                    int position = op.getPosition();
                    int calledArity = called.getArity();
                    meanPosition += (float) position / calledArity;
                    ArgumentDescription ad = called.getDescriptions().get(position);
                    nbIndirectOperations += ad.nbOperations;
                    break;
                case Operation.PSI_BOT:
                    nbPsiBots++;
                    if(!positions.contains(op.getPosition())) {
                        positions.add(op.getPosition());
                    }
                    break;
            }
        }

        if(nbPsis > 0) {
            meanPosition /= nbPsis;
        }
        else {
            meanPosition = 2;
        }
        nbOperations = nbPsiBots+nbPsis+nbAssign+nbDec+nbGets;
        nbPositions = positions.size();
    }

    public int getUseRatio() {
        if(predicate.nbOperations() > 0) {
            return nbOperations / predicate.nbOperations();
        }
        return 0;
    }

    public ArgumentProfile getProfile() {
        return this.profile;
    }

    public int getOldOrder() {
        return this.oldOrder;
    }

    public int getNewOrder() {
        return this.newOrder;
    }

    public ArgumentDescription setNewOrder(int newOrder) {
        this.newOrder = newOrder;
        return this;
    }

    public void updateProfileNums() {
        profile.setNum(this.newOrder);
    }

    public boolean isOutput() {
        return this.profile.isOutput();
    }
    public boolean isInput() {
        return !this.profile.isOutput();
    }

    public int getNbInteractions() {
        return nbInteractions;
    }

    public void setNbInteractions(int nbInteractions) {
        this.nbInteractions = nbInteractions;
    }

    public int getNbOperations() {
        return nbOperations;
    }

    public void setNbOperations(int nbOperations) {
        this.nbOperations = nbOperations;
    }

    public int getNbIndirectOperations() {
        return nbIndirectOperations;
    }

    public void setNbIndirectOperations(int nbIndirectOperations) {
        this.nbIndirectOperations = nbIndirectOperations;
    }

    public int getNbPositions() {
        return nbPositions;
    }

    public void setNbPositions(int nbPositions) {
        this.nbPositions = nbPositions;
    }

    public int getNbDec() {
        return nbDec;
    }

    public void setNbDec(int nbDec) {
        this.nbDec = nbDec;
    }

    public int getNbAssign() {
        return nbAssign;
    }

    public void setNbAssign(int nbAssign) {
        this.nbAssign = nbAssign;
    }

    public int getNbGets() {
        return nbGets;
    }

    public void setNbGets(int nbGets) {
        this.nbGets = nbGets;
    }

    public int getNbPsiBots() {
        return nbPsiBots;
    }

    public void setNbPsiBots(int nbPsiBots) {
        this.nbPsiBots = nbPsiBots;
    }

    public int getNbPsis() {
        return nbPsis;
    }

    public void setNbPsis(int nbPsis) {
        this.nbPsis = nbPsis;
    }

    public float getMeanPosition() {
        return meanPosition;
    }

    public void setMeanPosition(float meanPosition) {
        this.meanPosition = meanPosition;
    }

    public Map<String, Number> getTuple() {
        Map<String, Number> tuple = new HashMap<>();
        tuple.put("nbInteractions", nbInteractions);
        tuple.put("nbOperations", nbOperations);
        tuple.put("nbIndirectOperations", nbIndirectOperations);
        tuple.put("nbPositions", nbPositions);
        tuple.put("nbPsis", nbPsis);
        tuple.put("nbPsiBots", nbPsiBots);
        tuple.put("meanPosition", meanPosition);
        tuple.put("nbGets", nbGets);
        tuple.put("nbDec", nbDec);
        tuple.put("nbAssign", nbAssign);

        return tuple;
    }
}
