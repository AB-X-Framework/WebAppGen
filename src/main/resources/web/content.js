class PageContent {

    static processTile(specs) {
        document.title = specs.title;
    }

    static renderPage(name) {
        $.get(`/page/specs/${name}`, (specs) => {
            PageContent.processTile(specs);
            var output = [];
            console.log(JSON.stringify(specs.component));
            PageContent.renderComponent(output, specs.component)
            $("#body-content").html(output.join(""));
            M.updateTextFields();
        })
    }

    static renderComponents(output, container) {
        for (var innerComponent of container.components) {
            output.push("<div>");
            PageContent.renderComponent(output, innerComponent.component);
            output.push("</div>");
        }
    }

    static renderComponent(output, componentSpecs) {
        if (componentSpecs.isContainer) {
            var horizontal = componentSpecs.layout === "horizontal";
            var vertical = componentSpecs.layout === "vertical";
            if (horizontal){
                output.push('<div class="row">');
            }
            for (var component of componentSpecs.children) {
                if (vertical){
                    output.push('<div class="row">');
                }
                PageContent.renderComponent(output, component)
                if (vertical){
                    output.push('</div>');
                }
            }
            if (horizontal){
                output.push('</div>');
            }
        } else {

            switch (componentSpecs.type) {
                case "button":
                    PageContent.renderButton(output, componentSpecs);
                    break
                case "img":
                    PageContent.renderImg(output, componentSpecs);
                    break

            }
        }

    }

    static renderButton(output, componentSpecs) {
        var specs = componentSpecs.specs;
        var size = componentSpecs.size;
        var results =
            `<div class="col s${size}"><button id="showFormBtn" class="btn waves-effect waves-light "> ${specs.text} 
             </button></div>`
        output.push(results);
    }

    static renderImg(output, componentSpecs) {
        var specs = componentSpecs.specs;
        var size = componentSpecs.size;
        var results =
            `<div class="col s${size}">
        <img src="/binary/${specs.src}" class="responsive-img" alt="example">
</div>`
        output.push(results);
    }


}