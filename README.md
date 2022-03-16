# Java Path Tracer for TornadoVM

![Demo](Demo.png)

## Description

This project aims to build a real-time path tracer in Java, accelerated on heterogeneous hardware using
[TornadoVM](https://www.tornadovm.org/).

The project uses [JavaFX](https://openjfx.io/) to obtain a canvas for a graphical context to draw on and to build an
interactive graphical user interface, while the entire rendering process consists of manual calculation of the colors of
the pixels through tracing rays, applying the Blinn-Phong shading model and sampling soft shadows.

The embarrassingly parallel property of path tracing allows for concurrent computation of each individual pixel color,
achieving up to 930x performance increase on an Nvidia RTX2060 GPU against the default sequential Java execution on an
Intel i7-8550u CPU.

## Installation

1. Clone and set up the project as a [maven](https://maven.apache.org/) project.
2. Set up TornadoVM with the instructions found at: [TornadoVM GitHub](https://github.com/beehive-lab/TornadoVM), for
   this project, please build only the OpenCL backend.
3. Download the JavaFX SDK for your system from: [JavaFX downloads](https://gluonhq.com/products/javafx/), latest LTS
   version is recommended
4. To run the program, compile and run App.java, for which obtain the VM options as below:
    1. Navigate to your TornadoVM installation folder e.g.: `cd /home/alice/Downloads/tornadovm/`
    2. Set your Tornado environment variables using `source source.sh`<br/><br/>
    3. Obtain tornado flags using: `tornado --printFlags`
    4. Copy the output starting from `server`. You should have flags similar to the
       following:<br/>```-server -XX:-UseCompressedOops -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -Djava.library.path=/home/alice/Downloads/tornadovm/bin/sdk/lib --module-path .:/home/alice/Downloads/tornadovm/bin/sdk/share/java/tornado -Dtornado.load.api.implementation=uk.ac.manchester.tornado.runtime.tasks.TornadoTaskSchedule -Dtornado.load.runtime.implementation=uk.ac.manchester.tornado.runtime.TornadoCoreRuntime -Dtornado.load.tornado.implementation=uk.ac.manchester.tornado.runtime.common.Tornado -Dtornado.load.device.implementation.opencl=uk.ac.manchester.tornado.drivers.opencl.runtime.OCLDeviceFactory -Dtornado.load.device.implementation.ptx=uk.ac.manchester.tornado.drivers.ptx.runtime.PTXDeviceFactory -Dtornado.load.device.implementation.spirv=uk.ac.manchester.tornado.drivers.spirv.runtime.SPIRVDeviceFactory -Dtornado.load.annotation.implementation=uk.ac.manchester.tornado.annotation.ASMClassVisitor -Dtornado.load.annotation.parallel=uk.ac.manchester.tornado.api.annotations.Parallel -XX:+UseParallelOldGC -XX:-UseBiasedLocking @/home/alice/Downloads/tornadovm/bin/sdk/etc/exportLists/common-exports @/home/alice/Downloads/tornadovm/bin/sdk/etc/exportLists/opencl-exports --add-modules ALL-SYSTEM,tornado.runtime,tornado.annotation,tornado.drivers.common,tornado.drivers.opencl```<br/><br/>
    5. Add the path to the lib folder of your downloaded JavaFX SDK to `--module-path`
       e.g:<br/>```--module-path .:/home/alice/Downloads/tornadovm/bin/sdk/share/java/tornado::/home/alice/Downloads/javafx-sdk-17.0.2/lib```<br/>
       Note the `::` to separate the Tornado and the JavaFX module paths.<br/><br/>
    6. Add the following to `--add-modules`: `javafx.controls,javafx.graphics,javafx.fxml`
       e.g:<br/>```--add-modules ALL-SYSTEM,tornado.runtime,tornado.annotation,tornado.drivers.common,tornado.drivers.opencl,javafx.controls,javafx.graphics,javafx.fxml```<br/><br/>
    7. Add an extra option: `-Dprism.vsync=false`<br/><br/>
    8. You should end up with VM options similar to the
       following:<br/>```-server -XX:-UseCompressedOops -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -Djava.library.path=/home/alice/Downloads/tornadovm/bin/sdk/lib --module-path .:/home/alice/Downloads/tornadovm/bin/sdk/share/java/tornado::/home/alice/Downloads/javafx-sdk-17.0.2/lib -Dtornado.load.api.implementation=uk.ac.manchester.tornado.runtime.tasks.TornadoTaskSchedule -Dtornado.load.runtime.implementation=uk.ac.manchester.tornado.runtime.TornadoCoreRuntime -Dtornado.load.tornado.implementation=uk.ac.manchester.tornado.runtime.common.Tornado -Dtornado.load.device.implementation.opencl=uk.ac.manchester.tornado.drivers.opencl.runtime.OCLDeviceFactory -Dtornado.load.device.implementation.ptx=uk.ac.manchester.tornado.drivers.ptx.runtime.PTXDeviceFactory -Dtornado.load.device.implementation.spirv=uk.ac.manchester.tornado.drivers.spirv.runtime.SPIRVDeviceFactory -Dtornado.load.annotation.implementation=uk.ac.manchester.tornado.annotation.ASMClassVisitor -Dtornado.load.annotation.parallel=uk.ac.manchester.tornado.api.annotations.Parallel -XX:+UseParallelOldGC -XX:-UseBiasedLocking @/home/alice/Downloads/tornadovm/bin/sdk/etc/exportLists/common-exports @/home/alice/Downloads/tornadovm/bin/sdk/etc/exportLists/opencl-exports --add-modules ALL-SYSTEM,tornado.runtime,tornado.annotation,tornado.drivers.common,tornado.drivers.opencl,javafx.controls,javafx.graphics,javafx.fxml -Dprism.vsync=false```

## Publication

TO DO

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

TO DO