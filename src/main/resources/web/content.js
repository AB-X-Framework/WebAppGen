class PageContent {

    static processTile(specs) {
        document.title = specs.title;
    }

    static renderPage(name) {
        $.get(`/page/specs/${name}`, (specs) => {
            PageContent.processTile(specs);
            var output = [];
            console.log(JSON.stringify(specs.components));
            PageContent.renderComponents( output, specs.components)
            $("#body-content").html(output.join(""));
        })
    }

    static renderComponents(output,container) {
        for (var innerComponent of container.components) {
            output.push( "<div>");
            PageContent.renderComponent(output,innerComponent.component,innerComponent.innerName)
            output.push("</div>");
        }
    }

    static renderComponent(output,componentSpecs,innerName) {
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