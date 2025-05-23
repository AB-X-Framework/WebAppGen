function uploadModel(modelPath) {
    App.process('model.loadModel', {"modelPath": modelPath},
        (result) => {
            PageContent.showModal("Load Mode", "New model loaded successfully.");
        },
        (result) => {
            PageContent.showModal("Load Mode", "Cannot load model. "+result);
        });
}