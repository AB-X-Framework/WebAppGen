class PageContent {

    static processTile(specs) {
        document.title = specs.title;
    }

    static renderPage(name) {
        $.get(`/page/specs/${name}`, (specs) => {
            PageContent.processTile(specs);
            var output = [];
            PageContent.renderComponents( output, specs.components)
            $("#body-content").html(output.join(""));
        })
    }

    static renderComponents(componentSpecs) {
        for (var component of componentSpecs) {
            output.push( "<div>hi</div>");
        }
    }
}