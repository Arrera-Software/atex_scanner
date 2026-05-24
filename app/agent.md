# Contexte du Projet : ATEX Scanner

## Objectif principal
Créer un outil métier Android pour les inspecteurs en zone ATEX visant à automatiser et sécuriser la rédaction des rapports d'adéquation.
L'application doit :
- Supprimer la double saisie (terrain puis bureau).
- Réduire les erreurs de copie.
- Générer un rapport Excel formaté rapidement.
- Conserver les photos prises lors de l'inspection.

## Stack Technique & Règles de Développement

- **Langage** : Kotlin.
- **Interface Utilisateur** : Jetpack Compose exclusivement.
- **Architecture de données** :
    - Utilisation de **Room** comme base de données locale.
    - C'est l'**unique source de vérité** (Single Source of Truth) pour l'application.
    - Tables requises : `Site`, `ZoneATEX`, `Equipement`, et `Inspection`.
- **Appareil Photo** : Utilisation de **CameraX** (API Google).
- **Intelligence Artificielle (OCR)** : Utilisation de **Google ML Kit (Text Recognition)**. Le traitement d'image pour lire les plaques signalétiques doit se faire en local, instantanément sur le processeur du téléphone.
- **Génération Excel** : Utilisation de la librairie Java **Apache POI**. Les manipulations de création de tableau et de formatage de cellules (ex: vert pour conforme, rouge pour non-conforme) doivent être faites en code Kotlin.
- **Gestion des Fichiers** : Respect strict du Scoped Storage (Android 10+). Interdiction d'écrire silencieusement dans le stockage. Utilisation obligatoire de `Intent.ACTION_CREATE_DOCUMENT` pour laisser l'utilisateur choisir l'emplacement de sauvegarde du fichier Excel généré.

## Fonctionnalités Clés à Implémenter

1. **Création de dossier** : Écrans de saisie (via Jetpack Compose) pour les informations de base du lieu et de la zone ATEX.
2. **Scan intelligent** :
    - Déclenchement de CameraX.
    - Capture et sauvegarde de la photo de la plaque signalétique.
    - Extraction automatique des informations via ML Kit.
3. **Saisie assistée et verdict** :
    - Algorithme de comparaison entre les exigences de la zone ATEX et les caractéristiques extraites du matériel.
    - Affichage immédiat du verdict : Conforme ou Non conforme.
4. **Export et Sauvegarde** :
    - Enregistrement des données de l'inspection dans la base Room.
    - Génération du rapport `.xlsx` via Apache POI.
    - Appel de l'intent système pour la sauvegarde du fichier.

## Contraintes Matérielles Cibles (Pour optimisation)
- **RAM** : Le code doit être optimisé pour fonctionner sur des appareils avec 4 à 6 Go de RAM, particulièrement lors de l'utilisation simultanée de CameraX, ML Kit et Apache POI, pour éviter les OutOfMemoryExceptions.
- **Version Android** : Le `minSdkVersion` doit être au minimum à 26 (Android 8.0), avec un ciblage optimisé pour Android 10+ (API 29+) pour la gestion des fichiers et les performances de l'IA.
- **Focus Camera** : Assurer une configuration optimale de l'autofocus via CameraX (et forcer le flash si nécessaire) car la qualité de l'OCR (ML Kit) en dépend à 90%.
- **Poids** : L'application doit rester légère (< 100 Mo base de données incluse).