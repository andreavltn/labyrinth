# Labyrinthe

Implémentation du jeu de société "Labyrinthe" en Java, avec une interface graphique JavaFX, un mode multijoueur local 
et la possibilité de joueur contre des bots.

## Fonctionnalités implémentées

- Plateau 7x7 avec tuiles mélangées aléatoirement
- 2 à 4 joueurs (humains ou bots)
- Tour par tour : insertion d'une tuile puis déplacement du pion
- Objectif : collecter tous ses trésors dans l'ordre, puis revenir à sa case de départ
- Bot avec stratégie Monte-Carlo
- Sauvegarde automatique entre chaque tour (SQLite)
- Sauvegarde/chargement manuel de parties au format JSON
- Gestion de profils de joueurs persistants

## Images du jeu
![img.png](img.png)
![img_1.png](img_1.png)

## Outils utilisés

- Java 21
- JavaFX
- SQLite (via JDBC)
- Gson

## Lancer le projet + Informations importantes

- java --module-path [libJavaFX] --add-modules javafx.controls,javafx.fxml -jar labyrinth.jar
  - où [libJavaFX] est le chemin d'accès absolu vers le répertoire 'lib' de l'installation JavaFX sur la machine

- Un fichier labyrinth.db est généré au lancement de l'application, et permet la persistence de la partie en cours.
  - Si le jeu est fermé et relancé, la partie reprend son cours là où elle a été interrompue.
- IMPORTANT: Pour la sauvegarde, il faut enregistrer le fichier au format .json


## Structure

```
src/
└── labyrinth/
    ├── model/              # logique du jeu
    │   └── strategy/       # IA (stratégie Monte-Carlo)
    │       └── simulation/ # moteur de jeu pour le bot
    ├── service/            # conteneur des services
    │   ├── dialog/         # service de dialogue (messages, fichiers, profils)
    │   ├── persistence/    # accès base de données SQLite (parties et profils)
    │   └── serialization/  # sérialisation JSON via Gson (DTOs + adaptateurs)
    ├── view/               # vues JavaFX
    ├── viewmodel/          # modèles de vue (pattern MVVM)
    └── util/
        ├── adapter/        # pont observables <-> JavaFX
        ├── collection/     # structures de données génériques (Grid, Position)
        ├── graph/          # graphe générique + BFS + adaptateur Maze -> Graph
        └── observable/     # système d'observables indépendant de JavaFX
```

## Architecture

Le projet suit le pattern MVVM. Le modèle est indépendant de JavaFX et expose ses états via des valeurs observables.
Les modèles de vues adaptent ces observables vers JavaFX avec `FxAdapter`.

Dans le moteur de simulation (`GameSimulation`), le bot explore les coups possibles sans modifier l'état réel de la partie.
Il cherche quel coup sera le meilleur parmi la majorité des coups possibles. Sa stratégie est simplement de se rapprocher
le plus possible du trésor qu'il doit atteindre, en créant un chemin accessible.

## Difficultés rencontrées

- Beaucoup de temps pour bien comprendre le modèle MVVM et son implémentation
- Quelques problèmes tout le long du projet pour afficher/cacher et mettre à jour des données affichées sur l'interface
en temps réel
- Un bon blocage sur la sauvegarde/chargement pour réussir à reprendre une partie en cours de route. Il fallait forcer
le raffraichissement de l'affichage pour revenir sur le plateau de jeu, et bien gérer le tour qui était en cours avant
d'avoir quitté l'application.
- Quelques oublis à droite à gauche, comme des "id" au lieu de "id+1" sur l'affichage du numéro de trésor
dans les tuiles, qui créait des doublons et donc des trésors manquants (existants, mais cachés sous un mauvais numéro)
- Un peu de problèmes avec le bot et le popup. Initialement, il y avait un popup de fin de tour, mais le fait de
relancer une nouvelle partie pendant qu'on en jouait déjà une (par exemple) ne supprimait pas les anciens écouteurs 
pour leur création, et donc la nouvelle partie avait 2x plus de popups qui s'ouvraient. L'idée a été avortée, 
et remplacée par un délai entre chaque coup du bot pour bien voir ce qu'il a fait durant son tour.

## Auteurs

VALENTINI Andréa
FUCHS Jordan