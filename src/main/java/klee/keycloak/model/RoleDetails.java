package klee.keycloak.model;

import java.util.List;
import java.util.Map;

import org.keycloak.models.RoleModel;

public class RoleDetails {

    private Map<String, List<String>> attributes;
    private String id;
    private String name;
    private String containerId;
    private String description;

    public RoleDetails(RoleModel model) {
        this.attributes = model.getAttributes();
        this.id = model.getId();
        this.name = model.getName();
        this.containerId = model.getContainerId();
        this.description = model.getDescription();
    }

    public String getDescription() {
        return description;
    }

    public String getContainerId() {
        return containerId;
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
