# Structure de la Base de Données - ATEX Scanner

Ce document décrit l'organisation de la base de données locale (Room) utilisée pour stocker les informations d'inspection.

## Schéma Relationnel

L'application utilise une structure hiérarchique :
`Site` -> `ZoneATEX` -> `Equipement` -> `Inspection`

---

### 1. Table : `sites`
Représente le lieu géographique ou industriel de l'intervention.

| Champ | Type | Contrainte | Description |
| :--- | :--- | :--- | :--- |
| `id` | Long | PK, Auto | Identifiant unique auto-généré. |
| `nom` | String | Not Null | Nom du site (ex: "Usine Chimique Sud"). |
| `dateCreation` | Long | Default | Timestamp de création (System.currentTimeMillis()). |

---

### 2. Table : `zones_atex`
Définit les zones à risque au sein d'un site.

| Champ | Type | Contrainte | Description |
| :--- | :--- | :--- | :--- |
| `id` | Long | PK, Auto | Identifiant unique auto-généré. |
| `siteId` | Long | FK (Site) | Référence vers le site parent (Cascade Delete). |
| `nom` | String | Not Null | Nom de la zone (ex: "Local Charge Batterie"). |
| `classification`| String | Not Null | Zone 0, 1, 2, 20, 21, 22. |
| `groupeGaz` | String | Not Null | IIA, IIB, IIC. |
| `classeTemperature`| String | Not Null | T1 à T6. |

---

### 3. Table : `equipements`
Matériels inventoriés et scannés dans une zone.

| Champ | Type | Contrainte | Description |
| :--- | :--- | :--- | :--- |
| `id` | Long | PK, Auto | Identifiant unique auto-généré. |
| `zoneId` | Long | FK (Zone) | Référence vers la zone parente (Cascade Delete). |
| `nom` | String | Not Null | Nom du matériel (ex: "Moteur Pompe 1"). |
| `marque` | String | Not Null | Constructeur du matériel. |
| `modele` | String | Not Null | Modèle du matériel. |
| `numeroSerie` | String | Not Null | Identifiant unique du constructeur. |
| `protection` | String | Not Null | Mode de protection (ex: Ex db eb). |
| `groupeMateriel` | String | Not Null | Groupe d'explosion (ex: IIC). |
| `tempMateriel` | String | Not Null | Température max de surface (ex: T4). |
| `photoPlaquePath`| String | Nullable | Chemin local vers la photo de la plaque. |

---

### 4. Table : `inspections`
Historique des verdicts de conformité pour un équipement.

| Champ | Type | Contrainte | Description |
| :--- | :--- | :--- | :--- |
| `id` | Long | PK, Auto | Identifiant unique auto-généré. |
| `equipementId` | Long | FK (Equip) | Référence vers l'équipement (Cascade Delete). |
| `date` | Long | Default | Timestamp de l'inspection. |
| `inspecteurNom` | String | Not Null | Nom de la personne ayant réalisé l'audit. |
| `isConforme` | Boolean| Not Null | Résultat de l'algorithme d'adéquation. |
| `commentaires` | String | Nullable | Notes libres sur l'état du matériel. |

---

## Règles de Gestion
- **Single Source of Truth** : Room est l'unique source de vérité.
- **Réactivité** : Les accès en lecture utilisent des `Flow<T>` pour la mise à jour UI.
- **Intégrité** : L'utilisation de `CASCADE DELETE` garantit qu'aucune donnée orpheline ne reste en cas de suppression d'un parent.
