import java.util.*;

public class ArgumentProfile {
    private int num;
    private String name;

    private Map<ArgumentProfile, List<Operation>> osets;

    public ArgumentProfile(int num, String name) {
        this.num = num;
        this.name = name;
        this.osets = new HashMap<>();
    }

    public ArgumentProfile addOperation(Operation o, ArgumentProfile ap) {
        List<Operation> oplist;
        if(this.osets.get(ap) != null) {
            oplist = this.osets.get(ap);
        }
        else {
            oplist = new ArrayList<>();
            this.osets.put(ap, oplist);
        }
        if(!oplist.contains(o)) {
            oplist.add(o);
        }
        return this;
    }

    public String toString() {
        String str = "";// this.name + " (argument " + this.num +"): \n";
        Iterator<Map.Entry<ArgumentProfile, List<Operation>>> it = this.osets.entrySet().iterator();
        Map.Entry<ArgumentProfile, List<Operation>> oset;
        for(int k = 0; k < this.osets.entrySet().size(); k++) {
            oset = it.next();
            str += "({";
            for(int i = 0; i < oset.getValue().size(); i++) {
                str += oset.getValue().get(i).toString();
                if(i < oset.getValue().size() - 1) {
                    str += ", ";
                }
            }
            str += "}, " + oset.getKey().num + ")";
            if(k < this.osets.entrySet().size() - 1) {
                str += ", ";
            }
        }

        return str;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
