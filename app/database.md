# Structure de la Base de Données - ATEX Scanner

Ce document décrit l'organisation de la base de données locale (Room) pour correspondre au rapport Excel final et au fonctionnement de l'application.

## Schéma Relationnel
`Site` -> `ZoneATEX` -> `Equipement` -> `Inspection`

---

### 1. Table : `sites`
| Champ | Type | Description |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `nom` | String | Nom du site client. |
| `dateCreation` | Long | Date de création (Timestamp). |

---

### 2. Table : `zones_atex` (Section LOCALISATION & EXIGENCE)
| Champ | Type | Options / Exemples |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `siteId` | Long (FK) | Lien vers le Site. |
| `nom` | String | Nom de la zone (ex: Local Chargeur). |
| `section` | String | Zone géographique large. |
| `sousSection` | String | Emplacement précis. |
| `typeAtmosphere` | String | Gaz / Poussière. |
| `exigenceClassification` | String | **Gaz**: 0, 1, 2 \| **Poussière**: 20, 21, 22. |
| `exigenceGroupe` | String | **Gaz**: IIA, IIB, IIC \| **Poussière**: IIIA, IIIB, IIIC. |
| `exigenceTemperature` | String | **Gaz**: T1 à T6 \| **Poussière**: Valeur en °C. |

---

### 3. Table : `equipements` (Section MATÉRIEL & MARQUAGE)
| Champ | Type | Options / Exemples |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `zoneId` | Long (FK) | Lien vers la Zone. |
| `tagNumber` | String | N° TAG unique du matériel. |
| `fabricant` | String | Marque / Fabricant. |
| `typeMateriel` | String | Modèle / Type. |
| `numeroSerie` | String | S/N. |
| `indiceProtection`| String | Préfixe "IP" obligatoire + chiffres (ex: IP66). |
| `anneeFabrication`| String | Année (ex: 2023). |
| `dirGroupe` | String | Directives : **I**, **II**. |
| `dirCategorie` | String | Directives : **1**, **2**, **3**. |
| `dirAtmosphere` | String | Directives : **G**, **D**, **GD**. |
| `normeProtection` | String | Modes : **d, e, m, ia, ib, ic, p, o, h, c, nA, n, q, nR, b, K**. |
| `normeGroupe` | String | Normes : **II, IIA, IIB, IIC, IIIA, IIIB, IIIC**. |
| `normeTemperature`| String | **T1 à T6** ou valeur en **°C**. |
| `normeEPL` | String | Niveau : **Ga, Gb, Gc, Da, Db, Dc**. |
| `numeroAttestation`| String | N° de certificat (Saisie forcée en MAJUSCULES). |
| `photoPlaquePath` | String | URI / Chemin local de la photo de la plaque. |

---

### 4. Table : `inspections` (Section ADÉQUATION & REMARQUES)
| Champ | Type | Description |
| :--- | :--- | :--- |
| `id` | Long (PK) | Identifiant unique. |
| `equipementId` | Long (FK) | Lien vers l'Équipement. |
| `date` | Long | Date de l'inspection (Timestamp). |
| `inspecteurNom` | String | Nom du technicien. |
| `conformiteAtex` | String | OUI / NON. |
| `commentaires` | String | Observations générales. |
| `assistanceMiseConformite` | String | Conseils pour correction. |
| `marquageRemarques` | String | Remarques sur le marquage. |
| `presseEtoupes` | String | État des presse-étoupes / bouchons. |
| `miseAlaTerre` | String | État de la mise à la terre. |
| `gainesCables` | String | État des gaines et câbles. |
| `boitierEnveloppe` | String | État du boîtier / enveloppe. |
| `autres` | String | Autres points de contrôle. |

---

## Logique d'Exportation Excel (Rapport Global)
L'exportation se fait au niveau du **Site** et génère deux fichiers dans `Documents/ATEX_Scanner/` :
1.  **Fichier Excel (`.xlsx`)** :
    *   **Localisation** : Section, Sous-section et Nom Zone hérités de la zone parente.
    *   **Zone ATEX** : Découpée en 3 colonnes (**Classification** | **Groupe** | **Température**).
    *   **Marquage Directives** : Découpé en 3 colonnes (**Groupe** | **Catégorie** | **Atmosphère**).
    *   **Marquage Normes** : Découpé en 4 colonnes (**Prot** | **Groupe** | **Température** | **EPL**).
    *   **Tous les équipements** sont exportés, avec ou sans photo.
2.  **Fichier ZIP (`.zip`)** :
    *   Contient toutes les photos des plaques signalétiques du site.
    *   Chaque photo est renommée selon le **TAG** de l'équipement (ex: `TAG123.jpg`).
