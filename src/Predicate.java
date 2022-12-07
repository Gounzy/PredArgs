import java.sql.Array;
import java.util.*;

public class Predicate {

    public static final String DEC = "=>",
                         GETS = "<=",
                         ASSIGN = ":=",
                         TEST = "<->";
    private String name;
    private List<String> arguments = new ArrayList<>();
    private List<Tree<String>> bodies;

    private PredicateProfile profile;

    public Predicate(String name, List<String> inputArguments, List<String> outputArguments, List<Tree<String>> bodies) {
        this.arguments.addAll(inputArguments);
        this.arguments.addAll(outputArguments);
        this.name = name;
        this.bodies = bodies;
        this.profile = new PredicateProfile(inputArguments, outputArguments);
        this.profile.setPredicate(this);
    }

    public String toString() {
        String str = "";
        for (Tree<String> body : this.bodies) {
            str += this.name + "(" + this.displayArguments() + ")" + " <- " + this.displayBody(body) + ".\n";
        }
        return str;
    }

    public String getName() {
        return name;
    }

    public int getArity() {
        return this.arguments.size();
    }


    public List<String> extractInputVariables(List<String> variables) {
        return this.extractVariables(variables, true);
    }

    public List<String> extractOutputVariables(List<String> variables) {
        return this.extractVariables(variables, false);
    }

    private List<String> extractVariables(List<String> variables, boolean input) {
        List<String> list = new ArrayList<>();
        List<ArgumentProfile> aps = this.profile.getArgumentProfiles();
        aps.sort(new SortArgumentProfiles());

        for(int i = 0; i < aps.size(); i++) {
            if(aps.get(i).isInput() == input) {
                list.add(variables.get(i));
            }
        }
        return list;
    }

    public String getSignature() {
        return this.name + "/" + this.getArity();
    }

    public List<String> getArguments() {
        return arguments;
    }

    public List<Tree<String>> getBodies() {
        return bodies;
    }

    public PredicateProfile getProfile() {
        return profile;
    }

    public ArgumentProfile retrieveArgumentProfile(String varName) {
        return this.profile.retrieveArgumentProfile(varName);
    }

    private String displayArguments() {
        String str = "";
        int size = this.arguments.size();
        for(int i = 0; i < size; i++) {
            str += this.arguments.get(i);
            if(i < size - 1) {
                str += ", ";
            }
        }
        return str;
    }

    private String displayBody(Tree<String> body) {
        Node<String> root = body.getRoot();
        return this.displayBody(root);
    }

    public int getNbOutsideCalls(List<Predicate> allowedPredicates) {
        List<String> allowedNames = new ArrayList<>();
        for(Predicate p : allowedPredicates) {
            allowedNames.add(p.name);
        }

        ArrayList<String> called = new ArrayList<>();
        for (Tree<String> body : this.bodies) {
            Node<String> root = body.getRoot();
            for(Node<String> child : root.getChildren()) {
                List<Node<String>> children = child.getChildren();
                switch (child.getData()) {
                    case ASSIGN:
                    case TEST:
                    case DEC:
                    case GETS:
                        break;
                    default:
                        if (!child.getData().equals(this.name)) {
                            if (!called.contains(child.getData())) {
                                if(allowedNames.contains(child.getData()))
                                    called.add(child.getData());
                            }
                            break;
                        }
                }
            }
        }
        return called.size();
    }

    public void reorderArguments(List<Predicate> predicates) {
        PredicateProfile profile = this.profile;
        List<ArgumentDescription> values = new ArrayList<>();
        profile.getArgumentProfiles().sort(new SortArgumentProfiles());

        ArgumentDescription argumentDescription;
        for(ArgumentProfile ap : profile.getArgumentProfiles()) {
            argumentDescription = new ArgumentDescription(ap, predicates);
            values.add(argumentDescription);

            for(ArgumentProfile ap2 : profile.getArgumentProfiles()) {

            }
        }

        values.sort(new SortArguments());

        int i = 0;
        for(ArgumentDescription ad : values) {
            ad.setNewOrder(i);
            i++;
        }
        this.reorderArgumentList(values);

        this.updateProfileNums(values, this.getNewPositionsMap());
        profile.getArgumentProfiles().sort(new SortArgumentProfiles());

    }

    public int nbOperations() {
        int nb = 0;
        for (Tree<String> body : this.bodies) {
            nb += body.getRoot().getChildren().size();
        }
        return nb;
    }

    private void reorderArgumentList(List<ArgumentDescription> values) {
        ArrayList<String> newArguments = new ArrayList<>();
        for(int i = 0; i < this.arguments.size(); i++) {
            newArguments.add(this.retrieveCurrentArgumentDescriptionValue(values, i));
        }
        this.arguments = newArguments;
    }

    private String retrieveCurrentArgumentDescriptionValue(List<ArgumentDescription> ads, int newPosition) {
        for(ArgumentDescription ad : ads) {
            if(ad.getNewOrder() == newPosition) {
                return ad.getProfile().getName();
            }
        }
        return null;
    }

    private void updateProfileNums(List<ArgumentDescription> ads, Map<Integer,Integer> amap) {
        for(ArgumentDescription ad : ads) {
            ad.updateProfileNums();
        }
    }

    public List<ArgumentDescription> getDescriptions() {
        List<ArgumentDescription> ads = new ArrayList<>();
        for(ArgumentProfile ap : this.profile.getArgumentProfiles()) {
            if(ap.getDescription() != null) {
                ads.add(ap.getDescription());
            }
        }
        return ads;
    }

    public Map<Integer,Integer> getNewPositionsMap() {
        List<ArgumentDescription> ads = this.getDescriptions();
        Map<Integer,Integer> map = new HashMap<>();
        for(ArgumentDescription ad : ads) {
            map.put(ad.getOldOrder(), ad.getNewOrder());
        }
        return map;
    }

    public void reorderCalls(List<Predicate> predicates) {
        for(Tree<String> tree : this.getBodies()) {
            Node<String> root = tree.getRoot();
            for(Node<String> child : root.getChildren()) {
                List<Node<String>> children = child.getChildren();
                switch(child.getData()) {
                    case ASSIGN:
                    case TEST:
                    case DEC:
                    case GETS:
                        break;
                    default:
                        Predicate called = null;
                        for(Predicate p : predicates) {
                            if(p.getName().equals(child.getData())) {
                                // p is called
                                called = p;
                            }
                        }

                        if(called == null) {
                            break;
                        }

                        List<ArgumentDescription> ads = called.getDescriptions();
                        if(ads.isEmpty()) {
                            break;
                        }

                        List<Node<String>> newChildren = new ArrayList<>();
                        int i = 0;
                        for(ArgumentDescription ad : ads) {
                            if(ad.getNewOrder() == i) {
                                newChildren.add(children.get(ad.getOldOrder()));
                                i++;
                            }
                        }
                        child.setChildren(newChildren);
                }
            }
        }
    }

    private String displayBody(Node<String> root) {
        if(root.getChildren().size() == 0) {
            return root.getData();
        }

        String str = "";
        int i = 0;

        for(Node<String> child : root.getChildren()) {
            List<Node<String>> children = child.getChildren();
            switch (child.getData()) {
                case ASSIGN :
                case TEST :
                    if(children.size() > 1) {
                        Node<String> first = children.get(0);
                        Node<String> second = children.get(1);
                        str += first.getData() + " " + child.getData() +  " " + second.getData();
                    }
                    break;
                case DEC:
                case GETS:
                    if(children.size() > 1) {
                        Node<String> first = children.get(0);
                        Node<String> second = children.get(1);
                        str += first.getData() + " " + child.getData() +  " ";
                        if(second.getChildren().size() > 0)
                            str += second.getData() + "(";
                        str += this.displayBody(second);
                        if(second.getChildren().size() > 0)
                            str += ")";
                    }
                    break;
                default:
                    if(children.size() > 0) {
                        str += child.getData() + "(";
                        for (int j = 0; j < child.getChildren().size(); j++) {
                            str += this.displayBody(child.getChildren().get(j));
                            if (j < child.getChildren().size() - 1)
                                str += ", ";
                        }
                        str += ")";
                    }
                    else {
                        str += child.getData();
                    }
                    break;
            }
            if (i < root.getChildren().size() - 1) {
                str += ", ";
            }
            i++;
        }

        return str;
    }

    public static Predicate app() {
        List<String> inputArguments = new ArrayList<>(Arrays.asList("X", "Y")),
                outputArguments = new ArrayList<>(Arrays.asList("Z"));

        List<Tree<String>> trees = new ArrayList<>();

        Tree<String> tree1 = new Tree<>("");
        Node<String> node1 = tree1.getRoot();

        node1.addChild(Predicate.DEC, Arrays.asList("X","nil")).addChild(Predicate.ASSIGN, Arrays.asList("Z","Y"));
        trees.add(tree1);

        Tree<String> tree2 = new Tree<>("");
        Node<String> node2 = tree2.getRoot();
        node2.add(Predicate.DEC, Arrays.asList(new Node<>("X"), new Node<>("cons", Arrays.asList(new Node<>("E"), new Node<>("Es")))))
                .addChild("app", Arrays.asList("Es", "Y", "Zs"))
                .add(Predicate.GETS, Arrays.asList(new Node<>("Z"), new Node<>("cons", Arrays.asList(new Node<>("E"), new Node<>("Zs")))));
        trees.add(tree2);

        return new Predicate("app", inputArguments, outputArguments, trees);
    }

    public static Predicate tapp() {
        List<String> inputArguments = new ArrayList<>(Arrays.asList("L1", "L2", "L3")),
                outputArguments = new ArrayList<>(Arrays.asList("R"));

        List<Tree<String>> trees = new ArrayList<>();

        Tree<String> tree1 = new Tree<>("");
        Node<String> node1 = tree1.getRoot();

        node1.addChild("app", Arrays.asList("L1","L2","L12"))
                .addChild("app", Arrays.asList("L12","L3", "R"));
        trees.add(tree1);

        return new Predicate("tapp", inputArguments, outputArguments, trees);
    }

    public static Predicate tapp2() {
        List<String> inputArguments = new ArrayList<>(Arrays.asList("A", "B", "C")),
                outputArguments = new ArrayList<>(Arrays.asList("D"));

        List<Tree<String>> trees = new ArrayList<>();

        Tree<String> tree1 = new Tree<>("");
        Node<String> node1 = tree1.getRoot();

        node1.addChild(Predicate.DEC, Arrays.asList("A", "nil"))
                .addChild(Predicate.DEC, Arrays.asList("B", "nil"))
                .addChild(Predicate.ASSIGN, Arrays.asList("D", "C"));

        Tree<String> tree2 = new Tree<>("");
        Node<String> node2 = tree2.getRoot();
        node2.addChild(Predicate.DEC, Arrays.asList("A", "nil"))
                .add(Predicate.DEC, Arrays.asList(new Node<>("B"), new Node<>("cons", Arrays.asList(new Node<>("B1"), new Node<>("B2")))))
                .addChild("tapp2", Arrays.asList("A","B2","C", "D1"))
                .add(Predicate.GETS, Arrays.asList(new Node<>("D"), new Node<>("cons", Arrays.asList(new Node<>("B1"), new Node<>("D1")))))
        ;

        Tree<String> tree3 = new Tree<>("");
        Node<String> node3 = tree3.getRoot();
        node3.add(Predicate.DEC, Arrays.asList(new Node<>("A"), new Node<>("cons", Arrays.asList(new Node<>("A1"), new Node<>("As")))))
            .addChild("tapp2", Arrays.asList("As", "B", "C", "D1"))
            .add(Predicate.GETS, Arrays.asList(new Node<>("D"), new Node<>("cons", Arrays.asList(new Node<>("A1"), new Node<>("D1")))))
        ;

        trees.addAll(Arrays.asList(tree1,tree2,tree3));
        return new Predicate("tapp2", inputArguments, outputArguments, trees);
    }

    public static Predicate qapp() {
        List<String> inputArguments = new ArrayList<>(Arrays.asList("F", "G", "H", "I")),
                outputArguments = new ArrayList<>(Arrays.asList("J"));

        List<Tree<String>> trees = new ArrayList<>();

        Tree<String> tree1 = new Tree<>("");
        Node<String> node1 = tree1.getRoot();

        node1.addChild("app", Arrays.asList("F", "G", "FG"))
                .addChild("app", Arrays.asList("FG", "H", "FGH"))
                .addChild("app", Arrays.asList("FGH", "I", "J"));

        trees.addAll(Arrays.asList(tree1));
        return new Predicate("qapp", inputArguments, outputArguments, trees);
    }

    public static Predicate qapp2() {
        List<String> inputArguments = new ArrayList<>(Arrays.asList("F1", "G1", "H1", "I1")),
                outputArguments = new ArrayList<>(Arrays.asList("J1"));

        List<Tree<String>> trees = new ArrayList<>();

        Tree<String> tree1 = new Tree<>("");
        Node<String> node1 = tree1.getRoot();

        node1.addChild("concat", Arrays.asList("G1", "F1","FG1"))
                .addChild("concat", Arrays.asList("FG1","H1", "FGH1"))
                .addChild("concat", Arrays.asList("I1", "FGH1", "J1"));

        trees.addAll(Arrays.asList(tree1));
        return new Predicate("qapp2", inputArguments, outputArguments, trees);
    }

    public static Predicate concat() {
        List<String> inputArguments = new ArrayList<>(Arrays.asList("V1", "V2")),
                outputArguments = new ArrayList<>(Arrays.asList("V3"));

        List<Tree<String>> trees = new ArrayList<>();
        Tree<String> tree1 = new Tree<>("");
        Node<String> node1 = tree1.getRoot();

        node1.addChild(Predicate.DEC, Arrays.asList("V2", "nil")).addChild(Predicate.ASSIGN, Arrays.asList("V3", "V1"));

        Tree<String> tree2 = new Tree<>("");
        Node<String> node2 = tree2.getRoot();
        node2.add(Predicate.DEC, Arrays.asList(new Node<>("V2"), new Node<>("cons", Arrays.asList(new Node<>("V21"), new Node<>("V2s")))))
                .addChild("concat", Arrays.asList("V1", "V2s", "V3s"))
                .add(Predicate.GETS, Arrays.asList(new Node<>("V3"), new Node<>("cons", Arrays.asList(new Node<>("V21"), new Node<>("V3s")))));

        trees.addAll(Arrays.asList(tree2, tree1));
        return new Predicate("concat", inputArguments, outputArguments, trees);
    }
}
