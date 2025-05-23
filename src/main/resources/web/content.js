class PageContent {

    static processTile(specs) {
        document.title = specs.title;
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
            for (var line of js) {
                eval(line)
            }

        })
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
        if (componentSpecs.js !== "") {
            js.push(componentSpecs.js);
        }
        if (componentSpecs.isContainer) {
            if (componentSpecs.layout === "nav") {
                PageContent.renderNav(output, js, componentSpecs);
            } else if (componentSpecs.layout === "horizontal") {
                PageContent.renderHorizontal(output, js, componentSpecs);
            } else if (componentSpecs.layout === "container") {
                PageContent.renderContainer(output, js, componentSpecs);
            } else {
                PageContent.renderVertical(output, js, componentSpecs);
            }
        } else {

            var size = componentSpecs.size;
            componentSpecs.specs.id = componentSpecs.id;
            switch (componentSpecs.type) {
                case "button":
                    PageContent.renderButton(output, componentSpecs.specs);
                    break;
                case "img":
                    PageContent.renderImg(output, componentSpecs.specs);
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
                case "select":
                    PageContent.renderSelect(output, js, componentSpecs.specs);
                    break;
                case "file":
                    PageContent.renderFile(output, componentSpecs.specs);
                    break;
                case "modal":
                    PageContent.renderModal(output, js, componentSpecs.specs);
                    break;
                case "js":
                    PageContent.renderJS(output, componentSpecs.specs);
                    break;
                case "header":
                    PageContent.renderHeader(output, componentSpecs.specs);
                    break;
                case "menu":
                    PageContent.renderMenu(output, componentSpecs.specs);
                    break;
            }
        }

    }

    static renderNav(output, js, componentSpecs) {
        var children = componentSpecs.children;
        output.push(`
    <link href="/web/icons.css" rel="stylesheet">
<header><div class="container">
       <div class="container">
        <a href="#" data-target="nav-mobile"
           class="top-nav sidenav-trigger waves-effect waves-light circle hide-on-large-only">
            <i  class="material-icons">menu</i>
        </a>
    </div>
    <ul id="nav-mobile" class="sidenav sidenav-fixed">`)
        PageContent.renderComponent(output, js, children[0])
        output.push(`</ul></header><main>
    <div class="section" id="index-banner">
        <div class="container">  <div class="row" style="margin-bottom: 0;">`);
        PageContent.renderComponent(output, js, children[1])
        output.push(`   </div>     </div></div></main>`)
    }

    static renderHorizontal(output, js, componentSpecs) {
        output.push(`<div  ${componentSpecs.size}" style="height: 100%; margin-bottom: 0;" id="${componentSpecs.id}" class="row">`);
        for (var component of componentSpecs.children) {
            output.push(`<div class="col ${component.size}" >`);
            PageContent.renderComponent(output, js, component);
            output.push('</div>');
        }
        output.push('</div>');
    }

    static renderVertical(output, js, componentSpecs) {
        output.push(`<div ${componentSpecs.size}" id="${componentSpecs.id}">`);
        for (var component of componentSpecs.children) {
            output.push(`<div class="row ${component.size}">`);
            PageContent.renderComponent(output, js, component)
            output.push('</div>');
        }
        output.push('</div>');
    }


    static renderContainer(output, js, componentSpecs) {
        output.push(`<div class=" container ${componentSpecs.size}" id="${componentSpecs.id}">`);
        for (var component of componentSpecs.children) {
            output.push(`<div class="row" ${component.size}>`);
            PageContent.renderComponent(output, js, component)
            output.push('</div>');
        }
        output.push('</div>');
    }

    static renderButton(output, specs) {
        var results =
            `<button id="${specs.id}" class="btn waves-effect waves-light  ${specs.size}"> ${specs.text} 
             </button>`
        output.push(results);
    }

    static renderImg(output, specs) {
        var results =
            `<img id="${specs.id}"  src="${specs.src}" class="responsive-img" style="max-height: 100%" alt="example">`
        output.push(results);
    }

    static renderTextfield(output, js, specs) {
        var results =
            `<div  class="input-field">
            <input placeholder="${specs.placeholder}" id="${specs.id}"  type="text" class="validate">
          <label for="${specs.id}">${specs.label}</label></div>`;
        output.push(results);
        if (typeof specs.url !== "undefined") {
            js.push(`$.get('${specs.url}',(res)=>{$(${specs.id}).val(res)})`);
        }
    }

    static renderPassword(output, specs) {
        var results =
            `<div  class="input-field">
            <input placeholder="${specs.placeholder}" id="${specs.id}"  type="password" class="validate">
          <label for="f_${specs.id}">${specs.label}</label></div>`;
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
    </div>
</div>`;
        js.push(`M.Modal.init(${specs.id});`)
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
            <div class="row ">
            <${specs.size} class="header">${specs.title}</${specs.size}>
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

    static renderSelect(output, js, specs) {
        if (typeof specs.list !== "undefined") {
            var results =
                `<div   class="input-field">
            <select id="${specs.id}">
             <option value="" disabled selected></option>
            </select>
             <label>${specs.label}</label>
            </div>`;
            js.push(`$.get("${specs.list}",(resultList)=>{resultList.forEach(function(item) {
                $(${specs.id}).append($('<option>', {
                    value: item,
                    text: item
               }));
              })
              M.FormSelect.init(${specs.id});
            });`);
            output.push(results);
            js.push( ` $(${specs.id}).on('change', function () {
        console.log('New value:', $(this).val());
      });`);
        } else {
            const optionsHtml = specs.values.map(item =>
                `<option value="${item.key}">${item.value}</option>`
            ).join('');
            var results =
                `<div   class="input-field">
            <select id="${specs.id}">
             <option value="" disabled selected></option>
            ${optionsHtml}
            </select>
             <label>${specs.label}</label>
            </div>`;
            js.push( `$(${specs.id}).formSelect();`);
            js.push( ` $(${specs.id}).on('change', function () {
        console.log('New value:', $(this).val());
      });`);
            output.push(results);
        }
    }

    static renderJS(output, specs) {
        var result = `
    <script src="/web/ace/src-min-noconflict/ace.js"></script>
    <div id="${specs.id}" style="height: ${specs.height}; width: 100%">// Write your JavaScript here</div>
        
<script>
    const editor = ace.edit("${specs.id}");
    editor.setTheme("ace/theme/monokai"); // Change theme if desired
    editor.session.setMode("ace/mode/javascript"); // JS highlighting
    editor.setOptions({
        fontSize: "14px",
        showPrintMargin: false,
        wrap: true,
        useWorker: true // Enables JS syntax checking
    });
</script>`;
        output.push(result);
    }

    static renderHeader(output, specs) {
        var links = "";
        for (var link of specs.links) {
            links += `<li><a href="${link.href}">${link.text}</li></a>`
        }
        var result = `  <nav>
    <div class="nav-wrapped">
      <a href="#" class="left">
      <img src="${specs.img}" alt="Logo" style="height: 64px; padding: 5px;">
    </a>
      <ul id="nav-mobileasd" class="right">
      ${links}
      </ul>
    </div>
  </nav><div style="height: 10px"></div>`
        output.push(result)
    }


    static renderMenu(output, specs) {

        var result = `  
        
        
  <style>
    /* Custom grey menu bar styles */
    #custom-menu-bar {
      height: 48px;
      line-height: 48px;
    }

    #custom-menu-bar .nav-wrapper {
      height: 48px;
    }

    #custom-menu-bar ul li a {
      line-height: 48px;
      height: 48px;
      padding: 0 12px;
      font-size: 14px;
      color: #212121;
    }

    #custom-menu-bar .material-icons {
      font-size: 18px;
      vertical-align: middle;
    }

    /* Active (selected) menu button */
    #custom-menu-bar ul li.active a {
      background-color: #9e9e9e; /* dark grey */
      color: white;
    }

    /* Widen and compact dropdowns */
    .dropdown-content {
      min-width: 200px !important;
    }

    .dropdown-content li > a {
      padding: 8px 16px;
      line-height: 1.2rem;
      font-size: 14px;
    }
  </style>
</head>
<body>

  <!-- Custom Grey Menu Bar -->
  <nav id="custom-menu-bar">
    <div class="nav-wrapper grey lighten-2">
      <ul id="nav-mobile" class="left">
        <li class="active">
          <a class="dropdown-trigger" href="#!" data-target="file-dropdown">
            File <i class="material-icons tiny right">arrow_drop_down</i>
          </a>
        </li>
        <li>
          <a class="dropdown-trigger" href="#!" data-target="edit-dropdown">
            Edit <i class="material-icons tiny right">arrow_drop_down</i>
          </a>
        </li>
      </ul>
    </div>
  </nav>

  <!-- File Dropdown Menu -->
  <ul id="file-dropdown" class="dropdown-content">
    <li><a href="#!" onclick="newComponent()">New Component</a></li>
    <li><a href="#!" onclick="deleteComponent()">Delete Component</a></li>
  </ul>

  <!-- Edit Dropdown Menu -->
  <ul id="edit-dropdown" class="dropdown-content">
    <li><a href="#!" onclick="renameComponent()">Rename Component</a></li>
  </ul>

  <!-- Scripts -->
  <script>


    function newComponent() {
      alert("New Component clicked");
    }

    function deleteComponent() {
      alert("Delete Component clicked");
    }

    function renameComponent() {
      alert("Rename Component clicked");
    }
     const elems = document.querySelectorAll('.dropdown-trigger');
      M.Dropdown.init(elems, { coverTrigger: false });

    // Optional: make active tab toggle dynamically
    document.querySelectorAll('#custom-menu-bar ul li').forEach(li => {
      li.addEventListener('click', () => {
        document.querySelectorAll('#custom-menu-bar ul li').forEach(el => el.classList.remove('active'));
        li.classList.add('active');
      });
    });
  </script>
`
        output.push(result)
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
            url: `/process/${method}`, // Replace or define $url
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

    static processBinaryDownload(method, args, data) {
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