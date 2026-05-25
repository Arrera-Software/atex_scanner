# Contexte du Projet : ATEX Scanner

## Objectif principal
Créer un outil métier Android pour les inspecteurs en zone ATEX visant à automatiser et sécuriser la rédaction des rapports d'adéquation.
L'application doit :
- Supprimer la double saisie (terrain puis bureau).
- Réduire les erreurs de saisie grâce à l'OCR.
- Générer un rapport Excel conforme au modèle métier (Colonnes : Localisation, Zone ATEX client, Matériel, Marquage, Verdict).
- Conserver les photos des plaques signalétiques comme preuves.

## Stack Technique & Règles de Développement

- **Langage** : Kotlin.
- **Interface Utilisateur** : Jetpack Compose exclusivement (Material 3).
- **Architecture de données** :
    - **Room** (Single Source of Truth).
    - Structure hiérarchique : `Site` > `ZoneAtex` > `Equipement` > `Inspection`.
- **Appareil Photo** : CameraX.
- **OCR** : Google ML Kit (Text Recognition) pour l'extraction des données de plaques.
- **Génération Excel** : Apache POI (Formatage conditionnel : Vert/Rouge pour le verdict).
- **Gestion des Fichiers** : Scoped Storage + `Intent.ACTION_CREATE_DOCUMENT`.

## Structure de Données & Métier (Alignée sur Rapport Excel)

1. **Localisation & Exigences (ZoneAtex)** :
    - Nom du lieu, Classification client (0, 1, 2...), Groupe Gaz, Classe de Température.
2. **Identification Matériel (Equipement)** :
    - Emplacement détaillé, N° TAG (obligatoire), Type, Fabricant, S/N, IP, Année.
3. **Marquage Technique (Equipement)** :
    - **Directives** : Groupe (II), Catégorie (2), Atmosphère (G/D).
    - **Normes** : Protection (ex: db), Groupe (IIB), Température (T4), EPL (Gb).
    - N° d'attestation d'examen de type.
4. **Verdict (Inspection)** :
    - Status : **C** (Conforme), **NA** (Non Applicable), **NC** (Non Conforme), **NE** (Non Examiné).
    - Type d'observation (Marquage, État, etc.).

## Fonctionnalités Clés

1. **Gestion Hiérarchique** : Navigation fluide de la liste des sites vers les zones, puis vers les équipements.
2. **Scan intelligent & Récupération de données (Priorité Actuelle)** :
    - Capture photo + OCR.
    - **Focus actuel** : Extraction fiable et structurée de toutes les informations des plaques signalétiques (TAG, Fabricant, S/N, Marquages Directives et Normes).
    - **Évolution future** : L'algorithme de comparaison automatique (Verdict C/NC) sera implémenté dans une phase ultérieure. Pour le moment, l'inspecteur saisit ou valide les données extraites.
3. **Export Professionnel** : Génération instantanée du fichier `.xlsx` respectant strictement le colonnage du tableau de référence.

## Contraintes d'Optimisation
- **Performance** : Gestion rigoureuse de la mémoire (RAM 4-6 Go) lors de l'usage simultané de la caméra et de l'OCR.
- **Qualité OCR** : Configuration CameraX optimisée pour la macro (mise au point sur plaques signalétiques).
- **Version Android** : Min SDK 26, Target SDK 35/36.
