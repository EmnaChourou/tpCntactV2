# COMPTE RENDU DE TRAVAIL PRATIQUE
## TP Android — Liste de Contacts (Version 2)
**Développement mobile Android en Kotlin • Android Studio • Interfaces XML**

---

### 1. Présentation du Projet et Objectifs
Ce TP a pour objectif de concevoir et développer une application Android native complète de gestion de contacts en Kotlin. L'application est articulée autour de deux activités principales (**MainActivity** et **DetailActivity**) permettant la consultation, l'édition, la communication téléphonique et e-mail, ainsi que l'association de photos issues de la galerie ou prises en direct via l'appareil photo.

#### Exigences satisfaites :
- Affichage d'une liste dynamique de contacts dans une `ListView` avec adaptateur personnalisé (`ContactAdapter`).
- Ouverture explicite de `DetailActivity` lors du clic sur un contact avec transmission des données via `Intent`.
- Modification des informations (nom, prénom, téléphone, e-mail, photo) avec validation des champs.
- Lancement d'Intents implicites pour les appels téléphoniques (`ACTION_DIAL`) et l'envoi d'e-mails (`ACTION_SENDTO`).
- Sélection d'une image de contact depuis la galerie du téléphone.
- Prise de photo via l'appareil photo avec stockage sécurisé grâce à `FileProvider`.
- Mise à jour immédiate de la liste principale lors de la sauvegarde grâce au mécanisme de retour de résultat d'activité (`ActivityResultLauncher`).

---

### 2. Architecture et Choix Techniques

#### 2.1 Modèle de Données (`Contact.kt`)
Le modèle `Contact` représente une entité contact avec les attributs : `id`, `nom`, `prenom`, `telephone`, `email` et `imageUri`. La classe implémente l'interface `Parcelable` pour garantir un transfert rapide et sans surcharge entre activités.

#### 2.2 Adaptateur Personnalisé (`ContactAdapter.kt` & `item_contact.xml`)
Chaque ligne de la `ListView` est structurée par `item_contact.xml` (`ImageView` pour la photo, `TextView` pour le nom et le téléphone). `ContactAdapter` hérite de `ArrayAdapter<Contact>` et assure le chargement dynamique des images (ressources drawable, URIs de galerie et URIs d'appareil photo).

#### 2.3 Communication inter-activités
- **Intent Explicite** : Passage de `MainActivity` vers `DetailActivity` avec `EXTRA_CONTACT` et `EXTRA_POSITION`.
- **ActivityResultLauncher** : Récupération du contact modifié via `ActivityResultContracts.StartActivityForResult()` et rafraîchissement immédiat de l'adaptateur via `notifyDataSetChanged()`.
- **Intents Implicites** : 
  - Téléphone : `Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))`
  - Messagerie : `Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))`

#### 2.4 Intégration Galerie, Caméra et FileProvider
- **Galerie** : `ActivityResultContracts.GetContent()` avec récupération de permissions d'accès persistantes.
- **Caméra** : Création d'un fichier temporaire dans `cacheDir` et génération d'un URI partagé sécurisé grâce à `FileProvider` (`res/xml/file_paths.xml`).

---

### 3. Étapes de Développement
1. **Étape 1** : Création du modèle de données `Contact` implémentant `Parcelable`.
2. **Étape 2** : Conception du layout d'élément `item_contact.xml` et développement de l'adaptateur `ContactAdapter`.
3. **Étape 3** : Implémentation de `MainActivity` (`ListView`, liste initiale de contacts, enregistrement de `ActivityResultLauncher`).
4. **Étape 4** : Conception du formulaire dans `activity_detail.xml` (`ImageView`, boutons photo, champs de saisie, boutons d'action).
5. **Étape 5** : Implémentation de `DetailActivity` (gestion de la galerie, caméra, appels, e-mails, validation des champs et renvoi de résultat).
6. **Étape 6** : Configuration de `FileProvider` dans le `AndroidManifest.xml` et tests complets.

---

### 4. Tableau de Tests et Résultats

| N° | Scénario de Test | Résultat Attendu | Statut |
| :--- | :--- | :--- | :--- |
| **1** | Affichage initial des contacts | La `ListView` affiche correctement la photo, le nom complet et le téléphone | **Succès** |
| **2** | Ouverture du détail d'un contact | `DetailActivity` s'ouvre avec tous les champs préremplis du contact | **Succès** |
| **3** | Modification & Enregistrement | Les données modifiées sont renvoyées et la `ListView` est immédiatement mise à jour | **Succès** |
| **4** | Action Appeler (Intent implicite) | Le composeur téléphonique s'ouvre avec le numéro prérempli | **Succès** |
| **5** | Action Envoyer e-mail (Intent implicite) | L'application e-mail s'ouvre avec l'adresse du destinataire préremplie | **Succès** |
| **6** | Sélection photo Galerie | La photo choisie est chargée et affichée dans l'ImageView | **Succès** |
| **7** | Prise de photo Caméra | La photo capturée est enregistrée via `FileProvider` et affichée à l'écran | **Succès** |
| **8** | Validation des champs obligatoires | Des messages d'erreur s'affichent sur les champs vides et bloquent l'enregistrement | **Succès** |
| **9** | Bouton Annuler | Retour à la liste principale sans conserver les modifications apportées | **Succès** |

---

### 5. Difficultés Rencontrées et Solutions Apportées
- **Permissions des URIs de Galerie** : Les URIs de la galerie perdaient parfois l'accès après redémarrage. *Solution* : appel à `contentResolver.takePersistableUriPermission()`.
- **Partage sécurisé pour la caméra** : Pour respecter la sécurité Android (API 24 à 37), utilisation de `FileProvider` pour partager l'URI temporaire du fichier image.
- **APIs dépréciées** : Remplacement de `startActivityForResult()` par l'API moderne `ActivityResultContracts.StartActivityForResult()`.

---

### 6. Conclusion
L'application **Liste de Contacts V2** développée répond fidèlement à l'ensemble du cahier des charges. L'architecture adoptée garantit la propreté du code, la sécurité des accès aux fichiers et une expérience utilisateur réactive.
