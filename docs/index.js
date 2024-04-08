// ******************** Declare Variables ********************
var dependency = `<dependency>
    <groupId>io.github.iso53</groupId>
    <artifactId>interactive-image-panel</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>`;

var javaCode = `InteractiveImagePanel interactiveImagePanel = new InteractiveImagePanel();
interactiveImagePanel.setImage(image);
interactiveImagePanel.setScalingAlgorithm(8);
interactiveImagePanel.addZoomCapability();
interactiveImagePanel.addMoveCapability();`;

// ************************ JS Starts ************************
topNavbarListener();
formatAndAddXML(dependency);
formatAndAddJava(javaCode);

// ******************** Declare Functions ********************
function topNavbarListener() {
    // When scrolling darken the background of top bar
    const navbar = document.getElementById("top_bar");
    window.onscroll = function () {
        if (document.body.scrollTop > 0 || document.documentElement.scrollTop > 0) {
            navbar.classList.add("darken");
        } else {
            navbar.classList.remove("darken");
        }
    };

    // On mobile devices widen-up the top bar on the click of the button
    const menuButton = document.getElementById("top_bar_open_menu_button");
    menuButton.onclick = function () {
        navbar.classList.toggle("open");
    };

    // On mobile devices close/narrow the top bar when clicking one of the links (href)
    navbar.onclick = function (event) {
        if (event.target.tagName.toLowerCase() === "a") {
            navbar.classList.remove("open");
        }
    };

    // On mobile devices if the clicked location outside of topbar, close/narrow the top bar
    window.onclick = function (event) {
        if (!event.target.closest("#top_bar")) {
            navbar.classList.remove("open");
        }
    };
}

function formatAndAddXML(xmlContent) {
    var formattedXML = xmlContent.replace(/<([^>]+)>/g, '<span class="xml-tag">&lt;$1&gt;</span>');

    var codeContainer = document.createElement("div");
    codeContainer.classList.add("code-container");

    var codeElement = document.createElement("pre");
    codeElement.classList.add("code");
    codeElement.innerHTML = formattedXML;

    codeContainer.appendChild(codeElement);

    document.getElementById("dependency").appendChild(codeContainer);
}

function formatAndAddJava(javaCode) {
    // Define Java keywords
    var javaKeywords = ["InteractiveImagePanel", "new", "setImage", "setScalingAlgorithm", "addZoomCapability", "addMoveCapability"];

    // Create a regular expression pattern to match Java keywords
    var pattern = new RegExp("\\b(" + javaKeywords.join("|") + ")\\b", "g");

    // Replace Java keywords with styled span elements
    var formattedJava = javaCode.replace(pattern, '<span class="java-keyword">$1</span>');

    var codeContainer = document.createElement("div");
    codeContainer.classList.add("code-container");

    var codeElement = document.createElement("pre");
    codeElement.classList.add("code");
    codeElement.innerHTML = formattedJava;

    codeContainer.appendChild(codeElement);

    document.getElementById("java_code").appendChild(codeContainer);
}
