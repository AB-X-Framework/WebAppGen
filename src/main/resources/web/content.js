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
            output.push(`<div id="${componentSpecs.id}">`);
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
            output.push('</div>');
        } else {

            var size = componentSpecs.size;
            output.push(`<div class="col s${size}">`)
            componentSpecs.specs.id = componentSpecs.id;
            switch (componentSpecs.type) {
                case "button":
                    PageContent.renderButton(output, componentSpecs.specs);
                    break
                case "img":
                    PageContent.renderImg(output, componentSpecs.specs);
                    break
                case "textfield":
                    PageContent.renderTextfield(output, componentSpecs.specs);
                    break

            }
            output.push(`</div>`);
        }

    }

    static renderButton(output, specs) {
        var results =
            `<button id="${specs.id}" class="btn waves-effect waves-light "> ${specs.text} 
             </button>`
        output.push(results);
    }

    static renderImg(output, specs) {
        var results =
            `<img id="${specs.id}"  src="/binary/${specs.src}" class="responsive-img" alt="example">`
        output.push(results);
    }

    static renderTextfield(output, specs){
        var results =
            `<div id="${specs.id}"  class="input-field"></div>
            <input placeholder="${specs.placeholder}" id="f_${specs.id}"  type="text" class="validate">
          <label for="f_${specs.id}">${specs.label}</label></div>`;
        output.push(results);

    }


}