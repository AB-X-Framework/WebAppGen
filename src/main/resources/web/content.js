class PageContent {

    static processTile(specs) {
        document.title = specs.title;
    }

    static renderPage(name) {
        $.get(`/page/specs/${name}`, (specs) => {
            PageContent.processTile(specs);
            var output = [];
            console.log(JSON.stringify(specs.component));
            PageContent.renderComponent( output, specs.component)
            $("#body-content").html(output.join(""));
            M.updateTextFields();
        })
    }

    static renderComponents(output,container) {
        for (var innerComponent of container.components) {
            output.push( "<div>");
            PageContent.renderComponent(output,innerComponent.component);
            output.push("</div>");
        }
    }

    static renderComponent(output,componentSpecs) {
        console.log("JE·"+JSON.stringify(componentSpecs));
        if (componentSpecs.isContainer){
            console.log(JSON.stringify( "AaSA"+componentSpecs.children));
            for (var component of componentSpecs.children) {
                PageContent.renderComponent(output,component)
            }
        }else {
            switch (componentSpecs.type) {
                case "button":
                    PageContent.renderButton(output,componentSpecs.specs);
                    break

            }
        }

    }
    static renderButton(output,specs) {
        var results =
            `<button id="showFormBtn" class="btn waves-effect waves-light"> ${specs.text} 
             </button>`
        output.push(results) ;


    }


}