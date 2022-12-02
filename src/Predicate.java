import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Predicate {

    public static final String DEC = "=>",
                         GETS = "<=",
                         ASSIGN = ":=",
                         TEST = "<->";
    private String name;
    private List<String> arguments;
    private List<Tree<String>> bodies;
    private int nbInputArguments = 0;

    private PredicateProfile profile;

    public Predicate(String name, List<String> inputArguments, List<String> outputArguments, List<Tree<String>> bodies) {
        this.arguments = inputArguments;
        this.nbInputArguments = inputArguments.size();
        this.arguments.addAll(outputArguments);
        this.name = name;
        this.bodies = bodies;
        this.profile = new PredicateProfile(this.arguments);
    }

    public String toString() {
        String str = "";
        for (Tree<String> body : this.bodies) {
            str += this.name + "(" + this.displayArguments() + ")" + " <- " + this.displayBody(body) + "\n";
        }
        return str;
    }

    public String getName() {
        return name;
    }

    public int getArity() {
        return this.arguments.size();
    }

    public int getNbInputArguments() {
        return this.nbInputArguments;
    }

    public List<String> extractInputVariables(List<String> variables) {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < this.nbInputArguments; i++) {
            list.add(variables.get(i));
        }
        return list;
    }

    public List<String> extractOutputVariables(List<String> variables) {
        List<String> list = new ArrayList<>();
        for(int i = this.nbInputArguments; i < this.getArity(); i++) {
            list.add(variables.get(i));
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
                    if(child.getChildren().size() > 1) {
                        Node<String> first = children.get(0);
                        Node<String> second = children.get(1);
                        str += first.getData() + " " + child.getData() +  " " + second.getData();
                    }
                    break;
                case DEC:
                case GETS:
                    if(child.getChildren().size() > 1) {
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
                    if(child.getChildren().size() > 0) {
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
}
