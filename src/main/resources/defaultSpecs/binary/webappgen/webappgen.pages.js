var M;
var maxLine = 73;
let workingEnv = {"shouldUpdate": false};

function markChanged() {
    workingEnv.SavePage.markChanged();
}

function markSaved() {
    workingEnv.SavePage.markSaved();
}


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

function saveCurrentSpecs() {
    $.post("/pages/", {page: JSON.stringify(workingEnv.page)}, function (response) {
        let message = "";
        if (response.success) {
            message = "Page saved successfully";
        } else {
            message = "Error saving page " + message.error;
        }
        markSaved();
        PageContent.showModal("Save page",message);
    });
}

function processNewPage(originalPackage,page){
    workingEnv.page = page;
    if (originalPackage !== workingEnv.page.package) {
        var exists = selectOrAddValue($(workingEnv.PagePackage), workingEnv.page.package);
        if (!exists) {
            $(workingEnv.PageName).empty();
            selectOrAddValue($(workingEnv.PageName), workingEnv.page.name);
        } else {
            $.get(`/pages/packages/${workingEnv.page.package}/pages`, (resultList) => {
                let pageNameBox = $(workingEnv.PageName);
                pageNameBox.empty();
                resultList.forEach(function (item) {
                    $(pageNameBox).append($('<option>', {
                        value: item,
                        text: item,
                        selected: item === workingEnv.page.name
                    }));
                });
                M.FormSelect.init(pageNameBox);
            });
        }
    } else {
        selectOrAddValue($(workingEnv.PageName), workingEnv.page.name);
    }
    $(workingEnv.PageName).change();
    markSaved();
}

function newPage(newName) {
    let originalPackage = $(workingEnv.PagePackage).val();
    $.post("/pages/new", {
            "newName": newName
        }, (status) => {
            if (status.success) {
                processNewPage(originalPackage,status.page);
                markSaved();
            } else {
                PageContent.showModal("Clone Page", "Cannot create new page. " +
                    JSON.stringify(result.error));
            }
        }
    )
}

function clonePage(newName) {
    let originalPackage = $(workingEnv.PagePackage).val();
    $.post("/pages/clone", {
            "page": JSON.stringify(workingEnv.page),
            "newName": newName
        }, (status) => {
            if (status.success) {
                processNewPage(originalPackage,status.page);
                markSaved();
            } else {
                PageContent.showModal("Clone Page", "Cannot clone page. " +
                    JSON.stringify(status.error));
            }
        }
    )
}

