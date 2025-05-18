class PageContent {

    static processTile(specs) {
        document.title = specs.title;
    }

    static renderPage(name) {
        $.get(`/page/specs/${name}`, (specs) => {
            PageContent.processTile(specs);
            var output = [];
            var js = [];
            PageContent.renderComponent(output,js, specs.component)
            $("#body-content").html(output.join(""));
            for (var line of js){
                eval(line)
            }
            M.updateTextFields();
        })
    }

    static renderComponents(output, js, container) {
        for (var innerComponent of container.components) {
            output.push("<div>");
            PageContent.renderComponent(output, js, innerComponent.component);
            output.push("</div>");
        }
        js.push(container.js);
    }

    static renderComponent(output, js,componentSpecs) {
        js.push(componentSpecs.js);
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
                PageContent.renderComponent(output, js, component)
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
                case "textarea":
                    PageContent.renderTextarea(output, componentSpecs.specs);
                    break
                case "section":
                    PageContent.renderSection(output, componentSpecs.specs);
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
            `<div id="${specs.id}"  class="input-field">
            <input placeholder="${specs.placeholder}" id="f_${specs.id}"  type="text" class="validate">
          <label for="f_${specs.id}">${specs.label}</label></div>`;
        output.push(results);
    }

    static renderTextarea(output, specs){
        var results =
            `<div id="${specs.id}"  class="input-field">
            <textarea placeholder="${specs.placeholder}" id="f_${specs.id}"  type="text" class="materialize-textarea"></textarea>
          <label for="f_${specs.id}">${specs.label}</label></div>`;
        output.push(results);
    }


    static renderSection(output, specs){
        var results =
            `<div id="${specs.id}"  class="section white section-content">
            <div class="row container">
            <h4 class="header">${specs.title}</h4>
             <p class="grey-text text-darken-3">
             ${specs.content}
             </p>
          </div></div>`;
        output.push(results);
    }

}