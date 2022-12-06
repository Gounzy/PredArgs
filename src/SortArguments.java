import java.util.Comparator;

public class SortArguments  implements Comparator<ArgumentDescription> {
    public int compare(ArgumentDescription a, ArgumentDescription b)
    {
        if(a.isInput() && b.isOutput()) {
            return 1;
        }
        else if(a.isOutput() && b.isInput()) {
            return -1;
        }
        else if(a.isInput() && b.isInput()) {
            // two inputs
            return 0;
        }
        else {
            // two outputs
            return 0;
        }
    }
}

