var processAction;
var processComponentType;
var processElementType;
var processContainerLayout;
var workingEnv = {};

var maxLine = 73;

function hideElemContainer() {
    $(workingEnv.ElementType).closest('.input-field').parent().hide();
    $(workingEnv.ContainerLayout).closest('.input-field').parent().hide();
    hideSpecs();
}

function processAction(x) {
    selectPackage($(workingEnv.ComponentName), x);
}

function showPreviewModal(){
    M.Modal.init($('#__preview')[0]).open();
}

function hideSpecs() {

    $(workingEnv.SpecsSize).closest('.input-field').parent().hide();
    $(workingEnv.SpecsOk).closest('.input-field').parent().hide();
    $(workingEnv.SpecsNo).closest('.input-field').parent().hide();

    $(workingEnv.SpecsOkCancelContainer).closest('.input-field').parent().hide();

    $(workingEnv.SpecsShowModal).parent().hide();
    $(workingEnv.SpecsSource).closest('.input-field').parent().hide();
    $(workingEnv.SpecsTitle).closest('.input-field').parent().hide();
    $(workingEnv.SpecsContent).closest('.input-field').parent().hide();
    $(workingEnv.ChildrenComponentClass).parent().hide();
    $(workingEnv.ChildrenComponentDetails).parent().hide();
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

function processChildren() {
    hideSpecs();
    $(workingEnv.ChildrenComponentClass).parent().show();
    $(workingEnv.ChildrenComponentDetails).parent().show();
    $(workingEnv.ComponentEnv).empty();
    $(workingEnv.ComponentEnv).closest('.input-field').children('label').text('Children');
    workingEnv.component.components.forEach(function (item, index) {
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

function processInnerComponent(index) {
    let component = workingEnv.component;
    $(workingEnv.ChildrenInnerId).val(component.components[index].innerId);
    $(workingEnv.ChildrenSize).val(component.components[index].size);
    $(workingEnv.InnerComponentEnv).val(component.components[index].env);
    var innerComponentName = component.components[index].component;
    $.get(`/page/components/${innerComponentName}`, (componentSpecs) => {
        console.log(JSON.stringify(componentSpecs));
        $(workingEnv.InnerComponentPackage).val(componentSpecs.package);
        let componentBox = workingEnv.InnerComponentName;
        M.FormSelect.init(workingEnv.InnerComponentPackage);
        $.get(`/page/packages/${componentSpecs.package}/components`, (resultList) => {
            resultList.forEach(function (item) {
                $(componentBox).append($('<option>', {
                    value: item,
                    text: item
                }));
            });
            $(componentBox).val(innerComponentName);
            M.FormSelect.init(componentBox);
        });
    });

}

function processJS() {
    $(workingEnv.ComponentEnv).closest('.input-field').children('label').text('JS');
    hideSpecs();
    $(workingEnv.SpecsJS).parent().parent().parent().show();
    $(workingEnv.ComponentEnv).empty();
    let first = false;
    workingEnv.component.js.forEach(function (item, va) {
        let env = item.env;
        if (!first) {
            first = true;
            $(workingEnv.Env).val(item.env);
            ace.edit(workingEnv.SpecsJS).setValue(item.value);
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

    $(workingEnv.ComponentEnv).change(() => {
        let index = $(workingEnv.ComponentEnv).val();
        var envValue = workingEnv.component.js[index];
        ace.edit(workingEnv.SpecsJS).setValue(envValue.value);
        $(workingEnv.Env).val(envValue.env);

    });
}

function processSelect() {
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


function discardSpecs(){
    processComponent(workingEnv.originalComponent);
}

function setSpecValue(type, newValue){
    var index = $(workingEnv.ComponentEnv).val();
    var component = workingEnv.component;
    var specs = component.specs[index];
    specs.value[type] = newValue;
    renderCurrentComponent()
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
            $(workingEnv.SpecsSource).closest('.input-field').parent().show();
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
            $(workingEnv.SpecsNo).closest('.input-field').parent().show();
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
            $(workingEnv.SpecsNo).val(specs.cancel);
            break;
        case "password":
            $(workingEnv.SpecsTitle).val(specs.title);
            $(workingEnv.SpecsContent).val(specs.content);
            break;
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
}

function renderCurrentComponent(){
    $.post(`/page/preview`,  {
            componentSpecs: JSON.stringify(workingEnv.component),
            env:""
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

function processComponent(componentName) {

    $.get(`/page/components/${componentName}`, (componentSpecs) => {
        workingEnv.originalComponent = componentSpecs.name;
        workingEnv.component = componentSpecs;
        $(workingEnv.ComponentDetails).empty();
        $(workingEnv.ComponentDetails).append($('<option>', {
            value: "js",
            text: "JS"
        }));
        $(workingEnv.show).empty();
        if (componentSpecs.isContainer) {
            processComponentType("Container");
            processContainerLayout(componentSpecs.layout)
            $(workingEnv.ComponentDetails).append($('<option>', {
                value: "children",
                text: "Children",
            }));
            processJS();
        } else {
            $(workingEnv.ComponentDetails).append($('<option>', {
                value: "specs",
                text: "Specs",
                selected: true
            }));

            processComponentType("Element");
            processElementType(componentSpecs.type);
            processSpecs();

        }
        $(workingEnv.ComponentDetails).change(() => {
            if ($(workingEnv.ComponentDetails).val() === "specs") {
                processSpecs();
            } else if ($(workingEnv.ComponentDetails).val() === "js") {
                processJS();
            } else if ($(workingEnv.ComponentDetails).val() === "children") {
                processChildren()
            }
        });

        M.FormSelect.init(workingEnv.ComponentDetails);
        renderCurrentComponent()
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

function setComponentTypeVisibility(type) {
    if (type === "Container") {
        $(workingEnv.ElementType).closest('.input-field').parent().hide()
        $(workingEnv.ContainerLayout).closest('.input-field').parent().show();
    } else {

        $(workingEnv.ElementType).closest('.input-field').parent().show()
        $(workingEnv.ContainerLayout).closest('.input-field').parent().hide();
    }
}

$(document).ready(hideSpecs);