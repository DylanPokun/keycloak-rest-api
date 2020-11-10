package klee.keycloak.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.keycloak.models.UserModel;

public class UserDetails {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Map<String, List<String>> attributes;
    private Set<GroupDetails> groups;
    private Set<RoleDetails> roles;
    private String id;

    public UserDetails(UserModel model) {
        this.userName = model.getUsername();
        this.firstName = model.getFirstName();
        this.lastName = model.getLastName();
        this.email = model.getEmail();
        this.attributes = model.getAttributes();
        this.groups = model.getGroups().stream().map(e -> new GroupDetails(e)).collect(Collectors.toSet());
        this.roles = model.getRoleMappings().stream().map(e -> new RoleDetails(e)).collect(Collectors.toSet());
        this.id = model.getId();
    }

    public String getId() {
        return id;
    }

    public Set<RoleDetails> getRoles() {
        return roles;
    }

    public Set<GroupDetails> getGroups() {
        return groups;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastName() {
        return lastName;
    }


}
