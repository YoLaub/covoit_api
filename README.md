# Application Mobile de Covoiturage 

## Présentation du projet

L'Application Mobile de Covoiturage est une solution développée pour le GRETA afin de permettre aux élèves de partager leurs trajets face à la hausse des prix des carburants. Le projet se compose d'une API REST backend et d'une application mobile frontend offrant un service complet de covoiturage.

## Objectifs

Le projet vise à créer une plateforme permettant aux utilisateurs de :

- Proposer des trajets en tant que conducteur
- Rechercher et réserver des places disponibles
- Communiquer avec les autres utilisateurs via email
- Gérer leurs réservations et trajets publiés


## Architecture technique

### Backend - API REST

L'API REST est développée avec **Spring Boot** et expose l'ensemble des fonctionnalités via des routes RESTful. Elle assure l'authentification par token, la gestion des trajets, des réservations et l'intégration avec l'API Brevo pour l'envoi d'emails.

### Base de données

La base de données PostgreSQL comprend les tables principales suivantes  :

- **user_profil** : gestion des utilisateurs et authentification
- **route** : trajets proposés avec coordonnées géographiques
- **vehicule** : informations sur les véhicules des conducteurs
- **user_route** : relation entre utilisateurs et trajets (conducteur/passager)
- **route_location** : relation entre les routes et les locations
- **notification** : système de notifications
- **brand** et **model** : marques et modèles de véhicules


### Fonctionnalités principales

**Authentification**

- Inscription et connexion avec token de session
- Gestion du profil utilisateur

**Gestion des trajets**

- Publication de trajets avec ville de départ/arrivée, date, heure et distance
- Recherche multicritères (villes, date)
- Consultation des détails avec liste des passagers

**Réservations**

- Réservation de places avec vérification de disponibilité
- Annulation de réservations
- Visualisation de ses trajets réservés

**Communication**

- Envoi d'emails aux conducteurs via API Brevo
- Notifications automatiques lors des réservations/annulations


## Technologies utilisées

- **Backend** : Spring
- **Base de données** : PostgreSQL
- **API externe** : Brevo (envoi d'emails)
- **Tests** :  Spring Boot Test
- **CI/CD** : GitLab
- **Qualité de code** : SonarQube
- **Déploiement** :  WAR (Spring)


## Gestion de projet

Le projet utilise **Git_Project** pour la planification des tâches et le suivi du rétro-planning.

***