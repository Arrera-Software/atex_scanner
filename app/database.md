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
| `section` | String | Section A |
| `sousSection` | String | Zone Sud |
| `typeAtmosphere` | String | Gaz / Poussière |
| `exigenceClassification` | String | 0, 1, 2 (Gaz) ou 20, 21, 22 (Poussière) |
| `exigenceGroupe` | String | IIB (Gaz) ou IIIC (Poussière) |
| `exigenceTemperature` | String | T4 (Gaz) ou 180°C (Poussière) |

---

### 3. Table : `equipements` (Section MATÉRIEL & MARQUAGE)
| Champ | Type | Exemple / Description |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `zoneId` | Long (FK) | Lien vers la Zone. |
| `emplacement1` | String | Section (Localisation) |
| `emplacement2` | String | Sous-section (Localisation) |
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
| `nature` | String | Électrique / Mécanique |
| `quantite` | String | 1 |
| `numeroAttestation`| String | LCIE 00 ATEX 6044 X |
| `photoPlaquePath` | String | Chemin de la photo. |

---

### 4. Table : `inspections` (Section ADÉQUATION & REMARQUES)
| Champ | Type | Description |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `equipementId` | Long (FK) | Lien vers l'Équipement. |
| `date` | Long | Date de l'inspection. |
| `inspecteurNom` | String | Nom du technicien. |
| `conformiteAtex` | String | OUI / NON |
| `commentaires` | String | Observations générales. |
| `assistanceMiseConformite` | String | Conseils pour correction. |
| `marquageRemarques` | String | Remarques sur le marquage. |
| `presseEtoupes` | String | État des presse-étoupes / bouchons. |
| `miseAlaTerre` | String | État de la mise à la terre. |
| `gainesCables` | String | État des gaines et câbles. |
| `boitierEnveloppe` | String | État du boîtier / enveloppe. |
| `autres` | String | Autres points de contrôle. |
