var processAction;

function selectPackage(componentBox, packageName) {
    $(componentBox).empty();
    $.get(`/page/components/packages/${packageName}/components`, (resultList) => {
        resultList.forEach(function (item) {
            $(componentBox).append($('<option>', {
                value: item,
                text: item
            }));
        })
        M.FormSelect.init(componentBox);
    });
}