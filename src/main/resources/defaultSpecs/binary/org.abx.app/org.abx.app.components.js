var processAction;
var processComponentType;
var processElementType;
var processContainerLayout;
var workingComponent = {};

var maxLine = 73;

function hideElemContainer() {
    $(workingComponent.ElementType).closest('.input-field').parent().hide();
    $(workingComponent.ContainerLayout).closest('.input-field').parent().hide();
    hideSpecs();
}

function processAction(x) {
    selectPackage($(workingComponent.ComponentName), x);
}

function hideSpecs() {

    $(workingComponent.SpecsSize).closest('.input-field').parent().hide();
    $(workingComponent.SpecsSource).closest('.input-field').parent().hide();
    $(workingComponent.SpecsTitle).closest('.input-field').parent().hide();
    $(workingComponent.SpecsContent).closest('.input-field').parent().hide();
    $(workingComponent.ChildrenComponentClass).hide();
    $(workingComponent.ChildrenComponentDetails).hide();
    $(workingComponent.SpecsSelect).closest('.input-field').parent().parent().parent().hide();
    $(workingComponent.SpecsSelectRemove).parent().hide();
    $(workingComponent.SpecsSelectEdit).parent().hide();
    $(workingComponent.SpecsSelectAdd).parent().hide();
    $(workingComponent.SpecsJS).parent().parent().parent().hide();
    $(workingComponent.ComponentEnv).off("change")
    ace.edit(workingComponent.SpecsJS).setValue("");


    $(workingComponent.SpecsSource).closest('.input-field').children("label").text("Source");
}

function selectPackage(componentBox, packageName) {
    $(componentBox).empty();
    $(componentBox).append($('<option>', {
        value: "",
        text: ""
    }));
    $(workingComponent.ComponentEnv).empty();
    M.FormSelect.init(workingComponent.ComponentEnv);
    $.get(`/page/packages/${packageName}/components`, (resultList) => {
        resultList.forEach(function (item) {
            $(componentBox).append($('<option>', {
                value: item,
                text: item
            }));
        });

        $(workingComponent.show).empty();
        $(workingComponent.div).empty();
        hideElemContainer();
        M.FormSelect.init(componentBox);
    });
}

function processChildren() {
    hideSpecs();
    $(workingComponent.ChildrenComponentClass).show();
    $(workingComponent.ChildrenComponentDetails).show();
    $(workingComponent.ComponentEnv).empty();
    $(workingComponent.ComponentEnv).closest('.input-field').children('label').text('Children');
    workingComponent.specs.components.forEach(function (item, index) {
        var line = JSON.stringify(item);
        if (line.length > maxLine) {
            line = line.substring(0, maxLine - 3) + "...";
        }
        $(workingComponent.ComponentEnv).append($('<option>', {
            value: index,
            text: line
        }));
        $(workingComponent.ComponentEnv).change(() => {
            processInnerComponent($(workingComponent.ComponentEnv).val());
        });
    });
    if (workingComponent.specs.components.length > 0) {
        processInnerComponent(0);
    }
    M.FormSelect.init(workingComponent.ComponentEnv);
}

function processInnerComponent(index) {
    let component = workingComponent.specs;
    $(workingComponent.ChildrenInnerId).val(component.components[index].innerId);
    $(workingComponent.ChildrenSize).val(component.components[index].size);
    $(workingComponent.InnerComponentEnv).val(component.components[index].env);
    var innerComponentName = component.components[index].component;
    $.get(`/page/components/${innerComponentName}`, (componentSpecs) => {
        console.log(JSON.stringify(componentSpecs));
        $(workingComponent.InnerComponentPackage).val(componentSpecs.package);
        let componentBox = workingComponent.InnerComponentName;
        M.FormSelect.init(workingComponent.InnerComponentPackage);
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
    $(workingComponent.ComponentEnv).closest('.input-field').children('label').text('JS');
    hideSpecs();
    $(workingComponent.SpecsJS).parent().parent().parent().show();
    $(workingComponent.ComponentEnv).empty();
    let first = false;
    workingComponent.specs.js.forEach(function (item, va) {
        let env = item.env;
        if (!first) {
            first = true;
            $(workingComponent.Env).val(item.env);
            ace.edit(workingComponent.SpecsJS).setValue(item.value);
        }
        if (env === "") {
            env = "Default";
        }
        var lineValue = JSON.stringify(item.value);
        if (lineValue.length > maxLine) {
            lineValue = lineValue.substring(0, maxLine - 3) + "...";
        }
        $(workingComponent.ComponentEnv).append($('<option>', {
            value: va,
            text: env + " -> " + lineValue
        }));
    });
    M.FormSelect.init(workingComponent.ComponentEnv);

    $(workingComponent.ComponentEnv).change(() => {
        let index = $(workingComponent.ComponentEnv).val();
        var envValue = workingComponent.specs.js[index];
        ace.edit(workingComponent.SpecsJS).setValue(envValue.value);
        $(workingComponent.Env).val(envValue.env);

    });
}

function processSelect() {
    var component = workingComponent.specs;
    let index = $(workingComponent.ComponentEnv).val();
    var specs = component.specs[index].value;
    specs.values.forEach(function (item, va) {
        var textLine = "value: " + item.value + ", text: " + item.text;
        if (textLine.length > maxLine) {
            textLine = textLine.substring(0, maxLine - 3) + "...";
        }
        $(workingComponent.SpecsSelect).append($('<option>', {
            value: item,
            text: textLine
        }));
    });
    M.FormSelect.init(workingComponent.SpecsSelect);
}

function processSpecs() {
    $(workingComponent.ComponentEnv).closest('.input-field').children('label').text('Specs');
    $(workingComponent.ComponentEnv).empty();
    let first = false;
    var component = workingComponent.specs;
    component.specs.forEach(function (item, va) {
        let env = item.env;
        if (!first) {
            first = true;
            $(workingComponent.Env).val(item.env);
        }
        if (env === "") {
            env = "Default";
        }
        var lineValue = JSON.stringify(item.value);
        if (lineValue.length > maxLine) {
            lineValue = lineValue.substring(0, maxLine - 3) + "...";
        }
        $(workingComponent.ComponentEnv).append($('<option>', {
            value: va,
            text: env + " -> " + lineValue
        }));
    });
    M.FormSelect.init(workingComponent.ComponentEnv);
    hideSpecs();
    switch (component.type) {
        case "img":
            $(workingComponent.SpecsSource).closest('.input-field').parent().show();
            break
        case "header":
            $(workingComponent.SpecsSource).closest('.input-field').parent().show();
            break
        case "button":
            $(workingComponent.SpecsTitle).closest('.input-field').parent().show();
            break
        case "select":
            $(workingComponent.SpecsTitle).closest('.input-field').parent().show();
            $(workingComponent.SpecsSelect).closest('.input-field').parent().parent().parent().show();
            $(workingComponent.SpecsSelectRemove).parent().show();
            $(workingComponent.SpecsSelectAdd).parent().show();
            $(workingComponent.SpecsSelectEdit).parent().show();
            processSelect();
            break
        case "modal":
            $(workingComponent.SpecsTitle).closest('.input-field').parent().show();
            $(workingComponent.SpecsContent).closest('.input-field').parent().show();
            break
        case "section":
            $(workingComponent.SpecsSize).closest('.input-field').parent().show();
            $(workingComponent.SpecsTitle).closest('.input-field').parent().show();
            $(workingComponent.SpecsContent).closest('.input-field').parent().show();
            break
        case "password":
            $(workingComponent.SpecsTitle).closest('.input-field').parent().show();
            $(workingComponent.SpecsContent).closest('.input-field').parent().show();
            break
        case "textfield":
            $(workingComponent.SpecsSource).closest('.input-field').parent().show();
            $(workingComponent.SpecsTitle).closest('.input-field').parent().show();
            $(workingComponent.SpecsContent).closest('.input-field').parent().show();
            break
    }
    processElement();
    $(workingComponent.ComponentEnv).change(() => processElement());
}

function processElement() {

    var component = workingComponent.specs;

    var index = $(workingComponent.ComponentEnv).val();
    $(workingComponent.Env).val(component.specs[index].env);
    var specs = component.specs[index].value;
    switch (component.type) {
        case "header":
            $(workingComponent.SpecsSource).val(specs.src);
            break;
        case "img":
            $(workingComponent.SpecsSource).val(specs.src);
            break;
        case "button":
            $(workingComponent.SpecsTitle).val(specs.title);
            break;
        case "select":
            $(workingComponent.SpecsTitle).val(specs.title);
            break;
        case "modal":
            $(workingComponent.SpecsTitle).val(specs.title);
            $(workingComponent.SpecsContent).val(specs.content);
            break;
        case "password":
            $(workingComponent.SpecsTitle).val(specs.title);
            $(workingComponent.SpecsContent).val(specs.content);
            break;
        case "textfield":
            $(workingComponent.SpecsSource).val(specs.src);
            $(workingComponent.SpecsTitle).val(specs.title);
            $(workingComponent.SpecsContent).val(specs.content);
            break;
        case "section":
            $(workingComponent.SpecsSize).val(specs.size);
            $(workingComponent.SpecsTitle).val(specs.title);
            $(workingComponent.SpecsContent).val(specs.content);
            break;
    }
}

function processComponent(componentName) {
    $(workingComponent.ComponentDetails).empty();
    $(workingComponent.ComponentDetails).append($('<option>', {
        value: "js",
        text: "JS"
    }));
    $(workingComponent.show).empty();
    $.get(`/page/components/${componentName}`, (componentSpecs) => {
        workingComponent.specs = componentSpecs;
        if (componentSpecs.isContainer) {
            processComponentType("Container");
            processContainerLayout(componentSpecs.layout)
            $(workingComponent.ComponentDetails).append($('<option>', {
                value: "children",
                text: "Children",
            }));
            processJS();
        } else {
            $(workingComponent.ComponentDetails).append($('<option>', {
                value: "specs",
                text: "Specs",
                selected: true
            }));

            processComponentType("Element");
            processElementType(componentSpecs.type);
            processSpecs();

        }
        $(workingComponent.ComponentDetails).change(() => {
            if ($(workingComponent.ComponentDetails).val() === "specs") {
                processSpecs();
            } else if ($(workingComponent.ComponentDetails).val() === "js") {
                processJS();
            } else if ($(workingComponent.ComponentDetails).val() === "children") {
                processChildren()
            }
        });

        M.FormSelect.init(workingComponent.ComponentDetails);
        $.get(`/page/component/${componentName}`, (componentSpecs) => {
            var output = [];
            var js = [];
            //Clear JS
            componentSpecs.js = "";
            PageContent.renderComponent(output, js, componentSpecs)
            $(workingComponent.show).html(output.join(""));
            M.updateTextFields();
            for (var line of js) {
                eval(line)
            }
        });
    });

}

function setComponentTypeVisibility(type, element, layout) {
    if (type === "Container") {
        $(workingComponent.ElementType).closest('.input-field').parent().hide()
        $(workingComponent.ContainerLayout).closest('.input-field').parent().show();
    } else {

        $(workingComponent.ElementType).closest('.input-field').parent().show()
        $(workingComponent.ContainerLayout).closest('.input-field').parent().hide();
    }
}

$(document).ready(hideSpecs);