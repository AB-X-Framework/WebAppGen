function parseBoolean(value) {
    if (typeof value === 'boolean') return value;
    if (typeof value === 'string') {
        const lower = value.trim().toLowerCase();
        return lower === 'true';
    }
    return Boolean(value);
}

class PageContent {

    static setActiveMenuItemText(containerElement, textToMatch) {
        // Find all <li> elements that have <a> inside
        $(containerElement).find('li').each(function () {
            const anchorText = $(this).find('a').text().trim();
            if (anchorText === textToMatch) {
                $(this).addClass('active');
            } else {
                $(this).removeClass('active'); // optional: remove active from others
            }
        });
    }

    static setActiveMenuItemHref(containerElement, textToMatch) {
        // Find all <li> elements that have <a> inside
        $(containerElement).find('li').each(function () {
            const anchorText = $(this).find('a').attr("href").trim();
            if (anchorText === textToMatch) {
                $(this).addClass('active');
            } else {
                $(this).removeClass('active'); // optional: remove active from others
            }
        });
    }

    static openModal(modal) {
        M.Modal.init(modal).open();
    }

    static closeModal(modal) {
        M.Modal.init(modal).close();
    }

    static hide(element) {
        $(element).closest('.input-field').hide();
    }

    static show(element) {
        $(element).closest('.input-field').show();
    }

    static processTile(specs) {
        document.title = specs.title;
    }

    static processJS(js) {
        for (var line of js) {
            try {
                eval(line);
            } catch (e) {
                console.log(line);
                throw e;
            }
        }
    }

    static renderPage(name) {
        $.get(`/page/specs/${name}`, (specs) => {
            PageContent.processTile(specs);
            var output = [];
            var js = [];
            PageContent.renderCSS(output, specs.css);
            PageContent.renderScripts(output, specs.scripts);
            PageContent.renderComponent(output, js, specs.component)
            $("#body-content").html(output.join(""));
            M.updateTextFields();
            PageContent.processJS(js);
        });
    }

    static renderCSS(output, cssList) {
        for (var css of cssList) {
            output.push(`<link href="${css}" rel="stylesheet">`)
        }
    }

    static renderScripts(output, jsList) {
        for (var js of jsList) {
            output.push(`<script src="${js}"></script>`)
        }
    }

    static renderComponent(output, js, componentSpecs) {
        if (componentSpecs.isContainer) {
            if (componentSpecs.layout === "nav") {
                PageContent.renderNav(output, js, componentSpecs);
            } else if (componentSpecs.layout === "horizontal") {
                PageContent.renderHorizontal(output, js, componentSpecs);
            } else if (componentSpecs.layout === "container") {
                PageContent.renderContainer(output, js, componentSpecs);
            } else if (componentSpecs.layout === "vertical") {
                PageContent.renderVertical(output, js, componentSpecs);
            } else if (componentSpecs.layout === "popup") {
                PageContent.renderPopup(output, js, componentSpecs);
            } else if (componentSpecs.layout === "menu") {
                PageContent.renderMenu(output, js, componentSpecs);
            } else {
                PageContent.renderTopLayout(output, js, componentSpecs);
            }
        } else {
            componentSpecs.specs.id = componentSpecs.id;
            switch (componentSpecs.type) {
                case "button":
                    PageContent.renderButton(output, componentSpecs.specs);
                    break;
                case "menuImg":
                    PageContent.renderMenuImg(output,js, componentSpecs);
                    break;
                case "menuItem":
                    PageContent.renderMenuItem(output,js, componentSpecs);
                    break;
                case "menuItems":
                    PageContent.renderMenuItems(output,js, componentSpecs);
                    break;
                case "img":
                    PageContent.renderImg(output, componentSpecs.specs);
                    break;
                case "canvas":
                    PageContent.renderCanvas(output, js, componentSpecs.specs);
                    break;
                case "textfield":
                    PageContent.renderTextfield(output, js, componentSpecs.specs);
                    break;
                case "password":
                    PageContent.renderPassword(output, componentSpecs.specs);
                    break;
                case "textarea":
                    PageContent.renderTextarea(output, componentSpecs.specs);
                    break;
                case "section":
                    PageContent.renderSection(output, componentSpecs.specs);
                    break;
                case "label":
                    PageContent.renderLabel(output, componentSpecs.specs);
                    break;
                case "autocomplete":
                    PageContent.renderAutocomplete(output, js, componentSpecs.specs);
                    break;
                case "select":
                    PageContent.renderSelect(output, js, componentSpecs.specs);
                    break;
                case "file":
                    PageContent.renderFile(output, componentSpecs.specs);
                    break;
                case "modal":
                    PageContent.renderModal(output, js, componentSpecs.specs);
                    break;
                case "jsEditor":
                    PageContent.renderJsEditor(output, componentSpecs.specs);
                    break;
                case "switch":
                    PageContent.renderSwitch(output, js, componentSpecs.specs);
                    break;
                case "header":
                    PageContent.renderHeader(output, componentSpecs.specs);
                    break;
                case "okCancelModal":
                    PageContent.renderOkCancelModal(output, js, componentSpecs.specs);
                    break;
                case "div":
                    PageContent.renderDiv(output, componentSpecs.specs);
                    break;
                case "divider":
                    PageContent.renderDivider(output, componentSpecs.specs);
                    break;
            }
        }
        if (componentSpecs.js !== "") {
            js.push(componentSpecs.js);
        }
    }

    static renderNav(output, js, componentSpecs) {
        var children = componentSpecs.children;
        output.push(`<header>
            <div class="container">
                <div class="container">
                    <a href="#" data-target="nav-mobile"
                        class="top-nav sidenav-trigger waves-effect waves-light circle hide-on-large-only">
                        <i  class="material-icons">menu</i>
                    </a>
                </div>
                <ul id="nav-mobile" class="sidenav sidenav-fixed">`)
        PageContent.renderComponent(output, js, children[0])
        output.push(`</ul>
        </header>
        <main>
            <div class="section" id="index-banner">
                <div class="container">  
                    <div class="row" style="margin-bottom: 0;">`);
        PageContent.renderComponent(output, js, children[1])
        output.push(`</div>
                </div>
            </div>
        </main>`)
    }

    static renderHorizontal(output, js, componentSpecs) {
        output.push(`<div  style="height: 100%; margin-bottom: 0;" id="${componentSpecs.id}" class="row">`);
        for (var component of componentSpecs.children) {
            output.push(`<div class="col ${component.size}" >`);
            PageContent.renderComponent(output, js, component);
            output.push('</div>');
        }
        output.push('</div>');
    }

    static renderVertical(output, js, componentSpecs) {
        output.push(`<div  id="${componentSpecs.id}">`);
        for (var component of componentSpecs.children) {
            output.push(`<div class="row ${component.size}">`);
            PageContent.renderComponent(output, js, component)
            output.push('</div>');
        }
        output.push('</div>');
    }

    static renderPopup(output, js, componentSpecs) {
        output.push(`<div  id="${componentSpecs.id}" class="modal">
    <div class="modal-content">`);
        let finalIndex = componentSpecs.children.length - 1;
        for (var i = 0; i < finalIndex; ++i) {
            PageContent.renderComponent(output, js, componentSpecs.children[i]);
        }
        output.push('</div> <div class="modal-footer">');
        PageContent.renderComponent(output, js, componentSpecs.children[finalIndex]);
        output.push('</div>');
        js.push(`M.Modal.init(${componentSpecs.id});`);
        output.push('</div>');
    }

    static renderTopLayout(output, js, componentSpecs) {
        output.push(`<div  id="${componentSpecs.id}">`);
        let first = false;
        for (var component of componentSpecs.children) {
            if (first) {
                first = true;
                output.push(`<div class="row ${component.size}">`);
            } else {
                output.push(`<div class=" ${component.size}">`);
            }
            PageContent.renderComponent(output, js, component)
            output.push('</div>');
        }
        output.push('</div>');
    }


    static renderContainer(output, js, componentSpecs) {
        output.push(`<div class=" container ${componentSpecs.size}" id="${componentSpecs.id}">`);
        for (var component of componentSpecs.children) {
            output.push(`<div class="row ${component.size}">`);
            PageContent.renderComponent(output, js, component)
            output.push('</div>');
        }
        output.push('</div>');
    }

    static renderButton(output, specs) {
        var results =
            `<button id="${specs.id}" class="btn waves-effect waves-light  "> ${specs.title} 
             </button>`
        output.push(results);
    }

    static renderImg(output, specs) {
        var results =
            `<img id="${specs.id}"  src="${specs.src}" class="responsive-img" style="max-height: 100%" alt="example">`
        output.push(results);
    }

    static renderCanvas(output, js, specs) {
        var results =
            `<canvas id="${specs.id}" width="100%" height="${specs.height}"></canvas>`
        output.push(results);

    }

    static renderDiv(output, specs) {
        var results =
            `<div id="${specs.id}"  ></div>`
        output.push(results);
    }

    static renderDivider(output, specs) {
        var results =
            `<div class="divider" id="${specs.id}"  ></div>`
        output.push(results);
    }

    static renderTextfield(output, js, specs) {
        let content;
        if (typeof specs.content === "undefined") {
            content = "";
        } else {
            content = specs.content;
        }
        var results =
            `<div  class="input-field">
            <input placeholder="${specs.label}" id="${specs.id}"  type="text" class="validate" value="${content}">
          <label for="${specs.id}">${specs.title}</label></div>`;
        output.push(results);
        if (typeof specs.src !== "undefined" && specs.src !== "") {
            js.push(`$.get('${specs.src}',(res)=>{$(${specs.id}).val(res)})`);
        }
    }

    static renderPassword(output, specs) {
        var results =
            `<div  class="input-field">
            <input placeholder="${specs.content}" id="${specs.id}"  type="password" class="validate">
          <label for="f_${specs.id}">${specs.title}</label></div>`;
        output.push(results);
    }

    static renderModal(output, js, specs) {
        var results = `<div id="${specs.id}" class="modal">
        <div class="modal-content">
            <h4>${specs.title}</h4>
            <p>${specs.content}</p>
        </div>
        <div class="modal-footer">
            <a href="#!" class="modal-close waves-effect waves-green btn-flat">OK</a>
        </div></div>`;
        js.push(`M.Modal.init(${specs.id});`)
        output.push(results);

    }

    static renderTextarea(output, specs) {
        let content;
        if (typeof specs.content === "undefined") {
            content = "";
        } else {
            content = specs.content;
        }
        var results =
            `<div class="input-field">
            <textarea placeholder="${specs.label}" id="${specs.id}"  type="text" class="materialize-textarea">${content}</textarea>
          <label for="${specs.id}">${specs.title}</label></div>`;
        output.push(results);
    }


    static renderSection(output, specs) {
        var results =
            `<div id="${specs.id}"  class="section white section-content">
            <div class="row ">
            <${specs.size} class="header">${specs.title}</${specs.size}>
             <p class="grey-text text-darken-3">
             ${specs.content.replaceAll("\n", "</><p class=\"grey-text text-darken-3\">")}
             </p>
          </div></div>`;
        output.push(results);
    }

    static renderLabel(output, specs) {
        var results =
            `<p id="${specs.id}" class="grey-text text-darken-3">
             ${specs.content.replaceAll("\n", "</><p class=\"grey-text text-darken-3\">")}
             </p>`;
        output.push(results);
    }

    static renderFile(output, specs) {
        var results =
            `<div class="input-field">
          <label for="${specs.id}" class="active">${specs.title}</label>
          <div class="file-field input-field">
              <input type="file" id="${specs.id}">
            <div class="file-path-wrapper">
              <input class="file-path validate" type="text" placeholder="${specs.content}">
            </div>
          </div>
        </div>`;
        output.push(results);
    }

    static renderAutocomplete(output, js, specs) {
        //PageContent.renderSelect(output,js,specs);
        output.push(` <div class="input-field" >
            <input type="text" id="${specs.id}" class="autocomplete">
          <label for="${specs.id}">${specs.title}</label></div>`);
        if (typeof specs.src !== "undefined" && specs.src.trim() !== "") {
            js.push(`
            $.get("${specs.src}",(resultList)=>{
                const $input = $(${specs.id});
                ${specs.id}._data = Object.fromEntries(resultList.map(item => [item, null]));
                const instance = M.Autocomplete.init($input, {
                    data: ${specs.id}._data ,
                    minLength: 0
                });
            });
            `);
        } else {
            js.push(`
                const $input = $(${specs.id});
                ${specs.id}._data = Object.fromEntries(${JSON.stringify(specs.values)}.map(item => [item, null]));
                const instance = M.Autocomplete.init($input, {
                    data: ${specs.id}._data,
                    minLength: 0
                });
            `);
        }
    }

    static renderSelect(output, js, specs) {
        if (typeof specs.src !== "undefined" && specs.src.trim() !== "") {
            var results =
                `<div   class="input-field">
            <select id="${specs.id}">
             <option value="" disabled selected></option>
            </select>
             <label>${specs.title}</label>
            </div>`;
            js.push(`$.get("${specs.src}",(resultList)=>{resultList.forEach(function(item) {
                $(${specs.id}).append($('<option>', {
                    value: item,
                    text: item
               }));
              })
              M.FormSelect.init(${specs.id});
            });`);
            output.push(results);
            if (specs.onChange) {
                js.push(`$(${specs.id}).on('change', function () {
        ${specs.onChange}( $(this).val());
      });`);
            }
        } else {
            let optionsHtml;
            if (typeof specs.values !== "undefined") {
                optionsHtml = specs.values.map(item =>
                    `<option value="${item.value}">${item.text}</option>`
                ).join('');
            } else {
                optionsHtml = "";
            }
            var results =
                `<div   class="input-field">
            <select id="${specs.id}">
             <option value="" disabled selected></option>
            ${optionsHtml}
            </select>
             <label>${specs.title}</label>
            </div>`;
            js.push(`$(${specs.id}).formSelect();`);
            if (specs.onChange) {
                js.push(`$(${specs.id}).on('change', function () {
                    ${specs.onChange}( $(this).val());
                });`);
            }
            output.push(results);
        }
    }

    static renderJsEditor(output, specs) {
        var result = `
    <script src="/resources/binary/ace/ace.js"></script>
    <div id="${specs.id}" style="height: ${specs.height}; width: 100%">${specs.content}</div>
    <script>
       { const editor = ace.edit("${specs.id}");
        editor.setTheme("ace/theme/monokai"); // Change theme if desired
        editor.session.setMode("ace/mode/javascript"); // JS highlighting
        editor.setOptions({
            fontSize: "14px",
            showPrintMargin: false,
            wrap: true,
            useWorker: true // Enables JS syntax checking
        });
        }
    </script>`;
        output.push(result);
    }

    static renderHeader(output, specs) {
        var links = "";
        for (var link of specs.values) {
            links += `<li><a href="${link.value}">${link.text}</li></a>`
        }
        var result = `<nav id="${specs.id}" style="margin-bottom: 5px;">
            <div>
                <a href="#" class="left" style="height: 64px; ">
                    <img src="${specs.src}" alt="Logo" style="height: 60px; padding: 2px;">
                </a>
                <ul class="right">
                    ${links}
                </ul>
            </div>
        </nav>`
        output.push(result)
    }

    static renderOkCancelModal(output, js, specs) {
        let html = `
        <div id="${specs.id}" class="modal">
          <div class="modal-content">
           <h5>${specs.title}</h5>
          <p>${specs.content}</p></div>
          <div class="modal-footer">
            <button class="modal-close btn green" id="${specs.id}_ok">${specs.ok}</button>
            <button class="modal-close btn red" id="${specs.id}_cancel">${specs.cancel}</button>
          </div>
        </div>
      `;
        js.push(`${specs.id}.ok=${specs.id}_ok`);
        js.push(`${specs.id}.cancel=${specs.id}_cancel`);
        output.push(html);
    }


    static renderSwitch(output, js, specs) {
        let result = `<div class="input-field">
            <p>${specs.title}</p>
            <div class="switch">
                <label>
                    ${specs.cancel}
                    <input id="${specs.id}" type="checkbox" >
                    <span class="lever"></span>
                    ${specs.ok}
                </label>
            </div>
        </div>`;
        js.push(`if ("${specs.src}" === "true"){
            ${specs.id}.checked = true;
        } else if ("${specs.src}" != "false"){
            $.get("${specs.src}",(result)=>{
                ${specs.id}.checked = parseBoolean(result);
            });
        }`)
        output.push(result);
    }


    static renderMenuImg(output, js, specs) {
        let innerSpecs = specs.specs;
        output.push(`
        <a href="#" class="left" style="height: 64px; ">
      <img src="/resources/binary/webappgen/abx.png" alt="Logo" style="height: 60px; padding: 2px;">
    </a>`);
    }

    static renderMenuItem(output, js, specs) {
        let innerSpecs = specs.specs;
        output.push(`  <ul class="${specs.size}"><li><a   id="${innerSpecs.id}" >${innerSpecs.title}</a></li></ul>`);
    }

    static renderMenuItems(output, js, specs) {
        let innerSpecs = specs.specs;
        const dropdownData = `dropdown_${innerSpecs.id}`
        output.push(`<ul id="${dropdownData}" class="dropdown-content">`);
        js.push(`${innerSpecs.id}.items=[]`);
        for (let entry of innerSpecs.values) {
            output.push(` <li><a id="${innerSpecs.id}_${entry.value}" href="#!">${entry.text}</a></li>`);
            js.push(`${innerSpecs.id}.${entry.value}=${innerSpecs.id}_${entry.value}`);
            js.push(`${innerSpecs.id}.items.push(${innerSpecs.id}_${entry.value})`);
        }
        output.push(`</ul>`);
        output.push(`<ul class="${specs.size}">
            <li>
                <a id="${innerSpecs.id}" class="dropdown-trigger" href="#!" data-target="${dropdownData}">Dropdown
            <i class="material-icons right">arrow_drop_down</i></a></li>
        </ul>`);
        js.push(` $('#${innerSpecs.id}').dropdown({ coverTrigger: false });`)

    }

    static renderMenu(output, js, specs) {
        output.push(` 
        <!-- Dropdown Structure -->
        <nav id="${specs.id}" >
          <div class="nav-wrapper">`);
        for (var component of specs.children) {
            PageContent.renderComponent(output, js, component)
        }
        output.push(`            
          </div>
        </nav>   
        `)
    }

    static showModal(title, content) {
        // Create modal container
        const modal = document.createElement('div');
        modal.className = 'modal';
        modal.id = 'temp-modal';

        // Modal content
        modal.innerHTML = `
        <div class="modal-content">
          <h4>${title}</h4>
          <p>${content}</p>
        </div>
        <div class="modal-footer">
          <a href="#!" class="modal-close waves-effect waves-green btn-flat">OK</a>
        </div>
      `;

        // Append to body
        document.body.appendChild(modal);

        // Initialize and open the modal
        const instance = M.Modal.init(modal, {
            onCloseEnd: () => modal.remove() // Cleanup after closing
        });
        instance.open();
    }


}

class App {
    static process(method, args, success, error) {
        App.processBinary(method, args, undefined, success, error);
    }

    static processBinary(method, args, dataFiles, success, error) {
        const formData = new FormData();
        if (typeof args === "undefined") {
            args = {};
        }
        formData.append("args", JSON.stringify(args));
        if (typeof dataFiles !== "undefined") {
            Object.entries(dataFiles).forEach(([key, value]) => {
                formData.append(key, value);
            });

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
            url: `/process/${method}`, // Replace or define $url
            method: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: success,
            error: error
        });
    }

    static processDownload(method, args, error) {
        App.processBinaryDownload(method, args, undefined, error);
    }

    static processBinaryDownload(method, args, dataFiles, error) {
        const formData = new FormData();
        if (typeof args === "undefined") {
            args = {};
        }
        formData.append("args", JSON.stringify(args));
        if (typeof dataFiles !== "undefined") {
            Object.entries(dataFiles).forEach(([key, value]) => {
                formData.append(key, value);
            });
        }
        fetch(`/process/${method}`, {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (!response.ok) throw new Error("Request failed");
                // Extract filename from headers
                const disposition = response.headers.get('Content-Disposition');
                let filename = 'result.dat';
                if (disposition && disposition.indexOf('attachment') !== -1) {
                    const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    const matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1]) {
                        filename = matches[1].replace(/['"]/g, '');
                    }
                }
                // Return blob and filename as an object
                return response.blob().then(blob => ({blob, filename}));
            })
            .then(({blob, filename}) => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            })
            .catch((err) => {
                if (typeof error === "undefined") {
                    console.error("Download failed:", err)
                } else {
                    error(err);
                }
            });
    }
}