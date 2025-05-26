var processAction;
var processComponentType;
var processElementType;
var processContainerLayout;
var workingComponent={};

function hideElemContainer(){

    $(workingComponent.ElementType).closest('.input-field').parent().hide();
    $(workingComponent.ContainerLayout).closest('.input-field').parent().hide();
}
function selectPackage(componentBox, packageName) {
    $(componentBox).empty();
    $(componentBox).append($('<option>', {
        value: "",
        text: ""
    }));
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

function processComponent(componentName) {
    $(workingComponent.div).empty();
    $(workingComponent.ComponentDetails).empty();
    $(workingComponent.ComponentDetails).append($('<option>', {
        value: "js",
        text: "JS"
    }));
    $(workingComponent.show).empty();
    $.get(`/page/components/${componentName}`, (componentSpecs) => {
        if (componentSpecs.isContainer) {
            processComponentType("Container");
            processContainerLayout(componentSpecs.layout)
        } else {
            $(workingComponent.ComponentDetails).append($('<option>', {
                value: "specs",
                text: "Specs"
            }));
            processComponentType("Element");
            processElementType(componentSpecs.type);
        }
        M.FormSelect.init(workingComponent.ComponentDetails);
        $.get(`/page/component/${componentName}`, (componentSpecs) => {
            var output = [];
            var js = [];
            //Clear JS
            componentSpecs.js="";
            PageContent.renderComponent(output, js, componentSpecs)
            $(workingComponent.show).html(output.join(""));
            M.updateTextFields();
            for (var line of js) {
                eval(line)
            }
        });
    });

}
function setComponentTypeVisibility(type, element,layout){
    if (type === "Container"){
        $(workingComponent.ElementType).closest('.input-field').parent().hide()
        $(workingComponent.ContainerLayout).closest('.input-field').parent().show();
    }else{

        $(workingComponent.ElementType).closest('.input-field').parent().show()
        $(workingComponent.ContainerLayout).closest('.input-field').parent().hide();
    }
}