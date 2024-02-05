### Intro
This framework provides the possibility to run small simulations. I see this project as a fun challenge for me. The Code is 100% Java/Kotlin and self written. The rendering is happening on the CPU and therefore not as performant.
The following interfaces are available (in package `framework.interfaces` except simulation in package `framework`):
```mermaid
classDiagram
direction BT
class Collidable {
<<Interface>>

}
class Drawable {
<<Interface>>
   Boolean outlineRasterization
   Vec? color
}
class Entity {
<<Interface>>

}
class Mass {
<<Interface>>
   Double mass
}
class Model {
<<Interface>>

}
class Moveable {
<<Interface>>
   Vec position
   Vec velocity
   Vec acceleration
   Status status
}
class PhysicsSimulation {
   Vec gravity
}
class Renderable {
<<Interface>>

}
class Simulation {
   Camera camera
   Display display
   KeyManager keyManager
}
class Tickable {
<<Interface>>

}
class Volume {
<<Interface>>
   Double pitch
   Double roll
   List~Vec~ meshObjCoords
   Double yaw
}

Collidable  -->  Mass 
Entity  -->  Moveable 
Entity  -->  Renderable 
Mass  -->  Entity 
Model  -->  Collidable 
Model  -->  Volume 
Moveable  -->  Tickable
PhysicsSimulation  -->  Simulation 
Renderable  -->  Drawable 
Volume  -->  Entity 
```
Additionally, some implementations of the interfaces are also available, mainly `PointMass`, `Sphere`, and `ImpulseConnection` (all in package physics)
# Usage
TODO

# Todos
1. Color scheme
2. 
