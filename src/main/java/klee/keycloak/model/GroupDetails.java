package klee.keycloak.model;

import java.util.List;
import java.util.Map;

import org.keycloak.models.GroupModel;

public class GroupDetails {

    private Map<String, List<String>> attributes;
    private String id;
    private String name;

    public GroupDetails(GroupModel model) {
        this.attributes = model.getAttributes();
        this.id = model.getId();
        this.name = model.getName();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }
}
