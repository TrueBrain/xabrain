XaBrain - A Minecraft Mod
=========================

Concept
-------
A collection of mods, for my personal joy.

It contains a lot of ideas of a lot of well known (and less known) mods. It is my attempt to make a collection of mods which have everything I would want from mods. To name a few:

 - Unified electricity
 - Unified energy
 - Sane transport system, with clever (auto-)routing
 - Less overpowered items for SMP

Personally, I only enjoy this game with my friends, so it will be heavily tuned towards SMP over SSP. In fact, it would be rather unlikely I tested SSP ;)


Mods
----

 - mod_ore: Adds Copper and Tin to the game. Other mods that add these? Just ommit thisone.
 - mod_transport: Adds pipes to the game. It cleverly routes packets on demand.


Requirement
------------

- Requires MCForge 3.3.8.164+
- Requires a custom patch for FML (see RenderWorldBlock in my fork on GitHub)
- Requires a modified 'recompile' script (to ignore /server/ for Client, and /client/ for Server)
- Requires a modification to Eclipse project (Exclude \*\*/server/\* in Client, and \*\*/client/\* in Server)


Installation
------------

- setup a MCForge development env
- git clone into src/minecraft
- symlink to src/minecraft_server
- compile
- enjoy


Binaries
--------

To come; I guess I have to setup a Jenkins at one point or another. Stay tuned?
