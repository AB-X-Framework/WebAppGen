var processAction;
var processComponentType;
var processElementType;
var processContainerLayout;

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
        })
        M.FormSelect.init(componentBox);
    });
}

function processComponent(componentName) {
    $.get(`/page/components/${componentName}`, (componentSpecs) => {
        if (componentSpecs.isContainer) {
            processComponentType("Container");
            processContainerLayout(componentSpecs.layout)
        } else {

            processComponentType("Element");
            processElementType(componentSpecs.type);
        }
    });

}