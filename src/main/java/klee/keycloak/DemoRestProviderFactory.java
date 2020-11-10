package klee.keycloak;

import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class DemoRestProviderFactory implements RealmResourceProviderFactory {
	// https://<your-keycloak-site>/auth/realms/master/<ID below>
    public static final String ID = "custom";

    public RealmResourceProvider create(KeycloakSession session) {
        return new DemoRestProvider(session);
    }

    public void init(Scope config) {
    }

    public void postInit(KeycloakSessionFactory factory) {
    }

    public void close() {
    }

    public String getId() {
        return ID;
    }

}
