# Structure de la Base de Données - ATEX Scanner

Ce document décrit l'organisation de la base de données locale (Room) pour correspondre au rapport Excel final.

## Schéma Relationnel
`Site` -> `ZoneATEX` -> `Equipement` -> `Inspection`

---

### 1. Table : `sites`
| Champ | Type | Description |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `nom` | String | Nom du site client. |
| `dateCreation` | Long | Date de création. |

---

### 2. Table : `zones_atex` (Section LOCALISATION & EXIGENCE)
| Champ | Type | Exemple |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `siteId` | Long (FK) | Lien vers le Site. |
| `nom` | String | Gazomètre aciérie |
| `exigenceClassification` | String | 2 |
| `exigenceGroupe` | String | IIB |
| `exigenceTemperature` | String | T1 |

---

### 3. Table : `equipements` (Section MATÉRIEL & MARQUAGE)
| Champ | Type | Exemple / Description |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `zoneId` | Long (FK) | Lien vers la Zone. |
| `emplacement1` | String | Au sol |
| `emplacement2` | String | Proximité gazomètre au sud |
| `tagNumber` | String | N° TAG |
| `typeMateriel` | String | Coffret électrique |
| `fabricant` | String | TECHNOR |
| `numeroSerie` | String | 112342 54 |
| `indiceProtection`| String | IP66 |
| `anneeFabrication`| String | 2002 |
| `dirGroupe` | String | Directives: II |
| `dirCategorie` | String | Directives: 2 |
| `dirAtmosphere` | String | Directives: G |
| `normeProtection` | String | Normes: de |
| `normeGroupe` | String | Normes: IIB |
| `normeTemperature`| String | Normes: T4 |
| `normeEPL` | String | Normes: Gb |
| `numeroAttestation`| String | LCIE 00 ATEX 6044 X |
| `photoPlaquePath` | String | Chemin de la photo. |

---

### 4. Table : `inspections` (Section VERDICT)
| Champ | Type | Exemple |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `equipementId` | Long (FK) | Lien vers l'Équipement. |
| `date` | Long | Date de l'inspection. |
| `statusConformite` | String | C, NA, NC, NE |
| `typeObservation` | String | Marquage |
| `commentaires` | String | Notes complémentaires. |
