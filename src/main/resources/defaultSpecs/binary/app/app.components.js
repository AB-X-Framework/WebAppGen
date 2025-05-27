var processAction;
var processComponentType;
var processElementType;
var processContainerLayout;
var workingComponent={};

var maxLine = 73;
function hideElemContainer(){
    $(workingComponent.ElementType).closest('.input-field').parent().hide();
    $(workingComponent.ContainerLayout).closest('.input-field').parent().hide();
    hideSpecs();
}

function hideSpecs(){

    $(workingComponent.SpecsSize).closest('.input-field').parent().hide();
    $(workingComponent.SpecsURL).closest('.input-field').parent().hide();
    $(workingComponent.SpecsTitle).closest('.input-field').parent().hide();
    $(workingComponent.SpecsContent).closest('.input-field').parent().hide();
    $(workingComponent.SpecsSelect).closest('.input-field').parent().hide();
    $(workingComponent.SpecsJS).hide();
    $(workingComponent.ComponentEnv).off("change")
    ace.edit(workingComponent.SpecsJS).setValue("");
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

function processJS() {
    hideSpecs();
    $(workingComponent.SpecsJS).show();
    $(workingComponent.ComponentEnv).empty();
    let first = false;
    workingComponent.specs.js.forEach(function (item, va) {
        let env= item.env;
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

    $(workingComponent.ComponentEnv).change(()=>{
        let index = $(workingComponent.ComponentEnv).val();
        var envValue = workingComponent.specs.js[index];
        ace.edit(workingComponent.SpecsJS).setValue(envValue.value);
        $(workingComponent.Env).val(envValue.env);

    });
}

function processSelect(){
    var component = workingComponent.specs;
    let index = $(workingComponent.ComponentEnv).val();
    var specs = component.specs[index].value;
    specs.values.forEach(function (item, va) {
        $(workingComponent.SpecsSelect).append($('<option>', {
            value: item,
            text: item.value+" "+item.text
        }));
    });
    M.FormSelect.init(workingComponent.SpecsSelect);
}

function processSpecs() {
    $(workingComponent.ComponentEnv).empty();
    let first = false;
    var component= workingComponent.specs;
    component.specs.forEach(function (item, va) {
        let env=item.env;
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
            $(workingComponent.SpecsURL).closest('.input-field').parent().show();
            break
        case "header":
            $(workingComponent.SpecsURL).closest('.input-field').parent().show();
            break
        case "button":
            $(workingComponent.SpecsTitle).closest('.input-field').parent().show();
            break
        case "select":
            $(workingComponent.SpecsTitle).closest('.input-field').parent().show();
            $(workingComponent.SpecsSelect).closest('.input-field').parent().show();
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
            $(workingComponent.SpecsURL).closest('.input-field').parent().show();
            $(workingComponent.SpecsTitle).closest('.input-field').parent().show();
            $(workingComponent.SpecsContent).closest('.input-field').parent().show();
            break
    }
    processElement();
    $(workingComponent.ComponentEnv).change(()=>processElement());
}

function processElement(){

    var component= workingComponent.specs;

    var index =  $(workingComponent.ComponentEnv).val();
    $(workingComponent.Env).val(component.specs[index].env);
    var specs = component.specs[index].value;
    switch (component.type) {
        case "header":
            $(workingComponent.SpecsURL).val(specs.src);
            break;
        case "img":
            $(workingComponent.SpecsURL).val(specs.src);
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
            $(workingComponent.SpecsURL).val(specs.src);
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
            } else {
                processJS();
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