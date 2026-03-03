import java.time.LocalDate;

public class Stolen {
    public int assetId;
    public String assetName;
    public Integer price;
    public boolean isStolen;
    public int ownerId;
    public String ownerFirstName;
    public String ownerLastName;
    public LocalDate ownerBirthday;
    public Integer thiefId;
    public String thiefFirstName;
    public String thiefLastName;
    public LocalDate thiefBirthday;

    public String getOwnerFullName() {
        return ownerFirstName + " " + ownerLastName;
    }

    public String getThiefFullName() {
        return (thiefFirstName != null) ? thiefFirstName + " " + thiefLastName : null;
    }
}