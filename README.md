# InteractiveImagePanel

InteractiveImagePanel is a Java library that provides an interactive image panel that can be zoomed and moved. The image can be zoomed in and out with the mouse wheel, and moved by clicking and dragging.

## Features

- You can now select three different Image-Fit (like CSS Object-Fit) property to scale and relocate the image on component resize. [NEW]
- Zoom in and out with the mouse wheel
- Move the image by clicking and dragging
- Set the zoom level, move the image, and set the image to be displayed
- Set the maximum and minimum zoom factors
- Set the amount of zoom to be applied on each scroll with mouse wheel
- Set the image to be displayed on the panel
- Set the scaling algorithm to be used for zooming the image

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>io.github.iso53</groupId>
  <artifactId>interactive-image-panel</artifactId>
  <version>1.2.1</version>
</dependency>
```

## Usage
Here is a basic example of how to use the InteractiveImagePanel:
```java
InteractiveImagePanel interactiveImagePanel = new InteractiveImagePanel();
interactiveImagePanel.setImage(image);
interactiveImagePanel.addZoomCapability();
interactiveImagePanel.addMoveCapability();
interactiveImagePanel.setImageFit(InteractiveImagePanel.IMAGE_FIT.COVER);
```

## Contributing
Contributions are welcome! Feel free to open issues or pull requests.

## License
This project is licensed under the [GNU General Public License v3.0](LICENSE).

[![Follow me on GitHub](https://img.shields.io/github/followers/iso53?label=Follow%20%40iso53&style=social)](https://github.com/iso53)

