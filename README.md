# Système de Gestion d'Inventaire

Un système de gestion d'inventaire distribué basé sur Java utilisant JavaFX pour l'interface client et RMI pour la communication client-serveur.

## Fonctionnalités

- Contrôle d'accès basé sur les rôles (Admin/Utilisateur)
- Gestion des produits (opérations CRUD)
- Gestion des employés (opérations CRUD)
- Authentification sécurisée
- Mises à jour en temps réel
- Persistance des données avec MySQL
- Pool de connexions avec HikariCP
- Cryptage des mots de passe avec BCrypt

## Prérequis

- Java JDK 17 ou supérieur
- MySQL 8.0 ou supérieur
- Maven 3.6 ou supérieur
- Ports disponibles :
    - 1099 (Registre RMI - configurable)
    - 3306 (MySQL)

## Justification des Choix Technologiques

### JavaFX
- Interface graphique riche et moderne
- Intégration native avec Java
- Support FXML pour séparer l'interface de la logique
- Architecture MVC claire avec les contrôleurs FXML
- Nombreux composants prêts à l'emploi

### RMI (Remote Method Invocation)
- Communication client-serveur typée et sécurisée
- Intégration naturelle avec Java
- Gestion transparente des objets distants
- Pas besoin de protocole de communication personnalisé
- Support natif de la sérialisation des objets Java

### MySQL
- Système de gestion de base de données robuste et éprouvé
- Excellentes performances pour les opérations CRUD
- Grande communauté et support étendu
- Intégration simple avec Java via JDBC
- Gestion efficace des transactions

### HikariCP
- Pool de connexions haute performance
- Configuration minimale requise
- Fiabilité et stabilité reconnues
- Surveillance et métriques intégrées
- Gestion optimisée des ressources de connexion

### BCrypt
- Algorithme de hachage sécurisé pour les mots de passe
- Protection contre les attaques par force brute
- Intégration du salt automatiquement
- Standard de l'industrie pour la sécurité des mots de passe
- Facilité d'utilisation avec une API simple

### Maven
- Gestion des dépendances centralisée
- Structure de projet standardisée
- Cycles de build automatisés
- Support multi-modules
- Large écosystème de plugins

## Démarrage Rapide

1. Cloner le dépôt :
```bash
git clone https://github.com/yzaza/inventory-management.git
cd inventory-management
```

2. Configurer la base de données selon l'une des options ci-dessous (voir section Configuration)

3. Compiler le projet :
```bash
mvn clean package
```

4. Démarrer le serveur :
```bash
java -jar server/target/server.jar
```

5. Démarrer le client :
```bash
java -jar client/target/client.jar
```

## Configuration

### Configuration de la Base de Données

Vous avez deux options pour configurer la base de données :

#### Option 1 : Configuration Automatique (Par défaut)
Dans le fichier `database.properties`, activez la création automatique de la base de données et le chargement des données de test :
```properties
# Configuration Base de Données
db.url=jdbc:mysql://localhost:3306/inventory_db
db.username=root
db.password=votre_mot_de_passe
db.driver=com.mysql.cj.jdbc.Driver

# Port RMI
rmi.port=1099

# Options d'Initialisation de la Base de Données
db.init.createDatabase=true    # Crée automatiquement la base de données
db.init.loadTestData=true      # Charge les données de test automatiquement
```

#### Option 2 : Configuration Manuelle
1. Créez manuellement la base de données MySQL
2. Exécutez les scripts SQL depuis `server/src/main/resources/` :
    - `schema.sql` pour créer les tables
    - `data.sql` pour charger les données de test (optionnel)
3. Configurez `database.properties` avec `createDatabase` et `loadTestData` à `false` :
```properties
db.init.createDatabase=false
db.init.loadTestData=false
```

## Structure du Projet

```
project/
├── client/          # Application client JavaFX
├── common/          # Interfaces et modèles partagés
└── server/          # Implémentation serveur RMI
```

## Compilation depuis les Sources

Pour compiler les modules individuels :

```bash
# Compiler tout le projet
mvn clean package

```

## Identifiants par Défaut

- Utilisateur Admin :
    - Nom d'utilisateur : admin
    - Mot de passe : 123
- Utilisateur Standard :
    - Nom d'utilisateur : employee1
    - Mot de passe : 123

## Détails Techniques

- **Client** : JavaFX avec FXML pour l'interface utilisateur
- **Serveur** : Java RMI
- **Base de données** : MySQL avec pool de connexions HikariCP
- **Outil de build** : Maven
- **Sécurité** : Hachage des mots de passe avec BCrypt

## Contribution

1. Forker le dépôt
2. Créer votre branche de fonctionnalité (`git checkout -b feature/Nouvellefonctionnalité`)
3. Commiter vos changements (`git commit -m 'Ajout d'une nouvelle fonctionnalité'`)
4. Pousser vers la branche (`git push origin feature/Nouvellefonctionnalité`)
5. Ouvrir une Pull Request

## Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Dépannage

1. **Problèmes de Connexion RMI** :
    - Vérifier que le serveur est en cours d'exécution
    - Vérifier si le port 1099 est disponible
    - Vérifier les paramètres du pare-feu

2. **Problèmes de Connexion à la Base de Données** :
    - Vérifier que MySQL est en cours d'exécution
    - Vérifier la configuration dans database.properties
    - S'assurer que l'utilisateur de la base de données a les privilèges appropriés

3. **Problèmes JavaFX** :
    - Vérifier que la version de Java est 17 ou supérieure
    - Vérifier si les modules JavaFX sont inclus dans le path
