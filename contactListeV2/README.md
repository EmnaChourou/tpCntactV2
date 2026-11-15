# TP Android — Liste de Contacts (Version 2)

Application Android native développée en **Kotlin** dans le cadre du TP *Liste de Contacts V2*.  
Cette application permet de gérer une liste de contacts personnalisée, d'afficher et de modifier les informations d'un contact via une seconde activité, d'interagir avec les composants système (téléphone, e-mail, galerie et appareil photo), et d'actualiser dynamiquement la liste principale lors de l'enregistrement.

---

## 📱 Fonctionnalités Principales

1. **MainActivity (Liste de contacts)**
   - Affichage d'une liste dynamique de contacts dans une `ListView` avec un adaptateur personnalisé (`ContactAdapter`).
   - Présentation pour chaque ligne de la **photo**, du **nom**, du **prénom** et du **numéro de téléphone**.
   - Sélection d'un contact ouvrant `DetailActivity` à l'aide d'un **Intent explicite** en transmettant les données du contact sélectionné.
   - Mise à jour immédiate de la liste et de la photo du contact lors du retour avec le résultat (`ActivityResultLauncher`).

2. **DetailActivity (Consultation & Modification)**
   - Affichage et édition du **nom**, **prénom**, **numéro de téléphone**, **adresse e-mail** et **photo**.
   - **Appeler** : Ouverture du composeur téléphonique prérempli via un **Intent implicite** (`Intent.ACTION_DIAL`).
   - **Envoyer un e-mail** : Ouverture de l'application de messagerie avec l'adresse préremplie via un **Intent implicite** (`Intent.ACTION_SENDTO`).
   - **Sélection de photo (Galerie)** : Choix d'une image locale via `ActivityResultContracts.GetContent()`.
   - **Prise de photo (Caméra)** : Prise de vue en temps réel via `ActivityResultContracts.TakePicture()` et `FileProvider`.
   - **Enregistrer** : Validation des champs obligatoires et renvoi des données modifiées à `MainActivity` via `setResult(RESULT_OK)`.
   - **Annuler** : Annulation des modifications et retour à la liste sans enregistrer (`RESULT_CANCELED`).

3. **Gestion des Cas Limites et Sécurité**
   - Contrôle des champs obligatoires vides avec affichage d'erreurs explicites.
   - Gestion sécurisée de la caméra et du partage de fichiers avec `androidx.core.content.FileProvider`.
   - Capture des exceptions `ActivityNotFoundException` si aucune application de téléphonie ou de messagerie n'est installée.
   - Maintien de l'état initial en cas d'annulation de la prise/sélection de photo.

---

## 🛠️ Stack Technique & Architecture

- **Langage** : Kotlin
- **IDE** : Android Studio
- **Composants d'interface** : XML Layouts (`ConstraintLayout`, `LinearLayout`, `ScrollView`, `ListView`, `TextInputLayout`)
- **Modèle de données** : `Contact.kt` implémentant `Parcelable`
- **Adaptateur** : `ContactAdapter.kt` héritant de `ArrayAdapter<Contact>`
- **Transfert de données inter-activités** : `ActivityResultContracts.StartActivityForResult()`
- **Stockage partagé pour caméra** : `FileProvider` (`res/xml/file_paths.xml`)

---

## 📁 Structure du Projet

```text
contactListeV2/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/contactlistev2/
│   │       │   ├── Contact.kt             # Modèle de données Contact (Parcelable)
│   │       │   ├── ContactAdapter.kt      # Adaptateur personnalisé pour la ListView
│   │       │   ├── MainActivity.kt        # Liste principale et gestion du résultat
│   │       │   └── DetailActivity.kt      # Écran de détails, éditions et intents
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   ├── activity_main.xml  # Layout de l'activité principale
│   │       │   │   ├── activity_detail.xml# Layout de l'écran de détail
│   │       │   │   └── item_contact.xml   # Layout d'un élément de la liste
│   │       │   └── xml/
│   │       │       └── file_paths.xml     # Déclaration des chemins FileProvider
│   │       └── AndroidManifest.xml        # Manifest de l'application & FileProvider
├── captures/                              # Dossier contenant les captures d'écran
├── Compte_Rendu_TP_Contacts_V2.docx       # Compte rendu rédigé au format Word (.docx)
├── build.gradle.kts                       # Configuration Gradle du module app
└── README.md                              # Fichier d'information du projet
```

---

## 🚀 Installation & Exécution

1. **Cloner le dépôt** :
   ```bash
   git clone <URL_DU_DEPOT_GITHUB>
   ```
2. **Ouvrir le projet dans Android Studio**.
3. **Synchroniser le projet avec Gradle** (`Sync Project with Gradle Files`).
4. **Lancer l'application** sur un émulateur (API 24+) ou un appareil physique Android connecté.

---

## 📝 Compte Rendu & Captures d'Écran

- Le **compte rendu complet** détaillant le travail réalisé, l'architecture, les étapes de développement, le plan de test et les difficultés rencontrées est disponible dans le fichier **`Compte_Rendu_TP_Contacts_V2.docx`**.
- Les captures d'écran réelles sont consultables dans le dossier **`captures/`**.
