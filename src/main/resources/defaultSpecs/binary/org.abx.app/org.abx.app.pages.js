var workingEnv = {"shouldUpdate": false};
var maxLine = 73;

function selectPackage(packageName, afterCall) {
    workingEnv.shouldUpdate = false;
    let pageNameBox = $(workingEnv.PageName);
    $(pageNameBox).empty();
    $(pageNameBox).append($('<option>', {
        value: "",
        text: ""
    }));
    $.get(`/pages/packages/${packageName}/components`, (resultList) => {
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