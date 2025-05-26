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

    $(workingComponent.SpecsURL).closest('.input-field').parent().hide();
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
    $(workingComponent.ComponentEnv).empty();
    let first = false;
    workingComponent.specs.js.forEach(function (item, va) {
        if (!first) {
            first = true;
            $(workingComponent.Env).val(item.env);
        }
        if (item.env === "") {
            item.env = "Default";
        }
        var lineValue = JSON.stringify(item.value);
        if (lineValue.length > maxLine) {
            lineValue = lineValue.substring(0, maxLine - 3) + "...";
        }
        $(workingComponent.ComponentEnv).append($('<option>', {
            value: va,
            text: item.env + " -> " + lineValue
        }));
    });
    M.FormSelect.init(workingComponent.ComponentEnv);
}

function processSpecs() {
    $(workingComponent.ComponentEnv).empty();
    let first = false;
    var component= workingComponent.specs;
    component.specs.forEach(function (item, va) {
        if (!first) {
            first = true;
            $(workingComponent.Env).val(item.env);
        }
        if (item.env === "") {
            item.env = "Default";
        }
        var lineValue = JSON.stringify(item.value);
        if (lineValue.length > maxLine) {
            lineValue = lineValue.substring(0, maxLine - 3) + "...";
        }
        $(workingComponent.ComponentEnv).append($('<option>', {
            value: va,
            text: item.env + " -> " + lineValue
        }));
    });
    M.FormSelect.init(workingComponent.ComponentEnv);
    hideSpecs();
    switch (component.type) {
        case "img":
            $(workingComponent.SpecsURL).closest('.input-field').parent().show();
            break
    }
    processElement();
}

function processElement(){
    var component= workingComponent.specs;
    var index =  $(workingComponent.ComponentEnv).val();
    var specs = component.specs[index].value;
    switch (component.type) {
        case "img":
            $(workingComponent.SpecsURL).val(specs.src);
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