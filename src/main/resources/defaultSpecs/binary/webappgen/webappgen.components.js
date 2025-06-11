var workingEnv = {"shouldUpdate": false};
var maxLine = 73;

function hideElemContainer() {
    $(workingEnv.ElementType).closest('.input-field').parent().hide();
    $(workingEnv.ContainerLayout).closest('.input-field').parent().hide();
    hideSpecs();
}

function showPreviewModal() {
    M.Modal.init($('#__preview')[0]).open();
}

function hideSpecs() {
    $(workingEnv.SpecsSize).closest('.input-field').parent().hide();
    $(workingEnv.SpecsOk).closest('.input-field').parent().hide();
    $(workingEnv.SpecsCancel).closest('.input-field').parent().hide();
    $(workingEnv.SpecsOkCancelContainer).parent().hide();
    $(workingEnv.SpecsShowModal).parent().hide();
    $(workingEnv.SpecsEnv).closest('.input-field').parent().hide();
    $(workingEnv.SpecsSource).closest('.input-field').parent().hide();
    $(workingEnv.SpecsTitle).closest('.input-field').parent().hide();
    $(workingEnv.SpecsContent).closest('.input-field').parent().hide();
    $(workingEnv.SpecsHeight).closest('.input-field').parent().hide();
    $(workingEnv.SpecsContentArea).closest('.input-field').parent().hide();
    $(workingEnv.ChildrenComponentClass).parent().hide();
    $(workingEnv.ChildrenEditingDetailType).parent().hide();
    $(workingEnv.SelectContainer).parent().hide();
    $(workingEnv.SpecsJS).parent().parent().parent().hide();
    $(workingEnv.ComponentEnv).off("change")
    ace.edit(workingEnv.SpecsJS).setValue("");
    $(workingEnv.SpecsSource).closest('.input-field').children("label").text("Source");
}

/**
 * Select packages with aftercall
 * @param packageName
 * @param afterCall
 */
function selectPackage(packageName, afterCall) {
    workingEnv.shouldUpdate = false;
    let componentBox = $(workingEnv.ComponentName);
    $(componentBox).empty();
    $(componentBox).append($('<option>', {
        value: "",
        text: ""
    }));
    $(workingEnv.ComponentEnv).empty();
    M.FormSelect.init(workingEnv.ComponentEnv);
    if (packageName === ""){
        return;
    }
    $.get(`/components/packages/${packageName}/components`, (resultList) => {
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
        workingEnv.shouldUpdate = true;
        if (typeof afterCall !== "undefined") {
            afterCall();
        }
    });
}


function defaultSpecs(elementType) {
    let newDefSpecs = {};
    switch (elementType) {
        case "canvas":
            newDefSpecs.height = "100px";
            break;
        case "jsEditor":
            newDefSpecs.height = "100px";
            newDefSpecs.content = "//Your JS Here";
            break;
        case "select":
            newDefSpecs.src = "";
            newDefSpecs.title = "The select";
            newDefSpecs.values = [];
            break;
        case "img":
            newDefSpecs.src = "";
            break;
        case "button":
            newDefSpecs.title = "Click me";
            break;
        case "textfield":
        case "textarea":
            newDefSpecs.title = "Text field";
            newDefSpecs.content = "Please type";
            break;
        case "header":
            newDefSpecs.src = "";
            newDefSpecs.values = [];
            break;
        case "section":
            newDefSpecs.title = "The section";
            newDefSpecs.content = "Nice section";
            newDefSpecs.size = "h2";
            break;
        case "modal":
            newDefSpecs.ok = "Ok";
            break;
        case "menu":
            newDefSpecs.items = [];
            break;
        case "switch":
            newDefSpecs.ok = "On";
            newDefSpecs.cancel = "Off";
            newDefSpecs.title = "title";
            newDefSpecs.src = "";
            break;
        case "file":
            newDefSpecs.title = "title";
            newDefSpecs.content = "Upload";
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
        component.specs = [{"env": "", "value": defaultSpecs(component.type)}];
    }
    processCurrentComponent(false);
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
    component.specs = [{"env": "", "value": defaultSpecs(component.type)}];
    processCurrentComponent(false);
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
    if (package ===""){
        workingEnv.shouldUpdate = true;
        return;
    }
    let index = $(workingEnv.ComponentEnv).val();
    let component = workingEnv.component;
    $.get(`/components/packages/${package}/components`, (resultList) => {
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
    $.get(`/components/details/${innerComponentName}`, (componentSpecs) => {
        $(workingEnv.InnerComponentPackage).val(componentSpecs.package);
        let componentBox = workingEnv.InnerComponentName;
        $(componentBox).empty();
        $.get(`/components/packages/${componentSpecs.package}/components`, (resultList) => {
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

function processJS(envIndex) {
    if (typeof envIndex === "undefined"){
        envIndex=0;
    }
    workingEnv.shouldUpdate = false;
    $(workingEnv.ComponentEnv).closest('.input-field').children('label').text('JS');
    hideSpecs();
    $(workingEnv.SpecsEnv).closest('.input-field').parent().show();
    let component = workingEnv.component;
    if (component.layout === "popup") {
        $(workingEnv.SpecsShowModal).parent().show();
    }
    $(workingEnv.SpecsJS).parent().parent().parent().show();
    $(workingEnv.ComponentEnv).empty();
    component.js.forEach(function (item, currIndex) {
        let env = item.env;
        if (envIndex==currIndex) {
            $(workingEnv.SpecsEnv).val(item.env);
            ace.edit(workingEnv.SpecsJS).setValue(item.value);
            workingEnv.shouldUpdate = true;
        }
        if (env === "") {
            env = "Default";
        }
        var lineValue = env + " -> " + item.value;
        if (lineValue.length > maxLine) {
            lineValue = lineValue.substring(0, maxLine - 3) + "...";
        }
        $(workingEnv.ComponentEnv).append($('<option>', {
            value: currIndex,
            text: lineValue,
            selected: currIndex == envIndex
        }));
    });
    M.FormSelect.init(workingEnv.ComponentEnv);
    $(workingEnv.ComponentEnv).change(() => {
        workingEnv.shouldUpdate = false;
        let index = $(workingEnv.ComponentEnv).val();
        var envValue = workingEnv.component.js[index];
        ace.edit(workingEnv.SpecsJS).setValue(envValue.value);
        $(workingEnv.SpecsEnv).val(envValue.env);
        workingEnv.shouldUpdate = true;
    });
    workingEnv.shouldUpdate = true;
}

function updateSelectedOptionText(selectElement, newText) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    if (selectedOption) {
        selectedOption.text = newText;

        // Reinitialize Materialize select to reflect changes
        const instance = M.FormSelect.getInstance(selectElement);
        if (instance) instance.destroy();
        M.FormSelect.init(selectElement);
    }
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
                "component": "webappgen.base.divider",
                "size": "l12",
                "env": env
            });
            //$(workingEnv.InnerComponentPackage).val("webappgen.base");
            processCurrentComponent(false, index);
        } else {
            component.specs.splice(index, 0,
                {
                    "env": env,
                    "value": defaultSpecs($(workingEnv.ElementType).val())
                });
            processCurrentComponent(false);
            if (index > 0) {
                $(workingEnv.ComponentEnv).val(index).change();
            }
        }

    } else {
        let newCode =  `//New JS code ${index}`;
        component.js.splice(index, 0, {
            "env": env,
            "value": newCode
        });
        processCurrentComponent(true,index);
        //ace.edit(workingEnv.SpecsJS).setValue(newCode);
    }
    $(workingEnv.SpecsEnv).val(env);
    $(workingEnv.ComponentEnv).val(index);
    M.FormSelect.init(workingEnv.ComponentEnv);
}

function swapComponentEnvPositions(posA, posB) {
    const $select = $(workingEnv.ComponentEnv);
    const $options = $select.find('option');
    const $optA = $options.eq(posA);
    const $optB = $options.eq(posB);  // Swap the text nodes
    const textA = $optA.text();
    const textB = $optB.text();
    $optA.text(textB);
    $optB.text(textA);
    $(workingEnv.ComponentEnv).val(posB);
    // Reinitialize Materialize so the UI reflects the change
    M.FormSelect.init(workingEnv.ComponentEnv);
}

/**
 * Swapw 2 values in array from positions a and b only
 * if value b exists and return if the swap was successfully
 * @param arr
 * @param a
 * @param b
 * @returns {boolean}
 */
function swapIfExists(arr, a, b) {
    // Check that b is within bounds and that arr[b] is not undefined
    if (b < 0 || b >= arr.length) {
        return false;
    }
    // Perform the swap
    [arr[a], arr[b]] = [arr[b], arr[a]];
    return true;
}

function moveUp() {
    movePos(-1);
}

function moveDown() {
    movePos(1);
}

function movePos(offset) {
    let component = workingEnv.component;
    let detailsType = $(workingEnv.shouldUpdateDetailType).val();
    let shouldChange;
    let index = parseInt($(workingEnv.ComponentEnv).val());
    if (detailsType !== "js") {
        let isContainer = $(workingEnv.ComponentType).val() === "Container";
        if (isContainer) {
            shouldChange = swapIfExists(component.components, index, index + offset);
        } else {
            shouldChange = swapIfExists(component.specs, index, index + offset);
        }
    } else {
        shouldChange = swapIfExists(component.js, index, index + offset);
    }
    if (shouldChange) {
        swapComponentEnvPositions(index, index + offset);
    }
    renderCurrentComponent()
}


function processSelect(index) {
    workingEnv.SelectContainer.setValues(index);
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
    if ($(workingEnv.EditingDetailType).val() !== "js") {
        return;
    }
    if (!workingEnv.shouldUpdate) {
        return;
    }
    let component = workingEnv.component;
    let index = $(workingEnv.ComponentEnv).val();
    let jsEnv = component.js[index]
    jsEnv["value"] = text;
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
            $(workingEnv.SpecsEnv).val(item.env);
        }
        if (env === "") {
            env = "Default";
        }
        var lineValue =  env + " -> " + JSON.stringify(item.value);
        if (lineValue.length > maxLine) {
            lineValue = lineValue.substring(0, maxLine - 3) + "...";
        }
        $(workingEnv.ComponentEnv).append($('<option>', {
            value: va,
            text:lineValue
        }));
    });
    M.FormSelect.init(workingEnv.ComponentEnv);
    hideSpecs();
    $(workingEnv.SpecsEnv).closest('.input-field').parent().show();
    switch (component.type) {
        case "canvas":
            $(workingEnv.SpecsHeight).closest('.input-field').parent().show();
            break
        case "jsEditor":
            $(workingEnv.SpecsHeight).closest('.input-field').parent().show();
            $(workingEnv.SpecsContentArea).closest('.input-field').parent().show();
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            break;
        case "img":
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            break;
        case "header":
            $(workingEnv.SelectContainer).parent().show();
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            processSelect();
            break;
        case "file":
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
        case "button":
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            break;
        case "select":
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SelectContainer).parent().show();
            processSelect();
            break;
        case "modal":
            $(workingEnv.SpecsOkCancelContainer).parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
            $(workingEnv.SpecsOk).closest('.input-field').parent().show();
            $(workingEnv.SpecsShowModal).parent().show();
            break;
        case "okCancelModal":
            $(workingEnv.SpecsOkCancelContainer).parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
            $(workingEnv.SpecsOk).closest('.input-field').parent().show();
            $(workingEnv.SpecsCancel).closest('.input-field').parent().show();
            $(workingEnv.SpecsShowModal).parent().show();
            break;
        case "switch":
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsOk).closest('.input-field').parent().show();
            $(workingEnv.SpecsCancel).closest('.input-field').parent().show();
            break;
        case "section":
            $(workingEnv.SpecsSize).closest('.input-field').parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContentArea).closest('.input-field').parent().show();
            break;
        case "password":
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
            break;
        case "textfield":
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContent).closest('.input-field').parent().show();
            break;
        case "textarea":
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
            $(workingEnv.SpecsTitle).closest('.input-field').parent().show();
            $(workingEnv.SpecsContentArea).closest('.input-field').parent().show();
            break;
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
        case "canvas":
            $(workingEnv.SpecsHeight).val(specs.height);
            break;
        case "jsEditor":
            $(workingEnv.SpecsHeight).val(specs.height);
            $(workingEnv.SpecsContentArea).val(specs.content);
            break;
        case "file":
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            break;
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
            if (typeof specs.src != "undefined") {
                $(workingEnv.SpecsSource).val(specs.src);
            }
            $(workingEnv.SpecsTitle).val(specs.title);
            break;
        case "modal":
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContentArea).val(specs.content);
            $(workingEnv.SpecsOk).val(specs.ok);
            break;
        case "okCancelModal":
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            $(workingEnv.SpecsOk).val(specs.ok);
            $(workingEnv.SpecsCancel).val(specs.cancel);
            break;
        case "switch":
            $(workingEnv.SpecsSource).val(specs.src);
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsOk).val(specs.ok);
            $(workingEnv.SpecsCancel).val(specs.cancel);
            break;
        case "password":
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            break;
        case "textarea":
            $(workingEnv.SpecsSource).val(specs.src);
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContentArea).val(specs.content);
            break;
        case "textfield":
            $(workingEnv.SpecsSource).val(specs.src);
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            break;
        case "section":
            $(workingEnv.SpecsSize).val(specs.size);
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContentArea).val(specs.content);
            break;
    }
    workingEnv.shouldUpdate = true;
}

function renderCurrentComponent() {
    $.post(`/components/preview`, {
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
            processJS(chooseChildren);
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
            processJS(chooseChildren);
        } else {
            processSpecs();
        }
    }
    $(workingEnv.shouldUpdateDetailType).change(() => {
        if ($(workingEnv.shouldUpdateDetailType).val() === "specs") {
            processSpecs();
        } else if ($(workingEnv.shouldUpdateDetailType).val() === "js") {
            processJS(chooseChildren);
        } else if ($(workingEnv.shouldUpdateDetailType).val() === "children") {
            processChildren()
        }
    });

    M.FormSelect.init(workingEnv.shouldUpdateDetailType);
    renderCurrentComponent()
}

function processComponent(componentName) {
    $.get(`/components/details/${componentName}`, (componentSpecs) => {
        workingEnv.originalComponent = componentSpecs.name;
        workingEnv.component = componentSpecs;
        processCurrentComponent(false);
    });
}

function saveCurrentSpecs() {
    $.post("/components/", {component: JSON.stringify(workingEnv.component)}, function (response) {
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

function removeCurrentEnv() {
    let component = workingEnv.component;
    let indexToRemove = $(workingEnv.ComponentEnv).val();
    if ($(workingEnv.EditingDetailType).val() === "js") {
        component.js.splice(indexToRemove, 1);
    } else {
        if ($(workingEnv.ComponentType).val() === "Container") {
            component.components.splice(indexToRemove, 1);
        } else {
            if (component.specs.length > 1) {
                component.specs.splice(indexToRemove, 1);
            }
        }
    }
    processCurrentComponent();
}


/**
 * Renames a component
 * @param newName
 */
function renameComponent(newName) {
    let originalPackage = workingEnv.component.package;
    let originalName = workingEnv.component.name;
    $.post("/components/rename", {
            "componentSpecs": JSON.stringify(workingEnv.component),
            "newName": newName
        }, (status) => {
            workingEnv.component.name = newName;
            workingEnv.component.package = status.package;
            if (status.package !== originalPackage) {
                let exists = addAutocompleteValue(workingEnv.ComponentPackage, status.package);
                if (!exists) {
                    $(workingEnv.ComponentName).empty();
                    selectOrAddValue($(workingEnv.ComponentName), newName);
                } else {
                    selectPackage(status.package, () => {
                        selectOrAddValue($(workingEnv.ComponentName), newName);
                        processComponent(newName);
                    });

                }
            } else {
                selectOrAddValue($(workingEnv.ComponentName), newName);
                $(workingEnv.ComponentName).find(`option[value="${originalName}"]`).remove();
                $(workingEnv.ComponentName).formSelect();
            }
        }
    )
}

/**
 * Clones a component
 * @param newName
 */
function cloneComponent(newName) {
    let originalPackage = workingEnv.component.package;
    $.post("/components/clone", {
            "componentSpecs": JSON.stringify(workingEnv.component),
            "newName": newName
        }, (status) => {
            workingEnv.component.name = newName;
            workingEnv.component.package = status.package;
            if (status.package !== originalPackage) {
                let exists = addAutocompleteValue(workingEnv.ComponentPackage, status.package);
                if (!exists) {
                    $(workingEnv.ComponentName).empty();
                    selectOrAddValue($(workingEnv.ComponentName), newName);
                } else {
                    selectPackage(status.package, () => {
                        selectOrAddValue($(workingEnv.ComponentName), newName);
                        processComponent(newName);
                    });
                }
            } else {
                selectOrAddValue($(workingEnv.ComponentName), newName);
            }
        }
    )
}

function newComponent(newName) {
    let originalPackage = $(workingEnv.ComponentPackage).val();
    $.post("/components/new", {
            "newName": newName
        }, (status) => {
            workingEnv.component = status.component;
            if (workingEnv.component.package !== originalPackage) {
                let exists = addAutocompleteValue(workingEnv.ComponentPackage, workingEnv.component.package);
                if (!exists) {
                    $(workingEnv.ComponentName).empty();
                    selectOrAddValue($(workingEnv.ComponentName), newName);
                } else {
                    selectPackage(workingEnv.component.package, () => {
                        selectOrAddValue($(workingEnv.ComponentName), newName);
                        processComponent(newName);
                    });
                }
            } else {
                selectOrAddValue($(workingEnv.ComponentName), newName);
            }
            processCurrentComponent(true);
        }
    )
}

function addSelectValueText(value, text) {
    let values = workingEnv.component.specs[$(workingEnv.ComponentEnv).val()].value.values;
    values.push({value: value, text: text});
    var textLine = "value: " + value + ", text: " + text;
    if (textLine.length > maxLine) {
        textLine = textLine.substring(0, maxLine - 3) + "...";
    }
    selectOrAddValue($(workingEnv.SpecsSelect), value, textLine);
    renderCurrentComponent();
}

$(document).ready(hideSpecs);