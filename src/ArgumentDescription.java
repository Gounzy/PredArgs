import java.util.Map;

public class ArgumentDescription {

    private ArgumentProfile profile;
    private int oldOrder = -1, newOrder = -1;

    public ArgumentDescription(ArgumentProfile ap) {
        this.profile = ap;
        this.oldOrder = ap.getNum();
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
}
