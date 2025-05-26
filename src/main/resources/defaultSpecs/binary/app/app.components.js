var processAction;
var processComponentType;
var processElementType;
var processContainerLayout;
var workingComponent={};

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
        $(workingComponent.ElementType).closest('.input-field').hide();
        $(workingComponent.ContainerLayout).closest('.input-field').hide();
        M.FormSelect.init(componentBox);
    });
}

function processComponent(componentName) {
    $(workingComponent.div).empty();
    $(workingComponent.show).empty();
    $.get(`/page/components/${componentName}`, (componentSpecs) => {
        if (componentSpecs.isContainer) {
            processComponentType("Container");
            processContainerLayout(componentSpecs.layout)
        } else {
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
            processComponentType("Element");
            processElementType(componentSpecs.type);
        }
    });

}
function setComponentTypeVisibility(type, element,layout){
    if (type === "Container"){
        $(element).closest('.input-field').hide();
        $(layout).closest('.input-field').show();
    }else{

        $(element).closest('.input-field').show();
        $(layout).closest('.input-field').hide();
    }
}