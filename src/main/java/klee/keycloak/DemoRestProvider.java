package klee.keycloak;

import klee.keycloak.model.GroupDetails;
import klee.keycloak.model.UserDetails;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.utils.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DemoRestProvider implements RealmResourceProvider {
	public static final Logger logger = LoggerFactory.getLogger(DemoRestProvider.class);

	private final KeycloakSession session;

	public DemoRestProvider(KeycloakSession session) {
		this.session = session;
	}

	public void close() {

	}

	public Object getResource() {
		return this;
	}
	
	@GET
	@Path("alive")
	@NoCache
	@Produces({ MediaType.TEXT_PLAIN_UTF_8 })
	@Encoded
	public String getAlive() {
		return "I AM ALIVE";
	}
	
	@GET
	@Path("hello")
	@NoCache
	@Produces({ MediaType.TEXT_PLAIN_UTF_8 })
	@Encoded
	/** Ceci permet d'illustrer le passage de paramètres */
	public String getHello(@Context HttpServletRequest request) {
		String name = request.getParameter("name");
		return "Hello " + name + "!";
	}

	@GET
	@Path("users")
	@NoCache
	@Produces({ MediaType.APPLICATION_JSON })
	@Encoded
	/** Les modèles sont cycliques, d'où les classes "Details" vs les classes "Model" */
	public List<UserDetails> getUsers() {
		List<UserModel> userModel = session.users().getUsers(session.getContext().getRealm());
		return userModel.stream().map(e -> new UserDetails(e)).collect(Collectors.toList());
	}

	@GET
	@Path("addusertogroup")
	@NoCache
	@Produces({ MediaType.APPLICATION_JSON })
	@Encoded
	/** Cette fonction est appelée par ScriptRunner en passant un displayName et un accountId */
	public int addUserToGroup(@Context HttpServletRequest request) {
		// Init
		List<UserModel> userModel = session.users().getUsers(session.getContext().getRealm());
		String displayName = request.getParameter("displayName");
		String accountId = request.getParameter("accountId");
		
		// Récupérer le bon utilisateur (par displayName car ScriptRunner ne récupère pas l'email). En cas 
		// d'appel par batch, celui-ci peut passer un e-mail en paramètre et ainsi simplifier ce filtre
		List<UserDetails> filteredUsers = userModel.stream()
				.filter(u -> displayName.equals(u.getFirstName() + " " + u.getLastName())).map(e -> new UserDetails(e))
				.collect(Collectors.toList());
		// /!\ On ne fait pas attention à la détection d'homonymes
		// On prend en dur l'élément 0 de la liste dans le cadre de ce POC
		UserDetails currentUser = filteredUsers.get(0);
		// On boucle sur les groupes pour ajouter 1 à 1 l'utilisateur aux groupes correspondants
		// dans le Cloud d'Atlassian
		HttpClient httpclient = HttpClients.createDefault();
		for (GroupDetails g : currentUser.getGroups()) {
			try {
				// Init du post
				HttpPost post = new HttpPost(
						"https://<your-site>.atlassian.net/rest/api/3/group/user?groupname=" + g.getName());

				// Headers
				post.setHeader("Content-Type", "application/json");
				post.setHeader("Accept", "application/json");
				post.setHeader("Authorization",
						"Basic 'your email:apikey encoded in base 64'");
				
				// JSON Body
			    String jsonRequestBody = "{\"accountId\":\""+accountId+"\"} ";
			    HttpEntity entity = new ByteArrayEntity(jsonRequestBody.getBytes("UTF-8"));
			    post.setEntity(entity);
			    
				// Exec
				httpclient.execute(post);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				// Interruption du traitement
				return HttpStatus.SC_METHOD_FAILURE;
			}
		}
		return HttpStatus.SC_OK;
	}
}
