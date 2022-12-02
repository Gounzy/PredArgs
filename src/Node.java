import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node<T> {
    private T data;
    private Node<T> parent;
    private List<Node<T>> children;

    public Node(T data) {
        this.data = data;
        this.children = new ArrayList<Node<T>>();
    }

    public Node(T data, List<Node<T>> children) {
        this.data = data;
        this.children = children;
    }

    public T getData() {
        return data;
    }

    public Node<T> setData(T data) {
        this.data = data;
        return this;
    }

    public List<Node<T>> getChildren() {
        return this.children;
    }
    public boolean hasChildren() { return !this.children.isEmpty(); }

    public Node<T> addChild(T data, List<T> children) {
        Node<T> node = new Node<>(data);
        for(T child : children) {
            node.addChild(child, new ArrayList<>());
        }
        this.children.add(node);
        return this;
    }

    public Node<T> add(T data, List<Node<T>> children) {
        Node<T> node = new Node<>(data);
        for(Node<T> child : children) {
            node.addChild(child);
        }
        this.children.add(node);
        return this;
    }

    public Node<T> addChild(Node<T> child) {
        this.children.add(child);
        return this;
    }

    public Set<String> getVariables() {
        Set<String> list = new HashSet<>();
        if(this.getChildren().size() == 0) {
            String str = this.data.toString();
            if(Character.isUpperCase(str.charAt(0))) {
                list.add(str);
            }
        }
        else {
            for(Node<T> child: this.getChildren()) {
                Set<String> childVariables = child.getVariables();
                list.addAll(childVariables);
            }
        }
        return list;
    }

    public List<String> getVariablesList() {
        List<String> list = new ArrayList<>();
        if(this.getChildren().size() == 0) {
            String str = this.data.toString();
            if(Character.isUpperCase(str.charAt(0))) {
                list.add(str);
            }
        }
        else {
            for(Node<T> child: this.getChildren()) {
                List<String> childVariables = child.getVariablesList();
                list.addAll(childVariables);
            }
        }
        return list;
    }

}