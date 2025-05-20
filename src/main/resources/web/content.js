class PageContent {

    static processTile(specs) {
        document.title = specs.title;
    }

    static renderPage(name) {
        $.get(`/page/specs/${name}`, (specs) => {
            PageContent.processTile(specs);
            var output = [];
            var js = [];
            PageContent.renderComponent(output, js, specs.component)
            $("#body-content").html(output.join(""));
            for (var line of js) {
                eval(line)
            }
            M.updateTextFields();

            const elems = document.querySelectorAll('select');
            M.FormSelect.init(elems);

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

    static renderComponent(output, js, componentSpecs) {
        js.push(componentSpecs.js);
        if (componentSpecs.isContainer) {
            var horizontal = componentSpecs.layout === "horizontal";
            var vertical = componentSpecs.layout === "vertical";
            var cv = componentSpecs.layout === "cv";
            if (cv) {
                output.push(`<div class="valign-wrapper row" style="height: 100%;">`)
            } else {
                output.push(`<div id="${componentSpecs.id}">`);
            }
            if (horizontal) {
                output.push('<div class="row">');
            }
            for (var component of componentSpecs.children) {
                if (vertical) {
                    output.push('<div class="row">');
                }
                PageContent.renderComponent(output, js, component)
                if (vertical) {
                    output.push('</div>');
                }
            }
            if (horizontal) {
                output.push('</div>');
            }
            output.push('</div>');
        } else {

            var size = componentSpecs.size;
            output.push(`<div class="col ${size}">`)
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
                case "select":
                    PageContent.renderSelect(output, componentSpecs.specs);
                    break
                case "file":
                    PageContent.renderFile(output, componentSpecs.specs);
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

    static renderTextfield(output, specs) {
        var results =
            `<div id="${specs.id}"  class="input-field">
            <input placeholder="${specs.placeholder}" id="f_${specs.id}"  type="text" class="validate">
          <label for="f_${specs.id}">${specs.label}</label></div>`;
        output.push(results);
    }

    static renderTextarea(output, specs) {
        var results =
            `<div id="${specs.id}"  class="input-field">
            <textarea placeholder="${specs.placeholder}" id="f_${specs.id}"  type="text" class="materialize-textarea"></textarea>
          <label for="f_${specs.id}">${specs.label}</label></div>`;
        output.push(results);
    }


    static renderSection(output, specs) {
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


    static renderFile(output, specs) {
        var results =
            `<input type="file" name="data" id="${specs.id}" required />`;
        output.push(results);
    }

    static renderSelect(output, specs) {
        const optionsHtml = specs.values.map(item =>
            `<option value="${item.key}">${item.value}</option>`
        ).join('');

        var results =
            `<div id="${specs.id}"   class="input-field">
            <select id="${specs.id}">
            ${optionsHtml}
            </select>
            </div>`;
        output.push(results);
    }

}

class App {
    static process(method, args, success, error) {
        App.processBinary(method, args, undefined, success, error);
    }

    static processBinary(method, args, data, success, error) {
        const formData = new FormData();
        if (typeof args === "undefined") {
            args = {};
        }
        formData.append("args", JSON.stringify(args));
        if (typeof data !== "undefined") {
            formData.append("data", data);
        }
        if (typeof success === "undefined") {
            success = function (response) {
                console.log("Method successful", response);
            }
        }
        if (typeof error === "undefined") {
            error = function (xhr, status, error) {
                console.error("Upload failed", status, error);
            }
        }

        $.ajax({
            url:`/process/${method}`, // Replace or define $url
            method: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: success,
            error: error
        });
    }

    static processDownload(method, args, success, error) {
        App.processBinaryDownload(method, args, undefined, success, error);
    }

    static processBinaryDownload(method, args, data){
        const formData = new FormData();
        if (typeof args === "undefined") {
            args = {};
        }
        formData.append("args", JSON.stringify(args));
        if (typeof data !== "undefined") {
            formData.append("data", data);
        }
        fetch(`/process/${method}`, {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (!response.ok) throw new Error("Request failed");
                return response.blob();
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = "model.zip"; // You may dynamically extract filename from headers if needed
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            })
            .catch(err => console.error("Download failed:", err));
    }

}