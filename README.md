# VERYCOOKED: Pizza Edition

Console cooking game inspired by hectic kitchen simulators. You guide two chefs through chopping, baking, plating, and serving pizza orders before the clock runs out.

## Prerequisites
- Java Development Kit 17 or newer
- Apache Maven 3.9+

Verify versions:

```
mvn -v
```

## Build and Run
```
mvn compile
java -cp target/classes Main
```

## Gameplay Overview
- Two chefs share a single kitchen map and work in turns.
- Orders arrive over time; expired orders reduce score.
- Stations handle specific actions: chopping, cooking, washing, plating, assembly, serving, trash, and ingredient storage.
- Dirty plates return to storage automatically after serving but must be washed before reuse.
- Oven only accepts chopped ingredients on the pizza map.

## Controls
- W A S D: Move active chef
- C: Pick up or drop items, interact with floor tiles
- V: Use the station in front of the chef
- B: Switch between the two chefs
- Q: Quit to menu

Chefs become locked in place while chopping or washing to reflect the busy state.

## Design Notes
- Model View Controller separation between model/, controller/, and view/ packages.
- StationFactory implements a factory method for spawning stations from the map layout.
- Station base class and map types apply template methods for shared behavior.

## Project Structure
- src/main/java/controller: Game loop, input handling, timed tasks
- src/main/java/model: Game entities (chefs, items, stations, map, orders)
- src/main/java/view: Console renderer and UI prompts

## Troubleshooting
- Cannot move while chopping or washing: intended behavior; wait for the task to finish.
- Oven rejects ingredients: ensure the ingredient was chopped first.
- Missing dirty plates: check PlateStorage; washed plates return to the clean stack.
