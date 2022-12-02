import java.util.*;

public class Analyzer {
    private Map<Predicate,ArrayList<Interaction>> profiles = new HashMap<>();
    private List<Predicate> predicates = new ArrayList<>();
    private boolean verbose;

    public void addPredicate(Predicate p) {
        this.profiles.put(p, new ArrayList<>());
        this.predicates.add(p);
    }

    public Analyzer(boolean verbose) {
        this.verbose = verbose;
    }

    public void reorderPredicates() {
        Iterator<Predicate> predicateIterator = new ProgramPredicateIterator(this.predicates);
        List<Predicate> newPredicates = new ArrayList<>();
        while(predicateIterator.hasNext()) {
            newPredicates.add(predicateIterator.next());
        }
        this.predicates = newPredicates;
    }

    public void analyze() {
        Iterator<Predicate> predicateIterator = new ProgramPredicateIterator(this.predicates);
        while(predicateIterator.hasNext()) {
            Predicate p = predicateIterator.next();
            ArrayList<Interaction> predicateProfile = this.profiles.get(p);

            ArrayList<Interaction> currentProfile = new ArrayList<>();

            boolean changes = true;

            while (changes) {
                changes = false;
                int programPoint = 0;
                // Iterating every set of entry in the HashMap


                // System.out.println("New for loop");
                for (Tree<String> body : p.getBodies()) {
                    Node<String> root = body.getRoot();
                    for (Node<String> atom : root.getChildren()) {
                        List<Node<String>> children = atom.getChildren();
                        switch (atom.getData()) {
                            case Predicate.TEST:
                                break;
                            case Predicate.ASSIGN:
                                if (atom.getChildren().size() > 1) {
                                    String outVar = children.get(0).getData();
                                    String inVar = children.get(1).getData();
                                    Interaction interaction = new Interaction(inVar, outVar);
                                    interaction.addOperation(new Operation(Predicate.ASSIGN, programPoint));
                                    //System.out.println(interaction);
                                    interaction.mergeInto(currentProfile);
                                }
                                break;
                            case Predicate.DEC:
                            case Predicate.GETS:
                                if (children.size() > 1) {
                                    String op = atom.getData() + children.get(1).getData();
                                    String inVar, outVar;
                                    if (atom.getData().equals(Predicate.DEC)) {
                                        inVar = children.get(0).getData();
                                        outVar = null;
                                    } else {
                                        outVar = children.get(0).getData();
                                        inVar = null;
                                    }
                                    Node<String> second = children.get(1);
                                    Set<String> variables = second.getVariables();
                                    for (String v : variables) {
                                        Interaction interaction;
                                        if (atom.getData().equals(Predicate.DEC)) {
                                            interaction = new Interaction(inVar, v);
                                        } else {
                                            interaction = new Interaction(v, outVar);
                                        }
                                        interaction.addOperation(new Operation(op, programPoint));
                                        // System.out.println(interaction);
                                        interaction.mergeInto(currentProfile);
                                    }
                                }
                                break;
                            default:
                                if (children.size() > 1) {
                                    Predicate called;
                                    if ((called = this.findPredicate(atom.getData())) != null) {
                                        List<String> variables = atom.getVariablesList();
                                        List<String> inputVariables = called.extractInputVariables(variables);
                                        List<String> outputVariables = called.extractOutputVariables(variables);

                                        // Adding the profile of each argument
                                        for (String arg : called.getArguments()) {
                                            //String inVar = variables.get(i);
                                            ArgumentProfile ap = called.retrieveArgumentProfile(arg);
                                            Map<ArgumentProfile, List<Operation>> osets = ap.getOsets();
                                            if (!osets.isEmpty()) {
                                                for (Map.Entry<ArgumentProfile, List<Operation>> oset : osets.entrySet()) {
                                                    int targetNum = oset.getKey().getNum();
                                                    String targetVar = children.get(targetNum).getData();
                                                    String inVar = children.get(ap.getNum()).getData();
                                                    List<Operation> ops = oset.getValue();

                                                    Interaction inter = new Interaction(inVar, targetVar);
                                                    for (Operation op : ops) {
                                                        inter.addOperation(op);
                                                    }
                                                    inter.mergeInto(currentProfile);
                                                }
                                            }
                                        }

                                        // PSI operations
                                        for (String inVar : inputVariables) {
                                            for (String outVar : outputVariables) {
                                                Interaction interaction = new Interaction(inVar, outVar);
                                                String op = atom.getData().equals(p.getName()) ? Operation.PSI_BOT : Operation.PSI;
                                                Operation operation = new Operation(op, programPoint);
                                                if (!atom.getData().equals(p.getName())) {
                                                    operation.setPredicateProfile(called.getProfile());
                                                }

                                                interaction.addOperation(operation);
                                                // System.out.println(interaction);
                                                interaction.mergeInto(currentProfile);
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                        programPoint++;
                    }
                }

                if (this.verbose) {
                    System.out.println("Local environment computed for " + p.getSignature() + ":");
                    this.displayInteractions(currentProfile);
                    System.out.println("Computing closure...");
                }

                this.transitiveClosure(currentProfile);
                if (this.verbose) {
                    System.out.println("Restricting to arguments...");
                }
                this.argumentsOnly(p, currentProfile);

                if (this.verbose) {
                    System.out.println("Merging...");
                }

                for (Interaction i : currentProfile) {
                    changes = changes || i.mergeInto(predicateProfile);
                }

                if (this.verbose) {
                    System.out.println("Computing argument profiles...");
                }

                this.computeArgumentProfiles(p);

                if (this.verbose) {
                    System.out.println("Reordering arguments...");
                }

                p.reorderArguments();

                if (this.verbose) {
                    this.displayArgumentProfiles();

                    System.out.println("New environment: ");
                    this.displayEnvironment();
                }
            }
        }
    }

    public void computeArgumentProfiles(Predicate p) {
        List<Interaction> pp = this.profiles.get(p);

        for(Interaction i : pp) {
            String in = i.getInVar();
            String out = i.getOutVar();

            ArgumentProfile apIn = p.retrieveArgumentProfile(in);
            ArgumentProfile apOut = p.retrieveArgumentProfile(out);

            for(Operation o : i.getOperations()) {
                apIn.addOperation(o, apOut);
            }
        }
    }

    public void displayInteractions(ArrayList<Interaction> predicateProfile) {
        for (Interaction i : predicateProfile) {
            System.out.println(i.toString());
        }
    }

    public void displayEnvironment() {
        // Iterating every set of entry in the HashMap
        for (Predicate p : this.predicates) {
            ArrayList<Interaction> predicateProfile = this.profiles.get(p);

            //System.out.println("____________________________");
            System.out.println("Profil de : " + p.getSignature());
            System.out.println("____________________________");
            this.displayInteractions(predicateProfile);
            System.out.println("____________________________");
        }
    }

    public List<Predicate> getPredicates() {
        return this.predicates;
    }

    public Predicate findPredicate(String name) {
        for(Predicate p : this.getPredicates()) {
            if(p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void displayArgumentProfiles() {
        for (Predicate p : this.getPredicates()) {
            List<ArgumentProfile> argumentProfiles = p.getProfile().getArgumentProfiles();

            //System.out.println("____________________________");
            System.out.println("Profil des arguments de " + p.getSignature());
            System.out.println("____________________________");
            for(ArgumentProfile ap : argumentProfiles) {
                System.out.println(ap);
            }
        }
    }

    public void argumentsOnly(Predicate p, ArrayList<Interaction> interactions) {
        ArrayList<String> arguments = new ArrayList<>();

        for(String a : p.getArguments()) {
            arguments.add(a);
        }

        ArrayList<Interaction> toRemove = new ArrayList<>();

        for(Interaction i : interactions) {
            String inVar = i.getInVar();
            String outVar = i.getOutVar();

            if(!(arguments.contains(inVar) && arguments.contains(outVar))) {
                toRemove.add(i);
            }
        }

        interactions.removeAll(toRemove);
    }

    public void transitiveClosure(ArrayList<Interaction> interactions) {
        boolean changes = true;
        while(changes) {
            changes = false;
            ArrayList<Interaction> toAdd = new ArrayList<>();
            for(Interaction i : interactions) {
                for (Interaction i2 : interactions) {
                    if(!i.canMerge(i2) && i.getOutVar().equals(i2.getInVar())) {
                        Interaction newI = new Interaction(i.getInVar(), i2.getOutVar());
                        for(Operation o : i.getOperations()) {
                            newI.addOperation(o);
                        }
                        for(Operation o : i2.getOperations()) {
                            newI.addOperation(o);
                        }
                        toAdd.add(newI);
                    }
                }
            }

            if(!toAdd.isEmpty()) {
                for(Interaction tA : toAdd) {
                    changes = changes || tA.mergeInto(interactions);
                }
            }
        }
    }
}
