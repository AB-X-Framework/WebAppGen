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

    static renderComponents(output,componentSpecs) {
        for (var component of componentSpecs) {
            output.push( "<div>");
            PageContent.renderComponent(output,component)
            output.push("</div>");
        }
    }

    static renderComponent(output,componentSpecs) {
        if (componentSpecs.isContainer){
            for (var component of componentSpecs.components) {
                PageContent.renderComponent(output,component)
            }
        }else {
            switch (componentSpecs.type) {
                case "button":
                    output.push(componentSpecs);
                    break

            }
        }

    }



}