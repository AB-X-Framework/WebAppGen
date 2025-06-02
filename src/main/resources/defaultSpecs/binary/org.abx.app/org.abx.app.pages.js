var M;
let workingEnv = {"shouldUpdate": false};
const maxLine = 73;

function selectPackage(packageName) {
    workingEnv.shouldUpdate = false;
    let pageNameBox = $(workingEnv.PageName);
    $(pageNameBox).empty();
    $(pageNameBox).append($('<option>', {
        value: "",
        text: ""
    }));
    $.get(`/pages/packages/${packageName}/pages`, (resultList) => {
        resultList.forEach(function (item) {
            $(pageNameBox).append($('<option>', {
                value: item,
                text: item
            }));

        });
        M.FormSelect.init(pageNameBox);
    });
}