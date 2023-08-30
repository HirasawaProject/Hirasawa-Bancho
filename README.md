# Hirasawa Project
[![Unit tests](https://github.com/HirasawaProject/Hirasawa-Server/actions/workflows/tests.yml/badge.svg)](https://github.com/HirasawaProject/Hirasawa-Server/actions/workflows/tests.yml) [![Codacy Security Scan](https://github.com/HirasawaProject/Hirasawa-Server/actions/workflows/codacy.yml/badge.svg)](https://github.com/HirasawaProject/Hirasawa-Server/actions/workflows/codacy.yml)

Hirasawa Bancho is a WIP Bancho server for osu! supporting custom plugins written in Kotlin

## The ethos of the project
The project is intended to be an application providing a (close to) vanilla Bancho experience

This project should not provide anything to end user further than what would be expected from the vanilla server itself,
so any nice to have features should instead be done via the plugin support

## Plugins
We support plugins to extend the functionality of the server similarly to how Bukkit handles it for Minecraft utilising 
events for all actions in-game

Plugins currently have access to:
* Add chat commands
* Register to the event system
* Register web routes

Alongside having access to the same systems Hirasawa uses natively

We will also have a plugin store available at some point later down the line of development

## Requirements
* Java 17
* MariaDB
* Gradle

In terms of hardware requirements I've gotten this to run on a Pi Zero

## Builds
Hirasawa builds can be downloaded [here](https://github.com/cg0/Hirasawa-Project/releases), the version number uses the [semver versioning scheme](https://semver.org/)

## Building
To build Hirasawa Bancho you can run `gradle build`, this will generate three jars in `build/libs`:
* `HirasawaBancho-VERSION.jar` - The executable jar with all dependencies included
* `HirasawaBancho-VERSION-api.jar` - The API jar without dependencies
* `HirasawaBancho-VERSION-javadoc.jar` - A jar including generated Javadocs

## Contribution
Hirasawa Bancho is completely open to contribution, please feel free to open tickets or work on what we currently have in the
backlog

The language used in the project is British English, this won't impact language shown to the user but we'd prefer 
keeping the internal language consistent as possible

## Licencing
Hirasawa Bancho is licenced under the MIT licence so do with it as you please
