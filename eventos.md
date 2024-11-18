# Eventos de Minecraft

## Bloques

- **BlockBreakEvent** - Se llama cuando un bloque se rompe.
- **BlockBurnEvent** - Se llama cuando un bloque se quema.
- **BlockCanBuildEvent** - Se llama cuando intentamos colocar un bloque para verificar si se puede construir allí.
- **BlockDamageEvent** - Se llama cuando un bloque es dañado por un jugador.
- **BlockDispenseEvent** - Se llama cuando un objeto es dispensado de un bloque.
- **BlockEvent** - Representa un evento relacionado con bloques.
- **BlockFadeEvent** - Se llama cuando un bloque se desvanece, se derrite o desaparece debido a las condiciones del mundo.
- **BlockFormEvent** - Se llama cuando un bloque se forma o se expande debido a las condiciones del mundo.
- **BlockFromToEvent** - Representa eventos con un bloque fuente y un bloque de destino, solo se aplica a líquidos y a huevos de dragón que se teletransportan.
- **BlockGrowEvent** - Se llama cuando un bloque crece de manera natural en el mundo.
- **BlockIgniteEvent** - Se llama cuando un bloque se enciende.
- **BlockPhysicsEvent** - Se lanza cuando se realiza una comprobación de física de bloques.
- **BlockPistonEvent** - ?? 
- **BlockPistonExtendEvent** - ?? 
- **BlockPistonRetractEvent** - ?? 
- **BlockPlaceEvent** - Se llama cuando un jugador coloca un bloque.
- **BlockRedstoneEvent** - Se llama cuando un bloque es alimentado por redstone o cambia la corriente de redstone.
- **BlockSpreadEvent** - Se llama cuando un bloque se expande debido a las condiciones del mundo.
- **EntityBlockFormEvent** - Se llama cuando un bloque es formado por entidades.
- **LeavesDecayEvent** - Se llama cuando las hojas se decaen de forma natural.
- **NotePlayEvent** - Se llama cuando se reproduce un bloque de notas a través de la interacción de un jugador o una corriente de redstone.
- **SignChangeEvent** - Se llama cuando un jugador cambia un cartel.

## Encantamientos

- **EnchantItemEvent** - Se llama cuando un objeto es encantado con éxito en una mesa de encantamientos.
- **PrepareItemEnchantEvent** - Se llama cuando un objeto es insertado en una mesa de encantamientos, puede activarse múltiples veces.

## Entidades

- **CreatureSpawnEvent** - Se llama cuando una criatura es generada en el mundo.
- **CreeperPowerEvent** - Se llama cuando un Creeper es alcanzado por un rayo y se vuelve supercargado.
- **EntityBreakDoorEvent** - Se llama cuando una entidad rompe una puerta.
- **EntityChangeBlockEvent** - Se llama cuando una entidad viviente cambia un bloque.
- **EntityCombustByBlockEvent** - ?? 
- **EntityCombustByEntityEvent** - ?? 
- **EntityCombustEvent** - Se llama cuando una entidad se prende fuego (o se quema).
- **EntityCreatePortalEvent** - Se lanza cuando una entidad viviente crea un portal en el mundo.
- **EntityDamageByBlockEvent** - Se llama cuando una entidad es dañada por un bloque.
- **EntityDamageByEntityEvent** - Se llama cuando una entidad es dañada por otra entidad.
- **EntityDamageEvent** - Almacena los datos de eventos de daño.
- **EntityDeathEvent** - Se lanza cuando una entidad viviente muere.
- **EntityEvent** - Representa un evento relacionado con una entidad.
- **EntityExplodeEvent** - Se llama cuando una entidad explota.
- **EntityInteractEvent** - Se llama cuando una entidad interactúa con un objeto.
- **EntityMakeLoveEvent** - Se llama cuando una entidad se reproduce con otra entidad.
- **EntityPortalEnterEvent** - Almacena los datos de las entidades dentro de un portal.
- **EntityRegainHealthEvent** - Almacena los datos para eventos de recuperación de salud.
- **EntityShootBowEvent** - Se llama cuando una entidad viviente dispara un arco, lanzando una flecha.
- **EntityTameEvent** - Se lanza cuando una entidad viviente es domesticada.
- **EntityTargetEvent** - Se llama cuando una criatura selecciona o deselecciona otra entidad.
- **EntityTargetLivingEntityEvent** - Se llama cuando una entidad selecciona a una entidad viviente como objetivo (y solo puede ser un objetivo viviente).
- **EntityTeleportEvent** - Se lanza cuando una entidad no jugadora (como un Enderman) intenta teletransportarse de un lugar a otro.
- **ExpBottleEvent** - Se llama cuando una botella de experiencia lanzada golpea y libera experiencia.
- **ExplosionPrimeEvent** - Se llama cuando una entidad decide explotar (como TNT o un creeper).
- **FoodLevelChangeEvent** - Se llama cuando el nivel de comida de una entidad humana cambia.
- **ItemDespawnEvent** - Se llama cuando un objeto desaparece del mundo.
- **PigZapEvent** - Se llama cuando un cerdo es alcanzado por un rayo.
- **PlayerDeathEvent** - Se lanza cuando un jugador muere.
- **PotionSplashEvent** - Se llama cuando se lanza un proyectil de poción.
- **SheepDyeWoolEvent** - Se llama cuando la lana de una oveja es teñida.
- **SheepRegrowWoolEvent** - Se llama cuando una oveja regenera su lana.
- **SlimeSplitEvent** - Se llama cuando un Slime se divide en fragmentos más pequeños.

## Inventario

- **BrewEvent** - Se llama cuando se elabora algo (?).
- **CraftItemEvent** - Se llama cuando se elabora un objeto (?).
- **FurnaceBurnEvent** - Se llama cuando un objeto se está quemando como combustible en un horno.
- **FurnaceSmeltEvent** - Es lo mismo que el anterior, pero cuando se está fundiendo en otra sustancia.
- **InventoryClickEvent** - ?? 
- **InventoryCloseEvent** - Se llama cuando un inventario se cierra.
- **InventoryEvent** - Representa un evento relacionado con el inventario.
- **InventoryOpenEvent** - Se llama cuando un inventario se abre.

## Pinturas

- **PaintingBreakByEntityEvent** - Se activa cuando una pintura es removida por una entidad.
- **PaintingBreakEvent** - Se activa cuando una pintura es removida.
- **PaintingEvent** - Representa un evento relacionado con las pinturas.
- **PaintingPlaceEvent** - Se activa cuando una pintura es colocada en el mundo.

## Jugadores

- **AsyncPlayerChatEvent** - Se activa de manera asincrónica dependiendo de cómo se haya activado.
- **AsyncPlayerPreLoginEvent** - Almacena detalles de los jugadores que intentan iniciar sesión.
- **PlayerAnimationEvent** - Representa un evento de animación de un jugador.
- **PlayerBedEnterEvent** - Se activa cuando un jugador está a punto de entrar en la cama.
- **PlayerBedLeaveEvent** - Se activa cuando un jugador deja la cama.
- **PlayerBucketEmptyEvent** - Se llama cuando un cubo es vaciado.
- **PlayerBucketEvent** - Representa un evento relacionado con cubos.
- **PlayerBucketFillEvent** - Se llama cuando un jugador llena un cubo.
- **PlayerChannelEvent** - Se llama después de que un jugador registra o desregistra un canal de plugin.
- **PlayerChatEvent** - Contiene información sobre el chat del jugador y los comandos. ¡-DEPRECATED-!
- **PlayerChatTabCompleteEvent** - Se llama cuando un jugador intenta completar un mensaje con tabulador.
- **PlayerCommandPreprocessEvent** - Se llama al principio del proceso de manejo de comandos.
- **PlayerDropItemEvent** - Se lanza cuando un jugador deja caer un objeto de su inventario.
- **PlayerEggThrowEvent** - Se llama cuando un jugador lanza un huevo que podría eclosionar.
- **PlayerEvent** - Representa un evento relacionado con un jugador.
- **PlayerExpChangeEvent** - Se llama cuando cambia la experiencia de un jugador de forma natural.
- **PlayerFishEvent** - Se lanza cuando un jugador está pescando.
- **PlayerGameModeChangeEvent** - Se llama cuando un jugador cambia de modo de juego.
- **PlayerInteractEntityEvent** - Representa un evento que se activa cuando un jugador hace clic derecho sobre una entidad.
- **PlayerInteractEvent** - Se llama cuando un jugador interactúa con un objeto o con el aire.
- **PlayerInventoryEvent** - Representa un evento relacionado con el inventario de un jugador (aunque este evento realmente nunca hizo nada).
- **PlayerItemBreakEvent** - Se activa cuando un objeto de un jugador se rompe.
- **PlayerItemHeldEvent** - Se activa cuando un jugador cambia el objeto que tiene en la mano.
- **PlayerJoinEvent** - Se llama cuando un jugador se une a un servidor.
- **PlayerKickEvent** - Se llama cuando un jugador es expulsado del servidor.
- **PlayerLevelChangeEvent** - Se llama cuando el nivel de un jugador cambia.
- **PlayerLoginEvent** - Almacena detalles de los jugadores que intentan iniciar sesión.
- **PlayerMoveEvent** - Contiene información sobre los eventos de movimiento de un jugador.
- **PlayerPickupItemEvent** - Se lanza cuando un jugador recoge un objeto del suelo.
- **PlayerPortalEvent** - Se llama cuando un jugador completa el proceso de portal al estar dentro de un portal.
- **PlayerPreLoginEvent** - Almacena detalles de los jugadores que intentan iniciar sesión.
- **PlayerQuitEvent** - Se llama cuando un jugador deja el servidor.
- **PlayerRegisterChannelEvent** - Se llama inmediatamente después de que un jugador registre un canal de plugin.
- **PlayerRespawnEvent** - Se llama cuando un jugador respawnea.
- **PlayerShearEntityEvent** - Se llama cuando un jugador esquila una entidad, como una oveja.
- **PlayerTeleportEvent** - Contiene información sobre eventos de teletransportación de jugadores.
- **PlayerToggleFlightEvent** - Se llama cuando un jugador cambia su estado de vuelo.
- **PlayerToggleSneakEvent** - Se llama cuando un jugador cambia su estado de agacharse.
- **PlayerToggleSprintEvent** - Se llama cuando un jugador cambia su estado de correr.
- **PlayerUnregisterChannelEvent** - Se llama inmediatamente después de que un jugador desregistre un canal de plugin.
- **PlayerVelocityEvent** - ?? 

## Servidor

- **MapIntializeEvent** - Se llama cuando un mapa se carga.
- **PluginDisableEvent** - Se llama cuando un plugin es desactivado.
- **PluginEnableEvent** - Se llama cuando un plugin es activado.
- **PluginEvent** - Se usa para eventos de activación/desactivación de plugins.
- **RemoteServerCommandEvent** - ?? 
- **ServerCommandEvent** - ?? 
- **ServerEvent** - Eventos misceláneos del servidor.
- **ServerListPingEvent** - Se llama cuando un ping a la lista de servidores llega.
- **ServiceEvent** - Un evento relacionado con un servicio registrado.
- **ServiceRegisterEvent** - Se llama cuando se registra un servicio.
- **ServiceUnregisterEvent** - Se llama cuando se desregistra un servicio.

## Vehículos

- **VehicleBlockCollisionEvent** - Se activa cuando un vehículo colisiona con un bloque.
- **VehicleCollisionEvent** - Se activa cuando un vehículo colisiona.
- **VehicleCreateEvent** - Se activa cuando un vehículo es creado.
- **VehicleDamageEvent** - Se activa cuando un vehículo recibe daño de cualquier fuente.
- **VehicleDestroyEvent** - Se activa cuando un vehículo es destruido por cualquier causa, ya sea natural o por un jugador.
- **VehicleEnterEvent** - Se activa cuando una entidad entra en un vehículo.
- **VehicleEntityCollisionEvent** - Se activa cuando un vehículo colisiona con una entidad.
- **VehicleEvent** - Representa un evento relacionado con vehículos.
- **VehicleExitEvent** - Se activa cuando una entidad viviente sale de un vehículo.
- **VehicleMoveEvent** - Se activa cuando un vehículo se mueve.
- **VehicleUpdateEvent** - Se llama cuando un vehículo se actualiza (?).

## Clima

- **LightningStrikeEvent** - Almacena datos para cuando un rayo golpea.
- **ThunderChangeEvent** - Almacena datos para el cambio del estado de los truenos en un mundo.
- **WeatherChangeEvent** - Almacena datos para cuando cambia el clima en un mundo.
- **WeatherEvent** - Representa un evento relacionado con el clima.

## Mundo

- **ChunkEvent** - Representa un evento relacionado con un Chunk.
- **ChunkLoadEvent** - Se llama cuando un Chunk es cargado.
- **ChunkPopulateEvent** - Se lanza cuando un nuevo Chunk ha terminado de poblarse.
- **ChunkUnloadEvent** - Se llama cuando un Chunk es descargado.
- **PortalCreateEvent** - Se llama cuando un portal es creado.
- **SpawnChangeEvent** - Se llama cuando el punto de aparición del mundo cambia.
- **StructureGrowEvent** - Se llama cuando una estructura orgánica intenta crecer (ejemplo: árbol o hongo gigante) de forma natural o utilizando abono.
- **WorldEvent** - Representa eventos dentro de un mundo.
- **WorldInitEvent** - Se llama cuando un mundo está inicializándose.
- **WorldLoadEvent** - Se llama cuando un mundo es cargado.
- **WorldSaveEvent** - Se llama cuando un mundo es guardado.
- **WorldUnloadEvent** - Se llama cuando un mundo es descargado.