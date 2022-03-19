## Java Ray Tracer for TornadoVM - Sequential Implementation

![Demo](Demo.png)

# Description

This project aims to build a ray tracing engine in Java, accelerated on heterogeneous hardware using
TornadoVM ([https://www.tornadovm.org/](https://www.tornadovm.org/)).

The current branch contains a sequential, object-oriented implementation of the ray tracer, built from scratch for the
purposes of understanding the basics behind the algorithms ahead of performing an refactor compatible with acceleration
on heterogeneous hardware using TornadoVM.

This branch uses no external libraries.

The settings can be tweaked and objects can be added to the scene in Main.java. A render of the scene is output to "
Render.png".