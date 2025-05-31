var processContainerLayout;
var workingEnv = {"editing": false};

var maxLine = 73;

function hideElemContainer() {
    $(workingEnv.ElementType).closest('.input-field').parent().hide();
    $(workingEnv.ContainerLayout).closest('.input-field').parent().hide();
    hideSpecs();
}

function processAction(x) {
    selectPackage($(workingEnv.ComponentName), x);
}

function showPreviewModal() {
    M.Modal.init($('#__preview')[0]).open();
}

function hideSpecs() {
    $(workingEnv.SpecsSize).closest('.input-field').parent().hide();
    $(workingEnv.SpecsOk).closest('.input-field').parent().hide();
    $(workingEnv.SpecsCancel).closest('.input-field').parent().hide();
    $(workingEnv.SpecsOkCancelContainer).closest('.input-field').parent().hide();
    $(workingEnv.SpecsShowModal).parent().hide();
    $(workingEnv.SpecsSource).closest('.input-field').parent().hide();
    $(workingEnv.SpecsTitle).closest('.input-field').parent().hide();
    $(workingEnv.SpecsContent).closest('.input-field').parent().hide();
    $(workingEnv.ChildrenComponentClass).parent().hide();
    $(workingEnv.ChildrenEditingDetailType).parent().hide();
    $(workingEnv.SpecsSelect).closest('.input-field').parent().parent().parent().hide();
    $(workingEnv.SpecsSelectRemove).parent().hide();
    $(workingEnv.SpecsSelectEdit).parent().hide();
    $(workingEnv.SpecsSelectAdd).parent().hide();
    $(workingEnv.SpecsJS).parent().parent().parent().hide();
    $(workingEnv.ComponentEnv).off("change")
    ace.edit(workingEnv.SpecsJS).setValue("");
    $(workingEnv.SpecsSource).closest('.input-field').children("label").text("Source");
}

function selectPackage(componentBox, packageName) {
    $(componentBox).empty();
    $(componentBox).append($('<option>', {
        value: "",
        text: ""
    }));
    $(workingEnv.ComponentEnv).empty();
    M.FormSelect.init(workingEnv.ComponentEnv);
    $.get(`/page/packages/${packageName}/components`, (resultList) => {
        resultList.forEach(function (item) {
            $(componentBox).append($('<option>', {
                value: item,
                text: item
            }));
        });
        $(workingEnv.show).empty();
        $(workingEnv.div).empty();
        hideElemContainer();
        M.FormSelect.init(componentBox);
    });
}


function defaultSpecs(value) {
    let newValue = {};
    let specs = [{"env": "", "value": newValue}];
    switch (value) {
        case "select":
            newValue.title = "";
            newValue.content = "";
            newValue.values = [];
            break;
        case "img":
            newValue.src = "";
        case "button":
            newValue.title = "";
            newValue.content = "";
            break;
        case "textfield":
        case "textarea":
            newValue.title = "";
            newValue.content = "";
            break;
        case "header":
            newValue.src = "";
            newValue.links = [];
            break;
        case "modal":
            newValue.ok = "Ok";
            break;
        case "menu":
            newValue.items = [];
            break;
        case "okCancelModal":
            newValue.ok = "Ok";
            newValue.cancel = "Cancel";
            newValue.title = "title";
            newValue.content = "content";
            break;
    }
    return specs;
}


function processComponentType(componentType) {
    workingEnv.editing = false;
    $(workingEnv.ComponentType).val(componentType);
    $(workingEnv.ComponentType).formSelect()
    $(workingEnv.ComponentType).change();
    setComponentTypeVisibility();
    workingEnv.editing = true;
}

function updateComponentType() {
    if (workingEnv.editing === false) {
        return;
    }
    let isContainer = $(workingEnv.ComponentType).val() === "Container";
    let component = workingEnv.component;
    component.specs = null;
    component.isContainer = isContainer;
    component.components = null;
    if (isContainer) {
        component.layout = "vertical";
        component.components = [];
    } else {
        component.type = "button";
        component.specs = defaultSpecs(component.type);
    }
    setComponentTypeVisibility();
    processCurrentComponent(true);
}

function processElementType(elementType) {
    workingEnv.editing = false;
    $(workingEnv.ElementType).val(elementType);
    $(workingEnv.ElementType).formSelect();
    workingEnv.editing = true;
}

function updateElementType() {
    if (workingEnv.editing === false) {
        return;
    }
    let value = $(workingEnv.ElementType).val();
    let component = workingEnv.component;
    component.type = value;
    component.specs = defaultSpecs(component.type)
    processSpecs();
    renderCurrentComponent();
}

function processChildren() {
    hideSpecs();
    let component = workingEnv.component;
    if (component.layout === "popup") {
        $(workingEnv.SpecsShowModal).parent().show();
    }
    $(workingEnv.ChildrenComponentClass).parent().show();
    $(workingEnv.ChildrenEditingDetailType).parent().show();
    $(workingEnv.ComponentEnv).empty();
    $(workingEnv.ComponentEnv).closest('.input-field').children('label').text('Children');
    component.components.forEach(function (item, index) {
        var line = JSON.stringify(item);
        if (line.length > maxLine) {
            line = line.substring(0, maxLine - 3) + "...";
        }
        $(workingEnv.ComponentEnv).append($('<option>', {
            value: index,
            text: line
        }));
        $(workingEnv.ComponentEnv).change(() => {
            processInnerComponent($(workingEnv.ComponentEnv).val());
        });
    });
    if (workingEnv.component.components.length > 0) {
        processInnerComponent(0);
    }
    M.FormSelect.init(workingEnv.ComponentEnv);
}

function updateInnerElement() {
    let index = $(workingEnv.ComponentEnv).val();
    let component = workingEnv.component;
    let inner = component.components[index];
    inner.component = $(workingEnv.InnerComponentName).val();
    renderCurrentComponent()
}

function updateInnerPackage() {
    if (!workingEnv.editing) {
        return;
    }
    workingEnv.editing = false;
    let componentBox = workingEnv.InnerComponentName;
    $(componentBox).empty();
    let package = $(workingEnv.InnerComponentPackage).val();
    let index = $(workingEnv.ComponentEnv).val();
    let component = workingEnv.component;
    $.get(`/page/packages/${package}/components`, (resultList) => {
        let first = true;
        resultList.forEach(function (item) {
            $(componentBox).append($('<option>', {
                value: item,
                text: item,
                selected: first
            }));
            if (first) {
                first = false;
                component.components[index].component = item;
            }
        });
        workingEnv.editing = true;
        M.FormSelect.init(componentBox);
        renderCurrentComponent()
    });
}

function processInnerComponent(index) {
    workingEnv.editing = false;
    let component = workingEnv.component;
    $(workingEnv.ChildrenInnerId).val(component.components[index].innerId);
    $(workingEnv.ChildrenSize).val(component.components[index].size);
    $(workingEnv.InnerComponentEnv).val(component.components[index].env);
    var innerComponentName = component.components[index].component;
    $.get(`/page/components/${innerComponentName}`, (componentSpecs) => {
        $(workingEnv.InnerComponentPackage).val(componentSpecs.package);
        M.FormSelect.init(workingEnv.InnerComponentPackage);
        let componentBox = workingEnv.InnerComponentName;
        $(componentBox).empty();
        $.get(`/page/packages/${componentSpecs.package}/components`, (resultList) => {
            resultList.forEach(function (item) {
                $(componentBox).append($('<option>', {
                    value: item,
                    text: item
                }));
            });
            $(componentBox).val(innerComponentName);
            M.FormSelect.init(componentBox);
            workingEnv.editing = true;
        });
    });
}

function processJS() {
    workingEnv.editing = false;
    $(workingEnv.ComponentEnv).closest('.input-field').children('label').text('JS');
    hideSpecs();
    let component = workingEnv.component;
    if (component.layout === "popup") {
        $(workingEnv.SpecsShowModal).parent().show();
    }
    $(workingEnv.SpecsJS).parent().parent().parent().show();
    $(workingEnv.ComponentEnv).empty();
    let first = false;
    component.js.forEach(function (item, va) {
        let env = item.env;
        if (!first) {
            first = true;
            $(workingEnv.Env).val(item.env);
            ace.edit(workingEnv.SpecsJS).setValue(item.value);

            workingEnv.editing = true;
        }
        if (env === "") {
            env = "Default";
        }
        var lineValue = item.value;
        if (lineValue.length > maxLine) {
            lineValue = lineValue.substring(0, maxLine - 3) + "...";
        }
        $(workingEnv.ComponentEnv).append($('<option>', {
            value: va,
            text: env + " -> " + lineValue
        }));
    });
    M.FormSelect.init(workingEnv.ComponentEnv);
    $(workingEnv.ComponentEnv).change(() => {
        workingEnv.editing = false;
        let index = $(workingEnv.ComponentEnv).val();
        var envValue = workingEnv.component.js[index];
        ace.edit(workingEnv.SpecsJS).setValue(envValue.value);
        $(workingEnv.Env).val(envValue.env);
        workingEnv.editing = true;
    });
    workingEnv.editing = true;
}

/**
 * Adds new environment as JS, Specs or Children
 */
function addNewEnv() {
    workingEnv.editing=false;
    var component = workingEnv.component;
    let index = $(workingEnv.ComponentEnv).val();
    if (typeof index !== "number"){
        index = 0;
    }
    let detailsType = $(workingEnv.EditingDetailType).val();
    if (detailsType === "js") {
        component.js.splice(index,0, {
            "env": "",
            "value": `//New JS code ${index}`
        });
        processCurrentComponent(true);
    } else {
        let isContainer = $(workingEnv.ComponentType).val() === "Container";
        if (isContainer) {
            component.components.splice(index,0, {
                "innerId": `elem${index}`,
                "component": "com.abx.app.base.div",
                "size": "l12",
                "env": ""
            });
        } else {
            component.specs.splice(index,0, defaultSpecs($(workingEnv.ComponentType)));
        }
        processCurrentComponent(false);
    }
    workingEnv.editing=true;
    renderCurrentComponent();
}

function processSelect() {
    $(workingEnv.SpecsSelect).empty();
    var component = workingEnv.component;
    let index = $(workingEnv.ComponentEnv).val();
    var specs = component.specs[index].value;
    specs.values.forEach(function (item, va) {
        var textLine = "value: " + item.value + ", text: " + item.text;
        if (textLine.length > maxLine) {
            textLine = textLine.substring(0, maxLine - 3) + "...";
        }
        $(workingEnv.SpecsSelect).append($('<option>', {
            value: item,
            text: textLine
        }));
    });
    M.FormSelect.init(workingEnv.SpecsSelect);
}


function discardSpecs() {
    processComponent(workingEnv.originalComponent);
}

function setSpecValue(type, newValue) {
    var index = $(workingEnv.ComponentEnv).val();
    var component = workingEnv.component;
    var specs = component.specs[index];
    specs.value[type] = newValue;
    renderCurrentComponent()
    let text = JSON.stringify(specs.value);
    if (specs.env === "") {
        text = "Default -> " + text;
    } else {
        text = specs.env + " -> " + text;
    }
    if (text.length > maxLine) {
        text = text.substring(0, maxLine) + "...";
    }
    $(workingEnv.ComponentEnv).find('option:selected').text(text);
    $(workingEnv.ComponentEnv).formSelect();
}

function setChildValue(type, newValue) {
    let component = workingEnv.component;
    let index = $(workingEnv.ComponentEnv).val();
    let child = component.components[index];
    child[type] = newValue;
    renderCurrentComponent();
    let text = JSON.stringify(child);
    if (text.length > maxLine) {
        text = text.substring(0, maxLine) + "...";
    }
    $(workingEnv.ComponentEnv).find('option:selected').text(text);
    $(workingEnv.ComponentEnv).formSelect();
}

function updateText(delta, text) {
    if (!workingEnv.editing) {
        return;
    }
    let component = workingEnv.component;
    let index = $(workingEnv.ComponentEnv).val();
    let jsEnv = component.js[index]
    jsEnv.value = text;
    if (jsEnv.env === "") {
        text = "Default -> " + text;
    } else {
        text = jsEnv.env + " -> " + text;
    }
    renderCurrentComponent()
    if (text.length > maxLine) {
        text = text.substring(0, maxLine) + "...";
    }
    $(workingEnv.ComponentEnv).find('option:selected').text(text);
    $(workingEnv.ComponentEnv).formSelect();
}

function processSpecs() {
    $(workingEnv.ComponentEnv).closest('.input-field').children('label').text('Specs');
    $(workingEnv.ComponentEnv).empty();
    let first = false;
    var component = workingEnv.component;
    component.specs.forEach(function (item, va) {
        let env = item.env;
        if (!first) {
            first = true;
            $(workingEnv.Env).val(item.env);
        }
        if (env === "") {
            env = "Default";
        }
        var lineValue = JSON.stringify(item.value);
        if (lineValue.length > maxLine) {
            lineValue = lineValue.substring(0, maxLine - 3) + "...";
        }
        $(workingEnv.ComponentEnv).append($('<option>', {
            value: va,
            text: env + " -> " + lineValue
        }));
    });
    M.FormSelect.init(workingEnv.ComponentEnv);
    hideSpecs();
    switch (component.type) {
        case "img":
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            break
        case "header":
            $(workingEnv.SpecsSelect).closest('.input-field').parent().parent().parent().show();
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            $(workingEnv.SpecsSelectRemove).parent().show();
            $(workingEnv.SpecsSelectAdd).parent().show();
            $(workingEnv.SpecsSelectEdit).parent().show();
            processSelect();
            break
        case "button":
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            break
        case "select":
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsSelect).closest('.input-field').parent().parent().parent().show();
            $(workingEnv.SpecsSelectRemove).parent().show();
            $(workingEnv.SpecsSelectAdd).parent().show();
            $(workingEnv.SpecsSelectEdit).parent().show();
            processSelect();
            break
        case "modal":
            $(workingEnv.SpecsOkCancelContainer).closest('.input-field').parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
            $(workingEnv.SpecsOk).closest('.input-field').parent().show();
            $(workingEnv.SpecsShowModal).parent().show();
            break
        case "okCancelModal":
            $(workingEnv.SpecsOkCancelContainer).closest('.input-field').parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
            $(workingEnv.SpecsOk).closest('.input-field').parent().show();
            $(workingEnv.SpecsCancel).closest('.input-field').parent().show();
            $(workingEnv.SpecsShowModal).parent().show();
            break
        case "section":
            $(workingEnv.SpecsSize).closest('.input-field').parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
            break
        case "password":
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
            break
        case "textfield":
        case "textarea":
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
            break
    }
    processElement();
    $(workingEnv.ComponentEnv).change(() => processElement());
}

function processElement() {
    var component = workingEnv.component;
    var index = $(workingEnv.ComponentEnv).val();
    $(workingEnv.Env).val(component.specs[index].env);
    var specs = component.specs[index].value;
    switch (component.type) {
        case "header":
            $(workingEnv.SpecsSource).val(specs.src);
            break;
        case "img":
            $(workingEnv.SpecsSource).val(specs.src);
            break;
        case "button":
            $(workingEnv.SpecsTitle).val(specs.title);
            break;
        case "select":
            $(workingEnv.SpecsTitle).val(specs.title);
            break;
        case "modal":
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            $(workingEnv.SpecsOk).val(specs.ok);
            break;
        case "okCancelModal":
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            $(workingEnv.SpecsOk).val(specs.ok);
            $(workingEnv.SpecsCancel).val(specs.cancel);
            break;
        case "password":
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            break;
        case "textarea":
        case "textfield":
            $(workingEnv.SpecsSource).val(specs.src);
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            break;
        case "section":
            $(workingEnv.SpecsSize).val(specs.size);
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            break;
    }
    workingEnv.editing = true;
}

function renderCurrentComponent() {
    $.post(`/page/preview`, {
            componentSpecs: JSON.stringify(workingEnv.component),
            env: ""
        },
        (componentSpecs) => {
            var output = [];
            var js = [];
            //Clear JS
            componentSpecs.js = "";
            PageContent.renderComponent(output, js, componentSpecs)
            $(workingEnv.show).html(output.join(""));
            M.updateTextFields();
            for (var line of js) {
                eval(line)
            }
        });
}

function processCurrentComponent(showJS) {
    let componentSpecs = workingEnv.component;
    $(workingEnv.EditingDetailType).empty();
    $(workingEnv.EditingDetailType).append($('<option>', {
        value: "js",
        text: "JS"
    }));
    $(workingEnv.show).empty();
    if (componentSpecs.isContainer) {
        processComponentType("Container");
        processContainerLayout(componentSpecs.layout)
        $(workingEnv.EditingDetailType).append($('<option>', {
            value: "children",
            text: "Children",
        }));
        if (showJS) {
            processJS();
        }else{
            processChildren();
        }
    } else {
        $(workingEnv.EditingDetailType).append($('<option>', {
            value: "specs",
            text: "Specs",
            selected: true
        }));
        processComponentType("Element");
        processElementType(componentSpecs.type);
        if (showJS) {
            processJS();
        }else{
            processSpecs();
        }
    }
    $(workingEnv.EditingDetailType).change(() => {
        if ($(workingEnv.EditingDetailType).val() === "specs") {
            processSpecs();
        } else if ($(workingEnv.EditingDetailType).val() === "js") {
            processJS();
        } else if ($(workingEnv.EditingDetailType).val() === "children") {
            processChildren()
        }
    });

    M.FormSelect.init(workingEnv.EditingDetailType);
    renderCurrentComponent()
}

function processComponent(componentName) {
    $.get(`/page/components/${componentName}`, (componentSpecs) => {
        workingEnv.originalComponent = componentSpecs.name;
        workingEnv.component = componentSpecs;
        processCurrentComponent(false);
    });
}

function saveCurrentSpecs() {
    $.post("/page/components", {component: JSON.stringify(workingEnv.component)}, function (response) {
        let message = "";
        if (response.success) {
            message = "Component saved successfully";
        } else {
            message = "Error saving component " + message.error;
        }
        $('body').append(`
    <div id="okModal" class="modal">
      <div class="modal-content"><p></p>${message}</div>
      <div class="modal-footer">
        <button class="modal-close btn blue" id="okBtn">OK</button>
      </div>
    </div>
  `);
        M.Modal.init($('#okModal')[0]).open();
    });
}

function setComponentTypeVisibility() {
    if ($(workingEnv.ComponentType).val() === "Container") {
        $(workingEnv.ElementType).closest('.input-field').parent().hide()
        $(workingEnv.ContainerLayout).closest('.input-field').parent().show();
    } else {
        $(workingEnv.ElementType).closest('.input-field').parent().show()
        $(workingEnv.ContainerLayout).closest('.input-field').parent().hide();
    }
}

$(document).ready(hideSpecs);