import java.util.Comparator;

public class SortArgumentProfiles  implements Comparator<ArgumentProfile> {

    public int compare(ArgumentProfile a, ArgumentProfile b) {
        return a.getNum() - b.getNum();
    }
}
