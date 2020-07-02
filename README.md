# Hirasawa Project
Hirasawa Project is a WIP server for osu! supporting custom plugins written in Kotlin

The idea is to have a vanilla Bancho experience with a basic web experience which is fully customisable via plugins

## Plugins
We support plugins to extend the functionality of the server similarly to how Bukkit handles it forMinecraft utilising 
events for all actions in-game

Plugins currently have access to:
* Add chat commands
* Register to the event system
* Register web routes

Alongside having access to the same systems Hirasawa uses natively

We will also have a plugin store available at some point later down the line of development

## Requirements
* Java 8
* Mysql (soon to be configurable)
* Gradle

In terms of hardware requirements I've gotten this to run on a Pi Zero

## Builds
Hirasawa builds can be downloaded [here](https://github.com/cg0/Hirasawa-Project/releases), the version number uses the
format YYYYDDMM using the UTC timezone

## Building
Building Hirasawa is very easy, for building testing builds `gradle build` will output to 
`build/libs/HirasawaProject-Testing.jar`

If you want to build a production jar `gradle prod build` will output to `build/libs/HirasawaProject-VERSION.jar` using
a generated version number based on the date, Hirasawa will also be aware of the version internally

## Contribution
Hirasawa is completely open to contribution, please feel free to open tickets or work on what we currently have in the
backlog

## Licencing
Hirasawa Project is licenced under the MIT licence so do with it as you please