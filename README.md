# Introduction
keycloak-rest-api est une application d'exemple pour créer un plugin keycloak (en .jar) exposant une API REST custom et non protégée (accès anonyme).

Une des APIs reçoit un appel paramétré de ScriptRunner for Jira Cloud (addUserToGroup on user creation) et boucle sur les groupes de l'utilisateur matché pour les lui ajouter côté Jira Cloud, lors de sa création. 

Ce plugin retourne aussi des informations d'users (first name / last name / user name ) présents dans le Keycloak, sous un format sans dépendances cycliques.

Le Keycloak utilisé étant dans un container docker, pour déployer le plugin, il suffit de : 

Construire le fichier .jar sur le serveur
```mvn clean install```

Puis le déplacer dans le répertoire de déploiement de Keycloak  : 

```sudo docker cp target/keycloak-rest-api-1.0.jar <container-name>:/opt/jboss/keycloak/standalone/deployments/keycloak-rest-api-1.0.jar```

Il suffit ensuite d'entrer l'URL dans Chrome our effectuer un simple GET et récupérer la liste des utilisateurs du Keycloak. Par exemple :

```https://<your-keycloak-site-basename>/auth/realms/master/<ID in DemoRestProviderFactory.java>/users```

Cela retournera un JSON
