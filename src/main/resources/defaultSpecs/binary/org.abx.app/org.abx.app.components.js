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


function defaultSpecs(elementType) {
    let newDefSpecs = {};
    switch (elementType) {
        case "select":
            newDefSpecs.title = "";
            newDefSpecs.content = "";
            newDefSpecs.values = [];
            break;
        case "img":
            newDefSpecs.src = "";
            break;
        case "button":
            newDefSpecs.title = "";
            newDefSpecs.content = "";
            break;
        case "textfield":
        case "textarea":
            newDefSpecs.title = "";
            newDefSpecs.content = "";
            break;
        case "header":
            newDefSpecs.src = "";
            newDefSpecs.links = [];
            break;
        case "modal":
            newDefSpecs.ok = "Ok";
            break;
        case "menu":
            newDefSpecs.items = [];
            break;
        case "okCancelModal":
            newDefSpecs.ok = "Ok";
            newDefSpecs.cancel = "Cancel";
            newDefSpecs.title = "title";
            newDefSpecs.content = "content";
            break;
    }
    return newDefSpecs;
}


function processComponentType(componentType) {
    workingEnv.shouldUpdate = false;
    $(workingEnv.ComponentType).val(componentType);
    $(workingEnv.ComponentType).formSelect()
    $(workingEnv.ComponentType).change();
    setComponentTypeVisibility();
    workingEnv.shouldUpdate = true;
}

function updateComponentType() {
    if (workingEnv.shouldUpdate === false) {
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
        component.specs = [defaultSpecs(component.type)];
    }
    setComponentTypeVisibility();
    processCurrentComponent(true);
}

function processElementType(elementType) {
    workingEnv.shouldUpdate = false;
    $(workingEnv.ElementType).val(elementType);
    $(workingEnv.ElementType).formSelect();
    workingEnv.shouldUpdate = true;
}

function updateElementType() {
    if (workingEnv.shouldUpdate === false) {
        return;
    }
    let value = $(workingEnv.ElementType).val();
    let component = workingEnv.component;
    component.type = value;
    component.specs = [defaultSpecs(component.type)];
    processSpecs();
    renderCurrentComponent();
}

function processChildren(chooseChildren) {
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
    });
    let index;
    if (typeof chooseChildren !== "undefined") {
        index = parseInt(chooseChildren);
    } else {
        index = 0;
    }
    if (workingEnv.component.components.length > 0) {
        processInnerComponent(index);
    }
    $(workingEnv.ComponentEnv).change(() => {
        processInnerComponent($(workingEnv.ComponentEnv).val());
    });
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
    if (!workingEnv.shouldUpdate) {
        return;
    }
    workingEnv.shouldUpdate = false;
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
        workingEnv.shouldUpdate = true;
        M.FormSelect.init(componentBox);
        renderCurrentComponent()
    });
}

function processInnerComponent(index) {
    workingEnv.shouldUpdate = false;
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
            workingEnv.shouldUpdate = true;
        });
    });
}

function processJS() {
    workingEnv.shouldUpdate = false;
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

            workingEnv.shouldUpdate = true;
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
        workingEnv.shouldUpdate = false;
        let index = $(workingEnv.ComponentEnv).val();
        var envValue = workingEnv.component.js[index];
        ace.edit(workingEnv.SpecsJS).setValue(envValue.value);
        $(workingEnv.Env).val(envValue.env);
        workingEnv.shouldUpdate = true;
    });
    workingEnv.shouldUpdate = true;
}

/**
 * Adds new environment as JS, Specs or Children
 */
function addNewEnv() {
    M.Modal.init(workingEnv.AddPopUpEnv).close();
    workingEnv.shouldUpdate = false;
    let component = workingEnv.component;
    let env = $(workingEnv.AddEnvValue).val();
    let index = $(workingEnv.ComponentEnv).val();
    if (index == null) {
        index = 0;
    } else {
        index = parseInt(index) + 1;
    }
    let detailsType = $(workingEnv.shouldUpdateDetailType).val();
    if (detailsType !== "js") {
        let isContainer = $(workingEnv.ComponentType).val() === "Container";
        if (isContainer) {
            component.components.splice(index, 0, {
                "innerId": `elem${index}`,
                "component": "org.abx.app.base.divider",
                "size": "l12",
                "env": env
            });
            //$(workingEnv.InnerComponentPackage).val("org.abx.app.base");
            processCurrentComponent(false, index);
        } else {
            component.specs.splice(index, 0,
                {
                    "env": env,
                    "value": defaultSpecs($(workingEnv.ElementType).val())
                });
            processCurrentComponent(false);
        }
    } else {
        component.js.splice(index, 0, {
            "env": env,
            "value": `//New JS code ${index}`
        });
        processCurrentComponent(true);
    }
    $(workingEnv.ComponentEnv).val(index);
    M.FormSelect.init(workingEnv.ComponentEnv);
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

/**
 * Sets one of the specs value
 * @param type
 * @param newValue
 */
function setSpecValue(type, newValue) {
    //Autofill protection
    if ($(workingEnv.ComponentType).val() === "Container") {
        return;
    }
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
    if (!workingEnv.shouldUpdate) {
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
    workingEnv.shouldUpdate = true;
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

function processContainerLayout(layout) {
    $(workingEnv.ContainerLayout).val(layout);
    $(workingEnv.ContainerLayout).formSelect();
}

function updateContainerLayout() {
    if (workingEnv.shouldUpdate === false) {
        return;
    }
    let layout = $(workingEnv.ContainerLayout).val();
    workingEnv.component.layout = layout;
    renderCurrentComponent();
    if (layout === "popup") {
        $(workingEnv.SpecsShowModal).parent().show();
    } else {
        $(workingEnv.SpecsShowModal).parent().hide();
    }
}


/**
 * After is for the select package which is a request
 * @param showJS
 * @param after
 */
function processCurrentComponent(showJS, chooseChildren) {
    let componentSpecs = workingEnv.component;
    $(workingEnv.shouldUpdateDetailType).empty();
    $(workingEnv.shouldUpdateDetailType).append($('<option>', {
        value: "js",
        text: "JS",
        selected: showJS
    }));
    $(workingEnv.show).empty();
    if (componentSpecs.isContainer) {
        processComponentType("Container");
        processContainerLayout(componentSpecs.layout)
        $(workingEnv.shouldUpdateDetailType).append($('<option>', {
            value: "children",
            text: "Children",
            selected: !showJS
        }));
        if (showJS) {
            processJS();
        } else {
            processChildren(chooseChildren);
        }
    } else {
        $(workingEnv.shouldUpdateDetailType).append($('<option>', {
            value: "specs",
            text: "Specs",
            selected: !showJS
        }));
        processComponentType("Element");
        processElementType(componentSpecs.type);
        if (showJS) {
            processJS();
        } else {
            processSpecs();
        }
    }
    $(workingEnv.shouldUpdateDetailType).change(() => {
        if ($(workingEnv.shouldUpdateDetailType).val() === "specs") {
            processSpecs();
        } else if ($(workingEnv.shouldUpdateDetailType).val() === "js") {
            processJS();
        } else if ($(workingEnv.shouldUpdateDetailType).val() === "children") {
            processChildren()
        }
    });

    M.FormSelect.init(workingEnv.shouldUpdateDetailType);
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

/**
 *
 * @param newName
 */
function cloneComponent(newName) {
    $.post("page/clone", {
            "componentSpecs": JSON.stringify(workingEnv.component),
            "newName": newName
        }, () => {

        }
    )
}

$(document).ready(hideSpecs);