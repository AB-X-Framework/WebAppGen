function loadModel(modelPath) {
    App.process('model.loadModel', {"modelPath": modelPath},
        (result) => {
            PageContent.showModal("Load Model", "New model loaded successfully.");
        },
        (result) => {
            PageContent.showModal("Load Model", "Cannot load model. "+result);
        });
}


function downloadModel() {
    App.processDownload('model.downloadModel', {},
        undefined,
        (result) => {
            PageContent.showModal("Download Mode", "Cannot download model. "+result);
        });
}
function saveModel(modelPath) {
    App.process('model.saveModel', {"modelPath": modelPath},
        (result) => {
            PageContent.showModal("Save Model", "Model saved successfully.");
        },
        (result) => {
            PageContent.showModal("Save Model", "Cannot save model. "+result);
        });
}

