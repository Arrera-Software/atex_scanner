# Contexte du Projet : ATEX Scanner

## Objectif principal
Créer un outil métier Android pour les inspecteurs en zone ATEX visant à automatiser et sécuriser la rédaction des rapports d'adéquation.
L'application doit :
- Supprimer la double saisie (terrain puis bureau).
- Réduire les erreurs de saisie grâce à l'OCR.
- Générer un rapport Excel conforme au modèle métier détaillé.
- Conserver les photos des plaques signalétiques comme preuves dans un fichier ZIP.

## Stack Technique & Règles de Développement
- **Langage** : Kotlin.
- **Interface Utilisateur** : Jetpack Compose exclusivement (Material 3).
- **Architecture de données** :
    - **Room** : Structure hiérarchique : `Site` > `ZoneAtex` > `Equipement` > `Inspection`.
- **Appareil Photo** : CameraX.
- **OCR** : Google ML Kit (Text Recognition) pour l'extraction des données.
- **Génération Excel** : Apache POI.
- **Gestion des Fichiers** : MediaStore pour enregistrer dans le dossier public `Documents/ATEX_Scanner/`.

## Structure de Données & Métier (Alignée sur Rapport Excel)
L'exportation Excel est structurée selon un découpage technique précis :
1. **Localisation** : Section, Sous-section et Nom de Zone hérités de la Zone ATEX parente.
2. **Zone ATEX Client (3 colonnes)** : Classification | Groupe | Température.
3. **Identification Matériel** : N° TAG, Type, Fabricant, S/N, IP (préfixe "IP" auto), Année.
4. **Marquage Directives (3 colonnes)** : Groupe (I, II) | Catégorie (1, 2, 3) | Atmosphère (G, D, GD).
5. **Marquage Normes (4 colonnes)** : Protection (d, e, m...) | Groupe (II, IIA... IIIC) | Température (T1-T6 ou °C) | EPL (Ga, Gb...).
6. **Certification** : Numéro d'attestation (Saisie en MAJUSCULES forcée, historique des certificats du site proposé via des chips de suggestion).

## Fonctionnalités Clés Implémentées
1. **Navigation Hiérarchique** : Gestion complète des Sites, Zones et Équipements.
2. **Scan & OCR** : Extraction structurée des données de la plaque avec interface de validation.
3. **Export Global (Niveau Site)** :
    - Génération d'un **Excel unique** pour tout le site (tous les équipements inclus).
    - Génération d'un **ZIP de photos** renommées par TAG (ex: `TAG123.jpg`).
    - Traitement en arrière-plan (`Dispatchers.IO`) avec écran de chargement pour éviter les freezes.
4. **Sécurité & Ergonomie** :
    - Gestion automatique des permissions (Caméra, Stockage) au lancement.
    - Claviers techniques personnalisés (Prot, EPL).
    - Auto-formatage des champs (IP, Température en °C).

## Contraintes d'Optimisation
- **Performance** : Travail asynchrone pour les tâches lourdes (OCR, Export Excel).
- **Qualité de Saisie** : Minimiser les erreurs via des listes déroulantes techniques exhaustives et des suggestions basées sur l'historique du site.
- **Version Android** : Support du Scoped Storage (Android 10+) et compatibilité avec les versions antérieures pour l'écriture dans Documents.
