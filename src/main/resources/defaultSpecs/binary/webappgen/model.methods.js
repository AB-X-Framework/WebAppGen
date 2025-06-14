function loadModel(modelPath) {
    App.process('webappgen.loadModel', {"modelPath": modelPath},
        (result) => {
            PageContent.showModal("Load Model", "New model loaded successfully.");
        },
        (result) => {
            PageContent.showModal("Load Model", "Cannot load model. "+JSON.stringify(result));
        });
}


function downloadModel(removeDefaults) {
    App.processDownload('webappgen.downloadModel', {"removeDefaults":removeDefaults},
        (result) => {
            PageContent.showModal("Download Mode", "Cannot download model. "+JSON.stringify(result));
        });
}



function uploadModel(model) {
    App.processBinary('webappgen.uploadModel', {},{"model":model},
        (result) => {
            PageContent.showModal("Load Model", "New model loaded successfully.");
        },
        (result) => {
            PageContent.showModal("Load Model", "Cannot load model. "+JSON.stringify(result));
        });
}

function saveModel(modelPath,removeDefaults) {
    App.process('webappgen.saveModel', {"modelPath": modelPath,
        "removeDefaults":removeDefaults},
        (result) => {
            if (result.success) {
                PageContent.showModal("Save Model", "Model saved successfully.");
            }else {
                PageContent.showModal("Save Model", "Cannot save model. "+JSON.stringify(result.error));
            }
        },
        (result) => {
            PageContent.showModal("Save Model", "Cannot save model. "+JSON.stringify(result));
        });
}

