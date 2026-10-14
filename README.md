# Java Ray Tracer for TornadoVM

![Demo](Demo.png)

## Description

This project aims to build a real-time ray tracer in Java, accelerated on heterogeneous hardware using
[TornadoVM](https://www.tornadovm.org/).

The project uses [JavaFX](https://openjfx.io/) to obtain a canvas for a graphical context to draw on and to build an
interactive graphical user interface, while the entire rendering process consists of manual calculation of the colors of
the pixels through tracing rays, applying the Blinn-Phong shading model and sampling soft shadows.

The embarrassingly parallel property of ray tracing allows for concurrent computation of each individual pixel color,
achieving up to 930x performance increase on an Nvidia RTX2060 GPU against the default sequential Java execution on an
Intel i7-8550u CPU.

## Installation

1. Clone the project:

```bash 
git clone https://github.com/Vinhixus/TornadoVM-Ray-Tracer.git
```

2. Install dependencies:

- Install TornadoVM (via SDKMAN!)

TornadoVM is distributed through our [**official website**](https://www.tornadovm.org/downloads) and **SDKMAN!**. Install a version that matches your OS, architecture, and accelerator backend.

All TornadoVM SDKs are available on the [SDKMAN! TornadoVM page](https://sdkman.io/sdks/tornadovm/).

```bash
sdk install tornadovm
```

- Use JDK 21 or any JDK 22+ (e.g. JDK 25)

TornadoVM-Ray-Tracer builds and runs with **JDK 21** or any **JDK 22 or newer**, as long as the installed TornadoVM distribution matches the JDK in use (TornadoVM ships separate `jdk21` and `jdk22plus` builds). The Maven build auto-detects the running JDK and selects the matching TornadoVM dependency:

| JDK | Maven profile (auto-activated) | TornadoVM dependency |
|-----|-------------------------------|----------------------|
| 21.x | `jdk21` | `tornado-api` `*-jdk21` |
| 22+  | `jdk22plus` | `tornado-api` `*-jdk22plus` |

```bash
sdk install java 21.0.2-open    # for JDK 21
sdk install java 25.0.2-open    # for JDK 25 (any JDK 22+ works)
```

- Download the JavaFX SDK for your system from: [JavaFX downloads](https://gluonhq.com/products/javafx/). You will need
  the path of the JavaFX SDK for Step 3.

**Note that TornadoVM-Ray-Tracer has been tested with JavaFX version 18.**

3. Set up the environment and store the variables in a file (e.g. `sources.env`):

```bash 
cd TornadoVM-Ray-Tracer
vim sources.env
export TORNADO_RAY_TRACER_ROOT="${PWD}"
export PATH="${PATH}:${TORNADO_RAY_TRACER_ROOT=}/bin"
export JAVAFX_SDK=<path to JavaFX>/javafx-sdk-18/
```

Load the environment:

```bash
source sources.env
```

4. Build TornadoVM-Ray-Tracer:

```bash
mvn clean install
```

The JDK profile is selected automatically from the JDK running Maven. To force a specific variant regardless of the running JDK:

```bash
mvn clean install -P jdk21,'!jdk22plus'   # force the JDK 21 variant
```

5. Run TornadoVM-Ray-Tracer:

**With GUI:**

```bash
tornadovm-ray-tracer
```

**Without GUI in benchmarking mode:**

```bash
tornadovm-ray-tracer benchmark
```

## Author

### Vinh Pham Van

LinkedIn: [vinh-pham-van](https://www.linkedin.com/in/vinh-pham-van/)  
Email: [phamvvinh1998@gmail.com](mailto:phamvvinh1998@gmail.com)

### With special thanks to:

**Christos Kotselidis:** Supervisor & TornadoVM Project Leader  
**Juan Fumero:** TornadoVM Lead Architect  
**Thanos Stratikopoulos:** TornadoVM Senior Solutions Architect  
**Maria Xekalaki:** TornadoVM Principal Software Engineer  
**Florin Blanaru:** TornadoVM Senior Software Engineer

## Licenses

[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)](https://github.com/beehive-lab/TornadoVM/blob/master/LICENSE_APACHE2)
