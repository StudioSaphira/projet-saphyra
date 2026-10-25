# Plateformes partagées avec Saphyra — Minecraft 1.21.1

## Répartition

Saphyra fournit les interfaces `net.saphyra.common.platform` et leurs implémentations Fabric/NeoForge :

- registres par namespace : blocs, items, BlockItems, entités, BlockEntities, menus et onglets ;
- identifiants et références différées aux objets enregistrés ;
- construction des onglets, ordre et recherche ; les mixins Fabric correspondants appartiennent désormais à Saphyra ;
- envoi serveur → joueur d'un `CustomPacketPayload` enregistré par le mod ;
- demande de mise à jour des données de rendu ;
- installation d'extensions définies par les mods.

Le Common de Saphyra ne contient aucun import Fabric ou NeoForge et aucune référence à Genesis/Orevia.
Genesis conserve les formats de données de rendu des Copycats dans ses trois classes `PlatformModelData` :
ces classes connaissent `CopycatPart`, les états copiés et la propriété du modèle NeoForge. Elles ne sont pas des services génériques.

`ModRegistries` déclare le contenu Genesis dans le même ordre qu'avant.
Les points d'entrée créent directement un registre Saphyra avec le namespace `scp_genesis`
et, pour NeoForge, le bus d'événements de **Genesis**.
Les identifiants des blocs, items et données sauvegardées restent identiques.
Le protocole, les codecs et l'enregistrement des paquets Reader restent dans Genesis ;
seul leur transport vers le joueur est délégué à Saphyra.

## Utiliser Saphyra avant sa publication

Une publication publique n'est pas nécessaire. La commande suivante construit et installe
les trois artefacts dans `%USERPROFILE%/.m2/repository`. Elle n'envoie rien sur Internet :

```powershell
Set-Location 'I:\Documents\1 - Saphira\6 - Projet Minecraft\6 - Projet Saphyra\2 - Dev\projet-saphyra-1.21.1'
.\gradlew.bat publishToMavenLocal
```

Puis compiler Genesis :

```powershell
Set-Location 'I:\Documents\1 - Saphira\6 - Projet Minecraft\3 - Projet Genesis\1 - mod\SCP Saphira\projet-genesis-1.21.1'
.\gradlew.bat :common:compileJava :fabric:remapJar :neoforge:jar
```

Genesis cherche uniquement le groupe `net.saphyra` dans Maven local par défaut.
Les autres bibliothèques continuent à utiliser leurs dépôts habituels.
Après une modification de Saphyra, relancer sa publication locale, puis la compilation
de Genesis ; utiliser `--refresh-dependencies` si un artefact de même version reste en cache.

Coordonnées actuelles :

| Module consommateur | Dépendance | Configuration |
| --- | --- | --- |
| Common | `net.saphyra:saphyra-common-1.21.1:0.1-beta1` | `compileOnly` |
| Fabric | `net.saphyra:saphyra-fabric-1.21.1:0.1-beta1` | `modImplementation` |
| NeoForge | `net.saphyra:saphyra-neoforge-1.21.1:0.1-beta1` | `implementation` |

Le JAR Common est une API de compilation en noms Mojang, pas un mod à installer.
Chaque JAR de loader inclut le Common de Saphyra. Le JAR Fabric est remappé en noms
Intermediary par Loom ; le JAR NeoForge reste dans les noms attendus par NeoForge.
Les sources sont publiées avec chaque artefact.

Les dépendances de mod sont déclarées dans `fabric.mod.json` et `neoforge.mods.toml`.
Dans un profil de jeu, installer **Genesis et Saphyra pour le même loader**.
Genesis n'embarque pas les classes Saphyra et ne doit pas les recopier dans son JAR.
Les lancements de développement reçoivent Saphyra via leurs dépendances Gradle.

## Dépôt local isolé, sans Maven local

Dans Saphyra :

```powershell
.\gradlew.bat publish
```

Sans URL de publication distante configurée, cela écrit uniquement dans `Saphyra/build/repo`.
Dans Genesis :

```powershell
.\gradlew.bat :fabric:remapJar :neoforge:jar "-PsaphyraRepository=I:/Documents/1 - Saphira/6 - Projet Minecraft/6 - Projet Saphyra/2 - Dev/projet-saphyra-1.21.1/build/repo"
```

## Passer au dépôt Maven public

Le dépôt public doit exposer les mêmes coordonnées et artefacts que la publication locale.
Une URL GitHub ou un JAR de release seul ne suffit pas à constituer un dépôt Maven.
Si ton automatisation utilise d'autres coordonnées, adapter les trois dépendances en conséquence.

Dans Genesis, fournir `-PsaphyraRepository=https://adresse-de-ton-depot/` et la version
avec `-Psaphyra_version=...`, ou définir ces propriétés dans les propriétés Gradle locales.
L'adresse illustrée est un exemple à remplacer ; aucun dépôt distant n'a été publié par cette tâche.

Dans Saphyra, `-PsaphyraVersion=...` permet de fixer la version publiée
(par défaut : `mod_version-version_typeversion_id`, soit `0.1-beta1`).
Un build composite est également possible, mais exige ici de sélectionner correctement les variantes
nommées/remappées de Loom et celles de NeoForge. Le dépôt Maven local exerce exactement le futur chemin
de consommation des publications et évite cette complexité supplémentaire.

## Ajouter les registres d'un autre mod

Dans le Common du mod :

```java
public static void register(PlatformRegistry registry) {
    MY_BLOCK = registry.registerBlock("my_block", MyBlock::new);
    MY_ITEM = registry.registerBlockItem("my_block", MY_BLOCK);
}
```

Dans son point d'entrée Fabric :

```java
var registry = new FabricPlatformRegistry("mon_mod");
MyRegistries.register(registry);
registry.register();
```

Dans son point d'entrée NeoForge :

```java
var registry = new NeoForgePlatformRegistry("mon_mod", modEventBus);
MyRegistries.register(registry);
registry.register();
```

Les classes de registre Saphyra peuvent être étendues si une opération particulière
au mod est nécessaire. Garder ces sous-classes dans le module du loader concerné.
Appeler `register()` une seule fois, après les déclarations ; ne pas appeler `get()`
sur une référence NeoForge dans un initialiseur statique avant son événement de registre.
Les factories et listes de blocs fournies par `Supplier` sont résolues au bon moment.

## Ajouter un service propre à un mod

Définir une interface publique dans le Common du mod, avec des types Java/vanilla uniquement :

```java
public interface MyModServices {
    void refreshSpecialFeature(BlockEntity entity);
}
```

Chaque loader fournit son implémentation, puis l'enregistre dans le point d'entrée du mod :

```java
PlatformServices.registerExtension(MyModServices.class, new FabricMyModServices());
// NeoForge : new NeoForgeMyModServices()
```

Le Common la récupère après l'initialisation du mod :

```java
PlatformServices.extension(MyModServices.class).refreshSpecialFeature(entity);
```

Une interface ne peut être installée qu'une fois. Chaque mod emploie son propre package ;
les contrats ne se remplacent donc pas mutuellement.
Ne pas appeler `PlatformServices.initialize(...)` depuis un mod consommateur :
Saphyra installe lui-même le backend global.
Les extensions utilisées pendant l'initialisation d'un autre mod nécessitent un ordre de chargement explicite.

## Références

- [Publication Maven locale — Gradle](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Variantes nommées et remapping — Fabric Loom](https://docs.fabricmc.net/develop/loom/)
