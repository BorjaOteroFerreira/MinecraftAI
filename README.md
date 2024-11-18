# 🤖 Autonomous NPCs Plugin - Proof of Concept

## 📖 Descripción
Este es un plugin experimental diseñado para **Minecraft** que utiliza la API de **Citizens** para crear **NPCs autónomos** controlados por modelos de inteligencia artificial conectados a **LM Studio**. 

El objetivo es demostrar cómo los NPCs pueden interactuar de manera dinámica y adaptativa, aprovechando modelos de lenguaje avanzados. En el futuro, se planea incorporar compatibilidad con **Ollama** y **Llama-C++** para expandir las capacidades del plugin.

---

## ⚠️ **Estado del Proyecto**
Este es un proyecto de **prueba de concepto (PoC)**. Aunque funcional en su forma básica, no está listo para producción y está destinado únicamente a exploraciones iniciales y pruebas.

---

## 🛠 **Requisitos Previos**
1. **Servidor Minecraft compatible** con plugins basados en Bukkit/Spigot/Paper.
2. **Citizens versión 2.0.36**: Este plugin requiere específicamente esta versión para funcionar correctamente. 
3. Coloca el archivo JAR del plugin en la carpeta `/plugins` del servidor para que pueda ejecutarse.

---

## 🏗 **Instrucciones para Compilación**
### Pasos importantes antes de compilar:
- **No incluyas Citizens en la compilación.** Asegúrate de desmarcar el plugin de `Citizens 2.0.36-SNAPSHOT.jar` para compilacion y mantener unicamente `citizens-v1_21_R2-2.0.36-SNAPSHOT.jar`.


---

## 🚀 **Instalación**
1. Copia el archivo JAR generado en la carpeta `/plugins` de tu servidor Minecraft.
2. Reinicia el servidor.
3. Asegúrate de que **Citizens 2.0.36-SNAPSHOT.jar** también esté en la carpeta `/plugins`.

---

## 🌟 **Características Clave**
- Creación de NPCs autónomos que pueden interactuar dinámicamente con jugadores.
- Conexión a modelos de lenguaje avanzados a través de **LM Studio**.
- Diseño modular para futuras integraciones con **Ollama** y **Llama-C++**.

---

## 📋 **Notas Finales**
Este plugin está en una fase inicial y puede contener errores o limitaciones. Si tienes feedback o sugerencias, no dudes en compartirlas en el apartado de **Issues** del repositorio.

**Autor:** [Borja Otero Ferreira]  
**Licencia:** [MIT]
