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

- Install TornadoVM. The following example builds TornadoVM with OpenJDK 11 and OpenCL:

```bash
git clone https://github.com/beehive-lab/TornadoVM.git 
cd TornadoVM
./scripts/tornadoVMInstaller.sh --jdk11 --opencl
source source.sh
cd ..
```

**If you cannot build TornadoVM with the installer, try
the [manual installation](https://github.com/beehive-lab/TornadoVM/blob/master/assembly/src/docs/12_INSTALL_WITH_JDK11_PLUS.md)
.**

- Download the JavaFX SDK for your system from: [JavaFX downloads](https://gluonhq.com/products/javafx/). You will need
  the path of the JavaFX SDK for Step 3.

**Note that TornadoVM-Ray-Tracer has been tested with JavaFX version 17.0.2.**

3. Set up the environment and store the variables in a file (e.g. `sources.env`):

```bash 
cd TornadoVM-Ray-Tracer
vim sources.env
export TORNADO_RAY_TRACER_ROOT="${PWD}"
export PATH="${PATH}:${TORNADO_RAY_TRACER_ROOT=}/bin"
export JAVAFX_SDK=<path to JavaFX>/javafx-sdk-17.0.2/
export TORNADO_ROOT=<path to TornadoVM>
export PATH="${PATH}:${TORNADO_ROOT}/bin/bin/"
export TORNADO_SDK=${TORNADO_ROOT}/bin/sdk
export JAVA_HOME=${TORNADO_ROOT}/TornadoVM-OpenJDK11/jdk-11.0.13+8
```

Load the environment:

```bash
source sources.env
```

4. Build TornadoVM-Ray-Tracer:

```bash
mvn clean install
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
