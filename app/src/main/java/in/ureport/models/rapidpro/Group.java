package in.ureport.models.rapidpro;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class Group {

    private Integer group;

    private String uuid;

    private String name;

    private Integer size;

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Group{" +
                "group=" + group +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                '}';
    }
}
