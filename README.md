QuestManager
============

Quest Manager (QM) is a plugin that creates, imports, and manages quests on a Bukkit server.  Quests vary from things as simple as 'deliver this item to this npc' to as complicated as 'kill this horde of monsters, fight a boss, and then have one member of your party be at each of these locations all at once' and so on. 

QM is designed to work completely from config, meaning no programming experience is required to design your own quests. This also means the plugin will work without needing to be recompiled. Just throw QM into your plugins folder, do some setup on the configuration files, and away you go!
QM also, however, provides a extremely simple interface to developers to create their own quests. By coding your own quests, you get more custom functionality than through config-creation.

Abstraction
-----------

Typically in games involving quests, very specific information (such as location information, dialogue & dialogue options, etc) is very closely married (coupled) with the codebase behind it. Quest systems derived from configuration files typically only include text-based RPG's and similar, as more specific information is necessary for games with a higher dimensionality/complexity. To address this issue, QM:

    * Defines an expanded configuration vocabulary
    * Is optimized for large configuration file reading

Features
--------

There already are a large number of features available. A lot of work has been put in to create an more traditional-MMO/RPG feel than what's usually experienced in Minecraft. Features include:

    * Chat-based menu system with automatic handling and proofing
    * Inventory-based menu systems for shops and service providers
    * Multiple NPC types with custom functionality
    * Shops and Service Shops (for selling and crafting)
    * 10 requirement types to build completely custom goals
    * Player stats including current & completed quest count and Fame
    * Self-updated Quest-log and Quest Journal
    * A magic-compass feature
    * A portal feature that allows easy integration with Multiverse Worlds
    * Title system for cross-world rewards
    * Extensive plugin-wide configuration


Still To Come
-------------

An official documentation of the configuration:

- Structure
- Equipment Specification
- Requirement Enumeration/Types
