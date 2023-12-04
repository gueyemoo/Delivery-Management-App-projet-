
<h1 align="center"> Delivery App Management </h1> <br>
<p align="center">
  <a href="https://gitpoint.co/">
    <img alt="DeliveryAppManagement" title="DeliveryAppManagement" src="https://i.imgur.com/iGo9Yht.png" width="450">
  </a>
</p>

<p align="center">
  Un gestionnaire de livraison dans votre poche. Construit avec Android Studio.
</p>

<p align="center">
  <a href="https://www.mediafire.com/file/tasfuohw6b66xo7/DeliveryManagementApp.apk/file">
    <img alt="Download on mediafire" title="Download link" src="https://i.imgur.com/L9ssPWN.png" height="65" width="150">
  </a>
</p>

## Table of Contents

- [Introduction](#introduction)
- [Fonctionnalités](#fonctionnalités)
- [Solution Générale](#solution-générale)
- [Idée et Supposition](#idée-et-supposition)
- [Choix Technique](#choix-technique)
- [Installation](#installation)
- [Credit](#credit)
- [Remerciements](#remerciements)
- [Feedback](#feedback)


## Introduction


Une application mobile de gestion permettant chaque matin d'attribuer à chaque livreur disponible une liste de colis à distribuer dans la journée.
**Disponible pour Android seulement.**

<p align="center">
  <img src = "https://imgur.com/NSSnxpr.png" width=350>
</p>

## Fonctionnalités

Quelques fonctionnialités disponible avec l'application:

* Voir les livreurs du jour disponible
* Créer des livreurs et des colis
* Ajouter des livreurs et des colis
* Retirer des livreurs et des colis
* Suivre l'état d'avancement de la livraison des colis pour un livreur donner
* Connaitre la situation actuel d'un livreur
* Visualiser un tableau de bord mis à jour en temps réel

<p align="center">
  <img src = "https://imgur.com/d8QSv2r.png" width=700>
</p>

<p align="center">
  <img src = "https://imgur.com/Fj9y9A5.png" width=700>
</p>


## Solution Générale

Afin d'aborder le problème donner j'ai décidé de faire plusieurs supposition concernant le sujet plutôt libre de choix avec pour seul but,
de développer une application permettant chaque matin d'attribuer à chaque livreur disponible une liste de colis à distribuer dans la journée comme indiquer sur le sujet.

Suivant mes différentes idées j'ai choisi de me tourner vers l'exercice **Fullstack** car il s'agissait là de l'exercice vers lequel je pouvais pleinement exprimer mes idées et ma créativité.

J'ai ainsi fait le choix de créer une application 100% mobile grâce à Android Studio car je trouvais cette solution plus adapté au sujet.


## Idée et Supposition

Ma première approche fût de déchiffrer le concept du sujet afin d'éclaircir certain point et de développer des idées face à celle-ci comme ci dessous:

`Une entreprise effectue des livraisons dans une ville:`
Je suis donc parti du principe que toutes les valeurs de X et Y se trouvent entre {-100 et 100}
<br> - -100 et 100 représentant les limites de la ville.

`Les colis se trouvent tous au centre de distribution situé aux coordonnées (0,0):`
Partant du principe qu'un livreur dispose de coordonnée X et Y, j'ai eu l'idée de considérer le centre de distribution comme un livreur par défaut se trouvant aux coordonnées (0,0)

`Chaque jour on dispose d'un certain nombre de livreurs:` J'ai donc décider de rendre les livreurs disponible ou indisponible de façon hasardeuse à chaque nouveau jour.
<br> - Le hasard réprésentant les différentes conditons qu'un livreur pourrait rencontrer dans sa vie le rendant indisponible au jour x.

`Les livreurs commencent leur tournée au centre, embarquent leur colis, et rentrent directement à leur domicile après la livraison du dernier colis:` À l'issue de cette indication, j'ai procédé à la création d'un statut pour le livreur permettant ainsi de déterminer si celui-ci est en *cours de livraison*, *à son domicile* ou *inconnu* si le livreur n'est pas disponible.
<br> - Le statut *Inconnu* vient du fait qu'un livreur indisponible ne travaille pas à l'instant t, donc nous ne pouvons pas savoir ce qu'il est actuellement en train de faire.


En me basant sur de réel horaire de livraison j'ai fait la supposition *qu'une livraison ne pouvait se faire qu'entre 8h et 21h.* <br>
À partir de ce principe, j'ai ainsi fait la déduction que tous colis affecter à un livreur et dont la livraison dépassera 21h sera livré *le lendemain* par le même livreur. <br>
Et ainsi que tous colis affecter à un livreur dans la nuit (c'est à dire entre 00h et 8h) ne sera livré par le livreur même *qu'à partir* de 8h

Concernant encores les délais de livraison, ceux-ci sont défini en fonction de la distance entre le centre de distribution et le lieu de livraison du colis. <br>
Par exemple: <br>
Si la distance entre le centre et le colis équivaut à moins de 5km   <br>
Alors cela signifie que le colis sera livré d'ici la prochaine heure (tout en respectant les horaires de livraisons).

`Pour simplifier le problème on suppose que les déplacements entre deux points de la carte s'effectuent en ligne droite:` Partant de cette phrase du sujet, j'ai calculé la distance entre les coordonnées du centre de distribution et celui du colis en utilisant le théoreme de Pythagore afin de déterminer la distance (en Km).

Afin de limiter le travail d'un livreur en particulier dans une journée, la distance à parcourir de celui-ci ne peut pas dépasser les 150km près. (retour au domicile *non* compris)

`Réglage de quelques détails:` Ayant proposer la possibilité de modifier, retirer et ajouter des colis et livreurs, certaines conditions (logique) ont dû être mis en place notamment:
- Un livreur *en cours de livraison* ne peut pas être rendu indisponible ou être retirer.
- Il est impossible d'ajouter un colis dont la date de livraison est antérieur à celle d'aujourd'hui.
- La date de livraison d'un colis n'est pas modifiable si le colis est déjà en cours de livraison.

## Choix Technique
Le développement de cette application s'est fait à l'aide de l'environnement de développement *Android Studio 3.5* en *java*.

Le moteur de base de données utilisé  afin de mettre en place la base de donnée de l'application comme ci-dessous est *SQLite*:
<p align="center">
  <img src = "https://imgur.com/YfeFg4s.png" width=700>
</p>
<br>

Vous trouverez le code java de l'application dans `ProjetCandidature > app > src > main > java > ws > splash > projetcandidature` <br> <br>
Vous trouverez le code xml des vues et animations de l'application dans `ProjetCandidature > app > src > main > res > layout | anim` <br> <br>
Les images se trouvent dans `ProjetCandidature > app > src > main > res > drawable`


## Installation

Afin de pouvoir utiliser l'application correctement il est nécessaire que votre mobile dispose d'une version android égal ou supérieur à la verison **Android 4.0.3 (API Level 15)**.

Pour installer l'application, il vous suffit de telecharger et d'installer le fichier [apk](https://www.mediafire.com/file/tasfuohw6b66xo7/DeliveryManagementApp.apk/file) sur votre mobile.

<h6>Comment installer un APK sur Android ?</h6>

- Allez dans les `Paramètres`, puis `Sécurité` de votre mobile.

- Activez les `Sources inconnues`

- Recherchez l’APK sur votre téléphone (à l’aide d’un explorateur de fichiers par exemple)

- Lancez le fichier APK et suivez les instructions

- (Optionnel, mais conseillé) Désactivez les sources inconnues

## Credit

Merci à Chattapat, Eucalyp, Srip et Freepik de www.flaticon.com qui m'ont permis d'utiliser et de retoucher leurs icônes pour cette application.

## Feedback

N'hésitez pas à m'envoyer un retour à mon adresse [mail](mailto:m.gueye0411@gmail.com?subject=[GitHub]%20Retour%20Application%20Mobile) en cas de problème rencontrer. <br>

**<u>N.B:</u>**
Je me suis beaucoup orienté sur la solidité du programme (aptitude à ne pas planter) et sur son ergonomie au vu de l'utilisateur. <br>
Différentes idées ont eu lieu d'exister durant la réalisation de l'application, mais certaines n'ont pas réussi à être réalisé en raison de différentes restrictions m'obligeant à trouver des alternatives afin de rendre une application fonctionnelle et répondant aux besoins.<br>
Notamment concernant l'ajout de colis à un livreur,
je souhaitais pouvoir sélectionner plusieurs colis à la fois avant de pouvoir les ajouter à un livreur permettant ainsi de limiter le nombre de clique et améliorer l'experience utilisateur.
