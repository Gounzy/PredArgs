import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
            return this.compareTuples(a.getTuple(), b.getTuple());
        }
        else {
            List<ArgumentProfile> inputProfilesA = a.getProfile().getInputProfiles();
            List<ArgumentProfile> inputProfilesB = b.getProfile().getInputProfiles();

            int nbInputProfilesA = inputProfilesA.size();
            int nbInputProfilesB = inputProfilesB.size();

            if(nbInputProfilesA != nbInputProfilesB) {
                return nbInputProfilesA - nbInputProfilesB;
            }

            List<ArgumentDescription> ads = new ArrayList<>();
            for(ArgumentProfile apA : inputProfilesA) {
                if(!inputProfilesB.contains(apA)) {
                    ads.add(apA.getDescription());
                }
            }
            for(ArgumentProfile apB : inputProfilesB) {
                if(!inputProfilesA.contains(apB)) {
                    ads.add(apB.getDescription());
                }
            }
            ads.sort(new SortArguments());

            int[] adsPositions = new int[ads.size()];
            int position = 0;
            for(int i = 0; i < ads.size(); i++) {
                if(i == 0 || this.compare(ads.get(i), ads.get(i - 1)) != 0)
                    position++;
                adsPositions[i] = position;
            }

            double meanA = 0, meanB = 0;
            int nbA = 0, nbB = 0;
            for(int i = 0; i < adsPositions.length; i++) {
                if(inputProfilesA.contains(ads.get(i).getProfile())) {
                    meanA += adsPositions[i];
                    nbA++;
                }
                if(inputProfilesB.contains(ads.get(i).getProfile())) {
                    meanB += adsPositions[i];
                    nbB++;
                }
            }
            if(nbA > 0) {
                meanA /= nbA;
            }
            if(nbB > 0) {
                meanB /= nbB;
            }

            return (int) Math.ceil(meanA - meanB);
        }
    }
    
    private int compareTuples(Map<String, Number> tupleA, Map<String, Number> tupleB) {
        int nbBuildsA = (int) tupleA.get("nbInteractions");
        int nbBuildsB = (int) tupleB.get("nbInteractions");
        if (nbBuildsA != nbBuildsB) {
            return nbBuildsA - nbBuildsB;
        }

        int nbOperationsA = (int) tupleA.get("nbOperations");
        int nbOperationsB = (int) tupleB.get("nbOperations");
        if(nbOperationsB != nbOperationsA) {
            return nbOperationsA - nbOperationsB;
        }

        float meanPositionA = (float) tupleA.get("meanPosition");
        float meanPositionB = (float) tupleB.get("meanPosition");
        if(meanPositionA != meanPositionB) {
            return (int) Math.ceil(meanPositionA- meanPositionB);
        }

        int nbPositionsA = (int) tupleA.get("nbPositions");
        int nbPositionsB = (int)  tupleB.get("nbPositions");
        if(nbPositionsA != nbPositionsB) {
            return nbPositionsA - nbPositionsB;
        }

        int nbIndOperationsA = (int) tupleA.get("nbIndirectOperations");
        int nbIndOperationsB = (int) tupleB.get("nbIndirectOperations");
        if(nbIndOperationsB != nbIndOperationsA) {
            return nbIndOperationsB - nbIndOperationsA;
        }

        int nbPsisA = (int) tupleA.get("nbPsis") + (int) tupleA.get("nbPsiBots");
        int nbPsisB = (int) tupleB.get("nbPsis") + (int) tupleB.get("nbPsiBots");
        if(nbPsisA != nbPsisB) {
            return nbPsisA - nbPsisB;
        }

        int nbDecA = (int) tupleA.get("nbDec");
        int nbDecB = (int) tupleB.get("nbDec");
        if(nbDecA != nbDecB) {
            return nbDecB - nbDecA; // nb dec = weaker
        }

        int nbAssignA = (int) tupleA.get("nbAssign");
        int nbAssignB = (int) tupleB.get("nbAssign");
        if(nbAssignA != nbAssignB) {
            return nbAssignA - nbAssignB;
        }

        return (int) tupleA.get("nbGets") - (int) tupleB.get("nbGets");
    }
}

