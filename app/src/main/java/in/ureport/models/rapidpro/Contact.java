package in.ureport.models.rapidpro;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class Contact {

    private String name;

    private String language;

    private List<String> groups;

    private List<String> urns;

    private Map<String, Object> fields;

    private Date modified_on;

    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getUrns() {
        return urns;
    }

    public void setUrns(List<String> urns) {
        this.urns = urns;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Date getModified_on() {
        return modified_on;
    }

    public void setModified_on(Date modified_on) {
        this.modified_on = modified_on;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", groups=" + groups +
                ", urns=" + urns +
                ", fields=" + fields +
                ", modified_on=" + modified_on +
                ", phone='" + phone + '\'' +
                '}';
    }
}
