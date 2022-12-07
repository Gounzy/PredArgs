import java.util.Comparator;

public class SortArguments  implements Comparator<ArgumentDescription> {
    public int compare(ArgumentDescription a, ArgumentDescription b)
    {
        if(a.isInput() && b.isOutput()) {
            return -1;
        }
        else if(a.isOutput() && b.isInput()) {
            return 1;
        }
        else if(a.isInput() && b.isInput()) {
            // two inputs
            int nbBuildsA = a.getNbInteractions();
            int nbBuildsB = b.getNbInteractions();
            if (nbBuildsA != nbBuildsB) {
                return nbBuildsA - nbBuildsB;
            }

            int nbOperationsA = a.getNbOperations();
            int nbOperationsB = b.getNbOperations();
            if(nbOperationsB != nbOperationsA) {
                return nbOperationsA - nbOperationsB;
            }

            int nbIndOperationsA = a.getNbIndirectOperations();
            int nbIndOperationsB = b.getNbIndirectOperations();
            if(nbIndOperationsB != nbIndOperationsA) {
                return nbIndOperationsA - nbIndOperationsB;
            }

            int nbPositionsA = a.getNbPositions();
            int nbPositionsB = b.getNbPositions();
            if(nbPositionsA != nbPositionsB) {
                return nbPositionsA - nbPositionsB;
            }

            int nbPsisA = a.getNbPsis() + a.getNbPsiBots();
            int nbPsisB = b.getNbPsis() + b.getNbPsiBots();
            if(nbPsisA != nbPsisB) {
                return nbPsisA - nbPsisB;
            }

            float meanPositionA = a.getMeanPosition();
            float meanPositionB = b.getMeanPosition();
            if(meanPositionA != meanPositionB) {
                return (int) Math.ceil(meanPositionA- meanPositionB);
            }

            int nbDecA = a.getNbDec();
            int nbDecB = b.getNbDec();
            if(nbDecA != nbDecB) {
                return nbDecB - nbDecA; // nb dec = weaker
            }

            int nbAssignA = a.getNbAssign();
            int nbAssignB = b.getNbAssign();
            if(nbAssignA != nbAssignB) {
                return nbAssignA - nbAssignB;
            }

            return a.getNbGets() - b.getNbGets();
        }
        else {
            // two outputs
            return 0;
        }
    }
}

